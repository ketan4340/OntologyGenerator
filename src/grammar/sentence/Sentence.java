package grammar.sentence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.GrammarInterface;
import grammar.SyntacticChild;
import grammar.SyntacticParent;
import grammar.clause.Clause;
import grammar.clause.SerialClause;
import grammar.clause.SingleClause;
import grammar.morpheme.Morpheme;
import grammar.morpheme.MorphemeFactory;
import grammar.pattern.ClausePattern;
import grammar.pattern.WordPattern;
import grammar.word.Adjunct;
import grammar.word.Word;
import language.pos.TagsFactory;

public class Sentence extends SyntacticParent<Clause<?>>
		implements SyntacticChild, GrammarInterface {
	private static int SUM = 0;

	private final int id;

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public Sentence(List<Clause<?>> clauses) {
		super(clauses);
		this.id = SUM++;
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public void initDependency(DependencyMap dm) {
		dm.forEach((from, to) -> from.setDepending(to));
	}
	public Sentence subsentence(int beginIndex, int endIndex) {
		List<Clause<?>> subClauses = new ArrayList<>(children.subList(beginIndex, endIndex));
		return new Sentence(subClauses);
	}
	
	public ClauseSequence subClauseSequence(int beginIndex, int endIndex) {
		List<Clause<?>> subClauses = children.subList(beginIndex, endIndex);
		return new ClauseSequence(subClauses);
	}
	
	/**
	 * 指定の品詞が末尾に並んでいる文節のうち，最初の一つを返す.
	 * @param cp 文節パターン
	 * @param ignoreSign 記号を無視するか否か
	 * @return 最後の単語が指定の品詞である文節のうち、この文の最初に現れるもの.
	 */
	public Optional<Clause<?>> findFirstClauseMatching(ClausePattern cp) {
		return children.stream().filter(cp::matches).findFirst();
	}
	
	/**
	 * 指定の文節をその右隣の文節に繋げる.
	 * 元の文節・単語は連文節の中に内包される. 
	 * @param frontClause 直後の文節と繋げる文節
	 * @param destructive 破壊的結合をするかどうか
	 */
	public boolean connect2Next(Clause<?> frontClause, boolean destructive) {
		if (!children.contains(frontClause)) return false;
		if (!frontClause.getOthers().isEmpty()) return false;		// 前の文節に接辞(句読点など)があれば繋げない

		Clause<?> backClause = nextChild(frontClause);
		if (backClause == null || backClause == SingleClause.ROOT) return false;

		Clause<?> concatClause;
		if (destructive) {
			if (!(backClause instanceof SingleClause))
				return false;
			concatClause = SingleClause.concatClauseDestructive(frontClause, (SingleClause) backClause);
		} else {
			concatClause = SerialClause.join(frontClause, backClause);
		}
		
		if (!replace(backClause, concatClause)) return false;
		if (!children.remove(frontClause)) return false;
		Set<Clause<?>> formerDependeds = clausesDepending(frontClause);
		formerDependeds.addAll(clausesDepending(backClause));
		gatherDepending(concatClause, formerDependeds);
		return true;
	}

	/** 渡したClauseが文中で連続しているかを<clause, Boolean>のMapで返す */
	/* 例:indexのリストが(2,3,4,6,8,9)なら(T,T,F,F,T,F) */
	public LinkedHashMap<Clause<?>, Boolean> getContinuity(List<Clause<?>> clauseList) {
		LinkedHashMap<Clause<?>, Boolean> continuity = new LinkedHashMap<>(clauseList.size());
		List<Integer> clauseIndexList = indexesOfChildren(clauseList);

		Iterator<Clause<?>> liID = clauseList.listIterator();
		Iterator<Integer> liIdx = clauseIndexList.listIterator();
		int currentIdx = liIdx.next();
		while (liIdx.hasNext() && liID.hasNext()) {
			int nextIdx = liIdx.next();
			Clause<?> clause = liID.next();
			if (currentIdx+1 == nextIdx) {	// indexが連続しているか
				continuity.put(clause, true);
			} else {							// 否か
				continuity.put(clause, false);
			}
			currentIdx = nextIdx;
		}
		continuity.put(liID.next(), false);	// 最後は絶対連続しないからfalse

		return continuity;
	}

	/**
	 * 主語係り先分割
	 * メインの主語が係る述語ごとに分割
	 */
	public List<Sentence> divide2() {
		List<Sentence> shortSentList = new ArrayList<>(5);
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Clause<?>> subjectList = subjectList(false);	// 主節のリスト

		Clause<?> lastClause = tail();	// 文の最後尾の文節
		if ((lastClause instanceof SingleClause) && 	// 最後尾の文節がClauseインスタンスで
				(subjectList.contains(lastClause))) {	// 主節である場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			((SingleClause) lastClause).nounize();
			subjectList.remove(lastClause);
		}
		if (subjectList.isEmpty()) return shortSentList;	// 文中に主語がなければ終了

		// 主節の連続性を表す真偽値のリスト
		Map<Clause<?>, Boolean> subjectsContinuity = getContinuity(subjectList);
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		ClauseSequence commonSubjectsOrigin = new ClauseSequence();
		for (Map.Entry<Clause<?>, Boolean> entry : subjectsContinuity.entrySet()) {
			commonSubjectsOrigin.add(entry.getKey());
			if (!entry.getValue()) break;	// 主語の連続が途切れたら核主語集め完了
		}
		// 後続の多くの述語に係る、核たる主語
		Clause<?> mainSubject = commonSubjectsOrigin.get(commonSubjectsOrigin.size()-1);
		List<Clause<?>> predicates = allDependings(mainSubject);
		predicates.retainAll(children);

		if (predicates.size() <= 1)	// 述語が一つならスルー
			return Collections.singletonList(this);

		/* 文章分割(dependUpon依存) */
		int fromIndex = 0, toIndex;
		for (final Clause<?> predicate: predicates) {
			predicate.setDepending(SingleClause.ROOT);	// 文末の述語となるので係り先はなし(null)
			toIndex = indexOfChild(predicate) + 1;		// 述語も含めて切り取るため+1
			//System.out.println("divide2.subList(" + fromIndex + ", " + toIndex + ")");
			Sentence subSent = subsentence(fromIndex, toIndex);
			// 文頭の主語は全ての分割後の文に係る
			ClauseSequence commonSubjects = commonSubjectsOrigin.cloneAll();

			if (fromIndex!=0) {		// 最初の分割文以外は、新たに主語を挿入する
				Clause<?> subSentFirst = subSent.children.get(0);	// 分割文の先頭文節
				if (subjectList.contains(subSentFirst))				// それが主語なら
					commonSubjectsOrigin.add(subSentFirst);			// 後続の短文にも係るので保管
				subSent.children.addAll(0, commonSubjects);		// 共通の主語を先頭に挿入
			}

			// 係り先を正す
			commonSubjects.forEach(s -> s.setDepending(predicate));

			subSent.gatherDepending(predicate);
			shortSentList.add(subSent);

			int commonSubjectsSize = commonSubjectsOrigin.size();
			if (commonSubjectsSize > 1) {
				Clause<?> nextClause = nextChild(predicate);
				if (subjectList.contains(nextClause))	// かつ次が主語である
					commonSubjectsOrigin.remove(commonSubjectsSize-1);
			}
			fromIndex = toIndex;
		}
		return shortSentList;
	}

	// 末尾がこれら以外なら述語とみなす
	/*
	private static final ClausePattern PARTICLE = 
			ClausePattern.compile(new String[][]{{"助詞", "-て"}, {"%o", "$"}});		// "て"以外の助詞
	private static final ClausePattern ADVERB = 
			ClausePattern.compile(new String[][]{{"副詞"}, {"%o", "$"}});				// "すぐに"、"おそらく"など
	private static final ClausePattern AUXILIARY = 
			ClausePattern.compile(new String[][]{{"助動詞", "体言接続"}, {"%o", "$"}});	// "〜で"など
	*/
	private static final ClausePattern NOUN = 
			ClausePattern.compile(new String[][]{{"名刺", "-て"}, {"%o", "$"}});
	private static final ClausePattern VERB = 
			ClausePattern.compile(new String[][]{{"動詞"}, {"%o", "$"}});
	private static final ClausePattern ADJECTIVE = 
			ClausePattern.compile(new String[][]{{"形容詞"}, {"%o", "$"}});
	private static final ClausePattern AUXILIARY = 
			ClausePattern.compile(new String[][]{{"助動詞", "-体言接続"}, {"%o", "$"}});
	private static final ClausePattern PARTICLE1 = 
			ClausePattern.compile(new String[][]{{"接続助詞"}, {"%o", "$"}});
	private static final ClausePattern PARTICLE2 = //CaboChaの解析の誤りに対応するため
			ClausePattern.compile(new String[][]{{"格助詞", "で"}, {"記号", "読点"}, {"%o", "$", ",."}});
	/**
	 * 述語係り元分割
	 * 述語に係る{動詞,形容詞,名詞,~だ,接続助詞}ごとに分割
	 */
	public List<Sentence> divide3() {
		List<Sentence> partSentList = new ArrayList<>(5);
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Clause<?>> subjectList = subjectList(false);	// 主語のリスト
		if (subjectList.isEmpty()) return partSentList;		// 文中に主語がなければ終了

		Clause<?> lastClause = tail();	// 文の最後尾Clause
		if ((lastClause instanceof SingleClause) && 			// 最後尾の文節がClauseインスタンスで
				(subjectList.contains(lastClause))) {	// 主節である場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			((SingleClause) lastClause).nounize();
			subjectList.remove(lastClause);
		}
		// 主節の連続性を表す真偽値のリスト
		LinkedHashMap<Clause<?>, Boolean> subjectsContinuity = getContinuity(subjectList);
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		ClauseSequence commonSubjectsOrigin = new ClauseSequence();
		for (Map.Entry<Clause<?>, Boolean> entry : subjectsContinuity.entrySet()) {
		 	commonSubjectsOrigin.add(entry.getKey());
			if (!entry.getValue()) break;	// 主語の連続が途切れたら核主語集め完了
		}

		/* 述語を収集 */
		List<Clause<?>> predicates = new ArrayList<>();
		for (final Clause<?> cls2Last: clausesDepending(lastClause)) {
			// 末尾が"て"を除く助詞または副詞でないClauseを述語として追加
			if (( NOUN.matches(cls2Last) || VERB.matches(cls2Last) || 
					ADJECTIVE.matches(cls2Last) || AUXILIARY.matches(cls2Last) ||
					PARTICLE1.matches(cls2Last) || PARTICLE2.matches(cls2Last) ) && 
				nextChild(cls2Last) != lastClause) // 次の文節が最後の文節であるものは除外
				predicates.add(cls2Last);
		}
		predicates.add(lastClause);
		//predicates.retainAll(children);
		predicates.sort(Comparator.comparing(c -> indexOfChild(c)));

		if (predicates.size() <= 1) // 述語が一つならスルー
			return Collections.singletonList(this);

		/* 文章分割(dependUpon依存) */
		int fromIndex = 0, toIndex;
		for (Iterator<Clause<?>> itr = predicates.iterator(); itr.hasNext(); ) {
			Clause<?> predicate = itr.next();
			predicate.setDepending(SingleClause.ROOT);	// 分割後、当該述語は文末にくるので係り先はなし(null)
			toIndex = indexOfChild(predicate) + 1;	// 述語も含めて切り取るため+1
			//System.out.println("divide3.subList(" + fromIndex + ", " + toIndex + ")");
			//TODO *from>to problem
			Sentence subSent = subsentence(fromIndex, toIndex);
			// 文頭の主語は全ての分割後の文に係る
			ClauseSequence commonSubjects = commonSubjectsOrigin.cloneAll();

			if (fromIndex != 0) {		// 最初の分割文は、新たに主語を挿入する必要ない
				Clause<?> subSentFirst = subSent.children.get(0);	// 分割文の先頭
				if (subjectList.contains(subSentFirst))			// それが主語なら
					commonSubjectsOrigin.add(subSentFirst);		// 後続の短文にも係るので保管
				subSent.children.addAll(0, commonSubjects);	// 共通の主語を挿入
			}
			// 主語の係り先を正す
			subSent.subjectList(false).forEach(s -> s.setDepending(predicate));
			subSent.gatherDepending(predicate);

			partSentList.add(subSent);

			// 述語のあとに主語があれば共通主語の最後尾を切り捨てる
			int commonSubjectsSize = commonSubjectsOrigin.size();
			if (commonSubjectsSize > 1) {
				Clause<?> nextClause = nextChild(predicate);
				if (subjectList.contains(nextClause))	// 次が主語
					commonSubjectsOrigin.remove(commonSubjectsSize-1);
			}
			fromIndex = toIndex;
		}
		return partSentList;
	}

	/**
	 * この文節が係る文節，その文節が係る文節，と辿っていき，経由した全ての文節をリストにして返す.
	 * @return この文節から係る全ての文節
	 */
	public List<Clause<?>> allDependings(Clause<?> c) {
		List<Clause<?>> allDepending = new ArrayList<>();
		Clause<?> depto = c.getDepending();
		while (depto != null) {
			allDepending.add(depto);
			depto = depto.getDepending();
		}
		return allDepending;
	}
	
	/**
	 * 文節の係り先が文中にないような場合，渡されたClauseにdependを向ける
	 */
	private boolean gatherDepending(Clause<?> target) {
		if (!children.contains(target)) return false;
		for (Iterator<Clause<?>> itr = children.iterator(); itr.hasNext(); ) {
			Clause<?> member = itr.next();
			if (!itr.hasNext()) break;	// 最後の述語だけは係り先がnullなのでスルー
			Clause<?> presentDepending = member.getDepending();
			if (!children.contains(presentDepending))
				member.setDepending(target);
		}
		return true;
	}
	public boolean gatherDepending(Clause<?> target, Collection<Clause<?>> memberClauses) {
		if (!children.contains(target)) return false;
		if (!children.containsAll(memberClauses)) return false;
		memberClauses.forEach(mc -> mc.setDepending(target));
		return true;
	}
	public Set<Clause<?>> clausesDepending(Clause<?> clause) {
		return getChildren().stream()
				.filter(c -> c.getDepending() == clause)
				.collect(Collectors.toSet());
	}
	
	private static final WordPattern POS_HA = new WordPattern("係助詞", "は");
	private static final WordPattern POS_GA = new WordPattern("格助詞", "が");
	private static final WordPattern POS_DE = new WordPattern("格助詞", "で");
	private static final WordPattern POS_NI = new WordPattern("格助詞", "に");
	/**
	 * 主語のリストを得る.
	 */
	public List<Clause<?>> subjectList(boolean includeGa) {
		List<Clause<?>> subjectList;
		List<Clause<?>> clause_Ha_List = clausesHave(POS_HA);	// 係助詞"は"を含むClause
		List<Clause<?>> clause_De_List = clausesHave(POS_DE);	// 格助詞"で"を含むClause
		List<Clause<?>> clause_Ni_List = clausesHave(POS_NI);	// 格助詞"に"を含むClause
		if (includeGa) {	// "が"は最初の一つのみ!!
			List<Clause<?>> clause_Ga_List = clausesHave(POS_GA);	// 係助詞"は"を含むClause
			// 係助詞"は"あるいは"が"を含むClause
			List<Clause<?>> clause_Ha_Ga_List = 
					Stream.concat(clause_Ha_List.stream(), clause_Ga_List.stream())
					.collect(Collectors.toList());
			if (!clause_Ga_List.isEmpty()) clause_Ga_List.remove(0); // "が"は最初の一つのみ残す
			clause_Ha_Ga_List.removeAll(clause_Ga_List);
			clause_Ha_Ga_List.removeAll(clause_De_List);	// "は"と"が"を含むClauseのうち、"で"を含まないものが主語("では"を除外するため)
			clause_Ha_Ga_List.removeAll(clause_Ni_List);	// "は"と"が"を含むClauseのうち、"に"を含まないものが主語("には"を除外するため)
			subjectList = clause_Ha_Ga_List;
		} else {
			clause_Ha_List.removeAll(clause_De_List);	// "は"を含むClauseのうち、"で"を含まないものが主語
			clause_Ha_List.removeAll(clause_Ni_List);	// "は"を含むClauseのうち、"に"を含まないものが主語
			subjectList = clause_Ha_List;		// 主語のリスト
		}
		return subjectList;
	}

	/** 二重主語を解消する. */
	public void uniteSubject() {
		List<Clause<?>> subjectList = subjectList(false);
		if (subjectList.isEmpty()) return;

		// 主節の連続性を表す真偽値のリスト
		Map<Clause<?>, Boolean> subjectsContinuity = getContinuity(subjectList);
		//System.out.println("subjContinuity: " + subjectsContinuity);
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		for (Map.Entry<Clause<?>, Boolean> entry: subjectsContinuity.entrySet()) {
			Clause<?> subject = entry.getKey();		boolean sbjCnt = entry.getValue();
			if (!sbjCnt) break;		// 連続した主語の最後尾には必要ない
			// 助詞・連体化"の"を新たに用意
			Adjunct no = new Adjunct(MorphemeFactory.getInstance().getMorpheme("の",TagsFactory.getInstance().getCabochaTags("助詞","連体化","*","*","*","*","の","ノ","ノ")));
			ListIterator<Adjunct> iter = subject.getAdjuncts().listIterator();
			while (iter.hasNext()) {
				Adjunct adjunct = iter.next();
					if (adjunct.matches(POS_HA)) {
						iter.set(no);	// "は"の代わりに"の"を挿入
						break;
					}
			}
		}
		WordPattern pos_NP = new WordPattern("助詞", "連体化");
		List<Clause<?>> clauses_NP = clausesHave(pos_NP);
		clauses_NP.forEach(c -> connect2Next(c, false));
	}

	/**
	 * 渡された品詞のいずれかに一致するWordを含むclauseを返す.
	 */
	public List<Clause<?>> clausesHave(WordPattern wp) {
		return children.stream()
				.filter(c -> c.containsWordHas(wp))
				.collect(Collectors.toList());
	}
	
	/** ClauseのリストからWordのリストにする */
	public List<Word> words() {
		return children.stream()
				.map(Clause::words)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	public List<Morpheme> morphemes() {
		return words().stream()
				.map(Word::getChildren)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}


	/* ================== Output Method ================= */
	public void printM() {
		System.out.println(
				morphemes().stream().map(Morpheme::name).collect(Collectors.joining("'")));
	}
	public void printW() {
		for (final Word word : words()) {
			System.out.print("("+word.id()+")" + word.name());
		}
		System.out.println();
	}
	public void printC() {
		for (final Clause<?> clause : children) {
			System.out.print("("+clause.id()+")" + clause.toString());
		}
		System.out.println();
	}
	public void printDep() {
		for (int i = 0; i < children.size(); i++) {
			Clause<?> clause = children.get(i);
			Clause<?> depto = clause.getDepending();
			int depIndex = (depto == null)? -1	: children.indexOf(depto);
			System.out.print("(" + i + ">" + depIndex + ")" + clause.toString());
		}
		System.out.println();
	}
	/** 文を区切りを挿入して出力する */
	public void printS() {
		for (final Word word : words()) { // Word単位で区切る
			System.out.print(word.name() + "|");
		}
		System.out.println();
		for (final Clause<?> clause : children) { // Clause単位で区切る
			System.out.print(clause.toString() + "|");
		}
		System.out.println();
	}


	/* ================================================== */
	/* ================ Interface Method ================ */
	/* ================================================== */
	@Override
	public int id() {return id;}
	
	@Override
	public String name() {
		return getChildren().stream().map(c -> c.name()).collect(Collectors.joining());
	}
	
	@Override
	public Resource toJASS(Model model) {
		List<Resource> clauseResources = getChildren().stream()
				.map(m -> m.toJASS(model)).collect(Collectors.toList());
		clauseDepend2RDF(clauseResources);
		Resource clauseList = 
				model.createList(clauseResources.iterator())
				.addProperty(RDF.type, JASS.ClauseList);

		Resource sentenceResource = model.createResource(getJassURI())
				.addProperty(RDF.type, JASS.Sentence)
				.addProperty(JASS.clauses, clauseList);
		return sentenceResource;
	}
	
	private void clauseDepend2RDF(List<Resource> clauseResources) {
		children.forEach(cls -> {
			Resource clauseResource = clauseResources.parallelStream()
					.filter(c -> Objects.equals(c.getURI(), cls.getJassURI()))
					.findAny().orElse(null);
			Clause<?> depending = cls.getDepending();
			Optional<Resource> dependingResource = depending==SingleClause.ROOT? 
					Optional.empty():
					clauseResources.parallelStream().filter(c -> Objects.equals(c.getURI(), depending.getJassURI())).findAny();
			dependingResource.ifPresent(d -> clauseResource.addProperty(JASS.dependTo, d));
		});
	}
	
	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public String toString() {
		return children.stream()
				.map(Clause::toString)
				.collect(Collectors.joining("/"));
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sentence other = (Sentence) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}

package grammar.sentence;

import java.util.ArrayList;
import java.util.Collection;
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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.rule.RDFizable;
import data.RDF.vocabulary.JASS;
import grammar.GrammarInterface;
import grammar.SyntacticParent;
import grammar.clause.Clause;
import grammar.clause.SerialClause;
import grammar.clause.SingleClause;
import grammar.morpheme.Morpheme;
import grammar.word.Adjunct;
import grammar.word.Word;
import pos.CabochaTags;

public class Sentence extends SyntacticParent<Clause<?>>
		implements GrammarInterface, RDFizable {
	private static int SENTENCE_SUM = 0;

	private final int id;

	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	public Sentence(List<Clause<?>> clauses) {
		super(clauses);
		this.id = SENTENCE_SUM++;
	}
	public Sentence(List<Clause<?>> clauses, Map<Clause<?>, Integer> dependingMap) {
		this(clauses);
		initializeDepending(dependingMap);
	}


	/* ================================================== */
	/* ==========        Member  Method        ========== */
	/* ================================================== */

	/**
	 * CaboChaがClause生成時に記録した係り受けマップを元に係り先dependingをセットする
	 * @param dependingMap 各文節がこの文の何番目の文節に係るか記録されたマップ
	 */
	private void initializeDepending(Map<Clause<?>, Integer> dependingMap) {
		dependingMap.entrySet().stream().forEach(e -> {
			Clause<?> c = e.getKey();
			int idxDep2 = e.getValue();
			c.setDepending(idxDep2 == -1? SingleClause.ROOT: children.get(idxDep2));
		});
	}

	public Sentence subSentence(int fromIndex, int toIndex) {
		List<Clause<?>> subClauses = new ArrayList<>(children.subList(fromIndex, toIndex));
		return new Sentence(subClauses);
	}


	/**
	 * 渡された品詞のいずれかに一致するWordを含むclauseを返す.
	 */
	public List<Clause<?>> collectClausesHaveSome(String[][] tags) {
		return children.stream()
				.filter(c -> c.containsAnyWordsHave(tags))
				.collect(Collectors.toList());
	}


	/**
	 * 指定の品詞が末尾に並んでいる文節のうち，最初の一つを返す.
	 * @param tags
	 * @return 最後の単語が指定の品詞である文節
	 */
	public Clause<?> findFirstClauseEndWith(String[][] tags, boolean ignoreSign) {
		for (Clause<?> clause : children)
			if (clause.endWith(tags, ignoreSign))
				return clause;
		return null;
	}


	/**
	 * 指定の文節をその右隣の文節に繋げる.
	 * @param frontClause 直後の文節と繋げる文節
	 */
	public boolean connect2Next(Clause<?> frontClause) {
		if (!children.contains(frontClause)) return false;
		if (!frontClause.getOthers().isEmpty()) return false;		// 前の文節に接辞(句読点など)があれば繋げない

		Clause<?> backClause = nextChild(frontClause);
		if (backClause == null || backClause == SingleClause.ROOT) return false;

		List<Clause<?>> backup = new ArrayList<>(children);

		SerialClause sc = SerialClause.connectClauses(frontClause, backClause);
		if (!replace(backClause, sc)) return false;
		if (!children.remove(frontClause)) {
			setChildren(backup);
			return false;
		}
		Set<Clause<?>> formerDependeds = clausesDependingThisClause(frontClause);
		formerDependeds.addAll(clausesDependingThisClause(backClause));
		gatherDepending(sc, formerDependeds);
		return true;
	}

	private Set<Clause<?>> clausesDependingThisClause(Clause<?> clause) {
		return getChildren().stream()
				.filter(c -> c.getDepending() == clause)
				.collect(Collectors.toSet());
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
		if ((lastClause instanceof SingleClause) && 			// 最後尾の文節がClauseインスタンスで
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
		List<Clause<?>> commonSubjectsOrigin = new ArrayList<>(subjectList.size());
		for (Map.Entry<Clause<?>, Boolean> entry : subjectsContinuity.entrySet()) {
			commonSubjectsOrigin.add(entry.getKey());
			if (!entry.getValue())	// 主語の連続が途切れたら
				break;				// 核主語集め完了
		}
		// 後続の多くの述語に係る、核たる主語
		Clause<?> mainSubject = commonSubjectsOrigin.get(commonSubjectsOrigin.size()-1);
		List<Clause<?>> predicates = mainSubject.allDependings();
		predicates.retainAll(children);

		if (predicates.size() <= 1) {	// 述語が一つならスルー
			shortSentList.add(this);
			return shortSentList;
		}

		/* 文章分割(dependUpon依存) */
		int fromIndex = 0, toIndex;
		for (final Clause<?> predicate: predicates) {
			predicate.setDepending(SingleClause.ROOT);	// 文末の述語となるので係り先はなし(null)
			toIndex = indexOfChild(predicate) + 1;		// 述語も含めて切り取るため+1
			//System.out.println("divide2.subList(" + fromIndex + ", " + toIndex + ")");
			Sentence subSent = subSentence(fromIndex, toIndex);
			// 文頭の主語は全ての分割後の文に係る
			List<Clause<?>> commonSubjects = SingleClause.cloneAll(commonSubjectsOrigin);

			if (fromIndex!=0) {		// 最初の分割文は、新たに主語を挿入する必要ない
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
		List<Clause<?>> commonSubjectsOrigin = new ArrayList<>(subjectList.size());
		for (Map.Entry<Clause<?>, Boolean> entry : subjectsContinuity.entrySet()) {
		 	commonSubjectsOrigin.add(entry.getKey());
			if (!entry.getValue())	// 主語の連続が途切れたら
				break;				// 核主語集め完了
		}

		/* 述語を収集 */
		String[][] tagParticle = {{"助詞", "-て"}};	// "て"以外の助詞
		String[][] tagAdverb = {{"副詞"}};
		String[][] tagAuxiliary = {{"助動詞", "体言接続"}};
		List<Clause<?>> predicates = new ArrayList<>();
		for (final Clause<?> cls2Last: clausesDependingThisClause(lastClause)) {
			// 末尾が"て"を除く助詞または副詞でないClauseを述語として追加
			if ( !cls2Last.endWith(tagParticle, true) &&
					!cls2Last.endWith(tagAdverb, true) &&
					!cls2Last.endWith(tagAuxiliary, true) )
				predicates.add(cls2Last);
		}
		predicates.add(lastClause);
		predicates.retainAll(children);
		predicates.sort(Comparator.comparing(c -> indexOfChild(c)));

		//List<Integer> commonObjects = new ArrayList<Integer>();	// 複数の述語にかかる目的語を保管

		if (predicates.size() <= 1) { // 述語が一つならスルー
			partSentList.add(this);
			return partSentList;
		}

		/* 文章分割(dependUpon依存) */
		int fromIndex = 0, toIndex;
		for (Iterator<Clause<?>> itr = predicates.iterator(); itr.hasNext(); ) {
			Clause<?> predicate = itr.next();
			predicate.setDepending(SingleClause.ROOT);	// 分割後、当該述語は文末にくるので係り先はなし(null)
			toIndex = indexOfChild(predicate) + 1;	// 述語も含めて切り取るため+1
			//System.out.println("divide3.subList(" + fromIndex + ", " + toIndex + ")");
			//TODO *from>to problem
			Sentence subSent = subSentence(fromIndex, toIndex);
			// 文頭の主語は全ての分割後の文に係る
			List<Clause<?>> commonSubjects = SingleClause.cloneAll(commonSubjectsOrigin);

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
	private boolean gatherDepending(Clause<?> target, Collection<Clause<?>> memberClauses) {
		if (!children.contains(target)) return false;
		if (!children.containsAll(memberClauses)) return false;
		memberClauses.forEach(mc -> mc.setDepending(target));
		return true;
	}

	/**
	 * 主語のリストを得る.
	 */
	public List<Clause<?>> subjectList(boolean includeGa) {
		List<Clause<?>> subjectList;

		if (includeGa) {	// "が"は最初の一つのみ!!
			String[][] tags_Ha_Ga = {{"係助詞", "は"}, {"格助詞", "が"}};	// "は"
			String[][] tags_Ga = {{"格助詞", "が"}};	//"が"
			String[][] tag_De = {{"格助詞", "で"}};	// "で"
			String[][] tag_Ni = {{"格助詞", "に"}};	// "に"
			List<Clause<?>> clause_Ha_Ga_List = collectClausesHaveSome(tags_Ha_Ga);	// 係助詞"は"を含むClause
			List<Clause<?>> clause_Ga_List = collectClausesHaveSome(tags_Ga);	// 係助詞"は"を含むClause
			List<Clause<?>> clause_De_List = collectClausesHaveSome(tag_De);	// 格助詞"で"を含むClause
			List<Clause<?>> clause_Ni_List = collectClausesHaveSome(tag_Ni);	// 格助詞"に"を含むClause
			if (!clause_Ga_List.isEmpty())	clause_Ga_List.remove(0);
			clause_Ha_Ga_List.removeAll(clause_Ga_List);
			clause_Ha_Ga_List.removeAll(clause_De_List);	// "は"と"が"を含むClauseのうち、"で"を含まないものが主語("では"を除外するため)
			clause_Ha_Ga_List.removeAll(clause_Ni_List);	// "は"と"が"を含むClauseのうち、"に"を含まないものが主語("には"を除外するため)
			subjectList = clause_Ha_Ga_List;
		} else {
			String[][] tags_Ha = {{"係助詞", "は"}};	// "は"
			String[][] tag_De = {{"格助詞", "で"}};	// "で"
			String[][] tag_Ni = {{"格助詞", "に"}};	// "に"
			List<Clause<?>> clause_Ha_List = collectClausesHaveSome(tags_Ha);	// 係助詞"は"を含むClause
			List<Clause<?>> clause_De_List = collectClausesHaveSome(tag_De);	// 格助詞"で"を含むClause
			List<Clause<?>> clause_Ni_List = collectClausesHaveSome(tag_Ni);	// 格助詞"に"を含むClause
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
		String[][] tag_Ha = {{"係助詞", "は"}};
		//String[][] tag_Ga = {{"格助詞", "が"}};
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		for (Map.Entry<Clause<?>, Boolean> entry: subjectsContinuity.entrySet()) {
			Clause<?> subject = entry.getKey();		boolean sbjCnt = entry.getValue();
			if (!sbjCnt)
				break;		// 連続した主語の最後尾には必要ない
			// 助詞・連体化"の"を新たに用意
			Adjunct no = new Adjunct(Morpheme.getInstance("の",CabochaTags.getInstance("助詞","連体化","*","*","*","*","の","ノ","ノ")));
			ListIterator<Adjunct> itr = subject.getAdjuncts().listIterator();
			while (itr.hasNext()) {
				Adjunct adjunct = itr.next();
				for (final String[] tag: tag_Ha)
					if (adjunct.hasAllTag(tag)) {
						itr.set(no);	// "は"の代わりに"の"を挿入
						break;
					}
			}
		}
		String[][] tags_NP = {{"助詞", "連体化"}};
		List<Clause<?>> clauses_NP = collectClausesHaveSome(tags_NP);
		clauses_NP.forEach(this::connect2Next);
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

	/**
	 * 決定木のレコード生成.
	 */
	public String toRecord() {
		List<String> values = new ArrayList<>();

		List<Clause<?>> subjectList = subjectList(true);	// 主語を整えたところで再定義
		if(subjectList.isEmpty()) return "";

		Clause<?> subjectClause = subjectList.get(0);			// 主節(!!最初の1つしか使っていない!!)
		// 述節
		Clause<?> predicateClause = subjectClause.getDepending();
		if(predicateClause == null) return "";
		Word predicateWord = predicateClause.getCategorem();	// 述語
		if(predicateWord == null) return "";
		// 述部(主節に続く全ての節)
		//String predicatePart = subSentence(clauses.indexOf(subjectClause)+1, clauses.size()).toString();


		//String[][] tag_Not = {{"助動詞", "ない"}, {"助動詞", "不変化型", "ん"},  {"助動詞", "不変化型", "ぬ"}};
		//boolean not = predicateClause.haveSomeTagWord(tag_Not);	// 述語が否定かどうか

		/* 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか */
		String[][] tagVerb = {{"動詞"}, {"サ変接続"}};
		String[][] tagAdjective = {{"形容詞"}, {"形容動詞語幹"}};

		/* 述語が動詞 */
		if( predicateClause.containsAnyWordsHave(tagVerb) ) {
			String[][] tagPassive = {{"接尾", "れる"}, {"接尾", "られる"}};
			if(predicateClause.containsAnyWordsHave(tagPassive))
				values.add("passive");
			else
				values.add("verb");
			values.add(predicateWord.infinitive());
		/* 述語が形容詞 */
		}else if(predicateClause.containsAnyWordsHave(tagAdjective)) {
			values.add("adjc");
			values.add(predicateWord.infinitive());
		/* 述語が名詞または助動詞 */
		}else {
			values.add("noun");
			String predNoun =predicateWord.infinitive();
			values.add(predNoun.substring(predNoun.length()-2));	// 最後の一文字だけ
		}
		return values.stream().collect(Collectors.joining(","));
	}


	/* ================================================== */
	/* ==========        Output  Method        ========== */
	/* ================================================== */
	public void printM() {
		System.out.println(
				morphemes().stream().map(Morpheme::name).collect(Collectors.joining("'")));
	}
	public void printW() {
		for(final Word word : words()) {
			System.out.print("("+word.id()+")" + word.name());
		}
		System.out.println();
	}
	public void printC() {
		for(final Clause<?> clause : children) {
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
		for(final Word word : words()) { // Word単位で区切る
			System.out.print(word.name() + "|");
		}
		System.out.println();
		for(final Clause<?> clause : children) { // Clause単位で区切る
			System.out.print(clause.toString() + "|");
		}
		System.out.println();
	}


	/* ================================================== */
	/* ==========       Interface Method       ========== */
	/* ================================================== */
	@Override
	public int id() {return id;}
	@Override
	public String name() {
		return getChildren().stream().map(c -> c.name()).collect(Collectors.joining());
	}
	@Override
	public Resource toRDF(Model model) {
		List<Resource> clauseResources = getChildren().stream()
				.map(m -> m.toRDF(model)).collect(Collectors.toList());
		clauseDepending2RDF(clauseResources);
		Resource clauseNode = model.createList(clauseResources.iterator());

		Resource sentenceResource = model.createResource(getURI())
				.addProperty(RDF.type, JASS.Sentence)
				.addProperty(JASS.consistsOfClauses, clauseNode);
		return sentenceResource;
	}
	private void clauseDepending2RDF(List<Resource> clauseResources) {
		children.forEach(cls -> {
			Resource clause = clauseResources.stream().filter(c -> Objects.equals(c.getURI(), cls.getURI())).findAny().orElse(null);
			Clause<?> depending = cls.getDepending();
			Optional<Resource> dependingResource = depending == SingleClause.ROOT? Optional.empty():
					clauseResources.stream().filter(c -> Objects.equals(c.getURI(), depending.getURI())).findAny();
			dependingResource.ifPresent(d -> clause.addProperty(JASS.dependTo, d));
		});
	}


	/* ================================================== */
	/* ==========        Object  Method        ========== */
	/* ================================================== */
	@Override
	public String toString() {
		return children.stream()
				.map(Clause::toString)
				.collect(Collectors.joining("/"));
	}

}
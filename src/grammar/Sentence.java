package grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import data.RDF.Namespace;
import data.RDF.RDFTriple;
import data.RDF.MyResource;

public class Sentence implements GrammarInterface{
	public static int sentSum = 0;

	private final int id;
	public List<Clause> clauses; // Clauseのリストで文を構成する

	private Sentence() {
		id = sentSum++;
		clauses = new ArrayList<>();
	}
	public Sentence(List<Clause> clauseList) {
		this();
		clauses = clauseList;
		//initializeDepending();
	}
	public Sentence(List<Clause> clauseList, Map<Clause, Integer> dependingMap) {
		this();
		clauses = clauseList;
		setClausesComeUnderItself();
		initializeDepending(dependingMap);
	}
	
	/**
	 * 各clauseのdepIndexを元に係り先dependingをセットする
	 * @param clauseList
	 */
	/*
	private void initializeDepending() {
		for (Clause clause : clauses) {
			int depIndex = clause.depIndex;
			clause.setDepending((depIndex != -1)?
					clauses.get(depIndex)
					: null);
		}
	}
	*/
	private void initializeDepending(Map<Clause, Integer> dependingMap) {
		for (Map.Entry<Clause, Integer> entry : dependingMap.entrySet()) {
			Clause clause = entry.getKey(); int deptoIndex = entry.getValue();
			clause.setDepending((deptoIndex != -1)?
					clauses.get(deptoIndex)
					: null);
		}
	}
	private void setClausesComeUnderItself() {
		clauses.stream().forEach(c -> c.comeUnder=this);
	}
	
	public void setSentence(List<Clause> clauseList) {
		clauses = clauseList;
	}

	public int indexOfC(Clause clause) {
		return clauses.indexOf(clause);
	}
	public List<Integer> indexesOfC(List<Clause> clauseList) {
		List<Integer> indexList = new ArrayList<Integer>(clauseList.size());
		for(final Clause clause: clauseList) {
			indexList.add(indexOfC(clause));
		}
		return indexList;
	}
	public Clause nextC(Clause clause) {
		int nextIndex = indexOfC(clause)+1;
		if(nextIndex < 0 || clauses.size() <= nextIndex)
			return null;
		return clauses.get(nextIndex);
	}
	public Clause previousC(Clause clause) {
		int prevIndex = indexOfC(clause)-1;
		if(prevIndex < 0 || clauses.size() <= prevIndex)
			return null;
		return clauses.get(prevIndex);
	}

	public Sentence subSentence(int fromIndex, int toIndex) {
		List<Clause> subClauses = new ArrayList<>(clauses.subList(fromIndex, toIndex));
		return new Sentence(subClauses);
	}

	public List<Integer> collectWords(String name) {
		List<Integer> words = new ArrayList<Integer>();
		for(final Clause clause: clauses) {
			words.addAll(clause.collectWords(name));	// 各clause内を探す
		}
		return words;
	}
	/** 渡された品詞に一致するWordを返す */
	public List<Word> collectTagWords(String[][] tagNames) {
		List<Word> taggedWords = new ArrayList<>();
		for(final Clause clause: clauses) {
			taggedWords.addAll(clause.collectAllTagWords(tagNames));	// 各clause内を探す
		}
		return taggedWords;
	}
	/** 渡された品詞に一致するWordを含むclauseのIDを返す */
	public List<Clause> collectTagClauses(String[][] tagNames) {
		List<Clause> taggedClauses = new ArrayList<>();
		for(final Clause clause: clauses) {
			if(clause.haveSomeTagWord(tagNames))
				taggedClauses.add(clause);	// 各clause内を探す
		}
		return taggedClauses;
	}
	public List<Clause> collectClausesEndWith(String[][][] tagNamess) {
		List<Clause> taggedClauses = new ArrayList<>();
		for(final Clause clause: clauses) {
			for(final String[][] tagNames: tagNamess) {
				if(clause.endWith(tagNames, true)) {
					taggedClauses.add(clause);	// 各clause内を探す
					break;
				}
			}
		}
		return taggedClauses;
	}

	/**
	 * 連続した複数の文節を繋げる. 
	 * @param connectClauseList
	 * @param baseIndex
	 * @return 繋げて1つになった文節
	 */
	public Clause uniteClauses(List<Clause> connectClauseList, int baseIndex) {
		Clause baseClause = connectClauseList.get(baseIndex);	// tagや係り受けはこのClauseに依存．全てこのClauseに収める
		List<Clause> newClauses = clauses;		// Clauseを組み替えた新たなclausesをここに

		// 渡されたClauseがSentence上で連続しているか
		LinkedHashMap<Clause, Boolean> continuity = getContinuity(connectClauseList);
		for(Iterator<Map.Entry<Clause, Boolean>> itr = continuity.entrySet().iterator(); itr.hasNext(); ) {
			Map.Entry<Clause, Boolean> entry = itr.next();
			if(entry.getValue() == false && !itr.hasNext()) {
				System.err.println("error: Not serial in sentence.");
				return baseClause;
			}
		}
		System.out.println("connectclauseList:"+connectClauseList + "\tbase:" + baseClause.toString());

		List<Word> phraseWords = new ArrayList<>();			// 新しいPhraseの元になるWord
		List<Word> functionWords = new ArrayList<>();		// Phrase完成後につなげる接続詞を保持
		Clause depto = baseClause.depending;				// 最後尾のClauseがどのClauseに係るか

		for(Iterator<Clause> itr = connectClauseList.iterator(); itr.hasNext(); ) {
			Clause connectClause = itr.next();

			if(connectClause.equals(baseClause)) {

			}else {
				// 元ClauseのWordはbaseClauseに属するように変える
				for(final Word word: connectClause.words) {
					word.comeUnder = baseClause;
				}
				// 全ての元Clauseの係り先をbaseClauseに変える
				for(final Clause bedep: connectClause.dependeds) {
					bedep.setDepending(baseClause);
				}
			}
			if(!itr.hasNext()) {		// 最後尾の場合
				phraseWords.add(connectClause.getMainWord());			// 被修飾語を入れる
				functionWords.addAll(connectClause.getFunctionWords());	// それに繋がる機能語を入れる
				//depto = connectclause.dependUpon;
			}else {
				phraseWords.addAll(connectClause.words);
			}
		}

		// 新しいPhraseを作成
		Phrase nph = new Phrase();
		nph.setPhrase(phraseWords, baseClause, false);
		List<Word> clauseBaseWords = new ArrayList<>();
		clauseBaseWords.add(nph);
		clauseBaseWords.addAll(functionWords);
		baseClause.setWords(clauseBaseWords);
		baseClause.setDepending(depto);

		// 古いclauseを削除して新しいclauseを挿入
		connectClauseList.remove(baseIndex);
		newClauses.removeAll(connectClauseList);

		updateDependency();
		setSentence(newClauses);
		printDep();
		return baseClause;
	}

	/** 渡された品詞の連続にマッチする単語を連結する */
	public void connectPattern(String[][] pattern, int baseIndex) {
		if(baseIndex >= pattern.length) System.out.println("index error");

		Set<List<Word>> matchingWordsSet = new HashSet<List<Word>>(5);
		List<Word> matchingWords = new ArrayList<>(pattern.length);
		int ptnIdx = 0;
		for(ListIterator<Word> li = getWordList().listIterator(); li.hasNext(); ) {
			Word word = li.next();
			if(word.hasAllTags(pattern[ptnIdx])) {	// マッチした場合
				ptnIdx++;		// 次のパターンへ
				matchingWords.add(word);
			}else {									// マッチしない場合
				for(int i=0; i<ptnIdx; i++) li.previous();	// カーソルを最初にマッチした単語まで戻す
				ptnIdx = 0;		// パターンも一番目にリセット
				matchingWords.clear();
			}

			// パターンの最後までマッチしたら
			if(ptnIdx >= pattern.length) {
				matchingWordsSet.add(matchingWords);	// 連続したWordをしまう
				matchingWords = new ArrayList<>();	// 新しいインスタンスを用意
				ptnIdx = 0;		// パターンのカーソルをリセット
			}
		}

		//printW();
		//System.out.println(matchingWordsSet);

		// Wordの列からclauseの列に直す
		Set<List<Clause>> connectClausesSet = new HashSet<>(matchingWordsSet.size());
		for(final List<Word> matchedWords: matchingWordsSet) {
			List<Clause> connectClauses = new ArrayList<>(matchingWords.size());
			for(final Word matchedWord: matchedWords) {
				Clause belong = matchedWord.comeUnder;
				if(!connectClauses.contains(belong))
					connectClauses.add(belong);	// どのClauseに所属するか
			}
		}
		// 複数のclauseを結合して新しいclauseを作成
		for(final List<Clause> connectClauses: connectClausesSet) {
			uniteClauses(connectClauses, baseIndex);
		}
	}

	/** 渡された品詞を繋げる.
	 * Clauseを跨いで連結はしない */
	public void connect(String[][] tagNames) {
		for(final Clause clause: clauses) {
			clause.connect(tagNames);
		}
	}

	/** 渡された品詞で終わるClauseを次のClauseに繋げる */
	public void connect2Next(String[][] tags, boolean ignoreSign) {
		List<Clause> newClauselist = clauses;
		List<Clause> modifyClauseList = new ArrayList<>();

		for(final Clause clause: clauses) {
			for(final String[] tag: tags) {
				String[][] tagArray = {tag};
				if(clause.endWith(tagArray, ignoreSign)) {	// 指定の品詞で終わるclauseなら(記号考慮)
					modifyClauseList.add(clause);
				}
			}
		}

		List<List<Clause>> phraseBaseClausesList = makeModificationList(modifyClauseList);
		//System.out.println("\tph_clauses" + phraseBaseClausesList);
		// 複数のClauseを結合して新しいClauseを作成
		for(final List<Clause> phraseBaseClauses: phraseBaseClausesList) {
			Clause newCls = new Clause(new ArrayList<Word>(), -1);
			newCls.uniteClauses(phraseBaseClauses);
			// 古いClauseを削除して新しいClauseを挿入
			newClauselist.add(newClauselist.indexOf(phraseBaseClauses.get(0)), newCls);
			newClauselist.removeAll(phraseBaseClauses);
			updateDependency();
		}
		setSentence(newClauselist);
	}

	/**
	 * 上記connect2Nextの補助
	 * 修飾節のリストから修飾節被修飾節のセットを作る
	 */
	private List<List<Clause>> makeModificationList(List<Clause> modifyClauseList) {
		List<List<Clause>> phraseBaseClausesList = new ArrayList<>();
		List<Clause> phraseBaseClauses = new ArrayList<>();
		for(final Clause modifyClause: modifyClauseList) {
			if(!phraseBaseClauses.contains(modifyClause))
				phraseBaseClauses.add(modifyClause);

			int nextIndex = clauses.indexOf(modifyClause) + 1;// 修飾節の次の文節が被修飾節
			if(nextIndex == clauses.size()) continue;			// 修飾節が文末なら回避

			Clause nextClause = clauses.get(nextIndex);			// 修飾語の直後に被修飾語があることが前提の設計
			if(modifyClauseList.contains(nextClause)) continue;	// 三文節以上連続の可能性を考慮

			phraseBaseClauses.add(nextClause);
			phraseBaseClausesList.add(phraseBaseClauses);
			phraseBaseClauses = new ArrayList<>();
		}
		if(phraseBaseClauses.size() > 1) phraseBaseClausesList.add(phraseBaseClauses);
		return phraseBaseClausesList;
	}

	/** 渡したClauseが文中で連続しているかを<clause, Boolean>のMapで返す */
	/* 例:indexのリストが(2,3,4,6,8,9)なら(T,T,F,F,T,F) */
	public LinkedHashMap<Clause, Boolean> getContinuity(List<Clause> clauseList) {
		LinkedHashMap<Clause, Boolean> continuity = new LinkedHashMap<>(clauseList.size());
		List<Integer> clauseIndexList = indexesOfC(clauseList);

		Iterator<Clause> liID = clauseList.listIterator();
		Iterator<Integer> liIdx = clauseIndexList.listIterator();
		int currentIdx = liIdx.next();
		while(liIdx.hasNext() && liID.hasNext()) {
			int nextIdx = liIdx.next();
			Clause clause = liID.next();
			if(currentIdx+1 == nextIdx) {	// indexが連続しているか
				continuity.put(clause, true);
			}else {							// 否か
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
		List<Sentence> shortSentList = new ArrayList<Sentence>(5);
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Clause> subjectList = getSubjectList(false);	// 主節のリスト

		Clause lastClause = clauses.get(clauses.size()-1);	// 文の最後尾Clause
		if(subjectList.contains(lastClause)) {	// 文の最後尾が主節の場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			lastClause.nounize(0, lastClause.words.size());
			subjectList.remove(subjectList.indexOf(lastClause));
		}
		if(subjectList.isEmpty()) return shortSentList;	// 文中に主語がなければ終了

		// 主節の連続性を表す真偽値のリスト
		Map<Clause, Boolean> subjectsContinuity = getContinuity(subjectList);
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		List<Clause> commonSubjectsOrigin = new ArrayList<>(subjectList.size());
		Clause mainSubject = null;
		for(Map.Entry<Clause, Boolean> entry : subjectsContinuity.entrySet()) {
			commonSubjectsOrigin.add(entry.getKey());
			if(!entry.getValue()) {	// 主語の連続が途切れたら
				mainSubject = entry.getKey();	// 後続の多くの述語に係る、核たる主語
				break;				// 核主語集め完了
			}
		}
		List<Clause> predicates = mainSubject.getAllDepending();
		predicates.retainAll(clauses);

		if(predicates.size() < 2) {	// 述語が一つならスルー
			shortSentList.add(this);
			return shortSentList;
		}

		/* 文章分割(dependUpon依存) */
		int fromIndex = 0, toIndex;
		for(final Clause predicate: predicates) {
			predicate.setDepending(null);	// 文末の述語となるので係り先はなし(null)
			toIndex = indexOfC(predicate) + 1;		// 述語も含めて切り取るため+1
			System.out.println("divide2.subList(" + fromIndex + ", " + toIndex + ")");
			Sentence subSent = subSentence(fromIndex, toIndex);
			// 文頭の主語は全ての分割後の文に係る
			List<Clause> commonSubjects = Clause.cloneAll(commonSubjectsOrigin);

			if(fromIndex!=0) {		// 最初の分割文は、新たに主語を挿入する必要ない
				Clause subSentFirst = subSent.clauses.get(0);	// 分割文の先頭文節
				if(subjectList.contains(subSentFirst)) {			// それが主語なら
					commonSubjectsOrigin.add(subSentFirst);			// 後続の短文にも係るので保管
				}
				subSent.clauses.addAll(0, commonSubjects);	// 共通の主語を挿入
			}

			for(Iterator<Clause> itr = commonSubjects.iterator(); itr.hasNext(); ) {
				Clause commonSubject = itr.next();
				commonSubject.setDepending(predicate);	// 係り先を正す
			}
			subSent.gatherDepending(predicate);
			subSent.updateDependency();
			shortSentList.add(subSent);

			int commonSubjectsSize = commonSubjectsOrigin.size();
			if(commonSubjectsSize > 1) {
				Clause nextClause = nextC(predicate);
				if(subjectList.contains(nextClause))	// かつ次が主語である
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
		List<Sentence> partSentList = new ArrayList<Sentence>(5);
		/* 主語を全て探し，それらが連続しているか否かを調べる */
		List<Clause> subjectList = getSubjectList(false);	// 主語のリスト
		if(subjectList.isEmpty()) return partSentList;		// 文中に主語がなければ終了

		Clause lastClause = clauses.get(clauses.size()-1);	// 文の最後尾Clause
		if(subjectList.contains(lastClause)) {		// 文の最後尾が主節の場合
			// おそらく固有名詞を正しく判定できていないせい
			// 最後尾の文節は一つの名詞にする
			lastClause.nounize(0, lastClause.words.size());
			subjectList.remove(lastClause);
		}
		// 主節の連続性を表す真偽値のリスト
		LinkedHashMap<Clause, Boolean> subjectsContinuity = getContinuity(subjectList);
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		List<Clause> commonSubjectsOrigin = new ArrayList<>(subjectList.size());
		for(Map.Entry<Clause, Boolean> entry : subjectsContinuity.entrySet()) {
			commonSubjectsOrigin.add(entry.getKey());
			if(!entry.getValue())	// 主語の連続が途切れたら
				break;				// 核主語集め完了
		}

		/* 述語を収集 */
		String[][] tagParticle = {{"助詞", "-て"}};	// "て"以外の助詞
		String[][] tagAdverb = {{"副詞"}};
		List<Clause> predicates = new ArrayList<>();
		for(final Clause cls2Last: lastClause.dependeds) {
			// 末尾が"て"を除く助詞または副詞でないClauseを述語として追加
			if( !cls2Last.endWith(tagParticle, true) && !cls2Last.endWith(tagAdverb, true) )
				predicates.add(cls2Last);
		}
		predicates.add(lastClause);
		predicates.retainAll(clauses);
		predicates.sort(Comparator.comparing(c -> indexOfC(c)));
		
		//TODO
		System.out.print("predicats : ");
		predicates.stream().mapToInt(p -> indexOfC(p)).forEach(i -> System.out.print(i+","));
		System.out.println();


		//List<Integer> commonObjects = new ArrayList<Integer>();	// 複数の述語にかかる目的語を保管

		if(predicates.size() < 2) { // 述語が一つならスルー
			partSentList.add(this);
			return partSentList;
		}
		
		/* 文章分割(dependUpon依存) */
		int fromIndex = 0, toIndex;
		for(Iterator<Clause> itr = predicates.iterator(); itr.hasNext(); ) {
			Clause predicate = itr.next();
			predicate.setDepending(null);	// 分割後、当該述語は文末にくるので係り先はなし(null)
			toIndex = indexOfC(predicate) + 1;	// 述語も含めて切り取るため+1
			System.out.println("divide3.subList(" + fromIndex + ", " + toIndex + ")");
			Sentence subSent = subSentence(fromIndex, toIndex);			//TODO *from>to problem
			// 文頭の主語は全ての分割後の文に係る
			List<Clause> commonSubjects = Clause.cloneAll(commonSubjectsOrigin);

			if(fromIndex!=0) {		// 最初の分割文は、新たに主語を挿入する必要ない
				Clause subSentFirst = subSent.clauses.get(0);	// 分割文の先頭
				if(subjectList.contains(subSentFirst)) {		// それが主語なら
					commonSubjectsOrigin.add(subSentFirst);		// 後続の短文にも係るので保管
				}
				subSent.clauses.addAll(0, commonSubjects);	// 共通の主語を挿入
			}
			// 主語の係り先を正す
			for(final Clause sbjClause : subSent.getSubjectList(false))
				sbjClause.setDepending(predicate);
			// 係り元の更新
			subSent.gatherDepending(predicate);
			subSent.updateDependency();
			partSentList.add(subSent);

			// 述語のあとに主語があれば共通主語の最後尾を切り捨てる
			int commonSubjectsSize = commonSubjectsOrigin.size();
			if(commonSubjectsSize > 1) {
				Clause nextClause = nextC(predicate);
				if(subjectList.contains(nextClause))	// 次が主語
					commonSubjectsOrigin.remove(commonSubjectsSize-1);
			}

			fromIndex = toIndex;
		}
		return partSentList;
	}

	/**
	 * 文節の係り先が文中にないような場合，渡されたClauseにdependを向ける
	 */
	private void gatherDepending(Clause dependedClause) {
		for (Iterator<Clause> itr = clauses.iterator(); itr.hasNext(); ) {
			Clause clause = itr.next();
			if (!itr.hasNext()) break;	// 最後の述語だけは係り先がnullなのでスルー
			Clause depto = clause.depending;
			if (!clauses.contains(depto))
				clause.setDepending(dependedClause);
		}
	}

	/**
	 * 主語のリストを得る
	 */
	private List<Clause> getSubjectList(boolean useGa) {
		List<Clause> subjectList;

		if (useGa) {	// "が"は最初の一つのみ!!
			String[][][] tags_Ha_Ga = {{{"係助詞", "は"}}, {{"格助詞", "が"}}};	// "は"
			String[][][] tags_Ga = {{{"格助詞", "が"}}};	//"が"
			String[][] tag_De = {{"格助詞", "で"}};	// "で"
			String[][] tag_Ni = {{"格助詞", "に"}};	// "に"
			List<Clause> clause_Ha_Ga_List = new ArrayList<>(collectClausesEndWith(tags_Ha_Ga));	// 係助詞"は"を含むClause
			List<Clause> clause_Ga_List = new ArrayList<>(collectClausesEndWith(tags_Ga));	// 係助詞"は"を含むClause
			List<Clause> clause_De_List = new ArrayList<>(collectTagClauses(tag_De));	// 格助詞"で"を含むClause
			List<Clause> clause_Ni_List = new ArrayList<>(collectTagClauses(tag_Ni));	// 格助詞"に"を含むClause
			if(!clause_Ga_List.isEmpty())	clause_Ga_List.remove(0);
			clause_Ha_Ga_List.removeAll(clause_Ga_List);
			clause_Ha_Ga_List.removeAll(clause_De_List);	// "は"と"が"を含むClauseのうち、"で"を含まないものが主語("では"を除外するため)
			clause_Ha_Ga_List.removeAll(clause_Ni_List);	// "は"と"が"を含むClauseのうち、"に"を含まないものが主語("には"を除外するため)
			subjectList = clause_Ha_Ga_List;
		} else {
			String[][][] tags_Ha = {{{"係助詞", "は"}}};	// "は"
			String[][] tag_De = {{"格助詞", "で"}};	// "で"
			String[][] tag_Ni = {{"格助詞", "に"}};	// "に"
			List<Clause> clause_Ha_List = new ArrayList<>(collectClausesEndWith(tags_Ha));	// 係助詞"は"を含むClause
			List<Clause> clause_De_List = new ArrayList<>(collectTagClauses(tag_De));	// 格助詞"で"を含むClause
			List<Clause> clause_Ni_List = new ArrayList<>(collectTagClauses(tag_Ni));	// 格助詞"に"を含むClause
			clause_Ha_List.removeAll(clause_De_List);		// "は"を含むClauseのうち、"で"を含まないものが主語
			clause_Ha_List.removeAll(clause_Ni_List);		// "は"を含むClauseのうち、"に"を含まないものが主語
			subjectList = clause_Ha_List;		// 主語のリスト
		}
		return subjectList;
	}

	public void uniteSubject() {
		List<Clause> subjectList = getSubjectList(false);
		if(subjectList.isEmpty()) return;

		// 主節の連続性を表す真偽値のリスト
		Map<Clause, Boolean> subjectsContinuity = getContinuity(subjectList);
		//System.out.println("subjContinuity: " + subjectsContinuity);
		String[][] tag_Ha = {{"係助詞", "は"}};
		//String[][] tag_Ga = {{"格助詞", "が"}};
		// 文頭に連続で並ぶ主語は文全体に係るとみなし、集めて使い回す
		for(Map.Entry<Clause, Boolean> entry: subjectsContinuity.entrySet()) {
			Clause subject = entry.getKey();		boolean sbjCnt = entry.getValue();
			if(!sbjCnt) break;	// 連続した主語の最後尾には必要ない

			//if(subject.endWith(tag_Ga, true)) {
				/*
				Word ha = new Word();	// 係り助詞"が"を新たに用意
				ha.setWord("は", Arrays.asList("助詞","係助詞","*","*","*","*","は","ハ","ワ"), subject.clauseID, false);
				int index_Ga = subject.indexOfW(subject.collectAllTagWords(tag_Ga).get(0));
				subject.wordIDs.set(index_Ga, ha.wordID);	// "は"の代わりに"が"を挿入
				*/
			//}else {
			// 助詞・連体化"の"を新たに用意
				Word no = new Word("の", Arrays.asList("助詞","連体化","*","*","*","*","の","ノ","ノ"), subject, false);
				int index_Ha = subject.indexOfW(subject.collectAllTagWords(tag_Ha).get(0));
				subject.words.set(index_Ha, no);	// "は"の代わりに"の"を挿入
			//}
		}
		String[][] tags_NP = {{"助詞", "連体化"}};
		connect2Next(tags_NP, true);
	}

	/** 文章から関係を見つけtripleにする */
	public List<RDFTriple> extractRelation() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();

		List<Clause> subjectList = getSubjectList(true);	// 主語を整えたところで再定義
		if(subjectList.isEmpty()) {
			System.err.println("subjectClause is null.");
			return triples;
		}
		Clause subjectClause = subjectList.get(0);			// 主節(!!最初の1つしか使っていない!!)
		Word subjectWord = subjectClause.getMainWord();		// 主語
		if(subjectWord == null) {
			System.err.println("subjectWord is null.");
			return triples;
		}
		MyResource subject = new MyResource(Namespace.GOO, subjectWord.name);
		this.printDep();
		// 述節
		Clause predicateClause = subjectClause.depending;
		if(predicateClause == null) {
			System.err.println("predicateClause is null.");
			return triples;
		}
		Word predicateWord = predicateClause.getMainWord();	// 述語
		if(predicateWord == null) {
			System.err.println("predicateWord is null.");
			return triples;
		}
		// 述部(主節に続く全ての節)
		String predicatePart = subSentence(clauses.indexOf(subjectClause)+1, clauses.size()).toString();

		//List<Clause> complementClauses;								// 補部
		//Word complementWord;											// 補語

		String[][] tag_Not = {{"助動詞", "ない"}, {"助動詞", "不変化型", "ん"},  {"助動詞", "不変化型", "ぬ"}};
		boolean isNot = (predicateClause.haveSomeTagWord(tag_Not))? true: false;	// 述語が否定かどうか

		//System.out.println(subjectClause.toString() + "->" + predicateClause.toString());

		/* 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか */
		String[][] tagVerb = {{"動詞"}, {"サ変接続"}};
		String[][] tagAdjective = {{"形容詞"}};

		/* リテラル情報かどうか */
		/* 長さ */
		String regexLength = "(.*?)[長径]?.*?(\\d+(\\.\\d+)?)([ア-ンa-zA-Zー－]+).*?";	// 「~(数字)(単位)~」を探す
		Pattern ptrnLength = Pattern.compile(regexLength);
		Matcher mtchLength = ptrnLength.matcher(predicatePart);
		boolean boolLength = mtchLength.matches();
		/* 重さ */
		String regexWeight = "(.*?)重.*?(\\d+(\\.\\d+)?)([ア-ンa-zA-Zー－]+).*?";	// 「~(数字)(単位)~」を探す
		Pattern ptrnWeight = Pattern.compile(regexWeight);
		Matcher mtchWeight = ptrnWeight.matcher(predicatePart);
		boolean boolWeight = mtchWeight.matches();

		if(boolLength || boolWeight) {
			System.out.println("リテラル");//TODO
			if(boolLength) {
				MyResource blank = new MyResource(Namespace.EMPTY, subjectWord.name+"-length");
				triples.add(new RDFTriple(subject, MyResource.LENGTH, blank));			// 空白ノード
				triples.add(new RDFTriple(blank, MyResource.VALUE, new MyResource(Namespace.LITERAL, mtchLength.group(2))));	// リテラル
				triples.add(new RDFTriple(blank, MyResource.UNITS, new MyResource(Namespace.LITERAL, mtchLength.group(4))));	// 単位
			}
			if(boolWeight) {
				MyResource blank = new MyResource(Namespace.EMPTY, subjectWord.name+"-weight");
				triples.add(new RDFTriple(subject, MyResource.WEIGHT, blank));			// 空白ノード
				triples.add(new RDFTriple(blank, MyResource.VALUE, new MyResource(Namespace.LITERAL, mtchLength.group(2))));	// リテラル
				triples.add(new RDFTriple(blank, MyResource.UNITS, new MyResource(Namespace.LITERAL, mtchLength.group(4))));	// 単位
			}
		/* 述語が動詞 */
		}else if( predicateClause.haveSomeTagWord(tagVerb) ) {
			System.out.println("動詞");//TODO
			/* "がある"かどうか */
			String[][] tag_Have = {{"動詞", "ある"}, {"動詞", "もつ"}, {"動詞", "持つ"}};		// 動詞の"ある"(助動詞ではない)
			boolean boolHave = predicateClause.haveSomeTagWord(tag_Have);
			/* "~の総称" */
			String regexGnrnm = "(.*?)(の総称)";				// 「〜の総称」を探す
			Pattern ptrnGnrnm = Pattern.compile(regexGnrnm);
			Matcher mtchGnrnm = ptrnGnrnm.matcher(predicateClause.toString());
			boolean boolGnrnm = mtchGnrnm.matches();

			if(boolHave) {			// "~がある","~をもつ"
				Clause previousClause = previousC(predicateClause);		// 動詞の一つ前の文節
				if(previousClause == null) return triples;
				MyResource part = new MyResource(Namespace.GOO, previousClause.getMainWord().name);	// その主辞のリソース
				String[][] tag_Ga_Wo = {{"格助詞", "が"}, {"格助詞", "を"}};
				if(previousClause.haveSomeTagWord(tag_Ga_Wo)) {
					triples.add(new RDFTriple(part, new MyResource(Namespace.DCTERMS, "isPartOf"), subject));
					triples.add(new RDFTriple(subject, new MyResource(Namespace.DCTERMS, "hasPart"), part));
				}

			}else if(boolGnrnm) {	// "~の総称"
				triples.add(new RDFTriple(subject, MyResource.EQUIVALENT_CLASS, new MyResource(Namespace.GOO, mtchGnrnm.group(1))));

			}else {					// その他の動詞
				MyResource verb = new MyResource(Namespace.GOO, predicateWord.tags.get(6));	// 原形を取り出すためのget(6)
				MyResource object = null;

				// 格助詞"に","を","へ"などを元に目的語を探す
				String[][] tag_Ni_Wo = {{"格助詞", "が"}, {"格助詞", "に"}, {"格助詞", "を"}};	// 目的語oと述語pを結ぶ助詞
				List<Clause> clauses_Ni_Wo = collectTagClauses(tag_Ni_Wo);
				if (!clauses_Ni_Wo.isEmpty()) {	// 目的語あり
					Clause clause_Ni_Wo = clauses_Ni_Wo.get(0);
					Word word_Ni_Wo = clause_Ni_Wo.getMainWord();	// "に"または"を"の主辞
					object = new MyResource(Namespace.GOO, word_Ni_Wo.name);
				} else {								// 目的語なしならnullのまま
					object = MyResource.NO_OBJECT;
				}
				RDFTriple triple = new RDFTriple(subject, verb, object);
				triples.addAll(makeObjectiveProperty(triple, isNot));
			}


		/* 述語が形容詞 */
		}else if(predicateClause.haveSomeTagWord(tagAdjective)) {
			System.out.println("形容詞");//TODO
			MyResource adjective = new MyResource(Namespace.GOO, predicateWord.tags.get(6));
			Clause previousClause = previousC(predicateClause);	// 形容詞の一つ前の文節
			if(previousClause == null) return triples;
			String[][] tag_Ga = {{"格助詞", "が"}};
			if(previousClause.haveSomeTagWord(tag_Ga)) {
				String part = previousClause.getMainWord().name;	// その主辞の文字列
				subject.setFragment(subject.getFragment()+"の"+part);
			}
			triples.add(new RDFTriple(adjective, new MyResource(Namespace.EXAMPLE, "attributeOf"), subject));

		/* 述語が名詞または助動詞 */
		}else {
			System.out.println("名詞");//TODO
			/* 別名・同義語かどうか */
			String regexSynonym = "(.*?)((に同じ)|(の別名)|(の略)|(のこと)|(の異称))";	// 「〜の別名」「〜に同じ」を探す
			Pattern ptrnSynonym = Pattern.compile(regexSynonym);
			Matcher mtchSynonym = ptrnSynonym.matcher(predicatePart);
			boolean boolSynonym = mtchSynonym.matches();
			/* 一種・一品種かどうか */
			String regexKind = "(.*?)((の一種)|(の一品種))";					// 「〜の一種」「〜の一品種」を探す
			Pattern ptrnKind = Pattern.compile(regexKind);
			Matcher mtchKind = ptrnKind.matcher(predicatePart);
			boolean boolKind = mtchKind.matches();
			/* 形容動詞語幹を含むか */
			String[][] tag_Adjective = {{"形容動詞語幹"}};
			boolean boolAdjective = predicateClause.haveSomeTagWord(tag_Adjective);

			if(boolSynonym) {
				//relations.add( new RDFTriple(subjectWord.name, "owl:sameClassAs", mtchSynonym.group(1)));
				triples.add(new RDFTriple(new MyResource(Namespace.GOO, mtchSynonym.group(1)), MyResource.ALTER_NAME, subject));
			}else if(boolKind) {
				triples.add( new RDFTriple(subject, MyResource.TYPE, new MyResource(Namespace.GOO, mtchKind.group(1))));
			}else if(boolAdjective) {
				MyResource adjective = new MyResource(Namespace.GOO, predicateWord.tags.get(6));
				triples.add( new RDFTriple(adjective, new MyResource(Namespace.EXAMPLE, "attributeOf"), subject));
			}else {
				triples.add(new RDFTriple(subject, MyResource.SUB_CLASS_OF, new MyResource(Namespace.GOO, predicateWord.name)));	// 述語が名詞の場合これがデフォ
			}
		}
		
		return triples;
	}

	/** (s,p,o)の三つ組を受け取り，domain，rangeの定義をする． */
	private List<RDFTriple> makeObjectiveProperty(RDFTriple triple, boolean not) {
		MyResource s = triple.getSubject(), p = triple.getPredicate(), o = triple.getObject();
		List<RDFTriple> triples = new LinkedList<RDFTriple>();

		triples.add( new RDFTriple(p, MyResource.TYPE, MyResource.ACTION) );
		
		triples.add( new RDFTriple(p, MyResource.AGENT, s));
		if(o != null) {	// 目的語あり
			triples.add( new RDFTriple(p, MyResource.OBJECT, o));	
		}else {			// 目的語なし
		}
		
		if(!not) {	// 原形
			triples.add(new RDFTriple(s,p,o));
		}else {		// 否定形
			triples.addAll(makeNegation(triple));
		}
		return triples;
	}
	/** (s,p,o)の否定のオントロジーを返す */
	private List<RDFTriple> makeNegation(RDFTriple triple) {
		MyResource s = triple.getSubject(), p = triple.getPredicate(), o = triple.getObject();
		
		List<RDFTriple> triples = new LinkedList<RDFTriple>();
		MyResource blank = new MyResource(Namespace.EMPTY, id + "-not");	// 空白ノードの名前を決める
		triples.add(new RDFTriple(blank, MyResource.TYPE, new MyResource(Namespace.OWL, "NegativePropertyAssertion")));
		triples.add(new RDFTriple(blank, new MyResource(Namespace.OWL, "sourceIndividual"), s));
		triples.add(new RDFTriple(blank, new MyResource(Namespace.OWL, "assertionProperty"), p));
		if(o!=null)	// 目的語が存在する場合のみ
			triples.add( new RDFTriple(blank, new MyResource(Namespace.OWL, "targetIndividual"), o));

		return triples;
	}

	/** ClauseのリストからWordのリストにする */
	public List<Word> getWordList() {
		List<Word> wordList = new ArrayList<>();
		for(final Clause clause: clauses) {
			wordList.addAll(clause.words);
		}
		return wordList;
	}

	/**
	 * Clauseの係り受け関係を更新する.
	 * 全てのclauseインスタンスのdependUponが正しいことが前提の設計.
	 */
	public void updateDependency() {
		clauses.forEach(c -> c.dependeds.clear());	// 一度全ての被係り受けをまっさらにする

		for(final Clause clause : clauses) {
			Clause depto = clause.depending;
			if(depto != null) depto.dependeds.add(clause);
		}
	}

	public String toRecord() {
		List<String> values = new ArrayList<>();

		List<Clause> subjectList = getSubjectList(true);	// 主語を整えたところで再定義
		if(subjectList.isEmpty()) return "";

		Clause subjectClause = subjectList.get(0);			// 主節(!!最初の1つしか使っていない!!)
		// 述節
		Clause predicateClause = subjectClause.depending;
		if(predicateClause == null) return "";
		Word predicateWord = predicateClause.getMainWord();	// 述語
		if(predicateWord == null) return "";
		// 述部(主節に続く全ての節)
		//String predicatePart = subSentence(clauses.indexOf(subjectClause)+1, clauses.size()).toString();


		//String[][] tag_Not = {{"助動詞", "ない"}, {"助動詞", "不変化型", "ん"},  {"助動詞", "不変化型", "ぬ"}};
		//boolean not = predicateClause.haveSomeTagWord(tag_Not);	// 述語が否定かどうか

		/* 述語が[<名詞>である。]なのか[<動詞>する。]なのか[<形容詞>。]なのか */
		String[][] tagVerb = {{"動詞"}, {"サ変接続"}};
		String[][] tagAdjective = {{"形容詞"}, {"形容動詞語幹"}};

		/* 述語が動詞 */
		if( predicateClause.haveSomeTagWord(tagVerb) ) {
			String[][] tagPassive = {{"接尾", "れる"}, {"接尾", "られる"}};
			if(predicateClause.haveSomeTagWord(tagPassive))
				values.add("passive");
			else
				values.add("verb");
			values.add(predicateWord.tags.get(6));
		/* 述語が形容詞 */
		}else if(predicateClause.haveSomeTagWord(tagAdjective)) {
			values.add("adjc");
			values.add(predicateWord.tags.get(6));
		/* 述語が名詞または助動詞 */
		}else {
			values.add("noun");
			String predNoun =predicateWord.tags.get(6);
			values.add(predNoun.substring(predNoun.length()-2));	// 最後の一文字だけ
		}
		return values.stream().collect(Collectors.joining(","));
	}

	/********************************/
	/************* 出力用 ************/
	/********************************/
	@Override
	public String toString() {
		return clauses.stream().map(c -> c.toString()).collect(Collectors.joining());
	}
	
	public void printDetail() {
		System.out.println(toString());
	}

	public void printW() {
		for(final Word word : getWordList()) {
			System.out.print("("+word.id+")" + word.name);
		}
		System.out.println();
	}
	public void printSF() {
		for(final Word word : getWordList()) {
			boolean sf = word.isCategorem;
			String t_f = sf? "T": "F";
			System.out.print("("+t_f+")" + word.name);
		}
		System.out.println();
	}
	public void printC() {
		for(final Clause clause : clauses) {
			System.out.print("("+clause.id+")" + clause.toString());
		}
		System.out.println();
	}
	public void printDep() {
		for (int i = 0; i < clauses.size(); i++) {
			Clause clause = clauses.get(i);
			Clause depto = clause.depending;
			int depIndex = (depto == null)? -1	: clauses.indexOf(depto);
			System.out.print("(" + i + ">" + depIndex + ")" + clause.toString());
		}
		System.out.println();
	}
	/** 文を区切りを挿入して出力する */
	public void printS() {
		for(final Word word : getWordList()) { // Word単位で区切る
			System.out.print(word.name + "|");
		}
		System.out.println();
		for(final Clause clause : clauses) { // Clause単位で区切る
			System.out.print(clause.toString() + "|");
		}
		System.out.println();
	}
}

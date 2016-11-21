package englishParse;

import java.util.*;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class Sentence {
	public List<Word> body; // 文の本体は単語のリスト
	public List<Integer> srNums; // srialNumのリストとしても表現する
	public Tree parse;
	
	public Sentence() {
		body = new ArrayList<Word>();
		srNums = new ArrayList<Integer>();
		parse = null;
	}
	
	/* Stringで渡された文を解析しWord型リストにして扱えるようにする */
	public void setSentence(String strSentence) {
		String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		parse = lp.parse(strSentence);
		parse.pennPrint(); // 木構造を表示
		TreebankLanguagePack tlp = lp.getOp().langpack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		
		// まず開始文字?ROOTを登録
		Word nword = new Word();
		nword.setWord("ROOT", "START");
		body.add(nword);
		srNums.add(nword.serialNum);
		// 単語を登録
		for(TaggedWord wordWithTag: parse.taggedYield()) {
			nword = new Word();
			nword.setWord(wordWithTag.word(), wordWithTag.tag());
			body.add(nword);
			srNums.add(nword.serialNum);
			System.out.print(nword.wordName+", ");
		}
		System.out.println("\n");
		
		// 従属関係を登録
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		System.out.println(tdl);
		for(TypedDependency td: tdl) {
			int gov = td.gov().index();
			int dep = td.dep().index();
			String reln = td.reln().toString();
			/*
			System.out.print(td.reln() + ", ");
			System.out.print(td.gov().index() + ", ");
			System.out.print(td.dep().index());
			System.out.println("\t = " + td);
			 */
			Word.get(srNums.get(gov)).dependUpon.put(dep, reln);
			Word.get(srNums.get(dep)).beDepended.put(gov, reln);
		}	
	}
	
	public void addS(int sn) {
		body.add(Word.get(sn));
		srNums.add(sn);		
	}
	public void addS(int index, int sn) {
		body.add(index, Word.get(sn));
		srNums.add(index, sn);
	}
	public void addAllS(Sentence snt) {
		body.addAll(snt.body);
		srNums.addAll(snt.srNums);
	}
	public int indexOfS(int sn) {
		return srNums.indexOf(sn);
	}
	public List<Integer> indexesOfS(List<Integer> snl) {
		List<Integer> indexList = new ArrayList<Integer>();
		for(int sn: snl) {
			indexList.add(indexOfS(sn));
		}
		return indexList;
	}
	public void removeS(int index) {
		body.remove(index);
		srNums.remove(index);
	}
	
	/* 渡された品詞に一致する単語をリストにして返す */
	public List<Word> collectWords(String[] tagNames) {
		List<String> tagNamesList = Arrays.asList(tagNames);
		List<Word> wordList = new ArrayList<Word>();
		for(int idx: srNums) {
			//if(tagNamesList.contains(wd.nonTerminal)) wordList.add(wd);
			if(tagNamesList.contains(Word.get(idx).nonTerminal)) wordList.add(Word.get(idx));
		}
		return wordList;
	}
	// serialNumのリストで返すバージョン
	public List<Integer> collectWords(String[] tagNames, int i) {
		List<String> tagNamesList = Arrays.asList(tagNames);
		List<Integer> wordNums = new ArrayList<Integer>();
		for(int idx: srNums) {
			//if(tagNamesList.contains(wd.nonTerminal)) wordNums.add(wd.serialNum);
			if(tagNamesList.contains(Word.get(idx).nonTerminal)) wordNums.add(idx);
		}
		return wordNums;
	}
	
	/* 文から2つの引数で挟まれた部分だけ切り取る */
	public Sentence subSentence(int fromIndex, int toIndex) {
		Sentence sub = new Sentence();
		sub.body = body.subList(fromIndex, toIndex);
		sub.srNums = srNums.subList(fromIndex, toIndex);
		return sub;
	}
	
	/* 渡された単語を連結して文を組み直す */
	/* serialNumberとIndexの混同に注意! */
	public Sentence concatenate(List<Integer> srNml) {
		//List<String> depRelation = Arrays.asList("amod", "compound"); //この従属関係を持つもの同士をつなげる
		Sentence connectedSent = this;
		
		List<Integer> idxList = connectedSent.indexesOfS(srNml);
		List<List<Integer>> snls = new ArrayList<List<Integer>>();
		int from = 0, to = 1;
		int now = idxList.get(0), next = 0;
		for(int i=0; i<idxList.size(); to = ++i+1, now = next) {
			if(to<idxList.size()) next = idxList.get(to);
			//System.out.println("now: " + now + ",next: " + next);
			if( !(now+1 == next) ) {
				snls.add(srNml.subList(from, to));
				//System.out.println("(" + from + "->" + to + ")" + idxLists);
				from = to;
			}
		}
		for(List<Integer> snl: snls) connectedSent.replacePhrase(snl);	
		
		/*
		int nowSrNm, nowIdx, nextIdx = -1;
		List<Integer> prsSNl = new ArrayList<Integer>(); // Phrase生成の元になる単語列
		for(int i=0; i<srNml.size(); i++) {
			// 引数のserialNumを一つずつ見て、そのIndexが並んでいるかを見ていく
			// serialNumが並んでいるかは関係ない
			nowSrNm = srNml.get(i);
			nowIdx = srNums.indexOf(nowSrNm);
			if(i+1<srNml.size()) nextIdx = srNums.indexOf(srNml.get(i+1));
			prsSNl.add(nowSrNm);
			// 次の単語が文中で並んでいない=連続でない場合
			if( !(nowIdx == nextIdx - 1) ) {
				// 連続したWord列を渡しPhraseに置き換える
				connectedSent.replacePhrase(prsSNl);
				prsSNl.clear(); // 次に使うためリセット
			}
		}
		*/
		return connectedSent;
	}
	
	/* 受け取った連続したWordをつなげて1つのPhraseに置き換える */
	public void replacePhrase(List<Integer> prsSNl) {
		if(srNums.containsAll(prsSNl)) {
			Phrase newTerm = new Phrase();
			newTerm.setPhrase(prsSNl);
			newTerm.copyDependency(prsSNl.get(prsSNl.size()-1)); // 従属関係は最後尾の単語と同じ
			
			int insertIndex = indexOfS(prsSNl.get(0));
			for(int i=0; i<prsSNl.size(); i++) {
				this.removeS(insertIndex);
			}
			this.addS(insertIndex, newTerm.serialNum);
		}
	}
	
	/* 所定の単語の位置で文を分割する */
	/* 分割した小文をリストにして返す */
	public List<Sentence> breakSentence() {
		List<Sentence> brokenSents = new ArrayList<Sentence>();
		String[] breakers = {",", ".", "and", "that", "who", "which"}; // 文を区切る境界となる語
		List<String> breakersList = Arrays.asList(breakers);
		
		// 主部探し
		Tree tr = parse;
		for(Iterator<Tree> itr = parse.iterator(); itr.hasNext(); ) {
			tr = itr.next();
			String tag = tr.label().toString();
			/*
			System.out.println(tr.value() + " ...");
			System.out.println("  label   : "+tr.label());
			System.out.println("  leaves  : "+tr.getLeaves());
			System.out.println("  children: "+tr.numChildren() + "={ ");
			*/
			if(tag.equals("NP")) {
				break;
			}
		}
		// 主部作り
		List<Tree> nouns = new ArrayList<Tree>(tr.getLeaves());
		List<Integer> nounsSN = new ArrayList<Integer>();
		for(Tree n: nouns) {
			String[] lbl = n.label().toString().split("-");
			int sn = Integer.parseInt(lbl[lbl.length-1]);
			nounsSN.add(sn);
		}
		System.out.println(nounsSN);
		Sentence noun = new Sentence();
		// serialNumとindexを混同しています!!
		// できるだけ早く他の手を考えて直せ!!
		for(int sn: nounsSN) noun.addS(sn);
		
		// fromIdxが突貫工事でひどい有様 改善求む
		int fromIdx = nounsSN.get(nounsSN.size()-1)-1, toIdx = 0, i = 0; // fromが1からなのは0番目の開始文字ROOTを避けるため
		for(Word wd: body) {
			//System.out.println(i + ": " + wd.wordName +"(from: "+fromIdx + ", " + "to: " + toIdx + ")");
			if(breakersList.contains(wd.wordName)) {
				toIdx = i;
				//Sentence sub = subSentence(fromIdx, toIdx);
				Sentence sub = new Sentence();
				sub.addAllS(noun); // 主部挿入
				sub.addAllS(subSentence(fromIdx+1, toIdx));
				brokenSents.add(sub);
				fromIdx = toIdx;
			}
			i++;
		}
		return brokenSents;
	}
	
	/* 文を標準出力する(ピリオドの前にもスペースが入ってしまうのはご愛嬌) */
	public void printSentence() {
		for(int idx: srNums) {
			//System.out.print(wd.wordName + " ");
			System.out.print(Word.get(idx).wordName + " ");
		}
		System.out.println();
	}
	public void printDependency() {
		for(Word wd: body) {
			System.out.println("("+wd.serialNum+")" + wd.wordName + "\t=" + wd.dependUpon);
			System.out.println("("+wd.serialNum+")" + wd.wordName + "\t=" + wd.beDepended);
		}
	}
	public static void printAllDependency() {
		for(Word wd: Word.allWordsList) {
			System.out.println("("+wd.serialNum+")" + wd.wordName + "\t:" + wd.dependUpon);
			System.out.println("("+wd.serialNum+")" + wd.wordName + "\t:" + wd.beDepended);
		}
	}
}

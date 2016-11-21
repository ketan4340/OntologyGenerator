package japaneseParse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		String sent_str1 = "クジラは広い海を泳いでいる。";	
		String sent_str2 = "鯨とは水生の巨大な哺乳類である。";
		//String sample = "辞書とは多数の語を集録し、一定の順序に配列して一つの集合体として、個々の語の意味・用法、またはその示す内容について記したもの。";
		//String sample = "夏目漱石はこころを執筆した。";
		//String sample = "クジラ目の哺乳類の総称。";
		
		String[] entries = {		// 辞書の見出し語群
				"アイアイ",
				"間鴨",
				"藍子",
				"秋沙"
		};
		String[] interpretations = {	// 辞書の語釈群
				"《その鳴き声から》アイアイ科の原始的な猿。頭胴長40センチくらいで、尾が長い。長い指は鉤爪 (かぎづめ) をもち、樹皮下の昆虫を掘り出して食う。マダガスカル島にのみ生息。指猿 (ゆびざる) 。",
				"アヒルの一品種。マガモと青首アヒルとの雑種。肉用。なきあひる。",
				"スズキ目アイゴ科の海水魚。全長約55センチ。体は楕円形で側扁し、淡褐色の地に小白点がある。背びれ・しりびれ・腹びれのとげに毒がある。本州中部以南の沿岸にすむ。冬季に美味。",
				"《「あきさ」の音変化》カモ科アイサ属の鳥の総称。くちばしは細長く、縁が鋸歯 (きょし) 状。潜水が巧みで、魚を捕食。日本では冬鳥であるが、北海道で繁殖するものもある。ウミアイサ・カワアイサ・ミコアイサの3種がみられる。のこぎりばがも。あいさがも。"
		};
		// かっこを消すか検討しよう
		
		
		/*** Collecting Entries ***/
		/* 見出し語と語釈の対応をmapに登録 */
		//List<String> entryList = Arrays.asList(entries);
		//List<String> interpretationList = Arrays.asList(interpretations);
		LinkedHashMap<String,String> lhMap = new LinkedHashMap<String,String>();
		for(int i=0; i<entries.length; i++) {
			String entry = entries[i];
			String interpretation = interpretations[i];
			lhMap.put(entry, interpretation);
		}
		/* 読点。で語釈を切り一文ずつに分ける */
		List<String> writings = new ArrayList<String>();
		for(int i=0; i<interpretations.length; i++) {
			String interpretation = interpretations[i];
			String[] writing = interpretation.split("。");
			//List<String> writingList = Arrays.asList(writing);
			for(int j= 0; j < writing.length; j++) {		// *要注意*
				writing[j] = entries[i] + "は" + writing[j];	// 全ての語釈に"「見出し語」は"をつけて主語を確保するという強行手段
				writing[j] = writing[j].replaceAll(" | |　", "");	// ここにいるスペースは何者なの?&nbsp;か?
				writing[j] = writing[j].replaceAll("\\(.+?\\)", "");
				writing[j] = writing[j].replaceAll("\\《.+?\\》", "");
				System.out.println(writing[j]);
				writings.add(writing[j]);
			}
		}
		
		/*
		Crawler crw = new Crawler("goo");
		try {
			sentences = crw.search(1, 10);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		List<List<String>> relations = new ArrayList<List<String>>();
		for(String writing: writings) {
			
			/*** Syntactic Parsing Module ***/
			System.out.println("\n\t Step0");
			Parser parse = new Parser("cabocha");
			Sentence sent = parse.run(writing);
					
			sent.printS();
			
			/*** Semantic Parsing Module ***/
			/** Step1: Term Extraction **/
			/* 名詞と形容詞だけ取り出す */
			System.out.println("\n\t Step1");
			String[][] tagNP = {{"形容詞"}, {"連体詞"}, {"助詞", "連体化"}, {"助動詞", "体言接続"}}; //これらを含むChunkを係り受け先につなげる
			List<Integer> wordList_NP = new ArrayList<Integer>(sent.collectTagWords(tagNP)); // 上記のtagを持つWordを集めた
			
			for(int word_NP: wordList_NP) {
				//System.out.println(word_NP + "@(C"+Word.get(word_NP).inChunk+"): " + Word.get(word_NP).wordName);
				System.out.print(Word.get(word_NP).wordName + ", ");
			}
			 		
			
			/** Step2: Concatenation **/
			/* 名詞と名詞または形容詞と名詞をつなげて1つの名詞句にする */
			System.out.println("\n\t Step2");
			
			Sentence connectedSent = sent.concatenate(wordList_NP);
			connectedSent.printS();
			
			
			/** Step3: Break Phrases **/
			System.out.println("\n\t Step3");
			// 未実装
			
			/** Step4: Relations Extraction **/
			/* 単語間の関係を見つけ，グラフにする(各単語及び関係性をNodeのインスタンスとする) */
			System.out.println("\n\t Step4");
			List<List<String>> relation = connectedSent.extractRelation(); 
			relations.addAll(relation);
		}
		
		// 得られた関係を読み取り，uriとtriplesに入れる
		List<String> uri = new ArrayList<String>();	// ここに入れた単語はWordとしての情報を失いその後は文字列として扱う
		uri.add("rdf:type");
		uri.add("rdfs:subClassOf");
		uri.add("rdfs:subPropertyOf");
		uri.add("rdfs:domain");
		uri.add("rdfs:range");
		
		List<List<Integer>> triples = new ArrayList<List<Integer>>();
		for(final List<String> relation: relations) {
        	System.out.println(relation);
        	List<Integer> triple_id = new ArrayList<Integer>(3);
        	for (int i=0; i < 3; i++) {
        		String concept = relation.get(i);
        		if( !uri.contains(concept) ) uri.add(concept);
        		triple_id.add(uri.indexOf(concept));
        	}
        	triples.add(triple_id);
        }
        
		//List<Node> nodes = Node.setTriples2Nodes(uri, triples);
		/*
		for(int i=0; i<uri.size(); i++) {
			System.out.println(i + ":\t" + uri.get(i));
			nodes.get(i).printNode1();
			nodes.get(i).printNode2();
		}
		*/
	// ここまでMainに詰め込みすぎ．何らかのクラスのメソッドにしましょう
		
		/*** OWL DL Axiom Module ***/
		OntologyBuilder dlb = new OntologyBuilder();
		dlb.writeOntology(uri, triples);
		dlb.output("ontology");	// 渡すのはファイル名
		//dlb.print();
		
	}

}

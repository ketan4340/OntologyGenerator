package japaneseParse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Main {

	public static void main(String[] args) {
		List<String> writingList = new ArrayList<String>();
		List<Sentence> sentList = new ArrayList<Sentence>();
		List<List<String>> relations = new ArrayList<List<String>>();

		String[] writings = {
				//"クジラは広い海を泳いでいる。",
				//"鯨とは水生の巨大な哺乳類である。",
				"鮎魚女は岩礁域に多く、体色は黄褐色から紫褐色まで場所によって変わる",
				"鮎魚女は岩礁域に多く、体色は黄褐色から紫褐色まで場所によって変わり、尾びれは短い",
				"アイベックスは角は、雄のものは大きくて後方に湾曲し、表面に竹のような節がある",
				//"藍鮫は全長約1メートル",
				"アイアイは長い指は鉤爪をもち、樹皮下の昆虫を掘り出して食う。",
				"アイアイは頭胴長40センチくらいで、尾が長い",
				//"藍子は全長約55センチ。",
				//"青擬天牛は体長13ミリくらい",
				"秋沙は日本では冬鳥であるが、北海道で繁殖するものもある",
				"青大将は全長1.5～2.5メートルで、日本では最大",
				//"青眼狗母魚は体長は10～15センチ",
				"葵貝は雌は貝殻をもち、殻は扁平で直径10～25センチ、白色で放射状のひだがある",
				"葵貝は雄は体長約1.5センチで、殻をつくらない",
				//"甘子はえのは",
				"コアラは夜行性で木の上にすみ、ユーカリの葉だけを食べる",
				"鯉は体は長い筒形で背から腹へかけての幅が広く、長短二対の口ひげがある",
				//"一角は一分銀の異称",
				//"犬はドッグに同じ"
		};
		writingList.addAll(Arrays.asList(writings));
		
		/*** Collecting Entries ***/
		/* 外部ファイルから日本語テキストを読み込む */
		/*
		//String readFile = "gooText生物-動物名-test.txt";
		String readFile = "writings/gooText生物-動物名-わ.txt";
		
		File file = new File(readFile);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while(line != null) {
				writingList.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		for(String writing: writingList) {
			/*** Syntactic Parsing Module ***/
			System.out.println("\n\t Step0");
			Parser parse = new Parser("cabocha");
			Sentence originalSent = parse.run(writing);
						
			/*** Semantic Parsing Module ***/
			/** Step1: Term Extraction **/
			originalSent.connectNouns();
			originalSent.connectVerbs();
			/* 名詞と形容詞だけ取り出す */
			//System.out.println("\n\t Step1");
			String[][] tagNP = {{"形容詞", "-連用テ接続"}, {"連体詞"}, {"助詞", "連体化"}, {"助動詞", "体言接続"}}; //これらを含むChunkを係り受け先につなげる
			List<Integer> chunkList_NP = originalSent.collectTagChunks(tagNP); // 上記のtagを持つWordを集めた
			
			/** Step2: Concatenation **/
			/* 名詞と名詞または形容詞と名詞をつなげて1つの名詞句にする */
			//System.out.println("\n\t Step2");
			originalSent.connectModifer(chunkList_NP);
			
			/** Step3: Break Phrases **/
			/* 長文を分割し複数の短文に分ける */
			//System.out.println("\n\t Step3");
			sentList.addAll(originalSent.separate2());
		}
		
		System.out.println("--------sentences---------");
		for(final Sentence partSent: sentList) {
			System.out.println(partSent.toString());
		}
		System.out.println("--------sentences---------\n");
		
		
		for(final Sentence partSent: sentList) {
			/** Step4: Relations Extraction **/
			/* 単語間の関係を見つけ，グラフにする(各単語及び関係性をNodeのインスタンスとする) */
			//System.out.println("\n\t Step4");
			List<List<String>> relation = partSent.extractRelation(); 
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
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmm");
		File fileRln = new File("csvs/relation"+sdf.format(c.getTime())+".csv");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileRln));
					
			for(final List<String> relation: relations) {
	        	System.out.println(relation);
	        	List<Integer> triple_id = new ArrayList<Integer>(3);
	        	for (int i=0; i < 3; i++) {
	        		String concept = relation.get(i);
	
	        		bw.write(concept+",");
	
	        		if( !uri.contains(concept) ) uri.add(concept);
	        		triple_id.add(uri.indexOf(concept));
	        	}
        	
	        	bw.newLine();
	        	triples.add(triple_id);
			}
		
		bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
		//List<Node> nodes = Node.setTriples2Nodes(uri, triples);
		/*
		for(int i=0; i<uri.size(); i++) {
			System.out.println(i + ":\t" + uri.get(i));
			nodes.get(i).printNode1();
			nodes.get(i).printNode2();
		}
		*/
	// ここまでMainに詰め込みすぎ．何らかのクラスのメソッドにしょう
		
		/*** OWL DL Axiom Module ***/
		
		
		OntologyBuilder ob = new OntologyBuilder();		
		ob.writeOntology(uri, triples);
		ob.output("owls/ontology"+sdf.format(c.getTime())+".owl");	// 渡すのは保存先のパス
		
	}

}
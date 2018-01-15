package modules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import data.original.Ontology;
import data.original.RDFTriple;
import grammar.NaturalLanguage;
import grammar.Paragraph;
import grammar.Sentence;
import modules.relationExtract.OntologyWriter;
import modules.syntacticParse.Cabocha;
import modules.syntacticParse.StringListUtil;

public class Generator {
	public Generator() {
	}

	public Ontology generate(Path textFile) {
		return generate(loadTextFile(textFile));
	}
	
	/**
	 * オントロジー構築器の実行
	 * @param naturalLanguageParagraphs 自然言語文の段落のリスト
	 */
	public Ontology generate(List<List<NaturalLanguage>> naturalLanguageParagraphs) {
		/***************************************/
		/**          構文解析モジュール          **/
		/***************************************/
		List<Paragraph> originalParagraphs = syntacticParse(naturalLanguageParagraphs);
		
		
		/***************************************/
		/**          文章整形モジュール          **/
		/***************************************/
		List<Sentence> editedSentences = new ArrayList<>();

		// 段落を処理に使う予定はまだないので，文のリストに均す
		List<Sentence> originalSentences = originalParagraphs.stream()
				.map(par -> par.getSentences())
				.flatMap(sents -> sents.stream())
				.collect(Collectors.toList());
		for(Sentence originalSentence : originalSentences) {
			System.out.println("\n\t Step0");
			
			/*** 文章整形Module ***/
			/** Step1: 単語結合 **/
			String[][] tagNouns = {{"接頭詞"}, {"名詞"}, {"接尾"}, {"形容詞"}};
			String[][] tagDo = {{"名詞"}, {"動詞", "する"}};
			String[][] tagDone = {{"動詞"}, {"動詞", "接尾"}};
			originalSentence.connect(tagNouns);
			originalSentence.connect(tagDo);
			originalSentence.connect(tagDone);
			/* 名詞と形容詞だけ取り出す */
			// これらがClauseの末尾につくものを隣のClauseにつなげる
			String[][] tags_NP = {{"形容詞", "-連用テ接続"}, {"連体詞"}, {"助詞", "連体化"}, {"助動詞", "体言接続"}, {"名詞"}};
			originalSentence.connect2Next(tags_NP, false);

			/** Step2: 長文分割 **/
			/* 長文を分割し複数の短文に分ける */
			originalSentence.printDep();
			for(final Sentence shortSent: originalSentence.divide2()) {
				//shortSent.printDep();
				for(final Sentence partSent: shortSent.divide3()) {
					partSent.uniteSubject();
					//partSent.printDep();
					editedSentences.add(partSent);
				}
			}
		}

		System.out.println("------------関係抽出モジュール------------");

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmm");
		List<String> textList = editedSentences.stream().map(s -> s.toString()).collect(Collectors.toList());
		Path textFile = Paths.get("texts/text"+sdf.format(c.getTime())+".txt");	// ついでに分割後のテキストを保存
		try {
			Files.write(textFile, textList, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		/***************************************/
		/**          関係抽出モジュール          **/
		/***************************************/
		List<RDFTriple> triples = new ArrayList<>();
		List<Model> graphs4JASS = new ArrayList<>();
		
		for(final Sentence partSent: editedSentences) {
			graphs4JASS.add(partSent.toJASS());
		}
		
		for(final Sentence partSent: editedSentences) {
			triples.addAll(partSent.extractRelation());
		}
		
		/* 文構造のRDF化 */
		/*
		String personURI    = "http://somewhere/JohnSmith";
		String fullName     = "John Smith";
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();
		// create the resource
		Resource johnSmith = model.createResource(personURI);
		// add the property
		johnSmith.addProperty(VCARD.FN, fullName);

		String strQuery = "" +
				"PREFIX rdf:  " +
				"PREFIX dc:  " +
				"SELECT ?rc ?title " +
				"WHERE {" +
				"?rc dc:title ?title ." +
				"}";
		Query query = QueryFactory.create(strQuery);
		QueryExecution qexec = QueryExecutionFactory.create(query,model);
		ResultSet result = qexec.execSelect();
		
		while(result.hasNext()) {
			QuerySolution qsol = result.next();
			Resource rc = (Resource) qsol.get("rc");
			Literal title = (Literal) qsol.get("title");
			System.out.println(rc);
			System.out.println(title);
		}

		model.close();
		 */
		
		//重複除去
		triples = new ArrayList<RDFTriple>(new LinkedHashSet<RDFTriple>(triples));

		File fileCSV = new File("csvs/relation"+sdf.format(c.getTime())+".csv");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileCSV));

			for(final RDFTriple triple: triples) {
				bw.write(triple.toString());
		        	bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		OntologyWriter ob = new OntologyWriter(OntologyWriter.N_TRIPLES, triples);
		ob.output("owls/ontology"+sdf.format(c.getTime()));	// 渡すのは保存先のパス(拡張子は含まない)
		
		System.out.println("Finished.");
		System.out.println("Sentences: " + naturalLanguageParagraphs.size() + "\t->dividedSentences: " + editedSentences.size());
		System.out.println("Relations: " + triples.size());
		
		return new Ontology(triples);
	}

	/**
	 * テキストファイル読み込み. テキストは1行一文. 空行を段落の境界とみなす.
	 * @param textFile テキストファイルのパス
	 * @return 段落のリスト(段落はNaturalLanguageのリストからなる)
	 */
	private List<List<NaturalLanguage>> loadTextFile(Path textFile) {
		List<String> texts;
		try {
			texts = Files.readAllLines(textFile);
		} catch (IOException e) {
			e.printStackTrace();
			texts = new ArrayList<>(Arrays.asList("ジェネレータはファイルからテキストを読み込めませんでした。"));
		}
		return StringListUtil.split("", texts).stream()
				.map(textList -> textList.stream()
						.map(text -> new NaturalLanguage(text))
						.collect(Collectors.toList()))
				.collect(Collectors.toList());
	}
	
	/**
	 * 自然言語文のリストのリストを構文解析し，段落のリストを返す.
	 * @param naturalLanguageParagraphs 自然言語文を段落ごとにリストしたものをまとめたリスト
	 * @return 段落のリスト
	 */
	private List<Paragraph> syntacticParse(List<List<NaturalLanguage>> naturalLanguageParagraphs) {
		Cabocha cabocha = new Cabocha();
		return naturalLanguageParagraphs.stream()
				.map(nlTexts -> cabocha.texts2sentences(nlTexts))
				.map(sentenceList -> new Paragraph(sentenceList))
				.collect(Collectors.toList());
	}
}
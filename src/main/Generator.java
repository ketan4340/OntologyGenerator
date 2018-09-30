package main;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import cabocha.Cabocha;
import data.RDF.Ontology;
import data.id.ModelIDMap;
import data.id.SentenceIDMap;
import data.id.StatementIDMap;
import grammar.naturalLanguage.NaturalLanguage;
import grammar.naturalLanguage.NaturalParagraph;
import grammar.sentence.Sentence;
import modules.OutputManager;
import modules.RDFConvert.RelationExtractor;
import modules.syntacticParse.SyntacticParser;
import modules.textRevision.SentenceReviser;
import util.StringListUtil;

public class Generator {
	private static final Path PATH_EXTENSION_RULE;// = Paths.get("resource/rule/extensionRules.txt");
	private static final Path PATH_ONTOLOGY_RULES;// = Paths.get("resource/rule/ontology-rules");
	private static final Path PATH_DEFAULT_JASS;// = "resource/ontology/SyntaxOntology.owl";
	//private static final Path PATH_NOUN_WORDS;
	private static final Path PATH_CABOCHA_PROP;
	
	/* ログ用 */
	private static final String RUNTIME = new SimpleDateFormat("MMdd-HHmm").format(Calendar.getInstance().getTime());
	private static final Path PATH_DIVIDED_SENTENCES;// = Paths.get("tmp/log/text/dividedText"+RUNTIME+".txt");
	private static final Path PATH_CONVERTEDJASS_TURTLE;// = Paths.get("tmp/log/jass/jass"+RUNTIME+RDFSerialize.Turtle.getExtension());
	private static final Path PATH_USEDRULES;// = Paths.get("tmp/log/rule/rule"+RUNTIME+".rule");
	private static final Path PATH_ONTOLOGY_TURTLE;// = Paths.get("dest/rdf/turtle/ontology"+RUNTIME+RDFSerialize.Turtle.getExtension());
	private static final Path PATH_ID_TRIPLE_CSV;// = Paths.get("dest/csv/RDFtriple"+RUNTIME+".csv");

	static {
		Properties prop = new Properties();
		try (InputStream is = Cabocha.class.getClassLoader().getResourceAsStream("conf/property.xml")) {
			prop.loadFromXML(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PATH_EXTENSION_RULE = Paths.get(prop.getProperty("extension_rule-file"));
		PATH_ONTOLOGY_RULES = Paths.get(prop.getProperty("ontology_rules-dir"));
		PATH_DEFAULT_JASS = Paths.get(prop.getProperty("default-JASS-file"));
		//PATH_NOUN_WORDS = Paths.get(prop.getProperty("noun-file"));
		PATH_CABOCHA_PROP = Paths.get(prop.getProperty("cabocha-prop"));
		
		PATH_DIVIDED_SENTENCES = Paths.get(prop.getProperty("output-shortsentence")+RUNTIME+".txt");
		PATH_CONVERTEDJASS_TURTLE = Paths.get(prop.getProperty("output-convertedJASS-turtle")+RUNTIME+".ttl");
		PATH_USEDRULES = Paths.get(prop.getProperty("output-usedrules")+RUNTIME+".rule");
		PATH_ONTOLOGY_TURTLE = Paths.get(prop.getProperty("output-ontology-turtle")+RUNTIME+".ttl");
		PATH_ID_TRIPLE_CSV = Paths.get(prop.getProperty("output-id_triple")+RUNTIME+".csv");
	}
	
	/****************************************/
	/**********    Main  Method    **********/
	/****************************************/
	public static void main(String[] args) {
		new Generator().execute(args.length == 1? args[0] : null);
	}

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Generator() {}


	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	/**
	 * ジェネレータの実行.
	 * ぶっちゃけテスト用に色々書くために仲介させているだけ.
	 */
	private void execute(String textFileString) {
		//textFileString = "resource/input/goo/text/gooText生物-動物名-All.txt";
		//textFileString = "resource/input/test/attribute.txt";

		if (Objects.nonNull(textFileString)) {
			Path textFilePath = Paths.get(textFileString);
			generate(textFilePath);
		}
	}

	/**
	 * ジェネレータ本体.
	 * @param textFilePath 入力するテキストファイルのパス
	 */
	public Ontology generate(Path textFilePath) {
		return generateParagraphs(loadTextFile(textFilePath));
	}

	public Ontology generateParagraphs(List<NaturalParagraph> naturalLanguageParagraphs) {
		// 段落を処理に使う予定はまだないので，文のリストに均して，
		// List<Sentence>を引数として受け取る#generateに渡す
		List<NaturalLanguage> naturalLanguages = naturalLanguageParagraphs.stream()
				.map(NaturalParagraph::getTexts)
				.flatMap(List<NaturalLanguage>::stream)
				.collect(Collectors.toList());
		return generate(naturalLanguages);
	}
	/**
	 * オントロジー構築器の実行
	 */
	public Ontology generate(List<NaturalLanguage> naturalLanguages) {
		System.out.println("Start.");
		/*************************************/
		/********** 構文解析モジュール  **********/
		/*************************************/
		List<Sentence> sentenceList = new SyntacticParser(PATH_CABOCHA_PROP).parseSentences(naturalLanguages);
		SentenceIDMap sentenceMap = SentenceIDMap.createFromList(sentenceList);
		sentenceMap.setLongSentence();
		
		/*************************************/
		/********** 文章整形モジュール **********/
		/*************************************/
		SentenceReviser sr = new SentenceReviser();
		/** Step1: 単語結合 **/
		sr.connectWord(sentenceMap);
		/** Step2: 長文分割 **/
		/* 長文を分割し複数の短文に分ける */
		sr.divideEachSentence(sentenceMap);
		sentenceMap.setShortSentence();

		//sentenceMap.forEachKey(System.out::println);	//PRINT
		/*************************************/
		/********** 関係抽出モジュール **********/
		/*************************************/
		RelationExtractor re = new RelationExtractor(PATH_EXTENSION_RULE, PATH_ONTOLOGY_RULES, PATH_DEFAULT_JASS);
		ModelIDMap JASSMap = re.convertMap_Sentence2JASSModel(sentenceMap);
		ModelIDMap modelMap = re.convertMap_JASSModel2RDFModel(JASSMap);
		StatementIDMap statementMap = re.convertMap_Model2Statements(modelMap);

		Model unionModel = modelMap.uniteModels().difference(re.defaultJASSModel);
		Ontology ontology = new Ontology(re.convertModel_Jena2TripleList(unionModel));
		

		OutputManager opm = new OutputManager();
		// ログや生成物の出力
		opm.outputDividedSentences(sentenceMap, PATH_DIVIDED_SENTENCES);
		// デフォルトJASSモデルは取り除いて出力
		opm.outputOntologyAsTurtle(
				JASSMap.uniteModels().difference(re.defaultJASSModel).setNsPrefixes(re.defaultJASSModel.getNsPrefixMap()), 
				PATH_CONVERTEDJASS_TURTLE);
		opm.outputRDFRulesSet(re.getExtensionRules(), re.getOntologyRules(), PATH_USEDRULES);

		opm.outputIDAsCSV(statementMap.createIDRelation(), PATH_ID_TRIPLE_CSV);
		opm.outputOntologyAsTurtle(unionModel, PATH_ONTOLOGY_TURTLE);

		System.out.println("Finished.");
		System.out.println("Sentences: " + naturalLanguages.size() + "\t->dividedSentences: " + sentenceMap.size());
		System.out.println("ontology size: " + ontology.getTriples().size() + "\n");

		return ontology;
	}


	/**
	 * テキストファイル読み込み. テキストは1行一文. 空行を段落の境界とみなす.
	 * @param textFile テキストファイルのパス
	 * @return 段落のリスト(段落はNaturalLanguageのリストからなる)
	 */
	private List<NaturalParagraph> loadTextFile(Path textFile) {
		List<String> texts;
		try {
			texts = Files.readAllLines(textFile);
		} catch (IOException e) {
			e.printStackTrace();
			texts = new ArrayList<>(Arrays.asList("ジェネレータはファイルからテキストを読み込めませんでした。"));
		}
		// 空行を見つけたら段落の境界とする
		return StringListUtil.split("", texts).stream()
				.map(NaturalLanguage::toNaturalLanguageList)
				.map(NaturalParagraph::new)
				.collect(Collectors.toList());
	}

}
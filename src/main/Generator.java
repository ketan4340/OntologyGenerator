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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import cabocha.Cabocha;
import data.RDF.rule.RDFRuleReader;
import data.RDF.rule.RDFRules;
import data.RDF.rule.RDFRulesSet;
import data.id.ModelIDMap;
import data.id.SentenceIDMap;
import grammar.naturalLanguage.NaturalLanguage;
import grammar.naturalLanguage.NaturalParagraph;
import grammar.sentence.Sentence;
import modules.OutputManager;
import modules.RDFConvert.EntityLinker;
import modules.RDFConvert.RelationExtractor;
import modules.syntacticParse.SyntacticParser;
import modules.textRevision.SentenceReviser;
import util.StringListUtil;

public class Generator {
	//private static final Path PATH_NOUN_WORDS;
	private static final Path PATH_DATE_WORDS;
	private static final Path PATH_ADJECTIVAL_REGEXES;
	private static final RDFRules EXTENSION_RULES;
	private static final RDFRulesSet ONTOLOGY_RULES_SET;
	private static final Model DEFAULT_JASS;
	private static final boolean USE_ENTITY_LINKING;
	private static final List<String> URL_SPARQL_ENDPOINTS;
	private static final int MAX_SIZE_OF_INSTATEMENT;
	private static final Path PATH_CABOCHA_PROP;
	
	/* ログ用 */
	private static final String RUNTIME = new SimpleDateFormat("MMdd-HHmm").format(Calendar.getInstance().getTime());
	private static final Path PATH_DIVIDED_SENTENCES;
	private static final Path PATH_CONVERTEDJASS_TURTLE;
	private static final Path PATH_USEDRULES;
	private static final Path PATH_ID_TRIPLE_CSV;
	private static final Path PATH_ONTOLOGY_TURTLE;
	private static final Path PATH_ONTOLOGY_TURTLE_EL;
	
	static {
		Properties prop = new Properties();
		try (InputStream is = Cabocha.class.getClassLoader().getResourceAsStream("conf/property.xml")) {
			prop.loadFromXML(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//PATH_NOUN_WORDS = Paths.get(prop.getProperty("noun-file"));
		PATH_DATE_WORDS = Paths.get(prop.getProperty("date_words-file"));
		PATH_ADJECTIVAL_REGEXES = Paths.get(prop.getProperty("adjectival_regexes-file"));
		EXTENSION_RULES = RDFRuleReader.readRDFRules(Paths.get(prop.getProperty("extension_rule-file")));
		ONTOLOGY_RULES_SET = RDFRuleReader.readRDFRulesSet(Paths.get(prop.getProperty("ontology_rules-dir")));
		DEFAULT_JASS = ModelFactory.createDefaultModel().read(
				Paths.get(prop.getProperty("default-JASS-file")).toUri().normalize().getPath() );
		USE_ENTITY_LINKING = Boolean.valueOf(prop.getProperty("use-entity_linking"));
		URL_SPARQL_ENDPOINTS = Pattern.compile(",").splitAsStream(prop.getProperty("sparql-endpoint"))
				.map(String::trim).collect(Collectors.toList());
		MAX_SIZE_OF_INSTATEMENT = Integer.valueOf(prop.getProperty("max-size-of-INstatement"));
		PATH_CABOCHA_PROP = Paths.get(prop.getProperty("cabocha-prop"));
		
		PATH_DIVIDED_SENTENCES = Paths.get(prop.getProperty("output-shortsentence")+".txt");
		PATH_CONVERTEDJASS_TURTLE = Paths.get(prop.getProperty("output-convertedJASS-turtle")+RUNTIME+".ttl");
		PATH_USEDRULES = Paths.get(prop.getProperty("output-usedrules")+RUNTIME+".rule");
		PATH_ID_TRIPLE_CSV = Paths.get(prop.getProperty("output-id_triple")+RUNTIME+".csv");
		PATH_ONTOLOGY_TURTLE = Paths.get(prop.getProperty("output-ontology-turtle")+RUNTIME+".ttl");
		PATH_ONTOLOGY_TURTLE_EL = Paths.get(prop.getProperty("output-ontology-turtle")+RUNTIME+"el.ttl");
	}
	

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public Generator() {}

	
	/* ================================================== */
	/* =================== Main Method ================== */
	/* ================================================== */
	public static void main(String[] args) {
		Generator g = new Generator();
		if (args.length == 0)
			g.execute(null);
		else if (args.length == 1)
			g.execute(args[0]);
		else
			System.err.println("The amount of arguments is not 1.");
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	/**
	 * ジェネレータの実行.
	 * ぶっちゃけテスト用に色々書くために仲介させているだけ.
	 */
	private void execute(String textFile_str) {
		//textFile_str = "resource/input/goo/text/gooText生物-動物名-All.txt";
		//textFile_str = "resource/input/test/single.txt";
		//textFile_str = "resource/input/test/failed.txt";
		//textFile_str = "resource/input/test/hashire_merosu_c.txt";
		
		if (Objects.nonNull(textFile_str)) {
			Path textFilePath = Paths.get(textFile_str);
			Model ontology = generate(textFilePath);
			/* デバッグ用
			if (ontology.size() < 100)
				ontology.write(System.out, "TURTLE");
			//*/
		}	
	}

	/**
	 * ジェネレータ本体.
	 * @param textFilePath 入力するテキストファイルのパス
	 */
	public Model generate(Path textFilePath) {
		return generateParagraphs(loadTextFile(textFilePath));
	}

	public Model generateParagraphs(List<NaturalParagraph> naturalLanguageParagraphs) {
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
	public Model generate(List<NaturalLanguage> naturalLanguages) {
		System.out.println("Start.");

		/********** 構文解析モジュール **********/
		SyntacticParser sp = new SyntacticParser(PATH_CABOCHA_PROP);
		List<Sentence> sentenceList = sp.parseSentences(naturalLanguages);
		sp.supplyDatesNETag(sentenceList, PATH_DATE_WORDS);
		sp.supplyAdjectivalNETag(sentenceList, PATH_ADJECTIVAL_REGEXES);
		SentenceIDMap sentenceMap = SentenceIDMap.createFromList(sentenceList);
		sentenceMap.setLongSentence();
		System.out.println("Syntactic parsed.");
		//sentenceMap.forEachKey(s -> s.printDep());	//PRINT
		
		/********** 文章整形モジュール **********/
		SentenceReviser sr = new SentenceReviser();
		/** Step1: 単語結合 **/
		sr.connectWord(sentenceMap);
		/** Step2: 長文分割 **/
		/* 長文を分割し複数の短文に分ける */
		sr.divideEachSentence(sentenceMap);
		// 主語結合
		sentenceMap.forEachKey(s -> s.uniteSubject());
		sentenceMap.setShortSentence();
		System.out.println("Sentence revised.");
		//sentenceMap.forEachKey(s -> s.printDep());	//PRINT
		
		/********** 関係抽出モジュール **********/
		RelationExtractor re = new RelationExtractor(EXTENSION_RULES, ONTOLOGY_RULES_SET, DEFAULT_JASS);
		ModelIDMap jassMap = re.convertMap_Sentence2JASSModel(sentenceMap);
		ModelIDMap ontologyMap = re.convertMap_JASSModel2RDFModel(jassMap);
		System.out.println("Relation extracted.");
		
		// 全てのModelIDMapを統合し１つのModelに
		Model unionOntology = ontologyMap.uniteModels();

		// ログや生成物の出力
		OutputManager opm = OutputManager.getInstance();
		opm.writeSentences(sentenceMap, PATH_DIVIDED_SENTENCES);
		// デフォルトJASSモデルは取り除いて出力
		opm.writeJassModel(
				re.removeJASSOntology(jassMap.uniteModels()).setNsPrefixes(DEFAULT_JASS.getNsPrefixMap()), 
				PATH_CONVERTEDJASS_TURTLE);
		opm.writeRDFRulesSet(EXTENSION_RULES, ONTOLOGY_RULES_SET, PATH_USEDRULES);
		opm.writeIDTupleAsCSV(ontologyMap.createIDRelation(), PATH_ID_TRIPLE_CSV);
		opm.writeOntologyAsTurtle(unionOntology, PATH_ONTOLOGY_TURTLE);

		System.out.println("Finished.");
		System.out.println("input sentences: " + naturalLanguages.size());
		System.out.println("-> divided sentences: " + sentenceMap.size());
		System.out.println("ontology size: " + unionOntology.size());
		
		if (USE_ENTITY_LINKING) {	// DBpediaとのエンティティリンキング
			EntityLinker el = new EntityLinker(URL_SPARQL_ENDPOINTS, MAX_SIZE_OF_INSTATEMENT);
			el.executeBySameLabelIdentification(jassMap.uniteModels(), unionOntology);
			System.out.println("Entity linked.");
			System.out.println("ontology size: " + unionOntology.size());
			opm.writeOntologyAsTurtle(unionOntology, PATH_ONTOLOGY_TURTLE_EL);
		}
		
		return unionOntology;
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

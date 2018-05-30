package modules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import data.RDF.Ontology;
import data.id.ModelIDMap;
import data.id.SentenceIDMap;
import grammar.NaturalLanguage;
import grammar.NaturalParagraph;
import grammar.Sentence;
import modules.relationExtract.RelationExtractor;
import modules.syntacticParse.SyntacticParser;
import modules.textRevision.SentenceReviser;
import util.StringListUtil;

public class Generator {

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public Generator() {}

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public Ontology generate(Path textFile) {
		return generateParagraphs(loadTextFile(textFile));
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
	 * @param naturalLanguageParagraphs 自然言語文の段落のリスト
	 */
	public Ontology generate(List<NaturalLanguage> naturalLanguages) {
		System.out.println("Start.");
		/*************************************/
		/********** 構文解析モジュール **********/
		/*************************************/
		SyntacticParser sp = new SyntacticParser();
		List<Sentence> sentenceList = sp.parseSentences(naturalLanguages);
		SentenceIDMap sentenceMap = SentenceIDMap.createFromList(sentenceList);
		sentenceMap.setLongSentenceID();
		
		
		/*************************************/
		/********** 文章整形モジュール **********/
		/*************************************/
		SentenceReviser sr = new SentenceReviser();
		/** Step1: 単語結合 **/
		sr.connectWord(sentenceMap);
		/** Step2: 長文分割 **/
		/* 長文を分割し複数の短文に分ける */
		sr.divideEachSentence(sentenceMap);
		sentenceMap.setShortSentenceID();
		
		/*************************************/
		/********** 関係抽出モジュール **********/
		/*************************************/
		RelationExtractor re = new RelationExtractor();
		ModelIDMap JASSMap = re.convertMap_Sentence2JASSModel(sentenceMap);
		ModelIDMap ontologyMap = re.convertMap_JASSModel2RDFModel(JASSMap);
		ontologyMap.setRuleID();
		Model unionModel = ontologyMap.uniteModels();

		Ontology ontology = new Ontology(re.convertModel_Jena2TripleList(unionModel));
		
		// ログや生成物の出力
		OutputManager opm = new OutputManager();
		opm.outputCSV2(ontologyMap);
		opm.outputOntology(unionModel);
		opm.outputDividedSentences(sentenceMap);
				
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
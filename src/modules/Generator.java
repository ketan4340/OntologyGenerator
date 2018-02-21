package modules;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import data.RDF.MyResource;
import data.RDF.Namespace;
import data.RDF.Ontology;
import data.RDF.RDFTriple;
import grammar.NaturalLanguage;
import grammar.Paragraph;
import grammar.Sentence;
import modules.relationExtract.JASSFactory;
import modules.relationExtract.RDFRuleReader;
import modules.relationExtract.RDFRules;
import modules.syntacticParse.Cabocha;
import modules.textRevision.SentenceReviser;
import util.StringListUtil;

public class Generator {
	public Generator() {
	}



	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public Ontology generate(Path textFile) {
		return generate(loadTextFile(textFile));
	}

	/**
	 * オントロジー構築器の実行
	 * @param naturalLanguageParagraphs 自然言語文の段落のリスト
	 */
	public Ontology generate(List<List<NaturalLanguage>> naturalLanguageParagraphs) {
		/*************************************/
		/********** 構文解析モジュール **********/
		/*************************************/
		List<Paragraph> originalParagraphs = syntacticParse(naturalLanguageParagraphs);

		/*************************************/
		/********** 文章整形モジュール **********/
		/*************************************/
		// 段落を処理に使う予定はまだないので，文のリストに均す
		List<Sentence> originalSentences = originalParagraphs.stream()
				.flatMap(p -> p.getSentences().stream())
				.collect(Collectors.toList());

		/*** 文章整形Module ***/
		List<Sentence> editedSentences = new LinkedList<>();
		SentenceReviser sr = new SentenceReviser();
		/** Step1: 単語結合 **/
		originalSentences.forEach(sr::connectWord);
		/** Step2: 長文分割 **/
		/* 長文を分割し複数の短文に分ける */
		for (Sentence originalSentence : originalSentences) {
			// originalSentence.printDep();	//TODO
			for (final Sentence shortSent: originalSentence.divide2()) {
				//shortSent.printDep();	//TODO
				for (final Sentence partSent: shortSent.divide3()) {
					partSent.uniteSubject();
					//partSent.printDep();	//TODO
					editedSentences.add(partSent);
				}
			}
		}

		/*************************************/
		/********** 関係抽出モジュール **********/
		/*************************************/
		System.out.println("------------関係抽出モジュール------------");

		/* 文構造のRDF化 */
		Model ontologyModel = ModelFactory.createDefaultModel();
		// RDFルール生成 (読み込み)
		RDFRules extensionRules = RDFRuleReader.read(Paths.get("resource/rule/extensionRules.txt"));
		RDFRules ontologyRules = RDFRuleReader.read(Paths.get("resource/rule/ontologyRules.txt"));
	
		editedSentences.stream()
			.map(JASSFactory::createJASSModel)
			.map(extensionRules::extend)
			.map(ontologyRules::convert)
			.forEach(ontologyModel::add);

		// ログの出力
		List<String> textList = editedSentences.stream().map(s -> s.name()).collect(Collectors.toList());
		Path textFile = Paths.get("tmp/log/text/dividedText.txt");	// 分割後のテキストを保存
		try {
			Files.write(textFile, textList, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try (final OutputStream os = Files.newOutputStream(Paths.get("./tmp/log/JenaModel/ontologyModel.nt"))) {
			ontologyModel.write(os, "N-TRIPLE");
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		Ontology ontology = new Ontology(convertJena2Original(ontologyModel));

		ontologyModel.close();
		
		List<String> csvList = ontology.getTriples().stream().map(tri -> tri.toString()).collect(Collectors.toList());
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmm");
		Path csvFile = Paths.get("dest/csv/relation"+sdf.format(Calendar.getInstance().getTime())+".csv");
		try {
			Files.write(csvFile, csvList, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Finished.");
		System.out.println("Sentences: " + naturalLanguageParagraphs.get(0).size() + "\t->dividedSentences: " + editedSentences.size());
		System.out.println("Relations: " + ontology.getTriples().size() + "\n");

		return ontology;
	}

	/**
	 * JenaのModelを独自クラスRDFTripleのリストに置き換える.
	 * @param model JenaのModel
	 * @return RDFTripleのリスト
	 */
	private List<RDFTriple> convertJena2Original(Model model) {	
		List<RDFTriple> triples = new LinkedList<>();
		StmtIterator stmtIter = model.listStatements();
		while (stmtIter.hasNext()) {
			Statement stmt = stmtIter.nextStatement(); // get next statement
			Resource subject = stmt.getSubject(); // get the subject
			Property predicate = stmt.getPredicate(); // get the predicate
			RDFNode object = stmt.getObject(); // get the object

			RDFTriple triple = new RDFTriple(
					new MyResource(subject),
					new MyResource(predicate),
					new MyResource(object));
			triples.add(triple);
		}
		return triples;
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
		// 空行を見つけたら段落の境界とする
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
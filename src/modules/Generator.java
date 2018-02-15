package modules;

import java.io.IOException;
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

import data.original.MyResource;
import data.original.Namespace;
import data.original.Ontology;
import data.original.RDFTriple;
import grammar.NaturalLanguage;
import grammar.Paragraph;
import grammar.Sentence;
import grammar.clause.AbstractClause;
import modules.relationExtract.JASSFactory;
import modules.relationExtract.RDFRuleReader;
import modules.relationExtract.RDFRules;
import modules.syntacticParse.Cabocha;
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
		/***************************************/
		/**          構文解析モジュール          **/
		/***************************************/
		List<Paragraph> originalParagraphs = syntacticParse(naturalLanguageParagraphs);

		/***************************************/
		/**          文章整形モジュール          **/
		/***************************************/
		// 段落を処理に使う予定はまだないので，文のリストに均す
		List<Sentence> originalSentences = originalParagraphs.stream()
				.flatMap(p -> p.getSentences().stream())
				.collect(Collectors.toList());

		List<Sentence> editedSentences = new LinkedList<>();
		for (Sentence originalSentence : originalSentences) {
			/*** 文章整形Module ***/
			/** Step1: 単語結合 **/
			String[][] tagNouns = {{"接頭詞"}, {"名詞"}, {"接尾"}};
			String[][] tagDo = {{"名詞"}, {"動詞", "する"}};
			String[][] tagDone = {{"動詞"}, {"動詞", "接尾"}};
			originalSentence.getChildren().forEach(c -> c.uniteAdjunct2Categorem(tagNouns[0], tagNouns[1]));
			originalSentence.getChildren().forEach(c -> c.uniteAdjunct2Categorem(tagDo[0], tagDo[1]));
			originalSentence.getChildren().forEach(c -> c.uniteAdjunct2Categorem(tagDone[0], tagDone[1]));
			/* 名詞と形容詞だけ取り出す */
			// これらがClauseの末尾につくものを隣のClauseにつなげる
			String[][] tags_NP = {{"形容詞", "-連用テ接続"}, {"連体詞"}, {"助詞", "連体化"}, {"助動詞", "体言接続"}, {"名詞"}};
			List<AbstractClause<?>> clauses_NP = originalSentence.collectClauseHasSome(tags_NP);
			clauses_NP.forEach(c -> originalSentence.connect2Next(c));

			/** Step2: 長文分割 **/
			/* 長文を分割し複数の短文に分ける */
			// originalSentence.printDep();	//TODO
			for(final Sentence shortSent: originalSentence.divide2()) {
				//shortSent.printDep();	//TODO
				for(final Sentence partSent: shortSent.divide3()) {
					partSent.uniteSubject();
					partSent.printDep();	//TODO
					editedSentences.add(partSent);
				}
			}
		}

		System.out.println("------------関係抽出モジュール------------");

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmm");

		List<String> textList = editedSentences.stream().map(s -> s.name()).collect(Collectors.toList());
		Path textFile = Paths.get("text/text"+sdf.format(c.getTime())+".txt");	// 分割後のテキストを保存
		try {
			Files.write(textFile, textList, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		/***************************************/
		/**          関係抽出モジュール          **/
		/***************************************/
		/*
		List<RDFTriple> triples = editedSentences.stream()
				.flatMap(s -> s.extractRelation().stream())
				.collect(Collectors.toList());
		//重複除去
		//triples = new ArrayList<RDFTriple>(new LinkedHashSet<RDFTriple>(triples));
		 */

		/* 文構造のRDF化 */
		Model ontologyModel = ModelFactory.createDefaultModel();
		// RDFルール生成 (読み込み)
		RDFRules extensionRules = RDFRuleReader.read(Paths.get("rule/extensionRules.txt"));
		RDFRules ontologyRules = RDFRuleReader.read(Paths.get("rule/ontologyRules.txt"));
	
		editedSentences.stream()
			.map(JASSFactory::createJASSModel)
			.map(extensionRules::extend)
			.map(ontologyRules::convert)
			.forEach(ontologyModel::add);

		/*
		System.out.println("\n\n Resolved Model");
		ontologyModel.write(System.out, "N-TRIPLE"); // TODO
		 */
		List<RDFTriple> triples = convertJena2Original(ontologyModel);
		ontologyModel.close();
		
		List<String> csvList = triples.stream().map(tri -> tri.toString()).collect(Collectors.toList());
		Path csvFile = Paths.get("csv/relation"+sdf.format(c.getTime())+".csv");
		try {
			Files.write(csvFile, csvList, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//List<RDFTriple> triples = new ArrayList<>();
		System.out.println("Finished.");
		System.out.println("Sentences: " + naturalLanguageParagraphs.get(0).size() + "\t->dividedSentences: " + editedSentences.size());
		System.out.println("Relations: " + triples.size() + "\n");

		return new Ontology(triples);
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
					new MyResource(Namespace.specify(subject.getURI()), subject.getLocalName()),
					new MyResource(Namespace.specify(predicate.getURI()), predicate.getLocalName()),
					object instanceof Resource?
							new MyResource(Namespace.specify(((Resource)object).getURI()), ((Resource)object).getLocalName())
							: new MyResource(object.toString()));
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
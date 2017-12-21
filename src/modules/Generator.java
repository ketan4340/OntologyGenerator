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

import data.RDF.RDFTriple;
import grammar.NaturalLanguage;
import grammar.Sentence;
import modules.relationExtract.OntologyWriter;
import modules.syntacticParse.Cabocha;

public class Generator {
	public Generator() {
	}

	public List<RDFTriple> generate(Path textFile) {
		List<NaturalLanguage> naturalLanguageTexts;
		try {
			naturalLanguageTexts = Files.readAllLines(textFile).stream()
					.map(stringText -> new NaturalLanguage(stringText))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
			naturalLanguageTexts = Arrays.asList(new NaturalLanguage("ジェネレータはファイルからテキストを読み込めませんでした。"));
		}
		return generate(naturalLanguageTexts);
	}
	
	/**
	 * オントロジー構築器の実行
	 * @param naturalLanguageTexts 自然言語文のリスト
	 */
	public List<RDFTriple> generate(List<NaturalLanguage> naturalLanguageTexts) {
		/***************************************/
		/**          構文解析モジュール          **/
		/***************************************/
		List<Sentence> originalSentences = syntacticParse(naturalLanguageTexts);
		
		
		/***************************************/
		/**          文章整形モジュール          **/
		/***************************************/
		List<Sentence> editedSentences = new ArrayList<>();		
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
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		/*
		for(final Sentence partSent: editedSentences) {
			triples.addAll(partSent.extractRelation());
		}
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
		System.out.println("Sentences: " + naturalLanguageTexts.size() + "\t->dividedSentences: " + editedSentences.size());
		System.out.println("Relations: " + triples.size());
		
		return triples;
	}


	/** 構文解析 */
	private List<Sentence> syntacticParse(List<NaturalLanguage> naturalLanguageTexts) {
		Cabocha cabocha = new Cabocha();
		return cabocha.texts2sentences(naturalLanguageTexts);
	}

}
package data.text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import grammar.NaturalLanguage;
import grammar.Sentence;
import grammar.clause.Clause;
import modules.syntacticParse.Cabocha;

public class DictionaryEditor {
	/* 繰り返しつかうのでここでコンパイル */
	/** 鉤括弧で囲まれた用例を探す正規表現 */
	private static final Pattern ptnExm1 = Pattern.compile("「[^「」]+／[^「」]+」");
	/** 鉤括弧で囲まれた用例を探す正規表現 */
	private static final Pattern ptnExm2 = Pattern.compile("(?<![あ-ん])「[^「」]+」(?![あ-ん、。])");
	/** 語釈文頭の箇条書きの数字を探す正規表現 */
	private static final Pattern ptnNum = Pattern.compile("[１-９\\d{2}][ ㋐-㋾]");	
	/** 補説とそこから行末までを探す正規表現 */
	private static final Pattern ptnSplm = Pattern.compile("\\[補説\\].+");


	public DictionaryEditor() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	/**
	 * 辞書を扱いやすいよう主語を加えた文章にして出力
	 */
	public void dictionary2Text(Path dictionaryFile, Path outputFile) {
		Map<String, String> dictionary = readDictionaryFile(dictionaryFile);

		Cabocha cabocha = new Cabocha();

		List<String> texts = new ArrayList<>(dictionary.size());
		for (Map.Entry<String, String> entry : dictionary.entrySet()) {
			String headword = entry.getKey();
			String serialInterpretation = entry.getValue();
			serialInterpretation = cleanText1(serialInterpretation, headword); // 余計なかっこを消す
			Matcher mchExm1 = ptnExm1.matcher(serialInterpretation);
			serialInterpretation = mchExm1.replaceAll(""); // 「用例」を消す
			Matcher mchExm2 = ptnExm2.matcher(serialInterpretation);
			serialInterpretation = mchExm2.replaceAll(""); // 「用例」を消す
			Matcher mchNum = ptnNum.matcher(serialInterpretation);
			serialInterpretation = mchNum.replaceAll(""); // 語釈文頭の箇条書きの数字を消す
			Matcher mchSplm = ptnSplm.matcher(serialInterpretation);
			serialInterpretation = mchSplm.replaceAll(""); // 文末の補説を消す

			serialInterpretation = cleanText2(serialInterpretation); // 残しておいたスペースを消す
			String[] interpretations = serialInterpretation.split("。", 0);

			for (String interpretation : interpretations) {
				Sentence sentence = cabocha.text2sentence(new NaturalLanguage(interpretation));
				List<Clause<?>> subjects = sentence.subjectList(false);
				Clause<?> firstSubjects = (subjects.isEmpty())? null : subjects.get(0);
				int subjectIndex = sentence.indexOfChild(firstSubjects); 
				Clause<?> firstCommaClause = sentence.findFirstClauseEndWith(new String[][]{{"、"}}, false);
				int commaIndex = sentence.indexOfChild(firstCommaClause);
				String text = (subjectIndex != -1 &&
						(commaIndex == -1 || subjectIndex <= commaIndex))?
						headword + "の" + interpretation:
						headword + "は" + interpretation;
				texts.add(text);
			}
		}

		try {
			Files.write(outputFile, texts);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * DictionaryFileを読み込み，見出し語をKey，語釈をValueにもつMapを返す. 
	 * @param dictionaryFile 見出し語<TAB>語釈<RETURN>という規則で書かれたテキストファイル
	 * @return 見出し語と語釈のマップ
	 */
	private Map<String, String> readDictionaryFile(Path dictionaryFile) {
		Map<String, String> dictionary = new LinkedHashMap<>();
		try {
			Files.lines(dictionaryFile).forEach(line -> {
				String[] item = line.split("\t", 2);
				String entry = item[0];
				String serialInterpretation = dictionary.getOrDefault(entry, "") + item[1];
				dictionary.put(entry, serialInterpretation);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dictionary;
	}
	
	/**
	 * 日本語テキストから余計なかっこ,記号を除去し，見出し語の代入を行う
	 */
	private String cleanText1(String text, String entry) {
		text = text.replaceAll("\\(.*?\\)", "");		// 半角かっこ()除去
		//text = text.replaceAll("\\[.+?\\]", "");	// 半角かっこ[]除去
		text = text.replaceAll("（.*?）", "");		// 全角かっこ（）除去
		text = text.replaceAll("［.*?］", "");		// 全角かっこ［］除去
		text = text.replaceAll("〈.*?〉", "");		// 全角かっこ〈〉除去
		text = text.replaceAll("《.*?》", "");		// 全角かっこ《》除去
		text = text.replaceAll("―", entry);			// 例文の―を見出し語に置き換える
		text = text.replaceAll("→|⇒", "");			// 矢印除去
		text = text.replaceAll(" ", "");				// 謎の空白文字除去
		return text;
	}

	/**
	 * テキストからスペースを除去する
	 */
	private String cleanText2(String text) {
		text = text.replaceAll("\\[.*?\\]", "");		// 半角かっこ[]除去
		//text = text.replaceAll("[「」]", "");		// 全角鉤かっこ「」除去
		text = text.replaceAll("[\\s　]", "");		// 空白文字除去
		text = text.replaceAll("[㋐-㋾]+", "");		// 囲み文字(カタカナ)除去
		text = text.replaceAll("\\d(?=」)", "");		// カギカッコ内最後の数字除去
		return text;
	}
	
	

	/**
	 * ディレクトリ内のファイルの内容を全て纏めた一つのファイルを出力する
	 */
	public boolean gatheringTexts(Path dirPath, Path outputFile) {
		Stream<Path> filePathStream = null;
		try {
			filePathStream = Files.list(dirPath).sorted(Comparator.comparing(Path::toString));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (filePathStream == null || filePathStream.count() == 0) {
			System.err.println("There is no file.");
			return false;
		}
		
		filePathStream.forEach(filePath -> {
			if (!Files.isRegularFile(filePath)) return;
			if (filePath.getFileName().toString().equals(".gitignore") ||
				filePath.getFileName().toString().equals(".DS_Store") ||
				filePath.equals(outputFile)) return;
			try {
				List<String> lines = Files.lines(filePath).collect(Collectors.toList());
				Files.write(outputFile, lines, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		filePathStream.close();
		return true;
	}
}
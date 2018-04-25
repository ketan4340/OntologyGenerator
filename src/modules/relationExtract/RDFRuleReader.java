package modules.relationExtract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RDFRuleReader {
	/** 空白文字,半角スペース,全角スペースにマッチ. ただしバックスラッシュが直前に付くものは対象外. */
	private static final Pattern removeSpacePattern = Pattern.compile("(?<!\\\\)[\\s 　]");
	/** 直後に"THEN"や"ELSE"がない"}"と、直前に"."がある";"の位置にマッチ. 位置にマッチなので置換しても"}"も";"も残る. */
	private static final Pattern splitRulesPattern = 
			Pattern.compile("(?<=}(?!(THEN|ELSE)))|(?<=(?<=\\.);)", Pattern.CASE_INSENSITIVE);

	/** IF{...}THEN{...}の形式で書かれたルールにマッチ. */
	private static final Pattern wholeIF_THENPattern = Pattern.compile("\\AIF\\{.+?\\Q}THEN{\\E.+?\\}\\z", Pattern.CASE_INSENSITIVE);
	private static final Pattern middleTHENPattern = Pattern.compile("\\Q}THEN{\\E", Pattern.CASE_INSENSITIVE);
	private static final Pattern middleELSEPattern = Pattern.compile("\\Q}ELSE{\\E", Pattern.CASE_INSENSITIVE);
	/** ...->...;の形式で書かれたルールにマッチ. */
	private static final Pattern wholeArrowPattern = Pattern.compile("\\A.+->.+;\\z");
	private static final Pattern middleArrowPattern = Pattern.compile("->");
	private static final Pattern middleExclamationArrowPattern = Pattern.compile("!>");

	/** ","にマッチ. ただし"\,"は無視する. */
	private static final Pattern commaPattern = Pattern.compile("(?<!\\\\),");
	/** "."にマッチ. ただし"\."は無視する. */
	private static final Pattern periodPattern = Pattern.compile("(?<!\\\\)"+Pattern.quote("."));
	/** コメントアウトの"#"以降にマッチ. "#"から行末まで削除する. */
	private static final Pattern commentPattern = Pattern.compile("#.*\\z");

	/**
	 * RDFルールを記述したファイルを読み込む. コメントアウトとBOMを削除
	 * @param rulesFile
	 * @return
	 */
	public static RDFRules read(Path rulesFile) {
		String rulesString;
		try {
			rulesString = Files.lines(rulesFile)
					.map(commentPattern::matcher)	// コメントアウトを検知
					.map(m -> m.replaceAll(""))		// #から行末まで削除
					.collect(Collectors.joining());
		} catch (IOException e) {
			rulesString = new String();
			e.printStackTrace();
		}
		if (rulesString.startsWith("\uFEFF"))
			rulesString = rulesString.substring(1);	// BOM削除
		return createRules(rulesString);
	}

	/**
	 * 文字列から一連のRDFルールを生成.
	 * @param rulesString
	 * @return
	 */
	public static RDFRules createRules(String rulesString) {
		rulesString = removeSpacePattern.matcher(rulesString).replaceAll("");
		return new RDFRules(splitRulesPattern.splitAsStream(rulesString)
				.map(RDFRuleReader::createRule)
				.collect(Collectors.toList()));
	}

	/**
	 * 文字列から1つのRDFルールを生成.
	 * @param ruleString
	 * @return
	 */
	public static RDFRule createRule(String ruleString) {
		String[] triplePatterns =
				wholeIF_THENPattern.matcher(ruleString).matches()?
						triplePatternsOfIF_THEN(ruleString) :
				wholeArrowPattern.matcher(ruleString).matches()?
						triplePatternsOfArrow(ruleString) :
				new String[2];

		String[][] triplePattern_IF = split2values(triplePatterns[0]);
		String[][] triplePattern_THEN = split2values(triplePatterns[1]);
		return new RDFRule(triplePattern_IF, triplePattern_THEN);
	}

	private static String[] triplePatternsOfIF_THEN(String ruleString) {
		// 先頭に"IF{"、末尾に"}"が付いていることは確認済みなので，切り取ってから"}THEN{"で分割。
		String[] triplePatterns = middleTHENPattern.split(ruleString.substring(3, ruleString.length()-2));
		if (triplePatterns.length != 2) {
			System.err.println("Rule's format error by IF_THEN style. : "+ruleString);
			return new String[2];
		}
		return triplePatterns;
	}
	private static String[] triplePatternsOfArrow(String ruleString) {
		// 末尾に";"が付いていることは確認済みなので，切り取ってから"->"で分割。
		String[] triplePatterns = middleArrowPattern.split(ruleString.substring(0, ruleString.length()-2));
		if (triplePatterns.length != 2) {
			System.err.println("Rule's format error by Arrow style. : "+ruleString);
			return new String[2];
		}
		return triplePatterns;
	}

	private static String[][] split2values(String triples) {
		return Stream.of(triples)			// Stream<String> (1)
				.map(periodPattern::split)	// Stream<String[]> (1)
				.flatMap(Stream::of)			// Stream<String> (n)
				.map(commaPattern::split)	// Stream<String[]> (n)
				.toArray(String[][]::new);	// String[][] (n)
	}
}
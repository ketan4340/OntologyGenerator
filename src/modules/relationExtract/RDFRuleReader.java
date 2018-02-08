package modules.relationExtract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RDFRuleReader {
	/** 空白文字,半角スペース,全角スペースにマッチ。ただしバックスラッシュが直前に付くものは対象外。 */
	private static final Pattern removeSpacePattern = Pattern.compile("(?<!\\\\)[\\s 　]");
	/** 直後に"THEN"がない"}"と、直前に"."がある";"の位置にマッチ。"}"も";"も残る。 */
	private static final Pattern splitRulesPattern = Pattern.compile("(?<=}(?!THEN))|(?<=(?<=\\.);)", Pattern.CASE_INSENSITIVE);

	/** IF{...}THEN{...}の形式で書かれたルールにマッチ。 */
	private static final Pattern wholeIF_THENPattern = Pattern.compile("\\AIF\\{.+?\\Q}THEN{\\E.+?\\}\\z", Pattern.CASE_INSENSITIVE);
	private static final Pattern middleTHENPattern = Pattern.compile("\\Q}THEN{\\E", Pattern.CASE_INSENSITIVE);
	/** ...->...;の形式で書かれたルールにマッチ。 */
	private static final Pattern wholeArrowPattern = Pattern.compile("\\A.+->.+;\\z");
	private static final Pattern middleArrowPattern = Pattern.compile("->");

	/** ","にマッチ。ただし"\,"は無視する。 */
	private static final Pattern commaPattern = Pattern.compile("(?<!\\\\),");
	/** "."にマッチ。ただし"\."は無視する。 */
	private static final Pattern periodPattern = Pattern.compile("(?<!\\\\)"+Pattern.quote("."));


	public static RDFRules read(Path rulesFile) {
		String rulesString;
		try {
			rulesString = Files.lines(rulesFile).collect(Collectors.joining());
		} catch (IOException e) {
			rulesString = new String();
			e.printStackTrace();
		}
		if (rulesString.startsWith("\uFEFF"))
			rulesString = rulesString.substring(1);	// BOM除去
		return createRules(rulesString);
	}

	public static RDFRules createRules(String rulesString) {
		rulesString = removeSpacePattern.matcher(rulesString).replaceAll("");
		return new RDFRules(splitRulesPattern.splitAsStream(rulesString)
				.map(RDFRuleReader::createRule)
				.collect(Collectors.toSet()));
	}

	public static RDFRule createRule(String ruleString) {
		String[] triplePatterns =
				wholeIF_THENPattern.matcher(ruleString).matches()?
						triplePatternsOfIF_THEN(ruleString) :
				wholeArrowPattern.matcher(ruleString).matches()?
						triplePatternsOfArrow(ruleString) :
				new String[2];

		String[][] ifTriples = split2values(triplePatterns[0]);
		String[][] thenTriples = split2values(triplePatterns[1]);
		return new RDFRule(ifTriples, thenTriples);
	}

	private static String[] triplePatternsOfIF_THEN(String ruleString) {
		// 先頭に"IF{"、末尾に"}"が付いていることは確認済みなので切り取ってから、"}THEN{"で分割。
		String[] triplePatterns = middleTHENPattern.split(ruleString.substring(3, ruleString.length()-2));
		if (triplePatterns.length != 2) {
			System.err.println("Rule's format error by IF_THEN style. : "+ruleString);
			return new String[2];
		}
		return triplePatterns;
	}
	private static String[] triplePatternsOfArrow(String ruleString) {
		// 末尾に";"が付いていることは確認済みなので切り取ってから、"->"で分割。
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
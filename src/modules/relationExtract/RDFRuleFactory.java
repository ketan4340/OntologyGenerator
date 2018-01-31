package modules.relationExtract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RDFRuleFactory {
	/** 空白文字,半角スペース,全角スペースにマッチ。ただしバックスラッシュが直前に付くものは対象外。 */
	private static final Pattern removeSpacePattern = Pattern.compile("(?<!\\\\)[\\s 　]");
	/** ダブルクォーテーションにマッチする。エスケープさせるために使う。ただしバックスラッシュが直前に付くものは対象外。 */
	private static final Pattern replaceDoubleQuoPattern = Pattern.compile("(?<!\\\\)\\\"");
	/** 直後に"THEN"がない"}"と、直前に"."がある";"の位置にマッチ。"}"も";"も残る。 */
	private static final Pattern separateRulesPattern = Pattern.compile("(?<=}(?!THEN))|(?<=(?<=\\.);)", Pattern.CASE_INSENSITIVE);

	/** IF{...}THEN{...}の形式で書かれたルールにマッチ。 */
	private static final Pattern wholeIF_THENPattern = Pattern.compile("\\AIF\\{.+\\Q}THEN{\\E.+\\}\\z", Pattern.CASE_INSENSITIVE);
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
		return createRules(rulesString);
	}

	public static RDFRules createRules(String rulesString) {
		//rulesString = replaceDoubleQuoPattern.matcher(rulesString).replaceAll("\\\\\"");
		rulesString = removeSpacePattern.matcher(rulesString).replaceAll("");
		return new RDFRules(separateRulesPattern.splitAsStream(rulesString)
				.map(RDFRuleFactory::createRule)
				.collect(Collectors.toSet())
				);
	}

	public static RDFRule createRule(String ruleString) {
		/*
		String[] triplePatterns =
				wholeIF_THENPattern.matcher(ruleString).matches()?
						triplePatternsOfIF_THEN(ruleString) :
				wholeArrowPattern.matcher(ruleString).matches()?
						triplePatternsOfArrow(ruleString) : new String[2];
		 */
		System.out.println(ruleString);
		Pattern p = Pattern.compile("\\AIF\\{.*?\\Q}THEN{\\E.*?\\}\\z");
		Matcher m = p.matcher(ruleString);
		System.out.println(m.matches());
		try {
			Path r = Files.write(Paths.get("./matchtest.txt"), Arrays.asList(ruleString), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		String[] triplePatterns;
				if(wholeIF_THENPattern.matcher(ruleString).matches()) {
					triplePatterns = triplePatternsOfIF_THEN(ruleString);
					System.out.println("ifthen");
				}else if(wholeArrowPattern.matcher(ruleString).matches()) {
					triplePatterns=triplePatternsOfArrow(ruleString);
					System.out.println("arrow");
				}else {
					triplePatterns = new String[2];
					System.out.println("else");
				}


		String[][] ifTriples = split2vals(triplePatterns[0]);
		String[][] thenTriples = split2vals(triplePatterns[1]);
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

	private static String[][] split2vals(String triples) {
		return Stream.of(triples)
				.map(periodPattern::split)
				.flatMap(Stream::of)
				.map(commaPattern::split)
				.toArray(String[][]::new);
	}
}
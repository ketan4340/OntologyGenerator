package modules.relationExtract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RDFRuleFactory {
	/** 空白文字,半角スペース,全角スペースにマッチ。ただしバックスラッシュが直前に付くものは対象外。 */
	private static final Pattern spaceCharPattern = Pattern.compile("(?<!\\\\)[\\s 　]");
	/** 直後に"THEN"がない"}"と、直前に"."がある";"の位置にマッチ。"}"も";"も残る。 */
	private static final Pattern separateRulesPattern = Pattern.compile("(?<=}(?!THEN))|(?<=(?<=\\.);)", Pattern.CASE_INSENSITIVE);
	
	/** IF{...}THEN{...}の形式で書かれたルールにマッチ。 */
	private static final Pattern containIF_THENPattern = Pattern.compile("\\AIF\\{.+\\Q}THEN{\\E.+\\}\\z", Pattern.CASE_INSENSITIVE);
	private static final Pattern middleTHENPattern = Pattern.compile("\\Q}THEN{\\E", Pattern.CASE_INSENSITIVE);
	/** ...->...;の形式で書かれたルールにマッチ。 */
	private static final Pattern containArrowPattern = Pattern.compile("\\A.+->.+;\\z");
	private static final Pattern middleArrowPattern = Pattern.compile("->");
	
	/** ","にマッチ。ただし"\,"は無視する。 */
	private static final Pattern commaPattern = Pattern.compile("(?<!\\\\),");
	/** "."にマッチ。ただし"\."は無視する。 */
	private static final Pattern periodPattern = Pattern.compile("(?<!\\\\)"+Pattern.quote("."));
	
	
	public static RDFRules readRules(Path rulesFile) {
		String rulesString;
		try {
			rulesString = Files.readAllLines(rulesFile).stream().collect(Collectors.joining());
		} catch (IOException e) {
			rulesString = new String();
			e.printStackTrace();
		}
		return createRules(spaceCharPattern.matcher(rulesString).replaceFirst(""));
	}
	
	public static RDFRules createRules(String rulesString) {
		return new RDFRules(separateRulesPattern.splitAsStream(rulesString)
				.map(RDFRuleFactory::createRule)
				.collect(Collectors.toSet())
				);
	}
	
	public static RDFRule createRule(String ruleString) {
		String[] triplePatterns = 
				containIF_THENPattern.matcher(ruleString).find()? 
						triplePatternsOfIF_THEN(ruleString) : 
				containArrowPattern.matcher(ruleString).find()? 
						triplePatternsOfArrow(ruleString) : new String[2];
		
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
		return Stream.of(periodPattern.split(triples))
				.map(commaPattern::split)
				.toArray(String[][]::new);
	}
}
package data.RDF.rule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RDFRuleReader {
	/** 直前に"}"がある";"と、直前に"."がある";"の後ろの位置にマッチ.
	 * ただし直前(直後)の判定の際，空白文字は無視する.
	 * 位置にマッチなので置換や分割をしても，"}"も";"も残る. */
	private static final Pattern SPLIT_RULES_PATTERN =
			Pattern.compile("(?<=(?<=\\});)|(?<=(?<=\\.)\\s*;)", Pattern.CASE_INSENSITIVE);
			//Pattern.compile("(?<=(}(?!(\\s*THEN))))|(?<=(?<=\\.)\\s*;)", Pattern.CASE_INSENSITIVE);

	/** "IF(name){...}THEN{...}"の形式で書かれたルールにマッチ.
	 * 大文字小文字を問わない. ルールの名前"(name)"は無くても良い. (){}の前後に空白文字が入っても良い. */
	private static final Pattern WHOLE_IFTHEN_PATTERN = Pattern.compile(
					"\\A\\s*IF\\s*(\\((.*)\\))?\\s*\\{(.+)\\}\\s*THEN\\s*\\{(.+)\\};\\s*\\z",
					Pattern.CASE_INSENSITIVE);
	/** {@link Matcher#group(int)}で取り出す際の定数. {@code WHOLE_IFTHEN_PATTERN}の括弧()の変更に合わせること. */
	private static final int NAME_IFTHEN = 2, IF_PATTERN_IFTHEN = 3, THEN_PATTERN_IFTHEN = 4;
	/** "...->...;"の形式で書かれたルールにマッチ. 名前は付けられない. */
	private static final Pattern WHOLE_ARROW_PATTERN = Pattern.compile("\\A(.+)->(.+);\\s*\\z");
	/** {@link Matcher#group(int)}で取り出す際の定数. {@code WHOLE_ARROW_PATTERN}の括弧()の変更に合わせること. */
	private static final int IF_PATTERN_ARROW = 1, THEN_PATTERN_ARROW = 2;

	/** コメントアウトの"#"以降にマッチ. "#"から行末まで削除するために使う. */
	private static final Pattern COMMENT_PATTERN = Pattern.compile("#.*$");


	/**
	 * RDFルールを記述したファイルを読み込む. コメントアウトとBOMを削除
	 * @param rulesFile
	 * @return RDFルールのセット
	 */
	public static RDFRules readRDFRules(Path rulesFile) {
		String rulesString;
		try {
			rulesString = Files.lines(rulesFile)
					.map(COMMENT_PATTERN::matcher)	// コメントアウトを検知
					.map(m -> m.replaceAll(""))		// #から行末まで削除
					.collect(Collectors.joining());
			rulesString = removeBOM(rulesString);
		} catch (IOException e) {
			rulesString = new String();
			e.printStackTrace();
		}
		return createRDFRules(rulesString);
	}
	/** BOM削除. */
	private static String removeBOM(String s) {
		return s.startsWith("\uFEFF")? s.substring(1): s;
	}

	/**
	 * 文字列から一連のRDFルールを生成.
	 * @param rulesString
	 * @return RDFルールセット
	 */
	private static RDFRules createRDFRules(String rulesString) {
		return new RDFRules(SPLIT_RULES_PATTERN.splitAsStream(rulesString)
				.map(RDFRuleReader::createRDFRule)
				.collect(Collectors.toCollection(LinkedHashSet::new)));
	}

	/**
	 * 文字列から1つのSPARQL対応RDFルールを生成.
	 * @param ruleString
	 * @return SPARQLルール
	 */
	private static RDFRule createRDFRule(String ruleString) {
		Matcher matcherIFTHEN = WHOLE_IFTHEN_PATTERN.matcher(ruleString);
		Matcher matcherArrow = WHOLE_ARROW_PATTERN.matcher(ruleString);
		return 	matcherIFTHEN.matches()?
					new RDFRule(matcherIFTHEN.group(NAME_IFTHEN), matcherIFTHEN.group(IF_PATTERN_IFTHEN), matcherIFTHEN.group(THEN_PATTERN_IFTHEN)) :
				matcherArrow.matches()?
					new RDFRule("arrowRule", matcherIFTHEN.group(IF_PATTERN_ARROW), matcherIFTHEN.group(THEN_PATTERN_ARROW)) :
					RDFRule.EMPTY_RULE;
	}
}
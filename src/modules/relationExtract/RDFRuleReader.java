package modules.relationExtract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RDFRuleReader {
	/** 直後に"THEN"や"ELSE"がない"}"と、直前に"."がある";"の後ろの位置にマッチ. 
	 * 間に空白文字が入っても良い.
	 * 位置にマッチなので置換や分割をしても，"}"も";"も残る. */
	private static final Pattern SPLIT_RULES_PATTERN = 
			Pattern.compile("(?<=(}(?!(\\s*(THEN|ELSE)))))|(?<=(?<=\\.)\\s*;)", Pattern.CASE_INSENSITIVE);

	/** IF{...}THEN{...}の形式で書かれたルールにマッチ. 大文字小文字を問わない. {}の前後に空白文字が入っても良い. */
	private static final Pattern WHOLE_IFTHEN_PATTERN = 
			Pattern.compile("\\A\\s*IF\\s*\\{.+\\}\\s*THEN\\s*\\{.+\\}\\s*\\z", Pattern.CASE_INSENSITIVE);
	private static final Pattern HEAD_IF_PATTERN = Pattern.compile("\\s*IF\\s*\\{", Pattern.CASE_INSENSITIVE);
	private static final Pattern MIDDLE_THEN_PATTERN = Pattern.compile("\\}\\s*THEN\\s*\\{", Pattern.CASE_INSENSITIVE);
	//private static final Pattern MIDDLE_ELSE_PATTERN = Pattern.compile("\\}\\s*ELSE\\s*\\{", Pattern.CASE_INSENSITIVE);
	private static final Pattern TAIL_BRACE_PATTERN = Pattern.compile("\\}\\s*$");
	/** ...->...;の形式で書かれたルールにマッチ. */
	private static final Pattern WHOLE_ARROW_PATTERN = Pattern.compile("\\s*.+->.+;\\s*");
	private static final Pattern MIDDLE_ARROW_PATTERN = Pattern.compile("->");
	//private static final Pattern MIDDLE_EXCLAMATION_ARROW_PATTERN = Pattern.compile("!>");
	private static final Pattern TAIL_SEMICOLON_PATTERN = Pattern.compile(";\\s*");

	/** " "にマッチ. ただし"\,"は無視する. */
	//private static final Pattern SPACE_PATTERN = Pattern.compile("(?<!\\\\) ");
	/** ","にマッチ. ただし"\,"は無視する. */
	//private static final Pattern COMMA_PATTERN = Pattern.compile("(?<!\\\\),");
	/** "."にマッチ. 前後に0以上の空白文字を許す. ただし"\."は無視する. */
	private static final Pattern PERIOD_PATTERN = Pattern.compile("\\s*(?<!\\\\)\\.\\s*");
	/** "\s...\s...\s...\s"にマッチ. Matcher#groupによってRDFトリプルを取り出せる. 使う段階ではピリオドは残ってないはずだが一応対象外としてある. */
	private static final Pattern TRIPLE_PATTERN = 
			Pattern.compile("^\\s*([\\S&&[^\\.]]+)\\s+([\\S&&[^\\.]]+)\\s+([\\S&&[^\\.]]+)\\s*$");
	/** コメントアウトの"#"以降にマッチ. "#"から行末まで削除する. */
	private static final Pattern COMMENT_PATTERN = Pattern.compile("#.*$");

	/**
	 * RDFルールを記述したファイルを読み込む. コメントアウトとBOMを削除
	 * @param rulesFile
	 * @return
	 */
	public static RDFRules readRDFRules(Path rulesFile) {
		String rulesString;
		try {
			rulesString = Files.lines(rulesFile)
					.map(COMMENT_PATTERN::matcher)	// コメントアウトを検知
					.map(m -> m.replaceAll(""))		// #から行末まで削除
					.collect(Collectors.joining());
		} catch (IOException e) {
			rulesString = new String();
			e.printStackTrace();
		}
		rulesString = removeBOM(rulesString);
		return createRules(rulesString);
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
	private static RDFRules createRules(String rulesString) {
		return new RDFRules(SPLIT_RULES_PATTERN.splitAsStream(rulesString)
				.map(RDFRuleReader::createSPARQLRule)
				.collect(Collectors.toCollection(LinkedHashSet::new)));
	}

	/** 
	 * 文字列から1つのRDFルールを生成. 旧型
	 * @param ruleString
	 * @return RDFルール
	 */
	public static RDFRule createRDFRule(String ruleString) {
		String[] graphPatternString = split2IF_THEN(ruleString);
		RDFGraphPattern rdfgp_if = createRDFGraphPattern(graphPatternString[0]);
		RDFGraphPattern rdfgp_then = createRDFGraphPattern(graphPatternString[1]);
		return new RDFRule(rdfgp_if, rdfgp_then);
	}

	/**
	 * 文字列から1つのSPARQL対応RDFルールを生成.
	 * @param ruleString
	 * @return SPARQLルール
	 */
	public static SPARQLRule createSPARQLRule(String ruleString) {
		String[] graphPatternString = split2IF_THEN(ruleString);
		return new SPARQLRule(graphPatternString[0], graphPatternString[1]);
	}
	
	/**
	 * 文字列から1つのRDFグラフパターンを生成.
	 * @param graphPatternString
	 * @return RDFグラフパターン
	 */
	private static RDFGraphPattern createRDFGraphPattern(String graphPatternString) {
		Set<RDFTriplePattern> triplePatterns = PERIOD_PATTERN.splitAsStream(graphPatternString)
				.map(RDFRuleReader::createRDFTriplePattern)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		return new RDFGraphPattern(triplePatterns);
	}

	/**
	 * 文字列から1つのRDFトリプルパターンを生成.
	 * @param triplePatternString
	 * @return RDFトリプルパターン
	 */
	private static RDFTriplePattern createRDFTriplePattern(String triplePatternString) {
		Matcher m = TRIPLE_PATTERN.matcher(triplePatternString);
		return m.matches()? new RDFTriplePattern(m.group(1), m.group(2), m.group(3)): null;
	}

	private static String[] split2IF_THEN(String ruleString) {
		return 	WHOLE_IFTHEN_PATTERN.matcher(ruleString).matches()?
					triplePatternsOfIFTHEN(ruleString) :
				WHOLE_ARROW_PATTERN.matcher(ruleString).matches()?
					triplePatternsOfArrow(ruleString) :
					new String[2];
	}
	
	private static String[] triplePatternsOfIFTHEN(String ruleString) {
		// 先頭に"IF{"、末尾に"}"が付いていることは確認済みなので，切り取ってから"}THEN{"で分割。
		ruleString = HEAD_IF_PATTERN.matcher(ruleString).replaceFirst("");
		ruleString = TAIL_BRACE_PATTERN.matcher(ruleString).replaceFirst("");
		String[] triplePatterns = MIDDLE_THEN_PATTERN.split(ruleString);
		if (triplePatterns.length != 2) {
			System.err.println("Rule's format error by IF_THEN style. : " + ruleString + "\n");
			return new String[2];
		}
		return triplePatterns;
	}
	private static String[] triplePatternsOfArrow(String ruleString) {
		// 末尾に";"が付いていることは確認済みなので，切り取ってから"->"で分割。
		ruleString = TAIL_SEMICOLON_PATTERN.matcher(ruleString).replaceFirst("");
		String[] triplePatterns = MIDDLE_ARROW_PATTERN.split(ruleString);
		if (triplePatterns.length != 2) {
			System.err.println("Rule's format error by Arrow style. : " + ruleString + "\n");
			return new String[2];
		}
		return triplePatterns;
	}
}
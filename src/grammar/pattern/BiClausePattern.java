package grammar.pattern;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BiClausePattern extends SubsentencePattern {

	private BiClausePattern() {
		super();
	}
	
	
	public static BiClausePattern compile(String[][][] strsss) {
		if (strsss.length != 2) 
			throw new Error("二連文節だから文節パターンは2つにしてどうぞ。");
		return Arrays.stream(strsss).map(ClausePattern::compile)
				.collect(Collectors.toCollection(BiClausePattern::new));
	}
}

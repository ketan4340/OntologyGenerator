package modules.relationExtract;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RDFGraphPattern {
	
	private Set<RDFTriplePattern> triplePatterns;

	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public RDFGraphPattern(Set<RDFTriplePattern> triplePatterns) {
		setTriplePatterns(triplePatterns);
	}
	public RDFGraphPattern(Collection<RDFTriplePattern> triplePatterns) {
		setTriplePatterns(new HashSet<>(triplePatterns));
	}
	

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public String[][] toArray() {
		return triplePatterns.stream().map(tp -> tp.toArray()).toArray(String[][]::new);
	}
	/**
	 * 指定の区切り文字や接頭辞，接尾辞で区切ったグラフパターン，トリプルパターンの文字列を返す.
	 * @param graphDelimiter	グラフパターンの区切り文字
	 * @param graphPrefix		グラフパターンの接頭辞
	 * @param graphSuffix		グラフパターンの接尾辞
	 * @param tripleDelimiter	トリプルパターンの区切り文字
	 * @param triplePrefix		トリプルパターンの接頭辞
	 * @param tripleSuffix		トリプルパターンの接尾辞
	 * @return
	 */
	public String joins(CharSequence graphDelimiter, CharSequence graphPrefix, CharSequence graphSuffix,
			CharSequence tripleDelimiter, CharSequence triplePrefix, CharSequence tripleSuffix) {
		return triplePatterns.stream()
				.map(tp -> tp.join(tripleDelimiter, triplePrefix, tripleSuffix))
				.collect(Collectors.joining(graphDelimiter, graphPrefix, graphSuffix));
	}
		
	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public Set<RDFTriplePattern> getTriplePatterns() {
		return triplePatterns;
	}
	public void setTriplePatterns(Set<RDFTriplePattern> triplePatterns) {
		this.triplePatterns = triplePatterns;
	}

	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return joins("\n", "{", "}", " ", "", " . ");
	}
}
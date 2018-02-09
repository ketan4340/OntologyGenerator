package modules.relationExtract;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import data.original.Namespace;

public class RDFGraphPattern {
	private static final String prefixRDF = Namespace.RDF.toQueryPrefixDefinition();
	private static final String prefixRDFS = Namespace.RDFS.toQueryPrefixDefinition();
	private static final String prefixOWL = Namespace.OWL.toQueryPrefixDefinition();
	private static final String prefixDC = Namespace.DC.toQueryPrefixDefinition();
	private static final String prefixDCTERM = Namespace.DCTERMS.toQueryPrefixDefinition();
	private static final String prefixSCHEMA = Namespace.SCHEMA.toQueryPrefixDefinition();
	private static final String prefixJASS = Namespace.JASS.toQueryPrefixDefinition();
	private static final String prefixGOO = Namespace.GOO.toQueryPrefixDefinition();

	
	
	private Set<RDFTriplePattern> triplePatterns;

	public RDFGraphPattern(Collection<? extends RDFTriplePattern> triplePatterns) {
		this.triplePatterns = new HashSet<>(triplePatterns);
	}
	public RDFGraphPattern(String[][] patternsString) {
		
	}
	
	

	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public String[][] toArray() {
		return triplePatterns.stream().map(tp -> tp.toArray()).toArray(String[][]::new);
	}
	public String joins(CharSequence graphDelimiter, CharSequence graphPrefix, CharSequence graphSuffix,
			CharSequence tripleDelimiter, CharSequence triplePrefix, CharSequence tripleSuffix) {
		return triplePatterns.stream()
				.map(tp -> tp.join(tripleDelimiter, triplePrefix, tripleSuffix))
				.collect(Collectors.joining(graphDelimiter, graphPrefix, graphSuffix));
	}
	
	public Query toQuery() {
		String queryString =
				prefixRDF+prefixRDFS+prefixOWL+prefixDC+prefixDCTERM+prefixSCHEMA+prefixJASS+prefixGOO +
				"SELECT * " +
				"WHERE {" +
				joins("", "", "", " ", "", " .") +
				"}";
		return QueryFactory.create(queryString);
	}
	
	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public Set<RDFTriplePattern> getTriplePatterns() {
		return triplePatterns;
	}
	public void setTriplePatterns(Set<RDFTriplePattern> triplePatterns) {
		this.triplePatterns = triplePatterns;
	}

	
	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return joins("\n", "{", "}", ", ", "", ".");
	}
}
package modules.relationExtract;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;

import data.RDF.Namespace;
import data.id.Identifiable;

public class RDFRule implements Identifiable{
	private static final String prefixRDF = Namespace.RDF.toQueryPrefixDefinition();
	private static final String prefixRDFS = Namespace.RDFS.toQueryPrefixDefinition();
	private static final String prefixOWL = Namespace.OWL.toQueryPrefixDefinition();
	private static final String prefixDC = Namespace.DC.toQueryPrefixDefinition();
	private static final String prefixDCTERM = Namespace.DCTERMS.toQueryPrefixDefinition();
	private static final String prefixSCHEMA = Namespace.SCHEMA.toQueryPrefixDefinition();
	private static final String prefixJASS = Namespace.JASS.toQueryPrefixDefinition();
	private static final String prefixGOO = Namespace.GOO.toQueryPrefixDefinition();
	private static final String prefixSIO = Namespace.SIO.toQueryPrefixDefinition();
	
	private int id;
	private String label;
	private RDFGraphPattern ifPattern;
	private RDFGraphPattern thenPattern;

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public RDFRule(String[][] ifs, String[][] thens) {
		this.ifPattern = new RDFGraphPattern(
				Stream.of(ifs)
				.map(tri -> new RDFTriplePattern(tri[0], tri[1], tri[2]))
				.collect(Collectors.toSet()));
		this.thenPattern = new RDFGraphPattern(
				Stream.of(thens)
				.map(tri -> new RDFTriplePattern(tri[0], tri[1], tri[2]))
				.collect(Collectors.toSet()));
	}

	/****************************************/
	/**********    Member Method   **********/
	/****************************************/
	public Model expands(Model model) {
		return model.add(solve(model));
	}

	public Model converts(Model model) {
		return solve(model);
	}

	public Model solve(Model model) {
		return QueryExecutionFactory.create(toQuery(), model).execConstruct();
	}
	
	private Query toQuery() {
		String queryString = 
				prefixRDF+prefixRDFS+prefixOWL+prefixDC+prefixDCTERM+prefixSCHEMA+prefixJASS+prefixGOO+prefixSIO +
				"CONSTRUCT " +
					thenPattern.joins(".", "{", "}", " ", "", " ") +
				"WHERE " +
					ifPattern.joins(".", "{", "}", " ", "", " ");
		return QueryFactory.create(queryString);
	}

	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public RDFGraphPattern getIfPattern() {
		return ifPattern;
	}
	public void setIfPattern(RDFGraphPattern ifPattern) {
		this.ifPattern = ifPattern;
	}
	public RDFGraphPattern getThenPattern() {
		return thenPattern;
	}
	public void setThenPattern(RDFGraphPattern thenPattern) {
		this.thenPattern = thenPattern;
	}

	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public int id() {
		return getID();
	}

	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return "IF "+ ifPattern.toString() +"\nTHEN "+ thenPattern.toString();
	}
}
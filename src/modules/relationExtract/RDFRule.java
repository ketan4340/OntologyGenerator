package modules.relationExtract;

import java.util.Objects;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;

import data.RDF.Namespace;
import data.id.Identifiable;
import grammar.GrammarInterface;

public class RDFRule implements Identifiable, GrammarInterface {
	protected static final String prefixRDF = Namespace.RDF.toQueryPrefixDefinition();
	protected static final String prefixRDFS = Namespace.RDFS.toQueryPrefixDefinition();
	protected static final String prefixOWL = Namespace.OWL.toQueryPrefixDefinition();
	protected static final String prefixDC = Namespace.DC.toQueryPrefixDefinition();
	protected static final String prefixDCTERM = Namespace.DCTERMS.toQueryPrefixDefinition();
	protected static final String prefixSCHEMA = Namespace.SCHEMA.toQueryPrefixDefinition();
	protected static final String prefixJASS = Namespace.JASS.toQueryPrefixDefinition();
	protected static final String prefixGOO = Namespace.GOO.toQueryPrefixDefinition();
	protected static final String prefixSIO = Namespace.SIO.toQueryPrefixDefinition();
	
	protected static final String QUERY_PREFIXES = 
			prefixRDF+prefixRDFS+prefixOWL+prefixDC+prefixDCTERM+prefixSCHEMA+prefixJASS+prefixGOO+prefixSIO;
	
	protected static final RDFRule EMPTY_RULE = new RDFRule("", "", "");
	
	private static int sum = 0;
	
	private final int id;
	private String name;
	private String ifPattern;
	private String thenPattern;

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public RDFRule(String name, String ifPattern, String thenPattern) {
		this.id = sum++;
		setName(name);
		setIfPattern(ifPattern);
		setThenPattern(thenPattern);
	}
	
	/****************************************/
	/**********    Member Method   **********/
	/****************************************/
	public Model solves(Model model) {
		return QueryExecutionFactory.create(toConstructQuery(), model).execConstruct();
	}
	
	public Model expands(Model model) {
		return model.add(solves(model));
	}

	public Model converts(Model model) {
		return solves(model);
	}

	private Query toConstructQuery() {
		String queryString = 
				QUERY_PREFIXES + 
				"CONSTRUCT {" +
					thenPattern +
				"} WHERE {" +
					ifPattern +
				"}";
		return QueryFactory.create(queryString);
	}
	

	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public int id() {
		return getID();
	}
	@Override
	public String name() {
		return name;
	}

	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public int getID() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = Objects.isNull(name)||name.isEmpty()? "no name" : name;
	}
	public String getIfPattern() {
		return ifPattern;
	}
	public void setIfPattern(String ifPattern) {
		this.ifPattern = ifPattern;
	}
	public String getThenPattern() {
		return thenPattern;
	}
	public void setThenPattern(String thenPattern) {
		this.thenPattern = thenPattern;
	}
	
	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return id + ":" + name + ":\tIF {"+ ifPattern +"\n} THEN {"+ thenPattern + "}";
	}

}
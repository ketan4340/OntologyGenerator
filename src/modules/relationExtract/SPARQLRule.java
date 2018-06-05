package modules.relationExtract;

import java.util.Objects;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import data.id.Identifiable;

public class SPARQLRule extends AbstractRDFRule implements Identifiable{
	private static int sum = 0;
	protected static final SPARQLRule EMPTY_RULE = new SPARQLRule("", "", "");
	
	private final int id;
	private String name;
	private String ifPattern;
	private String thenPattern;

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public SPARQLRule(String name, String ifPattern, String thenPattern) {
		this.id = sum++;
		setName(name);
		setIfPattern(ifPattern);
		setThenPattern(thenPattern);
	}

	/****************************************/
	/**********  Abstract  Method  **********/
	/****************************************/
	@Override
	public Query toConstructQuery() {
		String queryString = 
				prefixRDF+prefixRDFS+prefixOWL+prefixDC+prefixDCTERM+prefixSCHEMA+prefixJASS+prefixGOO+prefixSIO +
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
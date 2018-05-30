package modules.relationExtract;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import data.id.Identifiable;

public class SPARQLRule extends AbstractRDFRule implements Identifiable{
	private static int sum = 0;
	
	private final int id;
	private String ifPattern;
	private String thenPattern;

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public SPARQLRule(String ifPattern, String thenPattern) {
		this.id = sum++;
		setIfPattern(ifPattern);
		setThenPattern(thenPattern);
	}

	/****************************************/
	/**********  Abstract  Method  **********/
	/****************************************/
	@Override
	protected Query toQuery() {
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
		return "IF {"+ ifPattern +"\n} THEN {"+ thenPattern + "}";
	}

}
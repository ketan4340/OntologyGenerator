package modules.relationExtract;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import data.id.Identifiable;

public class RDFRule extends AbstractRDFRule implements Identifiable{
	private static int sum = 0;
	
	private final int id;
	private RDFGraphPattern ifPattern;
	private RDFGraphPattern thenPattern;

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public RDFRule(RDFGraphPattern ifPattern, RDFGraphPattern thenPattern) {
		this.id = sum++;
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
				"CONSTRUCT " +
					thenPattern.joins(".", "{", "}", " ", "", " ") +
				"WHERE " +
					ifPattern.joins(".", "{", "}", " ", "", " ");
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
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return id + ":\tIF {"+ ifPattern.toString() +"\n} THEN {"+ thenPattern.toString() + "}";
	}
}
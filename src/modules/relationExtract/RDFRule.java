package modules.relationExtract;

import java.util.Objects;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

public class RDFRule extends AbstractRDFRule {
	private static int sum = 0;
	protected static final RDFRule EMPTY_RULE = new RDFRule("", "", "");
	
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
	/**********  Abstract  Method  **********/
	/****************************************/
	@Override
	public Query toConstructQuery() {
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
package data.RDF.rule;

import data.id.Identifiable;
import grammar.GrammarInterface;

public class RDFRule implements Identifiable, GrammarInterface {
	protected static final RDFRule EMPTY_RULE = new RDFRule("empty rule", "", "");

	private static int sum = 0;

	private final int id;
	private final String name;
	private final String ifPattern;
	private final String thenPattern;

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public RDFRule(String name, String ifPattern, String thenPattern) {
		this.id = sum++;
		this.name = name;
		this.ifPattern = ifPattern;
		this.thenPattern = thenPattern;
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public String toQueryString() {
		return toCONSTRUCTPhrase() + toWHEREPhrase();
	}
	public String toCONSTRUCTPhrase() {
		return "CONSTRUCT {"+thenPattern+"} ";
	}
	public String toWHEREPhrase() {
		return "WHERE {"+ifPattern+"} ";
	}

	/* ================================================== */
	/* ================ Interface Method ================ */
	/* ================================================== */
	@Override
	public int id() {
		return id;
	}
	@Override
	public String name() {
		return name;
	}


	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public String toString() {
		return id + "-" + name + ":\tIF {"+ ifPattern +"}\n\t\tTHEN {"+ thenPattern + "}";
	}

}
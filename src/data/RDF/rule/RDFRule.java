package data.RDF.rule;

import data.id.Identifiable;

public class RDFRule implements Identifiable {
	protected static final RDFRule EMPTY_RULE = new RDFRule("empty rule", "", "");

	private static int SUM = 0;

	private final int id;
	private final String name;
	private final String ifPattern;
	private final String thenPattern;

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public RDFRule(String name, String ifPattern, String thenPattern) {
		this.id = SUM++;
		this.name = name;
		this.ifPattern = ifPattern;
		this.thenPattern = thenPattern;
	}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public String toQueryString() {
		return toConstructPhrase() + toWherePhrase();
	}
	public String toConstructPhrase() {
		return "CONSTRUCT {"+thenPattern+"} ";
	}
	public String toWherePhrase() {
		return "WHERE {"+ifPattern+"} ";
	}

	/* ================================================== */
	/* ================ Interface Method ================ */
	/* ================================================== */
	@Override
	public int id() { return id; }


	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public String toString() {
		return id + "-" + name + ":\tIF {"+ ifPattern +"}\n\t\tTHEN {"+ thenPattern + "}";
	}

}
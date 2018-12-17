package data.id;

import java.util.LinkedHashMap;

import org.apache.jena.rdf.model.Statement;

import util.RDF.RDFUtil;

public class StatementIDMap extends IDLinkedMap<Statement> {
	private static final long serialVersionUID = 511791879522986122L;

	private static int sum = 0;


	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public StatementIDMap() {
		super();
	}
	public StatementIDMap(int initialCapacity) {
		super(initialCapacity);
	}
	public StatementIDMap(LinkedHashMap<Statement, IDTupleByStatement> m) {
		super(m);
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public void setStatementID() {
		forEachValue(idt -> idt.setTripleID(String.valueOf(sum++)));
	}
	public void setSubjectString() {
		forEach((stmt, idt) -> idt.setSubject(RDFUtil.toResourceStringAsQName(stmt.getSubject())));
	}
	public void setPredicateString() {
		forEach((stmt, idt) -> idt.setPredicate(RDFUtil.toResourceStringAsQName(stmt.getPredicate())));
	}
	public void setObjectString() {
		forEach((stmt, idt) -> idt.setObject(RDFUtil.toResourceStringAsQName(stmt.getObject())));
	}

	public IDRelation createIDRelation() {
		setStatementID();
		setSubjectString();
		setPredicateString();
		setObjectString();
		return new IDRelation(values());
	}

}

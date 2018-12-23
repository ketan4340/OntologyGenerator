package data.id;

import java.util.LinkedHashMap;

import org.apache.jena.rdf.model.Statement;

public class StatementIDMap extends IDLinkedMap<Statement> {
	private static final long serialVersionUID = 511791879522986122L;


	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public StatementIDMap() {
		super();
	}
	public StatementIDMap(int initialCapacity) {
		super(initialCapacity);
	}
	public StatementIDMap(LinkedHashMap<Statement, IDTuple> m) {
		super(m);
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public void setStatementString() {
		forEach((stmt, idt) -> idt.setStatement(stmt));
	}

	public IDRelation createIDRelation() {
		setStatementString();
		return new IDRelation(IDTupleByStatement.ATTRIBUTES, values());
	}

}

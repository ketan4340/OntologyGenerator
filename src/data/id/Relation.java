package data.id;

import java.util.Collection;
import java.util.HashSet;

public class Relation<T extends Tuple> extends HashSet<T> {
	private static final long serialVersionUID = -795496356555507147L;

	private final Tuple attributes;

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public Relation(Collection<? extends T> tuples, Tuple attributes) {
		super(tuples);
		this.attributes = attributes;
	}
	public Relation(Tuple attributes) {
		super();
		this.attributes = attributes;
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	
	/* ===================== Getter ===================== */
	public Tuple getAttributes() {
		return attributes;
	}

}

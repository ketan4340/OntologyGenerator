package data.id;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IDRelation extends HashSet<IDTuple> {
	private static final long serialVersionUID = 3136163355790663043L;
	
	private final IDTuple attributes;

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public IDRelation(IDTuple attr, Collection<? extends IDTuple> tuples) {
		super(tuples);
		this.attributes = attr;
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	
	/* ===================== Getter ===================== */
	public IDTuple getAttributes() {
		return attributes;
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public List<String> toStringList() {
		Stream<String> attrStrm = Stream.of(getAttributes().toCSV());
		Stream<String> valStrm = stream()
				.sorted(Comparator.comparing(IDTuple::primaryKey)
						.thenComparing(Comparator.comparing(IDTuple::secondaryKey)))
				.map(IDTuple::toCSV);
		return Stream.concat(attrStrm, valStrm).collect(Collectors.toList());
	}

}

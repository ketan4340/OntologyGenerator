package data.id;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IDRelation extends Relation<IDTupleByStatement>{
	private static final long serialVersionUID = 3136163355790663043L;


	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public IDRelation(Collection<? extends IDTupleByStatement> tupleList) {
		super(tupleList, IDTupleByStatement.ATTRIBUTES);
	}
	public IDRelation() {
		super(IDTupleByStatement.ATTRIBUTES);
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public List<String> toStringList() {
		Stream<String> attrStrm = Stream.of(getAttributes().toCSV());
		Stream<String> valStrm = stream()
				.sorted(Comparator.comparing(IDTupleByStatement::getLongSentenceID)
						.thenComparing(Comparator.comparing(IDTupleByStatement::getShortSentenceID)))
				.map(IDTupleByStatement::toCSV);
		return Stream.concat(attrStrm, valStrm).collect(Collectors.toList());
	}

}

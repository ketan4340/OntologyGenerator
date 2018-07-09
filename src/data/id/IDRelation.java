package data.id;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import util.tuple.Relation;

public class IDRelation extends Relation<IDTuple>{
	private static final long serialVersionUID = 3136163355790663043L;


	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public IDRelation(Collection<? extends IDTuple> tupleList) {
		super(tupleList, IDTuple.ATTRIBUTES);
	}
	public IDRelation() {
		super(IDTuple.ATTRIBUTES);
	}



	/****************************************/
	/**********    Member Method   **********/
	/****************************************/
	public List<String> toStringList() {
		Stream<String> attrStrm = Stream.of(getAttributes().toCSV());
		Stream<String> valStrm = stream()
				.sorted(Comparator.comparing(IDTuple::getLongSentenceID)
						.thenComparing(Comparator.comparing(IDTuple::getShortSentenceID)))
				.map(IDTuple::toCSV);
		return Stream.concat(attrStrm, valStrm).collect(Collectors.toList());
	}

	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/

}
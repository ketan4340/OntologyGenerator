package data.id;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
		return stream()
				.sorted(Comparator.comparingInt(IDTuple::getLongSentenceID)
						.thenComparing(Comparator.comparingInt(IDTuple::getShortSentenceID)))
				.map(IDTuple::toCSV)
				.collect(Collectors.toList());
	}

	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/

}
package data.id;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

public class StatementIDMap extends IDLinkedMap<Statement> {
	private static final long serialVersionUID = 511791879522986122L;

	private static int sum = 0;
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public StatementIDMap() {
		super();
	}
	public StatementIDMap(int initialCapacity) {
		super(initialCapacity);
	}
	public StatementIDMap(LinkedHashMap<Statement, IDTuple> m) {
		super(m);
	}


	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public void setStatementID() {
		forEachValue(v -> v.setTripleID(sum++));
	}
	public void setSubjectString() {
		forEach((k, v) -> v.setSubject(k.getModel().qnameFor(k.getSubject().getURI())));
	}
	public void setPredicateString() {
		forEach((k, v) -> v.setPredicate(k.getModel().qnameFor(k.getPredicate().getURI())));
	}
	public void setObjectString() {
		forEach((k, v) -> {
			RDFNode object = k.getObject();
			String objectString = object.isResource()? object.asResource().getURI() : object.asLiteral().toString();
			v.setObject(k.getModel().qnameFor(objectString));
		});
	}
	public void setStatement() {
		setStatementID();
		setSubjectString();
		setPredicateString();
		setObjectString();
	}
	
	public List<String> toStringList() {
		setStatement();
		return values().stream().map(IDTuple::toCSV).collect(Collectors.toList());
	}
}
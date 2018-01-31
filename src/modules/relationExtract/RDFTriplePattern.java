package modules.relationExtract;

import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class RDFTriplePattern {

	private String subjectURI;
	private String predicateURI;
	private String objectURI;
	
	public RDFTriplePattern(String subjectURI, String predicateURI, String objectURI) {
		this.subjectURI = subjectURI;
		this.predicateURI = predicateURI;
		this.objectURI = objectURI;
	}
	
	public Statement fillStatement(Model targetModel, Map<String, String> varURIMap) {
		Resource subject = targetModel.getResource(varURIMap.get(subjectURI));
		Property predicate = targetModel.getProperty(varURIMap.get(predicateURI));
		RDFNode object = targetModel.getResource(varURIMap.get(objectURI));
		return targetModel.createStatement(subject, predicate, object);
	}
}
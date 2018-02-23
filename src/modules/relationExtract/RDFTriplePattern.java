package modules.relationExtract;

import java.util.Map;
import java.util.StringJoiner;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class RDFTriplePattern {

	private static final String BLANK = "_:";
	
	private String subjectVar;
	private String predicateVar;
	private String objectVar;


	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public RDFTriplePattern(String subjectURI, String predicateURI, String objectURI) {
		this.subjectVar = subjectURI;
		this.predicateVar = predicateURI;
		this.objectVar = objectURI;
	}


	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public String[] toArray() {
		return new String[]{subjectVar, predicateVar, objectVar};
	}
	public String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
		return new StringJoiner(delimiter, prefix, suffix)
				.add(subjectVar).add(predicateVar).add(objectVar).toString();
	}
	
	public Statement fillStatement(Model targetModel, Map<String, String> varURIMap) {
		/*
		System.out.println("filling");
		System.out.println("  "+subjectVar +", " + predicateVar + ", " + objectVar);
		 //*/
		String var_s = varURIMap.get(subjectVar);
		String var_p = varURIMap.get(predicateVar); 
		String var_o = varURIMap.get(objectVar);
		Resource subject = var_s.startsWith(BLANK)? 
				targetModel.createResource(): targetModel.getResource(var_s);
		Property predicate = targetModel.getProperty(var_p);
		RDFNode object = var_o.startsWith(BLANK)? 
				targetModel.createResource(): targetModel.getResource(var_o);

		/*
		System.out.println("filled");
		System.out.println("  "+subject.getURI() +", "+ predicate.getURI() +", "+ object.toString());	//TODO
		//*/
		
		return targetModel.createStatement(subject, predicate, object);
	}


	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return join(" ", "", ".");
	}
}
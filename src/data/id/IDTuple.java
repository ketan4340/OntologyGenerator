package data.id;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import data.RDF.rule.RDFRule;
import grammar.sentence.Sentence;

public interface IDTuple extends TupleInterface, Cloneable {
	String primaryKey();
	String secondaryKey();
	
	IDTuple clone();

	void setLongSentence(Sentence longSentence);
	void setShortSentence(Sentence shortSentence);
	void setRDFRuleID(RDFRule rule);
	void setStatement(Statement stmt);
	void setModel(Model model);	
}

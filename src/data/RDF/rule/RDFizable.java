package data.RDF.rule;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import data.RDF.vocabulary.JASS;
import data.id.Identifiable;

public interface RDFizable extends Identifiable {
	default String getURI() {
		return JASS.uri+getClass().getSimpleName()+id();
	}
	Resource toRDF(Model model);
}
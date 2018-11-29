package data.RDF.rule;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import data.RDF.vocabulary.JASS;
import data.id.Identifiable;

public interface JassModelizable extends Identifiable {
	default String getJassURI() {
		return JASS.getURI()+getClass().getSimpleName().toLowerCase()+id();
	}
	Resource toJASS(Model model);
}

package data.RDF;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public interface RDFconvertable {
	Resource toRDF(Model model);
}
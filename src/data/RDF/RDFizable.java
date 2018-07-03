package data.RDF;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public interface RDFizable {
	String getURI();

	Resource toRDF(Model model);
}
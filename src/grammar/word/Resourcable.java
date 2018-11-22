package grammar.word;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public interface Resourcable {
	String toResourceURI();
	Resource createResource(Model m);
}
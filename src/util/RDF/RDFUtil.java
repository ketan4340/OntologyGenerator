package util.RDF;

import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class RDFUtil {

	
	public static final Resource createRDFList(Model model, List<Resource> list) {
		Resource firstNode, node = firstNode = model.createResource();
		for (Resource r : list) {
			Resource nextNode = model.createResource().addProperty(RDF.type, RDF.List);
			node.addProperty(RDF.type, RDF.List)
				.addProperty(RDF.first, r)
				.addProperty(RDF.rest, nextNode);
			node = nextNode;
		}
		node.addProperty(RDF.rest, RDF.nil);
		return firstNode;
	}
}
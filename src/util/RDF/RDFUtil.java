package util.RDF;

import java.util.List;
import java.util.Objects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class RDFUtil {
	private static final String BLANK_NODE_PREFIX = "_:";
	
	
	/**
	 * RDFコレクションの作成. addProperty(RDF.type, RDF.List) これは暗黙の了解でわかってるからいい
	 * @param model
	 * @param list
	 * @return
	 */
	public static final Resource createRDFList(Model model, List<Resource> list) {
		if (list.isEmpty())
			return RDF.nil;
		boolean firstLoop = true;
		Resource firstNode, node = firstNode = RDF.nil;
		for (Resource r : list) {
			Resource beforeNode = node;
			node = model.createResource()
					.addProperty(RDF.first, r);
			if (firstLoop) {
				firstNode = node;
				firstLoop = false;
				continue;
			}
			beforeNode.addProperty(RDF.rest, node);
		}
		node.addProperty(RDF.rest, RDF.nil);
		return firstNode;
	}

	/**
	 * QNameを使った形式で返せるなら返す. リテラルならそのまま．空白ノードなら"_:~"で.
	 * @param object
	 * @return
	 */
	public static final String toResourceStringAsQName(RDFNode object) {
		Model m = object.getModel();
		if (object.isURIResource()) {
			if (Objects.nonNull(m))
				return m.shortForm(object.asResource().getURI());
			return object.asResource().getURI();
		} else if (object.isAnon())		// 空白ノード
			return BLANK_NODE_PREFIX + object.toString();
		else if (object.isLiteral())	// リテラル
			return object.asLiteral().toString();
		else 
			return object.toString();
	}
	
}
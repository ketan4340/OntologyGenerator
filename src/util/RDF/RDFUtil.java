package util.RDF;

import java.util.Objects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;

public class RDFUtil {
	private static final String BLANK_NODE_PREFIX = "_:";

	/**
	 * QNameを使った形式で返せるなら返す. リテラルならそのまま．空白ノードなら"_:~"で.
	 * @param object
	 * @return リソースのQName
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
package util;

import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.shared.PrefixMapping;

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
			String uri = object.asResource().getURI();
			return Objects.nonNull(m)? m.shortForm(uri): uri;
		} else if (object.isAnon())		// 空白ノード
			return BLANK_NODE_PREFIX + object.toString();
		else if (object.isLiteral())	// リテラル
			return object.asLiteral().toString();
		else 
			return object.toString();
	}
	
	public static String createPrefixDeclaration(PrefixMapping pm) {
		return pm.getNsPrefixMap().entrySet().parallelStream()
				.map(e -> "PREFIX " + e.getKey() + ": <" + e.getValue() + ">")
				.collect(Collectors.joining(" "));
	}
	
}

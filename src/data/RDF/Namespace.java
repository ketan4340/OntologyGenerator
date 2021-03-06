package data.RDF;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Namespace {
	EXAMPLE("ex", "http://www.uec.ac.jp/example#"),
	RDF("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
	RDFS("rdfs", "http://www.w3.org/2000/01/rdf-schema#"),
	OWL("owl", "http://www.w3.org/2002/07/owl#"),
	DC("dc","http://purl.org/dc/elements/1.1/"),
	DCTERMS("dcterms", "http://purl.org/dc/terms/"),
	SCHEMA("schema", "http://schema.org/"),
	SIO("sio", "http://semanticscience.org/resource/"),
	
	JASS("jass", "http://www.uec.ac.jp/k-lab/k-tanabe/jass/"),
	MOS("mos", "http://www.uec.ac.jp/k-lab/k-tanabe/mos/"),
	
	GOO("goo", "http://dictionary.goo.ne.jp/jn#"),
	
	EMPTY("_", "_"),
	LITERAL("literal", ""),
	;
	
	private final String prefix;
	private final String uri;

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	private Namespace(String prefix, String uri) {
		this.prefix = prefix;
		this.uri = uri;
	}

	/****************************************/
	/**********   Static  Method   **********/
	/****************************************/
	public static Map<String, String> prefixMap(String... names) {
		return Stream.of(names).map(n -> valueOf(n))
				.collect(Collectors.toMap(ns -> ns.prefix, ns -> ns.uri.toString()));
	}
	public static String getURIFromPrefix(String prefix) {
		for (Namespace ns : values())
			if (ns.prefix.equals(prefix))
				return ns.uri.toString();
		return null;
	}
	public static Namespace getNamespaceFromPrefix(String prefix) {
		for (Namespace ns : values())
			if (ns.prefix.equals(prefix))
				return ns;
		return null;
	}
	public static Namespace getNamespaceFromURI(String uri) {
		if (uri == null) return EMPTY;
		for (Namespace ns : values())
			if (uri.startsWith(ns.uri.toString()))
				return ns;
		return EMPTY;
	}
	public static String getFragmentFromURI(String uri) {
		if (uri == null) return "";
		for (Namespace ns : values())
			if (uri.startsWith(ns.uri.toString()))
				return uri.replaceFirst(ns.uri.toString(), "");
		return uri;
	}
	
	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public String toQueryPrefixDefinition() {
		return "PREFIX "+ prefix +": <"+ uri +">";
	}

	/****************************************/
	/**********        Getter      **********/
	/****************************************/
	public String getPrefix() {
		return prefix;
	}
	public String getURI() {
		return uri;
	}
	
	
	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return prefix;
	}
}
package data.RDF;

import java.net.URI;
import java.net.URISyntaxException;
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
	
	JASS("jass", "http://www.uec.ac.jp/k-lab/k-tanabe/jass/"),
	
	GOO("goo", "http://dictionary.goo.ne.jp/jn#"),
	
	EMPTY("_", "_"),
	LITERAL("literal", ""),
	;
	
	private final String prefix;
	private URI uri;

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	private Namespace(String prefix, String uri) {
		this.prefix = prefix;
		try {
			this.uri = new URI(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/***********************************/
	/**********  StaticMethod **********/
	/***********************************/
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
		for (Namespace ns : values())
			if (uri.startsWith(ns.uri.toString()))
				return ns;
		return EMPTY;
	}
	public static String getFragmentFromURI(String uri) {
		for (Namespace ns : values())
			if (uri.startsWith(ns.uri.toString()))
				return uri.replaceFirst(ns.uri.toString(), "");
		return uri;
	}
	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public String toQueryPrefixDefinition() {
		return "PREFIX "+ prefix +": <"+ uri +">";
	}

	
	/**********************************/
	/**********    Getter    **********/
	/**********************************/
	public String getPrefix() {
		return prefix;
	}
	public URI getURI() {
		return uri;
	}
	
	
	/**********************************/
	/********** ObjectMethod **********/
	/**********************************/
	@Override
	public String toString() {
		return prefix;
	}
}
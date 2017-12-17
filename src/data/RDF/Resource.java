package data.RDF;

public class Resource {
	public static final Resource TYPE = new Resource(Namespace.RDF, "type");
	public static final Resource SUB_CLASS_OF = new Resource(Namespace.RDFS, "subClassOf"); 
	public static final Resource SUB_PROPERTY_OF = new Resource(Namespace.RDFS, "subPropertyOf"); 
	public static final Resource RANGE = new Resource(Namespace.RDFS, "range"); 
	public static final Resource DOMAIN = new Resource(Namespace.RDFS, "domain"); 
	public static final Resource VALUE = new Resource(Namespace.RDF, "value");
	
	public static final Resource EQUIVALENT_CLASS = new Resource(Namespace.OWL, "equivalentClass");
	
	public static final Resource ALTER_NAME = new Resource(Namespace.SCHEMA, "alternateName");
	public static final Resource ACTION = new Resource(Namespace.SCHEMA, "Action");
	public static final Resource AGENT = new Resource(Namespace.SCHEMA, "agent");
	public static final Resource OBJECT = new Resource(Namespace.SCHEMA, "object");
	
	public static final Resource LENGTH = new Resource(Namespace.EXAMPLE, "hasLength");
	public static final Resource WEIGHT = new Resource(Namespace.EXAMPLE, "hasWeight");
	public static final Resource UNITS = new Resource(Namespace.EXAMPLE, "units");
	
	
	/**
	 * 名前空間.
	 */
	private Namespace namespace;
	/**
	 * 識別名.
	 * コロン(:)を含んではいけない．
	 */
	private String fragment;
	
	/* Constractor */
	public Resource(Namespace namespace, String fragment) {
		this.namespace = namespace;
		this.fragment = fragment;
	}
	public Resource(String fragment) {
		this(Namespace.LITERAL, fragment);
	}
	
	public Namespace getNamespace() {
		return namespace;
	}
	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}
	public String getFragment() {
		return fragment;
	}
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}
	
	public String uri() {
		return namespace == Namespace.LITERAL ?
				"\"" + fragment + "\"" :
				namespace.getURI() + fragment;
	}
	
	@Override
	public String toString() {
		return namespace == Namespace.LITERAL ? 
				"\"" + fragment + "\"":
				namespace + ":" + fragment;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fragment == null) ? 0 : fragment.hashCode());
		result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (fragment == null) {
			if (other.fragment != null)
				return false;
		} else if (!fragment.equals(other.fragment))
			return false;
		if (namespace != other.namespace)
			return false;
		return true;
	}
}
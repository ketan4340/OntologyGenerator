package data.original;

public class MyResource {
	public static final MyResource TYPE = new MyResource(Namespace.RDF, "type");
	public static final MyResource SUB_CLASS_OF = new MyResource(Namespace.RDFS, "subClassOf"); 
	public static final MyResource SUB_PROPERTY_OF = new MyResource(Namespace.RDFS, "subPropertyOf"); 
	public static final MyResource RANGE = new MyResource(Namespace.RDFS, "range"); 
	public static final MyResource DOMAIN = new MyResource(Namespace.RDFS, "domain"); 
	public static final MyResource VALUE = new MyResource(Namespace.RDF, "value");
	
	public static final MyResource EQUIVALENT_CLASS = new MyResource(Namespace.OWL, "equivalentClass");
	
	public static final MyResource ALTER_NAME = new MyResource(Namespace.SCHEMA, "alternateName");
	public static final MyResource ACTION = new MyResource(Namespace.SCHEMA, "Action");
	public static final MyResource AGENT = new MyResource(Namespace.SCHEMA, "agent");
	public static final MyResource OBJECT = new MyResource(Namespace.SCHEMA, "object");
	
	public static final MyResource LENGTH = new MyResource(Namespace.EXAMPLE, "hasLength");
	public static final MyResource WEIGHT = new MyResource(Namespace.EXAMPLE, "hasWeight");
	public static final MyResource UNITS = new MyResource(Namespace.EXAMPLE, "units");
	
	public static final MyResource NO_OBJECT = new MyResource(Namespace.EXAMPLE, "no_object");
	
	
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
	public MyResource(Namespace namespace, String fragment) {
		this.namespace = namespace;
		this.fragment = fragment;
	}
	public MyResource(String fragment) {
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
		MyResource other = (MyResource) obj;
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
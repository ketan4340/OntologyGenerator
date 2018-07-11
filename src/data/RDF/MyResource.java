package data.RDF;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class MyResource {
	/**
	 * 名前空間.
	 */
	private Namespace namespace;
	/**
	 * 識別名.
	 * コロン(:)を含んではいけない．
	 */
	private String fragment;


	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public MyResource(Namespace namespace, String fragment) {
		this.namespace = namespace;
		this.fragment = fragment;
	}

	public MyResource(String uri) {
		this(Namespace.getNamespaceFromURI(uri), Namespace.getFragmentFromURI(uri));
	}

	public MyResource(Resource jenaResource) {
		this(jenaResource.getURI());
	}
	public MyResource(Property jenaProperty) {
		this(jenaProperty.getURI());
	}
	public MyResource(RDFNode jenaRDFNode) {
		this(jenaRDFNode.toString());
	}


	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public String uri() {
		return namespace == Namespace.LITERAL ?
				"\"" + fragment + "\"" :
				namespace.getURI() + fragment;
	}


	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
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


	/**********************************/
	/********** ObjectMethod **********/
	/**********************************/
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
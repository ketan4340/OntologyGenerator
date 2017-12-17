package data.RDF;

public class RDFTriple {
	public static final int S = 0;
	public static final int P = 1;
	public static final int O = 2;
	
	private Resource subject;
	private Resource predicate;
	private Resource object;
	
	public RDFTriple(Resource subject, Resource predicate, Resource object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	public RDFTriple(String[] spo) {
		this(new Resource(Namespace.EXAMPLE, spo[0]), new Resource(Namespace.EXAMPLE, spo[1]), new Resource(Namespace.EXAMPLE, spo[2]));
	}
	
	public RDFTriple(Resource resource, Resource property, String literal) {
		//TODO 目的語がリテラルのRDFトリプル
	}
	
	/* Getter */
	public Resource getSubject() {
		return subject;
	}
	public Resource getPredicate() {
		return predicate;
	}
	public Resource getObject() {
		return object;
	}

	/**
	 * 主語，述語，目的語の順に並ぶResourceの配列を返す.
	 * @return トリプルの配列
	 */
	public Resource[] toArray() {
		return new Resource[]{subject, predicate, object};
	}
	
	@Override
	public String toString() {
		return subject + ", " + predicate + ", " + object;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		RDFTriple other = (RDFTriple) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}	
}
package data.original;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Ontology {
	private List<RDFTriple> triples;
	
	public Ontology(ArrayList<RDFTriple> triples) {
		this.triples = triples;
	}
	public Ontology(Collection<? extends RDFTriple> triples) {
		this(new ArrayList<>(triples));
	}
	public Ontology() {
		this(new ArrayList<>());
	}
	
	
	/* Getter/Setter */
	public List<RDFTriple> getTriples() {
		return triples;
	}
	public void setTriples(List<RDFTriple> triples) {
		this.triples = triples;
	}
	
	
}
package data.RDF;

public enum RDFSerialize {
	RDF_XML("RDF/XML", ".rdf"),
	N_Triples("N-Triples", ".nt"),
	Turtle("Turtle", ".ttl"),
	JSON_LD("JSON-LD", "."),
	;
	
	private final String label;
	private final String extension;
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	private RDFSerialize(String label, String extension) {
		this.label = label;
		this.extension = extension;
	}
	

	/****************************************/
	/**********        Getter      **********/
	/****************************************/
	public String getLabel() {
		return label;
	}
	public String getExtension() {
		return extension;
	}
	
	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return label+"("+extension+")";
	}
	
}
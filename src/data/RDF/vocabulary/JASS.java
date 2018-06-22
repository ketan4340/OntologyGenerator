package data.RDF.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;

public class JASS {

	public static final String uri = "http://www.uec.ac.jp/k-lab/k-tanabe/jass/";
	
	public static final String namespacePrefix = "jass";
	
	public static final Resource Paragraph = Init.Paragraph();
	public static final Resource Sentence = Init.Sentence();
	public static final Resource Clause = Init.Clause();
	public static final Resource SingleClause = Init.SingleClause();
	public static final Resource SerialClause = Init.SerialClause();
	public static final Resource Word = Init.Word();
	public static final Resource Phrase = Init.Phrase();
	public static final Resource Categorem = Init.Categorem();
	public static final Resource Adjunct = Init.Adjunct();
	public static final Resource Concept = Init.Concept();
	public static final Resource Morpheme = Init.Morpheme();
	
	//public static final Property 
	
	public static class Init {
	
		public static Resource Paragraph() {
			return new ResourceImpl(uri, "Paragraph");
		}
		public static Resource Sentence() {
			return new ResourceImpl(uri, "Sentence");
		}
		public static Resource Clause() {
			return new ResourceImpl(uri, "Clause");
		}
		public static Resource SingleClause() {
			return new ResourceImpl(uri, "SingleClause");
		}
		public static Resource SerialClause() {
			return new ResourceImpl(uri, "SerialClause");
		}
		public static Resource Word() {
			return new ResourceImpl(uri, "Word");
		}
		public static Resource Phrase() {
			return new ResourceImpl(uri, "Phrase");
		}
		public static Resource Categorem() {
			return new ResourceImpl(uri, "Categorem");
		}
		public static Resource Adjunct() {
			return new ResourceImpl(uri, "Adjunct");
		}
		public static Resource Concept() {
			return new ResourceImpl(uri, "Concept");
		}
		public static Resource Morpheme() {
			return new ResourceImpl(uri, "Morpheme");
		}
		
		public static Property pos() {
			return new PropertyImpl(uri, "pos");
		}
	}
	
	
}
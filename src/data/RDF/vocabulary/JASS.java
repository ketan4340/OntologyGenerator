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
	
	public static final Property name = Init.name();
	public static final Property pos = Init.pos();
	public static final Property mainPoS = Init.mainPoS();
	public static final Property subPoS1 = Init.subPoS1();
	public static final Property subPoS2 = Init.subPoS2();
	public static final Property subPoS3 = Init.subPoS3();
	public static final Property inflection = Init.inflection();
	public static final Property conjugation = Init.conjugation();
	public static final Property infinitive = Init.infinitive();
	public static final Property kana = Init.kana();
	public static final Property pronunsiation = Init.pronunsiation();
	
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
		
		public static Property name() {
			return new PropertyImpl(uri, "name");
		}
		public static Property pos() {
			return new PropertyImpl(uri, "pos");
		}
		public static Property mainPoS() {
			return new PropertyImpl(uri, "mainPoS");
		}
		public static Property subPoS1() {
			return new PropertyImpl(uri, "subPoS1");
		}
		public static Property subPoS2() {
			return new PropertyImpl(uri, "subPoS2");
		}
		public static Property subPoS3() {
			return new PropertyImpl(uri, "subPoS3");
		}
		public static Property inflection() {
			return new PropertyImpl(uri, "infinitive");
		}
		public static Property conjugation() {
			return new PropertyImpl(uri, "conjugation");
		}
		public static Property infinitive() {
			return new PropertyImpl(uri, "infinitive");
		}
		public static Property kana() {
			return new PropertyImpl(uri, "kana");
		}
		public static Property pronunsiation() {
			return new PropertyImpl(uri, "pronunciation");
		}
		
	}
}
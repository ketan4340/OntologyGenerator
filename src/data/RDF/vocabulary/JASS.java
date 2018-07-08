package data.RDF.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;

public class JASS {

	public static final String uri = "http://www.uec.ac.jp/k-lab/k-tanabe/jass/";

	public static final String namespacePrefix = "jass";

	/****************************************/
	/**********        Class       **********/
	/****************************************/
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

	/****************************************/
	/**********  Object  Property  **********/
	/****************************************/
	public static final Property means = Init.means();
	public static final Property dependTo = Init.dependTo();
	public static final Property partOf = Init.partOf();
	public static final Property consistsOfSentences = Init.consistsOfSentences();
	public static final Property consistsOfClauses = Init.consistsOfClauses();
	public static final Property consistsOfWords = Init.consistsOfWords();
	public static final Property consistsOfMorphemes = Init.consistsOfMorphemes();
	public static final Property composesClause = Init.composesClause();
	public static final Property consistsOfCategorem = Init.consistsOfCategorem();
	public static final Property consistsOfAdjuncts = Init.consistsOfAdjuncts();
	public static final Property composesPhrase = Init.composesPhrase();
	public static final Property consistsOfHead = Init.consistsOfHead();
	public static final Property consistsOfDependent = Init.consistsOfDependent();
	public static final Property sentenceElement = Init.sentenceElement();
	public static final Property subject = Init.subject();
	public static final Property predicate = Init.predicate();
	public static final Property object = Init.object();

	/****************************************/
	/**********   Data  Property   **********/
	/****************************************/
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
		/****************************************/
		/**********        Class       **********/
		/****************************************/
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

		/****************************************/
		/**********  Object  Property  **********/
		/****************************************/
		public static final Property means() {
			return new PropertyImpl(uri, "means");
		}
		public static final Property dependTo() {
			return new PropertyImpl(uri, "dependTo");
		}
		public static final Property partOf() {
			return new PropertyImpl(uri, "partOf");
		}
		public static final Property consistsOfSentences() {
			return new PropertyImpl(uri, "consistsOfSentences");
		}
		public static final Property consistsOfClauses() {
			return new PropertyImpl(uri, "consistsOfClauses");
		}
		public static final Property consistsOfWords() {
			return new PropertyImpl(uri, "consistsOfWords");
		}
		public static final Property consistsOfMorphemes() {
			return new PropertyImpl(uri, "consistsOfMorphemes");
		}
		public static final Property composesClause() {
			return new PropertyImpl(uri, "composesClause");
		}
		public static final Property consistsOfCategorem() {
			return new PropertyImpl(uri, "consistsOfCategorem");
		}
		public static final Property consistsOfAdjuncts() {
			return new PropertyImpl(uri, "consistsOfAdjuncts");
		}
		public static final Property composesPhrase() {
			return new PropertyImpl(uri, "composesPhrase");
		}
		public static final Property consistsOfHead() {
			return new PropertyImpl(uri, "consistsOfHead");
		}
		public static final Property consistsOfDependent() {
			return new PropertyImpl(uri, "consistsOfDependent");
		}
		public static final Property sentenceElement() {
			return new PropertyImpl(uri, "sentenceElement");
		}
		public static final Property subject() {
			return new PropertyImpl(uri, "subject");
		}
		public static final Property predicate() {
			return new PropertyImpl(uri, "predicate");
		}
		public static final Property object() {
			return new PropertyImpl(uri, "object");
		}

		/****************************************/
		/**********   Data  Property   **********/
		/****************************************/
		public static final Property name() {
			return new PropertyImpl(uri, "name");
		}
		public static final Property pos() {
			return new PropertyImpl(uri, "pos");
		}
		public static final Property mainPoS() {
			return new PropertyImpl(uri, "mainPoS");
		}
		public static final Property subPoS1() {
			return new PropertyImpl(uri, "subPoS1");
		}
		public static final Property subPoS2() {
			return new PropertyImpl(uri, "subPoS2");
		}
		public static final Property subPoS3() {
			return new PropertyImpl(uri, "subPoS3");
		}
		public static final Property inflection() {
			return new PropertyImpl(uri, "inflection");
		}
		public static final Property conjugation() {
			return new PropertyImpl(uri, "conjugation");
		}
		public static final Property infinitive() {
			return new PropertyImpl(uri, "infinitive");
		}
		public static final Property kana() {
			return new PropertyImpl(uri, "kana");
		}
		public static final Property pronunsiation() {
			return new PropertyImpl(uri, "pronunciation");
		}
	}

	public String getURI() {return uri;}
}
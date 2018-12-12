package data.RDF.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class JASS {

	public static final String uri = "http://www.sw.cei.uec.ac.jp/k-lab/k-tanabe/jass#";

	
	public static final String getURI() {
		return uri;
	}
	
    protected static final Resource resource( String local ) {
    	return ResourceFactory.createResource( uri + local );
    }

    protected static final Property property( String local ) {
    	return ResourceFactory.createProperty( uri, local );
    }
    
	/* ================================================== */
	/* ====================== Class ===================== */
	/* ================================================== */
    public static final Resource Language = resource("Language");
    public static final Resource Writing = resource("Writing");
    public static final Resource Paragraph = resource("Paragraph");
	public static final Resource Sentence = resource("Sentence");
	public static final Resource Clause = resource("Clause");
	public static final Resource SingleClause =	resource("SingleClause");
	public static final Resource SerialClause =	resource("SerialClause");
	public static final Resource Word = resource("Word");
	public static final Resource Phrase = resource("Phrase");
	public static final Resource Categorem = resource("Categorem");
	public static final Resource Adjunct = resource("Adjunct");
	public static final Resource Morpheme = resource("Morpheme");
	public static final Resource PoS = resource("PoS");
	
	public static final Resource LanguageList = resource("LanguageList");
	public static final Resource ParagraphList = resource("ParagraphList");
	public static final Resource SentenceList = resource("SentenceList");
	public static final Resource ClauseList = resource("ClauseList");
	public static final Resource WordList = resource("WordList");
	public static final Resource AdjunctList = resource("AdjunctList");
	public static final Resource MorphemeList = resource("MorphemeList");
	
	public static final Resource Meaning = resource("Meaning");

	/* ================================================== */
	/* ================= Object Property ================ */
	/* ================================================== */
	public static final Property languageComponent = property("languageComponent");
	public static final Property paragraphs = property("paragraphs");
	public static final Property sentences = property("sentences");
	public static final Property clauses = property("clauses");
	public static final Property words = property("words");
	public static final Property morphemes = property("morphemes");
	public static final Property clauseComponent = property("clauseComponent");
	public static final Property categorem = property("categorem");
	public static final Property adjuncts = property("adjuncts");
	public static final Property phraseComponent = property("phraseComponent");
	public static final Property head = property("head");
	public static final Property dependent = property("dependent");
	
	public static final Property sentenceElement = property("sentenceElement");
	public static final Property subject = property("subject");
	public static final Property predicate = property("predicate");
	public static final Property object = property("object");
	
	public static final Property dependTo = property("dependTo");

	public static final Property means = property("means");
	public static final Property coreNode = property("coreNode");
	
	/* ================================================== */
	/* ================== Data Property ================= */
	/* ================================================== */
	public static final Property name = property("name");
	public static final Property pos = property("pos");
	public static final Property mainPoS = property("mainPoS");
	public static final Property subPoS1 = property("subPoS1");
	public static final Property subPoS2 = property("subPoS2");
	public static final Property subPoS3 = property("subPoS3");
	public static final Property inflection = property("inflection");
	public static final Property conjugation = property("conjugation");
	public static final Property infinitive = property("infinitive");
	public static final Property kana = property("kana");
	public static final Property pronunsiation = property("pronunciation");	

}

package modules.relationExtract;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import data.original.Namespace;
import grammar.Sentence;
import grammar.clause.AbstractClause;
import grammar.word.Word;

public class JASSFactory {
	private static final Model commonModel = ModelFactory.createDefaultModel();

	/********************************************/
	/********** JASS Schema Definition **********/
	/********************************************/
	/* クラスResource */
    private static final String PARAGRAPH = Namespace.JASS.getURI() + "Paragraph";
	private static final String SENTENCE = Namespace.JASS.getURI() + "Sentence";
	private static final String CLAUSE = Namespace.JASS.getURI() + "Clause";
	private static final String WORD = Namespace.JASS.getURI() + "Word";
	private static final String CONCEPT = Namespace.JASS.getURI() + "Concept";

	/* プロパティResource */
	/* 文用 */
	private static final String CONTAINS_CLAUSE = Namespace.JASS.getURI() + "containsClause";
	private static final String SUBJECT = Namespace.JASS.getURI() + "subject";
    private static final String PREDICATE = Namespace.JASS.getURI() + "predicate";
    private static final String OBJECT = Namespace.JASS.getURI() + "object";
    private static final String MODIFIER = Namespace.JASS.getURI() + "object";
    	/* 文節用 */
    private static final String CONTAINS_WORD = Namespace.JASS.getURI() + "containsWord";
    private static final String CATEGOREM = Namespace.JASS.getURI() + "categorem";
    private static final String ADJUNCT = Namespace.JASS.getURI() + "adjunct";
    private static final String DEPEND = Namespace.JASS.getURI() + "depend";
    	/* 単語用 */
    private static final String INFINITIVE = Namespace.JASS.getURI() + "infinitive";
    private static final String POS = Namespace.JASS.getURI() + "pos";
    private static final String MEANS = Namespace.JASS.getURI() + "means";


	public static Model createJASSModel(Sentence sentence) {
		return convertSentence2jass(createDefaultJASSModel(), sentence);
	}
	
	private static Model convertSentence2jass(Model model, Sentence sentence) {
		Resource sentenceR = model.createResource(Namespace.JASS.getURI()+"Stc"+sentence.id)
				.addProperty(RDF.type, model.getResource(SENTENCE));
		sentence.getChildren().forEach(c -> convertClause2jass(model, c, sentenceR));
		
		sentence.getChildren().forEach(c -> {
			AbstractClause<?> depc = c.getDepending();
			if (depc == null) return;
			Resource cR = model.getResource(Namespace.JASS.getURI()+"Cls"+c.id);
			Resource depcR = model.getResource(Namespace.JASS.getURI()+"Cls"+depc.id);
			cR.addProperty(model.getProperty(DEPEND), depcR);
		});
		
		return model;
	}
	
	private static Model convertClause2jass(Model model, AbstractClause<?> clause, Resource sentenceR) {
		Resource clauseR = model.createResource(Namespace.JASS.getURI()+"Cls"+clause.id)
				.addProperty(RDF.type, model.getResource(CLAUSE));
		
		sentenceR.addProperty(model.getProperty(CONTAINS_CLAUSE), clauseR);
		
		clause.getChildren().forEach(w -> convertWord2jass(model, w, clauseR));
		
		Resource categoremR = model.getResource(Namespace.JASS.getURI()+"Ctg"+clause.getCategorem().id);
		clauseR.addProperty(model.getProperty(CATEGOREM), categoremR);
		if (!clause.getAdjuncts().isEmpty()) {
			Resource adjunctR = model.getResource(Namespace.JASS.getURI()+"Adj"+clause.getAdjuncts().get(clause.getAdjuncts().size()-1).id);
			clauseR.addProperty(model.getProperty(ADJUNCT), adjunctR);			
		}
		return model;
	}

	private static Model convertWord2jass(Model model, Word word, Resource clauseR) {
		Resource wordR = model.createResource(Namespace.JASS.getURI()+"Ctg"+word.id)
				.addProperty(RDF.type, model.getResource(WORD))
				.addProperty(model.getProperty(INFINITIVE), model.createLiteral(word.infinitive()))
				.addProperty(model.getProperty(POS), model.createLiteral(word.mainPoS()))
				.addProperty(model.getProperty(MEANS), 
						model.createResource(Namespace.GOO.getURI() + word.name())
							.addProperty(RDF.type, model.getResource(CONCEPT)));

		clauseR.addProperty(model.getProperty(CONTAINS_WORD), wordR);
		return model;
	}


	private static Model createDefaultJASSModel() {
		Model defaultModel =ModelFactory.createDefaultModel();
		defaultModel.setNsPrefixes(Namespace.prefixMap("RDF", "RDFS", "OWL", "DC", "DCTERMS", "SCHEMA", "JASS", "GOO"));


		/* クラスResource */
		Resource Paragraph = defaultModel.createResource(PARAGRAPH).addProperty(RDF.type, RDFS.Class);
		Resource Sentence = defaultModel.createResource(SENTENCE).addProperty(RDF.type, RDFS.Class);
		Resource Clause = defaultModel.createResource(CLAUSE).addProperty(RDF.type, RDFS.Class);
		Resource Word = defaultModel.createResource(WORD).addProperty(RDF.type, RDFS.Class);
		Resource Concept = defaultModel.createResource(CONCEPT).addProperty(RDF.type, RDFS.Class);

		/* プロパティResource */
		/* 文用 */
		Property contains_clause = defaultModel.createProperty(CONTAINS_CLAUSE);
		contains_clause.addProperty(RDF.type, RDF.Property).addProperty(RDFS.domain, Sentence).addProperty(RDFS.range, Clause);
		Property subject = defaultModel.createProperty(SUBJECT);
		subject.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, contains_clause);
		Property predicate = defaultModel.createProperty(PREDICATE);
		predicate.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, contains_clause);
		Property object = defaultModel.createProperty(OBJECT);
		object.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, contains_clause);
		/* 文節用 */
		Property contains_word = defaultModel.createProperty(CONTAINS_WORD);
		contains_word.addProperty(RDF.type, RDF.Property).addProperty(RDFS.domain, Clause).addProperty(RDFS.range, Word);
		Property categorem = defaultModel.createProperty(CATEGOREM);
		categorem.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, contains_word);
		Property adjunct = defaultModel.createProperty(ADJUNCT);
		adjunct.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, contains_word);
		Property depend = defaultModel.createProperty(DEPEND);
		depend.addProperty(RDF.type, RDF.Property).addProperty(RDFS.domain, Clause).addProperty(RDFS.range, Clause);
		/* 単語用 */
		Property infinitive = defaultModel.createProperty(INFINITIVE);
		infinitive.addProperty(RDF.type, RDF.Property).addProperty(RDFS.domain, Word).addProperty(RDFS.range, RDFS.Literal);
		Property pos = defaultModel.createProperty(POS);
		pos.addProperty(RDF.type, RDF.Property).addProperty(RDFS.domain, Word).addProperty(RDFS.range, RDFS.Literal);
		Property means = defaultModel.createProperty(MEANS);
		means.addProperty(RDF.type, RDF.Property).addProperty(RDFS.domain, Word).addProperty(RDFS.range, Concept);
		
		return defaultModel;
	}
}

package modules.relationExtract;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.Namespace;
import data.RDF.vocabulary.JASS;
import grammar.Concept;
import grammar.Sentence;
import grammar.clause.Clause;
import grammar.morpheme.Morpheme;
import grammar.word.Word;

public class JASSFactory {
	private static final String JASS_ONTOLOGY_URL = "../OntologyGenerator/resource/ontology/SyntaxOntology.owl";

	/********************************************/
	/********** JASS Schema Definition **********/
	/********************************************/
	/* プロパティResource */
	/* 文用 */
	private static final String CONTAINS_CLAUSE = Namespace.JASS.getURI() + "containsClause";
	private static final String CLAUSE_LIST = Namespace.JASS.getURI() + "clauseList";
	
    /* 文節用 */
    private static final String CONTAINS_WORD = Namespace.JASS.getURI() + "containsWord";
    private static final String CATEGOREM = Namespace.JASS.getURI() + "categorem";
    private static final String ADJUNCT = Namespace.JASS.getURI() + "adjunct";
    private static final String DEPEND = Namespace.JASS.getURI() + "depend";
    private static final String NEXT_CLAUSE = Namespace.JASS.getURI() + "nextClause";
    /* 単語用 */
    private static final String INFINITIVE = Namespace.JASS.getURI() + "infinitive";
    private static final String POS = Namespace.JASS.getURI() + "pos";
    private static final String MEANS = Namespace.JASS.getURI() + "means";
    private static final String MORPHEME_LIST = Namespace.JASS.getURI() + "morphemeList";


	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public static Model createJASSModel(Sentence sentence) {
		return sentence2jass(createDefaultJASSModel(), sentence);
	}
	
	private static Model sentence2jass(Model model, Sentence sentence) {
		Resource sentenceR = model.createResource(Namespace.JASS.getURI()+"Stc"+sentence.id)
				.addProperty(RDF.type, JASS.Sentence);
		
		Resource clauseNode = model.createResource();
		sentenceR.addProperty(model.getProperty(CLAUSE_LIST), clauseNode); 
		
		sentence.getChildren().forEach(c -> clause2jass(model, c, sentenceR));
		
		for (Clause<?> c : sentence.getChildren()) {

			Clause<?> depc = c.getDepending();
			Clause<?> nextc = sentence.nextChild(c);
			Resource cR = model.getResource(Namespace.JASS.getURI()+"Cls"+c.id);

			if (depc != null) {
			Resource depcR = model.getResource(Namespace.JASS.getURI()+"Cls"+depc.id);
			cR.addProperty(model.getProperty(DEPEND), depcR);
			}
			if (nextc != null) {
			Resource nextcR = model.getResource(Namespace.JASS.getURI()+"Cls"+nextc.id);
			cR.addProperty(model.getProperty(NEXT_CLAUSE), nextcR);
			}
			Resource nextClauseNode = model.createResource();
			clauseNode.addProperty(RDF.first, cR)
				.addProperty(RDF.rest, nextClauseNode);
			clauseNode = nextClauseNode;
		}
		clauseNode.addProperty(RDF.rest, RDF.nil);
		
		return model;
	}
	
	private static Model clause2jass(Model model, Clause<?> clause, Resource sentenceR) {
		Resource clauseR = model.createResource(Namespace.JASS.getURI()+"Cls"+clause.id)
				.addProperty(RDF.type, JASS.Clause);
		
		sentenceR.addProperty(model.getProperty(CONTAINS_CLAUSE), clauseR);
		
		clause.getChildren().forEach(w -> word2jass(model, w, clauseR));
		
		Resource categoremR = model.getResource(Namespace.JASS.getURI()+"Wrd"+clause.getCategorem().id);
		clauseR.addProperty(model.getProperty(CATEGOREM), categoremR);
		if (!clause.getAdjuncts().isEmpty()) {
			Resource adjunctR = model.getResource(Namespace.JASS.getURI()+"Wrd"+clause.getAdjuncts().get(clause.getAdjuncts().size()-1).id);
			clauseR.addProperty(model.getProperty(ADJUNCT), adjunctR);			
		}
		return model;
	}

	private static Model word2jass(Model model, Word word, Resource clauseR) {	
		Resource wordR = model.createResource(Namespace.JASS.getURI()+"Wrd"+word.id)
				.addProperty(RDF.type, JASS.Word)
				.addProperty(model.getProperty(INFINITIVE), model.createLiteral(word.infinitive()))
				.addProperty(model.getProperty(POS), model.createLiteral(word.mainPoS()))
				.addProperty(model.getProperty(POS), model.createLiteral(word.subPoS1()))
				.addProperty(model.getProperty(POS), model.createLiteral(word.subPoS2()));

		concept2jass(model, word.getConcept(), wordR);
		clauseR.addProperty(model.getProperty(CONTAINS_WORD), wordR);
		return model;
	}
	
	private static Model concept2jass(Model model, Concept concept, Resource wordR) {
		Resource conceptR =
				model.createResource(Namespace.GOO.getURI() + concept.name())
					.addProperty(RDF.type, JASS.Concept);

		Resource morphemeNode = model.createResource();
		wordR.addProperty(model.getProperty(MORPHEME_LIST), morphemeNode);

		for (Morpheme m : concept.getMorphemes()) {
			Resource mrpR = model.getResource(Namespace.JASS.getURI()+"Mrp"+m.id);
			Resource nextMorphemeNode = model.createResource();
			morphemeNode.addProperty(RDF.type, RDF.List)
				.addProperty(RDF.first, mrpR)
				.addProperty(RDF.rest, nextMorphemeNode);
			morphemeNode = nextMorphemeNode;
		}
		
		wordR.addProperty(model.getProperty(MEANS), conceptR);
		return model;
	}


	private static Model createDefaultJASSModel() {
		Model defaultModel = ModelFactory.createDefaultModel();
		defaultModel.setNsPrefixes(Namespace.prefixMap("RDF", "RDFS", "OWL", "DC", "DCTERMS", "SCHEMA", "JASS", "GOO"));
		defaultModel.read(JASS_ONTOLOGY_URL, "RDF/XML");

		return defaultModel;
	}
}

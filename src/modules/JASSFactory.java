package modules;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import data.original.Namespace;
import grammar.Sentence;
import grammar.clause.AbstractClause;
import grammar.word.Adjunct;
import grammar.word.Word;

public class JASSFactory {
	private static final Model commonModel = ModelFactory.createDefaultModel();
	private static boolean isReady = false;
	
	/********************************************/
	/********** JASS Schema Definition **********/
	/********************************************/
	/* クラスResource */
    	private static final Resource PARAGRAPH = commonModel.createResource(Namespace.JASS.getURI() + "Paragraph");
	private static final Resource SENTENCE = commonModel.createResource(Namespace.JASS.getURI() + "Sentence");
	private static final Resource CLAUSE = commonModel.createResource(Namespace.JASS.getURI() + "Clause");
	private static final Resource WORD = commonModel.createResource(Namespace.JASS.getURI() + "Word");
	private static final Resource PoS = commonModel.createResource(Namespace.JASS.getURI() + "PoS");
	
	/* プロパティResource */
		/* 文用 */
	private static final Property CONTAINS_CLAUSE = commonModel.createProperty(Namespace.JASS.getURI() + "containsClause");
	private static final Property SUBJECT = commonModel.createProperty(Namespace.JASS.getURI() + "subject");
    	private static final Property PREDICATE = commonModel.createProperty(Namespace.JASS.getURI() + "subject");
    	private static final Property MODIFIER = commonModel.createProperty(Namespace.JASS.getURI() + "subject");
    		/* 文節用 */
    	private static final Property CONTAINS_WORD = commonModel.createProperty(Namespace.JASS.getURI() + "containsWord");    	
    	private static final Property CATEGOREM = commonModel.createProperty(Namespace.JASS.getURI() + "categorem");
    	private static final Property ADJUNCT = commonModel.createProperty(Namespace.JASS.getURI() + "adjunct");
    		/* 単語用 */
    	private static final Property INFINITIVE = commonModel.createProperty(Namespace.JASS.getURI() + "infinitive");
    	private static final Property POS = commonModel.createProperty(Namespace.JASS.getURI() + "pos");
    	    	
    	
	public static Model createJASSModel(Sentence sentence) {
		Model jassModel = createDefaultJASSModel();
		
		// 文
		Resource sentenceR = jassModel.createResource("Stc"+sentence.id)
		.addProperty(RDF.type, SENTENCE);
			
		// 文節
		for (AbstractClause<?> clause : sentence.getChildren()) {
			Resource clauseR = jassModel.createResource("Cls"+clause.id);
			
			clauseR.addProperty(RDF.type, CLAUSE);
			sentenceR.addProperty(CONTAINS_CLAUSE, clauseR);
			
			// 単語
			Word categorem = clause.getCategorem();
			Resource categoremR = jassModel.createResource("Ctg"+categorem.id)
					.addProperty(RDF.type, WORD)
					.addProperty(INFINITIVE, jassModel.createResource("inf"+categorem.id))
					.addProperty(POS, jassModel.createResource("pos"+categorem.id));
			clauseR.addProperty(CATEGOREM, categoremR).addProperty(CONTAINS_WORD, categoremR);
			
			if (!clause.getAdjuncts().isEmpty()) {
				Adjunct adjunct = clause.getAdjuncts().get(clause.getAdjuncts().size() - 1);
				Resource adjunctR = jassModel.createResource("Ajc" + adjunct.id).addProperty(RDF.type, WORD)
						.addProperty(INFINITIVE, jassModel.createResource("inf" + adjunct.id))
						.addProperty(POS, jassModel.createResource("pos" + adjunct.id));
				clauseR.addProperty(ADJUNCT, adjunctR).addProperty(CONTAINS_WORD, ADJUNCT);
			}
		}

		return jassModel;
	}
	
	public static boolean commonModelInit() {
		commonModel.setNsPrefixes(Namespace.prefixMap("RDF", "RDFS", "OWL", "DC", "DCTERMS", "SCHEMA", "JASS", "GOO"));
		
		SENTENCE.addProperty(RDF.type, RDFS.Class);
		CLAUSE.addProperty(RDF.type, RDFS.Class);
		WORD.addProperty(RDF.type, RDFS.Class);
				
		CONTAINS_CLAUSE.addProperty(RDF.type, RDF.Property);
		SUBJECT.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, CONTAINS_CLAUSE);
		PREDICATE.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, CONTAINS_CLAUSE);
		MODIFIER.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, CONTAINS_CLAUSE);

		CONTAINS_WORD.addProperty(RDF.type, RDF.Property);
		CATEGOREM.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, CONTAINS_WORD);
		ADJUNCT.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, CONTAINS_WORD);
		
		return isReady = true;
	}
	
	private static Model createDefaultJASSModel() {
		return isReady? ModelFactory.createDefaultModel().add(commonModel)
				: ModelFactory.createDefaultModel();
	}
}

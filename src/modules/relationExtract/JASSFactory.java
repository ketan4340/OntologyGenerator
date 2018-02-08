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
import grammar.clause.Clause;
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
    private static final Property PREDICATE = commonModel.createProperty(Namespace.JASS.getURI() + "predicate");
    private static final Property MODIFIER = commonModel.createProperty(Namespace.JASS.getURI() + "object");
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
				.addProperty(RDF.type, jassModel.getResource(Namespace.JASS.getURI() + "Sentence"));


		// 文節
		for (AbstractClause<?> clause : sentence.getChildren()) {
			Resource clauseR = jassModel.createResource("Cls"+clause.id)
					.addProperty(RDF.type, jassModel.getResource(Namespace.JASS.getURI() + "Clause"));
			sentenceR.addProperty(CONTAINS_CLAUSE, clauseR);

			// 単語
				// 自立語
			Word categorem = clause.getCategorem();
			Resource categoremR = jassModel.createResource("Ctg"+categorem.id)
					.addProperty(RDF.type, jassModel.getResource(Namespace.JASS.getURI() + "Word"))
					.addProperty(jassModel.getProperty(Namespace.JASS.getURI() + "infinitive"), jassModel.createResource("inf"+categorem.id))
					.addProperty(jassModel.getProperty(Namespace.JASS.getURI() + "pos"), categorem.mainPoS());
			clauseR.addProperty(jassModel.getProperty(Namespace.JASS.getURI() + "categorem"), categoremR)
					.addProperty(jassModel.getProperty(Namespace.JASS.getURI() + "contains_word"), categoremR);
				// 付属語
			if (!clause.getAdjuncts().isEmpty()) {
				Adjunct adjunct = clause.getAdjuncts().get(clause.getAdjuncts().size() - 1);
				Resource adjunctR = jassModel.createResource("Ajc" + adjunct.id)
						.addProperty(RDF.type, jassModel.getResource(Namespace.JASS.getURI() + "Word"))
						.addProperty(jassModel.getProperty(Namespace.JASS.getURI() + "infinitive"), jassModel.createResource("inf" + adjunct.id))
						.addProperty(jassModel.getProperty(Namespace.JASS.getURI() + "pos"), adjunct.mainPoS());
				clauseR.addProperty(jassModel.getProperty(Namespace.JASS.getURI() + "adjunct"), adjunctR)
						.addProperty(jassModel.getProperty(Namespace.JASS.getURI() + "contains_word"), adjunctR);

				// 主語と述語
				if (!sentenceR.hasProperty(jassModel.getProperty(Namespace.JASS.getURI() + "subject"))) {
					if (adjunct.infinitive().equals("は") && adjunct.subPoS1().equals("係助詞")) {
						sentenceR.addProperty(jassModel.getProperty(Namespace.JASS.getURI() + "subject"), clauseR);
						if (clause.getDepending() != Clause.ROOT && clause.getDepending() != null) {
							sentenceR.addProperty(jassModel.getProperty(Namespace.JASS.getURI() + "predicate"),
									jassModel.createResource("Cls"+clause.getDepending().id)
											.addProperty(RDF.type, jassModel.getResource(Namespace.JASS.getURI() + "Clause")));
						}
					}
				}
			}


		}

		return polishJASSModel(jassModel);
	}

	private static Model polishJASSModel(Model jassModel) {
		//TODO
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
		Model defaultModel =ModelFactory.createDefaultModel();
		defaultModel.setNsPrefixes(Namespace.prefixMap("RDF", "RDFS", "OWL", "DC", "DCTERMS", "SCHEMA", "JASS", "GOO"));


		/* クラスResource */
		Resource Paragraph = defaultModel.createResource(Namespace.JASS.getURI() + "Paragraph").addProperty(RDF.type, RDFS.Class);
		Resource Sentence = defaultModel.createResource(Namespace.JASS.getURI() + "Sentence").addProperty(RDF.type, RDFS.Class);
		Resource Clause = defaultModel.createResource(Namespace.JASS.getURI() + "Clause").addProperty(RDF.type, RDFS.Class);
		Resource Word = defaultModel.createResource(Namespace.JASS.getURI() + "Word").addProperty(RDF.type, RDFS.Class);
		Resource Pos = defaultModel.createResource(Namespace.JASS.getURI() + "PoS").addProperty(RDF.type, RDFS.Class);

		/* プロパティResource */
		/* 文用 */
		Property contains_clause = defaultModel.createProperty(Namespace.JASS.getURI() + "containsClause");
		contains_clause.addProperty(RDF.type, RDF.Property).addProperty(RDFS.domain, Sentence).addProperty(RDFS.range, Clause);
		Property subject = defaultModel.createProperty(Namespace.JASS.getURI() + "subject");
		subject.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, contains_clause);
		Property predicate = defaultModel.createProperty(Namespace.JASS.getURI() + "predicate");
		predicate.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, contains_clause);
		Property object = defaultModel.createProperty(Namespace.JASS.getURI() + "object");
		object.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, contains_clause);
		/* 文節用 */
		Property contains_word = defaultModel.createProperty(Namespace.JASS.getURI() + "containsWord");
		contains_word.addProperty(RDF.type, RDF.Property).addProperty(RDFS.domain, Clause).addProperty(RDFS.range, Word);
		Property categorem = defaultModel.createProperty(Namespace.JASS.getURI() + "categorem");
		categorem.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, contains_word);
		Property adjunct = defaultModel.createProperty(Namespace.JASS.getURI() + "adjunct");
		adjunct.addProperty(RDF.type, RDF.Property).addProperty(RDFS.subPropertyOf, contains_word);
		/* 単語用 */
		Property infinitive = defaultModel.createProperty(Namespace.JASS.getURI() + "infinitive");
		infinitive.addProperty(RDF.type, RDF.Property).addProperty(RDFS.domain, Word).addProperty(RDFS.range, RDFS.Literal);
		Property pos = defaultModel.createProperty(Namespace.JASS.getURI() + "pos");
		pos.addProperty(RDF.type, RDF.Property).addProperty(RDFS.domain, Word).addProperty(RDFS.range, RDFS.Literal);

		return defaultModel;
	}
}

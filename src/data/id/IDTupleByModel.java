package data.id;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import data.RDF.rule.RDFRule;
import grammar.sentence.Sentence;

public class IDTupleByModel extends Tuple implements IDTuple {
	private static final long serialVersionUID = -7084738399282724525L;

	private static final int SIZE 				= 7;

	private static final int LONGSENTENCE_ID	= 0;
	private static final int LONGSENTENCE		= 1;
	private static final int SHORTSENTENCE_ID	= 2;
	private static final int SHORTSENTENCE		= 3;
	private static final int RDFRULE_ID 		= 4;
	private static final int MODEL	 			= 5;
	private static final int SCORE 				= 6;

	protected static IDTuple ATTRIBUTES = new IDTupleByModel(
			"Long Sentence ID",
			"Long Sentence",
			"Short Sentence ID",
			"Short Sentence",
			"RDFRule ID",
			"Triples", 
			"Score");

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public IDTupleByModel(String initValue) {
		super(SIZE, initValue);
	}
	public IDTupleByModel(String longSentenceID, String longSentence,
			String shortSentenceID, String shortSentence,
			String rdfRuleID, String triple,  String score) {
		super(	longSentenceID,
				longSentence,
				shortSentenceID,
				shortSentence,
				rdfRuleID,
				triple,
				score
				);
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */

	/* ================================================== */
	/* ================= Interface Method =============== */
	/* ================================================== */
	@Override
	public String primaryKey() {
		return getLongSentenceID();
	}
	@Override
	public String secondaryKey() {
		return getShortSentenceID();
	}
	@Override
	public void setLongSentence(Sentence longSentence) {
		setLongSentence(longSentence.name());
		setLongSentenceID(String.valueOf(longSentence.id()));
	}
	@Override
	public void setShortSentence(Sentence shortSentence) {
		setShortSentence(shortSentence.name());
		setShortSentenceID(String.valueOf(shortSentence.id()));
	}
	@Override
	public void setRDFRuleID(RDFRule rule) {
		setRDFRuleID(String.valueOf(rule.id()));
	}
	@Override
	public void setStatement(Statement stmt) {
		setModel(stmt.toString());
	}
	@Override
	public void setModel(Model model) {
		try (StringWriter sr = new StringWriter()) {
			model.write(sr, "TURTLE");
			setModel('"'+sr.toString().replaceAll(",", "\",\"")
					.replaceAll("@prefix.*\n", "")
					.replaceAll("\"", "\"\"")
					.replaceAll("\t", "  ")
					.replaceAll("^\n|\n$", "")
					.replaceAll("\n\n", "\n")
					+'"');
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	/* ================= Getter, Setter ================= */
	public String getLongSentenceID() { return get(LONGSENTENCE_ID); }
	public void setLongSentenceID(String longSentenceID) { set(LONGSENTENCE_ID, longSentenceID); }
	public String getLongSentence() { return get(LONGSENTENCE); }
	public void setLongSentence(String longSentence) { set(LONGSENTENCE, longSentence); }
	public String getShortSentenceID() { return get(SHORTSENTENCE_ID); }
	public void setShortSentenceID(String shortSentenceID) { set(SHORTSENTENCE_ID, shortSentenceID); }
	public String getShortSentence() { return get(SHORTSENTENCE); }
	public void setShortSentence(String shortSentence) { set(SHORTSENTENCE, shortSentence); }
	public String getRDFRuleID() { return get(RDFRULE_ID); }
	public void setRDFRuleID(String rdfRuleID) { set(RDFRULE_ID, rdfRuleID); }
	public String getModel() { return get(MODEL); }
	public void setModel(String model) { set(MODEL, model); }
	public String getScore() { return get(SCORE); }
	public void setScore(String score) { set(SCORE, (score)); }


	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public IDTupleByModel clone() {
		return new IDTupleByModel(
				getLongSentenceID(),
				getLongSentence(),
				getShortSentenceID(),
				getShortSentence(),
				getRDFRuleID(),
				getModel(),
				getScore());
	}

}

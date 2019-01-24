package data.id;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import data.RDF.rule.RDFRule;
import grammar.sentence.Sentence;
import util.RDFUtil;

public class IDTupleByStatement extends Tuple implements IDTuple {
	private static final long serialVersionUID = -3501992033602856621L;

	private static final int SIZE 				= 9;

	private static final int LONGSENTENCE_ID	= 0;
	private static final int LONGSENTENCE		= 1;
	private static final int SHORTSENTENCE_ID	= 2;
	private static final int SHORTSENTENCE		= 3;
	private static final int RDFRULE_ID 		= 4;
	private static final int SUBJECT			= 5;
	private static final int PREDICATE 			= 6;
	private static final int OBJECT 			= 7;
	private static final int SCORE 				= 8;

	protected static IDTuple ATTRIBUTES = new IDTupleByStatement(
			"Long Sentence ID",
			"Long Sentence",
			"Short Sentence ID",
			"Short Sentence",
			"RDFRule ID",
			"Subject",
			"Predicate",
			"Object",
			"Score");

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public IDTupleByStatement(String initValue) {
		super(SIZE, initValue);
	}
	public IDTupleByStatement(String longSentenceID, String longSentence,
			String shortSentenceID, String shortSentence,
			String rdfRuleID,
			String subject, String predicate, String object,  String score) {
		super(	longSentenceID,
				longSentence,
				shortSentenceID,
				shortSentence,
				rdfRuleID,
				subject,
				predicate,
				object,
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
		setSubject(RDFUtil.toResourceStringAsQName(stmt.getSubject()));
		setPredicate(RDFUtil.toResourceStringAsQName(stmt.getPredicate()));
		setObject(RDFUtil.toResourceStringAsQName(stmt.getObject()));
	}
	@Override
	public void setModel(Model model) {
		try (StringWriter sr = new StringWriter()) {
			model.write(sr, "N-TRIPLE");
			setSubject('"'+sr.toString().replaceAll(",", "\",\"")
					.replaceAll("@prefix.*\n", "")+'"');	// "はセル内改行のため	
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
	public String getSubject() { return get(SUBJECT); }
	public void setSubject(String subject) { set(SUBJECT, subject); }
	public String getPredicate() { return get(PREDICATE); }
	public void setPredicate(String predicate) { set(PREDICATE, predicate); }
	public String getObject() { return get(OBJECT); }
	public void setObject(String object) { set(OBJECT, object); }
	public String getScore() { return get(SCORE); }
	public void setScore(String score) { set(SCORE, (score)); }



	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public IDTupleByStatement clone() {
		return new IDTupleByStatement(
				getLongSentenceID(),
				getLongSentence(),
				getShortSentenceID(),
				getShortSentence(),
				getRDFRuleID(),
				getSubject(),
				getPredicate(),
				getObject(),
				getScore());
	}

}

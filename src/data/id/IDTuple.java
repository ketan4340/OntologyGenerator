package data.id;

import util.tuple.Tuple;

public class IDTuple extends Tuple {
	private static final long serialVersionUID = -3501992033602856621L;

	private static final int SIZE 				= 10;

	private static final int LONGSENTENCE_ID	= 0;
	private static final int LONGSENTENCE		= 1;
	private static final int SHORTSENTENCE_ID	= 2;
	private static final int SHORTSENTENCE		= 3;
	private static final int RDFRULE_ID 		= 4;
	private static final int TRIPLE_ID 			= 5;
	private static final int SUBJECT			= 6;
	private static final int PREDICATE 			= 7;
	private static final int OBJECT 			= 8;
	private static final int SCORE 				= 9;

	protected static Tuple ATTRIBUTES = new Tuple(
			"Long Sentence ID",
			"Long Sentence",
			"Short Sentence ID",
			"Short Sentence",
			"RDFRule ID",
			"Triple ID",
			"Subject",
			"Predicate",
			"Object",
			"Score");

	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	public IDTuple(String initValue) {
		super(SIZE, initValue);
	}
	public IDTuple(String longSentenceID, String longSentence,
			String shortSentenceID, String shortSentence,
			String rdfRuleID, String tripleID,
			String subject, String predicate, String object,  String score) {
		super(	longSentenceID,
				longSentence,
				shortSentenceID,
				shortSentence,
				rdfRuleID,
				tripleID,
				subject,
				predicate,
				object,
				score
				);
	}



	/* ================================================== */
	/* ==========        Member  Method        ========== */
	/* ================================================== */
	public void copy(IDTuple t) {
		setLongSentenceID(t.getLongSentenceID());
		setLongSentence(t.getLongSentence());
		setShortSentenceID(t.getShortSentenceID());
		setShortSentence(t.getShortSentence());
		setRDFRuleID(t.getRDFRuleID());
		setTripleID(t.getTripleID());
		setSubject(t.getSubject());
		setPredicate(t.getPredicate());
		setObject(t.getObject());
		setScore(t.getScore());
	}

	/* ================================================== */
	/* ==========        Getter, Setter        ========== */
	/* ================================================== */
	public String getLongSentenceID() {
		return get(LONGSENTENCE_ID);
	}
	public void setLongSentenceID(String longSentenceID) {
		this.set(LONGSENTENCE_ID, (longSentenceID));
	}
	public String getLongSentence() {
		return get(LONGSENTENCE);
	}
	public void setLongSentence(String longSentence) {
		this.set(LONGSENTENCE, longSentence);
	}
	public String getShortSentenceID() {
		return get(SHORTSENTENCE_ID);
	}
	public void setShortSentenceID(String shortSentenceID) {
		this.set(SHORTSENTENCE_ID, (shortSentenceID));
	}
	public String getShortSentence() {
		return get(SHORTSENTENCE);
	}
	public void setShortSentence(String shortSentence) {
		this.set(SHORTSENTENCE, shortSentence);
	}
	public String getRDFRuleID() {
		return get(RDFRULE_ID);
	}
	public void setRDFRuleID(String rdfRuleID) {
		this.set(RDFRULE_ID, (rdfRuleID));
	}
	public String getTripleID() {
		return get(TRIPLE_ID);
	}
	public void setTripleID(String tripleID) {
		this.set(TRIPLE_ID, (tripleID));
	}
	public String getSubject() {
		return get(SUBJECT);
	}
	public void setSubject(String subject) {
		this.set(SUBJECT, subject);
	}
	public String getPredicate() {
		return get(PREDICATE);
	}
	public void setPredicate(String predicate) {
		this.set(PREDICATE, predicate);
	}
	public String getObject() {
		return get(OBJECT);
	}
	public void setObject(String object) {
		this.set(OBJECT, object);
	}
	public String getScore() {
		return get(SCORE);
	}
	public void setScore(String score) {
		this.set(SCORE, (score));
	}


	/* ================================================== */
	/* ==========        Object  Method        ========== */
	/* ================================================== */
	@Override
	public IDTuple clone() {
		return new IDTuple(
				getLongSentenceID(),
				getLongSentence(),
				getShortSentenceID(),
				getShortSentence(),
				getRDFRuleID(),
				getTripleID(),
				getSubject(),
				getPredicate(),
				getObject(),
				getScore());
	}
}
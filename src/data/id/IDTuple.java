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
	public IDTuple(int longSentenceID, String longSentence,
			int shortSentenceID, String shortSentence,
			int rdfRuleID, int tripleID,
			String subject, String predicate, String object,  int score) {
		super(	String.valueOf(longSentenceID),
				longSentence,
				String.valueOf(shortSentenceID),
				shortSentence,
				String.valueOf(rdfRuleID),
				String.valueOf(tripleID),
				subject,
				predicate,
				object,
				String.valueOf(score)
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
	public int getLongSentenceID() {
		return Integer.parseInt(get(LONGSENTENCE_ID));
	}
	public void setLongSentenceID(int longSentenceID) {
		this.set(LONGSENTENCE_ID, String.valueOf(longSentenceID));
	}
	public String getLongSentence() {
		return get(LONGSENTENCE);
	}
	public void setLongSentence(String longSentence) {
		this.set(LONGSENTENCE, longSentence);
	}
	public int getShortSentenceID() {
		return Integer.parseInt(get(SHORTSENTENCE_ID));
	}
	public void setShortSentenceID(int shortSentenceID) {
		this.set(SHORTSENTENCE_ID, String.valueOf(shortSentenceID));
	}
	public String getShortSentence() {
		return get(SHORTSENTENCE);
	}
	public void setShortSentence(String shortSentence) {
		this.set(SHORTSENTENCE, shortSentence);
	}
	public int getRDFRuleID() {
		return Integer.parseInt(get(RDFRULE_ID));
	}
	public void setRDFRuleID(int rdfRuleID) {
		this.set(RDFRULE_ID, String.valueOf(rdfRuleID));
	}
	public int getTripleID() {
		return Integer.parseInt(get(TRIPLE_ID));
	}
	public void setTripleID(int tripleID) {
		this.set(TRIPLE_ID, String.valueOf(tripleID));
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
	public int getScore() {
		return Integer.parseInt(get(SCORE));
	}
	public void setScore(int score) {
		this.set(SCORE, String.valueOf(score));
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
package data.id;

public class IDTuple extends Tuple implements Cloneable{
	private static final int SIZE 				= 8;
	
	private static final int TRIPLE_ID 			= 0;
	private static final int SUBJECT				= 1;
	private static final int PREDICATE 			= 2;
	private static final int OBJECT 				= 3;
	private static final int SHORTSENTENCE_ID	= 4;
	private static final int LONGSENTENCE_ID		= 5;
	private static final int RDFRULE_ID 			= 6;
	private static final int SCORE 				= 7;
	
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public IDTuple() {
		super(SIZE);
	}
	public IDTuple(int tripleID, String subject, String predicate, String object, 
			int shortSentenceID, int longSentenceID, int rdfRuleID, int score) {
		this();
		setTripleID(tripleID);
		setSubject(subject);
		setPredicate(predicate);
		setObject(object);
		setShortSentenceID(shortSentenceID);
		setLongSentenceID(longSentenceID);
		setRDFRuleID(rdfRuleID);
		setScore(score);
	}



	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public void copy(IDTuple t) {
		setTripleID(t.getTripleID());
		setSubject(t.getSubject());
		setPredicate(t.getPredicate());
		setObject(t.getObject());
		setShortSentenceID(t.getShortSentenceID());
		setLongSentenceID(t.getLongSentenceID());
		setRDFRuleID(t.getRDFRuleID());
		setScore(t.getScore());
	}
	
	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public int getTripleID() {
		return Integer.parseInt(values.get(TRIPLE_ID));
	}
	public void setTripleID(int tripleID) {
		this.values.set(TRIPLE_ID, String.valueOf(tripleID));
	}
	public String getSubject() {
		return values.get(SUBJECT);
	}
	public void setSubject(String subject) {
		this.values.set(SUBJECT, subject);
	}
	public String getPredicate() {
		return values.get(PREDICATE);
	}
	public void setPredicate(String predicate) {
		this.values.set(PREDICATE, predicate);
	}
	public String getObject() {
		return values.get(OBJECT);
	}
	public void setObject(String object) {
		this.values.set(OBJECT, object);
	}
	public int getShortSentenceID() {
		return Integer.parseInt(values.get(SHORTSENTENCE_ID));
	}
	public void setShortSentenceID(int shortSentenceID) {
		this.values.set(SHORTSENTENCE_ID, String.valueOf(shortSentenceID));
	}
	public int getLongSentenceID() {
		return Integer.parseInt(values.get(LONGSENTENCE_ID));
	}
	public void setLongSentenceID(int longSentenceID) {
		this.values.set(LONGSENTENCE_ID, String.valueOf(longSentenceID));
	}
	public int getRDFRuleID() {
		return Integer.parseInt(values.get(RDFRULE_ID));
	}
	public void setRDFRuleID(int rdfRuleID) {
		this.values.set(RDFRULE_ID, String.valueOf(rdfRuleID));
	}
	public int getScore() {
		return Integer.parseInt(values.get(SCORE));
	}
	public void setScore(int score) {
		this.values.set(SCORE, String.valueOf(score));
	}
	

	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public IDTuple clone() {
		return new IDTuple(
				getTripleID(), 
				getSubject(), 
				getPredicate(), 
				getObject(), 
				getShortSentenceID(), 
				getLongSentenceID(), 
				getRDFRuleID(), 
				getScore());
	}
}
package data.id;

public class IDTuple extends Tuple{
	private static final int SIZE 			= 8;
	private static final int TRIPLEID 		= 0;
	private static final int SUBJECT			= 1;
	private static final int PREDICATE 		= 2;
	private static final int OBJECT 			= 3;
	private static final int SHORTSENTENCEID = 4;
	private static final int LONGSENTENCEID 	= 5;
	private static final int RDFRULEID 		= 6;
	private static final int SCORE 			= 7;
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
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



	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public IDTuple copy() {
		//TODO
		return null;
	}
	
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public int getTripleID() {
		return Integer.parseInt(values[TRIPLEID]);
	}
	public void setTripleID(int tripleID) {
		this.values[TRIPLEID] = String.valueOf(tripleID);
	}
	public String getSubject() {
		return values[SUBJECT];
	}
	public void setSubject(String subject) {
		this.values[SUBJECT] = subject;
	}
	public String getPredicate() {
		return values[PREDICATE];
	}
	public void setPredicate(String predicate) {
		this.values[PREDICATE] = predicate;
	}
	public String getObject() {
		return values[OBJECT];
	}
	public void setObject(String object) {
		this.values[OBJECT] = object;
	}
	public int getShortSentenceID() {
		return Integer.parseInt(values[SHORTSENTENCEID]);
	}
	public void setShortSentenceID(int shortSentenceID) {
		this.values[SHORTSENTENCEID] = String.valueOf(shortSentenceID);
	}
	public int getLongSentenceID() {
		return Integer.parseInt(values[LONGSENTENCEID]);
	}
	public void setLongSentenceID(int longSentenceID) {
		this.values[LONGSENTENCEID] = String.valueOf(longSentenceID);
	}
	public int getRDFRuleID() {
		return Integer.parseInt(values[RDFRULEID]);
	}
	public void setRDFRuleID(int rdfRuleID) {
		this.values[RDFRULEID] = String.valueOf(rdfRuleID);
	}
	public int getScore() {
		return Integer.parseInt(values[SCORE]);
	}
	public void setScore(int score) {
		this.values[SCORE] = String.valueOf(score);
	}
	

	
	/**********************************/
	/********** ObjectMethod **********/
	/**********************************/
}
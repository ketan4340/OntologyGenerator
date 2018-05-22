package modules.relationExtract;

import java.util.StringJoiner;

public class RDFTriplePattern {
	private String subjectVar;
	private String predicateVar;
	private String objectVar;


	/****************************************/
	/**********     Constructor    **********/
	/****************************************/

	public RDFTriplePattern(String subjectURI, String predicateURI, String objectURI) {
		this.subjectVar = subjectURI;
		this.predicateVar = predicateURI;
		this.objectVar = objectURI;
	}

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public String[] toArray() {
		return new String[]{subjectVar, predicateVar, objectVar};
	}
	/**
	 * 区切り文字，先頭挿入文字，末尾挿入文字を指定してトリプルを繋げる. 
	 * @param delimiter	区切り文字
	 * @param prefix		接頭辞
	 * @param suffix		接尾辞
	 * @return 列記したトリプルパターン
	 */
	public String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
		return new StringJoiner(delimiter, prefix, suffix)
				.add(subjectVar).add(predicateVar).add(objectVar).toString();
	}
	
	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public String getSubject() {
		return subjectVar;
	}
	public void setSubject(String subjectVar) {
		this.subjectVar = subjectVar;
	}
	public String getPredicate() {
		return predicateVar;
	}
	public void setPredicate(String predicateVar) {
		this.predicateVar = predicateVar;
	}
	public String getObject() {
		return objectVar;
	}
	public void setObject(String objectVar) {
		this.objectVar = objectVar;
	}
	
	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return join(" ", "", ".");
	}

}
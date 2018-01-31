package grammar.word;

import java.util.List;
import java.util.stream.Collectors;

import grammar.clause.AbstractClause;

/**
 * 名詞句を想定
 * @author tanabekentaro
 */
public class Phrase extends Word{

	private List<AbstractClause<?>> dependent;	// 従属部
	private Word	 head;							// 主要部
	
	

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public Phrase(List<AbstractClause<?>> dependent, Word head) {
		super(head.concept);
		this.dependent = dependent;
		this.head = head;
	}

	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	/**
	 * 全く同じWordを複製する
	 */
	@Override
	public Phrase clone() {
		List<AbstractClause<?>> cloneDependent = dependent.stream()
				.map(c -> c.clone()).collect(Collectors.toList());
		Word cloneHead = head.clone();
		return new Phrase(cloneDependent, cloneHead);
	}
	

	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return dependent.stream().map(d -> d.toString()).collect(Collectors.joining()) + head.toString();
	}
}
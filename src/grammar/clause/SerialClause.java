package grammar.clause;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import grammar.word.Adjunct;
import grammar.word.Phrase;
import grammar.word.Word;

public class SerialClause extends AbstractClause<Phrase> {
	//private static int clausesSum = 0;

	//private final int id;
	

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	private SerialClause(Phrase categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(categorem, adjuncts, others);
		//id = clausesSum++;
	}
	
	public static SerialClause connectClauses(AbstractClause<?>... clauses) {
		int tailIndex = clauses.length-1;
		List<AbstractClause<?>> dependent = Arrays.asList(Arrays.copyOfRange(clauses, 0, tailIndex));
		Word head = clauses[tailIndex].categorem;
		Phrase categorem = new Phrase(dependent, head); 
		List<Adjunct> adjuncts = clauses[tailIndex].adjuncts;
		List<Word> others = clauses[tailIndex].others;
		SerialClause sc = new SerialClause(categorem, adjuncts, others);
		sc.setDepending(clauses[tailIndex].depending);
		return sc;
	}

	
	
	/***********************************/
	/**********   Abstract    **********/
	/***********************************/
	@Override
	public SerialClause clone() {
		Phrase cloneCategorem = this.categorem.clone();
		List<Adjunct> cloneAdjuncts = this.adjuncts.stream().map(a -> a.clone()).collect(Collectors.toList());
		List<Word> cloneOthers = this.others.stream().map(o -> o.clone()).collect(Collectors.toList());

		SerialClause clone = new SerialClause(cloneCategorem, cloneAdjuncts, cloneOthers);
		clone.setDepending(getDepending());
		return clone;
	}
	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	
	/***********************************/
	/**********   Interface   **********/
	/***********************************/
	/*
	public int getID() {
		return id;
	}
	 */
	
	/**********************************/
	/**********    Getter    **********/
	/**********************************/


	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	

}
package grammar.clause;

import java.util.List;
import java.util.stream.Collectors;

import grammar.word.Adjunct;
import grammar.word.Phrase;
import grammar.word.Word;

public class SerialClause extends AbstractClause<Phrase> {
	private static int clausesSum = 0;

	private final int id;
	

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public SerialClause(Phrase categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(categorem, adjuncts, others);
		id = clausesSum++;
		//imprintThisOnChildren();
	}
	public SerialClause(List<AbstractClause<?>> clauses) {
		this(new Phrase(clauses.subList(0, clauses.size()), clauses.get(clauses.size()-1).categorem), 
				clauses.get(clauses.size()-1).adjuncts, 
				clauses.get(clauses.size()-1).others);
	}

	
	
	/***********************************/
	/**********   Abstract    **********/
	/***********************************/
	@Override
	public SerialClause clone() {
		Phrase cloneCategorem = this.categorem.clone();
		List<Adjunct> cloneAdjuncts = this.adjuncts.stream().map(a -> a.clone()).collect(Collectors.toList());
		List<Word> cloneOthers = this.adjuncts.stream().map(o -> o.clone()).collect(Collectors.toList());

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
	public int getID() {
		return id;
	}

	
	/**********************************/
	/**********    Getter    **********/
	/**********************************/


	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	

}
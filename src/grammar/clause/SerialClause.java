package grammar.clause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import grammar.word.Adjunct;
import grammar.word.Phrase;
import grammar.word.Word;

public class SerialClause extends Clause<Phrase> {


	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	private SerialClause(Phrase categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(categorem, adjuncts, others);
	}
	
	
	/****************************************/
	/**********   Static  Method   **********/
	/****************************************/
	public static SerialClause connectClauses(Clause<?>... clauses) {
		int tailIndex = clauses.length-1;
		List<Clause<?>> dependent = Arrays.asList(Arrays.copyOfRange(clauses, 0, tailIndex));
		Word head = clauses[tailIndex].categorem;
		Phrase categorem = new Phrase(dependent, head); 
		List<Adjunct> adjuncts = clauses[tailIndex].adjuncts;
		List<Word> others = clauses[tailIndex].others;
		SerialClause sc = new SerialClause(categorem, adjuncts, others);
		sc.setDepending(clauses[tailIndex].depending);
		return sc;
	}
	
	
	/****************************************/
	/**********  Abstract  Method  **********/
	/****************************************/
	@Override
	public SerialClause clone() {
		Phrase cloneCategorem = this.categorem.clone();
		List<Adjunct> cloneAdjuncts = this.adjuncts.stream().map(a -> a.clone()).collect(Collectors.toList());
		List<Word> cloneOthers = this.others.stream().map(o -> o.clone()).collect(Collectors.toList());

		SerialClause clone = new SerialClause(cloneCategorem, cloneAdjuncts, cloneOthers);
		clone.setDepending(getDepending());
		return clone;
	}
	
	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
		
	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public void setChildren(List<Word> words) {
		Phrase phrase = null;
		List<Adjunct> adjuncts = new ArrayList<>(2);
		List<Word> others = new ArrayList<>(2);
		for (Word word : words) {
			if (word instanceof Phrase)
				phrase = (Phrase) word;
			else if (word instanceof Adjunct)
				adjuncts.add((Adjunct) word);
			else
				others.add(word);
		}
		setCategorem(phrase);
		setAdjuncts(adjuncts);
		setOthers(others);
	}
	
	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/


	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return getChildren().stream()
				.map(w -> Objects.toString(w, "Word"))
				.collect(Collectors.joining(".", "[", "]"));
	}

}
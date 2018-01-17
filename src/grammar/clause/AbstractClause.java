package grammar.clause;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import grammar.Identifiable;
import grammar.Sentence;
import grammar.SyntacticComponent;
import grammar.word.Adjunct;
import grammar.word.Word;

public abstract class AbstractClause<W extends Word> extends SyntacticComponent<Sentence, Word> implements Identifiable{
	private W categorem;					// 自立語
	private List<Adjunct> adjuncts; 		// 付属語
	private List<Word> others;			// 。などの記号
	
	private AbstractClause<?> depending;			// 係り先文節.どのClauseに係るか
	private List<AbstractClause<?>> dependeds;	// 係られてる文節の集合.どのClauseから係り受けるか

	public AbstractClause(W categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(linedupWords(categorem, adjuncts, others));
		this.categorem = categorem;
		this.adjuncts = adjuncts;
		this.others = others;
	}
	public AbstractClause(List<Word> constituents) {
		super(constituents);
	}
	
	public List<Word> words() {
		List<Word> words = new ArrayList<>(5);
		words.add(categorem);
		words.addAll(adjuncts);
		words.addAll(others);
		return words;
	}
	
	private static List<Word> linedupWords(Word categorem, List<? extends Word> adjuncts, List<? extends Word> others) {
		List<Word> words = new ArrayList<>(5);
		words.add(categorem);
		words.addAll(adjuncts);
		words.addAll(others);
		return words;
	}
	
	/**********************************/
	/**********    Getter    **********/
	/**********************************/
	
	
	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return words().stream().map(w -> Objects.toString(w, "nullWord")).collect(Collectors.joining());
	}
}
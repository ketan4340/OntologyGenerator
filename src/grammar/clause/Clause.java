package grammar.clause;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import grammar.Concept;
import grammar.morpheme.Morpheme;
import grammar.word.Adjunct;
import grammar.word.Categorem;
import grammar.word.Word;

public class Clause extends AbstractClause<Categorem>{
	public static final Clause ROOT = new Clause(null, null, null);
	private static int clausesSum = 0;

	private final int id;
	

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	/**
	 * 新型
	 * @param categorem
	 * @param adjuncts
	 * @param others
	 */
	public Clause(Categorem categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(categorem, adjuncts, others);
		this.id = clausesSum++;
		imprintThisOnChildren();
	}

	
	
	/**
	 * この文節に含まれる単語をOthers以外全て繋げて一つの名詞にする.
	 * 構文解析結果を無視して変更する強力な処理なので注意.
	 * Phraseとは違い，修飾・被修飾の関係も消える.
	 */
	public boolean nounize() {
		List<Morpheme> morphemes = Stream
				.concat(categorem.getMorphemes().stream(), 
						adjuncts.stream().flatMap(ad -> ad.getMorphemes().stream()))
				.collect(Collectors.toList()); 
		Categorem nounedWord = new Categorem(Concept.getOrNewInstance(morphemes), this);
		
		this.categorem = nounedWord;
		this.adjuncts.clear();
		return true;
	}
	
	
	/***********************************/
	/**********   Abstract    **********/
	/***********************************/
	@Override
	public Clause clone() {
		Categorem cloneCategorem = this.categorem.clone();
		List<Adjunct> cloneAdjuncts = this.adjuncts.stream().map(a -> a.clone()).collect(Collectors.toList());
		List<Word> cloneOthers = this.adjuncts.stream().map(o -> o.clone()).collect(Collectors.toList());
		
		Clause clone = new Clause(cloneCategorem, cloneAdjuncts, cloneOthers);
		clone.setDepending(getDepending());
		return clone;
	}
	
	
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
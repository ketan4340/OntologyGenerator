package grammar.clause;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import data.RDF.vocabulary.JASS;
import grammar.Concept;
import grammar.morpheme.Morpheme;
import grammar.word.Adjunct;
import grammar.word.Categorem;
import grammar.word.Word;

public class SingleClause extends Clause<Categorem>{
	public static final SingleClause ROOT = 
			new SingleClause(Categorem.EMPTY_CATEGOREM, Collections.emptyList(), Collections.emptyList());

	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	/**
	 * 自立語，付属語のリスト，接辞のリストを受け取って初期化.
	 * @param categorem
	 * @param adjuncts
	 * @param others
	 */
	public SingleClause(Categorem categorem, List<Adjunct> adjuncts, List<Word> others) {
		super(categorem, adjuncts, others);
	}

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
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
	
	
	/****************************************/
	/**********  Abstract  Method  **********/
	/****************************************/
	@Override
	public SingleClause clone() {
		Categorem cloneCategorem = this.categorem.clone();
		List<Adjunct> cloneAdjuncts = this.adjuncts.stream().map(a -> a.clone()).collect(Collectors.toList());
		List<Word> cloneOthers = this.others.stream().map(o -> o.clone()).collect(Collectors.toList());
		
		SingleClause clone = new SingleClause(cloneCategorem, cloneAdjuncts, cloneOthers);
		clone.setDepending(getDepending());
		return clone;
	}
	
	
	/****************************************/
	/**********  Interface Method  **********/
	/****************************************/
	@Override
	public Resource toRDF(Model model) {
		return super.toRDF(model).addProperty(RDF.type, JASS.SingleClause);
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
				.collect(Collectors.joining("."));	}
}
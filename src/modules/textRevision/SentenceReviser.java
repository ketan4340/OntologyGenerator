package modules.textRevision;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import grammar.Sentence;
import grammar.clause.AbstractClause;

public class SentenceReviser {

	private static final String[][][] tags_CtgAdj = {
			{{"サ変接続"}, {"動詞", "サ変・スル"}},
			{{"動詞"}, {"動詞", "接尾"}}
			};
	private static final String[][][] tags_NP = {
			{{"形容詞", "-連用テ接続"}},
			{{"連体詞"}},
			{{"助詞", "連体化"}},
			{{"助動詞", "体言接続"}}, 
			{{"名詞"}}
			};
	
	

	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public SentenceReviser() {
	}
	

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public Sentence connectWord(Sentence sentence) {
		// サ変動詞と接尾をもつ動詞をつなげる
		Stream.of(tags_CtgAdj).forEach(tag_CA -> {
			sentence.getChildren().forEach(c -> c.uniteAdjunct2Categorem(tag_CA[0], tag_CA[1]));			
		});
		// 名詞と形容詞だけ取り出す
		// これらがClauseの末尾につくものを隣のClauseにつなげる
		Stream.of(tags_NP).forEach(tag_NP -> {
			for (AbstractClause<?> matchedClause = sentence.findFirstClauseEndWith(tag_NP, true); 
					matchedClause != null; ) {
				if (!sentence.connect2Next(matchedClause)) 
					break;
				matchedClause = sentence.findFirstClauseEndWith(tag_NP, true);
			}
		});
		return sentence;
	}
	
	public List<Sentence> divideLongSentence(Sentence sentence) {
		
		
		return new ArrayList<>();
	}
}
package grammar.word;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import grammar.clause.Clause;

public class Phrase extends Word{
	private List<Word> originWords;
	
	private Categorem head;			// 主要部
	private List<Clause> dependent;	// 従属部
	
	public Phrase() {
		super(new String(), new ArrayList<String>());
		originWords = new ArrayList<Word>();
	}

	public void setPhrase(List<Word> baseWords, Clause belongClause, boolean head_tail) {
		String phraseName = new String();
		String genkei = new String();
		String yomi1 = new String();
		String yomi2 = new String();
		for(Iterator<Word> itr = baseWords.iterator(); itr.hasNext(); ) {
			Word word = itr.next();
			originWords.add(word);
			phraseName += word.getName();
			if(word.getTags().size() > 6) {
				if(itr.hasNext())
					genkei += word.getName();
				else	// 最後尾は原形
					genkei += word.getTags().get(6);
			}
			if(word.getTags().size() > 7) yomi1 += word.getTags().get(7);
			if(word.getTags().size() > 8) yomi2 += word.getTags().get(8);
		}

		Word headWord = baseWords.get(0);
		Word tailWord = baseWords.get(baseWords.size()-1);

		// Tagはhead_tailがtrueなら先頭、falseなら最後尾のWordに依存
		List<String> phraseTags = head_tail ?headWord.getTags() :tailWord.getTags();
		// 原形・読みに関しては元の単語からつなげたもの
		phraseTags.set(6, genkei);
		phraseTags.set(7, yomi1);
		phraseTags.set(8, yomi2);

		if(belongClause == null) {
			belongClause = head_tail
					? headWord.parentClause		// 新しいPhraseの所属するClauseは先頭のWordに依存
					: tailWord.parentClause;	// 新しいPhraseの所属するClauseは最後尾のWordに依存
		}
		setWord(phraseName, phraseTags, belongClause, true);
	}
}
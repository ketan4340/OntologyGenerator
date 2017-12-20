package grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Phrase extends Word{
	public List<Word> originWords;

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
			phraseName += word.name;
			if(word.tags.size() > 6) {
				if(itr.hasNext())
					genkei += word.name;
				else	// 最後尾は原形
					genkei += word.tags.get(6);
			}
			if(word.tags.size() > 7) yomi1 += word.tags.get(7);
			if(word.tags.size() > 8) yomi2 += word.tags.get(8);
		}

		Word headWord = baseWords.get(0);
		Word tailWord = baseWords.get(baseWords.size()-1);

		// Tagはhead_tailがtrueなら先頭、falseなら最後尾のWordに依存
		List<String> phraseTags = head_tail ?headWord.tags :tailWord.tags;
		// 原形・読みに関しては元の単語からつなげたもの
		phraseTags.set(6, genkei);
		phraseTags.set(7, yomi1);
		phraseTags.set(8, yomi2);

		if(belongClause == null) {
			belongClause = head_tail
					? headWord.comeUnder		// 新しいPhraseの所属するClauseは先頭のWordに依存
					: tailWord.comeUnder;	// 新しいPhraseの所属するClauseは最後尾のWordに依存
		}
		setWord(phraseName, phraseTags, belongClause, true);
	}
}
package grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Phrase extends Word{
	public List<Word> originWords;

	public Phrase() {
		originWords = new ArrayList<Word>();
	}

	public void setPhrase(List<Word> baseWordList, int belongClauseID, boolean head_tail) {
		String phraseName = new String();
		String genkei = new String();
		String yomi1 = new String();
		String yomi2 = new String();
		for(Iterator<Word> itr = baseWordList.iterator(); itr.hasNext(); ) {
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

		Word headWord = baseWordList.get(0);
		Word tailWord = baseWordList.get(baseWordList.size()-1);

		// Tagはhead_tailがtrueなら先頭、falseなら最後尾のWordに依存
		List<String> phraseTags = head_tail ?headWord.tags :tailWord.tags;
		// 原形・読みに関しては元の単語からつなげたもの
		phraseTags.set(6, genkei);
		phraseTags.set(7, yomi1);
		phraseTags.set(8, yomi2);

		if(belongClauseID == -1) {
			belongClauseID = head_tail
					? headWord.belongClause	// 新しいPhraseの所属するChunkは先頭のWordに依存
					: tailWord.belongClause;	// 新しいPhraseの所属するChunkは最後尾のWordに依存
		}
		setWord(phraseName, phraseTags, belongClauseID, true);
	}
}
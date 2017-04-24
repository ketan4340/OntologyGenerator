package grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Phrase extends Word{
	public List<Integer> origins;
	
	public Phrase() {
		origins = new ArrayList<Integer>();
	}
	
	public void setPhrase(List<Integer> baseIDList, int belongClauseID, boolean head_tail) {
		String phraseName = new String();
		String genkei = new String();
		String yomi1 = new String();
		String yomi2 = new String();
		for(Iterator<Integer> itr = baseIDList.iterator(); itr.hasNext(); ) {
			int baseID = itr.next();
			Word wd = Word.get(baseID);
			origins.add(baseID);
			phraseName += wd.wordName;
			if(wd.tags.size() > 6) {
				if(itr.hasNext())
					genkei += wd.wordName;
				else	// 最後尾は原形
					genkei += wd.tags.get(6);
			}
			if(wd.tags.size() > 7) yomi1 += wd.tags.get(7);
			if(wd.tags.size() > 8) yomi2 += wd.tags.get(8);
		}

		int headID = baseIDList.get(0);
		int tailID = baseIDList.get(baseIDList.size()-1);
		
		// Tagはhead_tailがtrueなら先頭、falseなら最後尾のWordに依存
		List<String> phraseTags = Word.get(head_tail ?headID :tailID).tags;
		// 原形・読みに関しては元の単語からつなげたもの
		phraseTags.set(6, genkei);
		phraseTags.set(7, yomi1);
		phraseTags.set(8, yomi2);

		if(belongClauseID == -1) {
			belongClauseID = head_tail
					? Word.get(headID).belongClause	// 新しいPhraseの所属するChunkは先頭のWordに依存
					: Word.get(tailID).belongClause;	// 新しいPhraseの所属するChunkは最後尾のWordに依存
		}
		setWord(phraseName, phraseTags, belongClauseID, true);
	}
}
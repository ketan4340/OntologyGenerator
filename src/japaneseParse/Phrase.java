package japaneseParse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Phrase extends Word{
	public List<Integer> orgIDs;
	
	public Phrase() {
		orgIDs = new ArrayList<Integer>();
	}
	
	public void setPhrase(List<Integer> baseIDList, int inChunkID, boolean head_tail) {
		String phraseName = new String();
		String genkei = new String();
		String yomi1 = new String();
		String yomi2 = new String();
		for(Iterator<Integer> itr = baseIDList.iterator(); itr.hasNext(); ) {
			int baseID = itr.next();
			Word wd = Word.get(baseID);
			orgIDs.add(baseID);
			phraseName += wd.wordName;
			if(wd.tags.size() > 7) genkei += wd.tags.get(6);
			if(wd.tags.size() > 8) yomi1 += wd.tags.get(7);
			if(wd.tags.size() > 9) yomi2 += wd.tags.get(8);
		}
		int headID = baseIDList.get(0);
		int tailID = baseIDList.get(baseIDList.size()-1);
		
		List<String> phraseTags = Word.get(head_tail ?headID :tailID).tags;	// Tagはhead_tailがtrueなら先頭、falseなら最後尾のWordに依存
		/*
		Cabochaの仕様によりエラー原因となりやすいので封印
		phraseTags.set(6, genkei);
		phraseTags.set(7, yomi1);
		phraseTags.set(8, yomi2);
		*/
		if(inChunkID == -1) {
			inChunkID = head_tail
					? Word.get(headID).inChunk	// 新しいPhraseの所属するChunkは先頭のWordに依存
					: Word.get(tailID).inChunk;	// 新しいPhraseの所属するChunkは最後尾のWordに依存
		}
		setWord(phraseName, phraseTags, inChunkID, true);
	}
}
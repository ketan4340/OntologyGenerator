package japaneseParse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Phrase extends Word{
	public List<Word> orgWords; // Phraseを構成する単語一つ一つを持つ
	public List<Integer> orgIDs;
	
	public Phrase() {
		orgWords = new ArrayList<Word>();
		orgIDs = new ArrayList<Integer>();
	}
	
	public void setPhrase(List<Integer> idl) {
		String phraseName = new String();
		String genkei = new String();
		String yomi1 = new String();
		String yomi2 = new String();
		int id = -1;
		for(Iterator<Integer> itr = idl.iterator(); itr.hasNext(); ) {
			Word wd = Word.get(id);
			id = itr.next();
			orgWords.add(wd);
			orgIDs.add(id);
			phraseName += wd.wordName;
			//if(itr.hasNext()) phraseName += "_";
			genkei += wd.tags.get(6);
			yomi1 += wd.tags.get(7);
			yomi2 += wd.tags.get(8);
		}
		List<String> phraseTags = Word.get(idl.get(idl.size()-1)).tags;	// 新しいPhraseのTagは最後尾のWordに依存
		phraseTags.set(6, genkei);
		phraseTags.set(7, yomi1);
		phraseTags.set(8, yomi2);
		setWord(phraseName, phraseTags, Word.get(id).inChunk);	// 新しいPhraseの所属するChunkは最後尾のWordに依存
	}
	public void setPhrase(List<Integer> idl, int chunkNum) {	// 所属するChunkが明らかな場合
		String phraseName = new String();
		String genkei = new String();
		String yomi1 = new String();
		String yomi2 = new String();
		int id = -1;
		for(Iterator<Integer> itr = idl.iterator(); itr.hasNext(); ) {
			id = itr.next();
			Word wd = Word.get(id);
			orgWords.add(wd);
			orgIDs.add(id);
			phraseName += wd.wordName;
			if(wd.tags.size() > 7) genkei += wd.tags.get(6);
			if(wd.tags.size() > 8) yomi1 += wd.tags.get(7);
			if(wd.tags.size() > 9) yomi2 += wd.tags.get(8);
		}
		List<String> phraseTags = Word.get(idl.get(idl.size()-1)).tags;	// 新しいPhraseのTagは最後尾のWordに依存
		/*
		Cabochaの仕様によりエラー原因となりやすいので封印
		phraseTags.set(6, genkei);
		phraseTags.set(7, yomi1);
		phraseTags.set(8, yomi2);
		*/
		setWord(phraseName, phraseTags, chunkNum);
	}
}
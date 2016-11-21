package englishParse;

import java.util.*;

public class Phrase extends Word{
	public List<Word> orgWords; // Phraseを構成する単語一つ一つを持つ
	public List<Integer> orgSrNums;
	
	public Phrase() {
		orgWords = new ArrayList<Word>();
		orgSrNums = new ArrayList<Integer>();
	}
	
	public void setPhrase(List<Integer> snl) {
		String phraseName = new String();
		String phraseTag = new String();
		for(Iterator<Integer> itr = snl.iterator(); itr.hasNext(); ) {
			int sn = itr.next();
			orgWords.add(Word.get(sn));
			orgSrNums.add(sn);
			phraseName += Word.get(sn).wordName;
			if(itr.hasNext()) phraseName += "_"; 
		}
		phraseTag = Word.get(snl.get(snl.size()-1)).nonTerminal;
		setWord(phraseName, phraseTag);
	}
	/*
	public void setPhrase(List<Word> wdl) {
		for(Word wd: wdl) {
			orgWords.add(wd);
			orgSrNums.add(wd.serialNum);
		}
	}
	 */
}

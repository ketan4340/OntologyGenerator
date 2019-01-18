package modules.textRevision;

import grammar.sentence.ClauseSequence;
import grammar.sentence.Sentence;

public class SubsentenceMatcher implements SubsentenceMatchResult {

	private final Sentence sentence;
	
	public SubsentenceMatcher(Sentence sentence) {
		this.sentence = sentence;
	}

	
	/* ================================================== */
	/* =================== MatchResult ================== */
	/* ================================================== */
	@Override
	public boolean matches() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
	@Override
	public boolean lookingAt() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
	@Override
	public boolean find() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
	@Override
	public int start() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}
	@Override
	public int end() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}
	@Override
	public ClauseSequence group() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}

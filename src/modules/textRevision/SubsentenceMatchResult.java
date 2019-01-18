package modules.textRevision;

import grammar.sentence.ClauseSequence;

/** 
 * {@code MatchResult}の模倣.
 */
public interface SubsentenceMatchResult {

	public boolean matches();
	public boolean lookingAt();
	public boolean find();
	public int start();
    public int end();
    public ClauseSequence group();
    
}

package modules.textRevision;

import java.util.List;

import grammar.clause.Clause;

/** 
 * {@code MatchResult}の模倣.
 */
public interface SubsentenceMatchResult {

	public boolean matches();
	public boolean lookingAt();
	public boolean find();
	public int start();
    public int end();
    public List<Clause<?>> group();
    
}

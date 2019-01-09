package grammar.pattern;

import java.util.BitSet;

public class PatternOptions {
	private static final byte OPTION_SIZE = 2;
	private static final byte FORWARD_MATCH = 0;
	private static final byte BACKWARD_MATCH = 1;
	
	private BitSet options;
	
	public PatternOptions() {
		this.options = new BitSet(OPTION_SIZE);
	}
	
	public boolean getForwardMatch() {return options.get(FORWARD_MATCH);}
	public boolean getBackwardMatch() {return options.get(BACKWARD_MATCH);}
	
	public void setForwardMatch() {options.set(FORWARD_MATCH);}
	public void setBackwardMatch() {options.set(BACKWARD_MATCH);}
}

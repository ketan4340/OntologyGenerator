package grammar.pattern;

import java.util.BitSet;

public class PatternOptions {
	private static final byte OPTION_SIZE = 3;
	private static final byte FORWARD_MATCH = 0;
	private static final byte BACKWARD_MATCH = 1;
	private static final byte CHECK_SIGN = 2;
	
	private BitSet options;
	
	public PatternOptions() {
		this.options = new BitSet(OPTION_SIZE);
	}
	
	public boolean getForwardMatch() {return options.get(FORWARD_MATCH);}
	public boolean getBackwardMatch() {return options.get(BACKWARD_MATCH);}
	public boolean getCheckSign() {return options.get(CHECK_SIGN);}
	
	public void setForwardMatch() {options.set(FORWARD_MATCH);}
	public void setBackwardMatch() {options.set(BACKWARD_MATCH);}
	public void setCheckSign() {options.set(CHECK_SIGN);}
}

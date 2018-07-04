package util.tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tuple extends ArrayList<String> {
	private static final long serialVersionUID = -201286752453864379L;

	/* ================================================== */
	/* ==========          Constructor         ========== */
	/* ================================================== */
	public Tuple(int size, String initValue) {
		super(Collections.nCopies(size, initValue));
		//this.values = Stream.generate(() -> initValue).limit(size).collect(Collectors.toList());
	}
	public Tuple(List<String> values) {
		super(values);
	}

	public static Tuple valueOf(int size, String initValue) {
		return new Tuple(size, initValue);
	}
	public static Tuple valueOf(List<String> values) {
		return new Tuple(values);
	}


	/* ================================================== */
	/* ==========        Member  Method        ========== */
	/* ================================================== */
	public String toCSV() {
		return String.join(",", this);
	}


}
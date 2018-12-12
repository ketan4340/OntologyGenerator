package grammar.naturalLanguage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import data.id.Identifiable;

public class NaturalLanguage implements Identifiable {
	private static int SUM = 0;
	
	private final int id;
	private String text;

	
	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public NaturalLanguage(String text) {
		this.id = SUM++;
		this.text = text;
	}
	public NaturalLanguage() {
		this("");
	}


	/* ================================================== */
	/* ================== Static Method ================= */
	/* ================================================== */
	/** List<NaturalLanguage> -> List<String> */
	public static List<String> toStringList(List<NaturalLanguage> nlList) {
		return nlList.stream().map(NaturalLanguage::toString).collect(Collectors.toList());
	}
	/** List<String> -> List<NaturalLanguage> */
	public static List<NaturalLanguage> toNaturalLanguageList(List<String> stringList) {
		return stringList.stream().map(NaturalLanguage::new).collect(Collectors.toList());
	}	
	/** NaturalLanguage[] -> String[] */
	public static String[] toStringArray(NaturalLanguage[] nls) {
		return Stream.of(nls).map(NaturalLanguage::toString).toArray(String[]::new);
	}
	/** String[] -> NaturalLanguage[] */
	public static NaturalLanguage[] toNaturalLanguageArray(String[] strings) {
		return Stream.of(strings).map(NaturalLanguage::new).toArray(NaturalLanguage[]::new);
	}
	
	

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public String getText() {
		return text;
	}
	
	/* ================================================== */
	/* ================ Interface Method ================ */
	/* ================================================== */
	@Override
	public int id() {
		return id;
	}

	/* ================================================== */
	/* ================== Object Method ================= */
	/* ================================================== */
	@Override
	public String toString() {
		return text;
	}

}

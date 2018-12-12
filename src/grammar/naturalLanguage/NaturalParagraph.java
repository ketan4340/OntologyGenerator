package grammar.naturalLanguage;

import java.util.Arrays;
import java.util.List;

public class NaturalParagraph {

	private List<NaturalLanguage> texts;
	
	
	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public NaturalParagraph(List<NaturalLanguage> texts) {
		this.texts = texts;
	}
	public NaturalParagraph(NaturalLanguage[] texts) {
		this(Arrays.asList(texts));
	}
	

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public List<NaturalLanguage> getTexts() {
		return texts;
	}
	
}

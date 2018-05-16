package grammar;

import java.util.Arrays;
import java.util.List;

public class NaturalParagraph {

	private List<NaturalLanguage> texts;
	
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public NaturalParagraph(List<NaturalLanguage> texts) {
		setTexts(texts);
	}
	public NaturalParagraph(NaturalLanguage[] texts) {
		this(Arrays.asList(texts));
	}
	

	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
		
	/***********************************/
	/********** Getter/Setter **********/
	/***********************************/
	public List<NaturalLanguage> getTexts() {
		return texts;
	}
	public void setTexts(List<NaturalLanguage> texts) {
		this.texts = texts;
	}
	
}
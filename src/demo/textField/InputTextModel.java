package demo.textField;

import java.util.Arrays;
import java.util.List;

import javax.swing.text.BadLocationException;

import data.RDF.Ontology;
import grammar.NaturalLanguage;
import modules.Generator;

public class InputTextModel extends AbstractDocumentModel{

	private static final long serialVersionUID = 1L;

	public InputTextModel() {
		super();
	}

	// Generator実行
	public Ontology runGenerator() {
		String allText;
		try {
			allText = getText(0, getLength());
		} catch (BadLocationException e) {
			allText = "InputTextModelはテキストの取得に失敗しました。";
			e.printStackTrace();
		}
		List<NaturalLanguage> naturalLanguageParagraphs = 
				Arrays.asList(NaturalLanguage.toNaturalLanguageArray(allText.split("\n")));
				
		return new Generator().generate(naturalLanguageParagraphs);
	}
}
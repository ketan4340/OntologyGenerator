package demo;

import java.util.Arrays;
import java.util.List;

import javax.swing.text.BadLocationException;

import data.RDF.RDFTriple;
import grammar.NaturalLanguage;
import main.Generator;

public class InputTextModel extends AbstractDocumentModel{

	public InputTextModel() {
		super();
	}

	// Generator実行
	public List<RDFTriple> runGenerator() {
		String allText;
		try {
			allText = getText(0, getLength());
		} catch (BadLocationException e) {
			allText = "InputTextModelはテキストの取得に失敗しました。";
			e.printStackTrace();
		}
		List<NaturalLanguage> naturalLanguageTexts =
				NaturalLanguage.toNaturalLanguageList(Arrays.asList(allText.split("\n")));
				
		Generator generator = new Generator();
		return generator.generate(naturalLanguageTexts);
	}
}
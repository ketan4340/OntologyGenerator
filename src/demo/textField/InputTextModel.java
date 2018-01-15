package demo.textField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.BadLocationException;

import data.original.Ontology;
import grammar.NaturalLanguage;
import modules.Generator;

public class InputTextModel extends AbstractDocumentModel{

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
		List<List<NaturalLanguage>> naturalLanguageParagraphs =
				new ArrayList<>(Arrays.asList(
						NaturalLanguage.toNaturalLanguageList(
								Arrays.asList(allText.split("\n")))));
				
		return new Generator().generate(naturalLanguageParagraphs);
	}
}
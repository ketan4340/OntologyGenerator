package demo;

import java.util.List;

import javax.swing.text.BadLocationException;

import data.RDF.RDFTriple;
import japaneseParse.Generator;

public class InputTextModel extends AbstractDocumentModel{

	public InputTextModel() {
		super();
	}

	// Generator実行
	public List<RDFTriple> runGenerator() {
		Generator generator = new Generator();
		try {
			String text = getText(0, getLength());
			generator.run(text);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return generator.getTriples();
	}
}
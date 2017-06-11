package demo;

import java.util.LinkedList;
import java.util.List;

import javax.swing.text.BadLocationException;

import japaneseParse.GenerateProcess;

public class InputTextModel extends AbstractDocumentModel{

	public InputTextModel() {
		super();
	}

	// Generator実行
	public List<String[]> runGenerator() {
		List<String[]> triples = new LinkedList<String[]>();

		String text = new String();
		try {
			text = getText(0, getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		GenerateProcess process = new GenerateProcess();
		process.run(text);
		for(String[] relation: process.getRelations()) {
			String[] triple = new String[3];
			for(int i = 0; i < 3; i++) {
				triple[i] = relation[i];
			}
			triples.add(triple);
		}
        return triples;
	}
}

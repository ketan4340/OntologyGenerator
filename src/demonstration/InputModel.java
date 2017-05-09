package demonstration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import japaneseParse.GenerateProcess;

public class InputModel extends Observable{
	private List<String> inputTextList;

	public InputModel() {

	}

	public List<String> getInputTextList() {
		return inputTextList;
	}
	public void setInputTextList(List<String> inputTextList) {
		this.inputTextList = inputTextList;
	}
	public void setInputTextList(String text) {
		String[] texts = text.split("\n");
		this.inputTextList = Arrays.asList(texts);
	}

	// Generator実行
	public List<String[]> runGenerator(String text) {
		List<String[]> triples = new ArrayList<String[]>();

		GenerateProcess process = new GenerateProcess();
		process.run(text);
		for(List<String> relation: process.getRelations()) {
			String[] triple = new String[3];
			for(int i = 0; i < 3; i++) {
				triple[i] = relation.get(i);
			}
			triples.add(triple);
		}
        return triples;
	}
}

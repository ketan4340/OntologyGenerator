package demonstration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import grammar.Sentence;
import japaneseParse.GenerateProcess;

public class GenerateModel extends Observable{
	private GenerateProcess process;

	private List<String> writingList;
	private List<Sentence> sentList;
	private List<List<String>> relations;

	public GenerateModel() {
		process = new GenerateProcess();
	}

	public GenerateProcess getGenerateProcess() {
		return process;
	}
	public List<String> getWritingList() {
		return process.getWritingList();
	}
	public void setWritingList(List<String> writingList) {
		this.writingList = writingList;
	}
	public List<Sentence> getSentList() {
		return process.getSentList();
	}
	public void setSentList(List<Sentence> sentList) {
		this.sentList = sentList;
	}
	public List<List<String>> getRelations() {
		return process.getRelations();
	}
	public void setRelations(List<List<String>> relations) {
		this.relations = relations;
	}

// Generator実行
	public void runGenerator(String text) {
		process.run(text);
		setProcessMembers();
		System.out.println("wl"+writingList);
		System.out.println("sl"+sentList);
		System.out.println("rl"+relations);
		//process.setRelations(new ArrayList<>());
	}

	public void setProcessMembers() {
		writingList = process.getWritingList();
		sentList = process.getSentList();
		relations = process.getRelations();
		setChanged();
		notifyObservers();
	}
<<<<<<< HEAD

	private String setText() {
		String text = new String();
		return text;
	}

	private void setProcessMembers() {
=======
	public void replaceProcessMembers() {
>>>>>>> 814156a82d053888839f5ccbb3e816ec1889f266
		process.setWritingList(writingList);
		process.setSentList(sentList);
		process.setRelations(relations);
	}

}

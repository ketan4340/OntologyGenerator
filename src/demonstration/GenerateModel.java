package demonstration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;

import grammar.Sentence;
import japaneseParse.GenerateProcess;
import relationExtract.OntologyBuilder;
import syntacticParse.Parser;

public class GenerateModel extends Observable{
	private GenerateProcess process;

	private String text;
	private List<String> writingList;
	private List<Sentence> sentList;
	private List<List<String>> relations;

	public GenerateModel() {
		process = new GenerateProcess();
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
		setProcessMembers();
		process.generate(text);
		setChanged();
		notifyObservers();
		getRelations().clear();
	}

	private void setProcessMembers() {
		process.setWritingList(writingList);
		process.setSentList(sentList);
		process.setRelations(relations);
	}

}

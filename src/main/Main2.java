package main;

import japaneseParse.GenerateProcess;

public class Main2 {

	public static void main(String[] args) {
		String text = "クジラは水生の哺乳類である。\n"
					+ "";

		GenerateProcess process = new GenerateProcess();
		process.run(text);
	}
}

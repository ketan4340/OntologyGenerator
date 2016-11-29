package collectingDictionary;

import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		int start = 1001;
		int depth = 10;
		int interval = 10;
		
		Crawler crw = new Crawler("goo", depth, interval);
		String[] categories = {"生物", "動物名"}; 
		crw.run(categories, "あ");	
	}
}

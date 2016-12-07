package collectingDictionary;

public class Main {
	public static void main(String[] args) {
		int depth = 500;
		int interval = 30;
		
		Crawler crw = new Crawler("goo", depth, interval);

		String[] categories = {"生物", "動物名"}; 
		String syllabary = "の";
		crw.run(0, categories, syllabary);
		
	}
}

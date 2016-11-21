package englishParse;

import java.util.*;

public class Main {

	public static void main(String[] args) {
		//String sample = "A self-propelled vehicle is a motor vehicle or road vehicle that does not operate on rails.";
		//String sample = "Juvenile is an young fish or animal that has not reached sexual maturity.";		
		String sample = "Mosquito is a small bug which bits animals and sucks blood.";
		
		/*** Syntactic Parsing Module ***/
		Sentence sent = new Sentence();
		sent.setSentence(sample);
		
		
		/*** Semantic Parsing Module ***/
		/** Step1: Term Extraction **/
		/* 名詞と形容詞だけ取り出す */
		System.out.println("\n\t Step1");
		String[] tagNounAdjc = {"NN", "NNS", "NNP", "NNPS", "JJ", "JJR", "JJS"}; 
		List<Integer> snList_NNJJ = new ArrayList<Integer>(sent.collectWords(tagNounAdjc, 0));
		for(int sn: snList_NNJJ) {
			System.out.println(sn + ": " + Word.get(sn).wordName);
		}
					
		/** Step2: Concatenation **/
		/* 名詞と名詞または形容詞と名詞をつなげて1つの名詞句にする */
		System.out.println("\n\t Step2");
		sent.printDependency();
		Sentence connectedSent = sent.concatenate(snList_NNJJ);
		connectedSent.printSentence();
		connectedSent.printDependency();
		
		/** Step3: Break Phrases **/
		System.out.println("\n\t Step3");
		List<Sentence> subSents = sent.breakSentence();
		for(Sentence snt: subSents) {
			snt.concatenate(snList_NNJJ);
			snt.printSentence();
		}
		
		/** Step4: Relations Extraction **/
		System.out.println("\n\t Step4");
		//sent.printDependency();
		String[] tagVerb = {"VB", "VBZ"};
		List<Integer> snList_VB = new ArrayList<Integer>(sent.collectWords(tagVerb, 0));
		for(int sn: snList_VB) {
			System.out.println(sn + ": " + Word.get(sn).wordName);
		}
		
	}

}
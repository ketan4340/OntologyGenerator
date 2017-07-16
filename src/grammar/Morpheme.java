package grammar;

import java.util.ArrayList;
import java.util.List;

public class Morpheme implements GrammarInterface{
	public static int morphemeSum = 0;
	public static List<Morpheme> allMorphemesList = new ArrayList<Morpheme>();

	public int id;					// 通し番号。Morphemeを特定する
	public String name;				// 形態素の文字列
	public List<String> tags;		// 品詞・活用形、読みなど
	public boolean isSubject;		// 主辞か機能語か


	@Override
	public String toString() {
		return name;
	}
	@Override
	public void printDetail() {
		System.out.println(name);
	}
}

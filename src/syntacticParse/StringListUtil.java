package syntacticParse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringListUtil {

	/*
	 * すべてのListをmarkを境に分割する。
	 * 分割後のListにmarkの行は含まれない。
	 */
	public static List<List<String>> splitStringList(String mark, boolean matchMarkPerfectly, List<String> list) {
		List<List<String>> splitedLists = new ArrayList<>();

		List<Integer> borderIndexList = getBorderIndexList(mark, matchMarkPerfectly, list);

		int fromIndex = 0, toIndex = 0;

		for (final int borderIndex : borderIndexList) {
			toIndex = borderIndex;
			if (fromIndex != toIndex)
				splitedLists.add(new ArrayList<String>(list.subList(fromIndex, toIndex)));
			fromIndex = toIndex + 1;
		}
		return splitedLists;
	}

	/*
	 * すべてのListの最初の要素がmarkではじまるように分割する
	 */
	public static List<List<String>> splitStringListStartWith(String mark, boolean matchMarkPerfectly, List<String> list) {
		Collections.reverse(list);

		List<List<String>> splitedLists = splitStringListStartWith(mark, matchMarkPerfectly, list);

		Collections.reverse(splitedLists);

		return splitedLists;
	}

	/*
	 * すべてのListの最後の要素がmarkで終わるように分割する
	 */
	public static List<List<String>> splitStringListEndWith(String mark, boolean matchMarkPerfectly, List<String> list) {
		List<List<String>> splitedLists = new ArrayList<>();

		List<Integer> borderIndexList = getBorderIndexList(mark, matchMarkPerfectly, list);

		int fromIndex = 0, toIndex = 0;

		for (final int borderIndex : borderIndexList) {
			toIndex = borderIndex + 1;
			if (fromIndex != toIndex)
				splitedLists.add(new ArrayList<String>(list.subList(fromIndex, toIndex)));
			fromIndex = toIndex;
		}
		return splitedLists;
	}



	private static List<Integer> getBorderIndexList(String mark, boolean matchMarkPerfectly, List<String> list) {
		List<Integer> borderIndexList = new ArrayList<>();
		if (matchMarkPerfectly) {
			for (int i=0; i<list.size(); i++) {
				String line = list.get(i);
				if (line.equals(mark))
					borderIndexList.add(i);
			}
		} else {
			for (int i=0; i<list.size(); i++) {
				String line = list.get(i);
				if (line.startsWith(mark))
					borderIndexList.add(i);
			}
		}
		return borderIndexList;
	}
}

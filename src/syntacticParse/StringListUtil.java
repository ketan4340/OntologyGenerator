package syntacticParse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringListUtil {

	/**
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

	/**
	 * markと一致する行から，次のmarkの行の1つ前までを1セットとし分割する．
	 * 分割後のList<String>はいずれも最初の要素がmarkで始まる/一致する．
	 * matchMarkPerfectlyがtrueの場合，markの行は完全一致かどうかで判定する．
	 */
	public static List<List<String>> splitStringListStartWith(String mark, boolean matchMarkPerfectly, List<String> list) {
		List<List<String>> splitedLists = new ArrayList<>();

		List<Integer> borderIndexList = getBorderIndexList(mark, matchMarkPerfectly, list);

		int fromIndex = -1, toIndex = 0;

		for (final int borderIndex : borderIndexList) {
			if (fromIndex == -1) {
				fromIndex = borderIndex;
				continue;	// 1つ目のmarkはfromIndexだけ更新して飛ばす
			}

			toIndex = borderIndex;
			if (fromIndex != toIndex)
				splitedLists.add(new ArrayList<String>(list.subList(fromIndex, toIndex)));
			fromIndex = toIndex;
		}
		// 最後のmarkから行末まではここで追加
		toIndex = list.size();
		splitedLists.add(new ArrayList<String>(list.subList(fromIndex, toIndex)));

		return splitedLists;
	}

	/**
	 * markと一致する行の1つ後の行から，次のmarkの行までを1セットとし分割する．
	 * 分割後のList<String>はいずれも最後の要素がmarkで始まる/一致する．
	 * matchMarkPerfectlyがtrueの場合，markの行は完全一致かどうかで判定する．
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


	/**
	 * markで始まる/と一致する行のindexをListにする
	 */
	private static List<Integer> getBorderIndexList(String mark, boolean matchMarkPerfectly, List<String> list) {
		List<Integer> borderIndexList = new ArrayList<>(list.size()/2);
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

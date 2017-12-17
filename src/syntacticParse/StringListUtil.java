package syntacticParse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringListUtil {

	/**
	 * すべてのListをregexを境に分割する。
	 * 分割後のListにregexの行は含まれない。
	 */
	public static List<List<String>> split(String regex, List<String> list) {
		List<List<String>> splitedLists = new ArrayList<>();

		List<Integer> borderIndexList = getBorderIndexList(regex, list);

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
	 * regexとマッチする行から,次にregexにマッチする行の1つ前までを1セットとし分割する.
	 * 分割後のList<String>はいずれも最初の文字列がregexとマッチする.
	 * @param regex 分割の境界にする文字列の正規表現
	 * @param list 分割したい文字列リスト
	 * @return 分割された文字列リストのリスト.
	 */
	public static List<List<String>> splitStartWith(String regex, List<String> list) {
		List<List<String>> splitedLists = new ArrayList<>();

		List<Integer> borderIndexList = getBorderIndexList(regex, list);
		
		int fromIndex = 0, toIndex = list.size();

		for (final int borderIndex : borderIndexList) {
			if (fromIndex == -1) {
				fromIndex = borderIndex;
				continue;	// 1つ目のregexはfromIndexだけ更新して飛ばす
			}

			toIndex = borderIndex;
			if (fromIndex != toIndex)
				splitedLists.add(new ArrayList<String>(list.subList(fromIndex, toIndex)));
			fromIndex = toIndex;
		}
		// 最後のregexから行末まではここで追加
		toIndex = list.size();
		splitedLists.add(new ArrayList<String>(list.subList(fromIndex, toIndex)));

		return splitedLists;
	}

	/**
	 * regexとマッチする行の1つ後の行から,次にregexにマッチする行までを1セットとし分割する.
	 * 分割後のList<String>はいずれも最後の文字列がregexとマッチする.
	 * @param regex 分割の境界にする文字列の正規表現
	 * @param list 分割したい文字列リスト
	 * @return 分割された文字列リストのリスト.
	 */
	public static List<List<String>> splitEndWith(String regex, List<String> list) {
		List<List<String>> splitedLists = new ArrayList<>();

		List<Integer> borderIndexList = getBorderIndexList(regex, list);

		int fromIndex = 0, toIndex = 0;

		for (final int borderIndex : borderIndexList) {
			toIndex = borderIndex + 1;
			System.out.println("from : " + fromIndex + ", to : " + toIndex);
			if (fromIndex != toIndex)
				splitedLists.add(new ArrayList<String>(list.subList(fromIndex, toIndex)));
			fromIndex = toIndex;
		}
		return splitedLists;
	}


	/**
	 * 正規表現regexとマッチする行のindexをListにする.
	 */
	private static List<Integer> getBorderIndexList(String regex, List<String> list) {
		List<Integer> borderIndexList = new ArrayList<>(list.size()/2);
		Pattern pattern = Pattern.compile(regex);
		for (int i=0; i<list.size(); i++) {
			Matcher matcher = pattern.matcher(list.get(i));
			if (matcher.matches())
				borderIndexList.add(i);
		}
		return borderIndexList;
	}
}

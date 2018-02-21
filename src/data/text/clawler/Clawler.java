package data.text.clawler;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Clawler {
	protected int interval;		// クローリングのスリープ間隔

	
	/************************************/
	/**********   Constructor  **********/
	/************************************/
	public Clawler(int interval) {
		this.interval = interval;
	}
	
	/**
	 * 指定のURLが示すWebサイトのドキュメントを得る.
	 * @param url 実在するWebサイトのURL
	 * @return HTMLドキュメント
	 */
	public Document accessDocument(String url) {
		Connection connection = Jsoup.connect(url);
		connection.timeout(0);
		try {
			return connection.get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Document("http://www.uec.ac.jp");
	}
		
}
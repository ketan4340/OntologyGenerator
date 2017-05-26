package demonstration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.parser.ParserDelegator;

import grammar.Sentence;
import grammar.Word;
import syntacticParse.Parser;

public class DocumentModel{
	private PlainDocument plainDoc;
	private HTMLDocument htmlDoc;

	private Parser parser;

	private static String[] noun = {"名詞"};
	private static String defaultPlainText = "この文章はデフォルトテキストです。";
	private static String defaultHTMLTags =
			"<head>default head</head>"
			+ "<body id=\"body\">"
			+ "</body>";

	public DocumentModel() {
		plainDoc = new PlainDocument();
		htmlDoc = new HTMLDocument();
		parser = new Parser("cabocha");
		htmlDoc.setParser(new ParserDelegator());

		try {
			plainDoc.insertString(0, defaultPlainText, new SimpleAttributeSet());
			htmlDoc.setInnerHTML(htmlDoc.getDefaultRootElement(), defaultHTMLTags);
		} catch (BadLocationException | IOException e) {
			e.printStackTrace();
		}
	}

	public List<Sentence> getSentences(String plainTexts) {
		List<Sentence> sentenceList = new LinkedList<Sentence>();
		// PlainTextを改行を境に分解して解析
		for(String plainText : plainTexts.split("\n")) {
			Sentence sentence = parser.run(plainText);
			if(sentence != null) sentenceList.add(sentence);
		}
		return sentenceList;
	}
	private void plain2html() {
		List<Sentence> sentenceList = new ArrayList<Sentence>();
		try {
			sentenceList = getSentences(plainDoc.getText(0, plainDoc.getLength()));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		for(final Sentence sentence : sentenceList) {
			String htmlText = "<p>";
			//htmlText += "<font size=\"+1\">";

			for(int wdID : sentence.wordIDs()) {	// 文の単語を走査
				Word word = Word.get(wdID);
				htmlText += (word.hasSomeTags(noun))	// 名詞ならアンカータグで囲む
						? "<a href=\"" + word.wordName + "\">" + word.wordName + "</a>"
						: word.wordName;
			}
			//htmlText += "</font>";
			htmlText += "</p><br>\n";				// 文末で改行
			try {
				htmlDoc.insertBeforeEnd(htmlDoc.getElement("body"), htmlText);
			} catch (BadLocationException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public PlainDocument getPlainDoc() {
		return plainDoc;
	}
	public void setPlainDoc(PlainDocument plainDoc) {
		this.plainDoc = plainDoc;
	}
	public HTMLDocument getHtmlDoc() {
		plain2html();
		return htmlDoc;
	}
	public void setHtmlDoc(HTMLDocument htmlDoc) {
		this.htmlDoc = htmlDoc;
	}


}

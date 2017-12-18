package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.parser.ParserDelegator;

import grammar.NaturalLanguage;
import grammar.Sentence;
import grammar.Word;
import syntacticParse.Cabocha;

public class DocumentModel extends AbstractDocumentModel{
	private HTMLDocument htmlDoc;		// plainDocumentからHTMLに切り替える

	private Cabocha parser;

	private static String[] noun = {"名詞"};
	private static String defaultPlainText = "この文章はデフォルトテキストです。";
	private static String defaultHTMLTags =
			"<head>default head</head>"
			+ "<body id=\"body\">"
			+ "</body>";


	public DocumentModel() {
		super();
		htmlDoc = new HTMLDocument();
		parser = new Cabocha();
		htmlDoc.setParser(new ParserDelegator());

		try {
			insertString(0, defaultPlainText, new SimpleAttributeSet());
			htmlDoc.setInnerHTML(htmlDoc.getDefaultRootElement(), defaultHTMLTags);
		} catch (BadLocationException | IOException e) {
			e.printStackTrace();
		}
	}

	public List<Sentence> getSentences(String plainTexts) {
		List<Sentence> sentenceList = new LinkedList<Sentence>();
		// PlainTextを改行を境に分解して解析
		for(String plainText : plainTexts.split("\n")) {
			Sentence sentence = parser.text2sentence(new NaturalLanguage(plainText));
			if(sentence != null) sentenceList.add(sentence);
		}
		return sentenceList;
	}
	private void plain2html() {
		List<Sentence> sentenceList = new ArrayList<Sentence>();
		try {
			sentenceList = getSentences(this.getText(0, this.getLength()));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		for(final Sentence sentence : sentenceList) {
			String htmlText = "<p>";
			//htmlText += "<font size=\"+1\">";

			for(final Word word : sentence.getWordList()) {	// 文の単語を走査
				htmlText += (word.hasSomeTags(noun))	// 名詞ならアンカータグで囲む
						? "<a href=\"" + word.name + "\">" + word.name + "</a>"
						: word.name;
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

	public HTMLDocument getHtmlDoc() {
		plain2html();
		return htmlDoc;
	}
	public void setHtmlDoc(HTMLDocument htmlDoc) {
		this.htmlDoc = htmlDoc;
	}


}

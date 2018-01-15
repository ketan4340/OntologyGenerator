package demo.textField;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;

public abstract class AbstractDocumentModel extends PlainDocument{
	private static final Path path = Paths.get("writings/gooText生物-動物名-あ.txt");

	public AbstractDocumentModel() {
		super();
	}

	public String importRandomText() {
		List<String> writings = null;
		try (Stream<String> stream = Files.lines(path, Charset.forName("UTF-8"))) {
			 writings = stream.collect(Collectors.toList());
		} catch (IOException e) {
			System.err.println(e);
			writings = new LinkedList<String>(Arrays.asList("テキストを取得できませんでした。"));
		}

		String text = writings.get(new Random().nextInt(writings.size())); // ファイルからランダムで一文選ぶ
		text += "\n";

		try {
			insertString(this.getLength(), text, new SimpleAttributeSet());	// 最後尾に追加
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return text;
	}

	public void clearDocument() {
		try {
			remove(0, getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}

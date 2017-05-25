package demonstration;

import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.Element;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class MainController {
	/*** Model ***/
	private InputTextModel iptModel;
	private OntologyModel ontModel;
	private DocumentModel docModel;

	/*** View ***/
	private MainView view;
	private MainFXView fxView;

	public MainController(InputTextModel i, OntologyModel o, DocumentModel d) {
		iptModel = i;
		ontModel = o;
		docModel = d;

		// JavaFX試験運用
		// fxView = new MainFXView(this);
	}

	public void setMainView(MainView v) {
		this.view = v;
	}
	public void setMainFXView(MainFXView v) {
		this.fxView = v;
	}
	public InputTextModel getIptModel() {
		return iptModel;
	}
	public OntologyModel getOntModel() {
		return ontModel;
	}
	public DocumentModel getDocModel() {
		return docModel;
	}


	/** ActionListener **/
	/* RunGeneratorボタンの実装 */
	private ActionListener generateAction = (event -> {
		String text = view.getIptTextarea().getText();
		List<String[]> triples = iptModel.runGenerator(text);
		ontModel.addAllTriples(triples);
	});
	// JavaFX仕様
	private EventHandler<ActionEvent> generateActionFX = (event -> {
		String text = view.getIptTextarea().getText();
		List<String[]> triples = iptModel.runGenerator(text);
		ontModel.addAllTriples(triples);
	});
	/* ImportTextボタンの実装 */
	private ActionListener importTextAction = (event -> {
		List<String> writings = new LinkedList<String>();

		Path path = Paths.get("writings/gooText生物-動物名-あ.txt");
		// List<String> strs = Files.readAllLines(path, StandardCharsets.UTF_8); // ファイルが小さければこれでもいい
		try (Stream<String> stream = Files.lines(path, Charset.forName("UTF-8"))) {
			stream.forEach(line -> writings.add(line));
		} catch (IOException e) {
			System.out.println(e);
		}
		String text = writings.get(new Random().nextInt(writings.size()));	// ファイルからランダムで一文選ぶ
		text += "\n";
		Object button = event.getSource();
		if(button.equals(view.getIptImportBt())) {
			view.getIptTextarea().append(text);
		}else if(button.equals(view.getDocImportBt())) {
			view.getHTML_PlainTgBt().setSelected(false);	// エディタをplainに変更
			Document document = view.getDocEditorpane().getDocument();
			try {
				document.insertString(0, text, new SimpleAttributeSet());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	});
	/* Clearボタンの実装 */
	private ActionListener clearTextAction = (event -> {
		Object button = event.getSource();
		if(button.equals(view.getIptClearBt())) {
			view.getIptTextarea().setText("");
		}else if(button.equals(view.getDocClearBt())) {
			view.getDocEditorpane().setText("");
			view.getHTML_PlainTgBt().setSelected(false);	// エディタをplainに変更
		}
	});
	/* PlainテキストとHTMLテキストを切り替えるトグルボタンの実装 */
	private ItemListener switchHTMLPlainAction = (event -> {
		JEditorPane editorpane = view.getDocEditorpane();
		JToggleButton tgbt = (JToggleButton) event.getItem();
		if(tgbt.isSelected()) {						// plain->HTML
			HTMLDocument htmlDoc = docModel.getHtmlDoc();		// DocumentModelからhtmlDoc取得
			try {
				System.out.println("html :\t" + htmlDoc.getText(0, htmlDoc.getLength()));
				System.out.println("-----------------------------------");
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			tgbt.setText("HTML(編集不可)");						// ボタンの表示をHTMLに
			editorpane.setEditable(false);						// 編集不可に
			editorpane.setOpaque(false);						// 背景を透過に
			editorpane.setContentType("text/html");				// editorをHTML仕様に変えてから
			editorpane.setDocument(htmlDoc);					// HTMLDocumentをセット
		}else {										// HTML->plain
			PlainDocument plainDoc = docModel.getPlainDoc();	// DocumentModelからplainDoc取得
			try {
				System.out.println("plain:\t" + plainDoc.getText(0, plainDoc.getLength()));
				System.out.println("-----------------------------------");
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			tgbt.setText("plain(編集可能)");						// ボタンの表示をplainに
			editorpane.setEditable(true);						// 編集可能に
			editorpane.setOpaque(true);							// 背景を非透過に
			editorpane.setContentType("text/plain");			// editorをPlain仕様に変えてから
			editorpane.setDocument(plainDoc);					// PlainDocumentをセット
		}

	});
	/* Hyperlinkをクリックした時の動作を実装 */
	private HyperlinkListener hyperlinkAction = (event -> {
		JEditorPane editorpane = view.getDocEditorpane();

		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {		// クリックした場合
            try {
				JOptionPane.showMessageDialog(editorpane, event.getDescription());
			} catch (HeadlessException e) {
				e.printStackTrace();
			}

        }else if(event.getEventType() == HyperlinkEvent.EventType.ENTERED) {	// カーソルを当てた場合

        }else if(event.getEventType() == HyperlinkEvent.EventType.EXITED) {		// カーソルを外した場合

        }else {

        }
    });

	public ActionListener getGenerateAction() {
		return generateAction;
	}
	public ActionListener getImportTextAction() {
		return importTextAction;
	}
	public ActionListener getClearTextAction() {
		return clearTextAction;
	}
	/*
	public ActionListener getParseAction() {
		return parseAction;
	}
	*/
	public ItemListener getSwitchHTMLPlainAction() {
		return switchHTMLPlainAction;
	}
	public HyperlinkListener getHyperlinkAction() {
		return hyperlinkAction;
	}
}
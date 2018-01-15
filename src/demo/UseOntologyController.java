package demo;

import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.JEditorPane;
import javax.swing.JToggleButton;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.PlainDocument;
import javax.swing.text.html.HTMLDocument;

import data.original.RDFTriple;

public class UseOntologyController extends AbstractEditorController{
	/*** Model ***/
	private DocumentModel docModel = (DocumentModel) super.docModel;
	private OntologyModel ontModel;
	private OntologyModel subOntModel;

	/*** View ***/
	private DocumentView docView = (DocumentView) super.edtView;
	private OntologyTableView ontView;
	private OntologyTableView subOntTable;
	private OntologyGraphView subOntGraph;


	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	public UseOntologyController(AbstractDocumentModel docModel, OntologyModel ontModel, OntologyModel subOntModel) {
		super(docModel);
		this.docModel = (DocumentModel) docModel;
		this.ontModel = ontModel;
		this.subOntModel = subOntModel;
	}


	/** ActionListener **/
	/* PlainテキストとHTMLテキストを切り替えるトグルボタンの実装 */
	private ItemListener switchHTMLPlainAction = (event -> {
		JEditorPane editorpane = docView.getEditorpane();
		JToggleButton tgbt = (JToggleButton) event.getItem();
		if (tgbt.isSelected()) { // plain->HTML
			HTMLDocument htmlDoc = docModel.getHtmlDoc();	// DocumentModelからhtmlDoc取得
			tgbt.setText("HTML(編集不可)"); 					// ボタンの表示をHTMLに
			editorpane.setEditable(false);					// 編集不可に
			editorpane.setOpaque(false);					// 背景を透過に
			editorpane.setContentType("text/html");			// editorをHTML仕様に変えてから
			editorpane.setDocument(htmlDoc);				// HTMLDocumentをセット
		} else { // HTML->plain
			PlainDocument plainDoc = docModel;					// DocumentModelからplainDoc取得
			tgbt.setText("plain(編集可能)");						// ボタンの表示をplainに
			editorpane.setEditable(true);						// 編集可能に
			editorpane.setOpaque(true);							// 背景を非透過に
			editorpane.setContentType("text/plain");			// editorをPlain仕様に変えてから
			editorpane.setDocument(plainDoc);					// PlainDocumentをセット
		}

	});
	/* Hyperlinkをクリックした時の動作を実装 */
	private HyperlinkListener hyperlinkAction = (event -> {
		String clickedWord = event.getDescription();
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {	// クリックした場合
			List<RDFTriple> relationList = new LinkedList<RDFTriple>();		// クリックした単語をSかOに含むトリプルを集める
			relationList.addAll(ontModel.getPO(clickedWord).stream().map(tri -> new RDFTriple(tri)).collect(Collectors.toList()));
			relationList.addAll(ontModel.getSP(clickedWord).stream().map(tri -> new RDFTriple(tri)).collect(Collectors.toList()));

			// 選び出したオントロジーを追加
			// モデルを編集
			subOntModel.clear();
			subOntModel.addAllTriples(relationList);

			// }else if(event.getEventType() == HyperlinkEvent.EventType.ENTERED) { // カーソルを当てた場合
			// }else if(event.getEventType() == HyperlinkEvent.EventType.EXITED) { // カーソルを外した場合
		}
	});

	public DocumentView getDocumentView() {
		return docView;
	}
	public void setDocumentView(DocumentView docView) {
		this.docView = docView;
	}
	public OntologyTableView getOntologyTable() {
		return ontView;
	}
	public void setOntologyTable(OntologyTableView ontView) {
		this.ontView = ontView;
	}
	public OntologyTableView getSubOntologyTable() {
		return subOntTable;
	}
	public void setSubOntologyTable(OntologyTableView subOntTable) {
		this.subOntTable = subOntTable;
	}
	public OntologyGraphView getSubOntologyGraph() {
		return subOntGraph;
	}
	public void setSubOntologyGraph(OntologyGraphView subOntGraph) {
		this.subOntGraph = subOntGraph;
	}
	public DocumentModel getDocumentModel() {
		return docModel;
	}
	public OntologyModel getSubOntlogyModel() {
		return subOntModel;
	}
	public OntologyModel getOntologyModel() {
		return ontModel;
	}
	public void setOntologyModel(OntologyModel ontModel) {
		this.ontModel = ontModel;
	}
	public ItemListener getSwitchHTMLPlainAction() {
		return switchHTMLPlainAction;
	}
	public HyperlinkListener getHyperlinkAction() {
		return hyperlinkAction;
	}
}
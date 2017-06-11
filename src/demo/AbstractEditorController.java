package demo;

import java.awt.event.ActionListener;

/*** テキスト入力に関する部分のみ実装 ***/
public abstract class AbstractEditorController {
	/*** Model ***/
	protected AbstractDocumentModel docModel;

	/*** View ***/
	protected AbstractEditorView edtView;

	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	public AbstractEditorController(AbstractDocumentModel dm) {
		this.docModel = dm;
	}

	public void setEditorView(AbstractEditorView ev) {
		this.edtView = ev;
	}
	public AbstractDocumentModel getDocumentModel() {
		return docModel;
	}

	/* ImportTextボタンの実装 */
	private ActionListener importTextAction = (event -> {
		//edtView.getHTML_PlainTgBt().setSelected(false); // エディタをplainに変更
		docModel.importRandomText();
	});

	/* Clearボタンの実装 */
	private ActionListener clearTextAction = (event -> {
		//edtView.getHTML_PlainTgBt().setSelected(false); // エディタをplainに変更
		docModel.clearDocument();
	});


	public ActionListener getImportTextAction() {
		return importTextAction;
	}
	public ActionListener getClearTextAction() {
		return clearTextAction;
	}
}

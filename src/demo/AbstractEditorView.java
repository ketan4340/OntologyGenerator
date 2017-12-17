package demo;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public abstract class AbstractEditorView extends JPanel{
	/**** Model ****/
	//protected AbstractDocumentModel edtModel;

	/**** Controller ****/
	//protected AbstractEditorController edtController;

	/**** View ****/
	protected JFrame parentFrame;

	protected JPanel menuPanel;
	protected JButton importBt, clearBt;
	protected JEditorPane editorpane;


	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	public AbstractEditorView() {
		super();

		// メニューパネルの初期化
		menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.LINE_AXIS));	// 配置順を左から右に

		// エディタペインの初期化
		editorpane = new JEditorPane();
		editorpane.setContentType("text/plain");		// 初期設定:plain
	    editorpane.setEditable(true);					// 初期設定:編集可能
	    //editorpane.setDocument(edtModel);				// DocumentModelのメンバPlainDocをセット

	    // 上記2つの設置
		setLayout(new BorderLayout());
		add(menuPanel, BorderLayout.NORTH);
		add(new JScrollPane(editorpane), BorderLayout.CENTER);
	}

	/* メニューの配置は子クラスで決める */
	abstract protected void setMenu();

	/* 使うドキュメントは子クラスで決めろや */
	abstract protected void setDocument();


	public void setParentFrame(JFrame pf) {
		this.parentFrame = pf;
	}
	public JFrame getParentFrame() {
		return parentFrame;
	}
	public JPanel getPn_menu() {
		return menuPanel;
	}
	public JButton getImportBt() {
		return importBt;
	}
	public JButton getClearBt() {
		return clearBt;
	}
	public JEditorPane getEditorpane() {
		return editorpane;
	}

}

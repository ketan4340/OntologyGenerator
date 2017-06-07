package demonstration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

public class WholeView extends JFrame implements Observer{
	/**** Model ****/
	private InputTextModel iptModel;
	private OntologyModel ontModel;
	private DocumentModel docModel;

	/**** Controller ****/
	private MainController controller;

	/**** View ****/
	/*** Whole frame ***/
	private JSplitPane splitpane;

	/** Left panel **/
	private JPanel leftPanel;
	/* InputText parts */
	private JPanel iptPanel;
	private JScrollPane iptScrollpane;
	private JButton iptImportBt, iptClearBt;
	private JTextArea iptTextarea;
	/* Ontology parts */
	private JPanel ontPanel;
	private JScrollPane ontScrollpane;
	private JButton generateBt;
	private JTable ontTable;
	/** Center panel **/
	/** Right panel **/
	//private JPanel rightPanel;
	/* Document parts */
	private JPanel docPanel;
	private JScrollPane docScrollpane;
	private JButton docImportBt, docClearBt;
	private JToggleButton html_PlainTgBt;
	private JEditorPane docEditorpane;

	/**** 細かい子ビューの配置を決める ****/
	public WholeView(MainController ctrl) {
		super("OntologyGenerator");
		this.controller = ctrl;
		// controllerにこのviewインスタンスを持たせる
		controller.setMainView(this);

		// Modelを参照のために保持する
		iptModel = controller.getIptModel();
		ontModel = controller.getOntModel();
		docModel = controller.getDocModel();

		designWholeFrame();
	    setVisible(true);	// 表示
	}

	public WholeView(InputTextView iptView, OntologyTableView ontView, DocumentView docView) {
		super("OntologyGenerator");

		/* ウインドウ全体の設定 */
		setExtendedState(JFrame.MAXIMIZED_BOTH);		// ディスプレイ全体に表示
		setLocationRelativeTo(null);					// フレームを中央に表示
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// ウインドウを閉じたら終了
		setLayout(new GridLayout(1, 3));

		/* 子ビュー(JPanel)を配置 */
		add(iptView);
		add(ontView);
		add(docView);

		setVisible(true);
	}

	private void designWholeFrame() {
		setExtendedState(JFrame.MAXIMIZED_BOTH);		// 画面全体の半分のサイズ
		setLocationRelativeTo(null);					// フレームを中央に表示
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// ウインドウを閉じたら終了

		designInputTextPanel();
		designOntologyPanel();
		designDocumentPanel();

		leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(2, 1));
		leftPanel.add(iptPanel);
		leftPanel.add(ontPanel);

		splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, docPanel);
		splitpane.setOneTouchExpandable(true);
		splitpane.setDividerLocation(JFrame.MAXIMIZED_BOTH / 2); // 画面半分の位置に初期設定
		add(splitpane);
	}

	private void designInputTextPanel() {
		JPanel pn_menu = new JPanel();
		pn_menu.setLayout(new BoxLayout(pn_menu, BoxLayout.LINE_AXIS));	// 配置順を左から右に
	    iptImportBt = new JButton("インポート");
	    iptImportBt.addActionListener(controller.getImportTextAction());
		pn_menu.add(iptImportBt);
		pn_menu.add(new JLabel("設定"));
		pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		iptClearBt = new JButton("クリア");
		iptClearBt.addActionListener(controller.getClearTextAction());
		pn_menu.add(iptClearBt);

	    iptTextarea = new JTextArea();
	    iptTextarea.setBackground(new Color(209, 145, 71));	// 背景:駱駝色
	    iptTextarea.setLineWrap(true);
	    iptScrollpane = new JScrollPane(iptTextarea);

	    iptPanel = new JPanel(new BorderLayout());
	    iptPanel.add(pn_menu, BorderLayout.NORTH);
	    iptPanel.add(iptScrollpane, BorderLayout.CENTER);
	}
	private void designOntologyPanel() {
		JPanel pn_menu = new JPanel();
		pn_menu.setLayout(new BoxLayout(pn_menu, BoxLayout.LINE_AXIS));	// 配置順を左から右に
	    generateBt = new JButton("↓オントロジー構築↓");
	    generateBt.addActionListener(controller.getGenerateAction());
	    pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		pn_menu.add(generateBt);
		pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		pn_menu.add(new JLabel("クリア"));

		ontTable = new JTable(ontModel);
		ontScrollpane = new JScrollPane(ontTable);

		ontPanel = new JPanel(new BorderLayout());
		ontPanel.add(pn_menu, BorderLayout.NORTH);
		ontPanel.add(ontScrollpane, BorderLayout.CENTER);
	}
	private void designDocumentPanel() {
		JPanel pn_menu = new JPanel();
		pn_menu.setLayout(new BoxLayout(pn_menu, BoxLayout.LINE_AXIS));	// 配置順を左から右に

	    docImportBt = new JButton("インポート");
	    docImportBt.addActionListener(controller.getImportTextAction());
		pn_menu.add(docImportBt);
		pn_menu.add(new JLabel("設定"));
		pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		html_PlainTgBt = new JToggleButton("plain", false);
		html_PlainTgBt.addItemListener(controller.getSwitchHTMLPlainAction());
		pn_menu.add(html_PlainTgBt);
		pn_menu.add(Box.createGlue());	// 可変長の隙間を挿入
		docClearBt = new JButton("クリア");
		docClearBt.addActionListener(controller.getClearTextAction());
		pn_menu.add(docClearBt);

		docEditorpane = new JEditorPane();
		//docEditorpane.setBackground(new Color(156, 167, 22));	// 背景:スカイブルー
		docEditorpane.setContentType("text/plain");			// 初期設定:plain
	    docEditorpane.setEditable(true);					// 初期設定:編集可能
	    docEditorpane.setDocument(docModel);	// DocumentModelのメンバPlainDocをセット
	    docEditorpane.addHyperlinkListener(controller.getHyperlinkAction());
	    docScrollpane = new JScrollPane(docEditorpane);

	    docPanel = new JPanel(new BorderLayout());
	    docPanel.add(pn_menu, BorderLayout.NORTH);
	    docPanel.add(docScrollpane, BorderLayout.CENTER);
	}

	public void setInputTextModel(InputTextModel i_model) {
		this.iptModel = i_model;
	}
	public void setOntologyModel(OntologyModel o_model) {
		this.ontModel = o_model;
	}
	public void setModels(InputTextModel i_model, OntologyModel o_model) {
		setInputTextModel(i_model);
		setOntologyModel(o_model);
	}

	@Override
	public void update(Observable obs, Object arg) {

	}

	public JButton getGenerateBt() {
		return generateBt;
	}
	public JTextArea getIptTextarea() {
		return iptTextarea;
	}
	public JEditorPane getDocEditorpane() {
		return docEditorpane;
	}
	public JButton getIptImportBt() {
		return iptImportBt;
	}
	public JButton getIptClearBt() {
		return iptClearBt;
	}
	public JButton getDocImportBt() {
		return docImportBt;
	}
	public JButton getDocClearBt() {
		return docClearBt;
	}
	public JToggleButton getHTML_PlainTgBt() {
		return html_PlainTgBt;
	}
}

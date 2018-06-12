package demo.textField;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;

import demo.controller.BuildOntologyController;

public class InputTextView extends AbstractEditorView{
	private static final long serialVersionUID = 8179154552983594515L;

	/**** Model ****/
	private final InputTextModel iptModel;

	/**** Controller ****/
	private final BuildOntologyController buildController;

	/**** View ****/
	private JButton generateBt;

	public InputTextView(BuildOntologyController bCtrl) {
		super();
		this.buildController = bCtrl;
		this.iptModel = bCtrl.getInputTextModel();
		buildController.setInputTextView(this);

		setMenu();
		setDocument();
	}


	@Override
	protected void setMenu() {
	    importBt = new JButton("インポート");
	    importBt.addActionListener(buildController.getImportTextAction());
	    clearBt = new JButton("クリア");
		clearBt.addActionListener(buildController.getClearTextAction());

		menuPanel.add(importBt);
		menuPanel.add(new JLabel("設定"));
		menuPanel.add(Box.createGlue());	// 可変長の隙間を挿入

		generateBt = new JButton("↓オントロジー構築↓");
	    generateBt.addActionListener(buildController.getGenerateAction());
	    menuPanel.add(Box.createGlue());	// 可変長の隙間を挿入
		menuPanel.add(generateBt);

		clearBt = new JButton("クリア");
		clearBt.addActionListener(buildController.getClearTextAction());
		menuPanel.add(clearBt);
	}


	@Override
	protected void setDocument() {
		editorpane.setDocument(iptModel);	// DocumentModelのメンバPlainDocをセット
	}
}
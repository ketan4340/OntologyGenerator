package demonstration;

import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import edu.uci.ics.jung.graph.Graph;

public class InputTextView extends AbstractEditorView{
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

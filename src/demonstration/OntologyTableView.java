package demonstration;

import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

public class OntologyTableView extends JPanel{
	/**** Model ****/
	private OntologyModel ontModel;

	/**** Controller ****/
	//private UseOntologyController useController;
	//private BuildOntologyController buildController;

	/**** View ****/
	private JFrame parentFrame;

	private JTable table;


	public OntologyTableView(UseOntologyController uCtrl, BuildOntologyController bCtrl) {
		//this.useController = uCtrl;
		//this.buildController = bCtrl;

		// controllerにこのviewインスタンスを持たせる
		uCtrl.setOntologyView(this);
		bCtrl.setOntologyView(this);

		// Modelを参照のために保持する
		ontModel = uCtrl.getOntModel();

		table = new JTable(ontModel);

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void setParentFrame(JFrame pf) {
		this.parentFrame = pf;
	}
	public JFrame getParentFrame() {
		return parentFrame;
	}
}
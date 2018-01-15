package demo.ontology;

import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

import demo.controller.BuildOntologyController;
import demo.controller.UseOntologyController;

public class OntologyTableView extends JPanel{
	/**** Model ****/
	private OntologyModel ontModel;

	/**** Controller ****/
	//private UseOntologyController useController;
	//private BuildOntologyController buildController;

	/**** View ****/
	private JFrame parentFrame;

	private JTable table;


	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	/* 自然文から構築されたオントロジー */
	public OntologyTableView(UseOntologyController uCtrl, BuildOntologyController bCtrl) {
		super(new BorderLayout());
		// controllerにこのviewインスタンスを持たせる
		uCtrl.setSubOntologyTable(this);
		bCtrl.setOntologyTable(this);

		// Modelを参照のために保持する
		ontModel = uCtrl.getOntologyModel();

		table = new JTable(ontModel);

		this.add(new JScrollPane(table), BorderLayout.CENTER);
	}
	/* 構築済みオントロジーから何らかの基準で抜き出したオントロジー */
	public OntologyTableView(UseOntologyController uCtrl) {
		super(new BorderLayout());
		// controllerにこのviewインスタンスを持たせる
		uCtrl.setSubOntologyTable(this);

		// Modelを参照のために保持する
		ontModel = uCtrl.getSubOntlogyModel();

		table = new JTable(ontModel);

		this.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void setParentFrame(JFrame pf) {
		this.parentFrame = pf;
	}
	public JFrame getParentFrame() {
		return parentFrame;
	}
}
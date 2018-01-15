package demo.controller;

import java.awt.event.ActionListener;

import data.original.Ontology;
import demo.ontology.OntologyModel;
import demo.ontology.OntologyTableView;
import demo.textField.AbstractDocumentModel;
import demo.textField.InputTextModel;
import demo.textField.InputTextView;


public class BuildOntologyController extends AbstractEditorController{
	/*** Model ***/
	private InputTextModel iptModel = (InputTextModel) super.docModel;
	private OntologyModel ontModel;

	/*** View ***/
	private InputTextView iptView = (InputTextView) super.edtView;
	private OntologyTableView ontTable;


	/*************************/
	/****** コンストラクタ ******/
	/*************************/
	public BuildOntologyController(AbstractDocumentModel iptModel, OntologyModel ontModel) {
		super(iptModel);
		this.ontModel = ontModel;
	}


	/** ActionListener **/
	/* RunGeneratorボタンの実装 */
	private ActionListener generateAction = (event -> {
		Ontology ontology = iptModel.runGenerator();
		ontModel.addAllTriples(ontology.getTriples());
	});


	public InputTextView getInputTextView() {
		return iptView;
	}
	public void setInputTextView(InputTextView iptView) {
		this.iptView = iptView;
	}
	public OntologyTableView getOntologyTable() {
		return ontTable;
	}
	public void setOntologyTable(OntologyTableView ontView) {
		this.ontTable = ontView;
	}
	public InputTextModel getInputTextModel() {
		return iptModel;
	}
	public OntologyModel getOntologyModel() {
		return ontModel;
	}
	public ActionListener getGenerateAction() {
		return generateAction;
	}
}

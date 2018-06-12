package main;

import demo.WholeView;
import demo.controller.BuildOntologyController;
import demo.controller.UseOntologyController;
import demo.ontology.OntologyModel;
import demo.ontology.OntologyTableView;
import demo.textField.DocumentModel;
import demo.textField.DocumentView;
import demo.textField.InputTextModel;
import demo.textField.InputTextView;

public class Demonstration{
	/****************************************/
	/**********    Main  Method    **********/
	/****************************************/
	public static void main(String[] args) {
		new Demonstration().execute();
	}
	

	private void execute() {
		/** Model生成 **/
		InputTextModel iptModel = new InputTextModel();
		OntologyModel ontModel = new OntologyModel();
		DocumentModel docModel = new DocumentModel();
		OntologyModel subOntModel = new OntologyModel();

		/** Controller生成 **/
		BuildOntologyController buildCtrl = new BuildOntologyController(iptModel, ontModel);
		UseOntologyController useCtrl = new UseOntologyController(docModel, ontModel, subOntModel);

		/** View生成 **/
		InputTextView iptView = new InputTextView(buildCtrl);
		OntologyTableView ontView = new OntologyTableView(useCtrl, buildCtrl);
		DocumentView docView = new DocumentView(useCtrl);

		/*** 全体図構築 ***/
		new WholeView(iptView, ontView, docView);
	}
}
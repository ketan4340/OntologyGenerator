package main;

import demo.BuildOntologyController;
import demo.DocumentModel;
import demo.DocumentView;
import demo.InputTextModel;
import demo.InputTextView;
import demo.OntologyModel;
import demo.OntologyTableView;
import demo.UseOntologyController;
import demo.WholeView;

public class Main{
	public static void main(String[] args) {

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
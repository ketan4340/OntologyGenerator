package japaneseParse;

import demo.BuildOntologyController;
import demo.DocumentModel;
import demo.DocumentView;
import demo.InputTextModel;
import demo.InputTextView;
import demo.MainController;
import demo.OntologyGraphView;
import demo.OntologyModel;
import demo.OntologyTableView;
import demo.SubOntologyView;
import demo.UseOntologyController;
import demo.WholeView;

public class Main{
	public static void main(String[] args) {
		// JavaFX未使用
		//new WholeView(new MainController(new InputTextModel(), new OntologyModel(), new DocumentModel()));

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
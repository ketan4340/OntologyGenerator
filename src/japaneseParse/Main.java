package japaneseParse;

import demonstration.BuildOntologyController;
import demonstration.DocumentModel;
import demonstration.DocumentView;
import demonstration.InputTextModel;
import demonstration.InputTextView;
import demonstration.MainController;
import demonstration.OntologyGraphView;
import demonstration.WholeView;
import demonstration.OntologyModel;
import demonstration.OntologyTableView;
import demonstration.UseOntologyController;

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
package demonstration;

import javax.swing.JPanel;

public class OntologyTableView extends JPanel{
	/**** Model ****/
	private OntologyModel ontModel;

	/**** Controller ****/
	private UseOntologyController controller;

	/**** View ****/

	public OntologyTableView(UseOntologyController ctrl) {
		this.controller = ctrl;
		// controllerにこのviewインスタンスを持たせる
		//controller.setMainView(this);

		// Modelを参照のために保持する
		ontModel = controller.getOntModel();
	}
}
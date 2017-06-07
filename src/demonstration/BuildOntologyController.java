package demonstration;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.text.BadLocationException;

public class BuildOntologyController extends AbstractEditorController{
	/*** Model ***/
	private InputTextModel iptModel = (InputTextModel) super.docModel;
	private OntologyModel ontModel;

	/*** View ***/
	private InputTextView iptView = (InputTextView) super.edtView;
	private OntologyTableView ontView;

	public BuildOntologyController(AbstractDocumentModel iptModel, OntologyModel ontModel) {
		super(iptModel);
		//this.iptModel = (InputTextModel) iptModel;
		this.ontModel = ontModel;
	}


	/** ActionListener **/
	/* RunGeneratorボタンの実装 */
	private ActionListener generateAction = (event -> {
		List<String[]> triples = iptModel.runGenerator();
		ontModel.addAllTriples(triples);
	});


	public InputTextView getInputTextView() {
		return iptView;
	}
	public void setInputTextView(InputTextView iptView) {
		this.iptView = iptView;
	}
	public OntologyTableView getOntologyView() {
		return ontView;
	}
	public void setOntologyView(OntologyTableView ontView) {
		this.ontView = ontView;
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

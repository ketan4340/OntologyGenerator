package modules.relationExtract;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class RDFRules {

	private List<RDFRule> rules;
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public RDFRules(List<RDFRule> rules) {
		setRules(rules);
	}
	
	
	/****************************************/
	/**********    Member Method   **********/
	/****************************************/
	public Model expand(Model model) {
		rules.stream()
			.map(r -> r.expands(model))
			.forEach(model::union);
		return model;
	}

	public Model convert(Model targetModel) {
		Model convertedModel = ModelFactory.createDefaultModel();
		rules.stream()
			.map(r -> r.converts(targetModel))
			.forEach(convertedModel::add);
		return convertedModel;
	}

	/****************************************/
	/**********   Getter, Setter   **********/
	/****************************************/
	public List<RDFRule> getRules() {
		return rules;
	}
	public void setRules(List<RDFRule> rules) {
		this.rules = rules;
	}
	
	/****************************************/
	/**********   Object  Method   **********/
	/****************************************/
	@Override
	public String toString() {
		return rules.stream()
				.map(r -> r.toString())
				.collect(Collectors.joining("\n"));
	}	
}
package modules.relationExtract;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class RDFRules {

	private LinkedHashSet<AbstractRDFRule> rules;
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public RDFRules(LinkedHashSet<AbstractRDFRule> rules) {
		setRules(rules);
	}
	public RDFRules(List<RDFRule> rules) {
		setRules(new LinkedHashSet<>(rules));
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
	public LinkedHashSet<AbstractRDFRule> getRules() {
		return rules;
	}
	public void setRules(LinkedHashSet<AbstractRDFRule> rules) {
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
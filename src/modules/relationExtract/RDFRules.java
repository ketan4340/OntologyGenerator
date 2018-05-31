package modules.relationExtract;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

public class RDFRules {

	private LinkedHashSet<AbstractRDFRule> rules;
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public RDFRules(LinkedHashSet<AbstractRDFRule> rules) {
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
	
	public Map<Model, Integer> convert(Model model) {
		return rules.stream()
				.collect(Collectors.toMap(r -> r.converts(model), r -> r.id()));
	}
	
	public List<String> toStringList() {
		return rules.stream().map(AbstractRDFRule::toString).collect(Collectors.toList());
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
				.map(AbstractRDFRule::toString)
				.collect(Collectors.joining("\n"));
	}	
}
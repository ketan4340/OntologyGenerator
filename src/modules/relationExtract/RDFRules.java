package modules.relationExtract;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class RDFRules {

	private Set<RDFRule> rules;
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public RDFRules(Collection<? extends RDFRule> c) {
		this.rules = new HashSet<>(c);
	}
	
	
	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	public Model extend(Model targetModel) {
		rules.stream()
			.map(r -> r.expands(targetModel))
			.forEach(targetModel::union);
		return targetModel;
	}

	public Model convert(Model targetModel) {
		Model model = ModelFactory.createDefaultModel();
		rules.stream()
			.map(r -> r.converts(targetModel))
			.forEach(model::add);
		/*
		System.out.println("in RDFRules#convert");
		model.write(System.out, "N-TRIPLE"); // TODO
		*/
		return model;
	}


	/**********************************/
	/**********    Getter    **********/
	/**********************************/
	public Set<RDFRule> getRules() {
		return rules;
	}
	
	
	/**********************************/
	/********** Objectメソッド **********/
	/**********************************/
	@Override
	public String toString() {
		return rules.stream()
				.map(r -> r.toString())
				.collect(Collectors.joining("\n"));
	}	
}
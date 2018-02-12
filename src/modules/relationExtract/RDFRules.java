package modules.relationExtract;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class RDFRules {

	private List<RDFRule> rules;
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public RDFRules(List<RDFRule> c) {
		this.rules = new ArrayList<>(c);
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
	public List<RDFRule> getRules() {
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
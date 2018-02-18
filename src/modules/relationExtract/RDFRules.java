package modules.relationExtract;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
		
		//TODO
		try (final OutputStream os = Files.newOutputStream(Paths.get("./tmp/log/JenaModel/JASSModel.nt"))) {
			targetModel.write(os, "N-TRIPLE"); // TODO			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//TODO
		
		return targetModel;
	}

	public Model convert(Model targetModel) {
		Model convertedModel = ModelFactory.createDefaultModel();
		rules.stream()
			.map(r -> r.converts(targetModel))
			.forEach(convertedModel::add);
		return convertedModel;
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
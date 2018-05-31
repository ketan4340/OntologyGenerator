package modules;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.jena.rdf.model.Model;

import data.RDF.RDFSerialize;
import data.id.SentenceIDMap;
import data.id.StatementIDMap;
import modules.relationExtract.RDFRules;

public class OutputManager {
	private static final String RUNTIME = new SimpleDateFormat("MMdd-HHmm").format(Calendar.getInstance().getTime());

	private static final Path PATH_DIVIDED_SENTENCES = Paths.get("./tmp/log/text/dividedText"+RUNTIME+".txt");
	private static final Path PATH_GENERATED_ONTOLOGY_TURTLE = Paths.get("./dest/rdf/turtle/ontology"+RUNTIME+RDFSerialize.Turtle.getExtension());
	private static final Path PATH_GENERATED_ONTOLOGY_RDFXML = Paths.get("./dest/rdf/rdfxml/ontology"+RUNTIME+RDFSerialize.RDF_XML.getExtension());
	private static final Path PATH_TRIPLE_CSV = Paths.get("./dest/csv/RDFtriple"+RUNTIME+".csv");
	private static final Path PATH_RULES = Paths.get("./tmp/log/rule/rule"+RUNTIME+".rule");
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public OutputManager() {
		
	}
	

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public void outputDividedSentences(SentenceIDMap sentenceMap) {
		try {
			Files.write(PATH_DIVIDED_SENTENCES, sentenceMap.toStringList());
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void outputRDFRules(RDFRules rules) {
		try {
			Files.write(PATH_RULES, rules.toStringList());
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void outputOntology(Model model) {
		try (final OutputStream os = Files.newOutputStream(PATH_GENERATED_ONTOLOGY_TURTLE)) {
			model.write(os, "TURTLE");
		} catch (IOException e) {e.printStackTrace();}
		try (final OutputStream os = Files.newOutputStream(PATH_GENERATED_ONTOLOGY_RDFXML)) {
			model.write(os, "RDF/XML");
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void outputIDAsCSV(StatementIDMap ontologyMap) {
		try {
			Files.write(PATH_TRIPLE_CSV, ontologyMap.toStringList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
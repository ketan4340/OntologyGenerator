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
import data.id.IDRelation;
import data.id.ModelIDMap;
import data.id.SentenceIDMap;
import modules.relationExtract.RDFRules;

public class OutputManager {
	private static final String RUNTIME = new SimpleDateFormat("MMdd-HHmm").format(Calendar.getInstance().getTime());

	private static final Path PATH_DIVIDED_SENTENCES = Paths.get("../OntologyGenerator/tmp/log/text/dividedText"+RUNTIME+".txt");
	private static final Path PATH_JASSMODEL_TURTLE = Paths.get("../OntologyGenerator/tmp/log/jass/jass"+RUNTIME+RDFSerialize.Turtle.getExtension());
	private static final Path PATH_RULES = Paths.get("../OntologyGenerator/tmp/log/rule/rule"+RUNTIME+".rule");

	private static final Path PATH_GENERATED_ONTOLOGY_TURTLE = Paths.get("../OntologyGenerator/dest/rdf/turtle/ontology"+RUNTIME+RDFSerialize.Turtle.getExtension());
	private static final Path PATH_GENERATED_ONTOLOGY_RDFXML = Paths.get("../OntologyGenerator/dest/rdf/rdfxml/ontology"+RUNTIME+RDFSerialize.RDF_XML.getExtension());
	private static final Path PATH_TRIPLE_CSV = Paths.get("../OntologyGenerator/dest/csv/RDFtriple"+RUNTIME+".csv");
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public OutputManager() {}
	

	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public void outputDividedSentences(SentenceIDMap sentenceMap) {
		try {
			Files.write(PATH_DIVIDED_SENTENCES, sentenceMap.toStringList());
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void outputJASSGraph(ModelIDMap jassMap) {
		try (final OutputStream os = Files.newOutputStream(PATH_JASSMODEL_TURTLE)) {
			jassMap.uniteModels().write(os, "TURTLE");
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void outputRDFRules(RDFRules rules) {
		try {
			Files.write(PATH_RULES, rules.toStringList());
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void outputOntology(ModelIDMap ontologyMap) {
		Model unionModel = ontologyMap.uniteModels();
		try (final OutputStream os = Files.newOutputStream(PATH_GENERATED_ONTOLOGY_TURTLE)) {
			unionModel.write(os, "TURTLE");
		} catch (IOException e) {e.printStackTrace();}
		try (final OutputStream os = Files.newOutputStream(PATH_GENERATED_ONTOLOGY_RDFXML)) {
			unionModel.write(os, "RDF/XML");
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void outputIDAsCSV(IDRelation IDRelation) {
		try {
			Files.write(PATH_TRIPLE_CSV, IDRelation.toStringList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
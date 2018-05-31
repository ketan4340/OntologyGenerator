package modules;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;

import data.RDF.RDFSerialize;
import data.id.IDTuple;
import data.id.ModelIDMap;
import data.id.SentenceIDMap;

public class OutputManager {
	private static final String RUNTIME = new SimpleDateFormat("MMdd-HHmm").format(Calendar.getInstance().getTime());

	private static final Path PATH_DIVIDED_SENTENCES = Paths.get("./tmp/log/text/dividedText"+RUNTIME+".txt");
	private static final Path PATH_GENERATED_ONTOLOGY_TURTLE = Paths.get("./dest/rdf/turtle/ontology"+RUNTIME+RDFSerialize.Turtle.getExtension());
	private static final Path PATH_GENERATED_ONTOLOGY_RDFXML = Paths.get("./dest/rdf/rdfxml/ontology"+RUNTIME+RDFSerialize.RDF_XML.getExtension());
	private static final Path PATH_TRIPLE_CSV = Paths.get("./dest/csv/RDFtriple"+RUNTIME+".csv");
	
	
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
			Files.write(PATH_DIVIDED_SENTENCES, sentenceMap.stringList(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
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
	public void outputCSV2(ModelIDMap ontologyMap) {
		List<String> stringList = ontologyMap.IDList().stream().map(IDTuple::toCSV).collect(Collectors.toList());
		try {
			Files.write(PATH_TRIPLE_CSV, stringList, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
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

import data.id.IDTuple;
import data.id.ModelIDMap;
import data.id.SentenceIDMap;

public class OutputManager {
	public static SimpleDateFormat sdf = new SimpleDateFormat("MMddHH:mm");
	Path csvFile = Paths.get("dest/csv/relation"+sdf.format(Calendar.getInstance().getTime())+".csv");
	
	
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
			Files.write(Paths.get("tmp/log/text/dividedText.txt"), sentenceMap.stringList(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {e.printStackTrace();}
	}
	public void outputOntology(Model model) {
		try (final OutputStream os = Files.newOutputStream(Paths.get("./tmp/log/JenaModel/ontologyModel.ttl"))) {
			model.write(os, "TURTLE");
		} catch (IOException e) {e.printStackTrace();} 
	}
	public void outputCSV2(ModelIDMap ontologyMap) {
		List<String> stringList = ontologyMap.IDList().stream().map(IDTuple::toCSV).collect(Collectors.toList());
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmm");
		Path csvFile = Paths.get("dest/csv/relation"+sdf.format(Calendar.getInstance().getTime())+".csv");
		try {
			Files.write(csvFile, stringList, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
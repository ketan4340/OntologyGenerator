package modules;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.jena.rdf.model.Model;

import data.RDF.rule.RDFRules;
import data.RDF.rule.RDFRulesSet;
import data.id.IDRelation;
import data.id.ModelIDMap;
import data.id.SentenceIDMap;

public class OutputManager {


	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	public OutputManager() {}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public void outputDividedSentences(SentenceIDMap sentenceMap, Path path) {
		try {
			Files.createDirectories(path.getParent());
			Files.write(path, sentenceMap.toStringList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void outputJASSGraph(ModelIDMap jassMap, Path path) {
		try (final OutputStream os = Files.newOutputStream(path)) {
			Files.createDirectories(path.getParent());
			jassMap.uniteModels().write(os, "TURTLE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void outputRDFRules(RDFRules rules, Path path) {
		try {
			Files.createDirectories(path.getParent());
			Files.write(path, rules.toStringList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void outputRDFRulesSet(RDFRules exrules, RDFRulesSet rules, Path path) {
		try {
			Files.createDirectories(path.getParent());
			Files.write(path, exrules.toStringList());
			Files.write(path, rules.toStringList(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void outputOntologyAsTurtle(Model model, Path path) {
		try (final OutputStream os = Files.newOutputStream(path)) {
			Files.createDirectories(path.getParent());
			model.write(os, "TURTLE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void outputOntologyAsRDFXML(Model model, Path path) {
		try (final OutputStream os = Files.newOutputStream(path)) {
			Files.createDirectories(path.getParent());
			model.write(os, "RDF/XML");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void outputIDAsCSV(IDRelation IDRelation, Path path) {
		try {
			Files.createDirectories(path.getParent());
			Files.write(path, IDRelation.toStringList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
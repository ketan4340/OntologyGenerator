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
import data.id.SentenceIDMap;

public final class OutputManager {
	private static OutputManager INSTANCE = new OutputManager();

	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	private OutputManager() {}

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public static OutputManager getInstance() { return INSTANCE; }

	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public void writeSentences(SentenceIDMap sentenceMap, Path path) {
		try {
			Files.createDirectories(path.getParent());
			Files.write(path, sentenceMap.toStringList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeJassModel(Model jass, Path path) {
		try (final OutputStream os = Files.newOutputStream(path)) {
			Files.createDirectories(path.getParent());
			jass.write(os, "TURTLE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeRDFRules(RDFRules rules, Path path) {
		try {
			Files.createDirectories(path.getParent());
			Files.write(path, rules.toStringList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeRDFRulesSet(RDFRules exrules, RDFRulesSet rules, Path path) {
		try {
			Files.createDirectories(path.getParent());
			Files.write(path, exrules.toStringList());
			Files.write(path, rules.toStringList(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeOntologyAsTurtle(Model model, Path path) {
		try (final OutputStream os = Files.newOutputStream(path)) {
			Files.createDirectories(path.getParent());
			model.write(os, "TURTLE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeOntologyAsRDFXML(Model model, Path path) {
		try (final OutputStream os = Files.newOutputStream(path)) {
			Files.createDirectories(path.getParent());
			model.write(os, "RDF/XML");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeIDTupleAsCSV(IDRelation IDRelation, Path path) {
		try {
			Files.createDirectories(path.getParent());
			Files.write(path, IDRelation.toStringList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
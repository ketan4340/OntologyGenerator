package main;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import modules.OutputManager;

public class RDFFormatConverter {

	public static void main(String[] args) {

		Path p = Paths.get("dest/turtle/ontology1122-1900.ttl");
		
		Model m = ModelFactory.createDefaultModel();
		
		m.read(p.toUri().toString());
		
		Path pout = Paths.get("dest/ontology1122-1900.nt");

		try (final OutputStream os = Files.newOutputStream(pout)) {
			Files.createDirectories(pout.getParent());
			m.write(os, "N-TRIPLE");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

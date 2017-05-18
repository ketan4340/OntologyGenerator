package demonstration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.table.DefaultTableModel;

import com.sun.xml.internal.ws.util.NoCloseOutputStream;

public class OntologyModel extends DefaultTableModel{
	private List<String[]> triples;

	private static String[] columnNames = {"Subject", "Predicate", "Object"};

	public OntologyModel() {
		super(columnNames, 0);
		triples = new ArrayList<String[]>();
	}
	public OntologyModel(final MainView view) {
		this();
	}

	public String[] getColumnNames() {
		return columnNames;
	}
	public List<String[]> getTriples() {
		return triples;
	}
	public void setTriples(List<String[]> triples) {
		this.triples = triples;
	}

	private void addTriple(String[] newTriple) {
		if(newTriple.length != 3) {
			System.err.println("ERROR: triple is not composed of 3 concepts.");
		}else {
			triples.add(newTriple);
			addRow(newTriple);
		}
	}
	public void addAllTriples(List<String[]> newTriples) {
		for(String[] newTriple: newTriples) {
			addTriple(newTriple);
		}
	}
}

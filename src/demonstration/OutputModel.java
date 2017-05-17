package demonstration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.table.DefaultTableModel;

import com.sun.xml.internal.ws.util.NoCloseOutputStream;

public class OutputModel extends Observable{
	private List<String[]> triples;
	private DefaultTableModel tableModel;

	private String[] columnNames = {"Subject", "Predicate", "Object"};

	public OutputModel() {
		triples = new ArrayList<String[]>();
		tableModel = new DefaultTableModel(columnNames, 0);
	}
	public OutputModel(final MainView view) {
		this();
		addObserver(view);
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}
	public String[] getColumnNames() {
		return columnNames;
	}
	public List<String[]> getTriples() {
		return triples;
	}
	public void setTriples(List<String[]> triples) {
		this.triples = triples;

		setChanged();
        notifyObservers();
	}

	private void addTriple(String[] newTriple) {
		if(newTriple.length != 3) {
			System.err.println("ERROR: triple is not composed of 3 concepts.");
		}else {
			triples.add(newTriple);
			tableModel.addRow(newTriple);
		}
		/*
		setChanged();
		notifyObservers();
		*/
	}
	public void addAllTriples(List<String[]> newTriples) {
		for(String[] newTriple: newTriples) {
			addTriple(newTriple);
		}
	}
}

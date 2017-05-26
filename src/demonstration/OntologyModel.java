package demonstration;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class OntologyModel extends DefaultTableModel{
	private static int TRIPLE = 3;
	private static String[] columnNames = {"Subject", "Predicate", "Object"};

	public OntologyModel() {
		super(columnNames, 0);
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	private void addTriple(String[] newTriple) {
		if(newTriple.length != TRIPLE) {
			System.err.println("ERROR: this triple is not composed of 3 concepts." + newTriple);
		}else {
			addRow(newTriple);
		}
	}
	public void addAllTriples(List<String[]> newTriples) {
		for(String[] newTriple: newTriples) {
			addTriple(newTriple);
		}
	}

	public String[] getRow(int rowNum) {
		String[] row = new String[TRIPLE];
		for(int t = 0; t<TRIPLE; t++) {
			row[t] = (String) getValueAt(rowNum, t);
		}
		return row;
	}

	public List<String[]> getAllTable() {
		List<String[]> table = new LinkedList<String[]>();
		for(int r = 0; r<getRowCount(); r++) {
			table.add(getRow(r));
		}
		return table;
	}

	private List<String[]> getCommonConcepts(String concept, int s_p_o) {
		List<String[]> commonRowList = new LinkedList<String[]>();
		for(final String[] row: getAllTable()) {
			if(row[s_p_o].equals(concept)) {		// 共通のsまたはpまたはoを持つ行を集める
				commonRowList.add(row);
			}
		}
		return commonRowList;
	}
	public List<String[]> getPO(String subject) {
		return getCommonConcepts(subject, Triple.SUBJECT);
	}
	public List<String[]> getSO(String predicate) {
		return getCommonConcepts(predicate, Triple.PREDICATE);
	}
	public List<String[]> getSP(String object) {
		return getCommonConcepts(object, Triple.OBJECT);
	}
}

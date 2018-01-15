package demo;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.table.DefaultTableModel;

import data.original.RDFTriple;

public class OntologyModel extends DefaultTableModel{
	private static final String[] columnNames = {"Subject", "Predicate", "Object"};

	/*********************/
	/**** コンストラクタ ****/
	/*********************/
	public OntologyModel() {
		super(columnNames, 0);
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	private void addTriple(RDFTriple newTriple) {
		addRow(Stream.of(newTriple.toArray()).map(rsc -> rsc.toString()).toArray(String[]::new));
	}
	public void addAllTriples(List<RDFTriple> newTriples) {
		for(RDFTriple newTriple: newTriples) {
			addTriple(newTriple);
		}
	}

	public String[] getRow(int rowNum) {
		return new String[]{
				(String) getValueAt(rowNum, RDFTriple.S),
				(String) getValueAt(rowNum, RDFTriple.P),
				(String) getValueAt(rowNum, RDFTriple.O)
				};
	}

	public List<String[]> getAllTable() {
		List<String[]> table = new LinkedList<String[]>();
		for(int r = 0; r<getRowCount(); r++) {
			table.add(getRow(r));
		}
		return table;
	}

	public void clear() {
		setRowCount(0);
	}

	/**
	 * トリプルの主語，述語，目的語のいずれか(s_p_oで指定)が入力文字列と一致するトリプルを集める.
	 * @param concept 検索文字列
	 * @param s_p_o 主語，述語，目的語に対応する整数(0,1,2)
	 * @return
	 */
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
		return getCommonConcepts(subject, RDFTriple.S);
	}
	public List<String[]> getSO(String predicate) {
		return getCommonConcepts(predicate, RDFTriple.P);
	}
	public List<String[]> getSP(String object) {
		return getCommonConcepts(object, RDFTriple.O);
	}
}

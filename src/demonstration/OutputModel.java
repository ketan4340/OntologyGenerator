package demonstration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class OutputModel extends Observable{
	List<String[]> triples;

	public OutputModel() {
		triples = new ArrayList<String[]>();
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
		}
	}
}

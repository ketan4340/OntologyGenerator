package data.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IDRelation {

	private Set<IDTuple> tupleList;
	

	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public IDRelation(Collection<? extends IDTuple> tupleList) {
		this.tupleList = new HashSet<>(tupleList);
	}
	public IDRelation() {
		this.tupleList = new HashSet<>();
	}
	
	

	/***********************************/
	/**********  MemberMethod **********/
	/***********************************/
	private List<IDTuple> toListOrderedBySentenceID() {
		return tupleList.stream()
				.sorted(Comparator.comparingInt(IDTuple::getLongSentenceID)
						.thenComparing(Comparator.comparingInt(IDTuple::getShortSentenceID)))
				.collect(Collectors.toList());
	}
	public boolean write(Path filePath) {
		List<String> lines = toListOrderedBySentenceID().stream().map(IDTuple::toCSV).collect(Collectors.toList());
		if (lines == null) return false; 
		try {
			Files.write(filePath, lines);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**********************************/
	/********** ObjectMethod **********/
	/**********************************/	
}
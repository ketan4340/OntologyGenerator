package data.id;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import data.RDF.MyJenaModel;

public class ModelIDMap extends IDLinkedMap<MyJenaModel> {
	private static final long serialVersionUID = -7922615222139193991L;
	
	
	/***********************************/
	/********** Static Method **********/
	/***********************************/
	public static ModelIDMap create(List<MyJenaModel> modelList) {
		LinkedHashMap<MyJenaModel, IDTuple> lhm = modelList.stream()
				.collect(Collectors.toMap(s -> s, s -> new IDTuple(), (e1, e2) -> e1, LinkedHashMap::new));
		return new ModelIDMap(lhm);
	}
	
	/***********************************/
	/**********  Constructor  **********/
	/***********************************/
	public ModelIDMap() {
		super();
	}
	public ModelIDMap(LinkedHashMap<MyJenaModel, IDTuple> m) {
		super(m);
	}


	/***********************************/
	/********** Member Method **********/
	/***********************************/

}
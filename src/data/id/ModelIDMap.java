package data.id;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;

public class ModelIDMap extends IDLinkedMap<Model> {
	private static final long serialVersionUID = -7922615222139193991L;
	
	public static int modelSum = 0;
	
	/****************************************/
	/**********     Constructor    **********/
	/****************************************/
	public ModelIDMap() {
		super();
	}
	public ModelIDMap(int initialCapacity) {
		super(initialCapacity);
	}
	public ModelIDMap(LinkedHashMap<Model, IDTuple> m) {
		super(m);
	}


	/****************************************/
	/**********   Member  Method   **********/
	/****************************************/
	public void setModelID() {}
	/*
	//TODO
	// 他のLongSentenceIDなんかはこの手のメソッドでID登録しているからいつか同じようにしたい
	例)
	public void setRuleID() {
		forEach((k, v) -> v.setRDFRuleID(k.id()));
	}
	*/
	public Model uniteModels() {
		Model unionModel = ModelFactory.createDefaultModel();
		forEachKey(unionModel::add);
		return unionModel;
	}
	
	public ModelIDMap replaceModel2Models(Map<Model, List<Model>> replaceMap) {
		ModelIDMap newModelMap = new ModelIDMap();
		replaceMap.forEach((md, mds) -> 
		mds.forEach(m -> newModelMap.put(md, get(md).clone()))
		);
		return newModelMap;
	}
	
	public StatementIDMap replaceModel2Statements(Map<Model, List<Statement>> replaceMap) {
		StatementIDMap newStatementMap = new StatementIDMap();
		replaceMap.forEach((m, sts) -> sts.forEach(st -> newStatementMap.put(st, get(m).clone())));
		return newStatementMap;
	}

}
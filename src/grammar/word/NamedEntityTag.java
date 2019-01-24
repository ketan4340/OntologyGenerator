package grammar.word;

import org.apache.jena.rdf.model.Resource;

import data.RDF.vocabulary.JASS;

public enum NamedEntityTag {
    // 判定困難
    OPTIONAL(JASS.optional),
    
	/* 固有名詞的表現 */
	// 組織名、政府組織名 
    ORGANIZATION(JASS.organization),
    // 人名  
    PERSON(JASS.person),
    // 地名  
    LOCATION(JASS.location),
    // 固有物名 
    ARTIFACT(JASS.artifact),
    
    /* 時間表現 */
    // 日付表現
    DATE(JASS.date),
    // 時間表現
    TIME(JASS.time),

    /* 数値表現 */
    // 金額表現
    MONEY(JASS.money),
    // 割合表現
    PERCENT(JASS.percent),
    ;
    
	
	private final Resource individual;
    
    NamedEntityTag(Resource individual) {
    	this.individual = individual;
    }
    
    
    public Resource toJASS() {
    	return individual;
    }
    
    @Override
    public String toString() {
    	return name();
    }
}

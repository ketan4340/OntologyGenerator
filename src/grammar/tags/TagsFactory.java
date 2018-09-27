package grammar.tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import util.factory.Factory;

public interface TagsFactory extends Factory {
	Map<String, Set<CabochaTags>> CONSTANT_POOL = new HashMap<>();

	/** プールにすでに同等のインスタンスがあればそれを，無ければ{@code Optional#empty()}を返す. */
	static CabochaTags intern(String mainPoS, String subPoS1, String subPoS2, String subPoS3,
			String conjugation, String inflection, String infinitive, String yomi, String pronunciation) {
  		String key = mainPoS;
    	Set<CabochaTags> tagsSet = CONSTANT_POOL.computeIfAbsent(key, s -> new HashSet<>());
    	Optional<CabochaTags> tagsOpt =
    			Factory.getIfPresentSet(tagsSet, mainPoS, subPoS1, subPoS2, subPoS3,
    					conjugation, inflection, infinitive, yomi, pronunciation);
    	
    	CabochaTags tags = tagsOpt.orElseGet(() -> {
    		CabochaTags newTags = new CabochaTags(mainPoS, subPoS1, subPoS2, subPoS3,
    				conjugation, inflection, infinitive, yomi, pronunciation);
    		tagsSet.add(newTags);
    		return newTags;
    	});
		return tags;
  	}
}
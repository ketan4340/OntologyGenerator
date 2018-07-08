package grammar.morpheme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import grammar.tags.CabochaTags;
import util.factory.Factory;

public interface MorphemeFactory extends Factory {
	Map<String, Set<Morpheme>> CONSTANT_POOL = new HashMap<>();


	/** プールにすでに同等のインスタンスがあればそれを，無ければ{@code Optional#empty()}を返す. */
 	static Morpheme intern(String name, CabochaTags tags) {
  		String key = tags.mainPoS();
    	Set<Morpheme> morphemeSet = CONSTANT_POOL.computeIfAbsent(key, s -> new HashSet<>());
    	Optional<Morpheme> morphemeOpt = Factory.getIfPresentSet(morphemeSet, name, tags);
		Morpheme morpheme = morphemeOpt.orElseGet(() -> {
			Morpheme newMorpheme = new Morpheme(name, tags);
			morphemeSet.add(newMorpheme);
			return newMorpheme;
		});
		return morpheme;
  	}
}
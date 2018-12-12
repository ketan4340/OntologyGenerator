package grammar.morpheme;

import java.util.function.Supplier;

import language.pos.CabochaTags;
import util.Factory;

public final class MorphemeFactory extends Factory<Morpheme> {
	private static final MorphemeFactory FACTORY = new MorphemeFactory();
	public static MorphemeFactory getInstance() {
		return FACTORY;
	}
	
	public Morpheme getMorpheme(String name, CabochaTags tags) {
		Supplier<Morpheme> construction = () -> new Morpheme(name, tags);
		return MorphemeFactory.getInstance().intern(tags.mainPoS(), construction, name, tags);
	}
	
	
	/**
	 * プールにすでに同等のインスタンスがあればそれを，無ければ{@code Optional#empty()}を返す.
	 */
	/*
	public Morpheme intern(String name, CabochaTags tags) {
  		String key = tags.mainPoS();
    	Set<Morpheme> morphemeSet = constantPool.computeIfAbsent(key, s -> new HashSet<>());
    	Optional<Morpheme> morphemeOpt = getIfPresentSet(morphemeSet, name, tags);
		Morpheme morpheme = morphemeOpt.orElseGet(() -> {
			Morpheme newMorpheme = new Morpheme(name, tags);
			morphemeSet.add(newMorpheme);
			return newMorpheme;
		});
		return morpheme;
  	}
	*/
}

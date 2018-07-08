package util.factory;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public interface Factory {
	/** コンストラクタの引数をObject配列にする */
	Object[] initArgs();

	default boolean equivalent(Object... initArgs) {
		return Arrays.equals(this.initArgs(), initArgs);
	}

	static <E extends Factory> Optional<E> getIfPresentSet(Set<E> set, Object... initArgs) {
		for (E e : set)
			if (e.equivalent(initArgs))
				return Optional.of(e);
		return Optional.empty();
	}
}
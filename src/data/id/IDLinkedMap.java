package data.id;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

public abstract class IDLinkedMap<K> extends LinkedHashMap<K, IDTupleByStatement> {
	private static final long serialVersionUID = 4734377852049948002L;


	/* ================================================== */
	/* =================== Constructor ================== */
	/* ================================================== */
	protected IDLinkedMap() {
		super();
	}
	protected IDLinkedMap(int initialCapacity) {
		super(initialCapacity);
	}
	protected IDLinkedMap(LinkedHashMap<K, IDTupleByStatement> m) {
		super(m);
	}


	/* ================================================== */
	/* ================== Member Method ================= */
	/* ================================================== */
	public void forEachKey(Consumer<? super K> action) {
		keySet().forEach(action);
	}
	public void forEachValue(Consumer<? super IDTupleByStatement> action) {
		values().forEach(action);
	}

}

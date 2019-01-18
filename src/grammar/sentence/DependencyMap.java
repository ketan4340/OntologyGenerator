package grammar.sentence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import grammar.clause.Clause;

public class DependencyMap implements Map<Clause<?>, Clause<?>> {
	private final Map<Clause<?>, Clause<?>> map;
	
	public DependencyMap() {
		this.map = new HashMap<>();
	}
	public DependencyMap(Map<? extends Clause<?>,? extends Clause<?>> m) {
		this.map = new HashMap<>(m);
	}

	
	@Override
	public int size() {return map.size();}
	@Override
	public boolean isEmpty() {return map.isEmpty();}
	@Override
	public boolean containsKey(Object key) {return map.containsKey(key);}
	@Override
	public boolean containsValue(Object value) {return map.containsValue(value);}
	@Override
	public Clause<?> get(Object key) {return map.get(key);}
	@Override
	public Clause<?> put(Clause<?> key, Clause<?> value) {return map.put(key, value);}
	@Override
	public Clause<?> remove(Object key) {return map.remove(key);}
	@Override
	public void putAll(Map<? extends Clause<?>, ? extends Clause<?>> m) {map.putAll(m);}
	@Override
	public void clear() {map.clear();}
	@Override
	public Set<Clause<?>> keySet() {return map.keySet();}
	@Override
	public Collection<Clause<?>> values() {return map.values();}
	@Override
	public Set<Entry<Clause<?>, Clause<?>>> entrySet() {return map.entrySet();}
	@Override
	public boolean equals(Object o) {return map.equals(o);}
	@Override
	public int hashCode() {return map.hashCode();}
	@Override
	public Clause<?> getOrDefault(Object key, Clause<?> defaultValue) {return map.getOrDefault(key, defaultValue);}
	@Override
	public void forEach(BiConsumer<? super Clause<?>, ? super Clause<?>> action) {map.forEach(action);}
	@Override
	public void replaceAll(BiFunction<? super Clause<?>, ? super Clause<?>, ? extends Clause<?>> function) {map.replaceAll(function);}
	@Override
	public Clause<?> putIfAbsent(Clause<?> key, Clause<?> value) {return map.putIfAbsent(key, value);}
	@Override
	public boolean remove(Object key, Object value) {return map.remove(key, value);}
	@Override
	public boolean replace(Clause<?> key, Clause<?> oldValue, Clause<?> newValue) {return map.replace(key, oldValue, newValue);}
	@Override
	public Clause<?> replace(Clause<?> key, Clause<?> value) {return map.replace(key, value);}
	@Override
	public Clause<?> computeIfAbsent(Clause<?> key,
			Function<? super Clause<?>, ? extends Clause<?>> mappingFunction) {return map.computeIfAbsent(key, mappingFunction);}
	@Override
	public Clause<?> computeIfPresent(Clause<?> key,
			BiFunction<? super Clause<?>, ? super Clause<?>, ? extends Clause<?>> remappingFunction) {
		return map.computeIfPresent(key, remappingFunction);
	}
	@Override
	public Clause<?> compute(Clause<?> key,
			BiFunction<? super Clause<?>, ? super Clause<?>, ? extends Clause<?>> remappingFunction) {
		return map.compute(key, remappingFunction);
	}
	@Override
	public Clause<?> merge(Clause<?> key, Clause<?> value,
			BiFunction<? super Clause<?>, ? super Clause<?>, ? extends Clause<?>> remappingFunction) {
		return map.merge(key, value, remappingFunction);
	}
	
}

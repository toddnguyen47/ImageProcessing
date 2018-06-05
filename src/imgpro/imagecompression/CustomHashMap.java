/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro.imagecompression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A custom HashMap class that can find keys or first key of a value
 * @author Tho (Todd) Nguyen
 */
public class CustomHashMap<K, V> extends HashMap<K, V> {
	/**
	 * Get a set of keys of a certain value.
	 * @param value Value to obtain keys for
	 * @return Set of keys
	 * @author Tho Nguyen
	 */
	public Set<K> getKeys(V value) {
		Set<K> keys = new HashSet<> ();
		for (Map.Entry<K, V> entry : this.entrySet()) {
			if (value.equals(entry.getValue())) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	/**
	 * Get the first key encountered for a value
	 * @param value Value that you want to obtain the key for
	 * @return The first found key
	 */
	public K getFirstKey(V value) {
		K key = null;
		for (Map.Entry<K, V> entry : this.entrySet()) {
			if (value.equals(entry.getValue())) {
				key = entry.getKey();
				break;
			}
		}
		return key;
	}
}

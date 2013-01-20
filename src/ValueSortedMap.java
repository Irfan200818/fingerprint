import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

 
/**
 * This is an implementation of the Map interface, which is sorted
 * according to the natural order of the <strong>Value</strong> class,
 * or by the comparator for the <strong>Values</strong> that is provided 
 * at creation time, depending on which constructor is used.<br />
 * <br />
 * Since this class is backed by a TreeMap, it still has log(n) time 
 * cost for the containsKey, get, put and remove operations. 
 * 
 * @author Marco13, http://java-forum.org
 *
 * @param <K> The type of the keys in this ValueSortedMap
 * @param <V> The type of the values in this ValueSortedMap
 */
public class ValueSortedMap<K, V> implements Map<K, V>
{
    /**
     * The backing, sorted map (a TreeMap), which contains the 
     * key-value pairs sorted according to the value. 
     */
    private SortedMap<K, V> map;
 
    /**
     * The map which will be used for lookup operations. When two
     * keys have to be compared, then their values will be looked
     * up in this map, and the result of the comparison of the
     * values will be returned.
     */
    private Map<K, V> lookupMap = new HashMap<K, V>();
 
    /**
     * The class performing the actual comparison of the keys.
     * The values for the keys are looked up in the lookupMap.
     * Then the values will be compared. If the values for both 
     * keys are equal, then the keys themself will be compared. 
     * If the keys are not comparable, then an arbitrary (but 
     * constant) value will be returned, to indicate that the 
     * keys are not equal.<br>
     * <br>
     * The values will either be compared using the Comparator 
     * that was given in the constructor, or cast to Comparable 
     * and compared according to their natural ordering.
     */
    private class KeyByValueComparator implements Comparator<K>
    {
        /**
         * The Comparator for the values
         */
        private Comparator<? super V> valueComparator = null;
 
        /**
         * Creates a new KeyByValueComparator, which will compare the
         * values with the given Comparator. If the given comparator
         * is <tt>null</tt>, then the values will be compared 
         * according to their natural ordering.
         * 
         * @param comparator The Comparator which will be used
         * to compare the values.
         */
        KeyByValueComparator(Comparator<? super V> comparator)
        {
            if (comparator == null)
            {
                this.valueComparator = new Comparator<V>()
                {
                    @SuppressWarnings("unchecked")
                    public int compare(V a, V b)
                    {
                        Comparable<? super V> ca = (Comparable<? super V>) a;
                        return ca.compareTo(b);
                    }
                };
            }
            else
            {
                this.valueComparator = comparator;
            }
        }
 
        /**
         * Compares the values that are associated with the given
         * keys. If the values for both keys are equal, then the
         * keys themself will be compared. If the keys are not
         * comparable, then an arbitrary (but constant) value
         * will be returned, to indicate that the keys are not
         * equal.<br>
         * <br>
         * Note that the case of equal keys has already been 
         * checked in the put(K,V) method, so that this method
         * will never receive two equal keys.  
         */
        @SuppressWarnings("unchecked")
        public int compare(K a, K b)
        {
            V va = lookupMap.get(a);
            V vb = lookupMap.get(b);
 
            int valueResult = valueComparator.compare(va, vb);
            if (valueResult != 0)
            {
                return valueResult;
            }
            if (a instanceof Comparable)
            {
                @SuppressWarnings("rawtypes")
                Comparable ca = (Comparable) a;
                return ca.compareTo(b);
            }
            return System.identityHashCode(a)-System.identityHashCode(b);
        }
    }
 
    /**
     * Constructs a new, empty ValueSortedMap, using the natural ordering of 
     * its values. All values inserted into the map must implement the 
     * Comparable interface.  Furthermore, all such values must be
     * <i>mutually comparable</i>: <tt>v1.compareTo(v2)</tt> must not throw
     * a <tt>ClassCastException</tt> for any values <tt>v1</tt> and
     * <tt>v2</tt> in the map.  If the user attempts to put a value into the
     * map that violates this constraint (for example, the user attempts to
     * put a string value into a map whose values are integers), the
     * <tt>put(Object key, Object value)</tt> call will throw a
     * <tt>ClassCastException</tt>.
     */
    public ValueSortedMap()
    {
        map = new TreeMap<K, V>(new KeyByValueComparator(null));
    }
 
    /**
     * Constructs a new, empty ValueSortedMap, ordered according to the given
     * comparator.  All values inserted into the map must be <i>mutually
     * comparable</i> by the given comparator: <tt>comparator.compare(v1,
     * v2)</tt> must not throw a <tt>ClassCastException</tt> for any values
     * <tt>v1</tt> and <tt>v2</tt> in the map.  If the user attempts to put
     * a value into the map that violates this constraint, the <tt>put(Object
     * key, Object value)</tt> call will throw a
     * <tt>ClassCastException</tt>.
     *
     * @param c the comparator that will be used to order this map.
     *        If <tt>null</tt>, the natural ordering of the values will be used.
     */
    public ValueSortedMap(Comparator<? super V> c)
    {
        map = new TreeMap<K, V>(new KeyByValueComparator(c));
    }
 
    /**
     * Constructs a new ValueSortedMap containing the same mappings as the given
     * map, ordered according to the <i>natural ordering</i> of its values.
     * All values inserted into the new map must implement the Comparable 
     * interface.  Furthermore, all such values must be
     * <i>mutually comparable</i>: <tt>v1.compareTo(v2)</tt> must not throw
     * a <tt>ClassCastException</tt> for any values <tt>v1</tt> and
     * <tt>v2</tt> in the map.  This method runs in n*log(n) time.
     *
     * @param  m the map whose mappings are to be placed in this map
     * @throws ClassCastException if the values in m are not Comparable,
     *         or are not mutually comparable
     * @throws NullPointerException if the specified map is null
     */
    public ValueSortedMap(Map<? extends K, ? extends V> m)
    {
        map = new TreeMap<K, V>(m);
    }
 
    /**
     * Constructs a new ValueSortedMap map containing the same mappings as the given
     * map, ordered according to the <i>natural ordering</i> of its values.
     * All values inserted into the new map must implement the Comparable 
     * interface.  Furthermore, all such values must be
     * <i>mutually comparable</i>: <tt>v1.compareTo(v2)</tt> must not throw
     * a <tt>ClassCastException</tt> for any values <tt>v1</tt> and
     * <tt>v2</tt> in the map.  This method runs in n*log(n) time.
     *
     * @param  m the map whose mappings are to be placed in this map
     * @throws ClassCastException if the values in m are not Comparable,
     *         or are not mutually comparable
     * @throws NullPointerException if the specified map is null
     */
    public ValueSortedMap(SortedMap<? extends K, ? extends V> m)
    {
        map = new TreeMap<K, V>(m);
    }
 
    @Override
    public int size()
    {
        return map.size();
    }
 
    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }
 
    @Override
    public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }
 
    @Override
    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }
 
    @Override
    public V get(Object key)
    {
        return map.get(key);
    }
 
    @Override
    public V put(K key, V value)
    {
        if (lookupMap.containsKey(key))
        {
            map.remove(key);
        }
        lookupMap.put(key, value);
        return map.put(key, value);
    }
 
    @Override
    public V remove(Object key)
    {
        lookupMap.remove(key);
        return map.remove(key);
    }
 
    @Override
    public void putAll(Map<? extends K, ? extends V> otherMap)
    {
        lookupMap.putAll(otherMap);
        for (K k : otherMap.keySet())
        {
            put(k, otherMap.get(k));
        }
    }
 
    @Override
    public void clear()
    {
        lookupMap.clear();
        map.clear();
    }
 
    @Override
    public Set<K> keySet()
    {
        return map.keySet();
    }
 
    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        return map.entrySet();
    }
 
    @Override
    public Collection<V> values()
    {
        return map.values();
    }
 
    @Override
    public boolean equals(Object o)
    {
        return map.equals(o);
    }
 
    @Override
    public int hashCode()
    {
        return map.hashCode();
    }
 
    @Override
    public String toString()
    {
        return map.toString();
    }
}
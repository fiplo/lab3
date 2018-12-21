package laborai.lab3;

import laborai.studijosktu.*;
import java.util.*;

public class MapKTUOA<K,V> implements MapADTk<K,V> {

    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    public static final HashType DEFAULT_HASH_TYPE = HashType.DIVISION;
    private static final double MAX_LOAD_FACTOR = 0.5;
    private int numberOfEntries = 0;


    // Maišos lentelė
    protected Node<K, V>[] table;
    // Lentelėje esančių raktas-reikšmė porų kiekis
    protected int size = 0;
    // Apkrovimo faktorius
    protected float loadFactor;
    // Maišos metodas
    protected HashType ht;
    //--------------------------------------------------------------------------
    //  Maišos lentelės įvertinimo parametrai
    //--------------------------------------------------------------------------
    // Maksimalus suformuotos maišos lentelės grandinėlės ilgis
    protected int maxChainSize = 0;
    // Permaišymų kiekis
    protected int rehashesCounter = 0;
    // Paskutinės patalpintos poros grandinėlės indeksas maišos lentelėje
    protected int lastUpdatedChain = 0;
    // Lentelės grandinėlių skaičius
    protected int chainsCounter = 0;
    // Einamas poros indeksas maišos lentelėje
    protected int index = 0;
    protected int locationsUsed = 0;


    /* Klasėje sukurti 4 perkloti konstruktoriai, nustatantys atskirus maišos
     * lentelės parametrus. Jei kuris nors parametras nėra nustatomas -
     * priskiriama standartinė reikšmė.
     */
    public MapKTUOA() {
        this(DEFAULT_HASH_TYPE);
    }

    public MapKTUOA(HashType ht) {
        this(DEFAULT_INITIAL_CAPACITY, ht);
    }

    public MapKTUOA(int initialCapacity, HashType ht) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, ht);
    }

    public MapKTUOA(float loadFactor, HashType ht) {
        this(DEFAULT_INITIAL_CAPACITY, loadFactor, ht);
    }

    public MapKTUOA(int initialCapacity, float loadFactor, HashType ht) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }

        if ((loadFactor <= 0.0) || (loadFactor > 1.0)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        this.table = new Node[initialCapacity];
        this.loadFactor = loadFactor;
        this.ht = ht;
    }

    /**
     * Patikrinama ar atvaizdis yra tuščias.
     *
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Grąžinamas atvaizdyje esančių porų kiekis.
     *
     * @return Grąžinamas atvaizdyje esančių porų kiekis.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Išvalomas atvaizdis.
     */
    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
        index = 0;
        lastUpdatedChain = 0;
        maxChainSize = 0;
        rehashesCounter = 0;
        chainsCounter = 0;
    }

    /**
     * Patikrinama ar pora egzistuoja atvaizdyje.
     *
     * @param key raktas.
     * @return Patikrinama ar pora egzistuoja atvaizdyje.
     */
    @Override
    public boolean contains(K key) {
        return get(key) != null;
    }


    public int getChainsCounter() {
        return chainsCounter;
    }

    /**
     * Atvaizdis papildomas nauja pora.
     *
     * @param key raktas,
     * @param value reikšmė.
     * @return Atvaizdis papildomas nauja pora.
     */
    @Override
    public V put(K key, V value) {

        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is null in put(Key key, Value value)");
        }
        index = hash(key, ht);
//        if (table[index] == null) {
//            //chainsCounter++;
//        }

        Node<K, V> node = getInChain1(key, table[index]);
        if (node == null) {
            table[index] = new Node<>(key, value, table[index]);
            size++;

            if (size > table.length * loadFactor) {
                rehash(table[index]);
            } else {
                //lastUpdatedChain = index;
            }
        } else {
            node.value = value;
            //lastUpdatedChain = index;
        }

        return value;
    }

    private boolean isHashTableTooFull()
    {
        return locationsUsed > MAX_LOAD_FACTOR * table.length;
    } // end isHashTableTooFull


    /**
     * Pora pašalinama iš atvaizdžio.
     *
     * @param key Pora pašalinama iš atvaizdžio.
     * @return key raktas.
     */
    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in remove(Key key)");
        }

        index = hash(key, ht);
        Node<K, V> previous = null;
        for (Node<K, V> n = table[index]; n != null; n = n.next) {
            if ((n.key).equals(key)) {
                if (previous == null) {
                    table[index] = n.next;
                } else {
                    previous.next = n.next;
                }
                size--;

                if (table[index] == null) {
                    chainsCounter--;
                }
                return n.value;
            }
            previous = n;
        }
        return null;
    }

    /**
     * Grąžinama atvaizdžio poros reikšmė.
     *
     * @return Atvaizdžio poros reikšmė.
     *
     * @param key raktas.
     */
    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in get(Key key)");
        }
        index = hash(key, ht);
        Node<K, V> node = getInChain(key, table[index]);
        return (node != null) ? node.value : null;
    }


    /**
     * Permaišymas
     *
     * @param node
     */
    private void rehash(Node<K, V> node) {
        MapKTUOA mapKTU
                = new MapKTUOA(table.length * 2, loadFactor, ht);
        for (int i = 0; i < table.length; i++) {
            while (table[i] != null) {
                if (table[i].equals(node)) {
                    lastUpdatedChain = i;
                }
                mapKTU.put(table[i].key, table[i].value);
                table[i] = table[i].next;
            }
        }
        table = mapKTU.table;
        maxChainSize = mapKTU.maxChainSize;
        chainsCounter = mapKTU.chainsCounter;
        rehashesCounter++;
    }
    private void rehash1(Node<K, V> node) {
        MapKTUOA mapKTU =
                new MapKTUOA(table.length * 2, loadFactor, ht);
        for (int i = 0; i < table.length; i++) {
            while (table[i] != null) {
                if (table[i].equals(node)) {
                    lastUpdatedChain = i;
                }
                mapKTU.put(table[i].key, table[i].value);
                table[i] = table[i].next;
            }
        }
        table = mapKTU.table;
        maxChainSize = mapKTU.maxChainSize;
        chainsCounter = mapKTU.chainsCounter;
        rehashesCounter++;
    }

    /**
     * Maišos funkcijos skaičiavimas: pagal rakto maišos kodą apskaičiuojamas
     * atvaizdžio poros indeksas maišos lentelės masyve
     *
     * @param key
     * @param hashType
     * @return
     */
    private int hash(K key, HashType hashType) {
        int h = key.hashCode();
        switch (hashType) {
            case DIVISION:
                return Math.abs(h) % table.length;
            case MULTIPLICATION:
                double k = (Math.sqrt(5) - 1) / 2;
                return (int) (((k * Math.abs(h)) % 1) * table.length);
            case JCF7:
                h ^= (h >>> 20) ^ (h >>> 12);
                h = h ^ (h >>> 7) ^ (h >>> 4);
                return h & (table.length - 1);
            case JCF8:
                h = h ^ (h >>> 16);
                return h & (table.length - 1);
            default:
                return Math.abs(h) % table.length;
        }
    }
    public int getHashIndex(K key)
    {

        int hashIndex = key.hashCode() % table.length;

        if (hashIndex < 0)
        {
            hashIndex = hashIndex + table.length;
        } // end if

        return hashIndex;
    } // end getHashIndex

    /**
     * Paieška vienoje grandinėlėje
     *
     * @param key
     * @param node
     * @return
     */
    private Node getInChain(K key, Node node) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in getInChain(Key key, Node node)");
        }
        int chainSize = 0;
        for (Node<K, V> n = node; n != null; n = n.next) {
            chainSize++;
            if ((n.key).equals(key)) {
                return n;
            }
        }
        maxChainSize = Math.max(maxChainSize, chainSize + 1);
        return null;
    }

    private Node getInChain1(K key, Node node) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in getInChain(Key key, Node node)");
        }
        int chainSize = 0;
        for (Node<K, V> n = node; n != null; n = n.next) {
            if ((n.key).equals(key)) {
                return n;
            }
        }
        maxChainSize = Math.max(maxChainSize, chainSize*chainSize+1);
        return null;
    }

    @Override
    public String[][] toArray() {
        String[][] result = new String[table.length][];
        int count = 0;
        for (Node<K, V> n : table) {
            String[] list = new String[getMaxChainSize()];
            int countLocal = 0;
            while (n != null) {
                list[countLocal++] = n.toString();
                n = n.next;
            }
            result[count] = list;
            count++;
        }
        return result;
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Node<K, V> node : table) {
            if (node != null) {
                for (Node<K, V> n = node; n != null; n = n.next) {
                    result.append(n.toString()).append(System.lineSeparator());
                }
            }
        }
        return result.toString();
    }


    /**
     *Maksimalus grandinėlės ilgis.
     *
     * @return Maksimalus grandinėlės ilgis.
     */

    public int getMaxChainSize() {
        return maxChainSize;
    }

    /**
     * Grąžina  įvykusių permaišymų kiekį.
     *
     * @return Permaišymų kiekis.
     */

    public int getRehashesCounter() {
        return rehashesCounter;
    }

    /**
     * Grąžina maišos lentelės talpą.
     *
     * @return Maišos lentelės talpa.
     */

    public int getTableCapacity() {
        return table.length;
    }

    /**
     * Grąžina paskutinės papildytos grandinėlės indeksą.
     *
     * @return Paskutinės papildytos grandinėlės indeksas.
     */

    public int getLastUpdatedChain() {
        return lastUpdatedChain;
    }

    /**
     * Grąžina grandinėlių kiekį.
     *
     * @return Grandinėlių kiekis.
     */
    public void putAll(Map<K,V> map){
        map.forEach((k, v) -> {
            this.put(k, v);
        });
    }

    public Set<K> keySet(){
        HashSet<K> ret = new HashSet<K>();
        for(int i = 0; i < table.length; i++){
            if (table[i] != null) {
                ret.add(table[i].key);
            }
        }
        return ret;
    }
    public V putIfAbsent(K key, V val) {
        int index = hash(key, ht);
        Node<K,V> node = getInChain(key, table[index]);
        if (node == null) {
            table[index] = new Node<K,V> (key, val, table[index]);
            return null;
        } else {
            return node.value;
        }
	}

    @Override
    public int numberOfEmpties() {
		int sum = 0;
		for (int i = 0; i < table.length; i++) {
		    if(table[i] == null){
		        sum++;
            }
		}
		return sum;
	}
    @Override
    public boolean containsValue(V val){
		for (int i = 0; i < table.length; i++) {
			if (containsValue(table[i], val)) {
				return true;
			}
		}
		return false;
	}
    private boolean containsValue(Node<K, V> crrNode, V val){
		while (crrNode != null) {
			if (crrNode.value.equals(val)) {
				return true;
			}
			crrNode = crrNode.next;
		}
		return false;
	}

    private int hash2(K key){
        return 7 - (Math.abs(key.hashCode()) % 7);
    }

    protected class Node<K, V> {

        // Raktas
        protected K key;
        // Reikšmė
        protected V value;
        // Rodyklė į sekantį grandinėlės mazgą
        public Node<K, V> next;

        private boolean inTable; // true if entry is in table

        private Node(K searchKey, V dataValue)
        {
            key = searchKey;
            value = dataValue;
            inTable = true;
        } // end constructor

        private boolean isIn()
        {
            return inTable;
        } // end isIn

        protected Node() {
        }
         public K getKey()
        {
            return key;
        }
         private V getValue()
        {
            return value;
        } // end getValue

         private void setValue(V newValue)
        {
            value = newValue;
        } // end setValue

        protected Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
         private boolean isRemoved() // opposite of isIn
        {
            return !inTable;
        } // end isRemoved

        @Override
        public String toString() {
            return key + "=" + value;
        }

    }
}

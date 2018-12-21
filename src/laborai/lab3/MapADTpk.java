package laborai.lab3;

/**
 *
 * @author OMEN
 */
public interface MapADTpk<K, V> extends MapADTk<K, V> {

    /**
     * Grąžina maksimalų grandinėlės ilgį.
     *
     * @return Maksimalus grandinėlės ilgis.
     */
    int getMaxChainSize();

    /**
     * Grąžina maišos lentelę formuojant įvykusių permaišymų kiekį.
     *
     * @return Permaišymų kiekis.
     */
    int getRehashesCounter();

    /**
     * Grąžina maišos lentelės talpą.
     *
     * @return Maišos lentelės talpa.
     */
    int getTableCapacity();

    /**
     * Grąžina paskutinės papildytos grandinėlės indeksą.
     *
     * @return Paskutinės papildytos grandinėlės indeksas.
     */
    int getLastUpdatedChain();

    /**
     * Grąžina grandinėlių kiekį.
     *
     * @return Grandinėlių kiekis.
     */
    int getChainsCounter();
}

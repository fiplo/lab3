package laborai.lab3;

/**
 *
 * @author OMEN
 */
public interface MapADTxk<K, V> extends MapADTpk<K, V> {

    V put(String dataString);

    void load(String filePath);

    void save(String filePath);

    void println();

    void println(String title);

    /**
     * Grąžina maišos lentelės turinį, skirtą atvaizdavimui Swing ir JavaFX
     * lentelėse
     *
     * @param delimiter Poros teksto kirtiklis
     * @return Grąžina maišos lentelės turinį dvimačiu masyvu
     */
    String[][] getModelList(String delimiter);
}

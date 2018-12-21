package laborai.lab3;

import laborai.lab3.ZaidejuSukurimas;
import laborai.lab3.Zaidejas;
import laborai.demo.Timekeeper;
import laborai.gui.MyException;
import laborai.studijosktu.HashType;
import laborai.studijosktu.MapKTUx;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;

/**
 * @author eimutis
 */
public class GreitaveikosTyrimas {

    public static final String FINISH_COMMAND = "finishCommand";
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("laborai.gui.messages");

    private final BlockingQueue resultsLogger = new SynchronousQueue();
    private final Semaphore semaphore = new Semaphore(-1);
    private final Timekeeper tk;

    private final String[] TYRIMU_VARDAI = {"add0.75", "add0.25", "rem0.75", "rem0.25", "get0.75", "get0.25"};
    private final int[] TIRIAMI_KIEKIAI = {10000, 20000, 40000, 80000};

    private final MapKTUx<String, Zaidejas> zaidAtvaizdis
            = new MapKTUx(new String(), new Zaidejas(), 10, 0.75f, HashType.DIVISION);
    private final MapKTUx<String, Zaidejas> zaidAtvaizdis2
            = new MapKTUx(new String(), new Zaidejas(), 10, 0.25f, HashType.DIVISION);
    private final Queue<String> chainsSizes = new LinkedList<>();

    public GreitaveikosTyrimas() {
        semaphore.release();
        tk = new Timekeeper(TIRIAMI_KIEKIAI, resultsLogger, semaphore);
    }

    public void pradetiTyrima() {
        try {
            SisteminisTyrimas();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public void SisteminisTyrimas() throws InterruptedException {
        try {
            chainsSizes.add(MESSAGES.getString("msg4"));
            chainsSizes.add("   kiekis      " + TYRIMU_VARDAI[0] + "   " + TYRIMU_VARDAI[1]);
            for (int k : TIRIAMI_KIEKIAI) {
                Zaidejas[] zaidArray = ZaidejuSukurimas.SukurtiZaidejus(k);
                String[] zaidIdArray = ZaidejuSukurimas.gamintiZaidRaktus(k);
                zaidAtvaizdis.clear();
                zaidAtvaizdis2.clear();
                tk.startAfterPause();
                tk.start();

                for (int i = 0; i < k; i++) {
                    zaidAtvaizdis.put(zaidIdArray[i], zaidArray[i]);
                }
                tk.finish(TYRIMU_VARDAI[0]);

                String str = "   " + k + "          " + zaidAtvaizdis.getMaxChainSize();
                for (int i = 0; i < k; i++) {
                    zaidAtvaizdis2.put(zaidIdArray[i], zaidArray[i]);
                }
                tk.finish(TYRIMU_VARDAI[1]);

                str += "         " + zaidAtvaizdis2.getMaxChainSize();
                chainsSizes.add(str);
                for (String s : zaidIdArray) {
                    zaidAtvaizdis.remove(s);
                }
                tk.finish(TYRIMU_VARDAI[2]);

                for (String s : zaidIdArray) {
                    zaidAtvaizdis2.remove(s);
                }
                tk.finish(TYRIMU_VARDAI[3]);

                for (String s : zaidIdArray) {
                    zaidAtvaizdis2.get(s);
                }
                tk.finish(TYRIMU_VARDAI[4]);

                for (String s : zaidIdArray) {
                    zaidAtvaizdis2.get(s);
                }
                tk.finish(TYRIMU_VARDAI[5]);
                tk.seriesFinish();
            }

            StringBuilder sb = new StringBuilder();
            chainsSizes.stream().forEach(p -> sb.append(p).append(System.lineSeparator()));
            tk.logResult(sb.toString());
            tk.logResult(FINISH_COMMAND);
        } catch (MyException e) {
            tk.logResult(e.getMessage());
        }
    }

    public BlockingQueue<String> getResultsLogger() {
        return resultsLogger;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }
}

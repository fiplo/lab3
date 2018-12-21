package laborai.lab3;

import laborai.gui.MyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

public class ZaidejuSukurimas {
    private static final String ID_CODE = "TA";
    private static int serNR = 100;

    private Zaidejas[] Zaidejai;
    private String[] keys;
    private int kiekis = 0, idKiekis = 0;

    public static Zaidejas[] SukurtiZaidejus(int kiekis){
        Zaidejas[] zaidejai = IntStream.range(0, kiekis)
                .mapToObj(i -> new Zaidejas.Builder().buildRandom())
                .toArray(Zaidejas[]::new);
        Collections.shuffle(Arrays.asList(zaidejai));
        return zaidejai;
    }

    public static String[] gamintiZaidRaktus(int kiekis){
        String[] raktai = IntStream.range(0, kiekis)
                .mapToObj(i -> ID_CODE + (serNR++))
                .toArray(String[]::new);
        Collections.shuffle(Arrays.asList(raktai));
        return raktai;
    }

    public Zaidejas[] ZaidejuPridavimas(int aibesDydis, int aibesImtis) throws MyException{
        if(aibesImtis > aibesDydis){
            aibesImtis = aibesDydis;
        }
        Zaidejai = SukurtiZaidejus(aibesDydis);
        keys = gamintiZaidRaktus(aibesDydis);
        this.kiekis = aibesImtis;
        return Arrays.copyOf(Zaidejai, aibesImtis);
    }

    public Zaidejas SurinktiZaidejai(){
        if(Zaidejai == null){
            throw new MyException("Zaidejai yra null");
        }
        if(kiekis < Zaidejai.length){
            return Zaidejai[kiekis++];
        } else{
            throw new MyException("Zaideju kiekis daugiau negu elementu masyve");
        }
    }

    public String ZaidejaiAnalize() {
        if (keys == null){
            throw new MyException("Raktai yra null");
        }
        if (idKiekis >= keys.length){
            idKiekis = 0;
        }
        return keys[idKiekis++];
    }
}

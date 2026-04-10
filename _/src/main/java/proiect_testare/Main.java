package proiect_testare;

public class Main {
    public static void main(String[] args) {
        ProcesorComanda p = new ProcesorComanda();
        double[] preturi = {100.0, 200.0};
        System.out.println(p.calculeazaPretFinal(preturi, 1, false, 2.0));
    }
}

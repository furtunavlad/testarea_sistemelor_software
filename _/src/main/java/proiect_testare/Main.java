package proiect_testare;

public class Main {
    public static void main(String[] args) {
        ProcesorComanda p = new ProcesorComanda();
        double[] preturi = {50.0, 60.0};
        System.out.println(p.calculeazaPretFinal(preturi, 2, false, 6.0));
    }
}

package proiect_testare;

public class ProcesorComanda {

public double calculeazaPretFinal(double[] preturiProduse, int aniFidelitate, boolean areVoucher, double greutateColet) {
        
        // conditie compusa + if fara else
        if (preturiProduse == null || preturiProduse.length == 0 || greutateColet < 0 || aniFidelitate < 0) {
            throw new IllegalArgumentException("Date de intrare invalide");
        }

        double sumaInitiala = 0.0;

        // structura repetitiva
        for (int i = 0; i < preturiProduse.length; i++) {
            if (preturiProduse[i] < 0) {
                throw new IllegalArgumentException("Pretul unui produs nu poate fi negativ");
            }
            sumaInitiala += preturiProduse[i];
        }

        double reducereProcentuala = 0.0;

        // if else
        if (sumaInitiala >= 1000.0) {
            reducereProcentuala += 0.10; // 10% reducere pentru comenzi mari
        } else {
            reducereProcentuala += 0.02; // 2% reducere de baza pentru restul comenzilor
        }

        if (aniFidelitate > 0) {
            reducereProcentuala += Math.min(aniFidelitate * 0.01, 0.05); // Max 5%
        }

        double sumaDupaReducere = sumaInitiala * (1 - reducereProcentuala);

        if (areVoucher) {
            sumaDupaReducere -= 50.0;
            if (sumaDupaReducere < 0) {
                sumaDupaReducere = 0;
            }
        }

        double costLivrare = 0.0;
        if (sumaDupaReducere < 200.0) {
            costLivrare = 15.0; // taxa de baza
            if (greutateColet > 5.0) {
                costLivrare += (greutateColet - 5.0) * 2.0; // 2 RON pe kg suplimentar
            }
        }

        return sumaDupaReducere + costLivrare;
    }
}
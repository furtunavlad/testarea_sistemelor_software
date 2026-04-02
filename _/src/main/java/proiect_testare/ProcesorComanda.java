package proiect_testare;

public class ProcesorComanda {

    public double calculeazaPretFinal(double sumaInitiala, int aniFidelitate, boolean areVoucher, double greutateColet) {
        // 1. Validarea datelor (Frontiere și Excepții)
        if (sumaInitiala <= 0 || greutateColet < 0 || aniFidelitate < 0) {
            throw new IllegalArgumentException("Date de intrare invalide");
        }

        double reducereProcentuala = 0.0;

        // 2. Reducere de volum (Decizii și Condiții)
        if (sumaInitiala >= 1000.0) {
            reducereProcentuala += 0.10; // 10%
        } else if (sumaInitiala >= 500.0) {
            reducereProcentuala += 0.05; // 5%
        }

        // 3. Reducere de fidelitate (Plafonare logică)
        if (aniFidelitate > 0) {
            reducereProcentuala += Math.min(aniFidelitate * 0.01, 0.05); // Max 5%
        }

        double sumaDupaReducere = sumaInitiala * (1 - reducereProcentuala);

        // 4. Aplicare Voucher
        if (areVoucher) {
            sumaDupaReducere -= 50.0;
            // Ne asigurăm că voucherul nu face suma negativă
            if (sumaDupaReducere < 0) {
                sumaDupaReducere = 0;
            }
        }

        // 5. Calcul cost livrare
        double costLivrare = 0.0;
        if (sumaDupaReducere < 200.0) {
            costLivrare = 15.0; // taxa de bază
            if (greutateColet > 5.0) {
                // suprataxă de 2 RON pe kg suplimentar
                costLivrare += (greutateColet - 5.0) * 2.0;
            }
        }

        return sumaDupaReducere + costLivrare;
    }
}
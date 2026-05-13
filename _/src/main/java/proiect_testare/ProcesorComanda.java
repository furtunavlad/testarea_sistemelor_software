package proiect_testare;

public class ProcesorComanda {

    /**
     * Calculeaza pretul final al unei comenzi
     * @param preturiProduse Array de preturi pentru fiecare produs din comanda
     * @param aniFidelitate Numarul de ani de fidelitate ai clientului
     * @param areVoucher Daca clientul are un voucher de 50 lei
     * @param greutateColet Greutatea coletului in kg
     * @param isVIP Daca clientul este VIP
     * @return Pretul final al comenzii, rotunjit la 2 zecimale
     * @throws IllegalArgumentException daca datele de intrare sunt invalide
     */
    public double calculeazaPretFinal(double[] preturiProduse, int aniFidelitate, boolean areVoucher, double greutateColet, boolean isVIP) {
        
        // 1. Validare initiala (conditie compusa)
        if (preturiProduse == null || preturiProduse.length == 0 || greutateColet < 0 || aniFidelitate < 0) {
            throw new IllegalArgumentException("Date de intrare invalide");
        }

        double sumaInitiala = 0.0;
        
        // 2. Verificam fiecare pret (structura repetitiva)
        for (int i = 0; i < preturiProduse.length; i++) {
            if (preturiProduse[i] < 0) {
                throw new IllegalArgumentException("Pretul unui produs nu poate fi negativ");
            }
            sumaInitiala += preturiProduse[i];
        }

        double reducere = 0.0;

        // 3. Reducere de baza (conditie compusa OR)
        if (sumaInitiala >= 1000.0 || isVIP) {
            reducere = 0.10; // 10% reducere pentru comenzi mari sau VIP
        } else {
            reducere = 0.02; // 2% reducere standard
        }

        // 4. Fidelitate (conditie compusa AND)
        if (aniFidelitate > 0 && !isVIP) {
            double extraReducere = Math.min(aniFidelitate * 0.01, 0.05); // Max 5%
            reducere += extraReducere;
        }

        double sumaDupaReducere = sumaInitiala * (1.0 - reducere);

        // 5. Aplicare voucher
        if (areVoucher) {
            sumaDupaReducere -= 50.0;

            if (sumaDupaReducere < 0) {
                sumaDupaReducere = 0.0;
            }
        }

        double costLivrare = 0.0;

        // 6. Costuri de livrare
        if (sumaDupaReducere < 200.0) {
            costLivrare = 15.0; // taxa fixa
            
            // Suprataxa pentru colete grele
            if (greutateColet > 5.0) {
                costLivrare += (greutateColet - 5.0) * 2.0; // 2 lei pe kg suplimentar
            }
        }

        // Returnam rotunjit la 2 zecimale
        return Math.round((sumaDupaReducere + costLivrare) * 100.0) / 100.0;
    }
}
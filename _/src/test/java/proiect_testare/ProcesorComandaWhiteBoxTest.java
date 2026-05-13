package proiect_testare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box (structural) tests for ProcesorComanda.
 * Acopera toate ramurile, conditiile si "omoara" mutanti relevanti.
 */
class ProcesorComandaWhiteBoxTest {

    private ProcesorComanda procesor;

    @BeforeEach
    public void setUp() {
        procesor = new ProcesorComanda();
    }

    // -------------------------------------------------------------------------
    // 1. CONDITION COVERAGE: Testam clauzele multiple din validare
    // if (preturiProduse == null || preturiProduse.length == 0 || greutateColet < 0
    // || aniFidelitate < 0)
    // -------------------------------------------------------------------------
    @Test
    public void testStructural_ConditiiIndependenteValidare() {
        // Prima conditie True, restul n-au sens sa fie evaluate
        assertThrows(IllegalArgumentException.class,
                () -> procesor.calculeazaPretFinal(null, 1, false, 1.0, false));

        // A doua conditie True, restul False
        assertThrows(IllegalArgumentException.class,
                () -> procesor.calculeazaPretFinal(new double[] {}, 1, false, 1.0, false));

        // A treia conditie True, restul False
        assertThrows(IllegalArgumentException.class,
                () -> procesor.calculeazaPretFinal(new double[] { 10.0 }, 1, false, -1.0, false));

        // A patra conditie True, restul False
        assertThrows(IllegalArgumentException.class,
                () -> procesor.calculeazaPretFinal(new double[] { 10.0 }, -1, false, 1.0, false));
    }

    @Test
    public void testStructural_EroareInLoop() {
        // Intra in loop dar gaseste un pret negativ
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[] { 10.0, -5.0 }, 1, false, 1.0, false);
        });
    }

    // -------------------------------------------------------------------------
    // 2. CONDITION COVERAGE: if (sumaInitiala >= 1000.0 || isVIP)
    // Trebuie sa dovedim ca FIECARE clauza poate influenta iesirea independent
    // -------------------------------------------------------------------------
    @Test
    public void testStructural_Reducere_Cond1_True_Cond2_False() {
        // A >= 1000 (True) || VIP (False) -> primeste reducere 10%
        // 1000 * 0.9 = 900
        double result = procesor.calculeazaPretFinal(new double[] { 1000.0 }, 0, false, 2.0, false);
        assertEquals(900.0, result, 0.001);
    }

    @Test
    public void testStructural_Reducere_Cond1_False_Cond2_True() {
        // A >= 1000 (False) || VIP (True) -> primeste reducere 10% pentru ca e VIP
        // 500 * 0.9 = 450
        double result = procesor.calculeazaPretFinal(new double[] { 500.0 }, 0, false, 2.0, true);
        assertEquals(450.0, result, 0.001);
    }

    @Test
    public void testStructural_Reducere_Ambele_False() {
        // A >= 1000 (False) || VIP (False) -> primeste reducere 2%
        // 500 * 0.98 = 490
        double result = procesor.calculeazaPretFinal(new double[] { 500.0 }, 0, false, 2.0, false);
        assertEquals(490.0, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // 3. CONDITION COVERAGE: if (aniFidelitate > 0 && !isVIP)
    // -------------------------------------------------------------------------
    @Test
    public void testStructural_Fidelitate_Ambele_True() {
        // Vechime > 0 (True) && !VIP (True) -> primeste bonus vechime
        // 2 ani -> extra 2%. Total reducere = 2% + 2% = 4%.
        // 100 * 0.96 = 96. + 15 livrare = 111.
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 2, false, 2.0, false);
        assertEquals(111.0, result, 0.001);
    }

    @Test
    public void testStructural_Fidelitate_BlocataDeVIP() {
        // Vechime > 0 (True) && !VIP (False) -> NU primeste bonus de vechime, e deja
        // VIP
        // Are doar 10% reducere din VIP.
        // 100 * 0.90 = 90. + 15 livrare = 105.
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 2, false, 2.0, true);
        assertEquals(105.0, result, 0.001);
    }

    @Test
    public void testStructural_Fidelitate_AniFidelitateZero_NonVIP() {
        // Vechime > 0 (False) && !VIP (True) -> NU intra in blocul de fidelitate
        // (MC/DC: cazul FT)
        // Doar reducere standard 2%. 100 * 0.98 = 98. + 15 livrare = 113.
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 2.0, false);
        assertEquals(113.0, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // 4. BRANCH & STATEMENT COVERAGE (Decizii simple de flux si clampare negative)
    // -------------------------------------------------------------------------
    @Test
    public void testStructural_VoucherClampLaZero() {
        // if (sumaDupaReducere < 0) sumaDupaReducere = 0.0;
        // 40 * 0.98 = 39.2. Aplicam voucher de 50 -> devine -10.8 -> Clamp la 0.
        // +15 livrare = 15.0.
        double result = procesor.calculeazaPretFinal(new double[] { 40.0 }, 0, true, 2.0, false);
        assertEquals(15.0, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // 5. MUTATION TESTING - Uciderea mutantilor neechivalenti
    // -------------------------------------------------------------------------

    @Test
    public void testMutation_UcideMutantStrictMaiMare() {
        // Daca generatorul Pitest modifica "sumaInitiala >= 1000.0" in "sumaInitiala >
        // 1000.0"
        // Atunci exact la pragul de 1000, algoritmul ar da 2% reducere (980) in loc de
        // 10% (900)
        double result = procesor.calculeazaPretFinal(new double[] { 1000.0 }, 0, false, 2.0, false);
        assertEquals(900.0, result, 0.001, "A supravietuit mutantul care inlocuieste >= cu > la linia de discount");
    }

    @Test
    public void testMutation_UcideMutantMathMinMax() {
        // Daca generatorul modifica Math.min(..., 0.05) in Math.max(..., 0.05)
        // Daca testam cu 1 an: Min iti da 0.01 (corect). Max iti da 0.05 (gresit).
        // Corect: reducere 3% (2% std + 1% extra). 100 * 0.97 = 97 + 15 = 112.
        // Daca e Max: reducere 7% (2% std + 5% extra). 100 * 0.93 = 93 + 15 = 108.
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 1, false, 2.0, false);
        assertEquals(112.0, result, 0.001, "A supravietuit mutantul care inlocuieste Math.min cu Math.max");
    }

    @Test
    public void testMutation_UcideMutantPlusMinusVoucher() {
        // Daca generatorul modifica "sumaDupaReducere -= 50.0" in "+="
        // Normal: 100 * 0.98 = 98. 98 - 50 = 48. + 15 taxa = 63.
        // Mutant: 98 + 50 = 148. + 15 taxa = 163.
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, true, 2.0, false);
        assertEquals(63.0, result, 0.001, "A supravietuit mutantul de aritmetica (+ in loc de -) la voucher");
    }

    @Test
    public void testMutation_UcideMutantPretExactZero() {
        // Omoara 2 mutanti pe linia if (preturiProduse[i] < 0):
        // 1) "Substituted 0.0 with 1.0" -> devine < 1.0, ar arunca exceptie pt pret=0
        // (0 < 1.0 = true)
        // 2) "changed conditional boundary" -> devine <= 0, ar arunca exceptie pt
        // pret=0 (0 <= 0 = true)
        // Original: 0 < 0 = false -> NU arunca exceptie, pretul 0 este valid
        double result = procesor.calculeazaPretFinal(new double[] { 0.0 }, 0, false, 2.0, false);
        assertEquals(15.0, result, 0.001,
                "A supravietuit mutantul care modifica granita/constanta pe verificarea de pret negativ");
    }

    @Test
    public void testMutation_UcideMutantVoucherSumaAproapeZero() {
        // Omoara mutantul pe linia if (sumaDupaReducere < 0):
        // "Substituted 0.0 with 1.0" -> devine < 1.0, ar face clamp daca suma e intre 0
        // si 1
        // 51.53 * 0.98 = 50.4994. Cu voucher: 50.4994 - 50 = 0.4994
        // Original: 0.4994 < 0? Nu. Rezultat: (0.4994 + 15) = 15.4994 -> rotunjit 15.5
        // Mutant: 0.4994 < 1.0? Da! clamp la 0. Rezultat: 0 + 15 = 15.0
        double result = procesor.calculeazaPretFinal(new double[] { 51.53 }, 0, true, 2.0, false);
        assertEquals(15.5, result, 0.001,
                "A supravietuit mutantul care substituie 0 cu 1 in comparatia de clamp voucher");
    }
}
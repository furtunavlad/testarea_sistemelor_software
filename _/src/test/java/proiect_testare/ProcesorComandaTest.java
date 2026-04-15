package proiect_testare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcesorComandaTest {

    private ProcesorComanda procesor;

    @BeforeEach
    public void setUp() {
        procesor = new ProcesorComanda();
    }

    // MARK: Equivalence Partitioning

    @Test
    public void testConditieCompusa_PreturiNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(null, 2, false, 2.0);
        });
    }

    @Test
    public void testConditieCompusa_PreturiEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[]{}, 2, false, 2.0);
        });
    }

    @Test
    public void testConditieCompusa_GreutateNegativa() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[]{100.0}, 2, false, -1.0);
        });
    }

    @Test
    public void testConditieCompusa_AniFidelitateNegativi() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[]{100.0}, -1, false, 2.0);
        });
    }

    @Test
    public void testConditieCompusa_DateValide() {
        double[] preturiProduse = {100.0, 50.0};
        double result = procesor.calculeazaPretFinal(preturiProduse, 2, false, 2.0);
        assertEquals(159.0, result, 0.001, "Pretul final pentru un cos valid mic nu este calculat corect");
    }

    @Test
    public void testConditieSimpla_PretProdusNegativ_StructuraRepetitiva() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[]{50.0, -10.0}, 2, false, 2.0);
        });
    }

    // MARK: Boundary Value Analysis

    @Test
    public void testBoundary_SumaExactSubPragulDe1000() {
        // Suma: 999.9. Reducere de baza: 2%. Ani fidelitate = 0 -> reducere totala: 2%
        // 999.9 * 0.98 = 979.9. Fara voucher. Livrare: > 200 -> 0 taxa
        double expected = 999.9 * 0.98;
        double result = procesor.calculeazaPretFinal(new double[]{999.9}, 0, false, 2.0);
        assertEquals(expected, result, 0.001);
    }

    @Test
    public void testBoundary_PretProdus_LimitaValidaZero() {
        // limita inferioara absoluta pentru pretul unui produs
        double[] preturi = {0.0};
        double result = procesor.calculeazaPretFinal(preturi, 0, false, 2.0);

        assertEquals(15.0, result, 0.001, "Produsul cu pretul 0.0 ar trebui acceptat ca valoare de frontiera.");
    }

    @Test
    public void testBoundary_SumaExactPePragulDe1000_UcideMutant1() {
        // omoram Mutantul care ar schimba ">=" cu ">"
        // Suma: 1000.0. Reducere: 10%. Ani: 0
        // 1000 * 0.9 = 900.0. Livrare: 0. Total: 900.0
        double result = procesor.calculeazaPretFinal(new double[]{1000.0}, 0, false, 2.0);
        assertEquals(900.0, result, 0.001);
    }

    @Test
    public void testBoundary_AniFidelitate_LimitaSuperioaraProcent() {
        // 4 ani -> 4% extra. Total cu reducere de baza (2%) = 6%
        // Suma: 100. 100 * 0.94 = 94
        // 94 < 200 -> se adauga taxa livrare baza 15 (greutate < 5). Total 109
        double result4Ani = procesor.calculeazaPretFinal(new double[]{100.0}, 4, false, 2.0);
        assertEquals(109.0, result4Ani, 0.001);

        // 5 ani -> 5% extra. Total = 7%. SumaDupa = 93. Taxa = 15. Total = 108.
        double result5Ani = procesor.calculeazaPretFinal(new double[]{100.0}, 5, false, 2.0);
        assertEquals(108.0, result5Ani, 0.001);

        // 6 ani -> plafonat la 5% extra. Total = 7%
        double result6Ani = procesor.calculeazaPretFinal(new double[]{100.0}, 6, false, 2.0);
        assertEquals(108.0, result6Ani, 0.001);
    }

    @Test
    public void testBoundary_Greutate_PragSupraTaxa() {
        // suma < 200. Taxa de baza 15
        // greutate 5.0 -> fara suprataxa
        double resultGreutate5 = procesor.calculeazaPretFinal(new double[]{100.0}, 0, false, 5.0);
        assertEquals(113.0, resultGreutate5, 0.001); // (100*0.98) + 15 = 113.0

        // greutate 5.1 -> suprataxa (5.1 - 5.0) * 2.0 = 0.2 RON
        double resultGreutatePeste = procesor.calculeazaPretFinal(new double[]{100.0}, 0, false, 5.1);
        assertEquals(113.2, resultGreutatePeste, 0.001);
    }

    @Test
    public void testBoundary_SumaSub200_TaxaLivrare() {
        // reducere: 2%. SumaDupa: 199.92. Sub 200 => taxa 15
        double resultSub200 = procesor.calculeazaPretFinal(new double[]{204.0}, 0, false, 2.0);
        assertEquals(199.92 + 15.0, resultSub200, 0.001);
    }

    @Test
    public void testBoundary_SumaPeste200_FaraTaxaLivrare() {
        // reducere 2%. Suma init 205. Suma dupa 200.9. Peste 200 => fara taxa
        double resultPeste200 = procesor.calculeazaPretFinal(new double[]{205.0}, 0, false, 2.0);
        assertEquals(200.9, resultPeste200, 0.001);
    }

    @Test
    public void testVoucher_AplicaNormal_UcideMutant2() {
        // suma initiala 200. Reducere 2%. Suma = 196
        // are voucher: 196 - 50 = 146
        // sub 200 -> livrare = 15. Total = 161
        double result = procesor.calculeazaPretFinal(new double[]{200.0}, 0, true, 2.0);
        assertEquals(161.0, result, 0.001);
    }

    @Test
    public void testVoucher_SumaNegativaSetataLaZero() {
        // acopera ramura "if (sumaDupaReducere < 0) sumaDupaReducere = 0"
        // suma initiala 40. Reducere 2% = 39.2 
        // are voucher: 39.2 - 50 = -10.8 -> setat la 0
        // sub 200 -> livrare = 15. Total = 15
        double result = procesor.calculeazaPretFinal(new double[]{40.0}, 0, true, 2.0);
        assertEquals(15.0, result, 0.001);
    }
}
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
    public void testEP_PreturiNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(null, 2, false, 2.0);
        });
    }

    @Test
    public void testEP_PreturiEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[] {}, 2, false, 2.0);
        });
    }

    @Test
    public void testEP_GreutateNegativa() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[] { 100.0 }, 2, false, -1.0);
        });
    }

    @Test
    public void testEP_AniFidelitateNegativi() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[] { 100.0 }, -1, false, 2.0);
        });
    }

    @Test
    public void testEP_CosValidMicFaraVoucher() {
        // Reducere 2% + 2% (2 ani fidelitate) = 4%
        // Suma: 150 * 0.96 = 144. Sub 200 -> Taxa 15. Greutate 2.0 -> Fara suprataxa
        double result = procesor.calculeazaPretFinal(new double[] { 100.0, 50.0 }, 2, false, 2.0);
        assertEquals(159.0, result, 0.001);
    }

    @Test
    public void testEP_CosValidMareCuVoucher() {
        // Reducere 10% (suma >= 1000) + 3% (3 ani fidelitate) = 13%
        // Peste 200 -> Taxa 0
        double result = procesor.calculeazaPretFinal(new double[] { 600.0, 600.0 }, 3, true, 2.0);
        assertEquals(994.0, result, 0.001);
    }

    // MARK: Boundary Value Analysis

    @Test
    public void testBVA_SumaAproapeDe1000() {
        // La 999.9 aplicam reducerea normala de 2%
        double result = procesor.calculeazaPretFinal(new double[] { 999.9 }, 0, false, 2.0);
        assertEquals(999.9 * 0.98, result, 0.001);
    }

    @Test
    public void testBVA_SumaExactPePragulDe1000() {
        // La 1000.0 aplicam reducerea majorata de 10%
        double result = procesor.calculeazaPretFinal(new double[] { 1000.0 }, 0, false, 2.0);
        assertEquals(900.0, result, 0.001); // 1000 * 0.9
    }

    @Test
    public void testBVA_GreutateColetLimita0() {
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 0.0);
        assertEquals(113.0, result, 0.001); // (100 * 0.98) + 15 taxa de baza
    }

    @Test
    public void testBVA_GreutateColetFix5() {
        // Pragul unde inca nu se aplica suprataxa de greutate
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 5.0);
        assertEquals(113.0, result, 0.001); // (100 * 0.98) + 15 taxa
    }

    @Test
    public void testBVA_GreutateColetPeste5() {
        // 5.1 kg -> (5.1 - 5.0) * 2 = 0.2 supra-taxa
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 5.1);
        assertEquals(113.2, result, 0.001);
    }

    @Test
    public void testBVA_SumaSub200_AplicaTaxaLivrare() {
        // 204 * 0.98 = 199.92 (sub 200 => plateste livrare 15)
        double result = procesor.calculeazaPretFinal(new double[] { 204.0 }, 0, false, 2.0);
        assertEquals(199.92 + 15.0, result, 0.001);
    }

    @Test
    public void testBVA_SumaDupaReducereExact200_LivrareGratuita() {
        double result = procesor.calculeazaPretFinal(new double[] { 204.08163265306123 }, 0, false, 2.0);
        assertEquals(200.0, result, 0.001);
    }

    @Test
    public void testBVA_SumaPeste200_LivrareGratuita() {
        // Reducere 2%. 205 * 0.98 = 200.9 (peste 200 => livrare 0)
        double result = procesor.calculeazaPretFinal(new double[] { 205.0 }, 0, false, 2.0);
        assertEquals(200.9, result, 0.001);
    }

    @Test
    public void testBVA_AniFidelitateLaLimitaDe5Procente() {
        // 4 ani = 4%, 5 ani = 5%, 6 ani = max 5%
        double result4Ani = procesor.calculeazaPretFinal(new double[] { 100.0 }, 4, false, 2.0);
        assertEquals(109.0, result4Ani, 0.001); // 100 * 0.94 + 15

        double result5Ani = procesor.calculeazaPretFinal(new double[] { 100.0 }, 5, false, 2.0);
        assertEquals(108.0, result5Ani, 0.001); // 100 * 0.93 + 15

        double result6Ani = procesor.calculeazaPretFinal(new double[] { 100.0 }, 6, false, 2.0);
        assertEquals(108.0, result6Ani, 0.001); // Plafonat la 5% extra
    }

    // MARK: Structural Testing

    @Test
    public void testStructural_MultipleConditionCoverage_Exceptii() {
        // Condition Coverage pentru OR
        assertThrows(IllegalArgumentException.class,
                () -> procesor.calculeazaPretFinal(null, 1, false, 1.0)); // prima

        assertThrows(IllegalArgumentException.class,
                () -> procesor.calculeazaPretFinal(new double[] {}, 1, false, 1.0)); // a doua

        assertThrows(IllegalArgumentException.class,
                () -> procesor.calculeazaPretFinal(new double[] { 10.0 }, 1, false, -1.0)); // a treia

        assertThrows(IllegalArgumentException.class,
                () -> procesor.calculeazaPretFinal(new double[] { 10.0 }, -1, false, 1.0)); // a patra
    }

    @Test
    public void testStructural_LoopPesteValoriNegative() {
        // Condition Coverage for-loop "if (preturiProduse[i] < 0)"
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[] { 49.99, 19.99, 44.99, -10.0, 92.99 }, 2, false, 2.0);
        });
    }

    @Test
    public void testStructural_VoucherSumaNegativa() {
        // Branch coverage: ramura "if (sumaDupaReducere < 0)"
        double result = procesor.calculeazaPretFinal(new double[] { 40.0 }, 0, true, 2.0);
        assertEquals(15.0, result, 0.001); // Taxa livrare -> 15
    }

    @Test
    public void testStructural_VoucherSumaPozitiva() {
        // Branch coverage: "if (areVoucher)"
        double result = procesor.calculeazaPretFinal(new double[] { 200.0 }, 0, true, 2.0);
        assertEquals(161.0, result, 0.001); // 196 - 50 = 146. Taxa = 15 -> 161
    }

    // MARK: Mutation Testing

    @Test
    public void testMutation_UcideMutantStrictMaiMare() {
        // Daca >= devine > in "if (sumaInitiala >= 1000.0)"
        // => 2% reducere în loc de 10% la 1000.0
        double result = procesor.calculeazaPretFinal(new double[] { 1000.0 }, 0, false, 2.0);
        assertEquals(900.0, result, 0.001);
    }

    @Test
    public void testMutation_UcideMutantMathMinMax() {
        // Daca metoda Math.min() este mutata in Math.max()
        // Suma 100, red 3% -> 97 (cu Math.max 5% -> 95)
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 1, false, 2.0);
        assertEquals(112.0, result, 0.001); // 97 (reducere prod) + 15 (taxa livr)
    }

    @Test
    public void testMutation_UcideMutantPlusMinus() {
        // Daca sumaDupaReducere -= 50.0 -> += 50.0
        // Programul normal: 100 * 0.98 = 98 -> 98 - 50 = 48 -> taxa 15 -> 63
        // Mutantul: 98 + 50 = 148 -> taxa 15 -> 163
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, true, 2.0);
        assertEquals(63.0, result, 0.001);
    }
}
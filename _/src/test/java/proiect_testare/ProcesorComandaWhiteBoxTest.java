package proiect_testare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box (structural) tests for ProcesorComanda.
 *
 * Covers branch/condition coverage for every decision point in
 * calculeazaPretFinal(), plus mutation-killing tests.
 */
class ProcesorComandaWhiteBoxTest {

    private ProcesorComanda procesor;

    @BeforeEach
    public void setUp() {
        procesor = new ProcesorComanda();
    }

    // -------------------------------------------------------------------------
    // Branch: compound guard condition (condition coverage for each sub-clause)
    // -------------------------------------------------------------------------

    @Test
    public void testStructural_DateInvalide() {
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

    // -------------------------------------------------------------------------
    // Branch: "if (preturiProduse[i] < 0)" inside loop
    // -------------------------------------------------------------------------

    @Test
    public void testStructural_LoopPesteValoriNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[] { 49.99, 19.99, 44.99, -10.0, 92.99 }, 2, false, 2.0);
        });
    }

    // -------------------------------------------------------------------------
    // Branch: "if (sumaInitiala >= 1000.0)"
    // -------------------------------------------------------------------------

    @Test
    public void testStructural_SumaInitialaGeq1000_AdevArat() {
        // TRUE => reducere 10%; 1000 * 0.90 = 900 >= 200 => livrare gratuita
        double result = procesor.calculeazaPretFinal(new double[] { 1000.0 }, 0, false, 2.0);
        assertEquals(900.0, result, 0.001);
    }

    @Test
    public void testStructural_SumaInitialaGeq1000_Fals() {
        // FALSE => reducere 2%; 500 * 0.98 = 490 >= 200 => livrare gratuita
        double result = procesor.calculeazaPretFinal(new double[] { 500.0 }, 0, false, 2.0);
        assertEquals(490.0, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // Branch: "if (aniFidelitate > 0)"
    // -------------------------------------------------------------------------

    @Test
    public void testStructural_AniFidelitate_Pozitiv() {
        // TRUE => reducere extra min(3*0.01, 0.05) = 0.03; total 5%
        // 500 * 0.95 = 475 >= 200 => livrare gratuita
        double result = procesor.calculeazaPretFinal(new double[] { 500.0 }, 3, false, 2.0);
        assertEquals(475.0, result, 0.001);
    }

    @Test
    public void testStructural_AniFidelitate_Zero() {
        // FALSE => nicio reducere suplimentara; 500 * 0.98 = 490 >= 200
        double result = procesor.calculeazaPretFinal(new double[] { 500.0 }, 0, false, 2.0);
        assertEquals(490.0, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // Branch: "if (areVoucher)"
    // -------------------------------------------------------------------------

    @Test
    public void testStructural_VoucherSumaPozitiva() {
        // TRUE: 200 * 0.98 = 196 - 50 = 146 < 200 => taxa 15 => 161
        double result = procesor.calculeazaPretFinal(new double[] { 200.0 }, 0, true, 2.0);
        assertEquals(161.0, result, 0.001);
    }

    @Test
    public void testStructural_AreVoucher_Fals() {
        // FALSE: 100 * 0.98 = 98 < 200 => taxa 15 => 113
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 2.0);
        assertEquals(113.0, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // Branch: "if (sumaDupaReducere < 0)" — voucher exceeds discounted sum
    // -------------------------------------------------------------------------

    @Test
    public void testStructural_VoucherSumaNegativa() {
        // 40 * 0.98 = 39.2 - 50 = -10.8 => clampat la 0; livrare 15 => 15
        double result = procesor.calculeazaPretFinal(new double[] { 40.0 }, 0, true, 2.0);
        assertEquals(15.0, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // Branch: "if (sumaDupaReducere < 200.0)"
    // -------------------------------------------------------------------------

    @Test
    public void testStructural_SumaDupaReducere_Sub200() {
        // TRUE: 100 * 0.98 = 98 < 200 => costLivrare = 15; greutate=2.0 <= 5.0
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 2.0);
        assertEquals(113.0, result, 0.001);
    }

    @Test
    public void testStructural_SumaDupaReducere_Peste200() {
        // FALSE: 300 * 0.98 = 294 >= 200 => costLivrare = 0
        double result = procesor.calculeazaPretFinal(new double[] { 300.0 }, 0, false, 2.0);
        assertEquals(294.0, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // Branch: "if (greutateColet > 5.0)"
    // -------------------------------------------------------------------------

    @Test
    public void testStructural_GreutateColet_Peste5() {
        // TRUE: livrare 15 + (6.0 - 5.0) * 2 = 17; 98 + 17 = 115
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 6.0);
        assertEquals(115.0, result, 0.001);
    }

    @Test
    public void testStructural_GreutateColet_5() {
        // FALSE: greutate=5.0 => fara suprataxa; 98 + 15 = 113
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 5.0);
        assertEquals(113.0, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // Branch: "if (preturiProduse[i] < 0)" — price exactly 0 is valid
    // Kills: InlineConstant(0→1.0) and ConditionalsBoundary(<0→<=0) on that line
    // -------------------------------------------------------------------------

    @Test
    public void testStructural_PretProdusPeZero() {
        // price=0.0 is a valid input; should NOT throw
        // sumaInitiala=0, reducere=2%, sumaDupaReducere=0 < 200 => costLivrare=15
        double result = procesor.calculeazaPretFinal(new double[] { 0.0 }, 0, false, 2.0);
        assertEquals(15.0, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // Branch: "if (sumaDupaReducere < 0)" — value strictly between 0 and 1
    // Kills: InlineConstant(0.0→1.0) on that line (mutant clamps 0.47 to 0)
    // -------------------------------------------------------------------------

    @Test
    public void testStructural_VoucherSumaIntre0Si1() {
        // 51.5 * 0.98 = 50.47; 50.47 - 50 = 0.47 => NOT clamped (original < 0 is false)
        // With mutant (< 1.0): 0.47 < 1.0 = true => clamped to 0 => result=15, not 15.47
        double result = procesor.calculeazaPretFinal(new double[] { 51.5 }, 0, true, 2.0);
        assertEquals(15.47, result, 0.001);
    }

    // -------------------------------------------------------------------------
    // Mutation-killing tests
    // -------------------------------------------------------------------------

    @Test
    public void testMutation_UcideMutantStrictMaiMare() {
        // Ucide mutantul: >= devine > in "if (sumaInitiala >= 1000.0)"
        // => la exact 1000 trebuie sa primim 10%, nu 2%
        double result = procesor.calculeazaPretFinal(new double[] { 1000.0 }, 0, false, 2.0);
        assertEquals(900.0, result, 0.001);
    }

    @Test
    public void testMutation_UcideMutantMathMinMax() {
        // Ucide mutantul: Math.min() -> Math.max() in calculul reducerii fidelitate
        // Cu Math.min: 1 an => 1%, reducere totala 3%; 100 * 0.97 = 97 + 15 = 112
        // Cu Math.max: 1 an => 5% (max din 1%,5%), reducere totala 7%; 100 * 0.93 = 93
        // + 15 = 108
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 1, false, 2.0);
        assertEquals(112.0, result, 0.001);
    }

    @Test
    public void testMutation_UcideMutantPlusMinus() {
        // Ucide mutantul: -= 50 devine += 50 pentru voucher
        // Normal: 100 * 0.98 = 98 - 50 = 48 + 15 = 63
        // Mutant: 98 + 50 = 148 + 15 = 163
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, true, 2.0);
        assertEquals(63.0, result, 0.001);
    }
}

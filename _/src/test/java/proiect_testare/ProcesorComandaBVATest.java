package proiect_testare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Boundary Value Analysis (BVA) tests for ProcesorComanda.
 *
 * Boundaries analysed:
 * - Order sum threshold at 1000 (discount 2% vs 10%)
 * - Package weight threshold at 5 kg (surcharge applied above)
 * - Post-discount sum threshold at 200 (free delivery above)
 * - Fidelity years cap at 5% extra discount
 */
class ProcesorComandaBVATest {

    private ProcesorComanda procesor;

    @BeforeEach
    public void setUp() {
        procesor = new ProcesorComanda();
    }

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
}

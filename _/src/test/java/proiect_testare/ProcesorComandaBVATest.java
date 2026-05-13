package proiect_testare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Boundary Value Analysis (BVA) tests for ProcesorComanda.
 * Testam exact punctele de decizie de pe margini.
 */
class ProcesorComandaBVATest {

    private ProcesorComanda procesor;

    @BeforeEach
    public void setUp() {
        procesor = new ProcesorComanda();
    }

    @Test
    public void testBVA_SumaAproapeDe1000() {
        // La 999.9 aplicam reducerea normala de 2% (nu intra pe pragul de 10%)
        // 999.9 * 0.98 = 979.902 (peste 200, deci livrare gratuita)
        double result = procesor.calculeazaPretFinal(new double[] { 999.9 }, 0, false, 2.0, false);
        assertEquals(979.90, result, 0.01);
    }

    @Test
    public void testBVA_SumaExactPePragulDe1000() {
        // La 1000.0 aplicam reducerea majorata de 10%
        // 1000 * 0.9 = 900
        double result = procesor.calculeazaPretFinal(new double[] { 1000.0 }, 0, false, 2.0, false);
        assertEquals(900.0, result, 0.001); 
    }

    @Test
    public void testBVA_GreutateColetLimita0() {
        // Greutate 0. 100 * 0.98 = 98. Sub 200 -> taxa 15. Total: 113.
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 0.0, false);
        assertEquals(113.0, result, 0.001);
    }

    @Test
    public void testBVA_GreutateColetFix5() {
        // Pragul unde inca NU se aplica suprataxa de greutate
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 5.0, false);
        assertEquals(113.0, result, 0.001); // (100 * 0.98) + 15 taxa = 113
    }

    @Test
    public void testBVA_GreutateColetPeste5() {
        // 5.1 kg -> (5.1 - 5.0) * 2 = 0.2 lei supra-taxa
        // (100 * 0.98) + 15 + 0.2 = 113.2
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 5.1, false);
        assertEquals(113.2, result, 0.001);
    }

    @Test
    public void testBVA_SumaSub200_AplicaTaxaLivrare() {
        // 204 * 0.98 = 199.92 (sub 200 => plateste livrare 15)
        double result = procesor.calculeazaPretFinal(new double[] { 204.0 }, 0, false, 2.0, false);
        assertEquals(214.92, result, 0.01); // 199.92 + 15
    }

    @Test
    public void testBVA_SumaPeste200_LivrareGratuita() {
        // 205 * 0.98 = 200.9 (peste 200 => livrare 0)
        double result = procesor.calculeazaPretFinal(new double[] { 205.0 }, 0, false, 2.0, false);
        assertEquals(200.9, result, 0.001);
    }

    @Test
    public void testBVA_AniFidelitateLaLimitaDe5Procente() {
        // Verificam plafonarea math.min la fidelitate
        // 4 ani = 4%, 5 ani = 5%, 6 ani = max 5% (Total 6%, 7%, 7% din cauza reducerii standard de 2%)
        
        // 100 * 0.94 = 94 + 15 = 109
        double result4Ani = procesor.calculeazaPretFinal(new double[] { 100.0 }, 4, false, 2.0, false);
        assertEquals(109.0, result4Ani, 0.001); 

        // 100 * 0.93 = 93 + 15 = 108
        double result5Ani = procesor.calculeazaPretFinal(new double[] { 100.0 }, 5, false, 2.0, false);
        assertEquals(108.0, result5Ani, 0.001); 

        // 100 * 0.93 = 93 + 15 = 108 (Plafonat)
        double result6Ani = procesor.calculeazaPretFinal(new double[] { 100.0 }, 6, false, 2.0, false);
        assertEquals(108.0, result6Ani, 0.001); 
    }
}
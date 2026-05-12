package proiect_testare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Equivalence Partitioning (EP) tests for ProcesorComanda.
 *
 * Partitions:
 * - Invalid inputs (null prices, empty prices, negative weight, negative
 * fidelity years)
 * - Valid small cart without voucher
 * - Valid large cart with voucher
 */
class ProcesorComandaEPTest {

    private ProcesorComanda procesor;

    @BeforeEach
    public void setUp() {
        procesor = new ProcesorComanda();
    }

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
}

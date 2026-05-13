package proiect_testare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Equivalence Partitioning (EP) tests for ProcesorComanda.
 * Acoperim clasele de echivalenta valide si invalide deduse din cerinte.
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
            procesor.calculeazaPretFinal(null, 2, false, 2.0, false);
        });
    }

    @Test
    public void testEP_PreturiEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[] {}, 2, false, 2.0, false);
        });
    }

    @Test
    public void testEP_GreutateNegativa() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[] { 100.0 }, 2, false, -1.0, false);
        });
    }

    @Test
    public void testEP_AniFidelitateNegativi() {
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[] { 100.0 }, -1, false, 2.0, false);
        });
    }

    @Test
    public void testEP_CosValidMicFaraVoucher() {
        // Reducere 2% (standard) + 2% (2 ani fidelitate) = 4% reducere
        // Suma: 150 * 0.96 = 144. Sub 200 -> Taxa 15. Greutate 2.0 -> Fara suprataxa
        double result = procesor.calculeazaPretFinal(new double[] { 100.0, 50.0 }, 2, false, 2.0, false);
        assertEquals(159.0, result, 0.001); // 144 + 15
    }

    @Test
    public void testEP_CosValidMareCuVoucher() {
        // Reducere 10% (suma >= 1000) + 3% (3 ani fidelitate) = 13%
        // Suma inainte de voucher: 1200 * 0.87 = 1044. 
        // Are voucher: 1044 - 50 = 994.
        // Peste 200 -> Livrare gratuita.
        double result = procesor.calculeazaPretFinal(new double[] { 600.0, 600.0 }, 3, true, 2.0, false);
        assertEquals(994.0, result, 0.001);
    }

    @Test
    public void testEP_PretZero() {
        // Clasa de echivalenta valida: pretul unui produs este exact 0
        // 0 * 0.98 = 0. Sub 200 -> taxa livrare 15. Total: 15.
        double result = procesor.calculeazaPretFinal(new double[] { 0.0 }, 0, false, 2.0, false);
        assertEquals(15.0, result, 0.001);
    }

    @Test
    public void testEP_ClientVIP() {
        // Clasa de echivalenta: client VIP cu comanda mica, fara voucher
        // Reducere 10% (VIP). 100 * 0.9 = 90. Sub 200 -> taxa 15. Total: 105.
        double result = procesor.calculeazaPretFinal(new double[] { 100.0 }, 0, false, 2.0, true);
        assertEquals(105.0, result, 0.001);
    }

    @Test
    public void testEP_PretNegativ() {
        // Clasa de echivalenta invalida: un singur produs cu pret negativ (verificat in bucla)
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[] { -10.0 }, 0, false, 2.0, false);
        });
    }
}
package proiect_testare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProcesorComandaTest {

    private ProcesorComanda procesor;

    @BeforeEach
    void setUp() {
        procesor = new ProcesorComanda();
    }

    // ==========================================
    // 1. TESTE EPT (Clase de Echivalență) & WHITEBOX (Condiții Excepții)
    // ==========================================

    @Test
    @DisplayName("Exceptie - Preturi array este null (EPT Invalid)")
    void testPreturiNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> procesor.calculeazaPretFinal(null, 2, false, 2.5, false));
        assertEquals("Date de intrare invalide", exception.getMessage());
    }

    @Test
    @DisplayName("Exceptie - Preturi array este gol (EPT Invalid / BVA lungime 0)")
    void testPreturiGoale() {
        assertThrows(IllegalArgumentException.class, 
            () -> procesor.calculeazaPretFinal(new double[]{}, 2, false, 2.5, false));
    }

    @Test
    @DisplayName("Exceptie - Greutate negativa (EPT Invalid / BVA sub limita)")
    void testGreutateNegativa() {
        assertThrows(IllegalArgumentException.class, 
            () -> procesor.calculeazaPretFinal(new double[]{100.0}, 2, false, -0.1, false));
    }

    @Test
    @DisplayName("Exceptie - Fidelitate negativa (EPT Invalid / BVA sub limita)")
    void testFidelitateNegativa() {
        assertThrows(IllegalArgumentException.class, 
            () -> procesor.calculeazaPretFinal(new double[]{100.0}, -1, false, 2.5, false));
    }

    @Test
    @DisplayName("Exceptie - Un pret de produs este negativ (Structura repetitiva & Whitebox)")
    void testPretProdusNegativ() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> procesor.calculeazaPretFinal(new double[]{50.0, -10.0, 30.0}, 2, false, 2.5, false));
        assertEquals("Pretul unui produs nu poate fi negativ", exception.getMessage());
    }

    // ==========================================
    // 2. TESTE BVA (Valori de Frontieră) & WHITEBOX (Acoperire Ramificatii)
    // ==========================================

    @Test
    @DisplayName("Suma exact 1000 - BVA reducere de baza (10% reducere)")
    void testSumaExact1000_FaraVIP() {
        // Suma = 1000, 10% reducere -> 900.
        // Fara livrare (900 >= 200). Total: 900.0
        double rezultat = procesor.calculeazaPretFinal(new double[]{1000.0}, 0, false, 2.0, false);
        assertEquals(900.0, rezultat, 0.01);
    }

    @Test
    @DisplayName("Suma sub 1000 (ex: 999) - BVA reducere de baza (2% reducere)")
    void testSumaSub1000_FaraVIP() {
        // Suma = 999. Reducere 2% -> 979.02.
        // Livrare 0 pt ca 979.02 >= 200.
        double rezultat = procesor.calculeazaPretFinal(new double[]{999.0}, 0, false, 2.0, false);
        assertEquals(979.02, rezultat, 0.01);
    }

    @Test
    @DisplayName("Client VIP - Whitebox branch (suma < 1000, dar este VIP -> 10% reducere)")
    void testSumaMica_DarVIP() {
        // Suma = 100. VIP = true -> reducere 10% -> 90.
        // Livrare (90 < 200) -> 15. Total = 105.0
        double rezultat = procesor.calculeazaPretFinal(new double[]{100.0}, 0, false, 1.0, true);
        assertEquals(105.0, rezultat, 0.01);
    }

    // ==========================================
    // 3. TESTE PENTRU ANI FIDELITATE (BVA și Whitebox)
    // ==========================================

    @Test
    @DisplayName("Fidelitate ignorata pentru VIP - Whitebox Condition AND")
    void testFidelitateIgnorataLaVIP() {
        // Suma = 100, VIP = true, Fidelitate = 5. Reducere max = 10% (VIP suprascrie). 
        // 100 - 10% = 90. + Livrare(15) = 105.
        double rezultat = procesor.calculeazaPretFinal(new double[]{100.0}, 5, false, 1.0, true);
        assertEquals(105.0, rezultat, 0.01);
    }

    @Test
    @DisplayName("Fidelitate max (5 ani) - BVA limita superioara procent")
    void testFidelitate5Ani_BVA() {
        // Suma 100. 2% reducere baza + 5% fidelitate = 7%.
        // 100 - 7% = 93. Livrare = 15. Total = 108.
        double rezultat = procesor.calculeazaPretFinal(new double[]{100.0}, 5, false, 2.0, false);
        assertEquals(108.0, rezultat, 0.01);
    }

    @Test
    @DisplayName("Fidelitate peste 5 ani (ex: 10 ani) - BVA / limitare Math.min")
    void testFidelitate10Ani_BVA() {
        // Max ramane 5%. La fel ca testul de sus.
        double rezultat = procesor.calculeazaPretFinal(new double[]{100.0}, 10, false, 2.0, false);
        assertEquals(108.0, rezultat, 0.01);
    }

    // ==========================================
    // 4. TESTE PENTRU VOUCHER (Whitebox / BVA pe zero)
    // ==========================================

    @Test
    @DisplayName("Voucher aplicat pe o comanda mare - SumaDupaReducere pozitiva")
    void testVoucherCuSumaRamasaPozitiva() {
        // Suma = 100, reducere 2% = 98. 
        // Aplicare voucher: 98 - 50 = 48.
        // Cost livrare pt suma < 200 = 15. 
        // Total final = 48 + 15 = 63.0
        double rezultat = procesor.calculeazaPretFinal(new double[]{100.0}, 0, true, 2.0, false);
        assertEquals(63.0, rezultat, 0.01);
    }

    @Test
    @DisplayName("Voucher aplicat, suma devine negativa fortata la 0 - BVA / Whitebox")
    void testVoucherCuSumaDevineZero() {
        // Suma = 20. Reducere 2% = 19.6
        // Aplicare voucher 50 -> -30.4 -> Corectat in 0.0 de aplicatie.
        // Livrare pt suma < 200 (0 < 200) = 15. Total = 15.0
        double rezultat = procesor.calculeazaPretFinal(new double[]{20.0}, 0, true, 2.0, false);
        assertEquals(15.0, rezultat, 0.01);
    }

    // ==========================================
    // 5. TESTE COST LIVRARE (BVA - Prag 200 lei si Prag Greutate 5kg)
    // ==========================================

    @Test
    @DisplayName("Livrare gratuita - Suma exact 200 dupa reducere (BVA suma)")
    void testLivrareGratuita_Suma200BVA() {
        // Daca vrem exact 200 lei dupa 2% reducere -> pret = 200 / 0.98 = 204.0816...
        // Testam cu un pret care garanteaza o suma finala >= 200
        double rezultat = procesor.calculeazaPretFinal(new double[]{210.0}, 0, false, 1.0, false);
        // Suma initiala 210. Reducere 2% = 4.2 -> SumaDupa = 205.8 (care e > 200)
        // Livrare = 0.
        assertEquals(205.80, rezultat, 0.01);
    }

    @Test
    @DisplayName("Livrare platita cu cost extra pentru greutate - BVA Greutate > 5kg")
    void testLivrarePlatita_GreutatePeste5kg() {
        // Suma = 100. Reducere 2% = 98.
        // Livrare: 15 taxa fixa.
        // Greutate 7 kg (> 5). Exces 2 kg * 2 = 4 lei.
        // Livrare totala = 19. Suma finala: 98 + 19 = 117.
        double rezultat = procesor.calculeazaPretFinal(new double[]{100.0}, 0, false, 7.0, false);
        assertEquals(117.0, rezultat, 0.01);
    }

    @Test
    @DisplayName("Livrare platita greutate exact 5kg - BVA Greutate la limita")
    void testLivrarePlatita_GreutateExact5kg() {
        // Suma = 100. Reducere 2% = 98.
        // Livrare: 15 taxa fixa. Fara supliment.
        // Total = 113.
        double rezultat = procesor.calculeazaPretFinal(new double[]{100.0}, 0, false, 5.0, false);
        assertEquals(113.0, rezultat, 0.01);
    }
}
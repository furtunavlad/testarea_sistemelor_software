package proiect_testare;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcesorComandaTest {

    // 1. Declarăm obiectul pe care îl vom testa
    private ProcesorComanda procesor;

    // 2. Metoda @BeforeEach se execută automat înaintea FIECĂRUI @Test
    // Ne asigură că avem un obiect "curat" și nou pentru fiecare scenariu
    @BeforeEach
    void setUp() {
        procesor = new ProcesorComanda();
    }

    // --- TESTELE EFECTIVE ---

    @Test
    @DisplayName("Trebuie să arunce excepție pentru suma negativă")
    void testSumaInvalida() {
        // Aici folosim assertThrows pentru a verifica dacă sistemul crapă corect când primește date greșite
        assertThrows(IllegalArgumentException.class, () -> {
            procesor.calculeazaPretFinal(new double[]{100.0, 200.0}, 1, false, 2.0);
        });
    }
}
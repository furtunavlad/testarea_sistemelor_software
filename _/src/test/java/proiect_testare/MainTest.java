package proiect_testare;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for Main to achieve line coverage and kill no-coverage mutations.
 *
 * Main computes: {50.0, 60.0}, aniFidelitate=2, areVoucher=false, greutate=6.0
 *   sumaInitiala = 110.0
 *   reducere = 0.02 + min(0.02, 0.05) = 0.04
 *   sumaDupaReducere = 110 * 0.96 = 105.6  (< 200, pays delivery)
 *   costLivrare = 15 + (6.0 - 5.0) * 2 = 17.0
 *   result = 105.6 + 17.0 = 122.6
 */
class MainTest {

    @Test
    public void testMain_OutputCorect() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(baos));
        try {
            Main.main(new String[]{});
        } finally {
            System.setOut(originalOut);
        }
        assertEquals("122.6", baos.toString().trim());
    }
}

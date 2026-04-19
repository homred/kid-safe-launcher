package com.kidsafe.launcher.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for ParentalControlManager hash and utility logic.
 */
public class ParentalControlManagerTest {

    @Test
    public void testHashPinProducesConsistentResult() {
        String hash1 = ParentalControlManager.hashPin("1234");
        String hash2 = ParentalControlManager.hashPin("1234");
        assertEquals(hash1, hash2);
    }

    @Test
    public void testHashPinDifferentPinsProduceDifferentHashes() {
        String hash1 = ParentalControlManager.hashPin("1234");
        String hash2 = ParentalControlManager.hashPin("5678");
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void testHashPinStartsWithPrefix() {
        String hash = ParentalControlManager.hashPin("0000");
        assertTrue(hash.startsWith("ph_"));
    }

    @Test
    public void testHashPinNullReturnsEmptyString() {
        String hash = ParentalControlManager.hashPin(null);
        assertEquals("", hash);
    }

    @Test
    public void testHashPinEmptyString() {
        String hash = ParentalControlManager.hashPin("");
        assertNotNull(hash);
        assertTrue(hash.startsWith("ph_"));
    }

    @Test
    public void testHashPinDefaultPin() {
        String hash = ParentalControlManager.hashPin("0000");
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    public void testHashPinLongPin() {
        String hash = ParentalControlManager.hashPin("12345678");
        assertNotNull(hash);
        assertTrue(hash.startsWith("ph_"));
    }

    @Test
    public void testHashPinSingleChar() {
        String hash = ParentalControlManager.hashPin("1");
        assertNotNull(hash);
        assertTrue(hash.startsWith("ph_"));
    }

    @Test
    public void testHashPinOnlyNumericCharsHandled() {
        String hash = ParentalControlManager.hashPin("abcd");
        assertNotNull(hash);
        assertTrue(hash.startsWith("ph_"));
    }

    @Test
    public void testHashPinDeterministic() {
        // Ensure same pin always produces same hash
        for (int i = 0; i < 100; i++) {
            assertEquals(
                    ParentalControlManager.hashPin("9999"),
                    ParentalControlManager.hashPin("9999")
            );
        }
    }
}

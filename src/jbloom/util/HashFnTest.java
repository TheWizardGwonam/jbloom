package jbloom.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * Tests for HashFn
 */
public class HashFnTest extends TestCase {

    @Test
    public void testHash()
            throws NoSuchAlgorithmException, CloneNotSupportedException {
        HashFn hash = new HashFn(1,10000);

        //a hash of a thing should always be the same
        assertArrayEquals(hash.hash("a"),hash.hash("a"));

        //we know that for this setup the hash of "a" and "b" should be different
        assertNotEquals(hash.hash("b")[0], hash.hash("a")[0]);
    }
}
package jbloom.test;

import jbloom.util.HashFn;
import junit.framework.TestCase;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

/**
 * Tests for HashFn
 */
public class HashFnTest extends TestCase {

    @Test
    public void testHash()
            throws NoSuchAlgorithmException, CloneNotSupportedException {
        HashFn hash = new HashFn(10,1438);

        int[] hashed = hash.hash("a");
        int[] test = new int[]{537, 1167, 1421, 1367, 913, 498, 173, 54, 875, 707};

        //test is what the python version gives for the hash, should be the same as ours
        //to maintain compatibility
        assertArrayEquals(test, hashed);

        //a hash of a thing should always be the same
        assertArrayEquals(hash.hash("a"), hash.hash("a"));

        //we know that for this setup the hash of "a" and "b" should be different
        assertNotEquals(hash.hash("b")[0], hash.hash("a")[0]);
    }
}
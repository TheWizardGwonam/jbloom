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

        MessageDigest hashing = (MessageDigest) hash.salts[0].clone();
        hashing.update("a".getBytes());


        for(int i : hash.hash("a")){
            System.out.println(i);
        }
        //a hash of a thing should always be the same
        assertArrayEquals(hash.hash("a"),hash.hash("a"));

        //we know that for this setup the hash of "a" and "b" should be different
        assertNotEquals(hash.hash("b")[0], hash.hash("a")[0]);
    }
}
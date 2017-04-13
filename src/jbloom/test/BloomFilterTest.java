package jbloom.test;

import jbloom.core.BloomFilter;
import junit.framework.TestCase;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by srf51 on 4/12/2017.
 */
public class BloomFilterTest extends TestCase {

    @Test
    public void testAdd()
            throws NoSuchAlgorithmException, CloneNotSupportedException {
        int capacity = 1000;
        BloomFilter bf = new BloomFilter(capacity);
        for(int i = 0; i < capacity; i++){
            bf.add(Integer.valueOf(i).toString());
        }
        for(int i = 0; i < capacity; i++){
            assertTrue(bf.has(Integer.valueOf(i).toString()));
        }
        assertTrue(!bf.has(Integer.valueOf(1001).toString()));
    }
}
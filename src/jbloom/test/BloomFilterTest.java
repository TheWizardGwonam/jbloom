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

    @Test
    public void testIntersection()
            throws NoSuchAlgorithmException, CloneNotSupportedException {
        int capacity = 1000;
        BloomFilter bf1 = new BloomFilter(capacity);
        BloomFilter bf2 = new BloomFilter(capacity);
        for(int i = 0; i < capacity; i++){
            bf1.add(Integer.valueOf(i).toString());
        }
        for(int i = capacity/2; i < capacity*3/2; i++){
            bf2.add(Integer.valueOf(i).toString());
        }

        //intersection should have all the elements that are in both BloomFilters
        BloomFilter intersect = bf1.intersection(bf2);
        for(int i = capacity/2; i < capacity; i++) {
            assertTrue(intersect.has(Integer.valueOf(i).toString()));
        }
    }

    @Test
    public void testUnion()
            throws NoSuchAlgorithmException, CloneNotSupportedException {
        int capacity = 1000;
        BloomFilter bf1 = new BloomFilter(capacity);
        BloomFilter bf2 = new BloomFilter(capacity);
        for(int i = 0; i < capacity; i++){
            bf1.add(Integer.valueOf(i).toString());
        }
        for(int i = capacity/2; i < capacity*3/2; i++){
            bf2.add(Integer.valueOf(i).toString());
        }

        //union should have all the elements of either BloomFilter
        BloomFilter union = bf1.union(bf2);
        for(int i = 0; i < 3*capacity/2; i++) {
            assertTrue(union.has(Integer.valueOf(i).toString()));
        }
    }

    @Test
    public void testToString()
            throws NoSuchAlgorithmException, CloneNotSupportedException {
        int capacity = 1000;
        BloomFilter bf = new BloomFilter(capacity);
        for(int i = 0; i < capacity; i++){
            bf.add(Integer.valueOf(i).toString());
        }
        System.out.println(bf);
        String bloom = bf.toString();
        bf = BloomFilter.fromString(bloom);
        System.out.println(bf);
        for(int i = 0; i < capacity; i++){
            assertTrue(bf.has(Integer.valueOf(i).toString()));
        }
    }
}
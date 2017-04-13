package jbloom.examples;

import jbloom.core.BloomFilter;

import java.security.NoSuchAlgorithmException;

/**
 * Created by srf51 on 4/12/2017.
 */
public class BloomFilterExample {
    public static void main(String[] args)
            throws NoSuchAlgorithmException, CloneNotSupportedException, IndexOutOfBoundsException {

        //instantiates a bloom filter with capacity = 1000, error_rate = 5%
        BloomFilter bf = new BloomFilter(1000, 0.05);

        //add String x to the bloom filter.  Currently only strings can be added
        bf.add("x");

        //returns true
        System.out.println(bf.has("x"));

        //usually returns false, can return true with some small probability less than the error rate
        System.out.println(bf.has("a"));
    }
}

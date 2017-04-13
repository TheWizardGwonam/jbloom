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
        int capacity = 1000;
        BloomFilter bf = new BloomFilter(capacity, 0.05);

        //add a String x to the bloom filter.  Currently only strings can be added
        bf.add("x");

        //bf.has("x") returns true
        assert(bf.has("x"));

        //usually returns false, can return true with some small probability less than the error rate
        assert(!bf.has("a"));

        //two bloom filters with the same error_rate and capacity can be intersected or unioned,
        //which behaves about how you'd expect
        BloomFilter bf1 = new BloomFilter(capacity, 0.05);
        BloomFilter bf2 = new BloomFilter(capacity, 0.05);
        for(int i = 0; i < capacity; i++){
            bf1.add(Integer.valueOf(i).toString());
            bf2.add(Integer.valueOf(i + capacity/2).toString());
        }
        for(int i = 0; i < 3*capacity/2; i++){
            assert(bf1.union(bf2).has(Integer.valueOf(i).toString()));
        }
        for(int i = capacity/2; i < capacity; i++){
            assert(bf1.intersection(bf2).has(Integer.valueOf(i).toString()));
        }

        //bloom filters can be represented as strings and read back in,
        //this interoperates with python, so you can do BloomFilter.fromString(s)
        //with a string from the "pybloom" or "dynamic-pybloom" library
        String s = bf1.toString();
        System.out.println("Here is what the string of the bloom filter looks like");
        System.out.println(s);

        //when we read it back in, it better have the same stuff in it!
        bf = BloomFilter.fromString(s);
        for(int i = 0; i < capacity; i++){
            assert(bf.has(Integer.valueOf(i).toString()));
        }
    }
}

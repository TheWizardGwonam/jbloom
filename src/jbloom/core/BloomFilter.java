package jbloom.core;

import jbloom.util.HashFn;

import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

public class BloomFilter {
    private BitSet bitarray;
    private HashFn hash;
    private double error_rate;
    private int num_slices, bits_per_slice, num_bits;

    protected int count, capacity;

    public BloomFilter(int capacity, double error_rate) throws NoSuchAlgorithmException {
        assert(0 < error_rate && error_rate < 1);
        assert(capacity > 0);

        num_slices = (int)Math.ceil(Math.log(1.0/error_rate)/Math.log(2));
        bits_per_slice = (int) Math.ceil(
                (capacity * Math.abs(Math.log(error_rate)))
                        / (num_slices * (Math.pow(Math.log(2), 2))));
        setup(error_rate, num_slices, bits_per_slice, capacity, 0);
        bitarray = new BitSet();
    }

    public BloomFilter(int capacity) throws NoSuchAlgorithmException {
        this(capacity, 0.001);
    }

    protected void setup(double error_rate, int num_slices, int bits_per_slice, int capacity, int count) throws NoSuchAlgorithmException {
        this.error_rate = error_rate;
        this.num_slices = num_slices;
        this.bits_per_slice = bits_per_slice;
        this.capacity = capacity;
        this.count = count;
        this.num_bits = num_slices * bits_per_slice;
        assert(this.num_bits <= Integer.MAX_VALUE);
        this.hash = new HashFn(num_slices, bits_per_slice);
    }

    public boolean has(String key) throws CloneNotSupportedException {
        int[] hashes = hash.hash(key);
        int offset = 0;
        for(int i : hashes){
            if(!bitarray.get(offset + i)){
                return false;
            }
            offset += bits_per_slice;
        }
        return true;
    }

    public boolean add(String key, boolean skip_check)
            throws CloneNotSupportedException, IndexOutOfBoundsException{
        int[] hashes = hash.hash(key);
        boolean found_all_bits = true;
        if(count > capacity){
            throw new IndexOutOfBoundsException("Bloom Capacity Exceeded");
        }
        int offset = 0;
        for(int i : hashes){
            if(!skip_check && found_all_bits && !bitarray.get(offset + i)){
                found_all_bits = false;
            }
            bitarray.set(offset + i);
            offset += bits_per_slice;
        }
        if(skip_check || !found_all_bits){
            this.count++;
            return false;
        }
        return true;
    }

    public boolean add(String key)
            throws CloneNotSupportedException, IndexOutOfBoundsException {
        return this.add(key, false);
    }

    public static void main(String[] args)
            throws NoSuchAlgorithmException, CloneNotSupportedException, IndexOutOfBoundsException {
        BloomFilter bf = new BloomFilter(100);
        bf.add("x");
        System.out.println(bf.has("x"));
    }
}

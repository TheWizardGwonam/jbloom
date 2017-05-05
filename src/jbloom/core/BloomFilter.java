package jbloom.core;

import jbloom.util.HashFn;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

/**
 * A Bloom Filter is a probabilistic data structure that utilizes
 * hashing algorithms to create a representation of a set.
 * It can't produce a false negative but it has some controllable
 * chance of producing a false positive because of the nature
 * of finite hashes and finite bit vectors. This implementation is based
 * on the dynamic-pybloom library from python (https://github.com/srf5132/dynamic-pybloom),
 * and is interoperable with it through the toString and fromString methods
 */
public class BloomFilter {
    protected BitSet bitarray;
    private HashFn hash;
    private double error_rate;
    private int num_slices, bits_per_slice, num_bits;

    private int count,capacity;

    /**
     * Creates a standard BloomFilter object
     * @param capacity the desired maximum capacity for the bloom filter, will be statically set
     * @param error_rate the desired maximum error rte of the filter
     * @throws NoSuchAlgorithmException (for the hashfn, in practice this should never be thrown)
     */
    public BloomFilter(int capacity, double error_rate)
            throws NoSuchAlgorithmException {
        assert(0 < error_rate && error_rate < 1);
        assert(capacity > 0);

        num_slices = (int)Math.ceil(Math.log(1.0/error_rate)/Math.log(2));
        bits_per_slice = (int) Math.ceil(
                (capacity * Math.abs(Math.log(error_rate)))
                        / (num_slices * (Math.pow(Math.log(2), 2))));
        setup(error_rate, num_slices, bits_per_slice, capacity, 0);
        bitarray = new BitSet();
    }

    /**
     * Creates a standard BloomFilter object with error rate of 0.001
     * @param capacity the desired maximum capacity for the bloom filter, will be statically set
     * @throws NoSuchAlgorithmException (for the hashfn, in practice this should never be thrown)
     */
    public BloomFilter(int capacity)
            throws NoSuchAlgorithmException {
        this(capacity, 0.001);
    }

    /**
     * Called to actually create the BloomFilter within the package
     * @param error_rate
     * @param num_slices
     * @param bits_per_slice
     * @param capacity
     * @param count
     * @throws NoSuchAlgorithmException
     */
    protected void setup(double error_rate, int num_slices, int bits_per_slice, int capacity, int count)
            throws NoSuchAlgorithmException {
        this.error_rate = error_rate;
        this.num_slices = num_slices;
        this.bits_per_slice = bits_per_slice;
        this.capacity = capacity;
        this.count = count;
        this.num_bits = num_slices * bits_per_slice;

        // if num_bits > Integer.MAX_VALUE the indexing will break
        assert(this.num_bits <= Integer.MAX_VALUE);
        this.hash = new HashFn(num_slices, bits_per_slice);
    }

    /**
     * Lookup to see if a key is in the bloom filter
     * @param key
     * @return True if in the filter, False if not in the filter with error_rate < the max fro the filter
     * @throws CloneNotSupportedException
     */
    public boolean has(String key)
            throws CloneNotSupportedException {
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

    /**
     * Add an element to the bloom filter.
     * Flag skip check to put it in without checking if it is in it already first (only really
     * useful for people building extensions on the bloom filter)
     * @param key
     * @param skip_check
     * @return True if it is already in, false if it was just added
     * @throws CloneNotSupportedException
     * @throws IndexOutOfBoundsException
     */
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

    /**
     * Add key to the bloom filter
     * @param key
     * @return true if it was already in, false if it wasn't
     * @throws CloneNotSupportedException
     * @throws IndexOutOfBoundsException
     */
    public boolean add(String key)
            throws CloneNotSupportedException, IndexOutOfBoundsException {
        return this.add(key, false);
    }

    /**
     * Clone the filter
     * @return cloned filter
     */
    public BloomFilter clone(){
        try {
            BloomFilter return_bloom = new BloomFilter(this.capacity, this.error_rate);
            return_bloom.bitarray = (BitSet) this.bitarray.clone();
            return return_bloom;
        }catch(Exception e) {
            //should never get here but return null just in case
            return null;
        }
    }

    /**
     * Form the intersection of the bloom filter
     * @param other
     * @return this & other
     */
    public BloomFilter intersection(BloomFilter other){
        BloomFilter return_bloom = this.clone();
        return_bloom.bitarray.and(other.bitarray);
        return return_bloom;
    }

    /**
     * Form the union of two bloom filters
     * @param other
     * @return this | other
     */
    public BloomFilter union(BloomFilter other){
        BloomFilter return_bloom = this.clone();
        return_bloom.bitarray.or(other.bitarray);
        return return_bloom;
    }

    /**
     * Stringify the bloom filter in a way that is compatible with the python version of the library
     * @return stringified bloom filter
     */
    public String toString(){
        String return_str = "", order = "";
        ByteBuffer bytes = ByteBuffer.allocate((int) Math.ceil(num_bits/8.));
        bytes.put(bitarray.toByteArray());
        if(bytes.order() == ByteOrder.LITTLE_ENDIAN){
            order = "little";
        }
        else{
            order = "big";
        }
        return_str += Double.valueOf(error_rate).toString();
        return_str += ":" + Integer.valueOf(num_slices).toString();
        return_str += ":" + Integer.valueOf(bits_per_slice).toString();
        return_str += ":" + Integer.valueOf(capacity).toString();
        return_str += ":" + Integer.valueOf(count).toString();
        return_str += ":" + order;
        return_str += ":";
        for(byte a : bytes.array()){
            return_str += String.format("%02x", a);
        }
        return return_str;
    }

    /**
     * UnStringify a Stringified bloom filter s
     * Used to import from python
     * @param s
     * @return Bloom filter from that string
     * @throws NoSuchAlgorithmException
     */
    public static BloomFilter fromString(String s)
            throws NoSuchAlgorithmException {
        String[] values = s.split(":");
        BloomFilter return_bloom = new BloomFilter(1);
        double error_rate;
        int num_slices, bits_per_slice, capacity, count;
        String order;
        byte[] buf;

        error_rate = Double.valueOf(values[0]);
        num_slices = Integer.valueOf(values[1]);
        bits_per_slice = Integer.valueOf(values[2]);
        capacity = Integer.valueOf(values[3]);
        count = Integer.valueOf(values[4]);
        order = values[5];
        return_bloom.setup(error_rate, num_slices, bits_per_slice, capacity, count);
        ByteBuffer bytes = ByteBuffer.allocate((int) Math.ceil(num_slices * bits_per_slice/8.));
        if(order.equals("little")){
            bytes.order(ByteOrder.LITTLE_ENDIAN);
        }
        else{
            bytes.order(ByteOrder.BIG_ENDIAN);
        }
        buf = new byte[values[6].length()/2];
        for(int i = 0; i < values[6].length(); i += 2){
            buf[i/2] = (byte) (Integer.parseInt(values[6].substring(i, i + 2), 16) & 0xFF);
        }
        bytes.put(buf);
        bytes.position(0);
        return_bloom.bitarray = (BitSet) BitSet.valueOf(bytes).clone();
        return return_bloom;
    }

    public int getCount() {
        return count;
    }

    public int getCapacity() {
        return capacity;
    }
}

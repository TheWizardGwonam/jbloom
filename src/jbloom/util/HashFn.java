package jbloom.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/*
created by Sam Findler on 4/12/2017

Every Bloom Filter has a series of hash functions associated with it.
HashFn implements that hash and allows the BloomFilter to carry it around with it
Unfortunately a current limitation of java is 32 bit array indexing, so it won't be quite as
robust as the python version
 */


public class HashFn {
    private int num_salts, fmt_length, num_slices, num_bits;
    private char fmt_code;
    private MessageDigest[] salts;

    /**
     This is a HashFn container that generates the hash functions
     for the bloom filter in the same manner to that of the dynamic-pybloom library.
    @param num_slices
    @param num_bits
    @returns HashFn
     */
    public HashFn(int num_slices, int num_bits)
            throws java.security.NoSuchAlgorithmException{
        String hash_type;
        int chunk_size, total_hash_bits;

        this.num_slices = num_slices;
        this.num_bits = num_bits;

        if(num_bits >= Short.MAX_VALUE){
            fmt_code = 'I';
            chunk_size = 4;
        }
        else{
            fmt_code = 'H';
            chunk_size = 2;
        }
        total_hash_bits = 8 * num_slices * chunk_size;
        if(total_hash_bits > 384){
            hash_type = "SHA-512";
        }
        else if(total_hash_bits > 256){
            hash_type = "SHA-384";
        }
        else if(total_hash_bits > 160){
            hash_type = "SHA-256";
        }
        else if(total_hash_bits > 128){
            hash_type = "SHA-1";
        }
        else{
            hash_type = "MD5";
        }
        fmt_length = MessageDigest.getInstance(hash_type).getDigestLength() / chunk_size;
        num_salts = (int) Math.ceil((double) num_slices / (double) fmt_length);
        salts = new MessageDigest[num_salts];
        for(int i = 0; i < num_salts; i++){
            ByteBuffer struct = ByteBuffer.allocate(4);
            struct.putInt(i);
            salts[i] = MessageDigest.getInstance(hash_type);
            MessageDigest temp = MessageDigest.getInstance(hash_type);
            temp.update(struct.array());
            salts[i].update(temp.digest());
        }
    }

    /**
    Hashes a key to perform a lookup in the bloom filter
    @param key
    @returns 1 hash for each of the segments of the bloom filter
     */
    public int[] hash(String key)
            throws java.lang.CloneNotSupportedException{
        int[] return_val = new int[num_slices];
        int counter = 0;
        for(int i = 0; i < num_salts; i++){
            MessageDigest hash = (MessageDigest) salts[i].clone();
            hash.update(key.getBytes(Charset.forName("UTF-8")));
            if(fmt_code == 'I'){
                ByteBuffer buffer = ByteBuffer.allocate(4*fmt_length);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.put(hash.digest());
                buffer.position(0);
                for(int j = 0; j < fmt_length; j++){
                    long item = Integer.toUnsignedLong(buffer.getInt());
                    return_val[counter] = (int) item % num_bits;
                    counter++;
                    if(counter == num_slices){
                        return return_val;
                    }
                }
            }
            else if(fmt_code == 'H'){
                ByteBuffer buffer = ByteBuffer.allocate(2*fmt_length);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.put(hash.digest());
                buffer.position(0);
                for(int j = 0; j < fmt_length; j++){
                    int item = Short.toUnsignedInt(buffer.getShort());
                    return_val[counter] = item % num_bits;
                    counter++;
                    if(counter == num_slices){
                        return return_val;
                    }
                }
            }
        }
        return return_val;
    }
}

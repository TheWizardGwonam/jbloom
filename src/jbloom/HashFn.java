/*
Every Bloom Filter has a series of hash functions associated with it.
HashFn implements that hash and allows the BloomFilter to carry it around with it
Unfortunately a current limitation of java is 32 bit array indexing, so it won't be quite as
robust as the python version
 */

package jbloom;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class HashFn {
    private int num_salts, fmt_length, num_slices, num_bits;
    private char fmt_code;
    private MessageDigest[] salts;

    public HashFn(int num_slices, int num_bits) throws java.security.NoSuchAlgorithmException{
        String hash_type;
        int chunk_size, total_hash_bits;

        this.num_slices = num_slices;
        this.num_bits = num_bits;

        if(num_bits >= 1 << 15){
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
            ByteBuffer struct_to_pack;
            if(fmt_code == 'I') {
                struct_to_pack = ByteBuffer.allocate(32);
                struct_to_pack.putInt(i);
            }
            else{
                struct_to_pack = ByteBuffer.allocate(16);
                struct_to_pack.putShort((short) i);
            }
            salts[i] = MessageDigest.getInstance(hash_type);
            salts[i].update(MessageDigest.getInstance(hash_type).digest(struct_to_pack.array()));
        }
    }

    public int[] hash(String key) throws java.lang.CloneNotSupportedException{
        int[] return_val = new int[num_slices];
        int counter = 0;
        for(int i = 0; i < num_salts; i++){
            MessageDigest hash = (MessageDigest) salts[i].clone();
            hash.update(key.getBytes());
            if(fmt_code == 'I'){
                for(int item : ByteBuffer.wrap(hash.digest()).asIntBuffer().array()){
                    return_val[counter] = item % num_bits;
                    counter++;
                    if(counter > num_slices){
                        return return_val;
                    }
                }
            }
            else if(fmt_code == 'H'){
                for(short item : ByteBuffer.wrap(hash.digest()).asShortBuffer().array()){
                    return_val[counter] = item % num_bits;
                    counter++;
                    if(counter > num_slices){
                        return return_val;
                    }
                }
            }
        }
        return return_val;
    }
}

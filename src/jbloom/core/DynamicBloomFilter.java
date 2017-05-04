package jbloom.core;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by Sam on 4/13/2017.
 */
public class DynamicBloomFilter {
    private ArrayList<BloomFilter> filters;
    private double individual_error_rate, max_error_rate;
    private int base_capacity, max_capacity;

    /**
     * Creates a DynamicBloomFilter
     * @param base_capacity capacity of each bloom filter in the filter
     * @param max_capacity overall maximum capacity
     * @param error_rate maximum error rte
     */
    public DynamicBloomFilter(int base_capacity, int max_capacity, double error_rate){
        assert(error_rate > 0 && error_rate < 1);
        assert(base_capacity > 0);
        assert(max_capacity > 0);
        filters = new ArrayList<>((int) Math.ceil(max_capacity/base_capacity));
        individual_error_rate = 1 - Math.exp(Math.log(1 - error_rate)/Math.ceil(max_capacity/base_capacity));
        max_error_rate = error_rate;
        this.base_capacity = base_capacity;
        this.max_capacity = max_capacity;
    }

    /**
     * Lookup to see if a key is in the bloom filter
     * @param key
     * @return True if in the filter, False if not in the filter with error_rate < the max fro the filter
     * @throws CloneNotSupportedException
     */
    public boolean has(String key)
            throws CloneNotSupportedException {
        for(int i = filters.size() - 1; i > -1; i--){
            if(filters.get(i).has(key)){
                return true;
            }
        }
        return false;
    }

    /**
     * Add key to the bloom filter
     * @param key
     * @return true if it was already in, false if it wasn't
     * @throws CloneNotSupportedException
     * @throws IndexOutOfBoundsException
     */
    public boolean add(String key)
            throws CloneNotSupportedException, NoSuchAlgorithmException {
        BloomFilter filter;
        if(this.has(key)){
            return true;
        }
        if(filters.isEmpty()){
            filter = new BloomFilter(base_capacity, individual_error_rate);
            filters.add(filter);
        }
        else{
            filter = filters.get(filters.size() - 1);
            if(filter.getCount() >= filter.getCapacity()){
                filter = new BloomFilter(base_capacity, individual_error_rate);
                filters.add(filter);
            }
        }
        filter.add(key, true);
        return false;
    }

    /**
     * Form the union of two bloom filters
     * @param other
     * @return this | other
     */
    public DynamicBloomFilter union(DynamicBloomFilter other){
        DynamicBloomFilter return_filter = new DynamicBloomFilter(base_capacity, max_capacity, max_error_rate);
        ArrayList<BloomFilter> other_filters = other.filters;
        for(int i = 0; i < this.filters.size(); i++) {
            boolean found_union_mate = false;
            for (int j = 0; j < other.filters.size(); j++) {
                int other_filter_index = other.filters.size() - 1 - j;
                BloomFilter union_filter = (this.filters.get(i).union(other.filters.get(other_filter_index)));
                if(union_filter.getCount() < this.base_capacity){
                    other_filters.set(other_filter_index, union_filter);
                    found_union_mate = true;
                    break;
                }
            }
            if(!found_union_mate){
                other_filters.add(this.filters.get(i));
            }
        }
        return_filter.filters = other_filters;
        return return_filter;
    }

    /**
     * Form the intersection of the bloom filter
     * @param other
     * @return this & other
     */
    public DynamicBloomFilter intersection(DynamicBloomFilter other)
            throws NoSuchAlgorithmException {
        DynamicBloomFilter return_filter = new DynamicBloomFilter(base_capacity, max_capacity, max_error_rate);
        for(BloomFilter filter : filters){
            BloomFilter bf = new BloomFilter(base_capacity, individual_error_rate);
            for(BloomFilter other_filter : other.filters){
                bf = bf.union(filter.intersection(other_filter));
            }
            return_filter.filters.add(bf);
        }
        return return_filter;
    }

    /**
     * Stringify the bloom filter in a way that is compatible with the python version of the library
     * @return stringified bloom filter
     */
    public String toString(){
        String return_string = "";
        return_string += Integer.valueOf(base_capacity).toString();
        return_string += "," + Integer.valueOf(max_capacity).toString();
        return_string += "," + Double.valueOf(max_error_rate).toString();
        return_string += "," + filters.get(0);
        for(BloomFilter filter : filters.subList(1, filters.size())){
            return_string += "|" + filter.toString();
        }
        return return_string;
    }

    /**
     * UnStringify a Stringified bloom filter s
     * Used to import from python
     * @param s
     * @return Bloom filter from that string
     * @throws NoSuchAlgorithmException
     */
    public static DynamicBloomFilter fromString(String s)
            throws NoSuchAlgorithmException {
        String[] values = s.split(",");
        int base_capacity = Integer.valueOf(values[0]);
        int max_capacity = Integer.valueOf(values[1]);
        double max_error_rate = Double.valueOf(values[2]);
        DynamicBloomFilter dbf = new DynamicBloomFilter(base_capacity, max_capacity, max_error_rate);
        for(String filter : values[3].split("\\|")){
            dbf.filters.add(BloomFilter.fromString(filter));
        }
        return dbf;
    }
}

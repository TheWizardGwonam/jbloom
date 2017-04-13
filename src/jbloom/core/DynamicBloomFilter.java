package jbloom.core;

import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Sam on 4/13/2017.
 */
public class DynamicBloomFilter {
    private ArrayList<BloomFilter> filters;
    private double individual_error_rate, max_error_rate;
    private int base_capacity, max_capacity;

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

    public boolean has(String key)
            throws CloneNotSupportedException {
        for(int i = filters.size() - 1; i > -1; i--){
            if(filters.get(i).has(key)){
                return true;
            }
        }
        return false;
    }

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

    public DynamicBloomFilter union(DynamicBloomFilter other){
        DynamicBloomFilter return_filter = new DynamicBloomFilter(base_capacity, max_capacity, max_error_rate);
        for(BloomFilter filter : filters){
            return_filter.filters.add(filter);
        }
        for(BloomFilter filter : other.filters){
            return_filter.filters.add(filter);
        }
        return return_filter;
    }

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

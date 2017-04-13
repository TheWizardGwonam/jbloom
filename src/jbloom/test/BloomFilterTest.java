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

    @Test
    public void testFromString()
            throws NoSuchAlgorithmException, CloneNotSupportedException {
        //this is a random string export from pybloom to test reading it in in java
        //in python it had 0-999 in it, so we make sure these are still there
        BloomFilter bf = BloomFilter.fromString("0.001:10:1438:1000:999:little:7347afb798afd782a775f413e599e4090751815acf6107a64042e6ad4f6355163d82290ca6f85c3ac3100f089974ed38ea0b054f03fd52051e388c923cdedc1ea66f4c4648719a8fa3bbafc48b50c80766f65e0c0bf01a4f43066055f2c0bd145d38b6cad839e0b1787ae2961d109dfb9b9754cb96a185fc7b78feaf8ca66d682c30afbfac9fbf3d429477fbfc8fedaf799669f52e38193e88dd3ed367f2e1715f625b16e3ae2d15a1e7363c764edad4d6cf1b1213a96fe54aec9393029ee7c3141c58d0e46e3104a7b8aa70fd16764230ef3316c30bf07e47c11bc0f7622d76d9719ff6e54b2833fc0cac01af485cab59976356d18a3c3caba3fc00eb87cb1f0b01a9154c539a97ac53dd506d355280c6e8afb581bcfa1d910fd7eb410427d277ba2b31aa606e995bc400eadcc5b39835c864a2bf968860e675974acabc9b1b5d9f5d7de6429a8b75b2b5b57932a1a52c6c100b75b84b8bec70ed22a1250380f5d526d1fdf8bceec95a3c912f9bdfd4146cb82b45d136fdc74c93e6200e1a4c9c926cff2f65af311a08916259465ab7a3ae8ea81c80ac81654a788b85083555e78bccf7a6688440b3dcd777b4d203a386c768053eed348f12db122d005b1bfb07c64e727b42d3f685051c9e67eb4cabe4176943e4b3e9a9819a6c6ece532d5d19ebbda38d89fcae1ff3a5eac9a71b19f6c2efa598a03849b04f830022df4ad72a3d0917a2dd3a7ceb2758364669be4d061352887b6793bf62d7ec28f4201e95c29fcfd6654db1b201f3b35e14d2a8dcdf95accaa4d0613f7f3238f6201e888ff1268678b6cce5f51da620c7a521e9f3addf68bb7b6a3301f6cb12ba36eceb606abe0d348703fcf6b3b829ca292ca6a82b0c2b28eb7ae3b1c581ca8cfda1de0fdb48d853708a96c604f908d513a83e7497c3d67bc7ac2765086be976fa48048901651633e6e8e2ce827a7eea0d2c075c05aa6791c3dc0aa8b39bdf6d540e5df5a25f58e72bc35da3bdca0a42cb2fbc70568616428ccb3bbf32bb2c2bd22d82e76d9af30ec800a6cad364d74cda35ebbbd39e24be84f4243040b9795cc18afc238fb8fe9eb8b1c3aa5872e90b2f79637a3210968dec41e5026046f72d8a3c3dd69e646eda98835f0ab76097c5605298dc41be63c719e39e5cfa5b72c450b2d41bc33378fa0afd244c86bb0f16b81a66e6863a692b2224d60e432b6c46a66123f7d099aec332ce4ab7fce1752597d6304c366d5975f0f5c198a0259e1530eba4e0e988bb4f003d39563407313aaeee2ae147eb89100373b906b7aef24a93cdf84e4f1dbc93e2ab4725350fa41c5b9789275624f3b2e385fb73245be432b164371191429996795f9f1189c7c6e511751cfc5926c8ff893677109abd3a43ecb12df7dc2416362aa80bb6a42abc98d35fe6032f12b1f64e4841022fb37c30316b57e82e35fdec7cf00c6a91068110188c4bdaff2470ee077aeb4169998c07efd186f2452baff90a3f7e5dfb6551624d90676d62613b123bea8402d17369ca9c2ec6c07c42e0256ce3f8ee4fdb84107c1aea9dbb37fccced2ef9ad9d33ddabbee093dba443ccddd63a852dd2ed89b3cbcf6cf30e280a122ff6c2efa47da53d168c501976f13e56d9402fb5d871a508c574799115eaecc5ef207c89ea06c9f69a6983c7f1e5160242b6ad9a7af5216dda7a1e5c01ca7403a75047088d9dbbb2799d9986542c331bb6561664917197ee9ab4dd5c443da32f2fda9c7d4fe15ef745afa3f097dbfe74ac21b38877eb005cae5fcffb2b3a16d2a399af3ee946793fd59eed7bcc3606f63834c31f7e858f39f1dcf18849666ea42029ef7bd7c55625bf5cb62f4a04afe85b261d63832ce2b729280c61492fa67802f4d5bd3826135a56e29160be83e760202a7f79981250a6b18dd63ff3576d737a737083e7d0435c0c1d56871613ec8044677666a058a7345f885afee20224b419d920be9d6bc70571a78b42e0935bf574fdf09a079bdca59c65a459350cded36c2dd5d886f217d64de14904e02ea272aa15532e4bdf72b7abb455d2d38b3fbde34c128bf6e1a8b3f1d89ce883cba7399412e496f9bccd117e944ee8c462fa2f11d3ef32447a10e2b7a00127fdaa93da4c8299195c0061f8acd979155a3b924ef4f9ca9f9171b740a45c3bad719413a8693190fdd299e47d674a31db4ada458b7f879f71c6ad0e30fa30a6c5ddfe6eac5f9e19ff201de215dfff9394ddd9569a6572adf86c59524411dfc6fb6e1f3f461531e7404f4c286e57047131baec154737fc8f3c8fd8d93e684f774eaf9eb1646f3abbb69b519224b1e28bf5af3ae0843b60b1befaffe5550983028253d7e593e0c362d7e54c19edb3beb7fb421479c13e621a2b263f84d3502a3fc3d538105e5177b18086a6afe71d7fd1149dae3e722cf0f81a648a61b3e91b05943d9899cb1a242d13cf5330c0f3c1fb7d570d25eb79e366760ef00e6dc52d0221afc0e448fdcdf61bf4d7390e35064c540e4f4598935c0d8113f88c706983149a434167758306");
        for(int i = 0; i < bf.getCapacity(); i++){
            assertTrue(bf.has(Integer.valueOf(i).toString()));
        }
    }
}
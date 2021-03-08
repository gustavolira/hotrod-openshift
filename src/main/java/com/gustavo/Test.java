package com.gustavo;

import java.util.stream.IntStream;

import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.SingleFileStoreConfigurationBuilder;

public class Test {

   public static void main(String[] args) {
      System.out.println("INITIALIZING ... ");
      Test test = new Test();
      test.insertEntries();
      System.out.println("FINISHED");
   }

   public void insertEntries() {
      try(RemoteCacheManager cacheManager = new RemoteCacheManager()) {
//         SingleFileStoreConfigurationBuilder builder = new ConfigurationBuilder().memory().maxCount(10).persistence().addSingleFileStore();

         RemoteCache<Integer, Integer> remoteCache = cacheManager.getCache("default");
//         RemoteCache<Integer, Integer> remoteCache = cacheManager.getCache("cache1");
//         .withFlags(Flag.SKIP_CACHE_LOAD);


//         RemoteCache<Integer, Integer> cache = cacheManager.getCache("test");

         IntStream.range(1, 20).forEach(it -> remoteCache.put(it, it));

         System.out.println(remoteCache.size());
//         System.out.println(remoteCache.withFlags(Flag.SKIP_CACHE_LOAD).size());

//         IntStream.range(1, 1000000).forEach(it -> cache.get(it));

         System.out.println("FINISH!!!!!");
      }
   }
}

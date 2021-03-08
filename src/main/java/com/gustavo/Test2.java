package com.gustavo;

import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.SaslQop;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;

public class Test2 {

   public static void main(String[] args) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer()
            .host("ab4098f8fd5764076a08753679389517-1988767233.eu-north-1.elb.amazonaws.com")
            .port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
            .security().authentication()
            .enable()
            .username("testuser")
            .password("testpassword")
            .realm("default")
            .saslQop(SaslQop.AUTH)
            .serverName("xpto-ispn")
            .saslMechanism("SCRAM-SHA-512")
            .ssl().disable();
      builder.clientIntelligence(ClientIntelligence.BASIC);
//      builder.remoteCache("my-cache")
//            .templateName(DefaultTemplate.DIST_SYNC);
//      builder.remoteCache("another-cache")
//            .configuration("<infinispan><cache-container><distributed-cache name=\"another-cache\"><encoding media-type=\"application/x-protostream\"/></distributed-cache></cache-container></infinispan>");
      try (RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build())) {
         // Get a remote cache that does not exist.
         // Rather than return null, create the cache from a template.
         RemoteCache<String, String> cache = cacheManager.getCache("default");
         /// Store a value.
         cache.put("hello", "world");
         // Retrieve the value and print it.
         System.out.printf("key = %s\n", cache.get("hello"));
      }
   }
}

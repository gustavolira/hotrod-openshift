package com.gustavo;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryRemoved;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryModifiedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryRemovedEvent;

import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.persistence.jdbc.configuration.ConnectionFactoryConfiguration;
import org.infinispan.persistence.jdbc.configuration.JdbcStringBasedStoreConfigurationBuilder;

/**
 * Sample client application code. For more examples please see our client documentation
 * (https://infinispan.org/docs/stable/titles/hotrod_java/hotrod_java.html).
 * <p>
 * In order to run this application, it's necessary for a Infinispan server to be executing on your localhost with the
 * HotRod endpoint available on port 11222. The easiest way to achieve this, is by utilising our server docker image:
 * <p>
 * <code>docker run -p 11222:11222 -e USER="user" -e PASS="pass" infinispan/server</code>
 */
public class Application {

   public void propertyConfiguration() {
      System.out.println("\n\n1.  Demonstrating basic usage of Infinispan Client using 'bkp-hotrod-client.properties' configuration.");
      try (RemoteCacheManager remoteCacheManager = new RemoteCacheManager()) {
         // Retrieve the cache defined by `infinispan.client.hotrod.cache.my-cache.template_name=`
         RemoteCache<String, String> cache = remoteCacheManager.getCache("my-cache");
         helloWord(cache);
      }
   }

   public void jdbcTest() {

      //HOTROD CONFIGURATION
      ConfigurationBuilder cb = new ConfigurationBuilder();
      // Configure security
      cb.security()
            .authentication()
            .username("user")
            .password("pass")
            .saslMechanism("DIGEST-MD5");
      // Configure the server(s) to connect to
      cb.addServer()
            .host("localhost")
            .port(11222);

      //JDBC CONFIGURATION
      org.infinispan.configuration.cache.ConfigurationBuilder builder = new org.infinispan.configuration.cache.ConfigurationBuilder();
      builder
            .memory().maxCount(2)
            .persistence()
            .addStore(JdbcStringBasedStoreConfigurationBuilder.class)
            .segmented(false)
            .table()
            .tableNamePrefix("ISPN_TABLE")
            .idColumnName("id").idColumnType("VARCHAR(255)")
            .dataColumnName("data").dataColumnType("BYTEA")
            .timestampColumnName("timestamp").timestampColumnType("BIGINT")
            .createOnStart(true)
            .dropOnExit(false)
            .dataSource()
            .jndiUrl("jdbc/postgres");
//            .tableNamePrefix("ISPN_STRING_TABLE")
//            .idColumnName("ID_COLUMN").idColumnType("VARCHAR(255)")
//            .dataColumnName("DATA_COLUMN").dataColumnType("BINARY")
//            .timestampColumnName("TIMESTAMP_COLUMN").timestampColumnType("BIGINT")
//            .segmentColumnName("SEGMENT_COLUMN").segmentColumnType("INT")
//            .connectionPool()
//            .connectionUrl("jdbc:h2:mem:infinispan_string_based;DB_CLOSE_DELAY=-1")
//            .username("sa")
//            .driverClass("org.h2.Driver");

      RemoteCacheManager remoteCacheManager = new RemoteCacheManager(cb.build());
      RemoteCache<Integer, Integer> cache = remoteCacheManager.administration().getOrCreateCache("jdbc-cache", builder.build());

      IntStream.rangeClosed(1, 100).forEach(it -> cache.put(it, it));

   }

   public void programmaticConfiguration() {
      System.out.println("\n\n2.  Demonstrating basic usage of the Infinispan Client using programmatic configuration.");



      ConfigurationBuilder cb = new ConfigurationBuilder();
      // Configure security
      cb.security()
            .authentication()
            .username("user")
            .password("pass")
            .saslMechanism("DIGEST-MD5");
      // Configure the server(s) to connect to
      cb.addServer()
            .host("192.168.33.20")
            .port(11222);
      // Configure the caches to create on first access
      cb.remoteCache("my-cache")
            .templateName("org.infinispan.DIST_SYNC");

      try (RemoteCacheManager remoteCacheManager = new RemoteCacheManager(cb.build())) {
         RemoteCache<String, String> cache = remoteCacheManager.getCache("my-cache");
         helloWord(cache);
      }
   }

   private void helloWord(RemoteCache<String, String> cache) {
      System.out.println("  Storing value 'World' under key 'Hello'");
      String oldValue = cache.put("Hello", "World");
      System.out.printf("  Done.  Saw old value as '%s'\n", oldValue);

      System.out.println("  Replacing 'World' with 'Mars'.");
      boolean worked = cache.replace("Hello", "World", "Mars");
      System.out.printf("  Successful? %s\n", worked);
      System.out.println(cache.get("Hello"));

      assert oldValue == null;
      assert worked;
   }

   public static void main(String[] args) throws Exception {
      System.out.println("\n\n\n   ********************************  \n\n\n");
      System.out.println("Hello.  This is a sample application making use of Infinispan.");
      Application a = new Application();
//      a.propertyConfiguration();
      a.jdbcTest();
//      a.programmaticConfiguration();
//      a.asyncOperations();
//      a.registeringListeners();
      System.out.println("Sample complete.");
      System.out.println("\n\n\n   ********************************  \n\n\n");
   }

}

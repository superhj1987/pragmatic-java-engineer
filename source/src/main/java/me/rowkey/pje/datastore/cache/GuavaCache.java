package me.rowkey.pje.datastore.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.guava.CaffeinatedGuava;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.TimeUnit;

/**
 * Created by Bryant.Hang on 2017/8/23.
 */
public class GuavaCache {
    public static void main(String[] args) {
        final int MAX_ENTRIES = 1000; //最大元素数目
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(MAX_ENTRIES)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())//并行度
                .expireAfterWrite(2, TimeUnit.SECONDS) //写入2秒后失效
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        return "";//异步加载数据到缓存
                    }

                    @Override
                    public ListenableFuture<String> reload(String key, String oldValue) throws Exception {
                        return null;
                    }
                });

        //Using the cache
        String value = cache.getUnchecked("testKey");

        LoadingCache<String, String> cache1 = CaffeinatedGuava.build(
                Caffeine.newBuilder().maximumSize(MAX_ENTRIES),
                new CacheLoader<String, String>() { // Guava's CacheLoader
                    @Override
                    public String load(String key) throws Exception {
                        return "";
                    }
                });
    }
}

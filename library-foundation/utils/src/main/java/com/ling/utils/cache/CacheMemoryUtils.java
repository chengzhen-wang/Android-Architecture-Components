package com.ling.utils.cache;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

import com.ling.utils.constant.CacheConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * author : wangchengzhen
 * github : https://github.com/Blankj/AndroidUtilCode
 * time   : 2021/11/13
 * desc   : utils about memory cache - 内存缓存相关
 */
public final class CacheMemoryUtils implements CacheConstants {

    private static final int DEFAULT_MAX_COUNT = 256;

    private static final Map<String, CacheMemoryUtils> CACHE_MAP = new HashMap<>();

    private final String mCacheKey;
    private final LruCache<String, CacheValue> mMemoryCache;

    /**
     * 获取缓存实例
     * <p>
     * Return the single {@link CacheMemoryUtils} instance.
     *
     * @return the single {@link CacheMemoryUtils} instance
     */
    public static CacheMemoryUtils getInstance() {
        return getInstance(DEFAULT_MAX_COUNT);
    }

    /**
     * 获取缓存实例
     * <p>
     * Return the single {@link CacheMemoryUtils} instance.
     *
     * @param maxCount The max count of cache.
     * @return the single {@link CacheMemoryUtils} instance
     */
    public static CacheMemoryUtils getInstance(final int maxCount) {
        return getInstance(String.valueOf(maxCount), maxCount);
    }

    /**
     * 获取缓存实例
     * <p>
     * Return the single {@link CacheMemoryUtils} instance.
     *
     * @param cacheKey The key of cache.
     * @param maxCount The max count of cache.
     * @return the single {@link CacheMemoryUtils} instance
     */
    public static CacheMemoryUtils getInstance(final String cacheKey, final int maxCount) {
        CacheMemoryUtils cache = CACHE_MAP.get(cacheKey);
        if (cache == null) {
            synchronized (CacheMemoryUtils.class) {
                cache = CACHE_MAP.get(cacheKey);
                if (cache == null) {
                    cache = new CacheMemoryUtils(cacheKey, new LruCache<String, CacheValue>(maxCount));
                    CACHE_MAP.put(cacheKey, cache);
                }
            }
        }
        return cache;
    }

    private CacheMemoryUtils(String cacheKey, LruCache<String, CacheValue> memoryCache) {
        mCacheKey = cacheKey;
        mMemoryCache = memoryCache;
    }

    @Override
    public String toString() {
        return mCacheKey + "@" + Integer.toHexString(hashCode());
    }

    /**
     * 缓存中写入数据
     * <p>
     * Put bytes in cache.
     *
     * @param key   The key of cache.
     * @param value The value of cache.
     */
    public void put(@NonNull final String key, final Object value) {
        put(key, value, -1);
    }

    /**
     * 缓存中写入数据
     * <p>
     * Put bytes in cache.
     *
     * @param key      The key of cache.
     * @param value    The value of cache.
     * @param saveTime The save time of cache, in seconds.
     */
    public void put(@NonNull final String key, final Object value, int saveTime) {
        if (value == null) return;
        long dueTime = saveTime < 0 ? -1 : System.currentTimeMillis() + saveTime * 1000;
        mMemoryCache.put(key, new CacheValue(dueTime, value));
    }

    /**
     * 缓存中读取字节数组
     * <p>
     * Return the value in cache.
     *
     * @param key The key of cache.
     * @param <T> The value type.
     * @return the value if cache exists or null otherwise
     */
    public <T> T get(@NonNull final String key) {
        return get(key, null);
    }

    /**
     * 缓存中读取字节数组
     * <p>
     * Return the value in cache.
     *
     * @param key          The key of cache.
     * @param defaultValue The default value if the cache doesn't exist.
     * @param <T>          The value type.
     * @return the value if cache exists or defaultValue otherwise
     */
    public <T> T get(@NonNull final String key, final T defaultValue) {
        CacheValue val = mMemoryCache.get(key);
        if (val == null) return defaultValue;
        if (val.dueTime == -1 || val.dueTime >= System.currentTimeMillis()) {
            //noinspection unchecked
            return (T) val.value;
        }
        mMemoryCache.remove(key);
        return defaultValue;
    }

    /**
     * 获取缓存个数
     * <p>
     * Return the count of cache.
     *
     * @return the count of cache
     */
    public int getCacheCount() {
        return mMemoryCache.size();
    }

    /**
     * 根据键值移除缓存
     * <p>
     * Remove the cache by key.
     *
     * @param key The key of cache.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public Object remove(@NonNull final String key) {
        CacheValue remove = mMemoryCache.remove(key);
        if (remove == null) return null;
        return remove.value;
    }

    /**
     * 清除所有缓存
     * <p>
     * Clear all of the cache.
     */
    public void clear() {
        mMemoryCache.evictAll();
    }

    private static final class CacheValue {

        long dueTime;
        Object value;

        CacheValue(long dueTime, Object value) {
            this.dueTime = dueTime;
            this.value = value;
        }
    }
}

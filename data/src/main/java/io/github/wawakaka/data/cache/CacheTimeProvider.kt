package io.github.wawakaka.data.cache

interface CacheTimeProvider {
    fun currentTimeMillis(): Long
}

class SystemCacheTimeProvider : CacheTimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}

package ru.nikidzawa.app.configs.redis;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import ru.nikidzawa.app.configs.redis.annotation.UpdateCacheIfChangedKey;

import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
public class CacheAspect {

    @Autowired
    private CacheManager cacheManager;

    @Pointcut("@annotation(updateCacheIfChangedKey)")
    public void updateCache(UpdateCacheIfChangedKey updateCacheIfChangedKey) {}

    @Around(value = "updateCache(updateCacheIfChangedKey)", argNames = "proceedingJoinPoint,updateCacheIfChangedKey")
    public Object updateCacheAroundMethod(ProceedingJoinPoint proceedingJoinPoint, UpdateCacheIfChangedKey updateCacheIfChangedKey) throws Throwable {
        String prefix = updateCacheIfChangedKey.prefix();
        String oldKey = (String) proceedingJoinPoint.getArgs()[0];
        Optional<String> newKey = (Optional<String>) proceedingJoinPoint.getArgs()[1];
        Object result = proceedingJoinPoint.proceed();
        new Thread(() -> {
            if (newKey.isPresent()) {
                Objects.requireNonNull(cacheManager.getCache(prefix)).evict(oldKey);
                Objects.requireNonNull(cacheManager.getCache(prefix)).put(newKey.get(), result);
            } else {
                Objects.requireNonNull(cacheManager.getCache(prefix)).put(oldKey, result);}
        }).start();
        return result;
    }
}

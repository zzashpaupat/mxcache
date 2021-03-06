/*
 * Copyright (c) 2008-2014 Maxifier Ltd. All Rights Reserved.
 */
package com.maxifier.mxcache;

import com.maxifier.mxcache.caches.Cache;
import com.maxifier.mxcache.caches.Calculable;
import com.maxifier.mxcache.config.MxCacheConfigProvider;
import com.maxifier.mxcache.config.MxCacheConfigProviderImpl;
import com.maxifier.mxcache.context.CacheContext;
import com.maxifier.mxcache.impl.CacheProviderImpl;
import com.maxifier.mxcache.impl.resource.DependencyNode;
import com.maxifier.mxcache.provider.CacheDescriptor;
import com.maxifier.mxcache.provider.CacheProvider;
import com.maxifier.mxcache.clean.CleanableRegister;
import com.maxifier.mxcache.clean.Cleanable;
import com.maxifier.mxcache.clean.ClassCacheIds;
import com.maxifier.mxcache.clean.CacheCleaner;

import java.util.List;
import java.util.Map;

import com.maxifier.mxcache.scheduler.MxScheduler;
import com.maxifier.mxcache.scheduler.MxSchedulerImpl;

import javax.annotation.Nonnull;

import javax.annotation.Nullable;

/**
 * @author Alexander Kochurov (alexander.kochurov@maxifier.com)
 */
public final class CacheFactory {
    private static final CacheProvider PROVIDER = new CacheProviderImpl();
    private static final CleanableRegister REGISTRY = new CleanableRegister();
    private static final MxCacheConfigProvider CONFIGURATION = new MxCacheConfigProviderImpl(true);

    private static final ContextResolver<Object> RESOLVER = new GenericContextResolver();

    private static final MxScheduler SCHEDULER = new MxSchedulerImpl();

    private static final CacheContext DEFAULT_CONTEXT = new DefaultCacheContext();

    private static CacheContext overrideDefault = null;

    private static final ThreadLocal<CacheProvider> PROVIDER_OVERRIDE = new ThreadLocal<CacheProvider>();

    private CacheFactory() {
    }

    @PublicAPI
    // used in code generated by 2.1.9
    @Deprecated
    public static synchronized InstanceProvider getInstanceProvider() {
        return getDefaultContext().getInstanceProvider();
    }

    public static CacheCleaner getCleaner() {
        return REGISTRY;
    }

    public static DependencyNode getClassDependencyNode(Class<?> clazz) {
        return REGISTRY.getClassDependencyNode(clazz);
    }

    public static DependencyNode getClassInstanceDependencyNode(Class<?> clazz) {
        return REGISTRY.getClassInstanceDependencyNode(clazz);
    }

    public static DependencyNode getTagDependencyNode(String tag) {
        return REGISTRY.getTagDependencyNode(tag);
    }

    public static DependencyNode getGroupDependencyNode(String group) {
        return REGISTRY.getGroupDependencyNode(group);
    }

    @PublicAPI
    // used in code generated by 2.1.9
    public static <T> void registerClass(Class<T> c, Cleanable<T> cleanable, @Nullable Map<String, ClassCacheIds> groups, @Nullable Map<String, ClassCacheIds> tags) {
        REGISTRY.registerClass(c, cleanable, groups, tags);
    }

    @PublicAPI
    // this method is used in generated code
    public static void registerInstance(Object o, Class c) {
        REGISTRY.registerInstance(o, c);
    }

    public static List<Cache> getCaches(@Nonnull CacheDescriptor descriptor) {
        return REGISTRY.getCaches(descriptor);
    }

    @PublicAPI
    // this method is used in generated by 2.1.9 code
    public static void registerCache(Class<?> c, int id, Class key, Class value, String group, String[] tags, Object calculable, String methodName, String methodDesc) {
        // we cast to Calculable cause we cannot change signature of this method for backward compatibility
        getProvider().registerCache(c, id, key, value, group, tags, (Calculable)calculable, methodName, methodDesc, null);
    }

    @PublicAPI
    // this method is used in generated code
    public static void registerCache(Class<?> c, int id, Class key, Class value, String group, String[] tags, Object calculable, String methodName, String methodDesc, String cacheName) {
        // we cast to Calculable cause we cannot change signature of this method for backward compatibility
        getProvider().registerCache(c, id, key, value, group, tags, (Calculable)calculable, methodName, methodDesc, cacheName);
    }

    @PublicAPI
    // used in code generated by 2.1.9
    @Deprecated
    public static Object createCache(@Nonnull Class caller, int id, Object instance) {
        return getProvider().createCache(caller, id, instance, getDefaultContext());
    }

    @PublicAPI
    // this method is used in generated code
    public static Object createCache(@Nonnull Class caller, int id, Object instance, CacheContext context) {
        return getProvider().createCache(caller, id, instance, context);
    }

    public static CacheProvider getProvider() {
        CacheProvider override = PROVIDER_OVERRIDE.get();
        if (override != null) {
            return override;
        }
        return PROVIDER;
    }

    public static void setProviderOverride(CacheProvider providerOverride) {
        CacheFactory.PROVIDER_OVERRIDE.set(providerOverride);
    }

    @PublicAPI
    // this method is used in generated code
    public static CacheContext getContext(Object stream) {
        return RESOLVER.getContext(stream);
    }

    public static MxCacheConfigProvider getConfiguration() {
        return CONFIGURATION;
    }

    @Nonnull
    public static CacheContext getDefaultContext() {
        return overrideDefault == null ? DEFAULT_CONTEXT : overrideDefault;
    }

    @PublicAPI
    // may be used by client code
    public static void setDefaultContext(CacheContext defaultContext) {
        overrideDefault = defaultContext;
    }

    @PublicAPI
    public static MxScheduler getScheduler() {
        return SCHEDULER;
    }
}

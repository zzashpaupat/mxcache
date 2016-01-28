/*
 * Copyright (c) 2008-2014 Maxifier Ltd. All Rights Reserved.
 */
package com.maxifier.mxcache.impl.caches.def;

import com.maxifier.mxcache.caches.*;
import com.maxifier.mxcache.impl.MutableStatistics;
import com.maxifier.mxcache.impl.resource.DependencyNode;
import com.maxifier.mxcache.util.HashWeakReference;
import gnu.trove.set.hash.THashSet;

import javax.annotation.Nonnull;

import java.lang.ref.Reference;
import java.util.Iterator;
import java.util.Set;

/**
 * Inline dependency caches are special ones that implement both Cache and DependencyNode.
 * It is used to reduce memory consumption of memory cache for the simplest types of caches.
 *
 * THIS IS GENERATED CLASS! DON'T EDIT IT MANUALLY!
 *
 * GENERATED FROM PInlineDependencyCache.template
 *
 * @author Andrey Yakoushin (andrey.yakoushin@maxifier.com)
 * @author Alexander Kochurov (alexander.kochurov@maxifier.com)
 */
public class ObjectInlineDependencyCache extends ObjectInlineCacheImpl implements DependencyNode {
    /**
     * Set of dependent nodes. It may be null cause there is no need to allocate whole set for each node.
     */
    private Set<Reference<DependencyNode>> dependentNodes;

    /**
     * Number of elements in dependentNodes after which all the set should be checked for the presence of
     * references to GC'ed objects.
     *
     * This threshold is required in order to evict such references as they pollute memory and never GC'ed otherwise.
     */
    private int cleanupThreshold = 10;

    private Reference<DependencyNode> selfReference;

    public ObjectInlineDependencyCache(Object owner, ObjectCalculatable calculable, MutableStatistics statistics) {
        super(owner, calculable, statistics);
        setDependencyNode(this);
    }

    @Override
    public synchronized void visitDependantNodes(Visitor visitor) {
        if (dependentNodes != null) {
            for (Iterator<Reference<DependencyNode>> it = dependentNodes.iterator(); it.hasNext();) {
                Reference<DependencyNode> ref = it.next();
                DependencyNode instance = ref.get();
                if (instance != null) {
                    visitor.visit(instance);
                } else {
                    it.remove();
                }
            }
        }
    }

    @Override
    public synchronized Reference<DependencyNode> getSelfReference() {
        if (selfReference == null) {
            selfReference = new HashWeakReference<DependencyNode>(this);
        }
        return selfReference;
    }

    @Override
    public synchronized void trackDependency(DependencyNode node) {
        if (dependentNodes == null) {
            dependentNodes = new THashSet<Reference<DependencyNode>>();
        }
        dependentNodes.add(node.getSelfReference());
        cleanupIfNeeded();
    }

    private void cleanupIfNeeded() {
        if (dependentNodes.size() >= cleanupThreshold) {
            for (Iterator<Reference<DependencyNode>> it = dependentNodes.iterator(); it.hasNext(); ) {
                if (it.next().get() == null) {
                    it.remove();
                }
            }
            // It's important to increase cleanup threshold according to the number of elements in a set
            // in order to maintain the balance between CPU-overhead and memory-overhead

            // The cleanup has O(N) complexity, so doing this on addition of N new elements would lead to constant
            // small overhead and thus would not affect the asymptotic behaviour of operations.

            // The memory overhead could be significant but it's guaranteed that memory usage would not be more than
            // 2 * peak memory usage for alive elements.
            cleanupThreshold = dependentNodes.size() * 2;
        }
    }

    @Override
    public void addNode(@Nonnull CleaningNode cache) {
        throw new UnsupportedOperationException("Inline dependency node should has only one cache");
    }
}

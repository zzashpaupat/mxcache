/*
 * Copyright (c) 2008-2014 Maxifier Ltd. All Rights Reserved.
 */
package com.maxifier.mxcache.impl.caches.def;

import com.maxifier.mxcache.storage.*;

/**
 * IntShortTroveStorage
 *
 * THIS IS GENERATED CLASS! DON'T EDIT IT MANUALLY!
 *
 * GENERATED FROM P2PTroveStorage.template
 *
 * @author Andrey Yakoushin (andrey.yakoushin@maxifier.com)
 * @author Alexander Kochurov (alexander.kochurov@maxifier.com)
 */
public class IntShortTroveStorage extends gnu.trove.map.hash.TIntShortHashMap implements IntShortStorage {
    @Override
    public boolean isCalculated(int o) {
        return super.contains(o);
    }

    @Override
    public short load(int o) {
        return super.get(o);
    }

    @Override
    public void save(int o, short t) {
        put(o, t);
    }
}
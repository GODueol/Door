package com.teamdoor.android.door.Util;

import com.squareup.otto.Bus;

/**
 * Created by gimbyeongjin on 2017. 10. 21..
 */

public class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }

}

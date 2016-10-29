package com.canice.wristbandapp.model;

import java.util.List;

public class RecordSleepData extends ResponseInfo {

    public List<Item> sleepInfos;

    public static final class Item {
        public long ssmTime;
        public long qsmTime;
        public String sleepDate;
    }

    public long getSsmTime() {
        if (sleepInfos == null) {
            return 0;
        }
        long t = 0;
        for (Item item : sleepInfos) {
            t += item.ssmTime;
        }
        return t;
    }

    public long getQsmTime() {
        if (sleepInfos == null) {
            return 0;
        }
        long t = 0;
        for (Item item : sleepInfos) {
            t += item.qsmTime;
        }
        return t;
    }
}

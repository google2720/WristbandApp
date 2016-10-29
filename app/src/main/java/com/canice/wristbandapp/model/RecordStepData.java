package com.canice.wristbandapp.model;

import java.util.List;

public class RecordStepData extends ResponseInfo {

    public List<Item> stepInfos;

    public static final class Item {
        public int stepNum;
        public String recordDate;
    }

    public long getTotal() {
        if (stepInfos == null) {
            return 0;
        }
        long t = 0;
        for (Item item : stepInfos) {
            t += item.stepNum;
        }
        return t;
    }
}

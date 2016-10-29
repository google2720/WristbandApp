package com.canice.wristbandapp.ble;

import com.canice.wristbandapp.ble.data.Data;
import com.canice.wristbandapp.ble.data.HeartRateDataResult;
import com.canice.wristbandapp.ble.data.HistoryResult;

/**
 * notify通知解释
 * Created by y on 2016/6/29.
 */
public class NotifyDataHelper {

    public static Data parser(byte[] data) {
        if (data == null) {
            return null;
        }
        Data r = HeartRateDataResult.parser(data);
        if (r != null) {
            return r;
        }
        r = HistoryResult.parser(data);
        if (r != null) {
            return r;
        }
        return null;
    }
}

package com.canice.wristbandapp.ble;

import com.canice.wristbandapp.ble.data.HistoryResult;

/**
 * 历史记录控制器
 * Created by y on 2016/7/2.
 */
public class HistoryController {

    private BleCallback mCallback = new SimpleBleCallback() {

        @Override
        public void onFetchHistoryFailed(int error) {
            state = STATE_FAILED;
        }

        @Override
        public void onFetchHistory(String address, HistoryResult result) {
            if (state == STATE_START) {
                state = STATE_FETCHING;
            }
        }

        @Override
        public void onFetchHistoryStart() {
            state = STATE_START;
        }

        @Override
        public void onFetchHistorySuccess() {
            state = STATE_SUCCESS;
        }
    };
    public static final int STATE_INIT = 0;
    public static final int STATE_START = 1;
    public static final int STATE_FETCHING = 2;
    public static final int STATE_SUCCESS = 3;
    public static final int STATE_FAILED = 4;
    private int state = STATE_INIT;

    public HistoryController(BleController ble) {
        ble.addCallback(mCallback);
    }

    public int getState() {
        return state;
    }
}

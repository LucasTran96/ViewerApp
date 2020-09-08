/**
 * ClassName: Data.java
 * @Project: SecondClone
 * @author  Lucas Walker (lucas.walker@jexpa.com)
 * Created Date: 2018-06-05
 * Description:
 * History:2018-10-08
 * Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Model;

import java.io.Serializable;

public class Data implements Serializable {

    private String DeviceInfo;
    private String Extend;
    private String DeviceFeature;
    private String DeviceLive;

    public Data() {
    }

    public String getDeviceInfo() {
        return DeviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        DeviceInfo = deviceInfo;
    }

    public String getExtend() {
        return Extend;
    }

    public void setExtend(String extend) {
        Extend = extend;
    }

    public String getDeviceFeature() {
        return DeviceFeature;
    }

    public void setDeviceFeature(String deviceFeature) {
        DeviceFeature = deviceFeature;
    }

    public String getDeviceLive() {
        return DeviceLive;
    }

    public void setDeviceLive(String deviceLive) {
        DeviceLive = deviceLive;
    }
}
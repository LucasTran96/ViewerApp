/*
 ClassName: ModelDevice.java
 Project: SecondClone
 author  Lucas Walker (lucas.walker@jexpa.com)
 Created Date: 2018-06-05
 Description:
 History:2018-10-08
 Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.secondclone.Model;

import java.io.Serializable;


public class ModelDevice implements Serializable {
    private String name;
    private String id;

    public ModelDevice(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public ModelDevice() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

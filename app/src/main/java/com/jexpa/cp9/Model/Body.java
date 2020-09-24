/*
  ClassName: Body.java
  Project: ViewerApp
 author  Lucas Walker (lucas.walker@jexpa.com)
  Created Date: 2018-06-05
  Description:
  History:2018-10-08
  Copyright © 2018 Jexpa LLC. All rights reserved.
 */

package com.jexpa.cp9.Model;

import java.io.Serializable;

public class Body implements Serializable {

    private String IsSuccess;
    private String ResultId;
    private String Data;
    private String Code;
    private String Description;
    private String DebugInfo;


    public Body() {
    }

    public String getIsSuccess() {
        return IsSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        IsSuccess = isSuccess;
    }

    public String getResultId() {
        return ResultId;
    }

    public void setResultId(String resultId) {
        ResultId = resultId;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDebugInfo() {
        return DebugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        DebugInfo = debugInfo;
    }
}

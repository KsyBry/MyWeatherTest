package com.example.demoweathertest;

import java.util.List;

/**
 * 请求天气API返回结果
 */
public class Weather {
    //值为0或1：1：成功；0：失败
    private String          status;     //返回状态
    private String          count;      //返回结果总数目
    private String          info;       //返回的状态信息
    private String          infoCode;   //返回状态说明,10000 代表正确
    private List<Forecasts> forecasts;  //预报天气信息数据

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfoCode() {
        return infoCode;
    }

    public void setInfoCode(String infoCode) {
        this.infoCode = infoCode;
    }

    public List<Forecasts> getForecasts() {
        return forecasts;
    }

    public void setForecasts(String forecasts) {
//        this.forecasts = forecasts;
    }
}

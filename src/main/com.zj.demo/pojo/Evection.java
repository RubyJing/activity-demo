package com.zj.demo.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 出差申请中的流程变量对象
 */
public class Evection implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 出差单的名称
     */
    private String evectionName;

    /**
     * 出差天数
     */
    private Double day;

    /**
     *开始时间
     */
    private Date beginDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 目的地
     */
    private String dastination;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvectionName() {
        return evectionName;
    }

    public void setEvectionName(String evectionName) {
        this.evectionName = evectionName;
    }

    public Double getDay() {
        return day;
    }

    public void setDay(Double day) {
        this.day = day;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDastination() {
        return dastination;
    }

    public void setDastination(String dastination) {
        this.dastination = dastination;
    }
}

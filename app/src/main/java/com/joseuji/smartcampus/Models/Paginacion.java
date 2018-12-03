package com.joseuji.smartcampus.Models;

public class Paginacion {
    private Integer rowCount;
    private Integer pageSize;
    private Integer startRecord;

    public Paginacion(Integer rowCount, Integer pageSize, Integer startRecord) {
        this.rowCount = rowCount;
        this.pageSize = pageSize;
        this.startRecord = startRecord;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getStartRecord() {
        return startRecord;
    }

    public void setStartRecord(Integer startRecord) {
        this.startRecord = startRecord;
    }
}

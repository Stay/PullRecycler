package com.stay4it.widgets.pull.section;

/**
 * Created by Stay on 16/11/15.
 * Powered by www.stay4it.com
 */
public class SectionData<T> {
    public boolean isHeader;
    public int headerIndex;//用于索引ABC...的index定位
    public T t;
    public String header;

    public SectionData(boolean isHeader, int headerIndex, String header) {
        this.isHeader = isHeader;
        this.header = header;
        this.headerIndex = headerIndex;
        this.t = null;
    }

    public SectionData(T t) {
        this.isHeader = false;
        this.header = null;
        this.t = t;
    }
}

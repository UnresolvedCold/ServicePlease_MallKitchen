package com.schwifty.serviceplease_mallkitchen.Database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class ViewsDatabase
{
    @Id
    private Long Id;

    @Property
    private String ViewNumber;

    @Property
    private int leftMargin;

    @Property
    private int topMargin;

    @Property
    private int pageX;

    @Property
    private int pageY;

    @Property
    private int Width;

    @Property
    private int Height;

    @Property
    private int TextSize;

    @Generated(hash = 526950563)
    public ViewsDatabase(Long Id, String ViewNumber, int leftMargin, int topMargin,
            int pageX, int pageY, int Width, int Height, int TextSize) {
        this.Id = Id;
        this.ViewNumber = ViewNumber;
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;
        this.pageX = pageX;
        this.pageY = pageY;
        this.Width = Width;
        this.Height = Height;
        this.TextSize = TextSize;
    }

    @Generated(hash = 118173102)
    public ViewsDatabase() {
    }

    public Long getId() {
        return this.Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getViewNumber() {
        return this.ViewNumber;
    }

    public void setViewNumber(String ViewNumber) {
        this.ViewNumber = ViewNumber;
    }

    public int getLeftMargin() {
        return this.leftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public int getTopMargin() {
        return this.topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    public int getPageX() {
        return this.pageX;
    }

    public void setPageX(int pageX) {
        this.pageX = pageX;
    }

    public int getPageY() {
        return this.pageY;
    }

    public void setPageY(int pageY) {
        this.pageY = pageY;
    }

    public int getWidth() {
        return this.Width;
    }

    public void setWidth(int Width) {
        this.Width = Width;
    }

    public int getHeight() {
        return this.Height;
    }

    public void setHeight(int Height) {
        this.Height = Height;
    }

    public int getTextSize() {
        return this.TextSize;
    }

    public void setTextSize(int TextSize) {
        this.TextSize = TextSize;
    }


}

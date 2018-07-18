package org.fingerlinks.mobile.android.utils.widget.calendar;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by raphaelbussa on 17/03/16.
 */

public class SupportCalendarItem implements Serializable {

    private Date date;
    private String id;
    private String type;
    private int style;
    private Object data;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

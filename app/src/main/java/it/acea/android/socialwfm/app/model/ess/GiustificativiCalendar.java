package it.acea.android.socialwfm.app.model.ess;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by raphaelbussa on 30/03/16.
 */
public class GiustificativiCalendar extends RealmObject {

    @PrimaryKey
    private String Date;
    private String Year;
    private String Month;
    private String Day;
    private String Monthtext;
    private String Daytext;
    private String Actkey;
    private String Actkeytext;
    private String Actinfo;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getMonthtext() {
        return Monthtext;
    }

    public void setMonthtext(String monthtext) {
        Monthtext = monthtext;
    }

    public String getDaytext() {
        return Daytext;
    }

    public void setDaytext(String daytext) {
        Daytext = daytext;
    }

    public String getActkey() {
        return Actkey;
    }

    public void setActkey(String actkey) {
        Actkey = actkey;
    }

    public String getActkeytext() {
        return Actkeytext;
    }

    public void setActkeytext(String actkeytext) {
        Actkeytext = actkeytext;
    }

    public String getActinfo() {
        return Actinfo;
    }

    public void setActinfo(String actinfo) {
        Actinfo = actinfo;
    }
}

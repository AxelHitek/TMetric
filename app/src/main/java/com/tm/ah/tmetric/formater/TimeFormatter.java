package com.tm.ah.tmetric.formater;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import static java.lang.Integer.parseInt;

/**
 * Created by AH on 2/10/2018.
 */

public class TimeFormatter {

    public TimeFormatter() {
    }

    public String transformMaterialCalendarDateIntoSystemCalendarDate(CalendarDay calendarDay) {
        return calendarDay.toString().replace("CalendarDay{", "").replace("}", "");
    }

    public String formatDataBaseDayToMatchCalendarDayFormat(String dataBaseDate) {
        String[] date = dataBaseDate.split("-");
        String formatedDate = date[0] + "-" + (parseInt(date[1]) - 1) + "-" + parseInt(date[2]);
        return formatedDate;
    }

    public String formatSystemDayToMatchCalendarDayFormat(String dataBaseDate) {
        String[] date = dataBaseDate.split("-");
        String formatedDate = date[0] + "-";
        if ((parseInt(date[1]) + 1) < 10) {
            formatedDate += "0" + (parseInt(date[1]) + 1);
        } else {
            formatedDate += "" + (parseInt(date[1]) + 1);
        }
        formatedDate += "-";
        if ((parseInt(date[2])) < 10) {
            formatedDate += "0" + (parseInt(date[2]));
        } else {
            formatedDate += "" + (parseInt(date[2]));
        }
        return formatedDate;
    }
}

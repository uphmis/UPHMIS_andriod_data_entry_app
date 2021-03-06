package org.dhis2.mobile_uphmis.utils.date.filters;


import org.dhis2.mobile_uphmis.utils.date.PeriodFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public class SixMonthlyAprilPeriodFilter extends PeriodFilter {
    public SixMonthlyAprilPeriodFilter(DateTime startDate, DateTime endDate) {
        super(fixStartDate(startDate), fixEndDate(endDate));
    }

    private static DateTime fixStartDate(DateTime startDate) {
        if(startDate==null) {
            return null;
        }
        int month = startDate.getMonthOfYear();
        if (month >= 4 && month <= 9) {
            return startDate.withMonthOfYear(DateTimeConstants.APRIL).withDayOfMonth(1);
        } else if( month>9) {
            return startDate.withMonthOfYear(DateTimeConstants.OCTOBER).withDayOfMonth(1);
        } else{
            return startDate.withYear(startDate.getYear()-1).withMonthOfYear(DateTimeConstants.OCTOBER).withDayOfMonth(1);
        }
    }
    private static DateTime fixEndDate(DateTime endDate) {
        if(endDate==null) {
            return null;
        }
        int month = endDate.getMonthOfYear();
        if (month >= 4 && month <= 9) {
            return endDate.withMonthOfYear(DateTimeConstants.SEPTEMBER).withDayOfMonth(30);
        }else if( month>9) {
            return endDate.withYear(endDate.getYear() +1).withMonthOfYear(
                    DateTimeConstants.MARCH).withDayOfMonth(31);
        }else{
            return endDate.withMonthOfYear(
                    DateTimeConstants.MARCH).withDayOfMonth(31);
        }
    }

    @Override
    public boolean apply() {
        if ((startDate == null && endDate == null) || selectedDate == null) {
            return false;
        }

        if (startDate != null && endDate != null) {
            // return true, if criteria is not between two dates
            // return startDate.isBefore(selectedDate) || endDate.isAfter(selectedDate);
            return !((selectedDate.isAfter(startDate) || selectedDate.isEqual(startDate))
                    && (selectedDate.isBefore(endDate) || selectedDate.isEqual(endDate)));
        }

        if (startDate != null) {
            // return true, if criteria is before startDate
            // return startDate.isBefore(selectedDate);
            return !(selectedDate.isAfter(startDate) || selectedDate.isEqual(startDate));
        }

        // return true, if criteria is after endDate
        // return endDate.isAfter(selectedDate);
        return !(selectedDate.isBefore(endDate) || selectedDate.isEqual(endDate));
    }
}
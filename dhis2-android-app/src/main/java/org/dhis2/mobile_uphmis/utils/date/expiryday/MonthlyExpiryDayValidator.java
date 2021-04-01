package org.dhis2.mobile_uphmis.utils.date.expiryday;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class MonthlyExpiryDayValidator extends ExpiryDayValidator {
    protected static final String DATE_FORMAT = "yyyyMM";

    public MonthlyExpiryDayValidator(int expiryDays, String period) {
        super(expiryDays, period);
    }

    @Override
    protected LocalDate getMaxDateCanEdit() {
        LocalDate periodDate = LocalDate.parse(period, DateTimeFormat.forPattern(getDateFormat()));
        periodDate = periodDate.plusMonths(plusMonths());
        return periodDate.plusDays(expiryDays - 2);
    }

    protected int plusMonths() {
        return 1;
    }

    public String getDateFormat() {
        return DATE_FORMAT;
    }
}

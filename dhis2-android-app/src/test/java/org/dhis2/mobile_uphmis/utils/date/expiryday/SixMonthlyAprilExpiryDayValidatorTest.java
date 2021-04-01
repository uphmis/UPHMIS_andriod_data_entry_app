package org.dhis2.mobile_uphmis.utils.date.expiryday;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

public class SixMonthlyAprilExpiryDayValidatorTest {
    private static final String PATTERN = "yyyy'AprilS'";
    private static final int PREVIOUS_PERIOD_START_APRIL = 1;
    private static final int PREVIOUS_PERIOD_START_OCTOBER = 2;

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(
                previousPeriodStart == PREVIOUS_PERIOD_START_APRIL ? DateTimeConstants.APRIL
                        : DateTimeConstants.OCTOBER).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(6), new LocalDate()).getDays();
        SixMonthlyAprilExpiryDayValidator monthlyExpiryDayValidator =
                new SixMonthlyAprilExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN) + previousPeriodStart);
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanEditWithPeriodPreviousMonthWithTwoMoreDaysAtExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(
                previousPeriodStart == PREVIOUS_PERIOD_START_APRIL ? DateTimeConstants.APRIL
                        : DateTimeConstants.OCTOBER).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(6), new LocalDate()).getDays() + 2;
        SixMonthlyAprilExpiryDayValidator monthlyExpiryDayValidator =
                new SixMonthlyAprilExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN) + previousPeriodStart);
        assertTrue(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferencePlusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(
                previousPeriodStart == PREVIOUS_PERIOD_START_APRIL ? DateTimeConstants.APRIL
                        : DateTimeConstants.OCTOBER).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(6), new LocalDate()).getDays() + 1;
        SixMonthlyAprilExpiryDayValidator monthlyExpiryDayValidator =
                new SixMonthlyAprilExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN) + previousPeriodStart);
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    @Test
    public void testCanNotEditPreviousPeriodEndsSameTodayMinusDifferenceMinusOneExpiryDays() {
        LocalDate periodDate = new LocalDate();
        int previousPeriodStart = getPreviousPeriodStart();
        periodDate = periodDate.withMonthOfYear(
                previousPeriodStart == PREVIOUS_PERIOD_START_APRIL ? DateTimeConstants.APRIL
                        : DateTimeConstants.OCTOBER).withDayOfMonth(1);
        int expiryDays = Days.daysBetween(periodDate.plusMonths(6), new LocalDate()).getDays() - 1;
        SixMonthlyAprilExpiryDayValidator monthlyExpiryDayValidator =
                new SixMonthlyAprilExpiryDayValidator(
                        expiryDays,
                        periodDate.toString(PATTERN) + previousPeriodStart);
        assertFalse(monthlyExpiryDayValidator.canEdit());
    }

    private int getPreviousPeriodStart() {
        return new LocalDate().getMonthOfYear() >= DateTimeConstants.OCTOBER
                ? PREVIOUS_PERIOD_START_OCTOBER : PREVIOUS_PERIOD_START_APRIL;
    }
}

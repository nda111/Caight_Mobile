package com.example.caight;

import android.icu.util.Calendar;

public final class Date implements Comparable<Date>
{
    private int year;
    private short month;
    private short day;

    public Date(int year, int month, int day)
    {
        setYear(year);
        setMonth(month);
        setDay(day);
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth(int month)
    {
        this.month = (short)month;
    }

    public int getDay()
    {
        return day;
    }

    public void setDay(int day)
    {
        this.day = (short)day;
    }

    public long toLong()
    {
        long year = this.year;
        long month = this.month;
        long day = this.day;

        return (year << 32) | (month << 16) | day;
    }

    public Calendar toCalendar()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);

        return calendar;
    }

    public static Date fromBigInt(long date)
    {
        int year = (int)(date >> 32);
        short month = (short)((date >> 16) & 0xFFFF);
        short day = (short)(date & 0xFFFF);

        return new Date(year, month, day);
    }

    public static Date getToday()
    {
        return fromCalendar(Calendar.getInstance());
    }

    public static Date getPeriod(Date from, Date to)
    {
        Calendar cFrom = Calendar.getInstance();
        Calendar cTo = Calendar.getInstance();

        cFrom.set(from.year, from.month, from.day);
        cTo.set(to.year, to.month, to.day);

        Calendar period = Calendar.getInstance();
        period.setTimeInMillis(cTo.getTimeInMillis() - cFrom.getTimeInMillis());

        return fromCalendar(period);
    }

    public static Date fromCalendar(Calendar calendar)
    {
        return new Date(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Date)
        {
            Date other = (Date)obj;
            return
                            year == other.year &&
                            month == other.month &&
                            day == other.day;
        }

        return false;
    }

    @Override
    public int compareTo(Date o)
    {
        return (int)(toLong() - o.toLong());
    }
}

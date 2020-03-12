package com.example.caight;

import java.util.Calendar;

public class StringResources
{
    public static String[] NameExamples = null;
    public static String[] Species = null;

    public static class DateFormatter
    {
        private static String str;

        public static Calendar parse(String str)
        {
            DateFormatter.str = str;
            String[] part = str.split("-");
            if (part.length != 3)
            {
                return null;
            }

            int[] parsed = {
                    Integer.parseInt(part[0]),
                    Integer.parseInt(part[1]),
                    Integer.parseInt(part[2]),
            };

            Calendar result = Calendar.getInstance();
            result.set(parsed[0], parsed[1], parsed[2]);

            return result;
        }

        public static String format(Calendar date)
        {
            StringBuilder builder = new StringBuilder();

            builder.append(date.get(Calendar.YEAR));
            builder.append('-');

            int month = date.get(Calendar.MONTH);
            if (month < 10)
            {
                builder.append('0');
            }
            builder.append(month + 1);
            builder.append('-');

            int day = date.get(Calendar.DAY_OF_MONTH);
            if (day < 10)
            {
                builder.append('0');
            }
            builder.append(day);

            return builder.toString();
        }
    }
}

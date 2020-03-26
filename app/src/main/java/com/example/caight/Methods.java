package com.example.caight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.icu.util.Calendar;
import android.view.textclassifier.TextLinks;
import android.widget.Toast;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class Methods
{
    static byte[] intToByteArray(int i)
    {
        return new byte[] {
                (byte)(i),
                (byte)(i >> 8),
                (byte)(i >> 16),
                (byte)(i >> 24),
        };
    }

    static byte[] longToByteArray(long l)
    {
        return new byte[] {
                (byte)l,
                (byte)(l >> 8),
                (byte)(l >> 16),
                (byte)(l >> 24),
                (byte)(l >> 32),
                (byte)(l >> 40),
                (byte)(l >> 48),
                (byte)(l >> 56),
        };
    }

    static int byteArrayToInt(byte[] b)
    {
        return ((int)b[0]) | ((int)b[1] << 8) | ((int)b[1] << 16) | ((int)b[1] << 24);
    }

    public static int byteArrayToLong(byte[] b)
    {
        return b[0] | (b[1] << 8) | (b[2] << 16) | (b[3] << 24) | (b[4] << 32) | (b[5] << 40) | (b[6] << 48) | (b[7] << 56);
    }

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
            builder.append('.');

            int month = date.get(Calendar.MONTH);
            if (month < 10)
            {
                builder.append('0');
            }
            builder.append(month + 1);
            builder.append('.');

            int day = date.get(Calendar.DAY_OF_MONTH);
            if (day < 10)
            {
                builder.append('0');
            }
            builder.append(day);

            return builder.toString();
        }
    }

    public static String toHexId(int id)
    {
        String idString = Integer.toString(id, 16).toUpperCase();
        StringBuilder idBuilder = new StringBuilder();
        int cnt0 = 8 - idString.length();
        idBuilder.append('#');
        while (cnt0-- > 0)
        {
            idBuilder.append('0');
        }
        idBuilder.append(idString);

        return idBuilder.toString();
    }
}

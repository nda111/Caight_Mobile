package com.example.caight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.textclassifier.TextLinks;
import android.widget.Toast;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class StaticMethods
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
}

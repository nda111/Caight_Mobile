package com.example.caight;

public class StaticMethods
{
    public static byte[] intToByteArray(int i)
    {
        return new byte[]{
                (byte)(i),
                (byte)(i >> 8),
                (byte)(i >> 16),
                (byte)(i >> 24),
        };
    }

    public static int byteArrayToInt(byte[] b)
    {
        return ((int)b[0]) | ((int)b[1] << 8) | ((int)b[1] << 16) | ((int)b[1] << 24);
    }
}

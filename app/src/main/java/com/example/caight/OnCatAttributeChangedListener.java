package com.example.caight;

public interface OnCatAttributeChangedListener
{
    public static final int __ID_COLOR__ = 0;
    public static final int __ID_NAME__ = 1;
    public static final int __ID_BIRTHDAY__ = 2;
    public static final int __ID_GENDER__ = 3;
    public static final int __ID_SPECIES__ = 4;
    public static final int __ID_WEIGHTS__ = 5;

    void changed(int id, Object newValue);
}

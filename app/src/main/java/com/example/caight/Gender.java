package com.example.caight;

public enum Gender
{
    MALE(0),   // 0b000
    FEMALE(1), // 0b001
    NEUTERED(2),// 0b010
    SPAYED(3);  // 0b011

    private int value = -1;

    private Gender(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public boolean isMale()
    {
        return value % 2 == 0;
    }

    public boolean isFemale()
    {
        return value % 2 == 1;
    }

    public boolean isNeuteredOrSpayed()
    {
        return (value >> 1) == 1;
    }

    public static Gender fromValue(int value)
    {
        switch (value)
        {
        case 0b00:
            return MALE;

        case 0b01:
            return FEMALE;

        case 0b10:
            return NEUTERED;

        case 0b11:
            return SPAYED;

        default:
            return null;
        }
    }

    public static Gender evaluate(boolean isMale, boolean neuteredOrSpayed)
    {
        if (isMale)
        {
            if (neuteredOrSpayed)
            {
                return NEUTERED;
            }
            else
            {
                return MALE;
            }
        }
        else
        {
            if (neuteredOrSpayed)
            {
                return SPAYED;
            }
            else
            {
                return FEMALE;
            }
        }
    }
}

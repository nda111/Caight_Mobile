package com.example.caight;

public enum Gender
{
    MALE(false, false),
    FEMALE(true, false),
    NEUTERED(false, true),
    SPAYED(true, true);

    private boolean gender;
    private boolean neutered;

    private Gender(boolean gender, boolean neuteredOrSpayed)
    {
        this.gender = gender;
        this.neutered = neuteredOrSpayed;
    }

    public boolean isMale()
    {
        return gender;
    }
    public boolean isFemale()
    {
        return !gender;
    }
    public boolean isNeuteredOrSpayed()
    {
        return neutered;
    }

    public static Gender parse(String string)
    {
        switch (string)
        {
        case "MALE":
            return MALE;

        case "FEMALE":
            return FEMALE;

        case "NEUTERED":
            return NEUTERED;

        case "SPAYED":
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

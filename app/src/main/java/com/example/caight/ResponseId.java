package com.example.caight;

public enum ResponseId
{
    UNKNOWN(-1),

    UNKNOWN_EMAIL(0),
    REGISTERED_EMAIL(1),
    VERIFIED_EMAIL(2),

    REGISTER_OK(3),
    REGISTER_NO(4),

    VERIFY_OK_WEB_ONLY(5),
    VERIFY_NO_WEB_ONLY(6);

    private int id = -1;

    ResponseId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public static ResponseId fromId(int id)
    {
        for (ResponseId req : values())
        {
            if (req.getId() == id)
            {
                return req;
            }
        }

        return null;
    }
}

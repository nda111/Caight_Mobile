package com.example.caight;

public enum ResponseId
{
    UNKNOWN(-1),

    UNKNOWN_EMAIL(0),
    REGISTERED_EMAIL(1),
    CERTIFIED_EMAIL(2);

    private int id = -1;

    private ResponseId(int id)
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

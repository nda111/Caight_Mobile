package com.example.caight;

public enum RequestId
{
    UNKNOWN(-1),
    EVALUATE_EMAIL(0),
    REGISTER_EMAIL(1);

    private int id = -1;

    RequestId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public static RequestId fromId(int id)
    {
        for (RequestId req : values())
        {
            if (req.getId() == id)
            {
                return req;
            }
        }

        return null;
    }
}

package com.example.caight;

public enum RequestId
{
    UNKNOWN(-1),
    VALIDATE_EMAIL(0);


    private int id = -1;

    private RequestId(int id)
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

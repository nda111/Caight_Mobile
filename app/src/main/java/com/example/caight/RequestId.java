package com.example.caight;

public enum RequestId
{
    UNKNOWN(-1),

    EVALUATE_EMAIL(0),

    REGISTER_EMAIL(1),

    VERIFY_EMAIL_WEB_ONLY(2),

    SIGN_IN(3),

    NEW_GROUP(4),
    NEW_CAT(5),

    DOWNLOAD_ENTITY(6),

    CHANGE_NAME(7),

    LOGOUT(8);

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

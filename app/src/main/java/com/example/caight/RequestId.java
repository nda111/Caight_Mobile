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

    LOGOUT(8),

    REQUEST_RESET_PASSWORD_uri(9),

    RESET_PASSWORD_WEB_ONLY(10),

    RESET_PASSWORD_CONFIRM_WEB_ONLY(11),

    DELETE_ACCOUNT(12),

    JOIN_GROUP(13),

    DOWNLOAD_MEMBER(14),

    UPDATE_GROUP(15),

    DROP_GROUP(16),

    WITHDRAW_GROUP(17),

    DROP_CAT(18),

    EDIT_CAT(19),

    UPLOAD_WEIGHT(20);

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

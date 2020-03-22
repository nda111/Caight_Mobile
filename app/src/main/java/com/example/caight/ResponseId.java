package com.example.caight;

public enum ResponseId
{
    UNKNOWN(-1),

    /*
     * EVALUATE_EMAIL(0)
     */
    UNKNOWN_EMAIL(0),
    REGISTERED_EMAIL(1),
    VERIFIED_EMAIL(2),

    /*
     * REGISTER_EMAIL(1)
     */
    REGISTER_OK(3),
    REGISTER_NO(4),

    /*
     * VERIFY_EMAIL_WEB_ONLY(2)
     */
    VERIFY_OK_WEB_ONLY(5),
    VERIFY_NO_WEB_ONLY(6),

    /*
     * SIGN_IN(3)
     */
    SIGN_IN_OK(7),
    SIGN_IN_WRONG_PW(8),
    SING_IN_ERROR(9),

    /*
     * NEW_GROUP(4)
     * NEW_CAT(5)
     */
    ADD_ENTITY_OK(10),
    ADD_ENTITY_NO(11),
    ADD_ENTITY_NOT_PW(12),
    ADD_ENTITY_ERROR(13),

    /*
     * DOWNLOAD_ENTITY(6)
     */
    ENTITY_GROUP(14),
    ENTITY_CAT(15),
    END_OF_ENTITY(16),
    DOWNLOAD_REJECTED(17),

    /*
     * CHANGE_NAME(7)
     */
    CHANGE_NAME_OK(18),
    CHANGE_NAME_NO(19);

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

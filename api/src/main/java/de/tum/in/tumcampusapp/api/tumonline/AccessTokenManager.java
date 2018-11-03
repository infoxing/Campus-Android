package de.tum.in.tumcampusapp.api.tumonline;

import android.content.Context;

import de.tum.in.tumcampusapp.core.Const;
import de.tum.in.tumcampusapp.core.Utils;

/**
 * Easy accessible class for token management.
 */
public class AccessTokenManager {

    /**
     * Returns whether a valid access token already exists.
     *
     * @return Whether access token is set
     */
    public static boolean hasValidAccessToken(Context context) {
        final String oldAccessToken = Utils.getSetting(context, Const.ACCESS_TOKEN, "");
        return oldAccessToken.length() > 2;
    }

}

package volley.app.c_master.loc_service;

import android.content.Context;
import android.content.SharedPreferences;

class LockedState {

    private static final String PREF_KEY_LAST_STATUS = "lo_state_prefs_key_1";

    private final SharedPreferences preferences;

    LockedState(Context context) {
        this.preferences = context.getSharedPreferences("lo_state_prefs", Context.MODE_PRIVATE);
    }

    boolean isNeedToUpdateState(boolean lockedState) {
        int lockedStateKey = preferences.getInt(PREF_KEY_LAST_STATUS, -1);
        if (lockedStateKey == -1) {
            return true;
        }
        boolean lastLockedState = lockedStateKey == 1;
        return lastLockedState != lockedState;
    }

    void saveState(boolean lockedState) {
        preferences.edit()
                .putInt(PREF_KEY_LAST_STATUS, lockedState ? 1 : 0)
                .apply();
    }
}

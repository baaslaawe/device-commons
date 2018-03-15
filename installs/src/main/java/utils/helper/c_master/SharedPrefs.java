package utils.helper.c_master;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import utils.helper.c_master.models.ApkInfoModel;

public class SharedPrefs {

    private static final String TARGET_KEY = "target_key";

    private SharedPreferences preferences;
    private Gson gson;

    public SharedPrefs(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
    }

    public void saveTargetApkInfo(@Nullable ApkInfoModel model) {
        if (model == null) {
            preferences.edit().remove(TARGET_KEY).apply();
            return;
        }
        String json = gson.toJson(model);
        preferences.edit().putString(TARGET_KEY, json)
                .apply();
    }

    @Nullable
    public ApkInfoModel getTargetApkInfo() {
        String json = preferences.getString(TARGET_KEY, null);
        if (json != null) {
            return gson.fromJson(json, ApkInfoModel.class);
        }
        return null;
    }
}

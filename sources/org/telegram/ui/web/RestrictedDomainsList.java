package org.telegram.ui.web;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;

public class RestrictedDomainsList {
    private static RestrictedDomainsList instance;
    private boolean loaded;
    public final HashMap openedDomains = new HashMap();
    public final HashSet restrictedDomainsSet = new HashSet();
    public final ArrayList restrictedDomains = new ArrayList();

    public static RestrictedDomainsList getInstance() {
        if (instance == null) {
            instance = new RestrictedDomainsList();
        }
        return instance;
    }

    public int incrementOpen(String str) {
        load();
        Integer num = (Integer) this.openedDomains.get(str);
        if (num == null) {
            num = 0;
        }
        int intValue = num.intValue() + 1;
        this.openedDomains.put(str, Integer.valueOf(intValue));
        scheduleSave();
        return intValue;
    }

    public boolean isRestricted(String str) {
        load();
        return this.restrictedDomainsSet.contains(str);
    }

    public boolean isRestricted(String... strArr) {
        load();
        for (String str : strArr) {
            if (this.restrictedDomainsSet.contains(str)) {
                return true;
            }
        }
        return false;
    }

    public void load() {
        if (this.loaded) {
            return;
        }
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        try {
            JSONObject jSONObject = new JSONObject(globalMainSettings.getString("web_opened_domains", "{}"));
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                this.openedDomains.put(next, Integer.valueOf(jSONObject.getInt(next)));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            JSONArray jSONArray = new JSONArray(globalMainSettings.getString("web_restricted_domains2", "[]"));
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONArray jSONArray2 = jSONArray.getJSONArray(i);
                ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                    String string = jSONArray2.getString(i2);
                    this.restrictedDomainsSet.add(string);
                    arrayList.add(string);
                }
                this.restrictedDomains.add(arrayList);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        this.loaded = true;
    }

    public void save() {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        try {
            JSONObject jSONObject = new JSONObject();
            for (Map.Entry entry : this.openedDomains.entrySet()) {
                jSONObject.put((String) entry.getKey(), entry.getValue());
            }
            edit.putString("web_opened_domains", jSONObject.toString());
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            JSONArray jSONArray = new JSONArray();
            Iterator it = this.restrictedDomains.iterator();
            while (it.hasNext()) {
                ArrayList arrayList = (ArrayList) it.next();
                JSONArray jSONArray2 = new JSONArray();
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    jSONArray2.put((String) it2.next());
                }
                jSONArray.put(jSONArray2);
            }
            edit.putString("web_restricted_domains2", jSONArray.toString());
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        edit.apply();
    }

    public void scheduleSave() {
        AndroidUtilities.cancelRunOnUIThread(new Runnable() {
            @Override
            public final void run() {
                RestrictedDomainsList.this.save();
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                RestrictedDomainsList.this.save();
            }
        }, 1000L);
    }

    public void setRestricted(boolean z, String... strArr) {
        load();
        int i = -1;
        for (int i2 = 0; i2 < this.restrictedDomains.size(); i2++) {
            int i3 = 0;
            while (true) {
                if (i3 >= strArr.length) {
                    break;
                }
                if (strArr[i3] != null && ((ArrayList) this.restrictedDomains.get(i2)).contains(strArr[i3])) {
                    i = i2;
                    break;
                }
                i3++;
            }
            if (i >= 0) {
                break;
            }
        }
        if (z != isRestricted(strArr)) {
            if (z) {
                ArrayList arrayList = new ArrayList();
                for (String str : strArr) {
                    if (str != null) {
                        arrayList.add(str);
                    }
                }
                this.restrictedDomainsSet.addAll(arrayList);
                this.restrictedDomains.add(arrayList);
            } else {
                this.restrictedDomainsSet.removeAll((Collection) this.restrictedDomains.remove(i));
            }
            scheduleSave();
        }
    }
}

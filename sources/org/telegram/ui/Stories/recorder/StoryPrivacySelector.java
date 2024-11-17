package org.telegram.ui.Stories.recorder;

import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet;

public abstract class StoryPrivacySelector extends View {
    public static void applySaved(int i, StoryEntry storyEntry) {
        if (storyEntry == null) {
            return;
        }
        storyEntry.privacy = getSaved(i);
        storyEntry.privacyRules.clear();
        storyEntry.privacyRules.addAll(storyEntry.privacy.rules);
        if (UserConfig.getInstance(i).isPremium()) {
            storyEntry.period = MessagesController.getInstance(i).getMainSettings().getInt("story_period", 86400);
        } else {
            storyEntry.period = 86400;
        }
    }

    private static StoryPrivacyBottomSheet.StoryPrivacy getSaved(final int i) {
        try {
            String string = MessagesController.getInstance(i).getMainSettings().getString("story_privacy2", null);
            if (string == null) {
                return new StoryPrivacyBottomSheet.StoryPrivacy();
            }
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
            StoryPrivacyBottomSheet.StoryPrivacy read = read(serializedData);
            serializedData.cleanup();
            if (read.isNone()) {
                return new StoryPrivacyBottomSheet.StoryPrivacy();
            }
            final HashSet hashSet = new HashSet();
            hashSet.addAll(read.selectedUserIds);
            Iterator it = read.selectedUserIdsByGroup.values().iterator();
            while (it.hasNext()) {
                hashSet.addAll((ArrayList) it.next());
            }
            if (!hashSet.isEmpty()) {
                final MessagesStorage messagesStorage = MessagesStorage.getInstance(i);
                messagesStorage.getStorageQueue().postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        StoryPrivacySelector.lambda$getSaved$5(MessagesStorage.this, hashSet, i);
                    }
                });
            }
            return read;
        } catch (Exception e) {
            FileLog.e(e);
            return new StoryPrivacyBottomSheet.StoryPrivacy();
        }
    }

    public static void lambda$getSaved$4(int i, ArrayList arrayList) {
        MessagesController.getInstance(i).putUsers(arrayList, true);
    }

    public static void lambda$getSaved$5(MessagesStorage messagesStorage, HashSet hashSet, final int i) {
        final ArrayList<TLRPC.User> users = messagesStorage.getUsers(new ArrayList<>(hashSet));
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StoryPrivacySelector.lambda$getSaved$4(i, users);
            }
        });
    }

    private static StoryPrivacyBottomSheet.StoryPrivacy read(AbstractSerializedData abstractSerializedData) {
        int readInt32 = abstractSerializedData.readInt32(true);
        if (abstractSerializedData.readInt32(true) != 481674261) {
            throw new RuntimeException("wrong Vector magic in TL_StoryPrivacy");
        }
        int readInt322 = abstractSerializedData.readInt32(true);
        ArrayList arrayList = new ArrayList(readInt322);
        for (int i = 0; i < readInt322; i++) {
            arrayList.add(TLRPC.InputUser.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(true), true));
        }
        if (abstractSerializedData.readInt32(true) != 481674261) {
            throw new RuntimeException("wrong Vector magic in TL_StoryPrivacy (2)");
        }
        int readInt323 = abstractSerializedData.readInt32(true);
        ArrayList arrayList2 = new ArrayList(readInt323);
        for (int i2 = 0; i2 < readInt323; i2++) {
            arrayList2.add(Long.valueOf(abstractSerializedData.readInt64(true)));
        }
        if (abstractSerializedData.readInt32(true) != 481674261) {
            throw new RuntimeException("wrong Vector magic in TL_StoryPrivacy (3)");
        }
        int readInt324 = abstractSerializedData.readInt32(true);
        HashMap hashMap = new HashMap();
        for (int i3 = 0; i3 < readInt324; i3++) {
            long readInt64 = abstractSerializedData.readInt64(true);
            if (abstractSerializedData.readInt32(true) != 481674261) {
                throw new RuntimeException("wrong Vector magic in TL_StoryPrivacy (4)");
            }
            int readInt325 = abstractSerializedData.readInt32(true);
            ArrayList arrayList3 = new ArrayList(readInt325);
            for (int i4 = 0; i4 < readInt325; i4++) {
                arrayList3.add(Long.valueOf(abstractSerializedData.readInt64(true)));
            }
            hashMap.put(Long.valueOf(readInt64), arrayList3);
        }
        HashSet hashSet = new HashSet();
        hashSet.addAll(arrayList2);
        Iterator it = hashMap.values().iterator();
        while (it.hasNext()) {
            hashSet.addAll((ArrayList) it.next());
        }
        StoryPrivacyBottomSheet.StoryPrivacy storyPrivacy = new StoryPrivacyBottomSheet.StoryPrivacy(readInt32, arrayList, 0);
        storyPrivacy.selectedUserIds.clear();
        storyPrivacy.selectedUserIds.addAll(arrayList2);
        storyPrivacy.selectedUserIdsByGroup.clear();
        storyPrivacy.selectedUserIdsByGroup.putAll(hashMap);
        return storyPrivacy;
    }

    public static void save(int i, StoryPrivacyBottomSheet.StoryPrivacy storyPrivacy) {
        if (storyPrivacy == null) {
            MessagesController.getInstance(i).getMainSettings().edit().remove("story_privacy2").apply();
            return;
        }
        SerializedData serializedData = new SerializedData(true);
        write(serializedData, storyPrivacy);
        SerializedData serializedData2 = new SerializedData(serializedData.length());
        serializedData.cleanup();
        write(serializedData2, storyPrivacy);
        MessagesController.getInstance(i).getMainSettings().edit().putString("story_privacy2", Utilities.bytesToHex(serializedData2.toByteArray())).apply();
        serializedData2.cleanup();
    }

    private static void write(AbstractSerializedData abstractSerializedData, StoryPrivacyBottomSheet.StoryPrivacy storyPrivacy) {
        abstractSerializedData.writeInt32(storyPrivacy.type);
        abstractSerializedData.writeInt32(481674261);
        abstractSerializedData.writeInt32(storyPrivacy.selectedInputUsers.size());
        Iterator it = storyPrivacy.selectedInputUsers.iterator();
        while (it.hasNext()) {
            ((TLRPC.InputUser) it.next()).serializeToStream(abstractSerializedData);
        }
        abstractSerializedData.writeInt32(481674261);
        abstractSerializedData.writeInt32(storyPrivacy.selectedUserIds.size());
        Iterator it2 = storyPrivacy.selectedUserIds.iterator();
        while (it2.hasNext()) {
            abstractSerializedData.writeInt64(((Long) it2.next()).longValue());
        }
        abstractSerializedData.writeInt32(481674261);
        abstractSerializedData.writeInt32(storyPrivacy.selectedUserIdsByGroup.size());
        for (Map.Entry entry : storyPrivacy.selectedUserIdsByGroup.entrySet()) {
            abstractSerializedData.writeInt64(((Long) entry.getKey()).longValue());
            abstractSerializedData.writeInt32(481674261);
            abstractSerializedData.writeInt32(((ArrayList) entry.getValue()).size());
            Iterator it3 = ((ArrayList) entry.getValue()).iterator();
            while (it3.hasNext()) {
                abstractSerializedData.writeInt64(((Long) it3.next()).longValue());
            }
        }
    }
}

package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.VoIPPreNotificationService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPPermissionActivity extends Activity {
    @Override
    protected void onCreate(Bundle bundle) {
        boolean isVideo;
        int checkSelfPermission;
        int checkSelfPermission2;
        super.onCreate(bundle);
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            TLRPC.PhoneCall phoneCall = sharedInstance.privateCall;
            isVideo = phoneCall != null && phoneCall.video;
        } else {
            isVideo = VoIPPreNotificationService.isVideo();
        }
        ArrayList arrayList = new ArrayList();
        checkSelfPermission = checkSelfPermission("android.permission.RECORD_AUDIO");
        if (checkSelfPermission != 0) {
            arrayList.add("android.permission.RECORD_AUDIO");
        }
        if (isVideo) {
            checkSelfPermission2 = checkSelfPermission("android.permission.CAMERA");
            if (checkSelfPermission2 != 0) {
                arrayList.add("android.permission.CAMERA");
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        try {
            requestPermissions((String[]) arrayList.toArray(new String[0]), isVideo ? 102 : 101);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        boolean shouldShowRequestPermissionRationale;
        if (i == 101 || i == 102) {
            boolean z = false;
            int i2 = 0;
            while (true) {
                if (i2 >= iArr.length) {
                    z = true;
                    break;
                } else if (iArr[i2] != 0) {
                    break;
                } else {
                    i2++;
                }
            }
            if (iArr.length > 0 && z) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().acceptIncomingCall();
                } else {
                    VoIPPreNotificationService.answer(this);
                }
                finish();
                startActivity(new Intent(this, (Class<?>) LaunchActivity.class).setAction("voip"));
                return;
            }
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO");
            if (shouldShowRequestPermissionRationale) {
                finish();
                return;
            }
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().declineIncomingCall();
            } else {
                VoIPPreNotificationService.decline(this, 1);
            }
            VoIPHelper.permissionDenied(this, new Runnable() {
                @Override
                public final void run() {
                    VoIPPermissionActivity.this.finish();
                }
            }, i);
        }
    }
}

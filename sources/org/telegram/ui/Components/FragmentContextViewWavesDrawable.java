package org.telegram.ui.Components;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class FragmentContextViewWavesDrawable {
    private float amplitude;
    private float amplitude2;
    private float animateAmplitudeDiff;
    private float animateAmplitudeDiff2;
    private float animateToAmplitude;
    WeavingState currentState;
    private long lastUpdateTime;
    WeavingState pausedState;
    WeavingState previousState;
    WeavingState[] states = new WeavingState[4];
    float progressToState = 1.0f;
    ArrayList parents = new ArrayList();
    Paint paint = new Paint(1);
    LineBlobDrawable lineBlobDrawable = new LineBlobDrawable(5);
    LineBlobDrawable lineBlobDrawable1 = new LineBlobDrawable(7);
    LineBlobDrawable lineBlobDrawable2 = new LineBlobDrawable(8);
    RectF rect = new RectF();
    Path path = new Path();
    private final Paint selectedPaint = new Paint(1);

    public static class WeavingState {
        int color1;
        int color2;
        int color3;
        private final int currentState;
        private float duration;
        public Shader shader;
        private float startX;
        private float startY;
        private float time;
        private float targetX = -1.0f;
        private float targetY = -1.0f;
        private final Matrix matrix = new Matrix();
        int greenKey1 = Theme.key_voipgroup_topPanelGreen1;
        int greenKey2 = Theme.key_voipgroup_topPanelGreen2;
        int blueKey1 = Theme.key_voipgroup_topPanelBlue1;
        int blueKey2 = Theme.key_voipgroup_topPanelBlue2;
        int mutedByAdmin = Theme.key_voipgroup_mutedByAdminGradient;
        int mutedByAdmin2 = Theme.key_voipgroup_mutedByAdminGradient2;
        int mutedByAdmin3 = Theme.key_voipgroup_mutedByAdminGradient3;

        public WeavingState(int i) {
            this.currentState = i;
            createGradients();
        }

        private void createGradients() {
            RadialGradient radialGradient;
            int i = this.currentState;
            if (i == 0) {
                int color = Theme.getColor(this.greenKey1);
                this.color1 = color;
                int color2 = Theme.getColor(this.greenKey2);
                this.color2 = color2;
                radialGradient = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color, color2}, (float[]) null, Shader.TileMode.CLAMP);
            } else {
                if (i != 1) {
                    if (i == 3) {
                        int color3 = Theme.getColor(this.mutedByAdmin);
                        this.color1 = color3;
                        int color4 = Theme.getColor(this.mutedByAdmin3);
                        this.color3 = color4;
                        int color5 = Theme.getColor(this.mutedByAdmin2);
                        this.color2 = color5;
                        this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color3, color4, color5}, new float[]{0.0f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
                        return;
                    }
                    return;
                }
                int color6 = Theme.getColor(this.blueKey1);
                this.color1 = color6;
                int color7 = Theme.getColor(this.blueKey2);
                this.color2 = color7;
                radialGradient = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color6, color7}, (float[]) null, Shader.TileMode.CLAMP);
            }
            this.shader = radialGradient;
        }

        public void checkColor() {
            int i = this.currentState;
            if (i == 0) {
                if (this.color1 == Theme.getColor(this.greenKey1) && this.color2 == Theme.getColor(this.greenKey2)) {
                    return;
                }
            } else if (i == 1) {
                if (this.color1 == Theme.getColor(this.blueKey1) && this.color2 == Theme.getColor(this.blueKey2)) {
                    return;
                }
            } else {
                if (i != 3) {
                    return;
                }
                if (this.color1 == Theme.getColor(this.mutedByAdmin) && this.color2 == Theme.getColor(this.mutedByAdmin2)) {
                    return;
                }
            }
            createGradients();
        }

        public void setToPaint(Paint paint) {
            int i;
            int i2;
            int blendARGB;
            int i3 = this.currentState;
            if (i3 != 0 && i3 != 1 && i3 != 3) {
                paint.setShader(null);
                blendARGB = Theme.getColor(Theme.key_voipgroup_topPanelGray);
            } else {
                if (LiteMode.isEnabled(512)) {
                    paint.setShader(this.shader);
                    return;
                }
                paint.setShader(null);
                if (this.currentState == 3) {
                    i = ColorUtils.blendARGB(this.color1, this.color2, 0.5f);
                    i2 = this.color3;
                } else {
                    i = this.color1;
                    i2 = this.color2;
                }
                blendARGB = ColorUtils.blendARGB(i, i2, 0.5f);
            }
            paint.setColor(blendARGB);
        }

        public void update(int i, int i2, long j, float f) {
            float nextInt;
            float nextInt2;
            float nextInt3;
            float nextInt4;
            if (this.currentState == 2) {
                return;
            }
            float f2 = this.duration;
            if (f2 == 0.0f || this.time >= f2) {
                this.duration = Utilities.random.nextInt(700) + 500;
                this.time = 0.0f;
                if (this.targetX == -1.0f) {
                    int i3 = this.currentState;
                    if (i3 == 3) {
                        this.targetX = ((Utilities.random.nextInt(100) * 0.05f) / 100.0f) - 0.3f;
                        nextInt4 = Utilities.random.nextInt(100) * 0.05f;
                    } else if (i3 == 0) {
                        this.targetX = ((Utilities.random.nextInt(100) * 0.2f) / 100.0f) - 0.3f;
                        nextInt4 = Utilities.random.nextInt(100) * 0.3f;
                    } else {
                        this.targetX = ((Utilities.random.nextInt(100) / 100.0f) * 0.2f) + 1.1f;
                        nextInt3 = (Utilities.random.nextInt(100) * 4.0f) / 100.0f;
                        this.targetY = nextInt3;
                    }
                    nextInt3 = (nextInt4 / 100.0f) + 0.7f;
                    this.targetY = nextInt3;
                }
                this.startX = this.targetX;
                this.startY = this.targetY;
                int i4 = this.currentState;
                if (i4 == 3) {
                    this.targetX = ((Utilities.random.nextInt(100) * 0.05f) / 100.0f) - 0.3f;
                    nextInt2 = Utilities.random.nextInt(100) * 0.05f;
                } else if (i4 == 0) {
                    this.targetX = ((Utilities.random.nextInt(100) * 0.2f) / 100.0f) - 0.3f;
                    nextInt2 = Utilities.random.nextInt(100) * 0.3f;
                } else {
                    this.targetX = ((Utilities.random.nextInt(100) / 100.0f) * 0.2f) + 1.1f;
                    nextInt = (Utilities.random.nextInt(100) * 4.0f) / 100.0f;
                    this.targetY = nextInt;
                }
                nextInt = (nextInt2 / 100.0f) + 0.7f;
                this.targetY = nextInt;
            }
            float f3 = (float) j;
            float f4 = this.time + ((BlobDrawable.GRADIENT_SPEED_MIN + 0.5f) * f3) + (f3 * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * f);
            this.time = f4;
            float f5 = this.duration;
            if (f4 > f5) {
                this.time = f5;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / f5);
            float f6 = i2;
            float f7 = this.startX;
            float f8 = ((f7 + ((this.targetX - f7) * interpolation)) * f6) - 200.0f;
            float f9 = this.startY;
            float f10 = (i * (f9 + ((this.targetY - f9) * interpolation))) - 200.0f;
            float f11 = f6 / 400.0f;
            int i5 = this.currentState;
            float f12 = f11 * ((i5 == 0 || i5 == 3) ? 3.0f : 1.5f);
            this.matrix.reset();
            this.matrix.postTranslate(f8, f10);
            this.matrix.postScale(f12, f12, f8 + 200.0f, f10 + 200.0f);
            this.shader.setLocalMatrix(this.matrix);
        }
    }

    public FragmentContextViewWavesDrawable() {
        for (int i = 0; i < 4; i++) {
            this.states[i] = new WeavingState(i);
        }
    }

    private void checkColors() {
        int i = 0;
        while (true) {
            WeavingState[] weavingStateArr = this.states;
            if (i >= weavingStateArr.length) {
                return;
            }
            weavingStateArr[i].checkColor();
            i++;
        }
    }

    private void setState(int i, boolean z) {
        WeavingState weavingState = this.currentState;
        if (weavingState == null || weavingState.currentState != i) {
            if (VoIPService.getSharedInstance() == null && this.currentState == null) {
                this.currentState = this.pausedState;
                return;
            }
            WeavingState weavingState2 = z ? this.currentState : null;
            this.previousState = weavingState2;
            this.currentState = this.states[i];
            this.progressToState = weavingState2 != null ? 0.0f : 1.0f;
        }
    }

    public void addParent(View view) {
        if (this.parents.contains(view)) {
            return;
        }
        this.parents.add(view);
    }

    public void draw(float r21, float r22, float r23, float r24, android.graphics.Canvas r25, org.telegram.ui.Components.FragmentContextView r26, float r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextViewWavesDrawable.draw(float, float, float, float, android.graphics.Canvas, org.telegram.ui.Components.FragmentContextView, float):void");
    }

    public void removeParent(View view) {
        this.parents.remove(view);
        if (this.parents.isEmpty()) {
            this.pausedState = this.currentState;
            this.currentState = null;
            this.previousState = null;
        }
    }

    public void setAmplitude(float f) {
        this.animateToAmplitude = f;
        float f2 = f - this.amplitude;
        this.animateAmplitudeDiff = f2 / 250.0f;
        this.animateAmplitudeDiff2 = f2 / 120.0f;
    }

    public void updateState(boolean z) {
        int i;
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            int callState = sharedInstance.getCallState();
            if (!sharedInstance.isSwitchingStream() && (callState == 1 || callState == 2 || callState == 6 || callState == 5)) {
                setState(2, z);
                return;
            }
            ChatObject.Call call = sharedInstance.groupCall;
            if (call == null || (((tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) call.participants.get(sharedInstance.getSelfId())) == null || tL_groupCallParticipant.can_self_unmute || !tL_groupCallParticipant.muted || ChatObject.canManageCalls(sharedInstance.getChat())) && !sharedInstance.groupCall.call.rtmp_stream)) {
                i = sharedInstance.isMicMute();
            } else {
                sharedInstance.setMicMute(true, false, false);
                i = 3;
            }
            setState(i, z);
        }
    }
}

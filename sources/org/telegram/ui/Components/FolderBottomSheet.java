package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.FolderBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.FiltersSetupActivity;

public class FolderBottomSheet extends BottomSheetWithRecyclerListView {
    private int alreadyHeaderRow;
    private ArrayList alreadyJoined;
    private ArrayList alreadyPeers;
    private int alreadySectionRow;
    private int alreadyUsersEndRow;
    private int alreadyUsersStartRow;
    private FrameLayout bulletinContainer;
    private Button button;
    private View buttonShadow;
    private boolean deleting;
    private CharSequence escapedTitle;
    private int filterId;
    private HeaderCell headerCell;
    private int headerRow;
    private TL_chatlists.chatlist_ChatlistInvite invite;
    private long lastClicked;
    private long lastClickedDialogId;
    private Utilities.Callback onDone;
    private ArrayList peers;
    private int reqId;
    private int rowsCount;
    private int sectionRow;
    private ArrayList selectedPeers;
    private int shiftDp;
    private String slug;
    private boolean success;
    private CharSequence title;
    private TitleCell titleCell;
    private ArrayList titleEntities;
    private boolean titleNoanimate;
    private int titleRow;
    private TL_chatlists.TL_chatlists_chatlistUpdates updates;
    private int usersEndRow;
    private int usersSectionRow;
    private int usersStartRow;

    public static class Button extends FrameLayout {
        private ShapeDrawable background;
        float countAlpha;
        AnimatedFloat countAlphaAnimated;
        private ValueAnimator countAnimator;
        private float countScale;
        AnimatedTextView.AnimatedTextDrawable countText;
        private boolean enabled;
        private ValueAnimator enabledAnimator;
        private float enabledT;
        private int lastCount;
        private boolean loading;
        private ValueAnimator loadingAnimator;
        private CircularProgressDrawable loadingDrawable;
        private float loadingT;
        Paint paint;
        private View rippleView;
        AnimatedTextView.AnimatedTextDrawable text;

        public Button(Context context, String str) {
            super(context);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.countAlphaAnimated = new AnimatedFloat(350L, cubicBezierInterpolator);
            this.loadingT = 0.0f;
            this.countScale = 1.0f;
            this.enabledT = 1.0f;
            this.enabled = true;
            View view = new View(context);
            this.rippleView = view;
            int i = Theme.key_featuredStickers_addButton;
            view.setBackground(Theme.AdaptiveRipple.rect(Theme.getColor(i), 8.0f));
            addView(this.rippleView, LayoutHelper.createFrame(-1, -1.0f));
            ShapeDrawable createRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(8.0f), Theme.getColor(i));
            this.background = createRoundRectDrawable;
            setBackground(createRoundRectDrawable);
            Paint paint = new Paint(1);
            this.paint = paint;
            int i2 = Theme.key_featuredStickers_buttonText;
            paint.setColor(Theme.getColor(i2));
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, false);
            this.text = animatedTextDrawable;
            animatedTextDrawable.setAnimationProperties(0.3f, 0L, 250L, cubicBezierInterpolator);
            this.text.setCallback(this);
            this.text.setTextSize(AndroidUtilities.dp(14.0f));
            this.text.setTypeface(AndroidUtilities.bold());
            this.text.setTextColor(Theme.getColor(i2));
            this.text.setText(str);
            this.text.setGravity(1);
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = new AnimatedTextView.AnimatedTextDrawable(false, false, true);
            this.countText = animatedTextDrawable2;
            animatedTextDrawable2.setAnimationProperties(0.3f, 0L, 250L, cubicBezierInterpolator);
            this.countText.setCallback(this);
            this.countText.setTextSize(AndroidUtilities.dp(12.0f));
            this.countText.setTypeface(AndroidUtilities.bold());
            this.countText.setTextColor(Theme.getColor(i));
            this.countText.setText("");
            this.countText.setGravity(1);
            setWillNotDraw(false);
        }

        private void animateCount() {
            ValueAnimator valueAnimator = this.countAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.countAnimator = null;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.countAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    FolderBottomSheet.Button.this.lambda$animateCount$1(valueAnimator2);
                }
            });
            this.countAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    Button.this.countScale = 1.0f;
                    Button.this.invalidate();
                }
            });
            this.countAnimator.setInterpolator(new OvershootInterpolator(2.0f));
            this.countAnimator.setDuration(200L);
            this.countAnimator.start();
        }

        public void lambda$animateCount$1(ValueAnimator valueAnimator) {
            this.countScale = Math.max(1.0f, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            invalidate();
        }

        public void lambda$setEnabled$2(ValueAnimator valueAnimator) {
            this.enabledT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        public void lambda$setLoading$0(ValueAnimator valueAnimator) {
            this.loadingT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            return false;
        }

        public TextPaint getTextPaint() {
            return this.text.getPaint();
        }

        public boolean isLoading() {
            return this.loading;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            this.rippleView.draw(canvas);
            boolean z = false;
            if (this.loadingT > 0.0f) {
                if (this.loadingDrawable == null) {
                    this.loadingDrawable = new CircularProgressDrawable(this.text.getTextColor());
                }
                int dp = (int) ((1.0f - this.loadingT) * AndroidUtilities.dp(24.0f));
                this.loadingDrawable.setBounds(0, dp, getWidth(), getHeight() + dp);
                this.loadingDrawable.setAlpha((int) (this.loadingT * 255.0f));
                this.loadingDrawable.draw(canvas);
                invalidate();
            }
            float f = this.loadingT;
            if (f < 1.0f) {
                if (f != 0.0f) {
                    canvas.save();
                    canvas.translate(0.0f, (int) (this.loadingT * AndroidUtilities.dp(-24.0f)));
                    canvas.scale(1.0f, 1.0f - (this.loadingT * 0.4f));
                    z = true;
                }
                float currentWidth = this.text.getCurrentWidth();
                float f2 = this.countAlphaAnimated.set(this.countAlpha);
                float dp2 = ((AndroidUtilities.dp(15.66f) + this.countText.getCurrentWidth()) * f2) + currentWidth;
                android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                rect.set((int) (((getMeasuredWidth() - dp2) - getWidth()) / 2.0f), (int) (((getMeasuredHeight() - this.text.getHeight()) / 2.0f) - AndroidUtilities.dp(1.0f)), (int) ((((getMeasuredWidth() - dp2) + getWidth()) / 2.0f) + currentWidth), (int) (((getMeasuredHeight() + this.text.getHeight()) / 2.0f) - AndroidUtilities.dp(1.0f)));
                this.text.setAlpha((int) ((1.0f - this.loadingT) * 255.0f * AndroidUtilities.lerp(0.5f, 1.0f, this.enabledT)));
                this.text.setBounds(rect);
                this.text.draw(canvas);
                rect.set((int) (((getMeasuredWidth() - dp2) / 2.0f) + currentWidth + AndroidUtilities.dp(5.0f)), (int) ((getMeasuredHeight() - AndroidUtilities.dp(18.0f)) / 2.0f), (int) (((getMeasuredWidth() - dp2) / 2.0f) + currentWidth + AndroidUtilities.dp(13.0f) + Math.max(AndroidUtilities.dp(9.0f), this.countText.getCurrentWidth())), (int) ((getMeasuredHeight() + AndroidUtilities.dp(18.0f)) / 2.0f));
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(rect);
                if (this.countScale != 1.0f) {
                    canvas.save();
                    float f3 = this.countScale;
                    canvas.scale(f3, f3, rect.centerX(), rect.centerY());
                }
                this.paint.setAlpha((int) ((1.0f - this.loadingT) * 255.0f * f2 * f2));
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), this.paint);
                rect.offset(-AndroidUtilities.dp(0.3f), -AndroidUtilities.dp(0.4f));
                this.countText.setAlpha((int) ((1.0f - this.loadingT) * 255.0f * f2));
                this.countText.setBounds(rect);
                this.countText.draw(canvas);
                if (this.countScale != 1.0f) {
                    canvas.restore();
                }
                if (z) {
                    canvas.restore();
                }
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            String str;
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.widget.Button");
            StringBuilder sb = new StringBuilder();
            sb.append((Object) this.text.getText());
            if (this.lastCount > 0) {
                str = ", " + LocaleController.formatPluralString("Chats", this.lastCount, new Object[0]);
            } else {
                str = "";
            }
            sb.append(str);
            accessibilityNodeInfo.setContentDescription(sb.toString());
        }

        public void setCount(int i, boolean z) {
            int i2;
            if (z) {
                this.countText.cancelAnimation();
            }
            if (z && i != (i2 = this.lastCount) && i > 0 && i2 > 0) {
                animateCount();
            }
            this.lastCount = i;
            this.countAlpha = i != 0 ? 1.0f : 0.0f;
            this.countText.setText("" + i, z);
            invalidate();
        }

        public void setEmojiCacheType(int i) {
            this.text.setEmojiCacheType(i);
        }

        @Override
        public void setEnabled(boolean z) {
            if (this.enabled != z) {
                ValueAnimator valueAnimator = this.enabledAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.enabledAnimator = null;
                }
                float f = this.enabledT;
                this.enabled = z;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(f, z ? 1.0f : 0.0f);
                this.enabledAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        FolderBottomSheet.Button.this.lambda$setEnabled$2(valueAnimator2);
                    }
                });
                this.enabledAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                    }
                });
                this.enabledAnimator.start();
            }
        }

        public void setLoading(final boolean z) {
            if (this.loading != z) {
                ValueAnimator valueAnimator = this.loadingAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.loadingAnimator = null;
                }
                float f = this.loadingT;
                this.loading = z;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(f, z ? 1.0f : 0.0f);
                this.loadingAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        FolderBottomSheet.Button.this.lambda$setLoading$0(valueAnimator2);
                    }
                });
                this.loadingAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        Button.this.loadingT = z ? 1.0f : 0.0f;
                        Button.this.invalidate();
                    }
                });
                this.loadingAnimator.setDuration(320L);
                this.loadingAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.loadingAnimator.start();
            }
        }

        public void setText(CharSequence charSequence, boolean z) {
            if (z) {
                this.text.cancelAnimation();
            }
            this.text.setText(charSequence, z);
            invalidate();
        }

        @Override
        protected boolean verifyDrawable(Drawable drawable) {
            return this.text == drawable || this.countText == drawable || super.verifyDrawable(drawable);
        }
    }

    public static class HeaderCell extends FrameLayout {
        public AnimatedTextView actionTextView;
        public AnimatedTextView textView;

        public HeaderCell(Context context) {
            super(context);
            AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, false);
            this.textView = animatedTextView;
            animatedTextView.setTextSize(AndroidUtilities.dp(15.0f));
            this.textView.setTypeface(AndroidUtilities.bold());
            AnimatedTextView animatedTextView2 = this.textView;
            int i = Theme.key_windowBackgroundWhiteBlueHeader;
            animatedTextView2.setTextColor(Theme.getColor(i));
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.textView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 80, 21.0f, 15.0f, 21.0f, 2.0f));
            AnimatedTextView animatedTextView3 = new AnimatedTextView(context, true, true, true);
            this.actionTextView = animatedTextView3;
            animatedTextView3.setAnimationProperties(0.45f, 0L, 250L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.actionTextView.setTextSize(AndroidUtilities.dp(15.0f));
            this.actionTextView.setTextColor(Theme.getColor(i));
            this.actionTextView.setGravity(LocaleController.isRTL ? 3 : 5);
            addView(this.actionTextView, LayoutHelper.createFrame(-2, 20.0f, (LocaleController.isRTL ? 3 : 5) | 80, 21.0f, 15.0f, 21.0f, 2.0f));
            ViewCompat.setAccessibilityHeading(this, true);
        }

        public static void lambda$setAction$0(Runnable runnable, View view) {
            if (runnable != null) {
                runnable.run();
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.widget.TextView");
            accessibilityNodeInfo.setText(this.textView.getText());
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
        }

        public void setAction(CharSequence charSequence, final Runnable runnable) {
            this.actionTextView.setText(charSequence, !LocaleController.isRTL);
            this.actionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    FolderBottomSheet.HeaderCell.lambda$setAction$0(runnable, view);
                }
            });
        }

        public void setText(CharSequence charSequence, boolean z) {
            if (z) {
                this.textView.cancelAnimation();
            }
            this.textView.setText(charSequence, z && !LocaleController.isRTL);
        }
    }

    public class TitleCell extends FrameLayout {
        private boolean already;
        private FoldersPreview preview;
        private AnimatedEmojiSpan.TextViewEmojis subtitleTextView;
        private CharSequence title;
        private AnimatedEmojiSpan.TextViewEmojis titleTextView;

        public class FoldersPreview extends View {
            AnimatedTextView.AnimatedTextDrawable countText;
            Text leftFolder;
            Text leftFolder2;
            LinearGradient leftGradient;
            Matrix leftMatrix;
            Paint leftPaint;
            Text middleFolder;
            TextPaint paint;
            Path path;
            float[] radii;
            Text rightFolder;
            Text rightFolder2;
            LinearGradient rightGradient;
            Matrix rightMatrix;
            Paint rightPaint;
            Paint selectedPaint;
            TextPaint selectedTextPaint;

            public FoldersPreview(Context context, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, ArrayList arrayList, boolean z, CharSequence charSequence4, CharSequence charSequence5) {
                super(context);
                this.paint = new TextPaint(1);
                this.selectedTextPaint = new TextPaint(1);
                this.selectedPaint = new Paint(1);
                this.path = new Path();
                this.radii = new float[8];
                this.leftPaint = new Paint(1);
                this.rightPaint = new Paint(1);
                this.leftMatrix = new Matrix();
                this.rightMatrix = new Matrix();
                TextPaint textPaint = this.paint;
                int i = Theme.key_profile_tabText;
                textPaint.setColor(Theme.multAlpha(Theme.getColor(i), 0.8f));
                this.paint.setTextSize(AndroidUtilities.dp(15.33f));
                this.paint.setTypeface(AndroidUtilities.bold());
                TextPaint textPaint2 = this.selectedTextPaint;
                int i2 = Theme.key_windowBackgroundWhiteBlueText2;
                textPaint2.setColor(Theme.getColor(i2));
                this.selectedTextPaint.setTextSize(AndroidUtilities.dp(17.0f));
                this.selectedTextPaint.setTypeface(AndroidUtilities.bold());
                this.selectedPaint.setColor(Theme.getColor(Theme.key_featuredStickers_unread));
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
                this.countText = animatedTextDrawable;
                animatedTextDrawable.setAnimationProperties(0.3f, 0L, 250L, CubicBezierInterpolator.EASE_OUT_QUINT);
                this.countText.setCallback(this);
                this.countText.setTextSize(AndroidUtilities.dp(11.66f));
                this.countText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                this.countText.setTypeface(AndroidUtilities.bold());
                this.countText.setGravity(1);
                int multAlpha = Theme.multAlpha(Theme.getColor(i), 0.8f);
                int color = Theme.getColor(i2);
                if (charSequence != null) {
                    this.leftFolder2 = new Text(normalizeTitle(charSequence), 15.33f, AndroidUtilities.bold()).supportAnimatedEmojis(this).setColor(multAlpha);
                }
                if (charSequence2 != null) {
                    this.leftFolder = new Text(normalizeTitle(charSequence2), 15.33f, AndroidUtilities.bold()).supportAnimatedEmojis(this).setColor(multAlpha);
                }
                CharSequence normalizeTitle = normalizeTitle(charSequence3);
                Text color2 = new Text(normalizeTitle, 15.33f, AndroidUtilities.bold()).supportAnimatedEmojis(this).setColor(color);
                this.middleFolder = color2;
                this.middleFolder.setText(MessageObject.replaceAnimatedEmoji(Emoji.replaceEmoji(normalizeTitle, color2.getFontMetricsInt(), false), arrayList, this.middleFolder.getFontMetricsInt()));
                this.middleFolder.setEmojiCacheType(z ? 26 : 0);
                if (charSequence4 != null) {
                    this.rightFolder = new Text(normalizeTitle(charSequence4), 15.33f, AndroidUtilities.bold()).supportAnimatedEmojis(this).setColor(multAlpha);
                }
                if (charSequence5 != null) {
                    this.rightFolder2 = new Text(normalizeTitle(charSequence5), 15.33f, AndroidUtilities.bold()).supportAnimatedEmojis(this).setColor(multAlpha);
                }
                float[] fArr = this.radii;
                float dp = AndroidUtilities.dp(3.0f);
                fArr[3] = dp;
                fArr[2] = dp;
                fArr[1] = dp;
                fArr[0] = dp;
                float[] fArr2 = this.radii;
                float dp2 = AndroidUtilities.dp(1.0f);
                fArr2[7] = dp2;
                fArr2[6] = dp2;
                fArr2[5] = dp2;
                fArr2[4] = dp2;
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(80.0f), 0.0f, new int[]{-1, 16777215}, new float[]{0.0f, 1.0f}, tileMode);
                this.leftGradient = linearGradient;
                this.leftPaint.setShader(linearGradient);
                Paint paint = this.leftPaint;
                PorterDuff.Mode mode = PorterDuff.Mode.DST_OUT;
                paint.setXfermode(new PorterDuffXfermode(mode));
                LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(80.0f), 0.0f, new int[]{16777215, -1}, new float[]{0.0f, 1.0f}, tileMode);
                this.rightGradient = linearGradient2;
                this.rightPaint.setShader(linearGradient2);
                this.rightPaint.setXfermode(new PorterDuffXfermode(mode));
            }

            private boolean isCountEmpty() {
                return this.countText.getText() == null || this.countText.getText().length() == 0;
            }

            private CharSequence normalizeTitle(CharSequence charSequence) {
                return (charSequence == null || "ALL_CHATS".equals(charSequence.toString())) ? LocaleController.getString(R.string.FilterAllChats) : charSequence;
            }

            @Override
            protected void onDraw(Canvas canvas) {
                float f;
                float f2;
                float f3;
                super.onDraw(canvas);
                canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), 255, 31);
                float measuredWidth = getMeasuredWidth() / 2.0f;
                float measuredHeight = getMeasuredHeight() / 2.0f;
                if (this.middleFolder != null) {
                    canvas.save();
                    float currentWidth = this.middleFolder.getCurrentWidth() + (isCountEmpty() ? 0.0f : AndroidUtilities.dp(15.32f) + this.countText.getCurrentWidth());
                    float f4 = measuredWidth - (currentWidth / 2.0f);
                    canvas.translate(f4, measuredHeight - (this.middleFolder.getHeight() / 2.0f));
                    this.middleFolder.draw(canvas);
                    canvas.restore();
                    f2 = currentWidth;
                    f = f4;
                } else {
                    f = measuredWidth;
                    f2 = 0.0f;
                }
                if (!isCountEmpty()) {
                    android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                    rect.set((int) (this.middleFolder.getCurrentWidth() + f + AndroidUtilities.dp(4.66f)), (int) (measuredHeight - AndroidUtilities.dp(9.0f)), (int) (this.middleFolder.getCurrentWidth() + f + AndroidUtilities.dp(15.32f) + this.countText.getCurrentWidth()), (int) (AndroidUtilities.dp(9.0f) + measuredHeight));
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(rect);
                    canvas.drawRoundRect(rectF, AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), this.selectedPaint);
                    rect.offset(-AndroidUtilities.dp(0.33f), -AndroidUtilities.dp(0.66f));
                    this.countText.setBounds(rect);
                    this.countText.draw(canvas);
                }
                float dp = AndroidUtilities.dp(30.0f);
                float currentWidth2 = (f - dp) - this.leftFolder.getCurrentWidth();
                if (this.leftFolder2 == null || this.leftFolder.getCurrentWidth() >= AndroidUtilities.dp(64.0f)) {
                    f3 = currentWidth2;
                } else {
                    float currentWidth3 = currentWidth2 - (this.leftFolder2.getCurrentWidth() + dp);
                    canvas.save();
                    canvas.translate(currentWidth3, (measuredHeight - (this.leftFolder2.getHeight() / 2.0f)) + AndroidUtilities.dp(1.0f));
                    this.leftFolder2.draw(canvas);
                    canvas.restore();
                    f3 = currentWidth3;
                }
                if (this.leftFolder != null) {
                    canvas.save();
                    canvas.translate(currentWidth2, (measuredHeight - (this.leftFolder.getHeight() / 2.0f)) + AndroidUtilities.dp(1.0f));
                    this.leftFolder.draw(canvas);
                    canvas.restore();
                }
                float f5 = f + f2;
                if (this.rightFolder != null) {
                    canvas.save();
                    canvas.translate(f5 + dp, (measuredHeight - (this.rightFolder.getHeight() / 2.0f)) + AndroidUtilities.dp(1.0f));
                    this.rightFolder.draw(canvas);
                    canvas.restore();
                    f5 += this.rightFolder.getCurrentWidth() + dp;
                }
                if (this.rightFolder2 != null && this.rightFolder.getCurrentWidth() < AndroidUtilities.dp(64.0f)) {
                    canvas.save();
                    canvas.translate(f5 + dp, (measuredHeight - (this.rightFolder2.getHeight() / 2.0f)) + AndroidUtilities.dp(1.0f));
                    this.rightFolder2.draw(canvas);
                    canvas.restore();
                    f5 += dp + this.rightFolder2.getCurrentWidth();
                }
                float height = measuredHeight + (this.middleFolder.getHeight() / 2.0f) + AndroidUtilities.dp(12.0f);
                canvas.drawRect(0.0f, height, getMeasuredWidth(), height + 1.0f, this.paint);
                this.path.rewind();
                RectF rectF2 = AndroidUtilities.rectTmp;
                float f6 = f2 / 2.0f;
                float f7 = f6 + measuredWidth;
                rectF2.set((measuredWidth - f6) - AndroidUtilities.dp(4.0f), height - AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f) + f7, height);
                this.path.addRoundRect(rectF2, this.radii, Path.Direction.CW);
                canvas.drawPath(this.path, this.selectedPaint);
                canvas.save();
                float max = Math.max(AndroidUtilities.dp(8.0f), f3);
                this.leftMatrix.reset();
                this.leftMatrix.postTranslate(Math.min(f, max + AndroidUtilities.dp(8.0f)), 0.0f);
                this.leftGradient.setLocalMatrix(this.leftMatrix);
                float min = Math.min(getMeasuredWidth() - AndroidUtilities.dp(8.0f), f5);
                this.rightMatrix.reset();
                this.rightMatrix.postTranslate(Math.max(f7, min - AndroidUtilities.dp(88.0f)), 0.0f);
                this.rightGradient.setLocalMatrix(this.rightMatrix);
                canvas.drawRect(0.0f, 0.0f, measuredWidth, getMeasuredHeight(), this.leftPaint);
                canvas.drawRect(measuredWidth, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.rightPaint);
                canvas.restore();
                canvas.restore();
            }

            public void setCount(int i, boolean z) {
                String str;
                if (z) {
                    this.countText.cancelAnimation();
                }
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.countText;
                if (i > 0) {
                    str = "+" + i;
                } else {
                    str = "";
                }
                animatedTextDrawable.setText(str, z);
                invalidate();
            }

            @Override
            protected boolean verifyDrawable(Drawable drawable) {
                return drawable == this.countText || super.verifyDrawable(drawable);
            }
        }

        public TitleCell(Context context, boolean z, CharSequence charSequence, ArrayList arrayList, boolean z2) {
            super(context);
            this.already = z;
            FoldersPreview foldersPreview = new FoldersPreview(context, null, LocaleController.getString(R.string.FolderLinkPreviewLeft), charSequence == null ? "" : new SpannableStringBuilder(charSequence), arrayList, z2, LocaleController.getString(R.string.FolderLinkPreviewRight), null);
            this.preview = foldersPreview;
            addView(foldersPreview, LayoutHelper.createFrame(-1, 44.0f, 55, 0.0f, 17.33f, 0.0f, 0.0f));
            AnimatedEmojiSpan.TextViewEmojis textViewEmojis = new AnimatedEmojiSpan.TextViewEmojis(context);
            this.titleTextView = textViewEmojis;
            int i = Theme.key_windowBackgroundWhiteBlackText;
            textViewEmojis.setTextColor(Theme.getColor(i));
            this.titleTextView.setTextSize(1, 20.0f);
            this.titleTextView.setTypeface(AndroidUtilities.bold());
            this.titleTextView.setGravity(17);
            this.titleTextView.setLineSpacing(AndroidUtilities.dp(-1.0f), 1.0f);
            CharSequence replaceEmoji = Emoji.replaceEmoji((CharSequence) new SpannableStringBuilder(charSequence), this.titleTextView.getPaint().getFontMetricsInt(), false, 0.8f);
            this.title = replaceEmoji;
            this.title = MessageObject.replaceAnimatedEmoji(replaceEmoji, arrayList, this.titleTextView.getPaint().getFontMetricsInt(), false, 0.8f);
            this.titleTextView.setText(FolderBottomSheet.this.getTitle());
            this.titleTextView.setCacheType(z2 ? 26 : 0);
            this.titleTextView.setEmojiColor(Theme.getColor(Theme.key_featuredStickers_addButton, ((BottomSheet) FolderBottomSheet.this).resourcesProvider));
            addView(this.titleTextView, LayoutHelper.createFrame(-1, -2.0f, 48, 32.0f, 78.3f, 32.0f, 0.0f));
            AnimatedEmojiSpan.TextViewEmojis textViewEmojis2 = new AnimatedEmojiSpan.TextViewEmojis(context);
            this.subtitleTextView = textViewEmojis2;
            textViewEmojis2.setTextColor(Theme.getColor(i));
            this.subtitleTextView.setTextSize(1, 14.0f);
            this.subtitleTextView.setLines(2);
            this.subtitleTextView.setGravity(17);
            this.subtitleTextView.setLineSpacing(0.0f, 1.15f);
            addView(this.subtitleTextView, LayoutHelper.createFrame(-1, -2.0f, 48, 32.0f, 113.0f, 32.0f, 0.0f));
            setSelectedCount(0, false);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(172.0f), 1073741824));
        }

        public void setSelectedCount(int i, boolean z) {
            AnimatedEmojiSpan.TextViewEmojis textViewEmojis;
            CharSequence formatSpannable;
            if (FolderBottomSheet.this.deleting) {
                textViewEmojis = this.subtitleTextView;
                formatSpannable = LocaleController.formatSpannable(R.string.FolderLinkSubtitleRemove, this.title);
            } else if (this.already) {
                this.preview.setCount(FolderBottomSheet.this.peers != null ? FolderBottomSheet.this.peers.size() : 0, false);
                if (FolderBottomSheet.this.peers == null || FolderBottomSheet.this.peers.isEmpty()) {
                    textViewEmojis = this.subtitleTextView;
                    formatSpannable = LocaleController.formatSpannable(R.string.FolderLinkSubtitleAlready, this.title);
                } else {
                    textViewEmojis = this.subtitleTextView;
                    formatSpannable = LocaleController.formatPluralSpannable("FolderLinkSubtitleChats", FolderBottomSheet.this.peers != null ? FolderBottomSheet.this.peers.size() : 0, this.title);
                }
            } else if (FolderBottomSheet.this.peers == null || FolderBottomSheet.this.peers.isEmpty()) {
                textViewEmojis = this.subtitleTextView;
                formatSpannable = LocaleController.formatSpannable(R.string.FolderLinkSubtitleAlready, this.title);
            } else {
                textViewEmojis = this.subtitleTextView;
                formatSpannable = LocaleController.formatSpannable(R.string.FolderLinkSubtitle, this.title);
            }
            textViewEmojis.setText(AndroidUtilities.replaceTags(formatSpannable));
        }
    }

    public FolderBottomSheet(BaseFragment baseFragment, int i, List list) {
        super(baseFragment, false, false);
        MessagesController.DialogFilter dialogFilter;
        TLRPC.Chat chat;
        this.filterId = -1;
        this.title = "";
        this.titleEntities = new ArrayList();
        this.escapedTitle = "";
        this.alreadyJoined = new ArrayList();
        this.selectedPeers = new ArrayList();
        this.reqId = -1;
        this.shiftDp = -5;
        this.filterId = i;
        this.deleting = true;
        this.peers = new ArrayList();
        this.selectedPeers.clear();
        if (list != null) {
            this.selectedPeers.addAll(list);
        }
        ArrayList<MessagesController.DialogFilter> arrayList = baseFragment.getMessagesController().dialogFilters;
        if (arrayList != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (arrayList.get(i2).id == i) {
                    dialogFilter = arrayList.get(i2);
                    break;
                }
            }
        }
        dialogFilter = null;
        if (dialogFilter != null) {
            this.title = dialogFilter.name;
            this.titleEntities = dialogFilter.entities;
            this.titleNoanimate = dialogFilter.title_noanimate;
            for (int i3 = 0; i3 < this.selectedPeers.size(); i3++) {
                TLRPC.Peer peer = baseFragment.getMessagesController().getPeer(((Long) this.selectedPeers.get(i3)).longValue());
                if ((peer instanceof TLRPC.TL_peerChat) || (peer instanceof TLRPC.TL_peerChannel)) {
                    this.peers.add(peer);
                }
            }
            for (int i4 = 0; i4 < dialogFilter.alwaysShow.size(); i4++) {
                Long l = dialogFilter.alwaysShow.get(i4);
                long longValue = l.longValue();
                if (!this.selectedPeers.contains(l)) {
                    TLRPC.Peer peer2 = baseFragment.getMessagesController().getPeer(longValue);
                    if (((peer2 instanceof TLRPC.TL_peerChat) || (peer2 instanceof TLRPC.TL_peerChannel)) && ((chat = baseFragment.getMessagesController().getChat(Long.valueOf(-longValue))) == null || !ChatObject.isNotInChat(chat))) {
                        this.peers.add(peer2);
                    }
                }
            }
        }
        init();
    }

    public FolderBottomSheet(BaseFragment baseFragment, int i, TL_chatlists.TL_chatlists_chatlistUpdates tL_chatlists_chatlistUpdates) {
        super(baseFragment, false, false);
        int i2 = 0;
        this.filterId = -1;
        this.title = "";
        this.titleEntities = new ArrayList();
        this.escapedTitle = "";
        this.alreadyJoined = new ArrayList();
        ArrayList arrayList = new ArrayList();
        this.selectedPeers = arrayList;
        this.reqId = -1;
        this.shiftDp = -5;
        this.filterId = i;
        this.updates = tL_chatlists_chatlistUpdates;
        arrayList.clear();
        this.peers = tL_chatlists_chatlistUpdates.missing_peers;
        ArrayList<MessagesController.DialogFilter> arrayList2 = baseFragment.getMessagesController().dialogFilters;
        if (arrayList2 != null) {
            while (true) {
                if (i2 >= arrayList2.size()) {
                    break;
                }
                if (arrayList2.get(i2).id == i) {
                    this.title = arrayList2.get(i2).name;
                    break;
                }
                i2++;
            }
        }
        init();
    }

    public FolderBottomSheet(BaseFragment baseFragment, String str, TL_chatlists.chatlist_ChatlistInvite chatlist_chatlistinvite) {
        super(baseFragment, false, false);
        int i = 0;
        this.filterId = -1;
        this.title = "";
        this.titleEntities = new ArrayList();
        this.escapedTitle = "";
        this.alreadyJoined = new ArrayList();
        ArrayList arrayList = new ArrayList();
        this.selectedPeers = arrayList;
        this.reqId = -1;
        this.shiftDp = -5;
        this.slug = str;
        this.invite = chatlist_chatlistinvite;
        arrayList.clear();
        if (chatlist_chatlistinvite instanceof TL_chatlists.TL_chatlists_chatlistInvite) {
            TL_chatlists.TL_chatlists_chatlistInvite tL_chatlists_chatlistInvite = (TL_chatlists.TL_chatlists_chatlistInvite) chatlist_chatlistinvite;
            TLRPC.TL_textWithEntities tL_textWithEntities = tL_chatlists_chatlistInvite.title;
            this.title = tL_textWithEntities.text;
            this.titleEntities = tL_textWithEntities.entities;
            this.titleNoanimate = tL_chatlists_chatlistInvite.title_noanimate;
            this.peers = tL_chatlists_chatlistInvite.peers;
        } else if (chatlist_chatlistinvite instanceof TL_chatlists.TL_chatlists_chatlistInviteAlready) {
            TL_chatlists.TL_chatlists_chatlistInviteAlready tL_chatlists_chatlistInviteAlready = (TL_chatlists.TL_chatlists_chatlistInviteAlready) chatlist_chatlistinvite;
            this.peers = tL_chatlists_chatlistInviteAlready.missing_peers;
            this.alreadyPeers = tL_chatlists_chatlistInviteAlready.already_peers;
            this.filterId = tL_chatlists_chatlistInviteAlready.filter_id;
            ArrayList<MessagesController.DialogFilter> arrayList2 = baseFragment.getMessagesController().dialogFilters;
            if (arrayList2 != null) {
                while (true) {
                    if (i >= arrayList2.size()) {
                        break;
                    }
                    MessagesController.DialogFilter dialogFilter = arrayList2.get(i);
                    if (dialogFilter.id == this.filterId) {
                        this.title = dialogFilter.name;
                        this.titleEntities = dialogFilter.entities;
                        this.titleNoanimate = dialogFilter.title_noanimate;
                        break;
                    }
                    i++;
                }
            }
        }
        init();
    }

    private void announceSelection(boolean z) {
        String str;
        StringBuilder sb = new StringBuilder();
        sb.append(LocaleController.formatPluralString("FilterInviteHeaderChats", this.selectedPeers.size(), new Object[0]));
        if (!z || this.headerCell == null) {
            str = "";
        } else {
            str = ", " + ((Object) this.headerCell.actionTextView.getText());
        }
        sb.append(str);
        AndroidUtilities.makeAccessibilityAnnouncement(sb.toString());
    }

    private void deselectAll(final HeaderCell headerCell, final boolean z) {
        this.selectedPeers.clear();
        this.selectedPeers.addAll(this.alreadyJoined);
        if (!z) {
            for (int i = 0; i < this.peers.size(); i++) {
                long peerDialogId = DialogObject.getPeerDialogId((TLRPC.Peer) this.peers.get(i));
                if (!this.selectedPeers.contains(Long.valueOf(peerDialogId))) {
                    this.selectedPeers.add(Long.valueOf(peerDialogId));
                }
            }
        }
        updateCount(true);
        headerCell.setAction(LocaleController.getString(z ? R.string.SelectAll : R.string.DeselectAll), new Runnable() {
            @Override
            public final void run() {
                FolderBottomSheet.this.lambda$deselectAll$21(headerCell, z);
            }
        });
        announceSelection(true);
        for (int i2 = 0; i2 < this.recyclerListView.getChildCount(); i2++) {
            View childAt = this.recyclerListView.getChildAt(i2);
            if (childAt instanceof GroupCreateUserCell) {
                Object tag = childAt.getTag();
                if (tag instanceof Long) {
                    ArrayList arrayList = this.selectedPeers;
                    Long l = (Long) tag;
                    l.longValue();
                    ((GroupCreateUserCell) childAt).setChecked(arrayList.contains(l), true);
                }
            }
        }
    }

    private void init() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FolderBottomSheet.init():void");
    }

    public void lambda$deselectAll$21(HeaderCell headerCell, boolean z) {
        deselectAll(headerCell, !z);
    }

    public void lambda$init$5(View view) {
        onJoinButtonClicked();
    }

    public void lambda$onJoinButtonClicked$10(TLObject tLObject, final Pair pair) {
        this.reqId = getBaseFragment().getConnectionsManager().sendRequest(tLObject, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                FolderBottomSheet.this.lambda$onJoinButtonClicked$9(pair, tLObject2, tL_error);
            }
        });
    }

    public void lambda$onJoinButtonClicked$11(ArrayList arrayList, BaseFragment baseFragment) {
        BulletinFactory of;
        int i;
        SpannableStringBuilder replaceTags;
        String formatPluralString;
        if (this.updates != null || (this.invite instanceof TL_chatlists.TL_chatlists_chatlistInviteAlready)) {
            of = BulletinFactory.of(baseFragment);
            i = R.raw.folder_in;
            replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString(R.string.FolderLinkUpdatedTitle, this.escapedTitle));
            formatPluralString = arrayList.size() <= 0 ? LocaleController.formatPluralString("FolderLinkUpdatedSubtitle", this.alreadyJoined.size(), new Object[0]) : LocaleController.formatPluralString("FolderLinkUpdatedJoinedSubtitle", arrayList.size(), new Object[0]);
        } else {
            of = BulletinFactory.of(baseFragment);
            i = R.raw.contact_check;
            replaceTags = AndroidUtilities.replaceTags(LocaleController.formatString(R.string.FolderLinkAddedTitle, this.escapedTitle));
            formatPluralString = LocaleController.formatPluralString("FolderLinkAddedSubtitle", arrayList.size(), new Object[0]);
        }
        of.createSimpleBulletin(i, replaceTags, formatPluralString).setDuration(5000).show();
    }

    public static void lambda$onJoinButtonClicked$12(Utilities.Callback callback, INavigationLayout iNavigationLayout, Integer num) {
        callback.run(iNavigationLayout.getLastFragment());
    }

    public static void lambda$onJoinButtonClicked$14(DialogsActivity dialogsActivity, Integer num, final Utilities.Callback callback, final BaseFragment baseFragment) {
        dialogsActivity.scrollToFolder(num.intValue());
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                Utilities.Callback.this.run(baseFragment);
            }
        }, 200L);
    }

    public static void lambda$onJoinButtonClicked$15(INavigationLayout iNavigationLayout, final Utilities.Callback callback, final Integer num) {
        List fragmentStack = iNavigationLayout.getFragmentStack();
        boolean z = true;
        final BaseFragment baseFragment = null;
        for (int size = fragmentStack.size() - 1; size >= 0; size--) {
            baseFragment = (BaseFragment) fragmentStack.get(size);
            if (baseFragment instanceof DialogsActivity) {
                break;
            }
            if (z) {
                baseFragment.lambda$onBackPressed$323();
                z = false;
            } else {
                baseFragment.removeSelfFromStack();
            }
        }
        if (!(baseFragment instanceof DialogsActivity)) {
            callback.run(baseFragment);
            return;
        }
        final DialogsActivity dialogsActivity = (DialogsActivity) baseFragment;
        dialogsActivity.closeSearching();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FolderBottomSheet.lambda$onJoinButtonClicked$14(DialogsActivity.this, num, callback, baseFragment);
            }
        }, 80L);
    }

    public void lambda$onJoinButtonClicked$16(Utilities.Callback callback, int i, Boolean bool) {
        this.success = bool.booleanValue();
        lambda$new$0();
        callback.run(Integer.valueOf(i));
    }

    public void lambda$onJoinButtonClicked$17(org.telegram.tgnet.TLRPC.TL_error r4, org.telegram.tgnet.TLObject r5, final org.telegram.messenger.Utilities.Callback r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FolderBottomSheet.lambda$onJoinButtonClicked$17(org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLObject, org.telegram.messenger.Utilities$Callback):void");
    }

    public void lambda$onJoinButtonClicked$18(final Utilities.Callback callback, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FolderBottomSheet.this.lambda$onJoinButtonClicked$17(tL_error, tLObject, callback);
            }
        });
    }

    public void lambda$onJoinButtonClicked$6(BaseFragment baseFragment, ArrayList arrayList) {
        this.reqId = -1;
        BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.ic_delete, LocaleController.formatString(R.string.FolderLinkDeletedTitle, this.title), LocaleController.formatPluralString("FolderLinkDeletedSubtitle", arrayList.size(), new Object[0])).setDuration(5000).show();
        this.success = true;
        lambda$new$0();
        getBaseFragment().getMessagesController().invalidateChatlistFolderUpdate(this.filterId);
    }

    public void lambda$onJoinButtonClicked$7(final BaseFragment baseFragment, final ArrayList arrayList, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FolderBottomSheet.this.lambda$onJoinButtonClicked$6(baseFragment, arrayList);
            }
        });
    }

    public void lambda$onJoinButtonClicked$8(Pair pair) {
        this.reqId = -1;
        ((Runnable) pair.first).run();
    }

    public void lambda$onJoinButtonClicked$9(final Pair pair, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FolderBottomSheet.this.lambda$onJoinButtonClicked$8(pair);
            }
        });
    }

    public void lambda$onViewCreated$19(View view, int i) {
        int i2;
        String str;
        if (!(view instanceof GroupCreateUserCell) || (i2 = (i - 1) - this.usersStartRow) < 0 || i2 >= this.peers.size()) {
            return;
        }
        long peerDialogId = DialogObject.getPeerDialogId((TLRPC.Peer) this.peers.get(i2));
        if (!this.selectedPeers.contains(Long.valueOf(peerDialogId))) {
            this.selectedPeers.add(Long.valueOf(peerDialogId));
            ((GroupCreateUserCell) view).setChecked(true, true);
        } else {
            if (this.alreadyJoined.contains(Long.valueOf(peerDialogId))) {
                int i3 = -this.shiftDp;
                this.shiftDp = i3;
                AndroidUtilities.shakeViewSpring(view, i3);
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
                ArrayList arrayList = new ArrayList();
                if (peerDialogId >= 0) {
                    arrayList.add(getBaseFragment().getMessagesController().getUser(Long.valueOf(peerDialogId)));
                    str = "beep boop.";
                } else {
                    TLRPC.Chat chat = getBaseFragment().getMessagesController().getChat(Long.valueOf(-peerDialogId));
                    String string = LocaleController.getString(ChatObject.isChannelAndNotMegaGroup(chat) ? R.string.FolderLinkAlreadySubscribed : R.string.FolderLinkAlreadyJoined);
                    arrayList.add(chat);
                    str = string;
                }
                if (this.lastClickedDialogId != peerDialogId || System.currentTimeMillis() - this.lastClicked > 1500) {
                    this.lastClickedDialogId = peerDialogId;
                    this.lastClicked = System.currentTimeMillis();
                    BulletinFactory.of(this.bulletinContainer, null).createChatsBulletin(arrayList, str, null).setDuration(1500).show();
                    return;
                }
                return;
            }
            this.selectedPeers.remove(Long.valueOf(peerDialogId));
            ((GroupCreateUserCell) view).setChecked(false, true);
        }
        updateCount(true);
        updateHeaderCell(true);
        announceSelection(false);
    }

    public static void lambda$showForDeletion$0(BaseFragment baseFragment, TLObject tLObject, int i, Utilities.Callback callback) {
        FolderBottomSheet folderBottomSheet;
        if (baseFragment.getParentActivity() == null) {
            return;
        }
        if (tLObject instanceof Vector) {
            Vector vector = (Vector) tLObject;
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < vector.objects.size(); i2++) {
                try {
                    arrayList.add(Long.valueOf(DialogObject.getPeerDialogId((TLRPC.Peer) vector.objects.get(i2))));
                } catch (Exception unused) {
                }
            }
            folderBottomSheet = new FolderBottomSheet(baseFragment, i, arrayList);
        } else {
            folderBottomSheet = new FolderBottomSheet(baseFragment, i, (List) null);
        }
        folderBottomSheet.setOnDone(callback);
        baseFragment.showDialog(folderBottomSheet);
    }

    public static void lambda$showForDeletion$1(final BaseFragment baseFragment, final int i, final Utilities.Callback callback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FolderBottomSheet.lambda$showForDeletion$0(BaseFragment.this, tLObject, i, callback);
            }
        });
    }

    public static void lambda$showForDeletion$2(final int i, final BaseFragment baseFragment, final Utilities.Callback callback) {
        TL_chatlists.TL_chatlists_getLeaveChatlistSuggestions tL_chatlists_getLeaveChatlistSuggestions = new TL_chatlists.TL_chatlists_getLeaveChatlistSuggestions();
        TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
        tL_chatlists_getLeaveChatlistSuggestions.chatlist = tL_inputChatlistDialogFilter;
        tL_inputChatlistDialogFilter.filter_id = i;
        baseFragment.getConnectionsManager().sendRequest(tL_chatlists_getLeaveChatlistSuggestions, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FolderBottomSheet.lambda$showForDeletion$1(BaseFragment.this, i, callback, tLObject, tL_error);
            }
        });
    }

    public static void lambda$showForDeletion$3(Utilities.Callback callback, AlertDialog alertDialog, int i) {
        if (callback != null) {
            callback.run(Boolean.FALSE);
        }
    }

    public void lambda$updateHeaderCell$20(boolean z) {
        deselectAll(this.headerCell, z);
    }

    private void onJoinButtonClicked() {
        ArrayList<TLRPC.InputPeer> arrayList;
        final TL_chatlists.TL_chatlists_joinChatlistInvite tL_chatlists_joinChatlistInvite;
        FiltersSetupActivity filtersSetupActivity;
        Button button = this.button;
        if (button == null || !button.isLoading()) {
            ArrayList arrayList2 = this.peers;
            if (arrayList2 == null) {
                lambda$new$0();
                return;
            }
            if (arrayList2.isEmpty() && !this.deleting) {
                lambda$new$0();
                return;
            }
            if (this.selectedPeers.isEmpty() && (this.invite instanceof TL_chatlists.TL_chatlists_chatlistInvite)) {
                Button button2 = this.button;
                int i = -this.shiftDp;
                this.shiftDp = i;
                AndroidUtilities.shakeViewSpring(button2, i);
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
                return;
            }
            final ArrayList arrayList3 = new ArrayList();
            for (int i2 = 0; i2 < this.peers.size(); i2++) {
                long peerDialogId = DialogObject.getPeerDialogId((TLRPC.Peer) this.peers.get(i2));
                if (this.selectedPeers.contains(Long.valueOf(peerDialogId))) {
                    arrayList3.add(getBaseFragment().getMessagesController().getInputPeer(peerDialogId));
                }
            }
            UndoView undoView = null;
            if (this.deleting) {
                TL_chatlists.TL_chatlists_leaveChatlist tL_chatlists_leaveChatlist = new TL_chatlists.TL_chatlists_leaveChatlist();
                TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
                tL_chatlists_leaveChatlist.chatlist = tL_inputChatlistDialogFilter;
                tL_inputChatlistDialogFilter.filter_id = this.filterId;
                arrayList = tL_chatlists_leaveChatlist.peers;
                tL_chatlists_joinChatlistInvite = tL_chatlists_leaveChatlist;
            } else if (this.updates != null) {
                if (arrayList3.isEmpty()) {
                    TL_chatlists.TL_chatlists_hideChatlistUpdates tL_chatlists_hideChatlistUpdates = new TL_chatlists.TL_chatlists_hideChatlistUpdates();
                    TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter2 = new TL_chatlists.TL_inputChatlistDialogFilter();
                    tL_chatlists_hideChatlistUpdates.chatlist = tL_inputChatlistDialogFilter2;
                    tL_inputChatlistDialogFilter2.filter_id = this.filterId;
                    getBaseFragment().getConnectionsManager().sendRequest(tL_chatlists_hideChatlistUpdates, null);
                    getBaseFragment().getMessagesController().invalidateChatlistFolderUpdate(this.filterId);
                    lambda$new$0();
                    return;
                }
                TL_chatlists.TL_chatlists_joinChatlistUpdates tL_chatlists_joinChatlistUpdates = new TL_chatlists.TL_chatlists_joinChatlistUpdates();
                TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter3 = new TL_chatlists.TL_inputChatlistDialogFilter();
                tL_chatlists_joinChatlistUpdates.chatlist = tL_inputChatlistDialogFilter3;
                tL_inputChatlistDialogFilter3.filter_id = this.filterId;
                arrayList = tL_chatlists_joinChatlistUpdates.peers;
                tL_chatlists_joinChatlistInvite = tL_chatlists_joinChatlistUpdates;
            } else {
                if ((this.invite instanceof TL_chatlists.TL_chatlists_chatlistInviteAlready) && arrayList3.isEmpty()) {
                    lambda$new$0();
                    return;
                }
                TL_chatlists.TL_chatlists_joinChatlistInvite tL_chatlists_joinChatlistInvite2 = new TL_chatlists.TL_chatlists_joinChatlistInvite();
                tL_chatlists_joinChatlistInvite2.slug = this.slug;
                arrayList = tL_chatlists_joinChatlistInvite2.peers;
                tL_chatlists_joinChatlistInvite = tL_chatlists_joinChatlistInvite2;
            }
            arrayList.addAll(arrayList3);
            final INavigationLayout parentLayout = getBaseFragment().getParentLayout();
            if (!this.deleting) {
                if (parentLayout != null) {
                    final Utilities.Callback callback = new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            FolderBottomSheet.this.lambda$onJoinButtonClicked$11(arrayList3, (BaseFragment) obj);
                        }
                    };
                    final Utilities.Callback callback2 = this.updates != null ? new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            FolderBottomSheet.lambda$onJoinButtonClicked$12(Utilities.Callback.this, parentLayout, (Integer) obj);
                        }
                    } : new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            FolderBottomSheet.lambda$onJoinButtonClicked$15(INavigationLayout.this, callback, (Integer) obj);
                        }
                    };
                    int i3 = 0;
                    while (true) {
                        if (i3 >= arrayList3.size()) {
                            break;
                        }
                        if (this.alreadyJoined.contains(Long.valueOf(DialogObject.getPeerDialogId((TLRPC.InputPeer) arrayList3.get(i3))))) {
                            i3++;
                        } else {
                            boolean[] zArr = new boolean[1];
                            getBaseFragment().getMessagesController().ensureFolderDialogExists(1, zArr);
                            if (zArr[0]) {
                                getBaseFragment().getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                        }
                    }
                    this.button.setLoading(true);
                    this.reqId = getBaseFragment().getConnectionsManager().sendRequest(tL_chatlists_joinChatlistInvite, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FolderBottomSheet.this.lambda$onJoinButtonClicked$18(callback2, tLObject, tL_error);
                        }
                    });
                    return;
                }
                return;
            }
            if (parentLayout != null) {
                final BaseFragment lastFragment = parentLayout.getLastFragment();
                if (lastFragment instanceof ChatActivity) {
                    undoView = ((ChatActivity) lastFragment).getUndoView();
                } else if (lastFragment instanceof DialogsActivity) {
                    undoView = ((DialogsActivity) lastFragment).getUndoView();
                } else {
                    if (lastFragment instanceof FiltersSetupActivity) {
                        filtersSetupActivity = (FiltersSetupActivity) lastFragment;
                    } else if (lastFragment instanceof FilterCreateActivity) {
                        List fragmentStack = parentLayout.getFragmentStack();
                        if (fragmentStack.size() >= 2 && (fragmentStack.get(fragmentStack.size() - 2) instanceof FiltersSetupActivity)) {
                            filtersSetupActivity = (FiltersSetupActivity) fragmentStack.get(fragmentStack.size() - 2);
                            lastFragment.lambda$onBackPressed$323();
                        }
                    }
                    undoView = filtersSetupActivity.getUndoView();
                }
                UndoView undoView2 = undoView;
                if (undoView2 == null) {
                    this.button.setLoading(true);
                    this.reqId = getBaseFragment().getConnectionsManager().sendRequest(tL_chatlists_joinChatlistInvite, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            FolderBottomSheet.this.lambda$onJoinButtonClicked$7(lastFragment, arrayList3, tLObject, tL_error);
                        }
                    });
                    return;
                }
                ArrayList<Long> arrayList4 = new ArrayList<>();
                for (int i4 = 0; i4 < arrayList3.size(); i4++) {
                    arrayList4.add(Long.valueOf(DialogObject.getPeerDialogId((TLRPC.InputPeer) arrayList3.get(i4))));
                }
                final Pair<Runnable, Runnable> removeFolderTemporarily = getBaseFragment().getMessagesController().removeFolderTemporarily(this.filterId, arrayList4);
                undoView2.showWithAction(0L, 88, this.title, Integer.valueOf(arrayList3.size()), new Runnable() {
                    @Override
                    public final void run() {
                        FolderBottomSheet.this.lambda$onJoinButtonClicked$10(tL_chatlists_joinChatlistInvite, removeFolderTemporarily);
                    }
                }, (Runnable) removeFolderTemporarily.second);
                this.success = true;
                lambda$new$0();
                getBaseFragment().getMessagesController().invalidateChatlistFolderUpdate(this.filterId);
            }
        }
    }

    public static void showForDeletion(final BaseFragment baseFragment, final int i, final Utilities.Callback callback) {
        MessagesController.DialogFilter dialogFilter;
        ArrayList<MessagesController.DialogFilter> arrayList = baseFragment.getMessagesController().dialogFilters;
        if (arrayList != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (arrayList.get(i2).id == i) {
                    dialogFilter = arrayList.get(i2);
                    break;
                }
            }
        }
        dialogFilter = null;
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                FolderBottomSheet.lambda$showForDeletion$2(i, baseFragment, callback);
            }
        };
        if (dialogFilter == null || !dialogFilter.isMyChatlist()) {
            runnable.run();
            return;
        }
        AlertDialog create = new AlertDialog.Builder(baseFragment.getContext()).setTitle(LocaleController.getString(R.string.FilterDelete)).setMessage(LocaleController.getString(R.string.FilterDeleteAlertLinks)).setNegativeButton(LocaleController.getString(R.string.Cancel), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i3) {
                FolderBottomSheet.lambda$showForDeletion$3(Utilities.Callback.this, alertDialog, i3);
            }
        }).setPositiveButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i3) {
                runnable.run();
            }
        }).create();
        baseFragment.showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    public void updateHeaderCell(boolean z) {
        HeaderCell headerCell = this.headerCell;
        if (headerCell == null) {
            return;
        }
        headerCell.setText(this.deleting ? LocaleController.formatPluralString("FolderLinkHeaderChatsQuit", this.peers.size(), new Object[0]) : LocaleController.formatPluralString("FolderLinkHeaderChatsJoin", this.peers.size(), new Object[0]), false);
        ArrayList arrayList = this.peers;
        if (arrayList == null || arrayList.size() - this.alreadyJoined.size() <= 1) {
            this.headerCell.setAction("", null);
        } else {
            final boolean z2 = this.selectedPeers.size() >= this.peers.size() - this.alreadyJoined.size();
            this.headerCell.setAction(LocaleController.getString(z2 ? R.string.DeselectAll : R.string.SelectAll), new Runnable() {
                @Override
                public final void run() {
                    FolderBottomSheet.this.lambda$updateHeaderCell$20(z2);
                }
            });
        }
    }

    @Override
    protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
        return new RecyclerListView.SelectionAdapter() {
            @Override
            public int getItemCount() {
                return FolderBottomSheet.this.rowsCount;
            }

            @Override
            public int getItemViewType(int i) {
                if (i == FolderBottomSheet.this.titleRow) {
                    return 0;
                }
                if (i == FolderBottomSheet.this.sectionRow || i == FolderBottomSheet.this.usersSectionRow || i == FolderBottomSheet.this.alreadySectionRow) {
                    return 1;
                }
                return (i == FolderBottomSheet.this.headerRow || i == FolderBottomSheet.this.alreadyHeaderRow) ? 3 : 2;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 2 && viewHolder.getAdapterPosition() >= FolderBottomSheet.this.usersStartRow && viewHolder.getAdapterPosition() <= FolderBottomSheet.this.usersEndRow;
            }

            @Override
            public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FolderBottomSheet.AnonymousClass1.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view;
                int i2;
                if (i == 0) {
                    FolderBottomSheet folderBottomSheet = FolderBottomSheet.this;
                    FolderBottomSheet folderBottomSheet2 = FolderBottomSheet.this;
                    view = folderBottomSheet.titleCell = new TitleCell(folderBottomSheet2.getContext(), (FolderBottomSheet.this.invite instanceof TL_chatlists.TL_chatlists_chatlistInviteAlready) || FolderBottomSheet.this.updates != null, FolderBottomSheet.this.escapedTitle, FolderBottomSheet.this.titleEntities, FolderBottomSheet.this.titleNoanimate);
                } else {
                    if (i == 1) {
                        view = new TextInfoPrivacyCell(FolderBottomSheet.this.getContext());
                        i2 = Theme.key_windowBackgroundGray;
                    } else if (i == 2) {
                        GroupCreateUserCell groupCreateUserCell = new GroupCreateUserCell(FolderBottomSheet.this.getContext(), 1, 0, false);
                        groupCreateUserCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        view = groupCreateUserCell;
                    } else if (i == 3) {
                        view = new HeaderCell(FolderBottomSheet.this.getContext());
                        i2 = Theme.key_windowBackgroundWhite;
                    } else {
                        view = null;
                    }
                    view.setBackgroundColor(Theme.getColor(i2));
                }
                return new RecyclerListView.Holder(view);
            }
        };
    }

    @Override
    public void lambda$new$0() {
        super.lambda$new$0();
        if (this.reqId >= 0) {
            getBaseFragment().getConnectionsManager().cancelRequest(this.reqId, true);
        }
        Utilities.Callback callback = this.onDone;
        if (callback != null) {
            callback.run(Boolean.valueOf(this.success));
            this.onDone = null;
        }
    }

    @Override
    protected CharSequence getTitle() {
        int i;
        if (this.deleting) {
            i = R.string.FolderLinkTitleRemove;
        } else if (this.invite instanceof TL_chatlists.TL_chatlists_chatlistInvite) {
            i = R.string.FolderLinkTitleAdd;
        } else {
            ArrayList arrayList = this.peers;
            i = (arrayList == null || arrayList.isEmpty()) ? R.string.FolderLinkTitleAlready : R.string.FolderLinkTitleAddChats;
        }
        return LocaleController.getString(i);
    }

    @Override
    public void onViewCreated(FrameLayout frameLayout) {
        super.onViewCreated(frameLayout);
        this.recyclerListView.setOverScrollMode(2);
        this.recyclerListView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(this.button != null ? 68.0f : 0.0f));
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                FolderBottomSheet.this.lambda$onViewCreated$19(view, i);
            }
        });
    }

    public void setOnDone(Utilities.Callback callback) {
        this.onDone = callback;
    }

    public void updateCount(boolean r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FolderBottomSheet.updateCount(boolean):void");
    }
}

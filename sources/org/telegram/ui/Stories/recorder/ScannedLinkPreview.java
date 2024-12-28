package org.telegram.ui.Stories.recorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.RenderNode;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import com.google.android.exoplayer2.util.Consumer;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotFullscreenButtons$$ExternalSyntheticApiModelOutline2;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Text;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stories.recorder.ScannedLinkPreview;

public class ScannedLinkPreview extends View {
    private final AnimatedFloat animatedAlpha;
    private final Paint backgroundPaint;
    private final int[] blurLocation;
    private Object blurRenderNode;
    private View blurView;
    private final ButtonBounce bounce;
    private final RectF bounds;
    private Utilities.Callback clickListener;
    private final RectF clipBounds;
    private final Path clipPath;
    private final int currentAccount;
    private Runnable currentCancel;
    private String currentLink;
    private boolean hasImage;
    private boolean hasResolved;
    private final ImageReceiver imageReceiver;
    private ResolvedLink resolved;
    private final Runnable resolvedListener;
    private Text subtitle;
    private final int[] thisLocation;
    private Text title;
    private boolean touch;

    public static class ResolvedLink {
        public final String sourceLink;

        public ResolvedLink(String str) {
            this.sourceLink = str;
        }

        public static ResolvedLink fromChat(String str, final TLRPC.Chat chat) {
            if (chat == null) {
                return null;
            }
            return new ResolvedLink(str) {
                @Override
                public String getSubtitle() {
                    return LocaleController.getString(R.string.AccDescrOpenChat);
                }

                @Override
                public String getTitle() {
                    return chat.title;
                }

                @Override
                public void open(BaseFragment baseFragment) {
                    baseFragment.presentFragment(ChatActivity.of(-chat.id));
                }

                @Override
                public boolean setImage(ImageReceiver imageReceiver) {
                    AvatarDrawable avatarDrawable = new AvatarDrawable();
                    avatarDrawable.setInfo(chat);
                    imageReceiver.setForUserOrChat(chat, avatarDrawable);
                    return true;
                }
            };
        }

        public static ResolvedLink fromUser(String str, final TLRPC.User user) {
            if (user == null) {
                return null;
            }
            return new ResolvedLink(str) {
                @Override
                public String getSubtitle() {
                    return LocaleController.getString(R.string.ViewProfile);
                }

                @Override
                public String getTitle() {
                    return UserObject.getUserName(user);
                }

                @Override
                public void open(BaseFragment baseFragment) {
                    if (user.id != UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId()) {
                        baseFragment.presentFragment(ProfileActivity.of(user.id));
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", user.id);
                    bundle.putBoolean("my_profile", true);
                    baseFragment.presentFragment(new ProfileActivity(bundle, null));
                }

                @Override
                public boolean setImage(ImageReceiver imageReceiver) {
                    AvatarDrawable avatarDrawable = new AvatarDrawable();
                    avatarDrawable.setInfo(user);
                    imageReceiver.setForUserOrChat(user, avatarDrawable);
                    return true;
                }
            };
        }

        public static void lambda$resolve$0(Utilities.Callback callback, MessagesController messagesController, String str, Long l) {
            ResolvedLink fromChat;
            if (l == null) {
                fromChat = null;
            } else {
                TLObject userOrChat = messagesController.getUserOrChat(l.longValue());
                if (userOrChat instanceof TLRPC.User) {
                    fromChat = fromUser(str, (TLRPC.User) userOrChat);
                } else if (!(userOrChat instanceof TLRPC.Chat)) {
                    return;
                } else {
                    fromChat = fromChat(str, (TLRPC.Chat) userOrChat);
                }
            }
            callback.run(fromChat);
        }

        public static Runnable resolve(int i, final String str, final Utilities.Callback callback) {
            if (callback == null) {
                return null;
            }
            try {
                final MessagesController messagesController = MessagesController.getInstance(i);
                String str2 = messagesController.linkPrefix;
                Uri parse = Uri.parse(str);
                if (!TextUtils.equals(parse.getHost(), str2)) {
                    return null;
                }
                List<String> pathSegments = parse.getPathSegments();
                if (pathSegments.isEmpty()) {
                    return null;
                }
                String str3 = pathSegments.get(0);
                String queryParameter = parse.getQueryParameter("ref");
                if (TextUtils.isEmpty(queryParameter)) {
                    TLObject userOrChat = messagesController.getUserOrChat(str3);
                    if (userOrChat instanceof TLRPC.User) {
                        callback.run(fromUser(str, (TLRPC.User) userOrChat));
                        return null;
                    }
                    if (userOrChat instanceof TLRPC.Chat) {
                        callback.run(fromChat(str, (TLRPC.Chat) userOrChat));
                        return null;
                    }
                }
                return messagesController.getUserNameResolver().resolve(str3, queryParameter, new Consumer() {
                    @Override
                    public final void accept(Object obj) {
                        ScannedLinkPreview.ResolvedLink.lambda$resolve$0(Utilities.Callback.this, messagesController, str, (Long) obj);
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
                callback.run(null);
                return null;
            }
        }

        public abstract String getSubtitle();

        public abstract String getTitle();

        public abstract void open(BaseFragment baseFragment);

        public abstract boolean setImage(ImageReceiver imageReceiver);
    }

    public ScannedLinkPreview(Context context, int i, Runnable runnable) {
        super(context);
        this.animatedAlpha = new AnimatedFloat(this, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.bounds = new RectF();
        this.clipBounds = new RectF();
        this.imageReceiver = new ImageReceiver(this);
        this.clipPath = new Path();
        this.backgroundPaint = new Paint(1);
        this.bounce = new ButtonBounce(this);
        this.thisLocation = new int[2];
        this.blurLocation = new int[2];
        this.currentAccount = i;
        this.resolvedListener = runnable;
    }

    public void lambda$dispatchTouchEvent$1(BaseFragment baseFragment) {
        ResolvedLink resolvedLink = this.resolved;
        if (resolvedLink != null || baseFragment == null) {
            resolvedLink.open(baseFragment);
        }
    }

    public void lambda$setLink$0(ResolvedLink resolvedLink) {
        this.currentCancel = null;
        this.resolved = resolvedLink;
        this.hasResolved = resolvedLink != null;
        setup();
        invalidate();
        Runnable runnable = this.resolvedListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    private void setup() {
        ResolvedLink resolvedLink = this.resolved;
        if (resolvedLink == null) {
            return;
        }
        this.title = new Text(resolvedLink.getTitle(), 16.0f, AndroidUtilities.bold());
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.resolved.getSubtitle());
        if (spannableStringBuilder.toString().contains(">")) {
            spannableStringBuilder.clear();
            spannableStringBuilder.append(AndroidUtilities.replaceArrows(this.resolved.getSubtitle(), false));
        } else {
            spannableStringBuilder.append((CharSequence) " ");
            spannableStringBuilder.append((CharSequence) ">");
            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.settings_arrow);
            coloredImageSpan.setScale(1.25f, 1.25f);
            spannableStringBuilder.setSpan(coloredImageSpan, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
        }
        this.subtitle = new Text(spannableStringBuilder, 14.0f);
        this.hasImage = this.resolved.setImage(this.imageReceiver);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint;
        int i;
        Object obj;
        int width;
        int height;
        float f = this.animatedAlpha.set(this.hasResolved);
        Text text = this.title;
        if (text == null || this.subtitle == null || f <= 0.0f) {
            return;
        }
        text.ellipsize(getWidth() * 0.7f);
        this.subtitle.ellipsize(getWidth() * 0.7f);
        float dp = AndroidUtilities.dp(5.0f);
        float dp2 = AndroidUtilities.dp(10.0f);
        float dp3 = AndroidUtilities.dp(32.0f);
        float dp4 = AndroidUtilities.dp(2.0f);
        float dp5 = AndroidUtilities.dp(11.0f);
        float max = Math.max(Math.min(AndroidUtilities.dp(200.0f), getWidth() * 0.8f), (this.hasImage ? dp5 + dp3 + dp5 : 0.0f) + dp + Math.max(this.title.getCurrentWidth(), this.subtitle.getCurrentWidth()) + AndroidUtilities.dp(15.0f) + dp);
        float max2 = Math.max(this.hasImage ? dp3 : 0.0f, this.title.getHeight() + dp4 + this.subtitle.getHeight()) + dp2 + dp2;
        float scale = this.bounce.getScale(0.05f) * AndroidUtilities.lerp(0.6f, 1.0f, f);
        float dp6 = AndroidUtilities.dp(15.0f) * (1.0f - f);
        this.bounds.set((getWidth() - max) / 2.0f, (getHeight() - max2) / 2.0f, (getWidth() + max) / 2.0f, (getHeight() + max2) / 2.0f);
        this.clipBounds.set(this.bounds);
        AndroidUtilities.scaleRect(this.clipBounds, scale);
        this.clipBounds.offset(0.0f, dp6);
        if (Build.VERSION.SDK_INT < 29 || (obj = this.blurRenderNode) == null || this.blurView == null) {
            paint = this.backgroundPaint;
            i = -587202560;
        } else {
            RenderNode m = BotFullscreenButtons$$ExternalSyntheticApiModelOutline2.m(obj);
            this.clipPath.rewind();
            this.clipPath.addRoundRect(this.clipBounds, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Path.Direction.CW);
            getLocationOnScreen(this.thisLocation);
            this.blurView.getLocationOnScreen(this.blurLocation);
            canvas.saveLayerAlpha(this.clipBounds, (int) (255.0f * f), 31);
            canvas.clipPath(this.clipPath);
            int i2 = this.blurLocation[0];
            int[] iArr = this.thisLocation;
            canvas.translate(i2 - iArr[0], r3[1] - iArr[1]);
            float width2 = this.blurView.getWidth();
            width = m.getWidth();
            float f2 = width2 / width;
            float height2 = this.blurView.getHeight();
            height = m.getHeight();
            float max3 = Math.max(f2, height2 / height);
            canvas.scale(max3, max3);
            canvas.drawRenderNode(m);
            canvas.restore();
            paint = this.backgroundPaint;
            i = 1879048192;
        }
        paint.setColor(Theme.multAlpha(i, f));
        canvas.drawRoundRect(this.clipBounds, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), this.backgroundPaint);
        canvas.save();
        canvas.translate(0.0f, dp6);
        canvas.scale(scale, scale, this.bounds.centerX(), this.bounds.centerY());
        if (this.hasImage) {
            float f3 = dp3 / 2.0f;
            this.imageReceiver.setRoundRadius((int) f3);
            ImageReceiver imageReceiver = this.imageReceiver;
            RectF rectF = this.bounds;
            imageReceiver.setImageCoords(rectF.left + dp + dp5, rectF.centerY() - f3, dp3, dp3);
            this.imageReceiver.setAlpha(f);
            this.imageReceiver.draw(canvas);
        }
        float centerY = this.bounds.centerY() - (((this.title.getHeight() + dp4) + this.subtitle.getHeight()) / 2.0f);
        Text text2 = this.title;
        text2.draw(canvas, this.bounds.left + (this.hasImage ? dp5 + dp3 + dp5 : 0.0f) + dp, centerY + (text2.getHeight() / 2.0f), -1, f);
        this.subtitle.draw(canvas, this.bounds.left + (this.hasImage ? dp3 + dp5 + dp5 : 0.0f) + dp, centerY + this.title.getHeight() + dp4 + (this.subtitle.getHeight() / 2.0f), Theme.blendOver(-16777216, -1610612737), f);
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.ScannedLinkPreview.dispatchTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean inTouch() {
        return this.bounce.isPressed() || this.touch;
    }

    public boolean isResolved() {
        return this.hasResolved;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    public void setBlurRenderNode(View view, Object obj) {
        this.blurView = view;
        this.blurRenderNode = obj;
        invalidate();
    }

    public void setLink(String str) {
        Runnable runnable;
        if (TextUtils.isEmpty(str)) {
            Runnable runnable2 = this.currentCancel;
            if (runnable2 != null) {
                runnable2.run();
                this.currentCancel = null;
            }
            if (this.hasResolved) {
                invalidate();
            }
            this.hasResolved = false;
            this.currentLink = null;
            runnable = this.resolvedListener;
            if (runnable == null) {
                return;
            }
        } else {
            ResolvedLink resolvedLink = this.resolved;
            if ((resolvedLink == null && this.currentCancel == null) || (resolvedLink != null && !TextUtils.equals(resolvedLink.sourceLink, str) && !TextUtils.equals(this.currentLink, str))) {
                Runnable runnable3 = this.currentCancel;
                if (runnable3 != null) {
                    runnable3.run();
                    this.currentCancel = null;
                }
                this.resolved = null;
                this.currentLink = str;
                this.currentCancel = ResolvedLink.resolve(this.currentAccount, str, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        ScannedLinkPreview.this.lambda$setLink$0((ScannedLinkPreview.ResolvedLink) obj);
                    }
                });
                return;
            }
            ResolvedLink resolvedLink2 = this.resolved;
            if (resolvedLink2 == null || this.hasResolved || !TextUtils.equals(resolvedLink2.sourceLink, str)) {
                return;
            }
            this.hasResolved = true;
            setup();
            invalidate();
            runnable = this.resolvedListener;
            if (runnable == null) {
                return;
            }
        }
        runnable.run();
    }

    public void whenClicked(Utilities.Callback callback) {
        this.clickListener = callback;
    }
}

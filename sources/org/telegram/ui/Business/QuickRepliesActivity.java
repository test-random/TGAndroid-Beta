package org.telegram.ui.Business;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialogDecor;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.QuickRepliesActivity;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.LaunchActivity;

public class QuickRepliesActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static AlertDialog currentDialog;
    private NumberTextView countText;
    private ActionBarMenuItem deleteItem;
    private ActionBarMenuItem editItem;
    private UniversalRecyclerView listView;
    private int repliesOrderId;
    public final ArrayList selected = new ArrayList();
    private boolean shownEditItem = true;

    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
        }

        public void lambda$onItemClick$0(int i, String str) {
            QuickRepliesActivity.this.clearSelection();
            QuickRepliesController.getInstance(((BaseFragment) QuickRepliesActivity.this).currentAccount).renameReply(i, str);
        }

        public void lambda$onItemClick$1(AlertDialog alertDialog, int i) {
            QuickRepliesController.getInstance(((BaseFragment) QuickRepliesActivity.this).currentAccount).deleteReplies(QuickRepliesActivity.this.selected);
            QuickRepliesActivity.this.clearSelection();
        }

        @Override
        public void onItemClick(int i) {
            if (i == -1) {
                if (QuickRepliesActivity.this.selected.isEmpty()) {
                    QuickRepliesActivity.this.lambda$onBackPressed$323();
                    return;
                } else {
                    QuickRepliesActivity.this.clearSelection();
                    return;
                }
            }
            if (i != 1) {
                if (i == 2) {
                    QuickRepliesActivity quickRepliesActivity = QuickRepliesActivity.this;
                    quickRepliesActivity.showDialog(new AlertDialog.Builder(quickRepliesActivity.getContext(), QuickRepliesActivity.this.getResourceProvider()).setTitle(LocaleController.formatPluralString("BusinessRepliesDeleteTitle", QuickRepliesActivity.this.selected.size(), new Object[0])).setMessage(LocaleController.formatPluralString("BusinessRepliesDeleteMessage", QuickRepliesActivity.this.selected.size(), new Object[0])).setPositiveButton(LocaleController.getString(R.string.Remove), new AlertDialog.OnButtonClickListener() {
                        @Override
                        public final void onClick(AlertDialog alertDialog, int i2) {
                            QuickRepliesActivity.AnonymousClass1.this.lambda$onItemClick$1(alertDialog, i2);
                        }
                    }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create());
                    return;
                }
                return;
            }
            if (QuickRepliesActivity.this.selected.size() != 1) {
                return;
            }
            final int intValue = ((Integer) QuickRepliesActivity.this.selected.get(0)).intValue();
            QuickRepliesController.QuickReply findReply = QuickRepliesController.getInstance(((BaseFragment) QuickRepliesActivity.this).currentAccount).findReply(intValue);
            if (findReply == null) {
                return;
            }
            QuickRepliesActivity.openRenameReplyAlert(QuickRepliesActivity.this.getContext(), ((BaseFragment) QuickRepliesActivity.this).currentAccount, null, findReply, ((BaseFragment) QuickRepliesActivity.this).resourceProvider, false, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    QuickRepliesActivity.AnonymousClass1.this.lambda$onItemClick$0(intValue, (String) obj);
                }
            });
        }
    }

    public static class LargeQuickReplyView extends FrameLayout {
        private final Paint arrowPaint;
        private final Path arrowPath;
        private final AvatarDrawable avatarDrawable;
        private final CheckBox2 checkBox;
        private final ImageReceiver imageReceiver;
        private boolean needDivider;
        private final Theme.ResourcesProvider resourcesProvider;
        private int[] spanWidth;
        private final TextView textView;
        private final TextView titleView;

        public LargeQuickReplyView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.avatarDrawable = new AvatarDrawable();
            this.imageReceiver = new ImageReceiver(this);
            this.arrowPath = new Path();
            this.arrowPaint = new Paint(1);
            this.spanWidth = new int[1];
            this.resourcesProvider = resourcesProvider;
            setWillNotDraw(false);
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setSingleLine();
            TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
            textView.setEllipsize(truncateAt);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextSize(1, 16.0f);
            boolean z = LocaleController.isRTL;
            addView(textView, LayoutHelper.createFrame(-1, -2.0f, 7, z ? 40.0f : 78.0f, 10.33f, z ? 78.0f : 40.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(2);
            textView2.setEllipsize(truncateAt);
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
            textView2.setTextSize(1, 15.0f);
            boolean z2 = LocaleController.isRTL;
            addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 7, z2 ? 40.0f : 78.0f, 32.0f, z2 ? 78.0f : 40.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(getContext(), 21, resourcesProvider);
            this.checkBox = checkBox2;
            checkBox2.setColor(-1, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
            checkBox2.setDrawUnchecked(false);
            checkBox2.setDrawBackgroundAsArc(3);
            addView(checkBox2, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388659, 33.0f, 25.0f, 0.0f, 0.0f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            this.imageReceiver.setImageCoords(LocaleController.isRTL ? getMeasuredWidth() - AndroidUtilities.dp(65.0f) : AndroidUtilities.dp(9.0f), AndroidUtilities.dp(11.33f), AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.imageReceiver.draw(canvas);
            super.onDraw(canvas);
            canvas.drawPath(this.arrowPath, this.arrowPaint);
            if (this.needDivider) {
                Paint themePaint = Theme.getThemePaint("paintDivider", this.resourcesProvider);
                if (themePaint == null) {
                    themePaint = Theme.dividerPaint;
                }
                canvas.drawRect(AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 78.0f), getMeasuredHeight() - 1, getWidth() - AndroidUtilities.dp(LocaleController.isRTL ? 78.0f : 0.0f), getMeasuredHeight(), themePaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0f) + (this.needDivider ? 1 : 0), 1073741824));
            this.arrowPaint.setStyle(Paint.Style.STROKE);
            this.arrowPaint.setStrokeCap(Paint.Cap.ROUND);
            this.arrowPaint.setStrokeJoin(Paint.Join.ROUND);
            this.arrowPaint.setStrokeWidth(AndroidUtilities.dpf2(1.66f));
            this.arrowPaint.setColor(Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider), 0.85f));
            this.arrowPath.rewind();
            float measuredHeight = getMeasuredHeight() / 2.0f;
            float dpf2 = LocaleController.isRTL ? AndroidUtilities.dpf2(29.66f) : getMeasuredWidth() - AndroidUtilities.dpf2(24.33f);
            this.arrowPath.moveTo(dpf2, measuredHeight - AndroidUtilities.dpf2(5.66f));
            this.arrowPath.lineTo(((LocaleController.isRTL ? -1 : 1) * AndroidUtilities.dpf2(5.33f)) + dpf2, measuredHeight);
            this.arrowPath.lineTo(dpf2, measuredHeight + AndroidUtilities.dpf2(5.66f));
        }

        public void set(QuickRepliesController.QuickReply quickReply, boolean z) {
            ImageReceiver imageReceiver;
            int dp;
            TLRPC.Document document;
            long j;
            String str;
            ImageLocation imageLocation;
            TLRPC.Photo photo;
            int i = UserConfig.selectedAccount;
            this.titleView.setText(MessagesController.getInstance(i).getPeerName(UserConfig.getInstance(i).getClientUserId()));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            MessageObject messageObject = quickReply.topMessage;
            if (messageObject != null) {
                spannableStringBuilder.append(Emoji.replaceEmoji(messageObject.messageText, this.textView.getPaint().getFontMetricsInt(), false));
            }
            if (quickReply.getMessagesCount() > 1) {
                spannableStringBuilder.append((CharSequence) "  ");
                int dp2 = AndroidUtilities.displaySize.x - AndroidUtilities.dp(80.0f);
                CharSequence of = MoreSpan.of(quickReply.getMessagesCount() - 1, this.spanWidth);
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(TextUtils.ellipsize(spannableStringBuilder, this.textView.getPaint(), (dp2 * 1.5f) - this.spanWidth[0], TextUtils.TruncateAt.END));
                if (spannableStringBuilder2.length() > 0 && spannableStringBuilder2.charAt(spannableStringBuilder2.length() - 1) == 8230) {
                    spannableStringBuilder2.append((CharSequence) "  ");
                }
                spannableStringBuilder2.append(of);
                spannableStringBuilder = spannableStringBuilder2;
            }
            this.textView.setText(spannableStringBuilder);
            TLRPC.MessageMedia media = MessageObject.getMedia(quickReply.topMessage);
            if (media != null && (photo = media.photo) != null) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(36.0f), true, null, true);
                ImageReceiver imageReceiver2 = this.imageReceiver;
                ImageLocation forObject = ImageLocation.getForObject(closestPhotoSizeWithSize, media.photo);
                MessageObject messageObject2 = quickReply.topMessage;
                imageReceiver2.setImage(forObject, "36_36", messageObject2.strippedThumb, closestPhotoSizeWithSize == null ? 0L : closestPhotoSizeWithSize.size, (String) null, messageObject2, 0);
            } else {
                if (media == null || (document = media.document) == null) {
                    this.avatarDrawable.setInfo(UserConfig.getInstance(i).getCurrentUser());
                    this.imageReceiver.setForUserOrChat(UserConfig.getInstance(i).getCurrentUser(), this.avatarDrawable);
                    imageReceiver = this.imageReceiver;
                    dp = AndroidUtilities.dp(56.0f);
                    imageReceiver.setRoundRadius(dp);
                    this.needDivider = z;
                    invalidate();
                }
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, AndroidUtilities.dp(36.0f), true, null, true);
                if (closestPhotoSizeWithSize2 == null) {
                    ImageLocation forDocument = ImageLocation.getForDocument(media.document);
                    j = media.document.size;
                    imageLocation = forDocument;
                    str = "g";
                } else {
                    ImageLocation forObject2 = ImageLocation.getForObject(closestPhotoSizeWithSize2, media.document);
                    j = closestPhotoSizeWithSize2.size;
                    str = "36_36";
                    imageLocation = forObject2;
                }
                long j2 = j;
                ImageReceiver imageReceiver3 = this.imageReceiver;
                MessageObject messageObject3 = quickReply.topMessage;
                imageReceiver3.setImage(imageLocation, str, messageObject3.strippedThumb, j2, (String) null, messageObject3, 0);
            }
            imageReceiver = this.imageReceiver;
            dp = AndroidUtilities.dp(6.0f);
            imageReceiver.setRoundRadius(dp);
            this.needDivider = z;
            invalidate();
        }

        public void setChecked(boolean z, boolean z2) {
            this.checkBox.setChecked(z, z2);
        }
    }

    public static class MoreSpan extends ReplacementSpan {
        private final Paint backgroundPaint = new Paint(1);
        private final Text text;

        public MoreSpan(int i) {
            this.text = new Text(LocaleController.formatPluralString("BusinessRepliesMore", i, new Object[0]), 9.33f, AndroidUtilities.bold());
        }

        public static CharSequence of(int i, int[] iArr) {
            SpannableString spannableString = new SpannableString("+");
            MoreSpan moreSpan = new MoreSpan(i);
            iArr[0] = moreSpan.getSize();
            spannableString.setSpan(moreSpan, 0, spannableString.length(), 33);
            return spannableString;
        }

        @Override
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            float dpf2 = AndroidUtilities.dpf2(14.66f);
            float f2 = (i3 + i5) / 2.0f;
            RectF rectF = AndroidUtilities.rectTmp;
            float f3 = dpf2 / 2.0f;
            rectF.set(f, f2 - f3, getSize() + f, f3 + f2);
            Paint paint2 = this.backgroundPaint;
            int i6 = Theme.key_windowBackgroundWhiteGrayText2;
            paint2.setColor(Theme.multAlpha(Theme.getColor(i6), 0.15f));
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.backgroundPaint);
            this.text.draw(canvas, f + AndroidUtilities.dp(5.0f), f2, Theme.getColor(i6), Utilities.clamp((paint.getAlpha() * 2) / 255.0f, 1.0f, 0.0f));
        }

        public int getSize() {
            return (int) (this.text.getCurrentWidth() + AndroidUtilities.dp(10.0f));
        }

        @Override
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            return getSize();
        }
    }

    public static class QuickReplyView extends FrameLayout {
        private final AvatarDrawable avatarDrawable;
        private final CheckBox2 checkBox;
        private final ImageReceiver imageReceiver;
        private boolean local;
        private boolean needDivider;
        private final ImageView orderView;
        private final Theme.ResourcesProvider resourcesProvider;
        private int[] spanWidth;
        private final TextView textView;

        public QuickReplyView(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.avatarDrawable = new AvatarDrawable();
            this.imageReceiver = new ImageReceiver(this);
            this.spanWidth = new int[1];
            this.resourcesProvider = resourcesProvider;
            setWillNotDraw(false);
            int i = z ? 42 : 16;
            SpoilersTextView spoilersTextView = new SpoilersTextView(context);
            this.textView = spoilersTextView;
            spoilersTextView.setLines(2);
            spoilersTextView.setEllipsize(TextUtils.TruncateAt.END);
            spoilersTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
            spoilersTextView.setTextSize(1, 14.0f);
            boolean z2 = LocaleController.isRTL;
            addView(spoilersTextView, LayoutHelper.createFrame(-1, -2.0f, 7, z2 ? i : 64.0f, 7.0f, z2 ? 64.0f : i, 0.0f));
            if (z) {
                ImageView imageView = new ImageView(context);
                this.orderView = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setImageResource(R.drawable.list_reorder);
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
                imageView.setAlpha(0.0f);
                addView(imageView, LayoutHelper.createFrame(50, 50, (LocaleController.isRTL ? 3 : 5) | 112));
            } else {
                this.orderView = null;
            }
            CheckBox2 checkBox2 = new CheckBox2(getContext(), 21, resourcesProvider);
            this.checkBox = checkBox2;
            checkBox2.setColor(-1, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
            checkBox2.setDrawUnchecked(false);
            checkBox2.setDrawBackgroundAsArc(3);
            addView(checkBox2, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388659, 33.0f, 25.0f, 0.0f, 0.0f));
        }

        public void invalidateEmojis() {
            this.textView.invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            this.imageReceiver.setImageCoords(LocaleController.isRTL ? getMeasuredWidth() - AndroidUtilities.dp(51.0f) : AndroidUtilities.dp(15.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
            this.imageReceiver.draw(canvas);
            super.onDraw(canvas);
            if (this.needDivider) {
                Paint themePaint = Theme.getThemePaint("paintDivider", this.resourcesProvider);
                if (themePaint == null) {
                    themePaint = Theme.dividerPaint;
                }
                canvas.drawRect(AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 64.0f), getMeasuredHeight() - 1, getWidth() - AndroidUtilities.dp(LocaleController.isRTL ? 64.0f : 0.0f), getMeasuredHeight(), themePaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), 1073741824));
        }

        public void set(QuickRepliesController.QuickReply quickReply, String str, boolean z) {
            ImageReceiver imageReceiver;
            int dp;
            TLRPC.WebPage webPage;
            TLRPC.Photo photo;
            ImageLocation forObject;
            BitmapDrawable bitmapDrawable;
            long j;
            long j2;
            String str2;
            ImageLocation imageLocation;
            TLRPC.WebPage webPage2;
            ImageReceiver imageReceiver2;
            TLRPC.Photo photo2;
            String str3 = str;
            this.local = quickReply != null ? quickReply.local : false;
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (str3 != null && str.length() > 0 && !str3.startsWith("/")) {
                str3 = "/" + str3;
            }
            spannableStringBuilder.append((CharSequence) "/").append((CharSequence) quickReply.name);
            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, spannableStringBuilder.length(), 33);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider)), 0, spannableStringBuilder.length(), 33);
            if (str3 != null) {
                spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2, this.resourcesProvider)), 0, Math.min(str3.length() <= 0 ? 1 : str3.length(), spannableStringBuilder.length()), 33);
            }
            if (quickReply.topMessage != null) {
                spannableStringBuilder.append((CharSequence) " ");
                CharSequence charSequence = quickReply.topMessage.caption;
                if (TextUtils.isEmpty(charSequence)) {
                    charSequence = quickReply.topMessage.messageText;
                }
                CharSequence replaceEmoji = Emoji.replaceEmoji(new SpannableStringBuilder(charSequence), this.textView.getPaint().getFontMetricsInt(), false);
                TLRPC.Message message = quickReply.topMessage.messageOwner;
                if (message != null) {
                    MessageObject.replaceAnimatedEmoji(replaceEmoji, message.entities, this.textView.getPaint().getFontMetricsInt());
                }
                spannableStringBuilder.append(replaceEmoji);
            }
            if (quickReply.getMessagesCount() > 1) {
                spannableStringBuilder.append((CharSequence) "  ");
                int dp2 = AndroidUtilities.displaySize.x - AndroidUtilities.dp(80.0f);
                CharSequence of = MoreSpan.of(quickReply.getMessagesCount() - 1, this.spanWidth);
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(TextUtils.ellipsize(spannableStringBuilder, this.textView.getPaint(), (dp2 * 1.5f) - this.spanWidth[0], TextUtils.TruncateAt.END));
                if (spannableStringBuilder2.length() > 0 && spannableStringBuilder2.charAt(spannableStringBuilder2.length() - 1) == 8230) {
                    spannableStringBuilder2.append((CharSequence) "  ");
                }
                spannableStringBuilder2.append(of);
                spannableStringBuilder = spannableStringBuilder2;
            }
            this.textView.setText(spannableStringBuilder);
            int i = UserConfig.selectedAccount;
            TLRPC.MessageMedia media = MessageObject.getMedia(quickReply.topMessage);
            if (media != null && (photo2 = media.photo) != null) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo2.sizes, AndroidUtilities.dp(36.0f), true, null, true);
                ImageReceiver imageReceiver3 = this.imageReceiver;
                forObject = ImageLocation.getForObject(closestPhotoSizeWithSize, media.photo);
                MessageObject messageObject = quickReply.topMessage;
                bitmapDrawable = messageObject.strippedThumb;
                j = closestPhotoSizeWithSize != null ? closestPhotoSizeWithSize.size : 0L;
                imageReceiver2 = imageReceiver3;
                webPage2 = messageObject;
            } else {
                if (media != null && media.document != null && (quickReply.topMessage.isVideo() || quickReply.topMessage.isSticker())) {
                    TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(media.document.thumbs, AndroidUtilities.dp(36.0f), true, null, true);
                    if (closestPhotoSizeWithSize2 == null) {
                        ImageLocation forDocument = ImageLocation.getForDocument(media.document);
                        j2 = media.document.size;
                        imageLocation = forDocument;
                        str2 = "g";
                    } else {
                        ImageLocation forObject2 = ImageLocation.getForObject(closestPhotoSizeWithSize2, media.document);
                        j2 = closestPhotoSizeWithSize2.size;
                        str2 = "36_36";
                        imageLocation = forObject2;
                    }
                    long j3 = j2;
                    ImageReceiver imageReceiver4 = this.imageReceiver;
                    MessageObject messageObject2 = quickReply.topMessage;
                    imageReceiver4.setImage(imageLocation, str2, messageObject2.strippedThumb, j3, (String) null, messageObject2, 0);
                    imageReceiver = this.imageReceiver;
                    dp = AndroidUtilities.dp(4.0f);
                    imageReceiver.setRoundRadius(dp);
                    this.needDivider = z;
                    invalidate();
                }
                if (media == null || (webPage = media.webpage) == null || (photo = webPage.photo) == null) {
                    this.avatarDrawable.setInfo(UserConfig.getInstance(i).getCurrentUser());
                    this.imageReceiver.setForUserOrChat(UserConfig.getInstance(i).getCurrentUser(), this.avatarDrawable);
                    imageReceiver = this.imageReceiver;
                    dp = AndroidUtilities.dp(36.0f);
                    imageReceiver.setRoundRadius(dp);
                    this.needDivider = z;
                    invalidate();
                }
                TLRPC.PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(36.0f), true, null, true);
                ImageReceiver imageReceiver5 = this.imageReceiver;
                forObject = ImageLocation.getForObject(closestPhotoSizeWithSize3, media.webpage.photo);
                bitmapDrawable = quickReply.topMessage.strippedThumb;
                j = closestPhotoSizeWithSize3 != null ? closestPhotoSizeWithSize3.size : 0L;
                imageReceiver2 = imageReceiver5;
                webPage2 = media.webpage;
            }
            imageReceiver2.setImage(forObject, "36_36", bitmapDrawable, j, (String) null, webPage2, 0);
            imageReceiver = this.imageReceiver;
            dp = AndroidUtilities.dp(4.0f);
            imageReceiver.setRoundRadius(dp);
            this.needDivider = z;
            invalidate();
        }

        public void setChecked(boolean z, boolean z2) {
            this.checkBox.setChecked(z, z2);
        }

        public void setReorder(boolean z) {
            this.orderView.animate().alpha((!z || this.local) ? 0.0f : 1.0f).start();
        }
    }

    public void clearSelection() {
        this.selected.clear();
        AndroidUtilities.forEachViews((RecyclerView) this.listView, new Consumer() {
            @Override
            public final void accept(Object obj) {
                QuickRepliesActivity.lambda$clearSelection$2((View) obj);
            }
        });
        this.actionBar.hideActionMode();
        this.listView.allowReorder(false);
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asTopView(LocaleController.getString(R.string.BusinessRepliesInfo), "RestrictedEmoji", "📝"));
        universalAdapter.whiteSectionStart();
        if (QuickRepliesController.getInstance(this.currentAccount).canAddNew()) {
            arrayList.add(UItem.asButton(1, R.drawable.msg_viewintopic, LocaleController.getString(R.string.BusinessRepliesAdd)).accent());
        }
        this.repliesOrderId = universalAdapter.reorderSectionStart();
        Iterator it = QuickRepliesController.getInstance(this.currentAccount).replies.iterator();
        while (it.hasNext()) {
            QuickRepliesController.QuickReply quickReply = (QuickRepliesController.QuickReply) it.next();
            arrayList.add(UItem.asQuickReply(quickReply).setChecked(this.selected.contains(Integer.valueOf(quickReply.id))));
        }
        universalAdapter.reorderSectionEnd();
        universalAdapter.whiteSectionEnd();
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.BusinessRepliesAddInfo)));
    }

    public static void lambda$clearSelection$2(View view) {
        if (view instanceof QuickReplyView) {
            ((QuickReplyView) view).setChecked(false, true);
        }
    }

    public static boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$onClick$1(String str) {
        Bundle bundle = new Bundle();
        bundle.putInt("chatMode", 5);
        bundle.putLong("user_id", getUserConfig().getClientUserId());
        bundle.putString("quick_reply", str);
        ChatActivity chatActivity = new ChatActivity(bundle);
        chatActivity.forceEmptyHistory();
        presentFragment(chatActivity);
    }

    public static void lambda$openRenameReplyAlert$10(EditTextBoldCursor editTextBoldCursor, Runnable runnable) {
        AndroidUtilities.hideKeyboard(editTextBoldCursor);
        AndroidUtilities.runOnUIThread(runnable, 80L);
    }

    public static void lambda$openRenameReplyAlert$12(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public static void lambda$openRenameReplyAlert$3(TextView textView, TextView textView2, ValueAnimator valueAnimator) {
        textView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        textView2.setAlpha(1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public static void lambda$openRenameReplyAlert$4(Runnable[] runnableArr, ValueAnimator[] valueAnimatorArr, final TextView textView, final TextView textView2, Boolean bool) {
        AndroidUtilities.cancelRunOnUIThread(runnableArr[0]);
        ValueAnimator valueAnimator = valueAnimatorArr[0];
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(textView.getAlpha(), bool.booleanValue() ? 1.0f : 0.0f);
        valueAnimatorArr[0] = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                QuickRepliesActivity.lambda$openRenameReplyAlert$3(textView, textView2, valueAnimator2);
            }
        });
        valueAnimatorArr[0].setDuration(320L);
        valueAnimatorArr[0].setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        valueAnimatorArr[0].start();
        if (bool.booleanValue()) {
            AndroidUtilities.runOnUIThread(runnableArr[0], 5320L);
        }
    }

    public static void lambda$openRenameReplyAlert$5(Utilities.Callback callback) {
        callback.run(Boolean.FALSE);
    }

    public static void lambda$openRenameReplyAlert$6(EditTextBoldCursor editTextBoldCursor, Utilities.Callback callback, int i, QuickRepliesController.QuickReply quickReply, TextView textView, Utilities.Callback callback2, AlertDialog alertDialog, int i2) {
        Boolean bool;
        String obj = editTextBoldCursor.getText().toString();
        if (obj.length() <= 0 || obj.length() > 32) {
            AndroidUtilities.shakeView(editTextBoldCursor);
            bool = Boolean.FALSE;
        } else {
            if (!QuickRepliesController.getInstance(i).isNameBusy(obj, quickReply == null ? -1 : quickReply.id)) {
                if (callback2 != null) {
                    callback2.run(obj);
                }
                alertDialog.dismiss();
                return;
            } else {
                AndroidUtilities.shakeView(editTextBoldCursor);
                textView.setText(LocaleController.getString(R.string.BusinessRepliesNameBusy));
                bool = Boolean.TRUE;
            }
        }
        callback.run(bool);
    }

    public static void lambda$openRenameReplyAlert$8(View view, DialogInterface dialogInterface) {
        currentDialog = null;
        if (view != null) {
            view.requestFocus();
        }
    }

    public static void lambda$openRenameReplyAlert$9(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem.id == 1) {
            openRenameReplyAlert(getContext(), this.currentAccount, null, null, getResourceProvider(), false, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    QuickRepliesActivity.this.lambda$onClick$1((String) obj);
                }
            });
            return;
        }
        if (uItem.viewType == 16 && (uItem.object instanceof QuickRepliesController.QuickReply)) {
            if (!this.selected.isEmpty()) {
                updateSelect(uItem, view);
                return;
            }
            QuickRepliesController.QuickReply quickReply = (QuickRepliesController.QuickReply) uItem.object;
            if (quickReply.local) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("chatMode", 5);
            bundle.putLong("user_id", getUserConfig().getClientUserId());
            bundle.putString("quick_reply", quickReply.name);
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setQuickReplyId(quickReply.id);
            presentFragment(chatActivity);
        }
    }

    public boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
        if (uItem.viewType != 16) {
            return false;
        }
        Object obj = uItem.object;
        if ((obj instanceof QuickRepliesController.QuickReply) && ((QuickRepliesController.QuickReply) obj).local) {
            return false;
        }
        updateSelect(uItem, view);
        return true;
    }

    public static void openRenameReplyAlert(Context context, final int i, String str, final QuickRepliesController.QuickReply quickReply, final Theme.ResourcesProvider resourcesProvider, boolean z, final Utilities.Callback callback) {
        ?? r1;
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        Activity findActivity = AndroidUtilities.findActivity(context);
        final View currentFocus = findActivity != null ? findActivity.getCurrentFocus() : null;
        boolean z2 = lastFragment != null && (lastFragment.getFragmentView() instanceof SizeNotifierFrameLayout) && ((SizeNotifierFrameLayout) lastFragment.getFragmentView()).measureKeyboardHeight() > AndroidUtilities.dp(20.0f) && !z;
        final ?? r13 = new AlertDialog[1];
        ?? builder = z2 ? new AlertDialogDecor.Builder(context, resourcesProvider) : new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(LocaleController.getString((quickReply == null && str == null) ? R.string.BusinessRepliesNewTitle : R.string.BusinessRepliesEditTitle));
        final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
            AnimatedTextView.AnimatedTextDrawable limit;
            AnimatedColor limitColor = new AnimatedColor(this);
            private int limitCount;

            {
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
                this.limit = animatedTextDrawable;
                animatedTextDrawable.setAnimationProperties(0.2f, 0L, 160L, CubicBezierInterpolator.EASE_OUT_QUINT);
                this.limit.setTextSize(AndroidUtilities.dp(15.33f));
                this.limit.setCallback(this);
                this.limit.setGravity(5);
            }

            @Override
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                this.limit.setTextColor(this.limitColor.set(Theme.getColor(this.limitCount < 0 ? Theme.key_text_RedRegular : Theme.key_dialogSearchHint, resourcesProvider)));
                this.limit.setBounds(getScrollX(), 0, getScrollX() + getWidth(), getHeight());
                this.limit.draw(canvas);
            }

            @Override
            public void onMeasure(int i2, int i3) {
                super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), 1073741824));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                super.onTextChanged(charSequence, i2, i3, i4);
                if (this.limit != null) {
                    this.limitCount = 32 - charSequence.length();
                    this.limit.cancelAnimation();
                    AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.limit;
                    String str2 = "";
                    if (this.limitCount <= 4) {
                        str2 = "" + this.limitCount;
                    }
                    animatedTextDrawable.setText(str2);
                }
            }

            @Override
            protected boolean verifyDrawable(Drawable drawable) {
                return drawable == this.limit || super.verifyDrawable(drawable);
            }
        };
        MediaDataController.getInstance(i).fetchNewEmojiKeywords(AndroidUtilities.getCurrentKeyboardLanguage(), true);
        editTextBoldCursor.setTextSize(1, 18.0f);
        editTextBoldCursor.setText(quickReply == null ? str == null ? "" : str : quickReply.name);
        int i2 = Theme.key_dialogTextBlack;
        editTextBoldCursor.setTextColor(Theme.getColor(i2, resourcesProvider));
        editTextBoldCursor.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText, resourcesProvider));
        editTextBoldCursor.setHintText(LocaleController.getString(R.string.BusinessRepliesNamePlaceholder));
        editTextBoldCursor.setSingleLine(true);
        editTextBoldCursor.setFocusable(true);
        editTextBoldCursor.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField, resourcesProvider), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated, resourcesProvider), Theme.getColor(Theme.key_text_RedRegular, resourcesProvider));
        editTextBoldCursor.setImeOptions(6);
        editTextBoldCursor.setBackgroundDrawable(null);
        editTextBoldCursor.setPadding(0, 0, AndroidUtilities.dp(42.0f), 0);
        editTextBoldCursor.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i3, int i4, Spanned spanned, int i5, int i6) {
                return String.valueOf(charSequence).replaceAll("[^\\d_\\p{L}\\x{200c}\\x{00b7}\\x{0d80}-\\x{0dff}]", "");
            }
        }});
        ?? linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        final TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(i2, resourcesProvider));
        textView.setTextSize(1, 16.0f);
        textView.setText(LocaleController.getString((quickReply == null && str == null) ? R.string.BusinessRepliesNewMessage : R.string.BusinessRepliesEditMessage));
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2, 83));
        final TextView textView2 = new TextView(context);
        textView2.setTextColor(Theme.getColor(Theme.key_text_RedBold, resourcesProvider));
        textView2.setTextSize(1, 16.0f);
        textView2.setText(LocaleController.getString(R.string.BusinessRepliesNameBusy));
        textView2.setAlpha(0.0f);
        frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2, 83));
        final ValueAnimator[] valueAnimatorArr = new ValueAnimator[1];
        final Utilities.Callback callback2 = new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                QuickRepliesActivity.lambda$openRenameReplyAlert$4(r1, valueAnimatorArr, textView2, textView, (Boolean) obj);
            }
        };
        final Runnable[] runnableArr = {new Runnable() {
            @Override
            public final void run() {
                QuickRepliesActivity.lambda$openRenameReplyAlert$5(Utilities.Callback.this);
            }
        }};
        editTextBoldCursor.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (textView2.getAlpha() > 0.0f) {
                    AndroidUtilities.cancelRunOnUIThread(runnableArr[0]);
                    AndroidUtilities.runOnUIThread(runnableArr[0]);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i3, int i4, int i5) {
            }
        });
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 24.0f, 5.0f, 24.0f, 12.0f));
        linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, -2, 24.0f, 0.0f, 24.0f, 10.0f));
        builder.setView(linearLayout);
        builder.setWidth(AndroidUtilities.dp(292.0f));
        final View view = currentFocus;
        editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView3, int i3, KeyEvent keyEvent) {
                if (i3 != 6) {
                    return false;
                }
                String obj = EditTextBoldCursor.this.getText().toString();
                if (obj.length() <= 0 || obj.length() > 32) {
                    AndroidUtilities.shakeView(EditTextBoldCursor.this);
                    return true;
                }
                QuickRepliesController quickRepliesController = QuickRepliesController.getInstance(i);
                QuickRepliesController.QuickReply quickReply2 = quickReply;
                if (quickRepliesController.isNameBusy(obj, quickReply2 == null ? -1 : quickReply2.id)) {
                    AndroidUtilities.shakeView(EditTextBoldCursor.this);
                    textView2.setText(LocaleController.getString(R.string.BusinessRepliesNameBusy));
                    callback2.run(Boolean.TRUE);
                    return true;
                }
                Utilities.Callback callback3 = callback;
                if (callback3 != null) {
                    callback3.run(obj);
                }
                AlertDialog alertDialog = r13[0];
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                if (r13[0] == QuickRepliesActivity.currentDialog) {
                    AlertDialog unused = QuickRepliesActivity.currentDialog = null;
                }
                View view2 = view;
                if (view2 != null) {
                    view2.requestFocus();
                }
                return true;
            }
        });
        builder.setPositiveButton(LocaleController.getString(R.string.Done), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i3) {
                QuickRepliesActivity.lambda$openRenameReplyAlert$6(EditTextBoldCursor.this, callback2, i, quickReply, textView2, callback, alertDialog, i3);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i3) {
                alertDialog.dismiss();
            }
        });
        if (z2) {
            AlertDialog create = builder.create();
            currentDialog = create;
            r13[0] = create;
            create.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    QuickRepliesActivity.lambda$openRenameReplyAlert$8(currentFocus, dialogInterface);
                }
            });
            currentDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public final void onShow(DialogInterface dialogInterface) {
                    QuickRepliesActivity.lambda$openRenameReplyAlert$9(EditTextBoldCursor.this, dialogInterface);
                }
            });
            currentDialog.showDelayed(250L);
            r1 = 0;
        } else {
            builder.overrideDismissListener(new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    QuickRepliesActivity.lambda$openRenameReplyAlert$10(EditTextBoldCursor.this, (Runnable) obj);
                }
            });
            AlertDialog create2 = builder.create();
            r1 = 0;
            r13[0] = create2;
            create2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
                }
            });
            r13[0].setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public final void onShow(DialogInterface dialogInterface) {
                    QuickRepliesActivity.lambda$openRenameReplyAlert$12(EditTextBoldCursor.this, dialogInterface);
                }
            });
            r13[0].show();
        }
        r13[r1].setDismissDialogByButtons(r1);
        editTextBoldCursor.setSelection(editTextBoldCursor.getText().length());
    }

    private void updateEditItem() {
        boolean z = false;
        boolean z2 = this.selected.size() == 1;
        if (z2) {
            QuickRepliesController.QuickReply findReply = QuickRepliesController.getInstance(this.currentAccount).findReply(((Integer) this.selected.get(0)).intValue());
            if (findReply != null && !findReply.isSpecial()) {
                z = true;
            }
        } else {
            z = z2;
        }
        if (this.shownEditItem != z) {
            this.shownEditItem = z;
            this.editItem.animate().alpha(this.shownEditItem ? 1.0f : 0.0f).scaleX(this.shownEditItem ? 1.0f : 0.7f).scaleY(this.shownEditItem ? 1.0f : 0.7f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(340L).start();
        }
    }

    private void updateSelect(UItem uItem, View view) {
        QuickRepliesController.QuickReply quickReply = (QuickRepliesController.QuickReply) uItem.object;
        QuickReplyView quickReplyView = (QuickReplyView) view;
        if (this.selected.contains(Integer.valueOf(quickReply.id))) {
            this.selected.remove(Integer.valueOf(quickReply.id));
        } else {
            this.selected.add(Integer.valueOf(quickReply.id));
        }
        this.listView.allowReorder(!this.selected.isEmpty());
        boolean contains = this.selected.contains(Integer.valueOf(quickReply.id));
        uItem.checked = contains;
        quickReplyView.setChecked(contains, true);
        if (this.actionBar.isActionModeShowed() == this.selected.isEmpty()) {
            if (this.selected.isEmpty()) {
                this.actionBar.hideActionMode();
            } else {
                this.actionBar.showActionMode();
            }
        }
        this.countText.setNumber(Math.max(1, this.selected.size()), true);
        updateEditItem();
    }

    public void whenReordered(int i, ArrayList arrayList) {
        if (i == this.repliesOrderId) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (((UItem) arrayList.get(i2)).object instanceof QuickRepliesController.QuickReply) {
                    ((QuickRepliesController.QuickReply) ((UItem) arrayList.get(i2)).object).order = i2;
                }
            }
            QuickRepliesController.getInstance(this.currentAccount).reorder();
        }
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.BusinessReplies));
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(getContext());
        this.countText = numberTextView;
        numberTextView.setTextSize(18);
        this.countText.setTypeface(AndroidUtilities.bold());
        this.countText.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        createActionMode.addView(this.countText, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.countText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createView$0;
                lambda$createView$0 = QuickRepliesActivity.lambda$createView$0(view, motionEvent);
                return lambda$createView$0;
            }
        });
        ActionBarMenuItem addItem = createActionMode.addItem(1, R.drawable.msg_edit);
        this.editItem = addItem;
        addItem.setContentDescription(LocaleController.getString(R.string.Edit));
        ActionBarMenuItem addItem2 = createActionMode.addItem(2, R.drawable.msg_delete);
        this.deleteItem = addItem2;
        addItem2.setContentDescription(LocaleController.getString(R.string.Delete));
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824));
            }
        };
        sizeNotifierFrameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                QuickRepliesActivity.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                QuickRepliesActivity.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, new Utilities.Callback5Return() {
            @Override
            public final Object run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                boolean onLongClick;
                onLongClick = QuickRepliesActivity.this.onLongClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
                return Boolean.valueOf(onLongClick);
            }
        });
        this.listView = universalRecyclerView;
        universalRecyclerView.listenReorder(new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                QuickRepliesActivity.this.whenReordered(((Integer) obj).intValue(), (ArrayList) obj2);
            }
        });
        sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.fragmentView = sizeNotifierFrameLayout;
        return sizeNotifierFrameLayout;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalRecyclerView universalRecyclerView;
        UniversalAdapter universalAdapter;
        if (i != NotificationCenter.quickRepliesUpdated || (universalRecyclerView = this.listView) == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.quickRepliesUpdated);
        QuickRepliesController.getInstance(this.currentAccount).load();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.quickRepliesUpdated);
        super.onFragmentDestroy();
    }
}

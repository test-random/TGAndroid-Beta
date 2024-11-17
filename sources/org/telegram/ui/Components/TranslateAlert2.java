package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TranslateAlert2;

public abstract class TranslateAlert2 extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private static HashMap localesByCode;
    private PaddedAdapter adapter;
    private Boolean buttonShadowShown;
    private View buttonShadowView;
    private TextView buttonTextView;
    private FrameLayout buttonView;
    private boolean firstTranslation;
    private BaseFragment fragment;
    private String fromLanguage;
    private HeaderView headerView;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private LoadingTextView loadingTextView;
    private Utilities.CallbackReturn onLinkPress;
    private String prevToLanguage;
    private Integer reqId;
    private ArrayList reqMessageEntities;
    private int reqMessageId;
    private TLRPC.InputPeer reqPeer;
    private CharSequence reqText;
    private AnimatedFloat sheetTopAnimated;
    private boolean sheetTopNotAnimate;
    private LinkSpanDrawable.LinksTextView textView;
    private FrameLayout textViewContainer;
    private String toLanguage;

    private class ContainerView extends FrameLayout {
        private Paint bgPaint;
        private Path bgPath;
        private Boolean lightStatusBarFull;

        public ContainerView(Context context) {
            super(context);
            this.bgPath = new Path();
            Paint paint = new Paint(1);
            this.bgPaint = paint;
            paint.setColor(TranslateAlert2.this.getThemedColor(Theme.key_dialogBackground));
            Theme.applyDefaultShadow(this.bgPaint);
        }

        private void updateLightStatusBar(boolean z) {
            Boolean bool = this.lightStatusBarFull;
            if (bool == null || bool.booleanValue() != z) {
                this.lightStatusBarFull = Boolean.valueOf(z);
                AndroidUtilities.setLightStatusBar(TranslateAlert2.this.getWindow(), AndroidUtilities.computePerceivedBrightness(z ? TranslateAlert2.this.getThemedColor(Theme.key_dialogBackground) : Theme.blendOver(TranslateAlert2.this.getThemedColor(Theme.key_actionBarDefault), 855638016)) > 0.721f);
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            float sheetTop = TranslateAlert2.this.getSheetTop();
            float lerp = AndroidUtilities.lerp(0, AndroidUtilities.dp(12.0f), MathUtils.clamp(sheetTop / AndroidUtilities.dpf2(24.0f), 0.0f, 1.0f));
            TranslateAlert2.this.headerView.setTranslationY(Math.max(AndroidUtilities.statusBarHeight, sheetTop));
            updateLightStatusBar(sheetTop <= ((float) AndroidUtilities.statusBarHeight) / 2.0f);
            this.bgPath.rewind();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, sheetTop, getWidth(), getHeight() + lerp);
            this.bgPath.addRoundRect(rectF, lerp, lerp, Path.Direction.CW);
            canvas.drawPath(this.bgPath, this.bgPaint);
            super.dispatchDraw(canvas);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            Bulletin.addDelegate(this, new Bulletin.Delegate() {
                @Override
                public boolean allowLayoutChanges() {
                    return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
                }

                @Override
                public boolean bottomOffsetAnimated() {
                    return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
                }

                @Override
                public boolean clipWithGradient(int i) {
                    return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
                }

                @Override
                public int getBottomOffset(int i) {
                    return AndroidUtilities.dp(80.0f);
                }

                @Override
                public int getTopOffset(int i) {
                    return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
                }

                @Override
                public void onBottomOffsetChange(float f) {
                    Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
                }

                @Override
                public void onHide(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onHide(this, bulletin);
                }

                @Override
                public void onShow(Bulletin bulletin) {
                    Bulletin.Delegate.CC.$default$onShow(this, bulletin);
                }
            });
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Bulletin.removeDelegate(this);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824));
        }
    }

    public class HeaderView extends FrameLayout {
        private ImageView arrowView;
        private ImageView backButton;
        private View backgroundView;
        private TextView fromLanguageTextView;
        private View shadow;
        private LinearLayout subtitleView;
        private TextView titleTextView;
        private AnimatedTextView toLanguageTextView;

        public HeaderView(Context context) {
            super(context);
            View view = new View(context);
            this.backgroundView = view;
            view.setBackgroundColor(TranslateAlert2.this.getThemedColor(Theme.key_dialogBackground));
            addView(this.backgroundView, LayoutHelper.createFrame(-1, 44.0f, 55, 0.0f, 12.0f, 0.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.backButton = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.backButton.setImageResource(R.drawable.ic_ab_back);
            ImageView imageView2 = this.backButton;
            int i = Theme.key_dialogTextBlack;
            int themedColor = TranslateAlert2.this.getThemedColor(i);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            imageView2.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
            this.backButton.setBackground(Theme.createSelectorDrawable(TranslateAlert2.this.getThemedColor(Theme.key_listSelector)));
            this.backButton.setAlpha(0.0f);
            this.backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    TranslateAlert2.HeaderView.this.lambda$new$0(view2);
                }
            });
            addView(this.backButton, LayoutHelper.createFrame(54, 54.0f, 48, 1.0f, 1.0f, 1.0f, 1.0f));
            TextView textView = new TextView(context) {
                @Override
                protected void onMeasure(int i2, int i3) {
                    super.onMeasure(i2, i3);
                    if (LocaleController.isRTL) {
                        HeaderView.this.titleTextView.setPivotX(getMeasuredWidth());
                    }
                }
            };
            this.titleTextView = textView;
            textView.setTextColor(TranslateAlert2.this.getThemedColor(i));
            this.titleTextView.setTextSize(1, 20.0f);
            this.titleTextView.setTypeface(AndroidUtilities.bold());
            this.titleTextView.setText(LocaleController.getString(R.string.AutomaticTranslation));
            this.titleTextView.setPivotX(0.0f);
            this.titleTextView.setPivotY(0.0f);
            addView(this.titleTextView, LayoutHelper.createFrame(-1, -2.0f, 55, 22.0f, 20.0f, 22.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context) {
                @Override
                protected void onMeasure(int i2, int i3) {
                    super.onMeasure(i2, i3);
                    if (LocaleController.isRTL) {
                        HeaderView.this.subtitleView.setPivotX(getMeasuredWidth());
                    }
                }
            };
            this.subtitleView = linearLayout;
            if (LocaleController.isRTL) {
                linearLayout.setGravity(5);
            }
            this.subtitleView.setPivotX(0.0f);
            this.subtitleView.setPivotY(0.0f);
            if (!TextUtils.isEmpty(TranslateAlert2.this.fromLanguage) && !"und".equals(TranslateAlert2.this.fromLanguage)) {
                TextView textView2 = new TextView(context);
                this.fromLanguageTextView = textView2;
                textView2.setLines(1);
                this.fromLanguageTextView.setTextColor(TranslateAlert2.this.getThemedColor(Theme.key_player_actionBarSubtitle));
                this.fromLanguageTextView.setTextSize(1, 14.0f);
                this.fromLanguageTextView.setText(TranslateAlert2.capitalFirst(TranslateAlert2.languageName(TranslateAlert2.this.fromLanguage)));
                this.fromLanguageTextView.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(2.0f));
            }
            ImageView imageView3 = new ImageView(context);
            this.arrowView = imageView3;
            imageView3.setImageResource(R.drawable.search_arrow);
            ImageView imageView4 = this.arrowView;
            int i2 = Theme.key_player_actionBarSubtitle;
            imageView4.setColorFilter(new PorterDuffColorFilter(TranslateAlert2.this.getThemedColor(i2), mode));
            if (LocaleController.isRTL) {
                this.arrowView.setScaleX(-1.0f);
            }
            AnimatedTextView animatedTextView = new AnimatedTextView(context) {
                private Paint bgPaint = new Paint(1);
                private LinkSpanDrawable.LinkCollector links = new LinkSpanDrawable.LinkCollector();

                @Override
                public void onDraw(Canvas canvas) {
                    if (LocaleController.isRTL) {
                        AndroidUtilities.rectTmp.set(getWidth() - width(), (getHeight() - AndroidUtilities.dp(18.0f)) / 2.0f, getWidth(), (getHeight() + AndroidUtilities.dp(18.0f)) / 2.0f);
                    } else {
                        AndroidUtilities.rectTmp.set(0.0f, (getHeight() - AndroidUtilities.dp(18.0f)) / 2.0f, width(), (getHeight() + AndroidUtilities.dp(18.0f)) / 2.0f);
                    }
                    this.bgPaint.setColor(Theme.multAlpha(TranslateAlert2.this.getThemedColor(Theme.key_player_actionBarSubtitle), 0.1175f));
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.bgPaint);
                    if (this.links.draw(canvas)) {
                        invalidate();
                    }
                    super.onDraw(canvas);
                }

                @Override
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getAction() != 0) {
                        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                            if (motionEvent.getAction() == 1) {
                                performClick();
                            }
                            this.links.clear();
                            invalidate();
                        }
                        return super.onTouchEvent(motionEvent);
                    }
                    LinkSpanDrawable linkSpanDrawable = new LinkSpanDrawable(null, ((BottomSheet) TranslateAlert2.this).resourcesProvider, motionEvent.getX(), motionEvent.getY());
                    linkSpanDrawable.setColor(Theme.multAlpha(TranslateAlert2.this.getThemedColor(Theme.key_player_actionBarSubtitle), 0.1175f));
                    LinkPath obtainNewPath = linkSpanDrawable.obtainNewPath();
                    if (LocaleController.isRTL) {
                        AndroidUtilities.rectTmp.set(getWidth() - width(), (getHeight() - AndroidUtilities.dp(18.0f)) / 2.0f, getWidth(), (getHeight() + AndroidUtilities.dp(18.0f)) / 2.0f);
                    } else {
                        AndroidUtilities.rectTmp.set(0.0f, (getHeight() - AndroidUtilities.dp(18.0f)) / 2.0f, width(), (getHeight() + AndroidUtilities.dp(18.0f)) / 2.0f);
                    }
                    obtainNewPath.addRect(AndroidUtilities.rectTmp, Path.Direction.CW);
                    this.links.addLink(linkSpanDrawable);
                    invalidate();
                    return true;
                }
            };
            this.toLanguageTextView = animatedTextView;
            if (LocaleController.isRTL) {
                animatedTextView.setGravity(5);
            }
            this.toLanguageTextView.setAnimationProperties(0.25f, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.toLanguageTextView.setTextColor(TranslateAlert2.this.getThemedColor(i2));
            this.toLanguageTextView.setTextSize(AndroidUtilities.dp(14.0f));
            this.toLanguageTextView.setText(TranslateAlert2.capitalFirst(TranslateAlert2.languageName(TranslateAlert2.this.toLanguage)));
            this.toLanguageTextView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f));
            this.toLanguageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    TranslateAlert2.HeaderView.this.lambda$new$1(view2);
                }
            });
            if (LocaleController.isRTL) {
                this.subtitleView.addView(this.toLanguageTextView, LayoutHelper.createLinear(-2, -2, 16, 0, 0, this.fromLanguageTextView != null ? 3 : 0, 0));
                if (this.fromLanguageTextView != null) {
                    this.subtitleView.addView(this.arrowView, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 0, 0));
                    this.subtitleView.addView(this.fromLanguageTextView, LayoutHelper.createLinear(-2, -2, 16, 4, 0, 0, 0));
                }
            } else {
                TextView textView3 = this.fromLanguageTextView;
                if (textView3 != null) {
                    this.subtitleView.addView(textView3, LayoutHelper.createLinear(-2, -2, 16, 0, 0, 4, 0));
                    this.subtitleView.addView(this.arrowView, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 0, 0));
                }
                this.subtitleView.addView(this.toLanguageTextView, LayoutHelper.createLinear(-2, -2, 16, this.fromLanguageTextView != null ? 3 : 0, 0, 0, 0));
            }
            addView(this.subtitleView, LayoutHelper.createFrame(-1, -2.0f, 55, 22.0f, 43.0f, 22.0f, 0.0f));
            View view2 = new View(context);
            this.shadow = view2;
            view2.setBackgroundColor(TranslateAlert2.this.getThemedColor(Theme.key_dialogShadowLine));
            this.shadow.setAlpha(0.0f);
            addView(this.shadow, LayoutHelper.createFrame(-1, AndroidUtilities.getShadowHeight() / AndroidUtilities.dpf2(1.0f), 55, 0.0f, 56.0f, 0.0f, 0.0f));
        }

        public void lambda$new$0(View view) {
            TranslateAlert2.this.dismiss();
        }

        public void lambda$new$1(View view) {
            openLanguagesSelect();
        }

        public void lambda$openLanguagesSelect$2(Runnable[] runnableArr, LocaleController.LocaleInfo localeInfo, View view) {
            Runnable runnable = runnableArr[0];
            if (runnable != null) {
                runnable.run();
            }
            if (TextUtils.equals(TranslateAlert2.this.toLanguage, localeInfo.pluralLangCode)) {
                return;
            }
            if (TranslateAlert2.this.adapter.mMainView == TranslateAlert2.this.textViewContainer) {
                TranslateAlert2 translateAlert2 = TranslateAlert2.this;
                translateAlert2.prevToLanguage = translateAlert2.toLanguage;
            }
            this.toLanguageTextView.setText(TranslateAlert2.capitalFirst(TranslateAlert2.languageName(TranslateAlert2.this.toLanguage = localeInfo.pluralLangCode)));
            TranslateAlert2.this.adapter.updateMainView(TranslateAlert2.this.loadingTextView);
            TranslateAlert2.setToLanguage(TranslateAlert2.this.toLanguage);
            TranslateAlert2.this.translate();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0f), 1073741824));
        }

        public void openLanguagesSelect() {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext()) {
                @Override
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(Math.min((int) (AndroidUtilities.displaySize.y * 0.33f), View.MeasureSpec.getSize(i2)), 1073741824));
                }
            };
            Drawable mutate = ContextCompat.getDrawable(getContext(), R.drawable.popup_fixed_alert).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(TranslateAlert2.this.getThemedColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY));
            actionBarPopupWindowLayout.setBackground(mutate);
            final Runnable[] runnableArr = new Runnable[1];
            ArrayList<LocaleController.LocaleInfo> locales = TranslateController.getLocales();
            int i = 0;
            boolean z = true;
            while (i < locales.size()) {
                final LocaleController.LocaleInfo localeInfo = locales.get(i);
                if (!localeInfo.pluralLangCode.equals(TranslateAlert2.this.fromLanguage) && "remote".equals(localeInfo.pathToFile)) {
                    ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), 2, z, i == locales.size() - 1, ((BottomSheet) TranslateAlert2.this).resourcesProvider);
                    actionBarMenuSubItem.setText(TranslateAlert2.capitalFirst(TranslateAlert2.languageName(localeInfo.pluralLangCode)));
                    actionBarMenuSubItem.setChecked(TextUtils.equals(TranslateAlert2.this.toLanguage, localeInfo.pluralLangCode));
                    actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            TranslateAlert2.HeaderView.this.lambda$openLanguagesSelect$2(runnableArr, localeInfo, view);
                        }
                    });
                    actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
                    z = false;
                }
                i++;
            }
            final ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
            runnableArr[0] = new Runnable() {
                @Override
                public final void run() {
                    ActionBarPopupWindow.this.dismiss();
                }
            };
            actionBarPopupWindow.setPauseNotifications(true);
            actionBarPopupWindow.setDismissAnimationDuration(220);
            actionBarPopupWindow.setOutsideTouchable(true);
            actionBarPopupWindow.setClippingEnabled(true);
            actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
            actionBarPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            this.toLanguageTextView.getLocationInWindow(iArr);
            actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
            int measuredHeight = actionBarPopupWindowLayout.getMeasuredHeight();
            int i2 = iArr[1];
            actionBarPopupWindow.showAtLocation(((BottomSheet) TranslateAlert2.this).containerView, 51, iArr[0] - AndroidUtilities.dp(8.0f), ((float) i2) > (((float) AndroidUtilities.displaySize.y) * 0.9f) - ((float) measuredHeight) ? (i2 - measuredHeight) + AndroidUtilities.dp(8.0f) : (i2 + this.toLanguageTextView.getMeasuredHeight()) - AndroidUtilities.dp(8.0f));
        }

        @Override
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            float clamp = MathUtils.clamp((f - AndroidUtilities.statusBarHeight) / AndroidUtilities.dp(64.0f), 0.0f, 1.0f);
            if (!TranslateAlert2.this.hasEnoughHeight()) {
                clamp = 1.0f;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(clamp);
            this.titleTextView.setScaleX(AndroidUtilities.lerp(0.85f, 1.0f, interpolation));
            this.titleTextView.setScaleY(AndroidUtilities.lerp(0.85f, 1.0f, interpolation));
            this.titleTextView.setTranslationY(AndroidUtilities.lerp(AndroidUtilities.dpf2(-12.0f), 0.0f, interpolation));
            if (!LocaleController.isRTL) {
                this.titleTextView.setTranslationX(AndroidUtilities.lerp(AndroidUtilities.dpf2(50.0f), 0.0f, interpolation));
                this.subtitleView.setTranslationX(AndroidUtilities.lerp(AndroidUtilities.dpf2(50.0f), 0.0f, interpolation));
            }
            this.subtitleView.setTranslationY(AndroidUtilities.lerp(AndroidUtilities.dpf2(-22.0f), 0.0f, interpolation));
            this.backButton.setTranslationX(AndroidUtilities.lerp(0.0f, AndroidUtilities.dpf2(-25.0f), interpolation));
            float f2 = 1.0f - interpolation;
            this.backButton.setAlpha(f2);
            this.shadow.setTranslationY(AndroidUtilities.lerp(0.0f, AndroidUtilities.dpf2(22.0f), interpolation));
            this.shadow.setAlpha(f2);
        }
    }

    public class LoadingTextView extends TextView {
        private final LoadingDrawable loadingDrawable;
        private final LinkPath path;

        public LoadingTextView(Context context) {
            super(context);
            LinkPath linkPath = new LinkPath(true);
            this.path = linkPath;
            LoadingDrawable loadingDrawable = new LoadingDrawable();
            this.loadingDrawable = loadingDrawable;
            loadingDrawable.usePath(linkPath);
            loadingDrawable.setSpeed(0.65f);
            loadingDrawable.setRadiiDp(4.0f);
            setBackground(loadingDrawable);
        }

        private void updateDrawable() {
            LinkPath linkPath = this.path;
            if (linkPath == null || this.loadingDrawable == null) {
                return;
            }
            linkPath.rewind();
            if (getLayout() != null && getLayout().getText() != null) {
                this.path.setCurrentLayout(getLayout(), 0, getPaddingLeft(), getPaddingTop());
                getLayout().getSelectionPath(0, getLayout().getText().length(), this.path);
            }
            this.loadingDrawable.updateBounds();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.loadingDrawable.reset();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            updateDrawable();
        }

        @Override
        public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
            super.setText(charSequence, bufferType);
            updateDrawable();
        }

        @Override
        public void setTextColor(int i) {
            super.setTextColor(Theme.multAlpha(i, 0.2f));
            this.loadingDrawable.setColors(Theme.multAlpha(i, 0.03f), Theme.multAlpha(i, 0.175f), Theme.multAlpha(i, 0.2f), Theme.multAlpha(i, 0.45f));
        }
    }

    public static class PaddedAdapter extends RecyclerView.Adapter {
        private Context mContext;
        private View mMainView;
        private int mainViewType = 1;

        public PaddedAdapter(Context context, View view) {
            this.mContext = context;
            this.mMainView = view;
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            return this.mainViewType;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return i == 0 ? new RecyclerListView.Holder(new View(this.mContext) {
                @Override
                protected void onMeasure(int i2, int i3) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824), View.MeasureSpec.makeMeasureSpec((int) (AndroidUtilities.displaySize.y * 0.4f), 1073741824));
                }
            }) : new RecyclerListView.Holder(this.mMainView);
        }

        public void updateMainView(View view) {
            if (this.mMainView == view) {
                return;
            }
            this.mainViewType++;
            this.mMainView = view;
            notifyItemChanged(1);
        }
    }

    private TranslateAlert2(Context context, String str, String str2, CharSequence charSequence, ArrayList arrayList, TLRPC.InputPeer inputPeer, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        Drawable textSelectHandleLeft;
        Drawable textSelectHandleRight;
        this.firstTranslation = true;
        this.backgroundPaddingLeft = 0;
        fixNavigationBar();
        this.reqText = charSequence;
        this.reqPeer = inputPeer;
        this.reqMessageId = i;
        this.fromLanguage = str;
        this.toLanguage = str2;
        ContainerView containerView = new ContainerView(context);
        this.containerView = containerView;
        this.sheetTopAnimated = new AnimatedFloat(containerView, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
        LoadingTextView loadingTextView = new LoadingTextView(context);
        this.loadingTextView = loadingTextView;
        loadingTextView.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(6.0f));
        this.loadingTextView.setTextSize(1, SharedConfig.fontSize);
        LoadingTextView loadingTextView2 = this.loadingTextView;
        int i2 = Theme.key_dialogTextBlack;
        loadingTextView2.setTextColor(getThemedColor(i2));
        this.loadingTextView.setLinkTextColor(Theme.multAlpha(getThemedColor(i2), 0.2f));
        this.loadingTextView.setText(Emoji.replaceEmoji(charSequence == null ? "" : charSequence.toString(), this.loadingTextView.getPaint().getFontMetricsInt(), true));
        this.textViewContainer = new FrameLayout(context) {
            @Override
            protected void onMeasure(int i3, int i4) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3), 1073741824), i4);
            }
        };
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        this.textView = linksTextView;
        linksTextView.setDisablePaddingsOffsetY(true);
        this.textView.setPadding(AndroidUtilities.dp(22.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(6.0f));
        this.textView.setTextSize(1, SharedConfig.fontSize);
        this.textView.setTextColor(getThemedColor(i2));
        this.textView.setLinkTextColor(getThemedColor(Theme.key_chat_messageLinkIn));
        this.textView.setTextIsSelectable(true);
        this.textView.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
        int themedColor = getThemedColor(Theme.key_chat_TextSelectionCursor);
        try {
            if (Build.VERSION.SDK_INT >= 29 && !XiaomiUtilities.isMIUI()) {
                textSelectHandleLeft = this.textView.getTextSelectHandleLeft();
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
                textSelectHandleLeft.setColorFilter(themedColor, mode);
                this.textView.setTextSelectHandleLeft(textSelectHandleLeft);
                textSelectHandleRight = this.textView.getTextSelectHandleRight();
                textSelectHandleRight.setColorFilter(themedColor, mode);
                this.textView.setTextSelectHandleRight(textSelectHandleRight);
            }
        } catch (Exception unused) {
        }
        this.textViewContainer.addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || motionEvent.getY() >= TranslateAlert2.this.getSheetTop() - getTop()) {
                    return super.dispatchTouchEvent(motionEvent);
                }
                TranslateAlert2.this.dismiss();
                return true;
            }

            @Override
            protected boolean onRequestFocusInDescendants(int i3, android.graphics.Rect rect) {
                return true;
            }

            @Override
            public void requestChildFocus(View view, View view2) {
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setOverScrollMode(1);
        this.listView.setPadding(0, AndroidUtilities.statusBarHeight + AndroidUtilities.dp(56.0f), 0, AndroidUtilities.dp(80.0f));
        this.listView.setClipToPadding(true);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        PaddedAdapter paddedAdapter = new PaddedAdapter(context, this.loadingTextView);
        this.adapter = paddedAdapter;
        recyclerListView3.setAdapter(paddedAdapter);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i3) {
                if (i3 == 0) {
                    TranslateAlert2.this.sheetTopNotAnimate = false;
                }
                if ((i3 == 0 || i3 == 2) && TranslateAlert2.this.getSheetTop(false) > 0.0f && TranslateAlert2.this.getSheetTop(false) < AndroidUtilities.dp(96.0f) && TranslateAlert2.this.listView.canScrollVertically(1) && TranslateAlert2.this.hasEnoughHeight()) {
                    TranslateAlert2.this.sheetTopNotAnimate = true;
                    TranslateAlert2.this.listView.smoothScrollBy(0, (int) TranslateAlert2.this.getSheetTop(false));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                ((BottomSheet) TranslateAlert2.this).containerView.invalidate();
                TranslateAlert2 translateAlert2 = TranslateAlert2.this;
                translateAlert2.updateButtonShadow(translateAlert2.listView.canScrollVertically(1));
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() {
            @Override
            public void onChangeAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                ((BottomSheet) TranslateAlert2.this).containerView.invalidate();
            }

            @Override
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                ((BottomSheet) TranslateAlert2.this).containerView.invalidate();
            }
        };
        defaultItemAnimator.setDurations(180L);
        defaultItemAnimator.setInterpolator(new LinearInterpolator());
        this.listView.setItemAnimator(defaultItemAnimator);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -2, 80));
        HeaderView headerView = new HeaderView(context);
        this.headerView = headerView;
        this.containerView.addView(headerView, LayoutHelper.createFrame(-1, 78, 55));
        FrameLayout frameLayout = new FrameLayout(context);
        this.buttonView = frameLayout;
        frameLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        View view = new View(context);
        this.buttonShadowView = view;
        view.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.buttonShadowView.setAlpha(0.0f);
        this.buttonView.addView(this.buttonShadowView, LayoutHelper.createFrame(-1.0f, AndroidUtilities.getShadowHeight() / AndroidUtilities.dpf2(1.0f), 55));
        TextView textView = new TextView(context);
        this.buttonTextView = textView;
        textView.setLines(1);
        this.buttonTextView.setSingleLine(true);
        this.buttonTextView.setGravity(1);
        this.buttonTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.buttonTextView.setTypeface(AndroidUtilities.bold());
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setText(LocaleController.getString(R.string.CloseTranslation));
        this.buttonTextView.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_featuredStickers_addButton), 6.0f));
        this.buttonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                TranslateAlert2.this.lambda$new$0(view2);
            }
        });
        this.buttonView.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 87, 16.0f, 16.0f, 16.0f, 16.0f));
        this.containerView.addView(this.buttonView, LayoutHelper.createFrame(-1, -2, 87));
        translate();
    }

    public TranslateAlert2(Context context, String str, String str2, CharSequence charSequence, ArrayList arrayList, Theme.ResourcesProvider resourcesProvider) {
        this(context, str, str2, charSequence, arrayList, null, 0, resourcesProvider);
    }

    public static CharSequence capitalFirst(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() <= 0) {
            return null;
        }
        SpannableStringBuilder valueOf = charSequence instanceof SpannableStringBuilder ? (SpannableStringBuilder) charSequence : SpannableStringBuilder.valueOf(charSequence);
        valueOf.replace(0, 1, (CharSequence) valueOf.toString().substring(0, 1).toUpperCase());
        return valueOf;
    }

    public static String capitalFirst(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public float getSheetTop() {
        return getSheetTop(true);
    }

    public float getSheetTop(boolean z) {
        AnimatedFloat animatedFloat;
        float top = this.listView.getTop();
        if (this.listView.getChildCount() >= 1) {
            RecyclerListView recyclerListView = this.listView;
            top += Math.max(0, recyclerListView.getChildAt(recyclerListView.getChildCount() - 1).getTop());
        }
        float max = Math.max(0.0f, top - AndroidUtilities.dp(78.0f));
        if (!z || (animatedFloat = this.sheetTopAnimated) == null) {
            return max;
        }
        if (!this.listView.scrollingByUser && !this.sheetTopNotAnimate) {
            return animatedFloat.set(max);
        }
        animatedFloat.set(max, true);
        return max;
    }

    public static String getToLanguage() {
        return MessagesController.getGlobalMainSettings().getString("translate_to_language", LocaleController.getInstance().getCurrentLocale().getLanguage());
    }

    private static HashMap groupEmojiRanges(CharSequence charSequence) {
        ArrayList<Emoji.EmojiSpanRange> parseEmojis;
        HashMap hashMap = new HashMap();
        if (charSequence == null || (parseEmojis = Emoji.parseEmojis(charSequence)) == null) {
            return hashMap;
        }
        String charSequence2 = charSequence.toString();
        for (int i = 0; i < parseEmojis.size(); i++) {
            Emoji.EmojiSpanRange emojiSpanRange = parseEmojis.get(i);
            if (emojiSpanRange != null && emojiSpanRange.code != null) {
                String substring = charSequence2.substring(emojiSpanRange.start, emojiSpanRange.end);
                ArrayList arrayList = (ArrayList) hashMap.get(substring);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    hashMap.put(substring, arrayList);
                }
                arrayList.add(emojiSpanRange);
            }
        }
        return hashMap;
    }

    public boolean hasEnoughHeight() {
        float f = 0.0f;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            if (this.listView.getChildAdapterPosition(this.listView.getChildAt(i)) == 1) {
                f += r3.getHeight();
            }
        }
        return f >= ((float) ((this.listView.getHeight() - this.listView.getPaddingTop()) - this.listView.getPaddingBottom()));
    }

    public void lambda$new$0(View view) {
        dismiss();
    }

    public void lambda$translate$1(TLObject tLObject, TLRPC.TL_textWithEntities tL_textWithEntities) {
        this.reqId = null;
        if (tLObject instanceof TLRPC.TL_messages_translateResult) {
            TLRPC.TL_messages_translateResult tL_messages_translateResult = (TLRPC.TL_messages_translateResult) tLObject;
            if (!tL_messages_translateResult.result.isEmpty() && tL_messages_translateResult.result.get(0) != null && tL_messages_translateResult.result.get(0).text != null) {
                this.firstTranslation = false;
                TLRPC.TL_textWithEntities preprocess = preprocess(tL_textWithEntities, tL_messages_translateResult.result.get(0));
                SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(preprocess.text);
                MessageObject.addEntitiesToText(valueOf, preprocess.entities, false, true, false, false);
                this.textView.setText(preprocessText(valueOf));
                this.adapter.updateMainView(this.textViewContainer);
            }
        }
        if (this.firstTranslation) {
            dismiss();
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 1, LocaleController.getString(R.string.TranslationFailedAlert2));
            return;
        }
        BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createErrorBulletin(LocaleController.getString(R.string.TranslationFailedAlert2)).show();
        AnimatedTextView animatedTextView = this.headerView.toLanguageTextView;
        String str = this.prevToLanguage;
        this.toLanguage = str;
        animatedTextView.setText(languageName(str));
        this.adapter.updateMainView(this.textViewContainer);
    }

    public void lambda$translate$2(final TLRPC.TL_textWithEntities tL_textWithEntities, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                TranslateAlert2.this.lambda$translate$1(tLObject, tL_textWithEntities);
            }
        });
    }

    public static String languageName(String str) {
        return languageName(str, null);
    }

    public static String languageName(String str, boolean[] zArr) {
        if (str == null || str.equals("und") || str.equals("auto")) {
            return null;
        }
        String str2 = str.split("_")[0];
        if ("nb".equals(str2)) {
            str2 = "no";
        }
        if (zArr != null) {
            String string = LocaleController.getString("TranslateLanguage" + str2.toUpperCase());
            boolean z = (string == null || string.startsWith("LOC_ERR")) ? false : true;
            zArr[0] = z;
            if (z) {
                return string;
            }
        }
        String systemLanguageName = systemLanguageName(str);
        if (systemLanguageName == null) {
            systemLanguageName = systemLanguageName(str2);
        }
        if (systemLanguageName != null) {
            return systemLanguageName;
        }
        if ("no".equals(str)) {
            str = "nb";
        }
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        LocaleController.LocaleInfo builtinLanguageByPlural = LocaleController.getInstance().getBuiltinLanguageByPlural(str);
        if (builtinLanguageByPlural == null) {
            return null;
        }
        return (currentLocaleInfo == null || !"en".equals(currentLocaleInfo.pluralLangCode)) ? builtinLanguageByPlural.name : builtinLanguageByPlural.nameEnglish;
    }

    public static String languageNameCapital(String str) {
        String languageName = languageName(str);
        if (languageName == null) {
            return null;
        }
        return languageName.substring(0, 1).toUpperCase() + languageName.substring(1);
    }

    public static TLRPC.TL_textWithEntities preprocess(TLRPC.TL_textWithEntities tL_textWithEntities, TLRPC.TL_textWithEntities tL_textWithEntities2) {
        Emoji.EmojiSpanRange emojiSpanRange;
        ArrayList<TLRPC.MessageEntity> arrayList;
        TLRPC.MessageEntity tL_messageEntityMention;
        if (tL_textWithEntities2 == null || tL_textWithEntities2.text == null) {
            return null;
        }
        for (int i = 0; i < tL_textWithEntities2.entities.size(); i++) {
            TLRPC.MessageEntity messageEntity = tL_textWithEntities2.entities.get(i);
            if (messageEntity instanceof TLRPC.TL_messageEntityTextUrl) {
                if (messageEntity.url != null) {
                    String str = tL_textWithEntities2.text;
                    int i2 = messageEntity.offset;
                    String substring = str.substring(i2, messageEntity.length + i2);
                    if (TextUtils.equals(substring, messageEntity.url)) {
                        tL_messageEntityMention = new TLRPC.TL_messageEntityUrl();
                    } else if (messageEntity.url.startsWith("https://t.me/") && substring.startsWith("@") && TextUtils.equals(substring.substring(1), messageEntity.url.substring(13))) {
                        tL_messageEntityMention = new TLRPC.TL_messageEntityMention();
                    }
                    tL_messageEntityMention.offset = messageEntity.offset;
                    tL_messageEntityMention.length = messageEntity.length;
                    tL_textWithEntities2.entities.set(i, tL_messageEntityMention);
                }
            } else if ((messageEntity instanceof TLRPC.TL_messageEntityPre) && tL_textWithEntities != null && (arrayList = tL_textWithEntities.entities) != null && i < arrayList.size() && (tL_textWithEntities.entities.get(i) instanceof TLRPC.TL_messageEntityPre)) {
                messageEntity.language = tL_textWithEntities.entities.get(i).language;
            }
        }
        if (tL_textWithEntities != null && tL_textWithEntities.text != null && !tL_textWithEntities.entities.isEmpty()) {
            HashMap groupEmojiRanges = groupEmojiRanges(tL_textWithEntities.text);
            HashMap groupEmojiRanges2 = groupEmojiRanges(tL_textWithEntities2.text);
            for (int i3 = 0; i3 < tL_textWithEntities.entities.size(); i3++) {
                TLRPC.MessageEntity messageEntity2 = tL_textWithEntities.entities.get(i3);
                if (messageEntity2 instanceof TLRPC.TL_messageEntityCustomEmoji) {
                    String str2 = tL_textWithEntities.text;
                    int i4 = messageEntity2.offset;
                    String substring2 = str2.substring(i4, messageEntity2.length + i4);
                    if (!TextUtils.isEmpty(substring2)) {
                        ArrayList arrayList2 = (ArrayList) groupEmojiRanges.get(substring2);
                        ArrayList arrayList3 = (ArrayList) groupEmojiRanges2.get(substring2);
                        if (arrayList2 != null && arrayList3 != null) {
                            int i5 = 0;
                            while (true) {
                                if (i5 >= arrayList2.size()) {
                                    i5 = -1;
                                    break;
                                }
                                Emoji.EmojiSpanRange emojiSpanRange2 = (Emoji.EmojiSpanRange) arrayList2.get(i5);
                                int i6 = emojiSpanRange2.start;
                                int i7 = messageEntity2.offset;
                                if (i6 == i7 && emojiSpanRange2.end == i7 + messageEntity2.length) {
                                    break;
                                }
                                i5++;
                            }
                            if (i5 >= 0 && i5 < arrayList3.size() && (emojiSpanRange = (Emoji.EmojiSpanRange) arrayList3.get(i5)) != null) {
                                int i8 = 0;
                                while (true) {
                                    if (i8 >= tL_textWithEntities2.entities.size()) {
                                        TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji = new TLRPC.TL_messageEntityCustomEmoji();
                                        TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji2 = (TLRPC.TL_messageEntityCustomEmoji) messageEntity2;
                                        tL_messageEntityCustomEmoji.document_id = tL_messageEntityCustomEmoji2.document_id;
                                        tL_messageEntityCustomEmoji.document = tL_messageEntityCustomEmoji2.document;
                                        int i9 = emojiSpanRange.start;
                                        tL_messageEntityCustomEmoji.offset = i9;
                                        tL_messageEntityCustomEmoji.length = emojiSpanRange.end - i9;
                                        tL_textWithEntities2.entities.add(tL_messageEntityCustomEmoji);
                                        break;
                                    }
                                    TLRPC.MessageEntity messageEntity3 = tL_textWithEntities2.entities.get(i8);
                                    if (messageEntity3 instanceof TLRPC.TL_messageEntityCustomEmoji) {
                                        int i10 = emojiSpanRange.start;
                                        int i11 = emojiSpanRange.end;
                                        int i12 = messageEntity3.offset;
                                        if (AndroidUtilities.intersect1d(i10, i11, i12, messageEntity3.length + i12)) {
                                            break;
                                        }
                                    }
                                    i8++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return tL_textWithEntities2;
    }

    private CharSequence preprocessText(CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        if (this.onLinkPress != null || this.fragment != null) {
            for (final URLSpan uRLSpan : (URLSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class)) {
                int spanStart = spannableStringBuilder.getSpanStart(uRLSpan);
                int spanEnd = spannableStringBuilder.getSpanEnd(uRLSpan);
                if (spanStart != -1 && spanEnd != -1) {
                    spannableStringBuilder.removeSpan(uRLSpan);
                    spannableStringBuilder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            if (TranslateAlert2.this.onLinkPress != null) {
                                if (((Boolean) TranslateAlert2.this.onLinkPress.run(uRLSpan)).booleanValue()) {
                                    TranslateAlert2.this.dismiss();
                                }
                            } else if (TranslateAlert2.this.fragment != null) {
                                AlertsCreator.showOpenUrlAlert(TranslateAlert2.this.fragment, uRLSpan.getURL(), false, false);
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint textPaint) {
                            int min = Math.min(textPaint.getAlpha(), (textPaint.getColor() >> 24) & 255);
                            if (!(uRLSpan instanceof URLSpanNoUnderline)) {
                                textPaint.setUnderlineText(true);
                            }
                            textPaint.setColor(Theme.getColor(Theme.key_dialogTextLink));
                            textPaint.setAlpha(min);
                        }
                    }, spanStart, spanEnd, 33);
                }
            }
        }
        return Emoji.replaceEmoji(spannableStringBuilder, this.textView.getPaint().getFontMetricsInt(), true);
    }

    public static void setToLanguage(String str) {
        MessagesController.getGlobalMainSettings().edit().putString("translate_to_language", str).apply();
    }

    public static TranslateAlert2 showAlert(Context context, BaseFragment baseFragment, int i, String str, String str2, CharSequence charSequence, ArrayList arrayList, boolean z, Utilities.CallbackReturn callbackReturn, final Runnable runnable) {
        if (context == null) {
            return null;
        }
        TranslateAlert2 translateAlert2 = new TranslateAlert2(context, str, str2, charSequence, arrayList, null) {
            @Override
            public void dismiss() {
                super.dismiss();
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        };
        translateAlert2.setNoforwards(z);
        translateAlert2.setFragment(baseFragment);
        translateAlert2.setOnLinkPress(callbackReturn);
        if (baseFragment == null) {
            translateAlert2.show();
        } else if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(translateAlert2);
        }
        return translateAlert2;
    }

    public static TranslateAlert2 showAlert(Context context, BaseFragment baseFragment, int i, TLRPC.InputPeer inputPeer, int i2, String str, String str2, CharSequence charSequence, ArrayList arrayList, boolean z, Utilities.CallbackReturn callbackReturn, final Runnable runnable) {
        TranslateAlert2 translateAlert2 = new TranslateAlert2(context, str, str2, charSequence, arrayList, inputPeer, i2, null) {
            @Override
            public void dismiss() {
                super.dismiss();
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        };
        translateAlert2.setNoforwards(z);
        translateAlert2.setFragment(baseFragment);
        translateAlert2.setOnLinkPress(callbackReturn);
        if (baseFragment == null) {
            translateAlert2.show();
        } else if (baseFragment.getParentActivity() != null) {
            baseFragment.showDialog(translateAlert2);
        }
        return translateAlert2;
    }

    public static String systemLanguageName(String str) {
        return systemLanguageName(str, false);
    }

    public static String systemLanguageName(String str, boolean z) {
        if (str == null) {
            return null;
        }
        if (localesByCode == null) {
            localesByCode = new HashMap();
            try {
                Locale[] availableLocales = Locale.getAvailableLocales();
                for (int i = 0; i < availableLocales.length; i++) {
                    localesByCode.put(availableLocales[i].getLanguage(), availableLocales[i]);
                    String country = availableLocales[i].getCountry();
                    if (country != null && country.length() > 0) {
                        localesByCode.put(availableLocales[i].getLanguage() + "-" + country.toLowerCase(), availableLocales[i]);
                    }
                }
            } catch (Exception unused) {
            }
        }
        String lowerCase = str.replace("_", "-").toLowerCase();
        try {
            Locale locale = (Locale) localesByCode.get(lowerCase);
            if (locale != null) {
                String displayLanguage = locale.getDisplayLanguage(z ? locale : Locale.getDefault());
                if (!lowerCase.contains("-")) {
                    return displayLanguage;
                }
                String displayCountry = locale.getDisplayCountry(z ? locale : Locale.getDefault());
                if (TextUtils.isEmpty(displayCountry)) {
                    return displayLanguage;
                }
                return displayLanguage + " (" + displayCountry + ")";
            }
        } catch (Exception unused2) {
        }
        return null;
    }

    public void updateButtonShadow(boolean z) {
        Boolean bool = this.buttonShadowShown;
        if (bool == null || bool.booleanValue() != z) {
            this.buttonShadowShown = Boolean.valueOf(z);
            this.buttonShadowView.animate().cancel();
            this.buttonShadowView.animate().alpha(z ? 1.0f : 0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(320L).start();
        }
    }

    @Override
    public boolean canDismissWithSwipe() {
        return false;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            this.loadingTextView.invalidate();
            this.textView.invalidate();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override
    public void dismissInternal() {
        if (this.reqId != null) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId.intValue(), true);
            this.reqId = null;
        }
        super.dismissInternal();
    }

    public void setFragment(BaseFragment baseFragment) {
        this.fragment = baseFragment;
    }

    public void setNoforwards(boolean z) {
        LinkSpanDrawable.LinksTextView linksTextView = this.textView;
        if (linksTextView != null) {
            linksTextView.setTextIsSelectable(!z);
        }
        if (z) {
            getWindow().addFlags(8192);
        } else {
            getWindow().clearFlags(8192);
        }
    }

    public void setOnLinkPress(Utilities.CallbackReturn callbackReturn) {
        this.onLinkPress = callbackReturn;
    }

    @Override
    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    public void translate() {
        if (this.reqId != null) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId.intValue(), true);
            this.reqId = null;
        }
        TLRPC.TL_messages_translateText tL_messages_translateText = new TLRPC.TL_messages_translateText();
        final TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
        CharSequence charSequence = this.reqText;
        tL_textWithEntities.text = charSequence == null ? "" : charSequence.toString();
        ArrayList<TLRPC.MessageEntity> arrayList = this.reqMessageEntities;
        if (arrayList != null) {
            tL_textWithEntities.entities = arrayList;
        }
        TLRPC.InputPeer inputPeer = this.reqPeer;
        if (inputPeer != null) {
            tL_messages_translateText.flags = 1 | tL_messages_translateText.flags;
            tL_messages_translateText.peer = inputPeer;
            tL_messages_translateText.id.add(Integer.valueOf(this.reqMessageId));
        } else {
            tL_messages_translateText.flags |= 2;
            tL_messages_translateText.text.add(tL_textWithEntities);
        }
        String str = this.toLanguage;
        if (str != null) {
            str = str.split("_")[0];
        }
        if ("nb".equals(str)) {
            str = "no";
        }
        tL_messages_translateText.to_lang = str;
        this.reqId = Integer.valueOf(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_translateText, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TranslateAlert2.this.lambda$translate$2(tL_textWithEntities, tLObject, tL_error);
            }
        }));
    }
}

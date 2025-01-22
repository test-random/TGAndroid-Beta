package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EditTextCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.ReportBottomSheet;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class ReportBottomSheet extends BottomSheet {
    private final Paint backgroundPaint;
    private final long dialogId;
    private Listener listener;
    private final ArrayList messageIds;
    private final boolean sponsored;
    private final byte[] sponsoredId;
    private final boolean stories;
    private final ViewPagerFixed viewPager;

    public class AnonymousClass3 implements Listener {
        final BulletinFactory val$bulletinFactory;
        final boolean[] val$done;
        final Utilities.Callback val$whenDone;

        AnonymousClass3(boolean[] zArr, Utilities.Callback callback, BulletinFactory bulletinFactory) {
            this.val$done = zArr;
            this.val$whenDone = callback;
            this.val$bulletinFactory = bulletinFactory;
        }

        public static void lambda$onReported$0(BulletinFactory bulletinFactory) {
            if (LaunchActivity.getSafeLastFragment() == null) {
                return;
            }
            if (bulletinFactory == null) {
                bulletinFactory = BulletinFactory.of(LaunchActivity.getSafeLastFragment());
            }
            if (bulletinFactory == null) {
                return;
            }
            bulletinFactory.createSimpleBulletin(R.raw.msg_antispam, LocaleController.getString(R.string.ReportChatSent), LocaleController.getString(R.string.Reported2)).setDuration(5000).show();
        }

        @Override
        public void onHidden() {
            Listener.CC.$default$onHidden(this);
        }

        @Override
        public void onPremiumRequired() {
            Listener.CC.$default$onPremiumRequired(this);
        }

        @Override
        public void onReported() {
            Utilities.Callback callback;
            boolean[] zArr = this.val$done;
            if (!zArr[0] && (callback = this.val$whenDone) != null) {
                zArr[0] = true;
                callback.run(Boolean.TRUE);
            }
            final BulletinFactory bulletinFactory = this.val$bulletinFactory;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ReportBottomSheet.AnonymousClass3.lambda$onReported$0(BulletinFactory.this);
                }
            }, 200L);
        }
    }

    public class AnonymousClass4 implements Listener {
        final Context val$context;
        final ChatActivity val$fragment;
        final MessageObject val$message;
        final Theme.ResourcesProvider val$resourceProvider;

        AnonymousClass4(ChatActivity chatActivity, Context context, Theme.ResourcesProvider resourcesProvider, MessageObject messageObject) {
            this.val$fragment = chatActivity;
            this.val$context = context;
            this.val$resourceProvider = resourcesProvider;
            this.val$message = messageObject;
        }

        public static void lambda$onHidden$2(ChatActivity chatActivity, MessageObject messageObject) {
            BulletinFactory.of(chatActivity).createAdReportedBulletin(LocaleController.getString(R.string.AdHidden)).show();
            chatActivity.removeFromSponsored(messageObject);
            chatActivity.removeMessageWithThanos(messageObject);
        }

        public static void lambda$onReported$1(ChatActivity chatActivity, final Context context, Theme.ResourcesProvider resourcesProvider, MessageObject messageObject) {
            BulletinFactory.of(chatActivity).createAdReportedBulletin(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.AdReported), -1, 2, new Runnable() {
                @Override
                public final void run() {
                    Browser.openUrl(context, "https://promote.telegram.org/guidelines");
                }
            }, resourcesProvider)).show();
            chatActivity.removeFromSponsored(messageObject);
            chatActivity.removeMessageWithThanos(messageObject);
        }

        @Override
        public void onHidden() {
            final ChatActivity chatActivity = this.val$fragment;
            final MessageObject messageObject = this.val$message;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ReportBottomSheet.AnonymousClass4.lambda$onHidden$2(ChatActivity.this, messageObject);
                }
            }, 200L);
        }

        @Override
        public void onPremiumRequired() {
            this.val$fragment.showDialog(new PremiumFeatureBottomSheet(this.val$fragment, 3, true));
        }

        @Override
        public void onReported() {
            final ChatActivity chatActivity = this.val$fragment;
            final Context context = this.val$context;
            final Theme.ResourcesProvider resourcesProvider = this.val$resourceProvider;
            final MessageObject messageObject = this.val$message;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ReportBottomSheet.AnonymousClass4.lambda$onReported$1(ChatActivity.this, context, resourcesProvider, messageObject);
                }
            }, 200L);
        }
    }

    private class ContainerView extends FrameLayout {
        private final AnimatedFloat isActionBar;
        private final Path path;
        private Boolean statusBarOpen;
        private float top;

        public ContainerView(Context context) {
            super(context);
            this.isActionBar = new AnimatedFloat(this, 250L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.path = new Path();
        }

        private void updateLightStatusBar(boolean z) {
            Boolean bool = this.statusBarOpen;
            if (bool == null || bool.booleanValue() != z) {
                boolean z2 = AndroidUtilities.computePerceivedBrightness(ReportBottomSheet.this.getThemedColor(Theme.key_dialogBackground)) > 0.721f;
                boolean z3 = AndroidUtilities.computePerceivedBrightness(Theme.blendOver(ReportBottomSheet.this.getThemedColor(Theme.key_actionBarDefault), 855638016)) > 0.721f;
                this.statusBarOpen = Boolean.valueOf(z);
                if (!z) {
                    z2 = z3;
                }
                AndroidUtilities.setLightStatusBar(ReportBottomSheet.this.getWindow(), z2);
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            View[] viewPages = ReportBottomSheet.this.viewPager.getViewPages();
            this.top = 0.0f;
            for (View view : viewPages) {
                if (view != null) {
                    Page page = (Page) view;
                    this.top += page.top() * Utilities.clamp(1.0f - Math.abs(page.getTranslationX() / page.getMeasuredWidth()), 1.0f, 0.0f);
                    if (page.getVisibility() == 0) {
                        page.updateTops();
                    }
                }
            }
            float f = this.isActionBar.set(this.top <= ((float) AndroidUtilities.statusBarHeight) ? 1.0f : 0.0f);
            float f2 = AndroidUtilities.statusBarHeight;
            float f3 = f2 * f;
            this.top = Math.max(f2, this.top) - (AndroidUtilities.statusBarHeight * f);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(((BottomSheet) ReportBottomSheet.this).backgroundPaddingLeft, this.top, getWidth() - ((BottomSheet) ReportBottomSheet.this).backgroundPaddingLeft, getHeight() + AndroidUtilities.dp(8.0f));
            float lerp = AndroidUtilities.lerp(AndroidUtilities.dp(14.0f), 0, f);
            canvas.drawRoundRect(rectF, lerp, lerp, ReportBottomSheet.this.backgroundPaint);
            canvas.save();
            this.path.rewind();
            this.path.addRoundRect(rectF, lerp, lerp, Path.Direction.CW);
            canvas.clipPath(this.path);
            super.dispatchDraw(canvas);
            canvas.restore();
            updateLightStatusBar(f3 > ((float) AndroidUtilities.statusBarHeight) / 2.0f);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0 || motionEvent.getY() >= this.top) {
                return super.dispatchTouchEvent(motionEvent);
            }
            ReportBottomSheet.this.lambda$new$0();
            return true;
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            return super.drawChild(canvas, view, j);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824));
        }
    }

    public interface Listener {

        public abstract class CC {
            public static void $default$onHidden(Listener listener) {
            }

            public static void $default$onPremiumRequired(Listener listener) {
            }
        }

        void onHidden();

        void onPremiumRequired();

        void onReported();
    }

    public class Page extends FrameLayout {
        private ButtonWithCounterView button;
        private FrameLayout buttonContainer;
        TLRPC.TL_reportResultAddComment commentOption;
        private final FrameLayout contentView;
        private EditTextCell editTextCell;
        private final BigHeaderCell headerView;
        private final UniversalRecyclerView listView;
        TLRPC.TL_reportResultChooseOption option;
        int pageType;
        TLRPC.TL_channels_sponsoredMessageReportResultChooseOption sponsoredOption;

        public class BigHeaderCell extends FrameLayout {
            public BackDrawable backDrawable;
            private final ImageView btnBack;
            private Runnable onBackClickListener;
            private final TextView textView;

            public BigHeaderCell(Context context, Theme.ResourcesProvider resourcesProvider) {
                super(context);
                TextView textView = new TextView(context);
                this.textView = textView;
                textView.setTypeface(AndroidUtilities.bold());
                textView.setTextSize(1, 20.0f);
                textView.setGravity(LocaleController.isRTL ? 5 : 3);
                textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
                addView(textView);
                ImageView imageView = new ImageView(context);
                this.btnBack = imageView;
                BackDrawable backDrawable = new BackDrawable(false);
                this.backDrawable = backDrawable;
                imageView.setImageDrawable(backDrawable);
                this.backDrawable.setColor(-1);
                addView(imageView, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, 16.0f, 16.0f, 16.0f, 0.0f));
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        ReportBottomSheet.Page.BigHeaderCell.this.lambda$new$0(view);
                    }
                });
                setCloseImageVisible(true);
                setMinimumHeight(AndroidUtilities.dp(56.0f));
            }

            public void lambda$new$0(View view) {
                Runnable runnable = this.onBackClickListener;
                if (runnable != null) {
                    runnable.run();
                }
            }

            public CharSequence getText() {
                return this.textView.getText();
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
            }

            public void setCloseImageVisible(boolean z) {
                this.btnBack.setVisibility(z ? 0 : 8);
                TextView textView = this.textView;
                boolean z2 = LocaleController.isRTL;
                textView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, 55, (z2 || !z) ? 22.0f : 53.0f, 14.0f, (z2 && z) ? 53.0f : 22.0f, 12.0f));
            }

            public void setOnBackClickListener(Runnable runnable) {
                this.onBackClickListener = runnable;
            }

            public void setText(CharSequence charSequence) {
                this.textView.setText(charSequence);
            }
        }

        public Page(Context context) {
            super(context);
            FrameLayout frameLayout = new FrameLayout(context);
            this.contentView = frameLayout;
            frameLayout.setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
            frameLayout.setClipToPadding(true);
            addView(frameLayout, LayoutHelper.createFrame(-1, -1, 119));
            BigHeaderCell bigHeaderCell = new BigHeaderCell(context, ((BottomSheet) ReportBottomSheet.this).resourcesProvider);
            this.headerView = bigHeaderCell;
            bigHeaderCell.setOnBackClickListener(new Runnable() {
                @Override
                public final void run() {
                    ReportBottomSheet.Page.this.lambda$new$0();
                }
            });
            bigHeaderCell.setText(LocaleController.getString(ReportBottomSheet.this.sponsored ? R.string.ReportAd : ReportBottomSheet.this.stories ? R.string.ReportStory : R.string.Report2));
            bigHeaderCell.backDrawable.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, ((BottomSheet) ReportBottomSheet.this).resourcesProvider));
            bigHeaderCell.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, ((BottomSheet) ReportBottomSheet.this).resourcesProvider));
            addView(bigHeaderCell, LayoutHelper.createFrame(-1, -2, 55));
            UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(context, ((BottomSheet) ReportBottomSheet.this).currentAccount, 0, true, new Utilities.Callback2() {
                @Override
                public final void run(Object obj, Object obj2) {
                    ReportBottomSheet.Page.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
                }
            }, new Utilities.Callback5() {
                @Override
                public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    ReportBottomSheet.Page.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
                }
            }, null, ((BottomSheet) ReportBottomSheet.this).resourcesProvider);
            this.listView = universalRecyclerView;
            universalRecyclerView.setClipToPadding(false);
            universalRecyclerView.layoutManager.setReverseLayout(true);
            universalRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    Page.this.contentView.invalidate();
                    ((BottomSheet) ReportBottomSheet.this).containerView.invalidate();
                }
            });
            frameLayout.addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1.0f));
        }

        public void lambda$fillItems$2(View view) {
            if (!this.button.isEnabled() || this.button.isLoading()) {
                return;
            }
            this.button.setLoading(true);
            ReportBottomSheet.this.submitOption(this.headerView.getText(), this.commentOption.option, this.editTextCell.getText().toString());
        }

        public void lambda$new$0() {
            if (this.pageType == 0) {
                ReportBottomSheet.this.lambda$new$0();
            } else {
                ReportBottomSheet.this.onBackPressed();
            }
        }

        public void lambda$setOption$1() {
            AndroidUtilities.showKeyboard(this.editTextCell.editText);
        }

        public void onClick(UItem uItem, View view, int i, float f, float f2) {
            if (uItem.viewType == 30) {
                TLRPC.TL_channels_sponsoredMessageReportResultChooseOption tL_channels_sponsoredMessageReportResultChooseOption = this.sponsoredOption;
                if (tL_channels_sponsoredMessageReportResultChooseOption != null) {
                    TLRPC.TL_sponsoredMessageReportOption tL_sponsoredMessageReportOption = tL_channels_sponsoredMessageReportResultChooseOption.options.get(uItem.id);
                    if (tL_sponsoredMessageReportOption != null) {
                        ReportBottomSheet.this.submitOption(tL_sponsoredMessageReportOption.text, tL_sponsoredMessageReportOption.option, null);
                        return;
                    }
                    return;
                }
                TLRPC.TL_reportResultChooseOption tL_reportResultChooseOption = this.option;
                if (tL_reportResultChooseOption != null) {
                    TLRPC.TL_messageReportOption tL_messageReportOption = tL_reportResultChooseOption.options.get(uItem.id);
                    if (tL_messageReportOption != null) {
                        ReportBottomSheet.this.submitOption(tL_messageReportOption.text, tL_messageReportOption.option, null);
                        return;
                    }
                    return;
                }
                TLRPC.TL_reportResultAddComment tL_reportResultAddComment = this.commentOption;
                if (tL_reportResultAddComment == null) {
                    ReportBottomSheet.this.submitOption(uItem.text, null, null);
                    return;
                }
                byte[] bArr = tL_reportResultAddComment.option;
                if (bArr != null) {
                    ReportBottomSheet.this.submitOption(null, bArr, null);
                }
            }
        }

        public boolean atTop() {
            return !this.listView.canScrollVertically(-1);
        }

        public void bind(int i) {
            this.pageType = i;
            this.headerView.setCloseImageVisible(i != 0);
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView != null) {
                universalRecyclerView.adapter.update(true);
            }
        }

        public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
            String str;
            if (this.headerView.getMeasuredHeight() <= 0) {
                this.headerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(120.0f), Integer.MIN_VALUE));
            }
            UItem asSpace = UItem.asSpace(this.headerView.getMeasuredHeight());
            asSpace.id = -1;
            asSpace.transparent = true;
            arrayList.add(asSpace);
            int measuredHeight = (int) (0 + (this.headerView.getMeasuredHeight() / AndroidUtilities.density));
            TLRPC.TL_channels_sponsoredMessageReportResultChooseOption tL_channels_sponsoredMessageReportResultChooseOption = this.sponsoredOption;
            if (tL_channels_sponsoredMessageReportResultChooseOption != null || this.option != null || this.commentOption != null) {
                if (tL_channels_sponsoredMessageReportResultChooseOption != null || this.option != null) {
                    HeaderCell headerCell = new HeaderCell(getContext(), Theme.key_windowBackgroundWhiteBlueHeader, 21, 0, 0, false, ((BottomSheet) ReportBottomSheet.this).resourcesProvider);
                    TLRPC.TL_channels_sponsoredMessageReportResultChooseOption tL_channels_sponsoredMessageReportResultChooseOption2 = this.sponsoredOption;
                    if (tL_channels_sponsoredMessageReportResultChooseOption2 != null) {
                        str = tL_channels_sponsoredMessageReportResultChooseOption2.title;
                    } else {
                        TLRPC.TL_reportResultChooseOption tL_reportResultChooseOption = this.option;
                        if (tL_reportResultChooseOption != null) {
                            str = tL_reportResultChooseOption.title;
                        }
                        headerCell.setBackgroundColor(ReportBottomSheet.this.getThemedColor(Theme.key_dialogBackground));
                        UItem asCustom = UItem.asCustom(headerCell);
                        asCustom.id = -2;
                        arrayList.add(asCustom);
                        measuredHeight += 40;
                    }
                    headerCell.setText(str);
                    headerCell.setBackgroundColor(ReportBottomSheet.this.getThemedColor(Theme.key_dialogBackground));
                    UItem asCustom2 = UItem.asCustom(headerCell);
                    asCustom2.id = -2;
                    arrayList.add(asCustom2);
                    measuredHeight += 40;
                }
                if (this.sponsoredOption != null) {
                    for (int i = 0; i < this.sponsoredOption.options.size(); i++) {
                        UItem uItem = new UItem(30, false);
                        uItem.text = this.sponsoredOption.options.get(i).text;
                        uItem.iconResId = R.drawable.msg_arrowright;
                        uItem.id = i;
                        arrayList.add(uItem);
                        measuredHeight += 50;
                    }
                } else if (this.option != null) {
                    for (int i2 = 0; i2 < this.option.options.size(); i2++) {
                        UItem uItem2 = new UItem(30, false);
                        uItem2.text = this.option.options.get(i2).text;
                        uItem2.iconResId = R.drawable.msg_arrowright;
                        uItem2.id = i2;
                        arrayList.add(uItem2);
                        measuredHeight += 50;
                    }
                } else if (this.commentOption != null) {
                    if (this.editTextCell == null) {
                        EditTextCell editTextCell = new EditTextCell(getContext(), "", true, false, 1024, ((BottomSheet) ReportBottomSheet.this).resourcesProvider) {
                            @Override
                            public void onTextChanged(CharSequence charSequence) {
                                super.onTextChanged(charSequence);
                                if (Page.this.button != null) {
                                    ButtonWithCounterView buttonWithCounterView = Page.this.button;
                                    Page page = Page.this;
                                    buttonWithCounterView.setEnabled(page.commentOption.optional || !TextUtils.isEmpty(page.editTextCell.getText()));
                                }
                            }
                        };
                        this.editTextCell = editTextCell;
                        editTextCell.setShowLimitWhenNear(100);
                    }
                    this.editTextCell.editText.setHint(LocaleController.getString(this.commentOption.optional ? R.string.Report2CommentOptional : R.string.Report2Comment));
                    UItem asCustom3 = UItem.asCustom(this.editTextCell);
                    asCustom3.id = -3;
                    arrayList.add(asCustom3);
                    arrayList.add(UItem.asShadow(LocaleController.getString((ReportBottomSheet.this.messageIds == null || ReportBottomSheet.this.messageIds.isEmpty()) ? DialogObject.isUserDialog(ReportBottomSheet.this.dialogId) ? R.string.Report2CommentInfoUser : ChatObject.isChannelAndNotMegaGroup(MessagesController.getInstance(((BottomSheet) ReportBottomSheet.this).currentAccount).getChat(Long.valueOf(-ReportBottomSheet.this.dialogId))) ? R.string.Report2CommentInfoChannel : R.string.Report2CommentInfoGroup : ReportBottomSheet.this.messageIds.size() > 1 ? R.string.Report2CommentInfoMany : R.string.Report2CommentInfo)));
                    if (this.buttonContainer == null) {
                        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(getContext(), ((BottomSheet) ReportBottomSheet.this).resourcesProvider);
                        this.button = buttonWithCounterView;
                        buttonWithCounterView.setText(LocaleController.getString(R.string.Report2Send), false);
                        FrameLayout frameLayout = new FrameLayout(getContext());
                        this.buttonContainer = frameLayout;
                        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, ((BottomSheet) ReportBottomSheet.this).resourcesProvider));
                        this.buttonContainer.addView(this.button, LayoutHelper.createFrame(-1, 48.0f, 119, 12.0f, 12.0f, 12.0f, 12.0f));
                        View view = new View(getContext());
                        view.setBackgroundColor(Theme.getColor(Theme.key_divider, ((BottomSheet) ReportBottomSheet.this).resourcesProvider));
                        this.buttonContainer.addView(view, LayoutHelper.createFrame(-1.0f, 1.0f / AndroidUtilities.density, 48));
                    }
                    this.button.setEnabled(this.commentOption.optional || !TextUtils.isEmpty(this.editTextCell.getText()));
                    this.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view2) {
                            ReportBottomSheet.Page.this.lambda$fillItems$2(view2);
                        }
                    });
                    UItem asCustom4 = UItem.asCustom(this.buttonContainer);
                    asCustom4.id = -4;
                    arrayList.add(asCustom4);
                    measuredHeight += 112;
                }
                ((UItem) arrayList.get(arrayList.size() - 1)).hideDivider = true;
                if (ReportBottomSheet.this.sponsored && this.pageType == 0) {
                    FrameLayout frameLayout2 = new FrameLayout(getContext());
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(ReportBottomSheet.this.getThemedColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(getContext(), R.drawable.greydivider, Theme.getColor(Theme.key_windowBackgroundGrayShadow, ((BottomSheet) ReportBottomSheet.this).resourcesProvider)), 0, 0);
                    combinedDrawable.setFullsize(true);
                    frameLayout2.setBackground(combinedDrawable);
                    LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(getContext());
                    linksTextView.setTextSize(1, 14.0f);
                    linksTextView.setText(AndroidUtilities.replaceLinks(LocaleController.getString(R.string.ReportAdLearnMore), ((BottomSheet) ReportBottomSheet.this).resourcesProvider));
                    linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3, ((BottomSheet) ReportBottomSheet.this).resourcesProvider));
                    linksTextView.setGravity(17);
                    frameLayout2.addView(linksTextView, LayoutHelper.createFrame(-1, -2.0f, 17, 16.0f, 16.0f, 16.0f, 16.0f));
                    UItem asCustom5 = UItem.asCustom(frameLayout2);
                    asCustom5.id = -3;
                    arrayList.add(asCustom5);
                    measuredHeight += 46;
                }
            }
            if (this.listView != null) {
                if (((BottomSheet) ReportBottomSheet.this).containerView.getMeasuredHeight() - AndroidUtilities.statusBarHeight < AndroidUtilities.dp(measuredHeight)) {
                    this.listView.layoutManager.setReverseLayout(false);
                } else {
                    Collections.reverse(arrayList);
                    this.listView.layoutManager.setReverseLayout(true);
                }
            }
        }

        public void setHeaderText(CharSequence charSequence) {
            this.headerView.setText(charSequence);
            this.headerView.getText();
            this.headerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(120.0f), Integer.MIN_VALUE));
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView != null) {
                universalRecyclerView.adapter.update(true);
            }
        }

        public void setOption(TLRPC.TL_channels_sponsoredMessageReportResultChooseOption tL_channels_sponsoredMessageReportResultChooseOption) {
            this.sponsoredOption = tL_channels_sponsoredMessageReportResultChooseOption;
            this.option = null;
            this.commentOption = null;
            this.listView.adapter.update(false);
        }

        public void setOption(TLRPC.TL_reportResultAddComment tL_reportResultAddComment) {
            this.sponsoredOption = null;
            this.option = null;
            this.commentOption = tL_reportResultAddComment;
            this.listView.adapter.update(false);
            if (this.editTextCell != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ReportBottomSheet.Page.this.lambda$setOption$1();
                    }
                }, 120L);
            }
        }

        public void setOption(TLRPC.TL_reportResultChooseOption tL_reportResultChooseOption) {
            this.sponsoredOption = null;
            this.option = tL_reportResultChooseOption;
            this.commentOption = null;
            this.listView.adapter.update(false);
        }

        public float top() {
            UItem item;
            float paddingTop = this.contentView.getPaddingTop();
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                View childAt = this.listView.getChildAt(i);
                int position = this.listView.layoutManager.getPosition(childAt);
                if (position >= 0 && position < this.listView.adapter.getItemCount() && (item = this.listView.adapter.getItem(position)) != null && item.viewType == 28) {
                    paddingTop = this.contentView.getPaddingTop() + childAt.getY();
                }
            }
            return paddingTop;
        }

        public void updateTops() {
            float f = -this.headerView.getHeight();
            int i = 0;
            while (true) {
                if (i >= this.listView.getChildCount()) {
                    break;
                }
                View childAt = this.listView.getChildAt(i);
                if (this.listView.adapter.getItem(this.listView.layoutManager.getPosition(childAt)).viewType == 28) {
                    f = this.contentView.getPaddingTop() + childAt.getY();
                    break;
                }
                i++;
            }
            this.headerView.setTranslationY(Math.max(AndroidUtilities.statusBarHeight, f));
        }
    }

    public ReportBottomSheet(Context context, Theme.ResourcesProvider resourcesProvider, long j, byte[] bArr) {
        this(true, context, resourcesProvider, j, false, null, bArr);
    }

    public ReportBottomSheet(Context context, Theme.ResourcesProvider resourcesProvider, boolean z, long j, ArrayList arrayList) {
        this(false, context, resourcesProvider, j, z, arrayList, null);
    }

    public ReportBottomSheet(boolean z, final Context context, Theme.ResourcesProvider resourcesProvider, long j, boolean z2, ArrayList arrayList, byte[] bArr) {
        super(context, true, resourcesProvider);
        Paint paint = new Paint(1);
        this.backgroundPaint = paint;
        this.sponsored = z;
        this.messageIds = arrayList;
        this.stories = z2;
        this.sponsoredId = bArr;
        this.dialogId = j;
        int i = Theme.key_dialogBackground;
        paint.setColor(Theme.getColor(i, resourcesProvider));
        fixNavigationBar(Theme.getColor(i, resourcesProvider));
        this.smoothKeyboardAnimationEnabled = true;
        this.smoothKeyboardByBottom = true;
        this.containerView = new ContainerView(context);
        ViewPagerFixed viewPagerFixed = new ViewPagerFixed(context) {
            @Override
            protected boolean canScrollForward(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onStartTracking() {
                if (getCurrentView() instanceof Page) {
                    Page page = (Page) getCurrentView();
                    if (page.editTextCell != null) {
                        AndroidUtilities.hideKeyboard(page.editTextCell);
                    }
                }
            }

            @Override
            public void onTabAnimationUpdate(boolean z3) {
                super.onTabAnimationUpdate(z3);
                ((BottomSheet) ReportBottomSheet.this).containerView.invalidate();
            }
        };
        this.viewPager = viewPagerFixed;
        int i2 = this.backgroundPaddingLeft;
        viewPagerFixed.setPadding(i2, 0, i2, 0);
        this.containerView.addView(viewPagerFixed, LayoutHelper.createFrame(-1, -1, 119));
        viewPagerFixed.setAdapter(new ViewPagerFixed.Adapter() {
            @Override
            public void bindView(View view, int i3, int i4) {
                ((Page) view).bind(i4);
            }

            @Override
            public View createView(int i3) {
                return new Page(context);
            }

            @Override
            public int getItemCount() {
                return 5;
            }

            @Override
            public int getItemViewType(int i3) {
                return i3 == 0 ? 0 : 1;
            }
        });
        if (arrayList == null && bArr == null) {
            if (z) {
                setReportChooseOption((TLRPC.TL_channels_sponsoredMessageReportResultChooseOption) null);
            } else {
                setReportChooseOption((TLRPC.TL_reportResultChooseOption) null);
            }
        }
    }

    public static void continueReport(ChatActivity chatActivity, byte[] bArr, ArrayList arrayList, Utilities.Callback callback) {
        if (chatActivity == null) {
            return;
        }
        int currentAccount = chatActivity.getCurrentAccount();
        Context context = chatActivity.getContext();
        long dialogId = chatActivity.getDialogId();
        if (context == null) {
            return;
        }
        open(currentAccount, context, dialogId, false, arrayList, BulletinFactory.of(chatActivity), chatActivity.getResourceProvider(), bArr, callback);
    }

    public static void lambda$open$5(boolean[] zArr, Utilities.Callback callback) {
        if (zArr[0] || callback == null) {
            return;
        }
        zArr[0] = true;
        callback.run(Boolean.FALSE);
    }

    public static void lambda$open$6(Context context, Theme.ResourcesProvider resourcesProvider, boolean z, long j, ArrayList arrayList, TLObject tLObject, final boolean[] zArr, final Utilities.Callback callback, BulletinFactory bulletinFactory) {
        ReportBottomSheet reportBottomSheet = new ReportBottomSheet(context, resourcesProvider, z, j, arrayList);
        if (tLObject instanceof TLRPC.TL_reportResultChooseOption) {
            reportBottomSheet.setReportChooseOption((TLRPC.TL_reportResultChooseOption) tLObject);
        } else if (tLObject instanceof TLRPC.TL_reportResultAddComment) {
            reportBottomSheet.setReportChooseOption((TLRPC.TL_reportResultAddComment) tLObject);
        }
        reportBottomSheet.setListener(new AnonymousClass3(zArr, callback, bulletinFactory));
        reportBottomSheet.setOnDismissListener(new Runnable() {
            @Override
            public final void run() {
                ReportBottomSheet.lambda$open$5(zArr, callback);
            }
        });
        reportBottomSheet.show();
    }

    public static void lambda$open$7(boolean[] zArr, Utilities.Callback callback, BulletinFactory bulletinFactory) {
        if (!zArr[0] && callback != null) {
            zArr[0] = true;
            callback.run(Boolean.TRUE);
        }
        if (LaunchActivity.getSafeLastFragment() == null) {
            return;
        }
        if (bulletinFactory == null) {
            bulletinFactory = BulletinFactory.of(LaunchActivity.getSafeLastFragment());
        }
        if (bulletinFactory == null) {
            return;
        }
        bulletinFactory.createSimpleBulletin(R.raw.msg_antispam, LocaleController.getString(R.string.ReportChatSent), LocaleController.getString(R.string.Reported2)).setDuration(5000).show();
    }

    public static void lambda$open$8(final Context context, final Theme.ResourcesProvider resourcesProvider, final boolean z, final long j, final ArrayList arrayList, final boolean[] zArr, final Utilities.Callback callback, final BulletinFactory bulletinFactory, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            if ((tLObject instanceof TLRPC.TL_reportResultChooseOption) || (tLObject instanceof TLRPC.TL_reportResultAddComment)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ReportBottomSheet.lambda$open$6(context, resourcesProvider, z, j, arrayList, tLObject, zArr, callback, bulletinFactory);
                    }
                });
            } else if (tLObject instanceof TLRPC.TL_reportResultReported) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ReportBottomSheet.lambda$open$7(zArr, callback, bulletinFactory);
                    }
                }, 200L);
            }
        }
    }

    public static void lambda$openSponsored$11(ChatActivity chatActivity, final Context context, Theme.ResourcesProvider resourcesProvider, MessageObject messageObject) {
        BulletinFactory.of(chatActivity).createAdReportedBulletin(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.AdReported), -1, 2, new Runnable() {
            @Override
            public final void run() {
                Browser.openUrl(context, "https://promote.telegram.org/guidelines");
            }
        }, resourcesProvider)).show();
        chatActivity.removeFromSponsored(messageObject);
        chatActivity.removeMessageWithThanos(messageObject);
    }

    public static void lambda$openSponsored$12(ChatActivity chatActivity, int i, MessageObject messageObject) {
        BulletinFactory.of(chatActivity).createAdReportedBulletin(LocaleController.getString(R.string.AdHidden)).show();
        MessagesController.getInstance(i).disableAds(false);
        chatActivity.removeFromSponsored(messageObject);
        chatActivity.removeMessageWithThanos(messageObject);
    }

    public static void lambda$openSponsored$14(ChatActivity chatActivity, final Context context, Theme.ResourcesProvider resourcesProvider, MessageObject messageObject) {
        BulletinFactory.of(chatActivity).createAdReportedBulletin(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.AdReported), -1, 2, new Runnable() {
            @Override
            public final void run() {
                Browser.openUrl(context, "https://promote.telegram.org/guidelines");
            }
        }, resourcesProvider)).show();
        chatActivity.removeFromSponsored(messageObject);
        chatActivity.removeMessageWithThanos(messageObject);
    }

    public static void lambda$openSponsored$15(final Context context, final Theme.ResourcesProvider resourcesProvider, final long j, final byte[] bArr, final ChatActivity chatActivity, final MessageObject messageObject, final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
        Runnable runnable;
        if (tLObject != null) {
            if (tLObject instanceof TLRPC.TL_channels_sponsoredMessageReportResultChooseOption) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ReportBottomSheet.lambda$openSponsored$9(TLObject.this, context, resourcesProvider, j, bArr, chatActivity, messageObject);
                    }
                });
                return;
            } else if (tLObject instanceof TLRPC.TL_channels_sponsoredMessageReportResultReported) {
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        ReportBottomSheet.lambda$openSponsored$11(ChatActivity.this, context, resourcesProvider, messageObject);
                    }
                };
            } else if (!(tLObject instanceof TLRPC.TL_channels_sponsoredMessageReportResultAdsHidden)) {
                return;
            } else {
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        ReportBottomSheet.lambda$openSponsored$12(ChatActivity.this, i, messageObject);
                    }
                };
            }
        } else if (tL_error == null || !"AD_EXPIRED".equalsIgnoreCase(tL_error.text)) {
            return;
        } else {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    ReportBottomSheet.lambda$openSponsored$14(ChatActivity.this, context, resourcesProvider, messageObject);
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable, 200L);
    }

    public static void lambda$openSponsored$9(TLObject tLObject, Context context, Theme.ResourcesProvider resourcesProvider, long j, byte[] bArr, ChatActivity chatActivity, MessageObject messageObject) {
        new ReportBottomSheet(context, resourcesProvider, j, bArr).setReportChooseOption((TLRPC.TL_channels_sponsoredMessageReportResultChooseOption) tLObject).setListener(new AnonymousClass4(chatActivity, context, resourcesProvider, messageObject)).show();
    }

    public static void lambda$setReportChooseOption$0(View[] viewArr, TLRPC.TL_channels_sponsoredMessageReportResultChooseOption tL_channels_sponsoredMessageReportResultChooseOption) {
        ((Page) viewArr[0]).setOption(tL_channels_sponsoredMessageReportResultChooseOption);
    }

    public static void lambda$setReportChooseOption$1(View[] viewArr, TLRPC.TL_reportResultChooseOption tL_reportResultChooseOption) {
        ((Page) viewArr[0]).setOption(tL_reportResultChooseOption);
    }

    public static void lambda$setReportChooseOption$2(View[] viewArr, TLRPC.TL_reportResultAddComment tL_reportResultAddComment) {
        ((Page) viewArr[0]).setOption(tL_reportResultAddComment);
    }

    public void lambda$submitOption$3(org.telegram.tgnet.TLObject r4, java.lang.CharSequence r5, org.telegram.tgnet.TLRPC.TL_error r6, byte[] r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ReportBottomSheet.lambda$submitOption$3(org.telegram.tgnet.TLObject, java.lang.CharSequence, org.telegram.tgnet.TLRPC$TL_error, byte[]):void");
    }

    public void lambda$submitOption$4(final CharSequence charSequence, final byte[] bArr, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ReportBottomSheet.this.lambda$submitOption$3(tLObject, charSequence, tL_error, bArr);
            }
        });
    }

    public static void open(int i, final Context context, final long j, final boolean z, final ArrayList arrayList, final BulletinFactory bulletinFactory, final Theme.ResourcesProvider resourcesProvider, byte[] bArr, final Utilities.Callback callback) {
        TLRPC.TL_messages_report tL_messages_report;
        if (context == null || arrayList == null) {
            return;
        }
        final boolean[] zArr = {false};
        if (z) {
            TL_stories.TL_stories_report tL_stories_report = new TL_stories.TL_stories_report();
            tL_stories_report.peer = MessagesController.getInstance(i).getInputPeer(j);
            tL_stories_report.id.addAll(arrayList);
            tL_stories_report.option = bArr;
            tL_stories_report.message = "";
            tL_messages_report = tL_stories_report;
        } else {
            TLRPC.TL_messages_report tL_messages_report2 = new TLRPC.TL_messages_report();
            tL_messages_report2.peer = MessagesController.getInstance(i).getInputPeer(j);
            tL_messages_report2.id.addAll(arrayList);
            tL_messages_report2.option = bArr;
            tL_messages_report2.message = "";
            tL_messages_report = tL_messages_report2;
        }
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_report, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ReportBottomSheet.lambda$open$8(context, resourcesProvider, z, j, arrayList, zArr, callback, bulletinFactory, tLObject, tL_error);
            }
        });
    }

    public static void openChat(BaseFragment baseFragment, long j) {
        if (baseFragment == null) {
            return;
        }
        int currentAccount = baseFragment.getCurrentAccount();
        Context context = baseFragment.getContext();
        if (context == null) {
            return;
        }
        open(currentAccount, context, j, false, new ArrayList(), null, null, new byte[0], null);
    }

    public static void openChat(ChatActivity chatActivity) {
        if (chatActivity == null) {
            return;
        }
        int currentAccount = chatActivity.getCurrentAccount();
        Context context = chatActivity.getContext();
        long dialogId = chatActivity.getDialogId();
        if (context == null) {
            return;
        }
        open(currentAccount, context, dialogId, false, new ArrayList(), null, null, new byte[0], null);
    }

    public static void openMessage(BaseFragment baseFragment, MessageObject messageObject) {
        if (baseFragment == null) {
            return;
        }
        int currentAccount = baseFragment.getCurrentAccount();
        Context context = baseFragment.getContext();
        if (context == null) {
            return;
        }
        open(currentAccount, context, messageObject.getDialogId(), false, new ArrayList(Collections.singleton(Integer.valueOf(messageObject.getId()))), BulletinFactory.of(baseFragment), baseFragment.getResourceProvider(), new byte[0], null);
    }

    public static void openSponsored(final ChatActivity chatActivity, final MessageObject messageObject, final Theme.ResourcesProvider resourcesProvider) {
        if (chatActivity == null) {
            return;
        }
        final int currentAccount = chatActivity.getCurrentAccount();
        final Context context = chatActivity.getContext();
        final long dialogId = chatActivity.getDialogId();
        if (context == null) {
            return;
        }
        TLRPC.TL_messages_reportSponsoredMessage tL_messages_reportSponsoredMessage = new TLRPC.TL_messages_reportSponsoredMessage();
        tL_messages_reportSponsoredMessage.peer = MessagesController.getInstance(currentAccount).getInputPeer(dialogId);
        final byte[] bArr = messageObject.sponsoredId;
        tL_messages_reportSponsoredMessage.random_id = bArr;
        tL_messages_reportSponsoredMessage.option = new byte[0];
        ConnectionsManager.getInstance(currentAccount).sendRequest(tL_messages_reportSponsoredMessage, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ReportBottomSheet.lambda$openSponsored$15(context, resourcesProvider, dialogId, bArr, chatActivity, messageObject, currentAccount, tLObject, tL_error);
            }
        });
    }

    public static void openStory(int i, Context context, TL_stories.StoryItem storyItem, BulletinFactory bulletinFactory, Theme.ResourcesProvider resourcesProvider, Utilities.Callback callback) {
        open(i, context, storyItem.dialogId, true, new ArrayList(Collections.singleton(Integer.valueOf(storyItem.id))), bulletinFactory, resourcesProvider, new byte[0], callback);
    }

    public void submitOption(final CharSequence charSequence, final byte[] bArr, String str) {
        TLRPC.TL_messages_report tL_messages_report;
        ?? r8;
        if (this.sponsored) {
            r8 = new TLRPC.TL_messages_reportSponsoredMessage();
            r8.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            r8.random_id = this.sponsoredId;
            r8.option = bArr;
        } else {
            if (this.stories) {
                ?? tL_stories_report = new TL_stories.TL_stories_report();
                tL_stories_report.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
                ArrayList arrayList = this.messageIds;
                if (arrayList != null) {
                    tL_stories_report.id.addAll(arrayList);
                }
                if (str == null) {
                    str = "";
                }
                tL_stories_report.message = str;
                tL_stories_report.option = bArr;
                tL_messages_report = tL_stories_report;
            } else {
                TLRPC.TL_messages_report tL_messages_report2 = new TLRPC.TL_messages_report();
                tL_messages_report2.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
                ArrayList arrayList2 = this.messageIds;
                if (arrayList2 != null) {
                    tL_messages_report2.id.addAll(arrayList2);
                }
                if (str == null) {
                    str = "";
                }
                tL_messages_report2.message = str;
                tL_messages_report2.option = bArr;
                tL_messages_report = tL_messages_report2;
            }
            r8 = tL_messages_report;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(r8, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ReportBottomSheet.this.lambda$submitOption$4(charSequence, bArr, tLObject, tL_error);
            }
        });
    }

    @Override
    public boolean canDismissWithSwipe() {
        View currentView = this.viewPager.getCurrentView();
        if (currentView instanceof Page) {
            return ((Page) currentView).atTop();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.viewPager.getCurrentView() instanceof Page) {
            Page page = (Page) this.viewPager.getCurrentView();
            if (page.editTextCell != null) {
                AndroidUtilities.hideKeyboard(page.editTextCell);
            }
        }
        if (this.viewPager.getCurrentPosition() <= 0) {
            super.onBackPressed();
        } else {
            this.viewPager.scrollToPosition(r0.getCurrentPosition() - 1);
        }
    }

    public ReportBottomSheet setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public ReportBottomSheet setReportChooseOption(final TLRPC.TL_channels_sponsoredMessageReportResultChooseOption tL_channels_sponsoredMessageReportResultChooseOption) {
        final View[] viewPages = this.viewPager.getViewPages();
        View view = viewPages[0];
        if (view instanceof Page) {
            ((Page) view).bind(0);
            this.containerView.post(new Runnable() {
                @Override
                public final void run() {
                    ReportBottomSheet.lambda$setReportChooseOption$0(viewPages, tL_channels_sponsoredMessageReportResultChooseOption);
                }
            });
        }
        View view2 = viewPages[1];
        if (view2 instanceof Page) {
            ((Page) view2).bind(1);
        }
        return this;
    }

    public ReportBottomSheet setReportChooseOption(final TLRPC.TL_reportResultAddComment tL_reportResultAddComment) {
        final View[] viewPages = this.viewPager.getViewPages();
        View view = viewPages[0];
        if (view instanceof Page) {
            ((Page) view).bind(0);
            this.containerView.post(new Runnable() {
                @Override
                public final void run() {
                    ReportBottomSheet.lambda$setReportChooseOption$2(viewPages, tL_reportResultAddComment);
                }
            });
        }
        View view2 = viewPages[1];
        if (view2 instanceof Page) {
            ((Page) view2).bind(1);
        }
        return this;
    }

    public ReportBottomSheet setReportChooseOption(final TLRPC.TL_reportResultChooseOption tL_reportResultChooseOption) {
        final View[] viewPages = this.viewPager.getViewPages();
        View view = viewPages[0];
        if (view instanceof Page) {
            ((Page) view).bind(0);
            this.containerView.post(new Runnable() {
                @Override
                public final void run() {
                    ReportBottomSheet.lambda$setReportChooseOption$1(viewPages, tL_reportResultChooseOption);
                }
            });
        }
        View view2 = viewPages[1];
        if (view2 instanceof Page) {
            ((Page) view2).bind(1);
        }
        return this;
    }
}

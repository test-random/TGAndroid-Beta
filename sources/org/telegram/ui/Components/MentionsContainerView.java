package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.SpannableString;
import android.view.MotionEvent;
import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.PaddedListAdapter;
import org.telegram.ui.Business.QuickRepliesActivity;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.PhotoViewer;

public abstract class MentionsContainerView extends BlurredFrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private MentionsAdapter adapter;
    private boolean allowBlur;
    private int animationIndex;
    BaseFragment baseFragment;
    private PhotoViewer.PhotoViewerProvider botContextProvider;
    private ArrayList botContextResults;
    private Integer color;
    private float containerBottom;
    private float containerPadding;
    private float containerTop;
    private Delegate delegate;
    private ExtendedGridLayoutManager gridLayoutManager;
    private float hideT;
    private boolean ignoreLayout;
    private LinearLayoutManager linearLayoutManager;
    private MentionsListView listView;
    private boolean listViewHiding;
    private float listViewPadding;
    private SpringAnimation listViewTranslationAnimator;
    private RecyclerListView.OnItemClickListener mentionsOnItemClickListener;
    private PaddedListAdapter paddedAdapter;
    private Paint paint;
    private Path path;
    private android.graphics.Rect rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private int scrollRangeUpdateTries;
    private boolean scrollToFirst;
    private boolean shouldLiftMentions;
    private boolean shown;
    private final SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private boolean switchLayoutManagerOnEnd;
    private Runnable updateVisibilityRunnable;

    public class AnonymousClass1 extends LinearLayoutManager {
        AnonymousClass1(Context context) {
            super(context);
        }

        @Override
        public void setReverseLayout(boolean z) {
            super.setReverseLayout(z);
            MentionsContainerView.this.listView.setTranslationY((z ? -1 : 1) * AndroidUtilities.dp(6.0f));
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    public class AnonymousClass2 extends ExtendedGridLayoutManager {
        private Size size = new Size();

        AnonymousClass2(Context context, int i, boolean z, boolean z2) {
            super(context, i, z, z2);
            this.size = new Size();
        }

        @Override
        public int getFlowItemCount() {
            return (MentionsContainerView.this.adapter.getBotContextSwitch() == null && MentionsContainerView.this.adapter.getBotWebViewSwitch() == null) ? super.getFlowItemCount() : getItemCount() - 1;
        }

        @Override
        protected Size getSizeForItem(int i) {
            TLRPC.PhotoSize closestPhotoSizeWithSize;
            TLRPC.DocumentAttribute documentAttribute;
            Size size = this.size;
            int i2 = 0;
            size.full = false;
            if (i == 0) {
                size.width = getWidth();
                this.size.height = MentionsContainerView.this.paddedAdapter.getPadding();
                Size size2 = this.size;
                size2.full = true;
                return size2;
            }
            int i3 = i - 1;
            if (MentionsContainerView.this.adapter.getBotContextSwitch() == null && MentionsContainerView.this.adapter.getBotWebViewSwitch() == null) {
                i = i3;
            }
            Size size3 = this.size;
            size3.width = 0.0f;
            size3.height = 0.0f;
            Object item = MentionsContainerView.this.adapter.getItem(i);
            if (item instanceof TLRPC.BotInlineResult) {
                TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) item;
                TLRPC.Document document = botInlineResult.document;
                if (document != null) {
                    TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                    Size size4 = this.size;
                    size4.width = closestPhotoSizeWithSize2 != null ? closestPhotoSizeWithSize2.w : 100.0f;
                    size4.height = closestPhotoSizeWithSize2 != null ? closestPhotoSizeWithSize2.h : 100.0f;
                    while (i2 < botInlineResult.document.attributes.size()) {
                        documentAttribute = botInlineResult.document.attributes.get(i2);
                        if (!(documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) && !(documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                            i2++;
                        }
                        Size size5 = this.size;
                        size5.width = documentAttribute.w;
                        size5.height = documentAttribute.h;
                    }
                } else if (botInlineResult.content != null) {
                    while (i2 < botInlineResult.content.attributes.size()) {
                        documentAttribute = botInlineResult.content.attributes.get(i2);
                        if (!(documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) && !(documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                            i2++;
                        }
                        Size size52 = this.size;
                        size52.width = documentAttribute.w;
                        size52.height = documentAttribute.h;
                    }
                } else if (botInlineResult.thumb != null) {
                    while (i2 < botInlineResult.thumb.attributes.size()) {
                        documentAttribute = botInlineResult.thumb.attributes.get(i2);
                        if (!(documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) && !(documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                            i2++;
                        }
                        Size size522 = this.size;
                        size522.width = documentAttribute.w;
                        size522.height = documentAttribute.h;
                    }
                } else {
                    TLRPC.Photo photo = botInlineResult.photo;
                    if (photo != null && (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.photoSize.intValue())) != null) {
                        Size size6 = this.size;
                        size6.width = closestPhotoSizeWithSize.w;
                        size6.height = closestPhotoSizeWithSize.h;
                    }
                }
            }
            return this.size;
        }
    }

    public class AnonymousClass3 extends GridLayoutManager.SpanSizeLookup {
        AnonymousClass3() {
        }

        @Override
        public int getSpanSize(int i) {
            if (i == 0) {
                return 100;
            }
            int i2 = i - 1;
            Object item = MentionsContainerView.this.adapter.getItem(i2);
            if (item instanceof TLRPC.TL_inlineBotSwitchPM) {
                return 100;
            }
            if (item instanceof TLRPC.Document) {
                return 20;
            }
            if (MentionsContainerView.this.adapter.getBotContextSwitch() != null || MentionsContainerView.this.adapter.getBotWebViewSwitch() != null) {
                i = i2;
            }
            return MentionsContainerView.this.gridLayoutManager.getSpanSizeForItem(i);
        }
    }

    public class AnonymousClass4 implements MentionsAdapter.MentionsAdapterDelegate {
        final BaseFragment val$baseFragment;

        AnonymousClass4(BaseFragment baseFragment) {
            r2 = baseFragment;
        }

        @Override
        public void needChangePanelVisibility(boolean z) {
            boolean z2 = false;
            if (MentionsContainerView.this.getNeededLayoutManager() != MentionsContainerView.this.getCurrentLayoutManager() && MentionsContainerView.this.canOpen()) {
                if (MentionsContainerView.this.adapter.getLastItemCount() > 0) {
                    MentionsContainerView.this.switchLayoutManagerOnEnd = true;
                    MentionsContainerView.this.updateVisibility(z2);
                }
                MentionsContainerView.this.listView.setLayoutManager(MentionsContainerView.this.getNeededLayoutManager());
            }
            if (z && !MentionsContainerView.this.canOpen()) {
                z = false;
            }
            if (!z || MentionsContainerView.this.adapter.getItemCountInternal() > 0) {
                z2 = z;
            }
            MentionsContainerView.this.updateVisibility(z2);
        }

        @Override
        public void onContextClick(TLRPC.BotInlineResult botInlineResult) {
            MentionsContainerView.this.onContextClick(botInlineResult);
        }

        @Override
        public void onContextSearch(boolean z) {
            MentionsContainerView.this.onContextSearch(z);
        }

        @Override
        public void onItemCountUpdate(int i, int i2) {
            if (MentionsContainerView.this.listView.getLayoutManager() == MentionsContainerView.this.gridLayoutManager || !MentionsContainerView.this.shown) {
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(MentionsContainerView.this.updateVisibilityRunnable);
            AndroidUtilities.runOnUIThread(MentionsContainerView.this.updateVisibilityRunnable, r2.getFragmentBeginToShow() ? 0L : 100L);
        }
    }

    public class AnonymousClass5 extends PhotoViewer.EmptyPhotoViewerProvider {
        AnonymousClass5() {
        }

        @Override
        public org.telegram.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(org.telegram.messenger.MessageObject r5, org.telegram.tgnet.TLRPC.FileLocation r6, int r7, boolean r8) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MentionsContainerView.AnonymousClass5.getPlaceForPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, int, boolean):org.telegram.ui.PhotoViewer$PlaceProviderObject");
        }

        @Override
        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
            if (i < 0 || i >= MentionsContainerView.this.botContextResults.size()) {
                return;
            }
            MentionsContainerView.this.delegate.sendBotInlineResult((TLRPC.BotInlineResult) MentionsContainerView.this.botContextResults.get(i), z, i2);
        }
    }

    public interface Delegate {

        public abstract class CC {
            public static void $default$addEmojiToRecent(Delegate delegate, String str) {
            }

            public static void $default$onStickerSelected(Delegate delegate, TLRPC.TL_document tL_document, String str, Object obj) {
            }

            public static void $default$sendBotInlineResult(Delegate delegate, TLRPC.BotInlineResult botInlineResult, boolean z, int i) {
            }
        }

        void addEmojiToRecent(String str);

        Paint.FontMetricsInt getFontMetrics();

        void onStickerSelected(TLRPC.TL_document tL_document, String str, Object obj);

        void replaceText(int i, int i2, CharSequence charSequence, boolean z);

        void sendBotInlineResult(TLRPC.BotInlineResult botInlineResult, boolean z, int i);
    }

    public class MentionsListView extends RecyclerListView {
        private boolean isDragging;
        private boolean isScrolling;
        private int lastHeight;
        private int lastWidth;

        public class AnonymousClass1 extends RecyclerView.OnScrollListener {
            final MentionsContainerView val$this$0;

            AnonymousClass1(MentionsContainerView mentionsContainerView) {
                r2 = mentionsContainerView;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                MentionsListView.this.isScrolling = i != 0;
                MentionsListView.this.isDragging = i == 1;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int findLastVisibleItemPosition = (MentionsListView.this.getLayoutManager() == MentionsContainerView.this.gridLayoutManager ? MentionsContainerView.this.gridLayoutManager : MentionsContainerView.this.linearLayoutManager).findLastVisibleItemPosition();
                if ((findLastVisibleItemPosition == -1 ? 0 : findLastVisibleItemPosition) > 0 && findLastVisibleItemPosition > MentionsContainerView.this.adapter.getLastItemCount() - 5) {
                    MentionsContainerView.this.adapter.searchForContextBotForNextOffset();
                }
                MentionsContainerView.this.onScrolled(!r2.canScrollVertically(-1), true ^ MentionsListView.this.canScrollVertically(1));
            }
        }

        public class AnonymousClass2 extends RecyclerView.ItemDecoration {
            final MentionsContainerView val$this$0;

            AnonymousClass2(MentionsContainerView mentionsContainerView) {
                r2 = mentionsContainerView;
            }

            @Override
            public void getItemOffsets(android.graphics.Rect r3, android.view.View r4, androidx.recyclerview.widget.RecyclerView r5, androidx.recyclerview.widget.RecyclerView.State r6) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MentionsContainerView.MentionsListView.AnonymousClass2.getItemOffsets(android.graphics.Rect, android.view.View, androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$State):void");
            }
        }

        public MentionsListView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            setOnScrollListener(new RecyclerView.OnScrollListener() {
                final MentionsContainerView val$this$0;

                AnonymousClass1(MentionsContainerView mentionsContainerView) {
                    r2 = mentionsContainerView;
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    MentionsListView.this.isScrolling = i != 0;
                    MentionsListView.this.isDragging = i == 1;
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    int findLastVisibleItemPosition = (MentionsListView.this.getLayoutManager() == MentionsContainerView.this.gridLayoutManager ? MentionsContainerView.this.gridLayoutManager : MentionsContainerView.this.linearLayoutManager).findLastVisibleItemPosition();
                    if ((findLastVisibleItemPosition == -1 ? 0 : findLastVisibleItemPosition) > 0 && findLastVisibleItemPosition > MentionsContainerView.this.adapter.getLastItemCount() - 5) {
                        MentionsContainerView.this.adapter.searchForContextBotForNextOffset();
                    }
                    MentionsContainerView.this.onScrolled(!r2.canScrollVertically(-1), true ^ MentionsListView.this.canScrollVertically(1));
                }
            });
            addItemDecoration(new RecyclerView.ItemDecoration() {
                final MentionsContainerView val$this$0;

                AnonymousClass2(MentionsContainerView mentionsContainerView) {
                    r2 = mentionsContainerView;
                }

                @Override
                public void getItemOffsets(android.graphics.Rect r3, android.view.View r4, androidx.recyclerview.widget.RecyclerView r5, androidx.recyclerview.widget.RecyclerView.State r6) {
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MentionsContainerView.MentionsListView.AnonymousClass2.getItemOffsets(android.graphics.Rect, android.view.View, androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$State):void");
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (MentionsContainerView.this.linearLayoutManager.getReverseLayout()) {
                if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && motionEvent.getY() > MentionsContainerView.this.paddedAdapter.paddingView.getTop()) {
                    return false;
                }
            } else if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && motionEvent.getY() < MentionsContainerView.this.paddedAdapter.paddingView.getBottom()) {
                return false;
            }
            boolean z = !this.isScrolling && ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, MentionsContainerView.this.listView, 0, null, this.resourcesProvider);
            if ((MentionsContainerView.this.adapter.isStickers() && motionEvent.getAction() == 0) || motionEvent.getAction() == 2) {
                MentionsContainerView.this.adapter.doSomeStickersAction();
            }
            return super.onInterceptTouchEvent(motionEvent) || z;
        }

        @Override
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int i6 = i3 - i;
            int i7 = i4 - i2;
            boolean isReversed = MentionsContainerView.this.isReversed();
            LinearLayoutManager currentLayoutManager = MentionsContainerView.this.getCurrentLayoutManager();
            int findFirstVisibleItemPosition = isReversed ? currentLayoutManager.findFirstVisibleItemPosition() : currentLayoutManager.findLastVisibleItemPosition();
            View findViewByPosition = currentLayoutManager.findViewByPosition(findFirstVisibleItemPosition);
            if (findViewByPosition != null) {
                i5 = findViewByPosition.getTop() - (isReversed ? 0 : this.lastHeight - i7);
            } else {
                i5 = 0;
            }
            super.onLayout(z, i, i2, i3, i4);
            if (MentionsContainerView.this.scrollToFirst) {
                MentionsContainerView.this.ignoreLayout = true;
                currentLayoutManager.scrollToPositionWithOffset(0, 100000);
                super.onLayout(false, i, i2, i3, i4);
                MentionsContainerView.this.ignoreLayout = false;
                MentionsContainerView.this.scrollToFirst = false;
            } else if (findFirstVisibleItemPosition != -1 && i6 == this.lastWidth && i7 - this.lastHeight != 0) {
                MentionsContainerView.this.ignoreLayout = true;
                currentLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, i5, false);
                super.onLayout(false, i, i2, i3, i4);
                MentionsContainerView.this.ignoreLayout = false;
            }
            this.lastHeight = i7;
            this.lastWidth = i6;
        }

        @Override
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i2);
            if (MentionsContainerView.this.paddedAdapter != null) {
                MentionsContainerView.this.paddedAdapter.setPadding(size);
            }
            MentionsContainerView.this.listViewPadding = (int) Math.min(AndroidUtilities.dp(126.0f), AndroidUtilities.displaySize.y * 0.22f);
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size + ((int) MentionsContainerView.this.listViewPadding), 1073741824));
        }

        @Override
        public void onScrolled(int i, int i2) {
            super.onScrolled(i, i2);
            MentionsContainerView.this.invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (MentionsContainerView.this.linearLayoutManager.getReverseLayout()) {
                if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && motionEvent.getY() > MentionsContainerView.this.paddedAdapter.paddingView.getTop()) {
                    return false;
                }
            } else if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && motionEvent.getY() < MentionsContainerView.this.paddedAdapter.paddingView.getBottom()) {
                return false;
            }
            return super.onTouchEvent(motionEvent);
        }

        @Override
        public void requestLayout() {
            if (MentionsContainerView.this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            MentionsContainerView.this.invalidate();
        }
    }

    public MentionsContainerView(Context context, long j, long j2, BaseFragment baseFragment, SizeNotifierFrameLayout sizeNotifierFrameLayout, Theme.ResourcesProvider resourcesProvider) {
        super(context, sizeNotifierFrameLayout);
        this.shouldLiftMentions = false;
        this.rect = new android.graphics.Rect();
        this.ignoreLayout = false;
        this.scrollToFirst = false;
        this.shown = false;
        this.updateVisibilityRunnable = new Runnable() {
            @Override
            public final void run() {
                MentionsContainerView.this.lambda$new$0();
            }
        };
        this.animationIndex = -1;
        this.listViewHiding = false;
        this.hideT = 0.0f;
        this.switchLayoutManagerOnEnd = false;
        this.botContextProvider = new PhotoViewer.EmptyPhotoViewerProvider() {
            AnonymousClass5() {
            }

            @Override
            public org.telegram.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(org.telegram.messenger.MessageObject r5, org.telegram.tgnet.TLRPC.FileLocation r6, int r7, boolean r8) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MentionsContainerView.AnonymousClass5.getPlaceForPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, int, boolean):org.telegram.ui.PhotoViewer$PlaceProviderObject");
            }

            @Override
            public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
                if (i < 0 || i >= MentionsContainerView.this.botContextResults.size()) {
                    return;
                }
                MentionsContainerView.this.delegate.sendBotInlineResult((TLRPC.BotInlineResult) MentionsContainerView.this.botContextResults.get(i), z, i2);
            }
        };
        this.baseFragment = baseFragment;
        this.sizeNotifierFrameLayout = sizeNotifierFrameLayout;
        this.resourcesProvider = resourcesProvider;
        this.drawBlur = false;
        this.isTopView = false;
        setVisibility(8);
        setWillNotDraw(false);
        this.listViewPadding = (int) Math.min(AndroidUtilities.dp(126.0f), AndroidUtilities.displaySize.y * 0.22f);
        MentionsListView mentionsListView = new MentionsListView(context, resourcesProvider);
        this.listView = mentionsListView;
        mentionsListView.setTranslationY(AndroidUtilities.dp(6.0f));
        AnonymousClass1 anonymousClass1 = new LinearLayoutManager(context) {
            AnonymousClass1(Context context2) {
                super(context2);
            }

            @Override
            public void setReverseLayout(boolean z) {
                super.setReverseLayout(z);
                MentionsContainerView.this.listView.setTranslationY((z ? -1 : 1) * AndroidUtilities.dp(6.0f));
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.linearLayoutManager = anonymousClass1;
        anonymousClass1.setOrientation(1);
        AnonymousClass2 anonymousClass2 = new ExtendedGridLayoutManager(context2, 100, false, false) {
            private Size size = new Size();

            AnonymousClass2(Context context2, int i, boolean z, boolean z2) {
                super(context2, i, z, z2);
                this.size = new Size();
            }

            @Override
            public int getFlowItemCount() {
                return (MentionsContainerView.this.adapter.getBotContextSwitch() == null && MentionsContainerView.this.adapter.getBotWebViewSwitch() == null) ? super.getFlowItemCount() : getItemCount() - 1;
            }

            @Override
            protected Size getSizeForItem(int i) {
                TLRPC.PhotoSize closestPhotoSizeWithSize;
                TLRPC.DocumentAttribute documentAttribute;
                Size size = this.size;
                int i2 = 0;
                size.full = false;
                if (i == 0) {
                    size.width = getWidth();
                    this.size.height = MentionsContainerView.this.paddedAdapter.getPadding();
                    Size size2 = this.size;
                    size2.full = true;
                    return size2;
                }
                int i3 = i - 1;
                if (MentionsContainerView.this.adapter.getBotContextSwitch() == null && MentionsContainerView.this.adapter.getBotWebViewSwitch() == null) {
                    i = i3;
                }
                Size size3 = this.size;
                size3.width = 0.0f;
                size3.height = 0.0f;
                Object item = MentionsContainerView.this.adapter.getItem(i);
                if (item instanceof TLRPC.BotInlineResult) {
                    TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) item;
                    TLRPC.Document document = botInlineResult.document;
                    if (document != null) {
                        TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                        Size size4 = this.size;
                        size4.width = closestPhotoSizeWithSize2 != null ? closestPhotoSizeWithSize2.w : 100.0f;
                        size4.height = closestPhotoSizeWithSize2 != null ? closestPhotoSizeWithSize2.h : 100.0f;
                        while (i2 < botInlineResult.document.attributes.size()) {
                            documentAttribute = botInlineResult.document.attributes.get(i2);
                            if (!(documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) && !(documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                i2++;
                            }
                            Size size522 = this.size;
                            size522.width = documentAttribute.w;
                            size522.height = documentAttribute.h;
                        }
                    } else if (botInlineResult.content != null) {
                        while (i2 < botInlineResult.content.attributes.size()) {
                            documentAttribute = botInlineResult.content.attributes.get(i2);
                            if (!(documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) && !(documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                i2++;
                            }
                            Size size5222 = this.size;
                            size5222.width = documentAttribute.w;
                            size5222.height = documentAttribute.h;
                        }
                    } else if (botInlineResult.thumb != null) {
                        while (i2 < botInlineResult.thumb.attributes.size()) {
                            documentAttribute = botInlineResult.thumb.attributes.get(i2);
                            if (!(documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) && !(documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                i2++;
                            }
                            Size size52222 = this.size;
                            size52222.width = documentAttribute.w;
                            size52222.height = documentAttribute.h;
                        }
                    } else {
                        TLRPC.Photo photo = botInlineResult.photo;
                        if (photo != null && (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.photoSize.intValue())) != null) {
                            Size size6 = this.size;
                            size6.width = closestPhotoSizeWithSize.w;
                            size6.height = closestPhotoSizeWithSize.h;
                        }
                    }
                }
                return this.size;
            }
        };
        this.gridLayoutManager = anonymousClass2;
        anonymousClass2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            AnonymousClass3() {
            }

            @Override
            public int getSpanSize(int i) {
                if (i == 0) {
                    return 100;
                }
                int i2 = i - 1;
                Object item = MentionsContainerView.this.adapter.getItem(i2);
                if (item instanceof TLRPC.TL_inlineBotSwitchPM) {
                    return 100;
                }
                if (item instanceof TLRPC.Document) {
                    return 20;
                }
                if (MentionsContainerView.this.adapter.getBotContextSwitch() != null || MentionsContainerView.this.adapter.getBotWebViewSwitch() != null) {
                    i = i2;
                }
                return MentionsContainerView.this.gridLayoutManager.getSpanSizeForItem(i);
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(150L);
        defaultItemAnimator.setMoveDuration(150L);
        defaultItemAnimator.setChangeDuration(150L);
        defaultItemAnimator.setRemoveDuration(150L);
        defaultItemAnimator.setTranslationInterpolator(CubicBezierInterpolator.DEFAULT);
        defaultItemAnimator.setDelayAnimations(false);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setClipToPadding(false);
        this.listView.setLayoutManager(this.linearLayoutManager);
        MentionsAdapter mentionsAdapter = new MentionsAdapter(context2, false, j, j2, new MentionsAdapter.MentionsAdapterDelegate() {
            final BaseFragment val$baseFragment;

            AnonymousClass4(BaseFragment baseFragment2) {
                r2 = baseFragment2;
            }

            @Override
            public void needChangePanelVisibility(boolean z) {
                boolean z2 = false;
                if (MentionsContainerView.this.getNeededLayoutManager() != MentionsContainerView.this.getCurrentLayoutManager() && MentionsContainerView.this.canOpen()) {
                    if (MentionsContainerView.this.adapter.getLastItemCount() > 0) {
                        MentionsContainerView.this.switchLayoutManagerOnEnd = true;
                        MentionsContainerView.this.updateVisibility(z2);
                    }
                    MentionsContainerView.this.listView.setLayoutManager(MentionsContainerView.this.getNeededLayoutManager());
                }
                if (z && !MentionsContainerView.this.canOpen()) {
                    z = false;
                }
                if (!z || MentionsContainerView.this.adapter.getItemCountInternal() > 0) {
                    z2 = z;
                }
                MentionsContainerView.this.updateVisibility(z2);
            }

            @Override
            public void onContextClick(TLRPC.BotInlineResult botInlineResult) {
                MentionsContainerView.this.onContextClick(botInlineResult);
            }

            @Override
            public void onContextSearch(boolean z) {
                MentionsContainerView.this.onContextSearch(z);
            }

            @Override
            public void onItemCountUpdate(int i, int i2) {
                if (MentionsContainerView.this.listView.getLayoutManager() == MentionsContainerView.this.gridLayoutManager || !MentionsContainerView.this.shown) {
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(MentionsContainerView.this.updateVisibilityRunnable);
                AndroidUtilities.runOnUIThread(MentionsContainerView.this.updateVisibilityRunnable, r2.getFragmentBeginToShow() ? 0L : 100L);
            }
        }, resourcesProvider, isStories());
        this.adapter = mentionsAdapter;
        PaddedListAdapter paddedListAdapter = new PaddedListAdapter(mentionsAdapter);
        this.paddedAdapter = paddedListAdapter;
        this.listView.setAdapter(paddedListAdapter);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        setReversed(false);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public static void lambda$didReceivedNotification$6(View view) {
        if (view instanceof MentionCell) {
            ((MentionCell) view).invalidateEmojis();
        } else if (view instanceof QuickRepliesActivity.QuickReplyView) {
            ((QuickRepliesActivity.QuickReplyView) view).invalidateEmojis();
        } else {
            view.invalidate();
        }
    }

    public void lambda$new$0() {
        updateListViewTranslation(!this.shown, true);
    }

    public void lambda$updateListViewTranslation$1(float f, float f2, float f3, float f4, DynamicAnimation dynamicAnimation, float f5, float f6) {
        this.listView.setTranslationY(f5);
        onAnimationScroll();
        this.hideT = AndroidUtilities.lerp(f, f2, (f5 - f3) / (f4 - f3));
    }

    public void lambda$updateListViewTranslation$2(boolean z, DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
        if (z2) {
            return;
        }
        this.listViewTranslationAnimator = null;
        setVisibility(z ? 8 : 0);
        if (this.switchLayoutManagerOnEnd && z) {
            this.switchLayoutManagerOnEnd = false;
            this.listView.setLayoutManager(getNeededLayoutManager());
            this.shown = true;
            updateVisibility(true);
        }
    }

    public static void lambda$updateListViewTranslation$3(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
    }

    public void lambda$withDelegate$4(Delegate delegate, View view, int i) {
        Paint.FontMetricsInt fontMetricsInt;
        CharSequence charSequence;
        StringBuilder sb;
        String publicUsername;
        CharSequence charSequence2;
        if (i == 0 || getAdapter().isBannedInline()) {
            return;
        }
        int i2 = i - 1;
        Object item = getAdapter().getItem(i2);
        int resultStartPosition = getAdapter().getResultStartPosition();
        int resultLength = getAdapter().getResultLength();
        String str = "";
        if (getAdapter().isLocalHashtagHint(i2)) {
            TLRPC.Chat chat = getAdapter().chat;
            if (chat == null && getAdapter().parentFragment != null) {
                chat = getAdapter().parentFragment.getCurrentChat();
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(getAdapter().getHashtagHint());
            if (chat != null) {
                str = "@" + ChatObject.getPublicUsername(chat);
            }
            sb2.append(str);
            sb2.append(" ");
            charSequence2 = sb2.toString();
        } else {
            if (!getAdapter().isGlobalHashtagHint(i2)) {
                if (item instanceof TLRPC.TL_document) {
                    if (view instanceof StickerCell) {
                        ((StickerCell) view).getSendAnimationData();
                    }
                    TLRPC.TL_document tL_document = (TLRPC.TL_document) item;
                    delegate.onStickerSelected(tL_document, MessageObject.findAnimatedEmojiEmoticon(tL_document), getAdapter().getItemParent(i2));
                } else {
                    if (item instanceof TLRPC.Chat) {
                        publicUsername = ChatObject.getPublicUsername((TLRPC.Chat) item);
                        if (publicUsername != null) {
                            sb = new StringBuilder();
                            sb.append("@");
                            sb.append(publicUsername);
                            sb.append(" ");
                            charSequence = sb.toString();
                        }
                    } else if (item instanceof TLRPC.User) {
                        TLRPC.User user = (TLRPC.User) item;
                        if (UserObject.getPublicUsername(user) != null) {
                            sb = new StringBuilder();
                            sb.append("@");
                            publicUsername = UserObject.getPublicUsername(user);
                            sb.append(publicUsername);
                            sb.append(" ");
                            charSequence = sb.toString();
                        } else {
                            SpannableString spannableString = new SpannableString(UserObject.getFirstName(user, false) + " ");
                            spannableString.setSpan(new URLSpanUserMention("" + user.id, 3), 0, spannableString.length(), 33);
                            delegate.replaceText(resultStartPosition, resultLength, spannableString, false);
                        }
                    } else if (item instanceof String) {
                        charSequence = item + " ";
                    } else if (item instanceof MediaDataController.KeywordResult) {
                        String str2 = ((MediaDataController.KeywordResult) item).emoji;
                        delegate.addEmojiToRecent(str2);
                        if (str2 != null && str2.startsWith("animated_")) {
                            try {
                                try {
                                    fontMetricsInt = delegate.getFontMetrics();
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e, false);
                                    fontMetricsInt = null;
                                }
                                long parseLong = Long.parseLong(str2.substring(9));
                                TLRPC.Document findDocument = AnimatedEmojiDrawable.findDocument(UserConfig.selectedAccount, parseLong);
                                SpannableString spannableString2 = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(findDocument));
                                spannableString2.setSpan(findDocument != null ? new AnimatedEmojiSpan(findDocument, fontMetricsInt) : new AnimatedEmojiSpan(parseLong, fontMetricsInt), 0, spannableString2.length(), 33);
                                delegate.replaceText(resultStartPosition, resultLength, spannableString2, false);
                            } catch (Exception unused) {
                            }
                            updateVisibility(false);
                        }
                        delegate.replaceText(resultStartPosition, resultLength, str2, true);
                        updateVisibility(false);
                    }
                    delegate.replaceText(resultStartPosition, resultLength, charSequence, false);
                }
                if (item instanceof TLRPC.BotInlineResult) {
                    TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) item;
                    if ((!botInlineResult.type.equals("photo") || (botInlineResult.photo == null && botInlineResult.content == null)) && ((!botInlineResult.type.equals("gif") || (botInlineResult.document == null && botInlineResult.content == null)) && (!botInlineResult.type.equals("video") || botInlineResult.document == null))) {
                        delegate.sendBotInlineResult(botInlineResult, true, 0);
                        return;
                    }
                    ArrayList arrayList = new ArrayList(getAdapter().getSearchResultBotContext());
                    this.botContextResults = arrayList;
                    PhotoViewer.getInstance().setParentActivity(this.baseFragment, this.resourcesProvider);
                    PhotoViewer.getInstance().openPhotoForSelect(arrayList, getAdapter().getItemPosition(i2), 3, false, this.botContextProvider, null);
                    return;
                }
                return;
            }
            charSequence2 = getAdapter().getHashtagHint() + " ";
        }
        delegate.replaceText(resultStartPosition, resultLength, charSequence2, false);
    }

    public boolean lambda$withDelegate$5(View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, getListView(), 0, this.mentionsOnItemClickListener, null, this.resourcesProvider);
    }

    private void updateListViewTranslation(final boolean z, boolean z2) {
        float f;
        int i;
        SpringAnimation springAnimation;
        if (this.listView == null || this.paddedAdapter == null) {
            this.scrollRangeUpdateTries = 0;
            return;
        }
        if (this.listViewHiding && (springAnimation = this.listViewTranslationAnimator) != null && springAnimation.isRunning() && z) {
            this.scrollRangeUpdateTries = 0;
            return;
        }
        boolean isReversed = isReversed();
        if (z) {
            f = (-this.containerPadding) - AndroidUtilities.dp(6.0f);
        } else {
            int computeVerticalScrollRange = this.listView.computeVerticalScrollRange();
            float padding = (computeVerticalScrollRange - this.paddedAdapter.getPadding()) + this.containerPadding;
            if (computeVerticalScrollRange <= 0 && this.adapter.getItemCountInternal() > 0 && (i = this.scrollRangeUpdateTries) < 3) {
                this.scrollRangeUpdateTries = i + 1;
                updateVisibility(true);
                return;
            }
            f = padding;
        }
        this.scrollRangeUpdateTries = 0;
        float f2 = this.listViewPadding;
        float max = isReversed ? -Math.max(0.0f, f2 - f) : Math.max(0.0f, f2 - f) + (-f2);
        if (z && !isReversed) {
            max += this.listView.computeVerticalScrollOffset();
        }
        final float f3 = max;
        SpringAnimation springAnimation2 = this.listViewTranslationAnimator;
        if (springAnimation2 != null) {
            springAnimation2.cancel();
        }
        Integer num = null;
        if (z2) {
            this.listViewHiding = z;
            final float translationY = this.listView.getTranslationY();
            final float f4 = this.hideT;
            final float f5 = z ? 1.0f : 0.0f;
            if (translationY == f3) {
                this.listViewTranslationAnimator = null;
                num = Integer.valueOf(z ? 8 : 0);
                if (this.switchLayoutManagerOnEnd && z) {
                    this.switchLayoutManagerOnEnd = false;
                    this.listView.setLayoutManager(getNeededLayoutManager());
                    this.shown = true;
                    updateVisibility(true);
                }
            } else {
                SpringAnimation spring = new SpringAnimation(new FloatValueHolder(translationY)).setSpring(new SpringForce(f3).setDampingRatio(1.0f).setStiffness(550.0f));
                this.listViewTranslationAnimator = spring;
                spring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f6, float f7) {
                        MentionsContainerView.this.lambda$updateListViewTranslation$1(f4, f5, translationY, f3, dynamicAnimation, f6, f7);
                    }
                });
                if (z) {
                    this.listViewTranslationAnimator.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                        @Override
                        public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z3, float f6, float f7) {
                            MentionsContainerView.this.lambda$updateListViewTranslation$2(z, dynamicAnimation, z3, f6, f7);
                        }
                    });
                }
                this.listViewTranslationAnimator.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z3, float f6, float f7) {
                        MentionsContainerView.lambda$updateListViewTranslation$3(dynamicAnimation, z3, f6, f7);
                    }
                });
                this.listViewTranslationAnimator.start();
            }
        } else {
            this.hideT = z ? 1.0f : 0.0f;
            this.listView.setTranslationY(f3);
            if (z) {
                num = 8;
            }
        }
        if (num == null || getVisibility() == num.intValue()) {
            return;
        }
        setVisibility(num.intValue());
    }

    protected boolean canOpen() {
        return true;
    }

    public float clipBottom() {
        if (getVisibility() == 0 && !isReversed()) {
            return getMeasuredHeight() - this.containerTop;
        }
        return 0.0f;
    }

    public float clipTop() {
        if (getVisibility() == 0 && isReversed()) {
            return this.containerBottom;
        }
        return 0.0f;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            AndroidUtilities.forEachViews((RecyclerView) this.listView, new Consumer() {
                @Override
                public final void accept(Object obj) {
                    MentionsContainerView.lambda$didReceivedNotification$6((View) obj);
                }
            });
        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        float min;
        boolean isReversed = isReversed();
        this.containerPadding = AndroidUtilities.dp(((this.adapter.isStickers() || this.adapter.isBotContext()) && this.adapter.isMediaLayout() && this.adapter.getBotContextSwitch() == null && this.adapter.getBotWebViewSwitch() == null ? 2 : 0) + 2);
        canvas.save();
        float dp = AndroidUtilities.dp(6.0f);
        float f = this.containerTop;
        if (isReversed) {
            float min2 = Math.min(Math.max(0.0f, (this.paddedAdapter.paddingViewAttached ? r4.paddingView.getTop() : getHeight()) + this.listView.getTranslationY()) + this.containerPadding, (1.0f - this.hideT) * getHeight());
            android.graphics.Rect rect = this.rect;
            this.containerTop = 0.0f;
            int measuredWidth = getMeasuredWidth();
            this.containerBottom = min2;
            rect.set(0, (int) 0.0f, measuredWidth, (int) min2);
            min = Math.min(dp, Math.abs(getMeasuredHeight() - this.containerBottom));
            if (min > 0.0f) {
                canvas.clipRect(0, 0, getWidth(), getHeight());
                this.rect.top -= (int) min;
            }
        } else {
            if (this.listView.getLayoutManager() == this.gridLayoutManager) {
                this.containerPadding += AndroidUtilities.dp(2.0f);
                dp += AndroidUtilities.dp(2.0f);
            }
            float max = Math.max(0.0f, (this.paddedAdapter.paddingViewAttached ? r4.paddingView.getBottom() : 0) + this.listView.getTranslationY()) - this.containerPadding;
            this.containerTop = max;
            float max2 = Math.max(max, this.hideT * getHeight());
            android.graphics.Rect rect2 = this.rect;
            this.containerTop = max2;
            int measuredWidth2 = getMeasuredWidth();
            float measuredHeight = getMeasuredHeight();
            this.containerBottom = measuredHeight;
            rect2.set(0, (int) max2, measuredWidth2, (int) measuredHeight);
            min = Math.min(dp, Math.abs(this.containerTop));
            if (min > 0.0f) {
                canvas.clipRect(0, 0, getWidth(), getHeight());
                this.rect.bottom += (int) min;
            }
        }
        float f2 = min;
        if (Math.abs(f - this.containerTop) > 0.1f) {
            onAnimationScroll();
        }
        if (this.paint == null) {
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setShadowLayer(AndroidUtilities.dp(4.0f), 0.0f, 0.0f, 503316480);
        }
        Paint paint2 = this.paint;
        Integer num = this.color;
        paint2.setColor(num != null ? num.intValue() : getThemedColor(Theme.key_chat_messagePanelBackground));
        if (this.allowBlur && SharedConfig.chatBlurEnabled() && this.sizeNotifierFrameLayout != null) {
            if (f2 > 0.0f) {
                canvas.save();
                Path path = this.path;
                if (path == null) {
                    this.path = new Path();
                } else {
                    path.reset();
                }
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(this.rect);
                this.path.addRoundRect(rectF, f2, f2, Path.Direction.CW);
                canvas.clipPath(this.path);
            }
            this.sizeNotifierFrameLayout.drawBlurRect(canvas, getY(), this.rect, this.paint, isReversed);
            if (f2 > 0.0f) {
                canvas.restore();
            }
        } else {
            drawRoundRect(canvas, this.rect, f2);
        }
        canvas.clipRect(this.rect);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public void drawRoundRect(Canvas canvas, android.graphics.Rect rect, float f) {
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(rect);
        canvas.drawRoundRect(rectF, f, f, this.paint);
    }

    public MentionsAdapter getAdapter() {
        return this.adapter;
    }

    public LinearLayoutManager getCurrentLayoutManager() {
        RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
        LinearLayoutManager linearLayoutManager = this.linearLayoutManager;
        return layoutManager == linearLayoutManager ? linearLayoutManager : this.gridLayoutManager;
    }

    public MentionsListView getListView() {
        return this.listView;
    }

    public LinearLayoutManager getNeededLayoutManager() {
        return ((this.adapter.isStickers() || this.adapter.isBotContext()) && this.adapter.isMediaLayout()) ? this.gridLayoutManager : this.linearLayoutManager;
    }

    public boolean isOpen() {
        return this.shown;
    }

    public boolean isReversed() {
        RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
        LinearLayoutManager linearLayoutManager = this.linearLayoutManager;
        return layoutManager == linearLayoutManager && linearLayoutManager.getReverseLayout();
    }

    protected boolean isStories() {
        return false;
    }

    protected void onAnimationScroll() {
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    protected void onClose() {
    }

    protected void onContextClick(TLRPC.BotInlineResult botInlineResult) {
    }

    protected void onContextSearch(boolean z) {
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    protected void onOpen() {
    }

    public void onPanTransitionEnd() {
    }

    public void onPanTransitionStart() {
        this.shouldLiftMentions = isReversed();
    }

    public void onPanTransitionUpdate(float f) {
        if (this.shouldLiftMentions) {
            setTranslationY(f);
        }
    }

    protected void onScrolled(boolean z, boolean z2) {
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    public void setDialogId(long j) {
        this.adapter.setDialogId(j);
    }

    public void setIgnoreLayout(boolean z) {
        this.ignoreLayout = z;
    }

    public void setOverrideColor(int i) {
        this.color = Integer.valueOf(i);
        invalidate();
    }

    public void setReversed(boolean z) {
        if (z != isReversed()) {
            this.scrollToFirst = true;
            this.linearLayoutManager.setReverseLayout(z);
            this.adapter.setIsReversed(z);
        }
    }

    public void updateVisibility(boolean z) {
        if (z) {
            boolean isReversed = isReversed();
            if (!this.shown) {
                this.scrollToFirst = true;
                RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
                LinearLayoutManager linearLayoutManager = this.linearLayoutManager;
                if (layoutManager == linearLayoutManager) {
                    linearLayoutManager.scrollToPositionWithOffset(0, isReversed ? -100000 : 100000);
                }
                if (getVisibility() == 8) {
                    this.hideT = 1.0f;
                    this.listView.setTranslationY(isReversed ? -(this.listViewPadding + AndroidUtilities.dp(12.0f)) : r2.computeVerticalScrollOffset() + this.listViewPadding);
                }
            }
            setVisibility(0);
        } else {
            this.scrollToFirst = false;
        }
        this.shown = z;
        AndroidUtilities.cancelRunOnUIThread(this.updateVisibilityRunnable);
        SpringAnimation springAnimation = this.listViewTranslationAnimator;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        Runnable runnable = this.updateVisibilityRunnable;
        BaseFragment baseFragment = this.baseFragment;
        AndroidUtilities.runOnUIThread(runnable, (baseFragment == null || !baseFragment.getFragmentBeginToShow()) ? 100L : 0L);
        if (z) {
            onOpen();
        } else {
            onClose();
        }
    }

    public void withDelegate(final Delegate delegate) {
        this.delegate = delegate;
        MentionsListView listView = getListView();
        RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                MentionsContainerView.this.lambda$withDelegate$4(delegate, view, i);
            }
        };
        this.mentionsOnItemClickListener = onItemClickListener;
        listView.setOnItemClickListener(onItemClickListener);
        getListView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$withDelegate$5;
                lambda$withDelegate$5 = MentionsContainerView.this.lambda$withDelegate$5(view, motionEvent);
                return lambda$withDelegate$5;
            }
        });
    }
}

package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.GallerySheet;
import org.telegram.ui.Stories.recorder.TimelineView;

public class PhotoViewerCoverEditor extends FrameLayout {
    private float aspectRatio;
    public ButtonWithCounterView button;
    private GallerySheet gallerySheet;
    private Utilities.Callback onGalleryListener;
    public EditCoverButton openGalleryButton;
    private long time;
    public TimelineView timelineView;
    private VideoPlayer videoPlayer;

    public PhotoViewerCoverEditor(final Context context, final Theme.ResourcesProvider resourcesProvider, PhotoViewer photoViewer, BlurringShader.BlurManager blurManager) {
        super(context);
        this.time = -1L;
        this.aspectRatio = 1.39f;
        TimelineView timelineView = new TimelineView(context, null, null, resourcesProvider, blurManager);
        this.timelineView = timelineView;
        timelineView.setCover();
        addView(this.timelineView, LayoutHelper.createFrame(-1, TimelineView.heightDp(), 87, 0.0f, 0.0f, 0.0f, 74.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        this.button = buttonWithCounterView;
        buttonWithCounterView.setText("Save Cover", false);
        addView(this.button, LayoutHelper.createFrame(-1, 48.0f, 87, 10.0f, 10.0f, 10.0f, 10.0f));
        EditCoverButton editCoverButton = new EditCoverButton(context, photoViewer, "Choose from Gallery", true);
        this.openGalleryButton = editCoverButton;
        editCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                PhotoViewerCoverEditor.this.lambda$new$1(context, resourcesProvider, view);
            }
        });
        addView(this.openGalleryButton, LayoutHelper.createFrame(-1, 32.0f, 87, 60.0f, 0.0f, 60.0f, 134.0f));
        this.timelineView.setDelegate(new TimelineView.TimelineDelegate() {
            @Override
            public void onAudioLeftChange(float f) {
                TimelineView.TimelineDelegate.CC.$default$onAudioLeftChange(this, f);
            }

            @Override
            public void onAudioOffsetChange(long j) {
                TimelineView.TimelineDelegate.CC.$default$onAudioOffsetChange(this, j);
            }

            @Override
            public void onAudioRemove() {
                TimelineView.TimelineDelegate.CC.$default$onAudioRemove(this);
            }

            @Override
            public void onAudioRightChange(float f) {
                TimelineView.TimelineDelegate.CC.$default$onAudioRightChange(this, f);
            }

            @Override
            public void onAudioVolumeChange(float f) {
                TimelineView.TimelineDelegate.CC.$default$onAudioVolumeChange(this, f);
            }

            @Override
            public void onProgressChange(long j, boolean z) {
                TimelineView.TimelineDelegate.CC.$default$onProgressChange(this, j, z);
            }

            @Override
            public void onProgressDragChange(boolean z) {
                TimelineView.TimelineDelegate.CC.$default$onProgressDragChange(this, z);
            }

            @Override
            public void onRoundLeftChange(float f) {
                TimelineView.TimelineDelegate.CC.$default$onRoundLeftChange(this, f);
            }

            @Override
            public void onRoundOffsetChange(long j) {
                TimelineView.TimelineDelegate.CC.$default$onRoundOffsetChange(this, j);
            }

            @Override
            public void onRoundRemove() {
                TimelineView.TimelineDelegate.CC.$default$onRoundRemove(this);
            }

            @Override
            public void onRoundRightChange(float f) {
                TimelineView.TimelineDelegate.CC.$default$onRoundRightChange(this, f);
            }

            @Override
            public void onRoundSelectChange(boolean z) {
                TimelineView.TimelineDelegate.CC.$default$onRoundSelectChange(this, z);
            }

            @Override
            public void onRoundVolumeChange(float f) {
                TimelineView.TimelineDelegate.CC.$default$onRoundVolumeChange(this, f);
            }

            @Override
            public void onVideoLeftChange(int i, float f) {
                TimelineView.TimelineDelegate.CC.$default$onVideoLeftChange(this, i, f);
            }

            @Override
            public void onVideoLeftChange(boolean z, float f) {
                if (PhotoViewerCoverEditor.this.videoPlayer == null) {
                    return;
                }
                PhotoViewerCoverEditor.this.time = (f + ((f / 0.96f) * 0.04f)) * ((float) PhotoViewerCoverEditor.this.videoPlayer.getDuration());
                PhotoViewerCoverEditor.this.videoPlayer.seekTo(PhotoViewerCoverEditor.this.time, !z);
            }

            @Override
            public void onVideoOffsetChange(int i, long j) {
                TimelineView.TimelineDelegate.CC.$default$onVideoOffsetChange(this, i, j);
            }

            @Override
            public void onVideoRightChange(int i, float f) {
                TimelineView.TimelineDelegate.CC.$default$onVideoRightChange(this, i, f);
            }

            @Override
            public void onVideoRightChange(boolean z, float f) {
                TimelineView.TimelineDelegate.CC.$default$onVideoRightChange(this, z, f);
            }

            @Override
            public void onVideoSelected(int i) {
                TimelineView.TimelineDelegate.CC.$default$onVideoSelected(this, i);
            }

            @Override
            public void onVideoVolumeChange(float f) {
                TimelineView.TimelineDelegate.CC.$default$onVideoVolumeChange(this, f);
            }

            @Override
            public void onVideoVolumeChange(int i, float f) {
                TimelineView.TimelineDelegate.CC.$default$onVideoVolumeChange(this, i, f);
            }
        });
    }

    public void lambda$new$0() {
        this.gallerySheet = null;
    }

    public void lambda$new$1(Context context, Theme.ResourcesProvider resourcesProvider, View view) {
        if (this.gallerySheet == null) {
            GallerySheet gallerySheet = new GallerySheet(context, resourcesProvider, this.aspectRatio);
            this.gallerySheet = gallerySheet;
            gallerySheet.setOnDismissListener(new Runnable() {
                @Override
                public final void run() {
                    PhotoViewerCoverEditor.this.lambda$new$0();
                }
            });
            this.gallerySheet.setOnGalleryImage(this.onGalleryListener);
        }
        this.gallerySheet.show();
    }

    public void closeGallery() {
        GallerySheet gallerySheet = this.gallerySheet;
        if (gallerySheet != null) {
            gallerySheet.lambda$new$0();
            this.gallerySheet = null;
        }
    }

    public void destroy() {
        this.videoPlayer = null;
        this.timelineView.setVideo(false, null, 0L, 0.0f);
    }

    public long getTime() {
        return this.time;
    }

    public void set(org.telegram.messenger.MediaController.PhotoEntry r11, org.telegram.ui.Components.VideoPlayer r12, org.telegram.ui.ActionBar.Theme.ResourcesProvider r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhotoViewerCoverEditor.set(org.telegram.messenger.MediaController$PhotoEntry, org.telegram.ui.Components.VideoPlayer, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public void setOnGalleryImage(Utilities.Callback<MediaController.PhotoEntry> callback) {
        this.onGalleryListener = callback;
    }
}

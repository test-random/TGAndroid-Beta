package org.telegram.ui.Stories.recorder;

import org.telegram.ui.Stories.recorder.TimelineView;

public final class TimelineView$VideoThumbsLoader$$ExternalSyntheticLambda0 implements Runnable {
    public final TimelineView.VideoThumbsLoader f$0;

    public TimelineView$VideoThumbsLoader$$ExternalSyntheticLambda0(TimelineView.VideoThumbsLoader videoThumbsLoader) {
        this.f$0 = videoThumbsLoader;
    }

    @Override
    public final void run() {
        this.f$0.retrieveFrame();
    }
}

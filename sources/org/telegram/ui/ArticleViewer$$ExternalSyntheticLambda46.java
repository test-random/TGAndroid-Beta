package org.telegram.ui;

import org.telegram.messenger.Utilities;
import org.telegram.ui.web.BrowserHistory;

public final class ArticleViewer$$ExternalSyntheticLambda46 implements Utilities.Callback {
    public final ArticleViewer f$0;

    public ArticleViewer$$ExternalSyntheticLambda46(ArticleViewer articleViewer) {
        this.f$0 = articleViewer;
    }

    @Override
    public final void run(Object obj) {
        this.f$0.openHistoryEntry((BrowserHistory.Entry) obj);
    }
}

package org.telegram.p009ui.Components;

import org.telegram.p009ui.Components.ChatAttachAlertBotWebViewLayout;
import org.telegram.p009ui.Components.SimpleFloatPropertyCompat;

public final class C2016x21c92f6d implements SimpleFloatPropertyCompat.Setter {
    public static final C2016x21c92f6d INSTANCE = new C2016x21c92f6d();

    private C2016x21c92f6d() {
    }

    @Override
    public final void set(Object obj, float f) {
        ((ChatAttachAlertBotWebViewLayout.WebProgressView) obj).setLoadProgress(f);
    }
}
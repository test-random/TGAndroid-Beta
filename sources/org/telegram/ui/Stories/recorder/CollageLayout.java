package org.telegram.ui.Stories.recorder;

import android.graphics.RectF;
import android.text.TextUtils;
import java.util.ArrayList;
import org.telegram.messenger.BuildVars;

public class CollageLayout {
    private static ArrayList layouts;
    public final int[] columns;
    public final int h;
    public final ArrayList parts = new ArrayList();
    private final String src;
    public final int w;

    public static class Part {
        public final CollageLayout layout;
        public final int x;
        public final int y;

        private Part(CollageLayout collageLayout, int i, int i2) {
            this.layout = collageLayout;
            this.x = i;
            this.y = i2;
        }

        public final float b(float f) {
            return (f / this.layout.h) * (this.y + 1);
        }

        public final void bounds(RectF rectF, float f, float f2) {
            rectF.set(l(f), t(f2), r(f), b(f2));
        }

        public final float h(float f) {
            return f / this.layout.h;
        }

        public final float l(float f) {
            return (f / this.layout.columns[this.y]) * this.x;
        }

        public final float r(float f) {
            return (f / this.layout.columns[this.y]) * (this.x + 1);
        }

        public final float t(float f) {
            return (f / this.layout.h) * this.y;
        }

        public final float w(float f) {
            return f / this.layout.columns[this.y];
        }
    }

    public CollageLayout(String str) {
        str = str == null ? "." : str;
        this.src = str;
        String[] split = str.split("/");
        int length = split.length;
        this.h = length;
        this.columns = new int[length];
        int i = 0;
        for (int i2 = 0; i2 < split.length; i2++) {
            this.columns[i2] = split[i2].length();
            i = Math.max(i, split[i2].length());
        }
        this.w = i;
        for (int i3 = 0; i3 < split.length; i3++) {
            for (int i4 = 0; i4 < split[i3].length(); i4++) {
                this.parts.add(new Part(i4, i3));
            }
        }
    }

    public static ArrayList getLayouts() {
        if (layouts == null) {
            ArrayList arrayList = new ArrayList();
            layouts = arrayList;
            arrayList.add(new CollageLayout("./."));
            layouts.add(new CollageLayout(".."));
            layouts.add(new CollageLayout("../."));
            layouts.add(new CollageLayout("./.."));
            layouts.add(new CollageLayout("././."));
            layouts.add(new CollageLayout("..."));
            layouts.add(new CollageLayout("../.."));
            layouts.add(new CollageLayout("./../.."));
            layouts.add(new CollageLayout("../../."));
            layouts.add(new CollageLayout("../../.."));
            if (BuildVars.DEBUG_PRIVATE_VERSION) {
                layouts.add(new CollageLayout("../../../.."));
                layouts.add(new CollageLayout(".../.../..."));
                layouts.add(new CollageLayout("..../..../...."));
                layouts.add(new CollageLayout(".../.../.../..."));
            }
        }
        return layouts;
    }

    public CollageLayout delete(int i) {
        if (i < 0 || i >= this.parts.size()) {
            return null;
        }
        ArrayList arrayList = new ArrayList(this.parts);
        arrayList.remove(i);
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            Part part = (Part) arrayList.get(i3);
            if (part.y != i2) {
                sb.append("/");
                i2 = part.y;
            }
            sb.append(".");
        }
        return new CollageLayout(sb.toString());
    }

    public boolean equals(Object obj) {
        if (obj instanceof CollageLayout) {
            return TextUtils.equals(this.src, ((CollageLayout) obj).src);
        }
        return false;
    }

    public String toString() {
        return this.src;
    }
}

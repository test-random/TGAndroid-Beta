package org.telegram.messenger.utils;

import android.graphics.Paint;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import java.util.ArrayDeque;
import java.util.ArrayList;
import org.telegram.messenger.CodeHighlighting;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaDataController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.QuoteSpan;
import org.telegram.ui.Components.URLSpanReplacement;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;

public abstract class CopyUtilities {

    public static class HTMLTagAttributesHandler implements Html.TagHandler, ContentHandler {
        private final TagHandler handler;
        private ArrayDeque tagStatus;
        private Editable text;
        private ContentHandler wrapped;

        public interface TagHandler {
            boolean handleTag(boolean z, String str, Editable editable, Attributes attributes);
        }

        private HTMLTagAttributesHandler(TagHandler tagHandler) {
            this.tagStatus = new ArrayDeque();
            this.handler = tagHandler;
        }

        public static String getValue(Attributes attributes, String str) {
            int length = attributes.getLength();
            for (int i = 0; i < length; i++) {
                if (str.equals(attributes.getLocalName(i))) {
                    return attributes.getValue(i);
                }
            }
            return null;
        }

        @Override
        public void characters(char[] cArr, int i, int i2) {
            this.wrapped.characters(cArr, i, i2);
        }

        @Override
        public void endDocument() {
            this.wrapped.endDocument();
        }

        @Override
        public void endElement(String str, String str2, String str3) {
            if (!((Boolean) this.tagStatus.removeLast()).booleanValue()) {
                this.wrapped.endElement(str, str2, str3);
            }
            this.handler.handleTag(false, str2, this.text, null);
        }

        @Override
        public void endPrefixMapping(String str) {
            this.wrapped.endPrefixMapping(str);
        }

        @Override
        public void handleTag(boolean z, String str, Editable editable, XMLReader xMLReader) {
            if (this.wrapped == null) {
                this.text = editable;
                this.wrapped = xMLReader.getContentHandler();
                xMLReader.setContentHandler(this);
                this.tagStatus.addLast(Boolean.FALSE);
            }
        }

        @Override
        public void ignorableWhitespace(char[] cArr, int i, int i2) {
            this.wrapped.ignorableWhitespace(cArr, i, i2);
        }

        @Override
        public void processingInstruction(String str, String str2) {
            this.wrapped.processingInstruction(str, str2);
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.wrapped.setDocumentLocator(locator);
        }

        @Override
        public void skippedEntity(String str) {
            this.wrapped.skippedEntity(str);
        }

        @Override
        public void startDocument() {
            this.wrapped.startDocument();
        }

        @Override
        public void startElement(String str, String str2, String str3, Attributes attributes) {
            boolean handleTag = this.handler.handleTag(true, str2, this.text, attributes);
            this.tagStatus.addLast(Boolean.valueOf(handleTag));
            if (handleTag) {
                return;
            }
            this.wrapped.startElement(str, str2, str3, attributes);
        }

        @Override
        public void startPrefixMapping(String str, String str2) {
            this.wrapped.startPrefixMapping(str, str2);
        }
    }

    public static class HTMLTagHandler implements HTMLTagAttributesHandler.TagHandler {
        private HTMLTagHandler() {
        }

        private Object getLast(Editable editable, Class cls) {
            Object[] spans = editable.getSpans(0, editable.length(), cls);
            if (spans.length == 0) {
                return null;
            }
            for (int length = spans.length; length > 0; length--) {
                int i = length - 1;
                if (editable.getSpanFlags(spans[i]) == 17) {
                    return spans[i];
                }
            }
            return null;
        }

        private ParsedSpan getLast(Editable editable, Class cls, int i) {
            ParsedSpan[] parsedSpanArr = (ParsedSpan[]) editable.getSpans(0, editable.length(), cls);
            if (parsedSpanArr.length == 0) {
                return null;
            }
            for (int length = parsedSpanArr.length; length > 0; length--) {
                int i2 = length - 1;
                if (editable.getSpanFlags(parsedSpanArr[i2]) == 17) {
                    ParsedSpan parsedSpan = parsedSpanArr[i2];
                    if (parsedSpan.type == i) {
                        return parsedSpan;
                    }
                }
            }
            return null;
        }

        @Override
        public boolean handleTag(boolean z, String str, Editable editable, Attributes attributes) {
            int i = 0;
            int i2 = 1;
            if (str.startsWith("animated-emoji")) {
                if (z) {
                    String value = HTMLTagAttributesHandler.getValue(attributes, "data-document-id");
                    if (value != null) {
                        editable.setSpan(new AnimatedEmojiSpan(Long.parseLong(value), (Paint.FontMetricsInt) null), editable.length(), editable.length(), 17);
                        return true;
                    }
                } else {
                    AnimatedEmojiSpan animatedEmojiSpan = (AnimatedEmojiSpan) getLast(editable, AnimatedEmojiSpan.class);
                    if (animatedEmojiSpan != null) {
                        int spanStart = editable.getSpanStart(animatedEmojiSpan);
                        editable.removeSpan(animatedEmojiSpan);
                        if (spanStart != editable.length()) {
                            editable.setSpan(animatedEmojiSpan, spanStart, editable.length(), 33);
                        }
                        return true;
                    }
                }
            } else if (str.equals("spoiler")) {
                if (z) {
                    editable.setSpan(new ParsedSpan(i), editable.length(), editable.length(), 17);
                    return true;
                }
                ParsedSpan last = getLast(editable, ParsedSpan.class, 0);
                if (last != null) {
                    int spanStart2 = editable.getSpanStart(last);
                    editable.removeSpan(last);
                    if (spanStart2 != editable.length()) {
                        editable.setSpan(last, spanStart2, editable.length(), 33);
                    }
                    return true;
                }
            } else if (str.equals("pre")) {
                if (z) {
                    editable.setSpan(new ParsedSpan(i2, HTMLTagAttributesHandler.getValue(attributes, "lang")), editable.length(), editable.length(), 17);
                    return true;
                }
                ParsedSpan last2 = getLast(editable, ParsedSpan.class, 1);
                if (last2 != null) {
                    int spanStart3 = editable.getSpanStart(last2);
                    editable.removeSpan(last2);
                    if (spanStart3 != editable.length()) {
                        editable.setSpan(last2, spanStart3, editable.length(), 33);
                    }
                    return true;
                }
            } else if (str.equals("blockquote")) {
                int i3 = 2;
                if (z) {
                    editable.setSpan(new ParsedSpan(i3), editable.length(), editable.length(), 17);
                    return true;
                }
                ParsedSpan last3 = getLast(editable, ParsedSpan.class, 2);
                if (last3 != null) {
                    int spanStart4 = editable.getSpanStart(last3);
                    editable.removeSpan(last3);
                    if (spanStart4 != editable.length()) {
                        editable.setSpan(last3, spanStart4, editable.length(), 33);
                    }
                    return true;
                }
            } else if (str.equals("details")) {
                int i4 = 3;
                if (z) {
                    editable.setSpan(new ParsedSpan(i4), editable.length(), editable.length(), 17);
                    return true;
                }
                ParsedSpan last4 = getLast(editable, ParsedSpan.class, 3);
                if (last4 != null) {
                    int spanStart5 = editable.getSpanStart(last4);
                    editable.removeSpan(last4);
                    if (spanStart5 != editable.length()) {
                        editable.setSpan(last4, spanStart5, editable.length(), 33);
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public static class ParsedSpan {
        final String lng;
        final int type;

        private ParsedSpan(int i) {
            this.type = i;
            this.lng = null;
        }

        private ParsedSpan(int i, String str) {
            this.type = i;
            this.lng = str;
        }
    }

    public static Spannable fromHTML(String str) {
        TLRPC.MessageEntity entityStartEnd;
        TLRPC.MessageEntity tL_messageEntityPre;
        try {
            Spanned fromHtml = Build.VERSION.SDK_INT >= 24 ? Html.fromHtml("<inject>" + str + "</inject>", 63, null, new HTMLTagAttributesHandler(new HTMLTagHandler())) : Html.fromHtml("<inject>" + str + "</inject>", null, new HTMLTagAttributesHandler(new HTMLTagHandler()));
            if (fromHtml == null) {
                return null;
            }
            Object[] spans = fromHtml.getSpans(0, fromHtml.length(), Object.class);
            ArrayList arrayList = new ArrayList(spans.length);
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            for (Object obj : spans) {
                int spanStart = fromHtml.getSpanStart(obj);
                int spanEnd = fromHtml.getSpanEnd(obj);
                if (obj instanceof StyleSpan) {
                    int style = ((StyleSpan) obj).getStyle();
                    if ((style & 1) > 0) {
                        arrayList.add(setEntityStartEnd(new TLRPC.TL_messageEntityBold(), spanStart, spanEnd));
                    }
                    if ((style & 2) > 0) {
                        tL_messageEntityPre = new TLRPC.TL_messageEntityItalic();
                        entityStartEnd = setEntityStartEnd(tL_messageEntityPre, spanStart, spanEnd);
                    }
                } else {
                    if (obj instanceof UnderlineSpan) {
                        tL_messageEntityPre = new TLRPC.TL_messageEntityUnderline();
                    } else if (obj instanceof StrikethroughSpan) {
                        tL_messageEntityPre = new TLRPC.TL_messageEntityStrike();
                    } else {
                        if (obj instanceof ParsedSpan) {
                            ParsedSpan parsedSpan = (ParsedSpan) obj;
                            int i = parsedSpan.type;
                            if (i == 0) {
                                tL_messageEntityPre = new TLRPC.TL_messageEntitySpoiler();
                            } else if (i == 1) {
                                if (TextUtils.isEmpty(parsedSpan.lng)) {
                                    tL_messageEntityPre = new TLRPC.TL_messageEntityPre();
                                } else {
                                    arrayList2.add(parsedSpan);
                                }
                            } else if (i == 2 || i == 3) {
                                arrayList3.add(parsedSpan);
                            }
                        } else if (obj instanceof AnimatedEmojiSpan) {
                            TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji = new TLRPC.TL_messageEntityCustomEmoji();
                            AnimatedEmojiSpan animatedEmojiSpan = (AnimatedEmojiSpan) obj;
                            tL_messageEntityCustomEmoji.document_id = animatedEmojiSpan.documentId;
                            tL_messageEntityCustomEmoji.document = animatedEmojiSpan.document;
                            entityStartEnd = setEntityStartEnd(tL_messageEntityCustomEmoji, spanStart, spanEnd);
                        }
                    }
                    entityStartEnd = setEntityStartEnd(tL_messageEntityPre, spanStart, spanEnd);
                }
                arrayList.add(entityStartEnd);
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(fromHtml.toString());
            MediaDataController.addTextStyleRuns((ArrayList<TLRPC.MessageEntity>) arrayList, spannableStringBuilder, spannableStringBuilder);
            for (Object obj2 : spans) {
                if (obj2 instanceof URLSpan) {
                    int spanStart2 = fromHtml.getSpanStart(obj2);
                    int spanEnd2 = fromHtml.getSpanEnd(obj2);
                    String charSequence = fromHtml.subSequence(spanStart2, spanEnd2).toString();
                    String url = ((URLSpan) obj2).getURL();
                    spannableStringBuilder.setSpan(charSequence.equals(url) ? new URLSpan(url) : new URLSpanReplacement(url), spanStart2, spanEnd2, 33);
                }
            }
            MediaDataController.addAnimatedEmojiSpans(arrayList, spannableStringBuilder, null);
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                ParsedSpan parsedSpan2 = (ParsedSpan) arrayList2.get(i2);
                int spanStart3 = fromHtml.getSpanStart(parsedSpan2);
                int spanEnd3 = fromHtml.getSpanEnd(parsedSpan2);
                spannableStringBuilder.setSpan(new CodeHighlighting.Span(true, 0, null, parsedSpan2.lng, spannableStringBuilder.subSequence(spanStart3, spanEnd3).toString()), spanStart3, spanEnd3, 33);
            }
            for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                ParsedSpan parsedSpan3 = (ParsedSpan) arrayList3.get(i3);
                QuoteSpan.putQuoteToEditable(spannableStringBuilder, fromHtml.getSpanStart(parsedSpan3), fromHtml.getSpanEnd(parsedSpan3), parsedSpan3.type == 3);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            FileLog.e("Html.fromHtml", e);
            return null;
        }
    }

    private static TLRPC.MessageEntity setEntityStartEnd(TLRPC.MessageEntity messageEntity, int i, int i2) {
        messageEntity.offset = i;
        messageEntity.length = i2 - i;
        return messageEntity;
    }
}

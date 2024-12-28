package org.telegram.ui.Stories.recorder;

import android.text.SpannableString;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Stories.recorder.CollageLayout;

public class DraftsController {
    public final int currentAccount;
    public final ArrayList drafts = new ArrayList();
    private File draftsFolder;
    private boolean loaded;
    private boolean loadedFailed;
    private boolean loading;
    private boolean loadingFailed;

    public static class StoryDraft {
        public String audioAuthor;
        public long audioDuration;
        public float audioLeft;
        public long audioOffset;
        public String audioPath;
        public float audioRight;
        public String audioTitle;
        public float audioVolume;
        public long averageDuration;
        public TLRPC.InputMedia botEdit;
        public long botId;
        public String botLang;
        public String caption;
        public ArrayList captionEntities;
        public CollageLayout collage;
        public ArrayList collageParts;
        public MediaController.CropState crop;
        public long date;
        public long duration;
        public long editDocumentId;
        public long editExpireDate;
        public long editPhotoId;
        public int editStoryId;
        public long editStoryPeerId;
        public TLRPC.TL_error error;
        public String file;
        public boolean fileDeletable;
        private String filterFilePath;
        private MediaController.SavedFilterState filterState;
        public String fullThumb;
        public int gradientBottomColor;
        public int gradientTopColor;
        public int height;
        public long id;
        public int invert;
        public boolean isEdit;
        public boolean isError;
        public boolean isVideo;
        public long left;
        public final float[] matrixValues;
        public ArrayList mediaEntities;
        public boolean muted;
        public int orientation;
        public String paintEntitiesFilePath;
        public String paintFilePath;
        public TLRPC.InputPeer peer;
        private int period;
        public final ArrayList privacyRules;
        public int resultHeight;
        public int resultWidth;
        public long right;
        public long roundDuration;
        public float roundLeft;
        public long roundOffset;
        public String roundPath;
        public float roundRight;
        public String roundThumb;
        public float roundVolume;
        public List stickers;
        public String thumb;
        public float videoVolume;
        public int width;

        public StoryDraft(AbstractSerializedData abstractSerializedData, boolean z) {
            this.matrixValues = new float[9];
            this.privacyRules = new ArrayList();
            this.audioRight = 1.0f;
            this.audioVolume = 1.0f;
            this.roundVolume = 1.0f;
            this.videoVolume = 1.0f;
            if (abstractSerializedData.readInt32(z) != -1318387531) {
                if (z) {
                    throw new RuntimeException("StoryDraft parse error");
                }
                return;
            }
            this.date = abstractSerializedData.readInt64(z);
            String readString = abstractSerializedData.readString(z);
            this.thumb = readString;
            if (readString != null && readString.length() == 0) {
                this.thumb = null;
            }
            this.isVideo = abstractSerializedData.readBool(z);
            String readString2 = abstractSerializedData.readString(z);
            this.file = readString2;
            if (readString2 != null && readString2.length() == 0) {
                this.file = null;
            }
            this.fileDeletable = abstractSerializedData.readBool(z);
            this.muted = abstractSerializedData.readBool(z);
            this.left = abstractSerializedData.readInt64(z);
            this.right = abstractSerializedData.readInt64(z);
            this.orientation = abstractSerializedData.readInt32(z);
            this.invert = abstractSerializedData.readInt32(z);
            this.width = abstractSerializedData.readInt32(z);
            this.height = abstractSerializedData.readInt32(z);
            this.resultWidth = abstractSerializedData.readInt32(z);
            this.resultHeight = abstractSerializedData.readInt32(z);
            this.duration = abstractSerializedData.readInt64(z);
            int i = 0;
            while (true) {
                float[] fArr = this.matrixValues;
                if (i >= fArr.length) {
                    break;
                }
                fArr[i] = abstractSerializedData.readFloat(z);
                i++;
            }
            this.gradientTopColor = abstractSerializedData.readInt32(z);
            this.gradientBottomColor = abstractSerializedData.readInt32(z);
            String readString3 = abstractSerializedData.readString(z);
            this.caption = readString3;
            if (readString3 != null && readString3.length() == 0) {
                this.caption = null;
            }
            if (abstractSerializedData.readInt32(z) != 481674261) {
                if (z) {
                    throw new RuntimeException("Vector magic in StoryDraft parse error (1)");
                }
                return;
            }
            int readInt32 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt32; i2++) {
                if (this.captionEntities == null) {
                    this.captionEntities = new ArrayList();
                }
                this.captionEntities.add(TLRPC.MessageEntity.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
            }
            if (abstractSerializedData.readInt32(z) != 481674261) {
                if (z) {
                    throw new RuntimeException("Vector magic in StoryDraft parse error (2)");
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            this.privacyRules.clear();
            for (int i3 = 0; i3 < readInt322; i3++) {
                this.privacyRules.add(TLRPC.InputPrivacyRule.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
            }
            abstractSerializedData.readBool(z);
            String readString4 = abstractSerializedData.readString(z);
            this.paintFilePath = readString4;
            if (readString4 != null && readString4.length() == 0) {
                this.paintFilePath = null;
            }
            this.averageDuration = abstractSerializedData.readInt64(z);
            if (abstractSerializedData.readInt32(z) != 481674261) {
                if (z) {
                    throw new RuntimeException("Vector magic in StoryDraft parse error (3)");
                }
                return;
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            for (int i4 = 0; i4 < readInt323; i4++) {
                if (this.mediaEntities == null) {
                    this.mediaEntities = new ArrayList();
                }
                this.mediaEntities.add(new VideoEditedInfo.MediaEntity(abstractSerializedData, true, z));
            }
            if (abstractSerializedData.readInt32(z) != 481674261) {
                if (z) {
                    throw new RuntimeException("Vector magic in StoryDraft parse error (4)");
                }
                return;
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            for (int i5 = 0; i5 < readInt324; i5++) {
                if (this.stickers == null) {
                    this.stickers = new ArrayList();
                }
                this.stickers.add(TLRPC.InputDocument.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
            }
            String readString5 = abstractSerializedData.readString(z);
            this.filterFilePath = readString5;
            if (readString5 != null && readString5.length() == 0) {
                this.filterFilePath = null;
            }
            int readInt325 = abstractSerializedData.readInt32(z);
            if (readInt325 == 1450380236) {
                this.filterState = null;
            } else if (readInt325 == -1318387530) {
                MediaController.SavedFilterState savedFilterState = new MediaController.SavedFilterState();
                this.filterState = savedFilterState;
                savedFilterState.readParams(abstractSerializedData, z);
            }
            if (abstractSerializedData.remaining() >= 4) {
                this.period = abstractSerializedData.readInt32(z);
            }
            if (abstractSerializedData.remaining() > 0) {
                if (abstractSerializedData.readInt32(z) != 481674261) {
                    if (z) {
                        throw new RuntimeException("Vector magic in StoryDraft parse error (5)");
                    }
                    return;
                }
                abstractSerializedData.readInt32(z);
            }
            if (abstractSerializedData.remaining() > 0) {
                this.isEdit = abstractSerializedData.readBool(z);
                this.editStoryId = abstractSerializedData.readInt32(z);
                this.editStoryPeerId = abstractSerializedData.readInt64(z);
                this.editExpireDate = abstractSerializedData.readInt64(z);
                this.editPhotoId = abstractSerializedData.readInt64(z);
                this.editDocumentId = abstractSerializedData.readInt64(z);
            }
            if (abstractSerializedData.remaining() > 0) {
                String readString6 = abstractSerializedData.readString(z);
                this.paintEntitiesFilePath = readString6;
                if (readString6 != null && readString6.length() == 0) {
                    this.paintEntitiesFilePath = null;
                }
            }
            if (abstractSerializedData.remaining() > 0) {
                this.isError = abstractSerializedData.readBool(z);
                int readInt326 = abstractSerializedData.readInt32(z);
                this.error = readInt326 != 1450380236 ? TLRPC.TL_error.TLdeserialize(abstractSerializedData, readInt326, z) : null;
                this.fullThumb = abstractSerializedData.readString(z);
            }
            if (abstractSerializedData.remaining() > 0 && abstractSerializedData.readInt32(z) == -1739392570) {
                this.audioPath = abstractSerializedData.readString(z);
                if (abstractSerializedData.readInt32(z) == -1222740358) {
                    this.audioAuthor = abstractSerializedData.readString(z);
                }
                if (abstractSerializedData.readInt32(z) == -1222740358) {
                    this.audioTitle = abstractSerializedData.readString(z);
                }
                this.audioDuration = abstractSerializedData.readInt64(z);
                this.audioOffset = abstractSerializedData.readInt64(z);
                this.audioLeft = abstractSerializedData.readFloat(z);
                this.audioRight = abstractSerializedData.readFloat(z);
                this.audioVolume = abstractSerializedData.readFloat(z);
            }
            if (abstractSerializedData.remaining() > 0) {
                this.peer = TLRPC.InputPeer.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if (abstractSerializedData.remaining() > 0 && abstractSerializedData.readInt32(z) == 1137015880) {
                this.roundPath = abstractSerializedData.readString(z);
                this.roundDuration = abstractSerializedData.readInt64(z);
                this.roundOffset = abstractSerializedData.readInt64(z);
                this.roundLeft = abstractSerializedData.readFloat(z);
                this.roundRight = abstractSerializedData.readFloat(z);
                this.roundVolume = abstractSerializedData.readFloat(z);
            }
            if (abstractSerializedData.remaining() > 0) {
                this.videoVolume = abstractSerializedData.readFloat(z);
            }
            if (abstractSerializedData.remaining() > 0) {
                this.botId = abstractSerializedData.readInt64(z);
                this.botLang = abstractSerializedData.readString(z);
                int readInt327 = abstractSerializedData.readInt32(z);
                if (readInt327 != 1450380236) {
                    this.botEdit = TLRPC.InputMedia.TLdeserialize(abstractSerializedData, readInt327, z);
                }
            }
            if (abstractSerializedData.remaining() > 0 && abstractSerializedData.readInt32(z) == -559038737) {
                this.collage = new CollageLayout(abstractSerializedData.readString(z));
                this.collageParts = new ArrayList();
                for (int i6 = 0; i6 < this.collage.parts.size(); i6++) {
                    VideoEditedInfo.Part part = new VideoEditedInfo.Part();
                    part.readParams(abstractSerializedData, z);
                    part.part = (CollageLayout.Part) this.collage.parts.get(i6);
                    this.collageParts.add(part);
                }
            }
            if (abstractSerializedData.remaining() <= 0 || abstractSerializedData.readInt32(z) != 1151577037) {
                return;
            }
            MediaController.CropState cropState = new MediaController.CropState();
            this.crop = cropState;
            cropState.readParams(abstractSerializedData, z);
        }

        public StoryDraft(StoryEntry storyEntry) {
            float[] fArr = new float[9];
            this.matrixValues = fArr;
            ArrayList arrayList = new ArrayList();
            this.privacyRules = arrayList;
            this.audioRight = 1.0f;
            this.audioVolume = 1.0f;
            this.roundVolume = 1.0f;
            this.videoVolume = 1.0f;
            this.id = storyEntry.draftId;
            this.date = storyEntry.draftDate;
            File file = storyEntry.draftThumbFile;
            this.thumb = file == null ? "" : file.toString();
            File file2 = storyEntry.uploadThumbFile;
            this.fullThumb = file2 == null ? "" : file2.toString();
            this.isVideo = storyEntry.isVideo;
            File file3 = storyEntry.file;
            this.file = file3 == null ? "" : file3.toString();
            this.fileDeletable = storyEntry.fileDeletable;
            this.muted = storyEntry.muted;
            float f = storyEntry.left;
            long j = storyEntry.duration;
            float f2 = (float) j;
            this.left = f * f2;
            this.right = storyEntry.right * f2;
            this.orientation = storyEntry.orientation;
            this.invert = storyEntry.invert;
            this.width = storyEntry.width;
            this.height = storyEntry.height;
            this.crop = storyEntry.crop;
            this.resultWidth = storyEntry.resultWidth;
            this.resultHeight = storyEntry.resultHeight;
            this.duration = j;
            storyEntry.matrix.getValues(fArr);
            this.gradientTopColor = storyEntry.gradientTopColor;
            this.gradientBottomColor = storyEntry.gradientBottomColor;
            CharSequence charSequence = storyEntry.caption;
            this.captionEntities = storyEntry.captionEntitiesAllowed ? MediaDataController.getInstance(storyEntry.currentAccount).getEntities(new CharSequence[]{charSequence}, true) : null;
            this.caption = charSequence == null ? "" : charSequence.toString();
            arrayList.addAll(storyEntry.privacyRules);
            File file4 = storyEntry.paintFile;
            this.paintFilePath = file4 == null ? "" : file4.toString();
            File file5 = storyEntry.paintEntitiesFile;
            this.paintEntitiesFilePath = file5 == null ? "" : file5.toString();
            this.averageDuration = storyEntry.averageDuration;
            this.mediaEntities = storyEntry.mediaEntities;
            this.stickers = storyEntry.stickers;
            File file6 = storyEntry.filterFile;
            this.filterFilePath = file6 != null ? file6.toString() : "";
            this.filterState = storyEntry.filterState;
            this.period = storyEntry.period;
            this.isError = storyEntry.isError;
            this.error = storyEntry.error;
            this.audioPath = storyEntry.audioPath;
            this.audioAuthor = storyEntry.audioAuthor;
            this.audioTitle = storyEntry.audioTitle;
            this.audioDuration = storyEntry.audioDuration;
            this.audioOffset = storyEntry.audioOffset;
            this.audioLeft = storyEntry.audioLeft;
            this.audioRight = storyEntry.audioRight;
            this.audioVolume = storyEntry.audioVolume;
            File file7 = storyEntry.round;
            this.roundPath = file7 != null ? file7.getAbsolutePath() : null;
            this.roundThumb = storyEntry.roundThumb;
            this.roundDuration = storyEntry.roundDuration;
            this.roundOffset = storyEntry.roundOffset;
            this.roundLeft = storyEntry.roundLeft;
            this.roundRight = storyEntry.roundRight;
            this.roundVolume = storyEntry.roundVolume;
            this.videoVolume = storyEntry.videoVolume;
            this.peer = storyEntry.peer;
            this.botId = storyEntry.botId;
            this.botLang = storyEntry.botLang;
            this.botEdit = storyEntry.editingBotPreview;
            this.collage = storyEntry.collage;
            this.collageParts = VideoEditedInfo.Part.toParts(storyEntry);
        }

        public int getObjectSize() {
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(true);
            toStream(nativeByteBuffer);
            return nativeByteBuffer.length();
        }

        public StoryEntry toEntry() {
            CharSequence charSequence;
            StoryEntry storyEntry = new StoryEntry();
            storyEntry.draftId = this.id;
            storyEntry.isDraft = true;
            storyEntry.draftDate = this.date;
            if (!TextUtils.isEmpty(this.thumb)) {
                storyEntry.draftThumbFile = new File(this.thumb);
            }
            if (!TextUtils.isEmpty(this.fullThumb)) {
                storyEntry.uploadThumbFile = new File(this.fullThumb);
            }
            storyEntry.isVideo = this.isVideo;
            if (this.file != null) {
                storyEntry.file = new File(this.file);
            }
            storyEntry.fileDeletable = this.fileDeletable;
            storyEntry.muted = this.muted;
            long j = this.duration;
            storyEntry.duration = j;
            if (j > 0) {
                float f = (float) j;
                storyEntry.left = ((float) this.left) / f;
                storyEntry.right = ((float) this.right) / f;
            } else {
                storyEntry.left = 0.0f;
                storyEntry.right = 1.0f;
            }
            storyEntry.orientation = this.orientation;
            storyEntry.invert = this.invert;
            storyEntry.width = this.width;
            storyEntry.height = this.height;
            storyEntry.crop = this.crop;
            storyEntry.resultWidth = this.resultWidth;
            storyEntry.resultHeight = this.resultHeight;
            storyEntry.matrix.setValues(this.matrixValues);
            storyEntry.gradientTopColor = this.gradientTopColor;
            storyEntry.gradientBottomColor = this.gradientBottomColor;
            if (this.caption != null) {
                SpannableString spannableString = new SpannableString(this.caption);
                if (Theme.chat_msgTextPaint == null) {
                    Theme.createCommonMessageResources();
                }
                charSequence = Emoji.replaceEmoji(spannableString, Theme.chat_msgTextPaint.getFontMetricsInt(), true);
                MessageObject.addEntitiesToText(charSequence, this.captionEntities, true, false, true, false);
            } else {
                charSequence = "";
            }
            storyEntry.caption = charSequence;
            storyEntry.privacyRules.clear();
            storyEntry.privacyRules.addAll(this.privacyRules);
            if (this.paintFilePath != null) {
                storyEntry.paintFile = new File(this.paintFilePath);
            }
            if (this.paintEntitiesFilePath != null) {
                storyEntry.paintEntitiesFile = new File(this.paintEntitiesFilePath);
            }
            storyEntry.averageDuration = this.averageDuration;
            storyEntry.mediaEntities = this.mediaEntities;
            storyEntry.stickers = this.stickers;
            if (this.filterFilePath != null) {
                storyEntry.filterFile = new File(this.filterFilePath);
            }
            storyEntry.filterState = this.filterState;
            storyEntry.period = this.period;
            storyEntry.isEdit = this.isEdit;
            storyEntry.editStoryId = this.editStoryId;
            storyEntry.editStoryPeerId = this.editStoryPeerId;
            storyEntry.editExpireDate = this.editExpireDate;
            storyEntry.editPhotoId = this.editPhotoId;
            storyEntry.editDocumentId = this.editDocumentId;
            storyEntry.isError = this.isError;
            storyEntry.error = this.error;
            storyEntry.audioPath = this.audioPath;
            storyEntry.audioAuthor = this.audioAuthor;
            storyEntry.audioTitle = this.audioTitle;
            storyEntry.audioDuration = this.audioDuration;
            storyEntry.audioOffset = this.audioOffset;
            storyEntry.audioLeft = this.audioLeft;
            storyEntry.audioRight = this.audioRight;
            storyEntry.audioVolume = this.audioVolume;
            if (this.roundPath != null) {
                storyEntry.round = new File(this.roundPath);
            }
            storyEntry.roundThumb = this.roundThumb;
            storyEntry.roundDuration = this.roundDuration;
            storyEntry.roundOffset = this.roundOffset;
            storyEntry.roundLeft = this.roundLeft;
            storyEntry.roundRight = this.roundRight;
            storyEntry.roundVolume = this.roundVolume;
            storyEntry.videoVolume = this.videoVolume;
            storyEntry.peer = this.peer;
            storyEntry.botId = this.botId;
            storyEntry.botLang = this.botLang;
            storyEntry.editingBotPreview = this.botEdit;
            storyEntry.collage = this.collage;
            storyEntry.collageContent = VideoEditedInfo.Part.toStoryEntries(this.collageParts);
            return storyEntry;
        }

        public void toStream(AbstractSerializedData abstractSerializedData) {
            ArrayList arrayList;
            abstractSerializedData.writeInt32(-1318387531);
            abstractSerializedData.writeInt64(this.date);
            abstractSerializedData.writeString(this.thumb);
            abstractSerializedData.writeBool(this.isVideo);
            abstractSerializedData.writeString(this.file);
            abstractSerializedData.writeBool(this.fileDeletable);
            abstractSerializedData.writeBool(this.muted);
            abstractSerializedData.writeInt64(this.left);
            abstractSerializedData.writeInt64(this.right);
            abstractSerializedData.writeInt32(this.orientation);
            abstractSerializedData.writeInt32(this.invert);
            abstractSerializedData.writeInt32(this.width);
            abstractSerializedData.writeInt32(this.height);
            abstractSerializedData.writeInt32(this.resultWidth);
            abstractSerializedData.writeInt32(this.resultHeight);
            abstractSerializedData.writeInt64(this.duration);
            int i = 0;
            while (true) {
                float[] fArr = this.matrixValues;
                if (i >= fArr.length) {
                    break;
                }
                abstractSerializedData.writeFloat(fArr[i]);
                i++;
            }
            abstractSerializedData.writeInt32(this.gradientTopColor);
            abstractSerializedData.writeInt32(this.gradientBottomColor);
            abstractSerializedData.writeString(this.caption);
            abstractSerializedData.writeInt32(481674261);
            ArrayList arrayList2 = this.captionEntities;
            abstractSerializedData.writeInt32(arrayList2 == null ? 0 : arrayList2.size());
            if (this.captionEntities != null) {
                for (int i2 = 0; i2 < this.captionEntities.size(); i2++) {
                    ((TLRPC.MessageEntity) this.captionEntities.get(i2)).serializeToStream(abstractSerializedData);
                }
            }
            abstractSerializedData.writeInt32(481674261);
            ArrayList arrayList3 = this.privacyRules;
            abstractSerializedData.writeInt32(arrayList3 == null ? 0 : arrayList3.size());
            if (this.privacyRules != null) {
                for (int i3 = 0; i3 < this.privacyRules.size(); i3++) {
                    ((TLRPC.InputPrivacyRule) this.privacyRules.get(i3)).serializeToStream(abstractSerializedData);
                }
            }
            abstractSerializedData.writeBool(false);
            abstractSerializedData.writeString(this.paintFilePath);
            abstractSerializedData.writeInt64(this.averageDuration);
            abstractSerializedData.writeInt32(481674261);
            ArrayList arrayList4 = this.mediaEntities;
            abstractSerializedData.writeInt32(arrayList4 == null ? 0 : arrayList4.size());
            if (this.mediaEntities != null) {
                for (int i4 = 0; i4 < this.mediaEntities.size(); i4++) {
                    ((VideoEditedInfo.MediaEntity) this.mediaEntities.get(i4)).serializeTo(abstractSerializedData, true);
                }
            }
            abstractSerializedData.writeInt32(481674261);
            List list = this.stickers;
            abstractSerializedData.writeInt32(list == null ? 0 : list.size());
            if (this.stickers != null) {
                for (int i5 = 0; i5 < this.stickers.size(); i5++) {
                    ((TLRPC.InputDocument) this.stickers.get(i5)).serializeToStream(abstractSerializedData);
                }
            }
            String str = this.filterFilePath;
            if (str == null) {
                str = "";
            }
            abstractSerializedData.writeString(str);
            if (this.filterState == null) {
                abstractSerializedData.writeInt32(1450380236);
            } else {
                abstractSerializedData.writeInt32(-1318387530);
                this.filterState.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(this.period);
            abstractSerializedData.writeInt32(481674261);
            abstractSerializedData.writeInt32(0);
            abstractSerializedData.writeBool(this.isEdit);
            abstractSerializedData.writeInt32(this.editStoryId);
            abstractSerializedData.writeInt64(this.editStoryPeerId);
            abstractSerializedData.writeInt64(this.editExpireDate);
            abstractSerializedData.writeInt64(this.editPhotoId);
            abstractSerializedData.writeInt64(this.editDocumentId);
            abstractSerializedData.writeString(this.paintEntitiesFilePath);
            abstractSerializedData.writeBool(this.isError);
            TLRPC.TL_error tL_error = this.error;
            if (tL_error == null) {
                abstractSerializedData.writeInt32(1450380236);
            } else {
                tL_error.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeString(this.fullThumb);
            if (this.audioPath == null) {
                abstractSerializedData.writeInt32(1450380236);
            } else {
                abstractSerializedData.writeInt32(-1739392570);
                abstractSerializedData.writeString(this.audioPath);
                if (this.audioAuthor == null) {
                    abstractSerializedData.writeInt32(1450380236);
                } else {
                    abstractSerializedData.writeInt32(-1222740358);
                    abstractSerializedData.writeString(this.audioAuthor);
                }
                if (this.audioTitle == null) {
                    abstractSerializedData.writeInt32(1450380236);
                } else {
                    abstractSerializedData.writeInt32(-1222740358);
                    abstractSerializedData.writeString(this.audioTitle);
                }
                abstractSerializedData.writeInt64(this.audioDuration);
                abstractSerializedData.writeInt64(this.audioOffset);
                abstractSerializedData.writeFloat(this.audioLeft);
                abstractSerializedData.writeFloat(this.audioRight);
                abstractSerializedData.writeFloat(this.audioVolume);
            }
            TLRPC.InputPeer inputPeer = this.peer;
            if (inputPeer != null) {
                inputPeer.serializeToStream(abstractSerializedData);
            } else {
                new TLRPC.TL_inputPeerSelf().serializeToStream(abstractSerializedData);
            }
            if (TextUtils.isEmpty(this.roundPath)) {
                abstractSerializedData.writeInt32(1450380236);
            } else {
                abstractSerializedData.writeInt32(1137015880);
                abstractSerializedData.writeString(this.roundPath);
                abstractSerializedData.writeInt64(this.roundDuration);
                abstractSerializedData.writeInt64(this.roundOffset);
                abstractSerializedData.writeFloat(this.roundLeft);
                abstractSerializedData.writeFloat(this.roundRight);
                abstractSerializedData.writeFloat(this.roundVolume);
            }
            abstractSerializedData.writeFloat(this.videoVolume);
            abstractSerializedData.writeInt64(this.botId);
            String str2 = this.botLang;
            abstractSerializedData.writeString(str2 != null ? str2 : "");
            TLRPC.InputMedia inputMedia = this.botEdit;
            if (inputMedia == null) {
                abstractSerializedData.writeInt32(1450380236);
            } else {
                inputMedia.serializeToStream(abstractSerializedData);
            }
            CollageLayout collageLayout = this.collage;
            if (collageLayout == null || collageLayout.parts.size() <= 1 || (arrayList = this.collageParts) == null || arrayList.size() <= 1) {
                abstractSerializedData.writeInt32(1450380236);
            } else {
                abstractSerializedData.writeInt32(-559038737);
                abstractSerializedData.writeString(this.collage.toString());
                Iterator it = this.collageParts.iterator();
                while (it.hasNext()) {
                    ((VideoEditedInfo.Part) it.next()).serializeToStream(abstractSerializedData);
                }
            }
            MediaController.CropState cropState = this.crop;
            if (cropState == null) {
                abstractSerializedData.writeInt32(1450380236);
            } else {
                cropState.serializeToStream(abstractSerializedData);
            }
        }
    }

    public DraftsController(int i) {
        this.currentAccount = i;
        loadFailed();
    }

    private void append(final StoryDraft storyDraft) {
        String str;
        StringBuilder sb;
        long j;
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("StoryDraft append ");
        sb2.append(storyDraft.id);
        sb2.append(" (edit=");
        sb2.append(storyDraft.isEdit);
        if (storyDraft.isEdit) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(", storyId=");
            sb3.append(storyDraft.editStoryId);
            sb3.append(", ");
            if (storyDraft.editDocumentId != 0) {
                sb = new StringBuilder();
                sb.append("documentId=");
                j = storyDraft.editDocumentId;
            } else {
                sb = new StringBuilder();
                sb.append("photoId=");
                j = storyDraft.editPhotoId;
            }
            sb.append(j);
            sb3.append(sb.toString());
            sb3.append(", expireDate=");
            sb3.append(storyDraft.editExpireDate);
            str = sb3.toString();
        } else {
            str = "";
        }
        sb2.append(str);
        sb2.append(", now=");
        sb2.append(System.currentTimeMillis());
        sb2.append(")");
        FileLog.d(sb2.toString());
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                DraftsController.lambda$append$5(MessagesStorage.this, storyDraft);
            }
        });
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesDraftsUpdated, new Object[0]);
    }

    public static void lambda$append$5(MessagesStorage messagesStorage, StoryDraft storyDraft) {
        SQLiteDatabase database;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                database = messagesStorage.getDatabase();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            if (database == null) {
                return;
            }
            sQLitePreparedStatement = database.executeFast("INSERT INTO story_drafts VALUES (?, ?, ?, ?)");
            sQLitePreparedStatement.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(storyDraft.getObjectSize());
            storyDraft.toStream(nativeByteBuffer);
            int i = 1;
            sQLitePreparedStatement.bindLong(1, storyDraft.id);
            sQLitePreparedStatement.bindLong(2, storyDraft.date);
            sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer);
            if (!storyDraft.isEdit) {
                i = storyDraft.isError ? 2 : 0;
            }
            sQLitePreparedStatement.bindInteger(4, i);
            sQLitePreparedStatement.step();
            nativeByteBuffer.reuse();
            sQLitePreparedStatement.dispose();
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public static void lambda$delete$6(MessagesStorage messagesStorage, ArrayList arrayList) {
        try {
            SQLiteDatabase database = messagesStorage.getDatabase();
            if (database == null) {
                return;
            }
            database.executeFast("DELETE FROM story_drafts WHERE id IN (" + TextUtils.join(", ", arrayList) + ")").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$edit$4(MessagesStorage messagesStorage, StoryDraft storyDraft) {
        SQLiteDatabase database;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                database = messagesStorage.getDatabase();
            } catch (Exception e) {
                FileLog.e(e);
                if (sQLitePreparedStatement == null) {
                    return;
                }
            }
            if (database == null) {
                return;
            }
            sQLitePreparedStatement = database.executeFast("REPLACE INTO story_drafts VALUES (?, ?, ?, ?)");
            sQLitePreparedStatement.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(storyDraft.getObjectSize());
            storyDraft.toStream(nativeByteBuffer);
            int i = 1;
            sQLitePreparedStatement.bindLong(1, storyDraft.id);
            sQLitePreparedStatement.bindLong(2, storyDraft.date);
            sQLitePreparedStatement.bindByteBuffer(3, nativeByteBuffer);
            if (!storyDraft.isEdit) {
                i = storyDraft.isError ? 2 : 0;
            }
            sQLitePreparedStatement.bindInteger(4, i);
            sQLitePreparedStatement.step();
            nativeByteBuffer.reuse();
            sQLitePreparedStatement.dispose();
            sQLitePreparedStatement.dispose();
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    public void lambda$load$2(ArrayList arrayList) {
        File file;
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            StoryEntry entry = ((StoryDraft) arrayList.get(i)).toEntry();
            if (entry != null) {
                if (entry.isCollage() || ((file = entry.file) != null && file.exists())) {
                    if (entry.isEdit) {
                        this.drafts.add(entry);
                        arrayList2.add(Long.valueOf(entry.draftId));
                    } else {
                        this.drafts.add(entry);
                        arrayList2.add(Long.valueOf(entry.draftId));
                    }
                }
                arrayList3.add(entry);
            }
        }
        delete(arrayList3);
        this.loading = false;
        this.loaded = true;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesDraftsUpdated, new Object[0]);
    }

    public void lambda$loadFailed$3(ArrayList arrayList) {
        File file;
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            StoryEntry entry = ((StoryDraft) arrayList.get(i)).toEntry();
            if (entry != null) {
                if ((entry.isCollage() || ((file = entry.file) != null && file.exists())) && currentTimeMillis - entry.draftDate <= 604800000) {
                    arrayList4.add(entry);
                    arrayList2.add(Long.valueOf(entry.draftId));
                } else {
                    arrayList3.add(entry);
                }
            }
        }
        delete(arrayList3);
        this.loadingFailed = false;
        this.loadedFailed = true;
        MessagesController.getInstance(this.currentAccount).getStoriesController().putUploadingDrafts(arrayList4);
    }

    public static void lambda$loadInternal$1(org.telegram.messenger.MessagesStorage r8, boolean r9, final org.telegram.messenger.Utilities.Callback r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.DraftsController.lambda$loadInternal$1(org.telegram.messenger.MessagesStorage, boolean, org.telegram.messenger.Utilities$Callback):void");
    }

    private void loadFailed() {
        if (this.loadedFailed || this.loadingFailed) {
            return;
        }
        this.loadingFailed = true;
        loadInternal(true, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DraftsController.this.lambda$loadFailed$3((ArrayList) obj);
            }
        });
    }

    private void loadInternal(final boolean z, final Utilities.Callback callback) {
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                DraftsController.lambda$loadInternal$1(MessagesStorage.this, z, callback);
            }
        });
    }

    private void prepare(StoryEntry storyEntry) {
        if (storyEntry == null) {
            return;
        }
        if (storyEntry.draftId == 0) {
            storyEntry.draftId = Utilities.random.nextLong();
        }
        storyEntry.draftDate = System.currentTimeMillis();
        storyEntry.isDraft = true;
        if (storyEntry.fileDeletable) {
            storyEntry.file = prepareFile(storyEntry.file);
        } else if (storyEntry.file != null) {
            File makeCacheFile = StoryEntry.makeCacheFile(this.currentAccount, storyEntry.isVideo);
            try {
                AndroidUtilities.copyFile(storyEntry.file, makeCacheFile);
                storyEntry.file = prepareFile(makeCacheFile);
                storyEntry.fileDeletable = true;
            } catch (IOException e) {
                FileLog.e(e);
            }
        }
        storyEntry.filterFile = prepareFile(storyEntry.filterFile);
        storyEntry.paintFile = prepareFile(storyEntry.paintFile);
        storyEntry.draftThumbFile = prepareFile(storyEntry.draftThumbFile);
    }

    private File prepareFile(File file) {
        if (file == null) {
            return null;
        }
        if (this.draftsFolder == null) {
            File file2 = new File(FileLoader.getDirectory(4), "drafts");
            this.draftsFolder = file2;
            if (!file2.exists()) {
                this.draftsFolder.mkdir();
            }
        }
        if (!file.getAbsolutePath().startsWith(this.draftsFolder.getAbsolutePath())) {
            File file3 = new File(this.draftsFolder, file.getName());
            if (file.renameTo(file3)) {
                return file3;
            }
        }
        return file;
    }

    public void append(StoryEntry storyEntry) {
        if (storyEntry == null || storyEntry.isRepostMessage) {
            return;
        }
        prepare(storyEntry);
        storyEntry.draftId = Utilities.random.nextLong();
        StoryDraft storyDraft = new StoryDraft(storyEntry);
        this.drafts.remove(storyEntry);
        this.drafts.add(0, storyEntry);
        append(storyDraft);
    }

    public void cleanup() {
        delete(this.drafts);
        this.loaded = false;
    }

    public void delete(ArrayList arrayList) {
        String str;
        StringBuilder sb;
        long j;
        if (arrayList == null) {
            return;
        }
        final ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            StoryEntry storyEntry = (StoryEntry) arrayList.get(i);
            if (storyEntry != null) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("StoryDraft delete ");
                sb2.append(storyEntry.draftId);
                sb2.append(" (edit=");
                sb2.append(storyEntry.isEdit);
                if (storyEntry.isEdit) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(", storyId=");
                    sb3.append(storyEntry.editStoryId);
                    sb3.append(", ");
                    if (storyEntry.editDocumentId != 0) {
                        sb = new StringBuilder();
                        sb.append("documentId=");
                        j = storyEntry.editDocumentId;
                    } else {
                        sb = new StringBuilder();
                        sb.append("photoId=");
                        j = storyEntry.editPhotoId;
                    }
                    sb.append(j);
                    sb3.append(sb.toString());
                    sb3.append(", expireDate=");
                    sb3.append(storyEntry.editExpireDate);
                    str = sb3.toString();
                } else {
                    str = "";
                }
                sb2.append(str);
                sb2.append(", now=");
                sb2.append(System.currentTimeMillis());
                sb2.append(")");
                FileLog.d(sb2.toString());
                arrayList2.add(Long.valueOf(storyEntry.draftId));
                storyEntry.destroy(true);
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        this.drafts.removeAll(arrayList);
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                DraftsController.lambda$delete$6(MessagesStorage.this, arrayList2);
            }
        });
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesDraftsUpdated, new Object[0]);
    }

    public void delete(StoryEntry storyEntry) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(storyEntry);
        delete(arrayList);
    }

    public void edit(StoryEntry storyEntry) {
        if (storyEntry == null) {
            return;
        }
        prepare(storyEntry);
        this.drafts.remove(storyEntry);
        if (!storyEntry.isError) {
            this.drafts.add(0, storyEntry);
        }
        final StoryDraft storyDraft = new StoryDraft(storyEntry);
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        messagesStorage.getStorageQueue().postRunnable(new Runnable() {
            @Override
            public final void run() {
                DraftsController.lambda$edit$4(MessagesStorage.this, storyDraft);
            }
        });
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesDraftsUpdated, new Object[0]);
    }

    public StoryEntry getForEdit(long j, TL_stories.StoryItem storyItem) {
        TLRPC.MessageMedia messageMedia;
        TLRPC.Document document;
        if (storyItem == null) {
            return null;
        }
        Iterator it = this.drafts.iterator();
        while (it.hasNext()) {
            StoryEntry storyEntry = (StoryEntry) it.next();
            if (storyEntry.isEdit && storyItem.id == storyEntry.editStoryId && j == storyEntry.editStoryPeerId && ((document = (messageMedia = storyItem.media).document) == null || document.id == storyEntry.editDocumentId)) {
                TLRPC.Photo photo = messageMedia.photo;
                if (photo == null || photo.id == storyEntry.editPhotoId) {
                    storyEntry.isEditSaved = true;
                    return storyEntry;
                }
            }
        }
        return null;
    }

    public void load() {
        if (this.loaded || this.loading) {
            return;
        }
        this.loading = true;
        loadInternal(false, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                DraftsController.this.lambda$load$2((ArrayList) obj);
            }
        });
    }

    public void saveForEdit(StoryEntry storyEntry, long j, TL_stories.StoryItem storyItem) {
        if (storyEntry == null || storyEntry.isRepostMessage || storyItem == null || storyItem.media == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        Iterator it = this.drafts.iterator();
        while (it.hasNext()) {
            StoryEntry storyEntry2 = (StoryEntry) it.next();
            if (storyEntry2.isEdit && storyEntry2.editStoryId == storyItem.id) {
                arrayList.add(storyEntry2);
            }
        }
        delete(arrayList);
        prepare(storyEntry);
        storyEntry.draftId = Utilities.random.nextLong();
        StoryDraft storyDraft = new StoryDraft(storyEntry);
        storyEntry.isEdit = true;
        storyDraft.isEdit = true;
        storyEntry.editStoryPeerId = j;
        storyDraft.editStoryPeerId = j;
        int i = storyItem.id;
        storyEntry.editStoryId = i;
        storyDraft.editStoryId = i;
        long j2 = storyItem.expire_date * 1000;
        storyEntry.editExpireDate = j2;
        storyDraft.editExpireDate = j2;
        TLRPC.MessageMedia messageMedia = storyItem.media;
        TLRPC.Document document = messageMedia.document;
        if (document != null) {
            long j3 = document.id;
            storyEntry.editDocumentId = j3;
            storyDraft.editDocumentId = j3;
        } else {
            TLRPC.Photo photo = messageMedia.photo;
            if (photo != null) {
                long j4 = photo.id;
                storyEntry.editPhotoId = j4;
                storyDraft.editPhotoId = j4;
            }
        }
        this.drafts.remove(storyEntry);
        this.drafts.add(0, storyEntry);
        append(storyDraft);
    }
}

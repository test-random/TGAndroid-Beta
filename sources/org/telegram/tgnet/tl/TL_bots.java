package org.telegram.tgnet.tl;

import android.graphics.Path;
import java.util.ArrayList;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class TL_bots {

    public static abstract class BotInfo extends TLObject {
        public botAppSettings app_settings;
        public ArrayList<TLRPC.TL_botCommand> commands = new ArrayList<>();
        public String description;
        public TLRPC.Document description_document;
        public TLRPC.Photo description_photo;
        public int flags;
        public boolean has_preview_medias;
        public BotMenuButton menu_button;
        public String privacy_policy_url;
        public long user_id;
        public int version;

        public static BotInfo TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            BotInfo tL_botInfo_layer192;
            switch (i) {
                case -2109505932:
                    tL_botInfo_layer192 = new TL_botInfo_layer192();
                    break;
                case -1892676777:
                    tL_botInfo_layer192 = new TL_botInfo_layer185();
                    break;
                case -1729618630:
                    tL_botInfo_layer192 = new TL_botInfo_layer131();
                    break;
                case -1154598962:
                    tL_botInfo_layer192 = new TL_botInfoEmpty_layer48();
                    break;
                case -468280483:
                    tL_botInfo_layer192 = new TL_botInfo_layer140();
                    break;
                case 164583517:
                    tL_botInfo_layer192 = new TL_botInfo_layer48();
                    break;
                case 460632885:
                    tL_botInfo_layer192 = new TL_botInfo_layer139();
                    break;
                case 912290611:
                    tL_botInfo_layer192 = new TL_botInfo();
                    break;
                default:
                    tL_botInfo_layer192 = null;
                    break;
            }
            if (tL_botInfo_layer192 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in BotInfo", Integer.valueOf(i)));
            }
            if (tL_botInfo_layer192 != null) {
                tL_botInfo_layer192.readParams(abstractSerializedData, z);
            }
            return tL_botInfo_layer192;
        }
    }

    public static abstract class BotMenuButton extends TLObject {
        public static BotMenuButton TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            BotMenuButton tL_botMenuButtonDefault = i != -944407322 ? i != 1113113093 ? i != 1966318984 ? null : new TL_botMenuButtonDefault() : new TL_botMenuButtonCommands() : new TL_botMenuButton();
            if (tL_botMenuButtonDefault == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in BotMenuButton", Integer.valueOf(i)));
            }
            if (tL_botMenuButtonDefault != null) {
                tL_botMenuButtonDefault.readParams(abstractSerializedData, z);
            }
            return tL_botMenuButtonDefault;
        }
    }

    public static class TL_botInfo extends BotInfo {
        public static final int constructor = 912290611;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.has_preview_medias = (readInt32 & 64) != 0;
            if ((readInt32 & 1) != 0) {
                this.user_id = abstractSerializedData.readInt64(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo = TLRPC.Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.description_document = TLRPC.Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 4) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.TL_botCommand TLdeserialize = TLRPC.TL_botCommand.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.commands.add(TLdeserialize);
                }
            }
            if ((this.flags & 8) != 0) {
                this.menu_button = BotMenuButton.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 128) != 0) {
                this.privacy_policy_url = abstractSerializedData.readString(z);
            }
            if ((this.flags & 256) != 0) {
                this.app_settings = botAppSettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(912290611);
            int i = this.has_preview_medias ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt64(this.user_id);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32) != 0) {
                this.description_document.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.commands.size();
                abstractSerializedData.writeInt32(size);
                for (int i2 = 0; i2 < size; i2++) {
                    this.commands.get(i2).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 8) != 0) {
                this.menu_button.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 128) != 0) {
                abstractSerializedData.writeString(this.privacy_policy_url);
            }
            if ((this.flags & 256) != 0) {
                this.app_settings.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_botInfoEmpty_layer48 extends TL_botInfo {
        public static final int constructor = -1154598962;

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1154598962);
        }
    }

    public static class TL_botInfo_layer131 extends TL_botInfo {
        public static final int constructor = -1729618630;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.user_id = abstractSerializedData.readInt32(z);
            this.description = abstractSerializedData.readString(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TLRPC.TL_botCommand TLdeserialize = TLRPC.TL_botCommand.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.commands.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1729618630);
            abstractSerializedData.writeInt32((int) this.user_id);
            abstractSerializedData.writeString(this.description);
            abstractSerializedData.writeInt32(481674261);
            int size = this.commands.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.commands.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_botInfo_layer139 extends BotInfo {
        public static final int constructor = 460632885;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.user_id = abstractSerializedData.readInt64(z);
            this.description = abstractSerializedData.readString(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TLRPC.TL_botCommand TLdeserialize = TLRPC.TL_botCommand.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.commands.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(460632885);
            abstractSerializedData.writeInt64(this.user_id);
            abstractSerializedData.writeString(this.description);
            abstractSerializedData.writeInt32(481674261);
            int size = this.commands.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.commands.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_botInfo_layer140 extends TL_botInfo {
        public static final int constructor = -468280483;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.user_id = abstractSerializedData.readInt64(z);
            this.description = abstractSerializedData.readString(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TLRPC.TL_botCommand TLdeserialize = TLRPC.TL_botCommand.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.commands.add(TLdeserialize);
            }
            this.menu_button = BotMenuButton.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-468280483);
            abstractSerializedData.writeInt64(this.user_id);
            abstractSerializedData.writeString(this.description);
            abstractSerializedData.writeInt32(481674261);
            int size = this.commands.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.commands.get(i).serializeToStream(abstractSerializedData);
            }
            this.menu_button.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_botInfo_layer185 extends TL_botInfo {
        public static final int constructor = -1892676777;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.has_preview_medias = (readInt32 & 64) != 0;
            if ((readInt32 & 1) != 0) {
                this.user_id = abstractSerializedData.readInt64(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo = TLRPC.Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.description_document = TLRPC.Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 4) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.TL_botCommand TLdeserialize = TLRPC.TL_botCommand.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.commands.add(TLdeserialize);
                }
            }
            if ((this.flags & 8) != 0) {
                this.menu_button = BotMenuButton.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1892676777);
            int i = this.has_preview_medias ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt64(this.user_id);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32) != 0) {
                this.description_document.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.commands.size();
                abstractSerializedData.writeInt32(size);
                for (int i2 = 0; i2 < size; i2++) {
                    this.commands.get(i2).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 8) != 0) {
                this.menu_button.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_botInfo_layer192 extends TL_botInfo {
        public static final int constructor = -2109505932;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.has_preview_medias = (readInt32 & 64) != 0;
            if ((readInt32 & 1) != 0) {
                this.user_id = abstractSerializedData.readInt64(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = abstractSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo = TLRPC.Photo.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.description_document = TLRPC.Document.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 4) != 0) {
                int readInt322 = abstractSerializedData.readInt32(z);
                if (readInt322 != 481674261) {
                    if (z) {
                        throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                    }
                    return;
                }
                int readInt323 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt323; i++) {
                    TLRPC.TL_botCommand TLdeserialize = TLRPC.TL_botCommand.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                    if (TLdeserialize == null) {
                        return;
                    }
                    this.commands.add(TLdeserialize);
                }
            }
            if ((this.flags & 8) != 0) {
                this.menu_button = BotMenuButton.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
            if ((this.flags & 128) != 0) {
                this.privacy_policy_url = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-2109505932);
            int i = this.has_preview_medias ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt64(this.user_id);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 32) != 0) {
                this.description_document.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(481674261);
                int size = this.commands.size();
                abstractSerializedData.writeInt32(size);
                for (int i2 = 0; i2 < size; i2++) {
                    this.commands.get(i2).serializeToStream(abstractSerializedData);
                }
            }
            if ((this.flags & 8) != 0) {
                this.menu_button.serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 128) != 0) {
                abstractSerializedData.writeString(this.privacy_policy_url);
            }
        }
    }

    public static class TL_botInfo_layer48 extends TL_botInfo {
        public static final int constructor = 164583517;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.user_id = abstractSerializedData.readInt32(z);
            this.version = abstractSerializedData.readInt32(z);
            abstractSerializedData.readString(z);
            this.description = abstractSerializedData.readString(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TLRPC.TL_botCommand TLdeserialize = TLRPC.TL_botCommand.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.commands.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(164583517);
            abstractSerializedData.writeInt32((int) this.user_id);
            abstractSerializedData.writeInt32(this.version);
            abstractSerializedData.writeString("");
            abstractSerializedData.writeString(this.description);
            abstractSerializedData.writeInt32(481674261);
            int size = this.commands.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.commands.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_botMenuButton extends BotMenuButton {
        public static final int constructor = -944407322;
        public String text;
        public String url;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.text = abstractSerializedData.readString(z);
            this.url = abstractSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-944407322);
            abstractSerializedData.writeString(this.text);
            abstractSerializedData.writeString(this.url);
        }
    }

    public static class TL_botMenuButtonCommands extends BotMenuButton {
        public static final int constructor = 1113113093;

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1113113093);
        }
    }

    public static class TL_botMenuButtonDefault extends BotMenuButton {
        public static final int constructor = 1966318984;

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1966318984);
        }
    }

    public static class TL_updateBotMenuButton extends TLRPC.Update {
        public static final int constructor = 347625491;
        public long bot_id;
        public BotMenuButton button;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.bot_id = abstractSerializedData.readInt64(z);
            this.button = BotMenuButton.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(347625491);
            abstractSerializedData.writeInt64(this.bot_id);
            this.button.serializeToStream(abstractSerializedData);
        }
    }

    public static class addPreviewMedia extends TLObject {
        public static final int constructor = 397326170;
        public TLRPC.InputUser bot;
        public String lang_code = "";
        public TLRPC.InputMedia media;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return botPreviewMedia.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(397326170);
            this.bot.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.lang_code);
            this.media.serializeToStream(abstractSerializedData);
        }
    }

    public static class allowSendMessage extends TLObject {
        public static final int constructor = -248323089;
        public TLRPC.InputUser bot;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-248323089);
            this.bot.serializeToStream(abstractSerializedData);
        }
    }

    public static class botAppSettings extends TLObject {
        public static final int constructor = -912582320;
        public int background_color;
        public int background_dark_color;
        public int flags;
        public int header_color;
        public int header_dark_color;
        public byte[] placeholder_path;
        public Path placeholder_svg_path;

        public static botAppSettings TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-912582320 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in botAppSettings", Integer.valueOf(i)));
                }
                return null;
            }
            botAppSettings botappsettings = new botAppSettings();
            botappsettings.readParams(abstractSerializedData, z);
            return botappsettings;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                byte[] readByteArray = abstractSerializedData.readByteArray(z);
                this.placeholder_path = readByteArray;
                this.placeholder_svg_path = SvgHelper.doPath(SvgHelper.decompress(readByteArray));
            }
            if ((this.flags & 2) != 0) {
                this.background_color = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 4) != 0) {
                this.background_dark_color = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 8) != 0) {
                this.header_color = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 16) != 0) {
                this.header_dark_color = abstractSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-912582320);
            abstractSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeByteArray(this.placeholder_path);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(this.background_color);
            }
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(this.background_dark_color);
            }
            if ((this.flags & 8) != 0) {
                abstractSerializedData.writeInt32(this.header_color);
            }
            if ((this.flags & 16) != 0) {
                abstractSerializedData.writeInt32(this.header_dark_color);
            }
        }
    }

    public static class botPreviewMedia extends TLObject {
        public static final int constructor = 602479523;
        public int date;
        public TLRPC.MessageMedia media;

        public static botPreviewMedia TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (602479523 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in botPreviewMedia", Integer.valueOf(i)));
                }
                return null;
            }
            botPreviewMedia botpreviewmedia = new botPreviewMedia();
            botpreviewmedia.readParams(abstractSerializedData, z);
            return botpreviewmedia;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.date = abstractSerializedData.readInt32(z);
            this.media = TLRPC.MessageMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(602479523);
            abstractSerializedData.writeInt32(this.date);
            this.media.serializeToStream(abstractSerializedData);
        }
    }

    public static class canSendMessage extends TLObject {
        public static final int constructor = 324662502;
        public TLRPC.InputUser bot;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(324662502);
            this.bot.serializeToStream(abstractSerializedData);
        }
    }

    public static class checkDownloadFileParams extends TLObject {
        public static final int constructor = 1342666121;
        public TLRPC.InputUser bot;
        public String file_name;
        public String url;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1342666121);
            this.bot.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.file_name);
            abstractSerializedData.writeString(this.url);
        }
    }

    public static class deletePreviewMedia extends TLObject {
        public static final int constructor = 755054003;
        public TLRPC.InputUser bot;
        public String lang_code = "";
        public ArrayList<TLRPC.InputMedia> media = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(755054003);
            this.bot.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.lang_code);
            abstractSerializedData.writeInt32(481674261);
            int size = this.media.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.media.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class editPreviewMedia extends TLObject {
        public static final int constructor = -2061148049;
        public TLRPC.InputUser bot;
        public String lang_code = "";
        public TLRPC.InputMedia media;
        public TLRPC.InputMedia new_media;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return botPreviewMedia.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-2061148049);
            this.bot.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.lang_code);
            this.media.serializeToStream(abstractSerializedData);
            this.new_media.serializeToStream(abstractSerializedData);
        }
    }

    public static class getBotInfo extends TLObject {
        public static final int constructor = -589753091;
        public TLRPC.InputUser bot;
        public int flags;
        public String lang_code;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return BotInfo.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-589753091);
            abstractSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                this.bot.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeString(this.lang_code);
        }
    }

    public static class getBotMenuButton extends TLObject {
        public static final int constructor = -1671369944;
        public TLRPC.InputUser user_id;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return BotMenuButton.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1671369944);
            this.user_id.serializeToStream(abstractSerializedData);
        }
    }

    public static class getPopularAppBots extends TLObject {
        public static final int constructor = -1034878574;
        public int limit;
        public String offset;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return popularAppBots.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1034878574);
            abstractSerializedData.writeString(this.offset);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class getPreviewInfo extends TLObject {
        public static final int constructor = 1111143341;
        public TLRPC.InputUser bot;
        public String lang_code = "";

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return previewInfo.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1111143341);
            this.bot.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.lang_code);
        }
    }

    public static class getPreviewMedias extends TLObject {
        public static final int constructor = -1566222003;
        public TLRPC.InputUser bot;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            TLRPC.Vector vector = new TLRPC.Vector();
            int readInt32 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt32; i2++) {
                vector.objects.add(botPreviewMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
            }
            return vector;
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1566222003);
            this.bot.serializeToStream(abstractSerializedData);
        }
    }

    public static class invokeWebViewCustomMethod extends TLObject {
        public static final int constructor = 142591463;
        public TLRPC.InputUser bot;
        public String custom_method;
        public TLRPC.TL_dataJSON params;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.TL_dataJSON.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(142591463);
            this.bot.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.custom_method);
            this.params.serializeToStream(abstractSerializedData);
        }
    }

    public static class popularAppBots extends TLObject {
        public static final int constructor = 428978491;
        public int flags;
        public String next_offset;
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static popularAppBots TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (428978491 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_bots_popularAppBots", Integer.valueOf(i)));
                }
                return null;
            }
            popularAppBots popularappbots = new popularAppBots();
            popularappbots.readParams(abstractSerializedData, z);
            return popularappbots;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                this.next_offset = abstractSerializedData.readString(z);
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            if (readInt322 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                }
                return;
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt323; i++) {
                TLRPC.User TLdeserialize = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.users.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(428978491);
            abstractSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
            }
            abstractSerializedData.writeInt32(481674261);
            int size = this.users.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.users.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class previewInfo extends TLObject {
        public static final int constructor = 212278628;
        public ArrayList<botPreviewMedia> media = new ArrayList<>();
        public ArrayList<String> lang_codes = new ArrayList<>();

        public static previewInfo TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (212278628 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in previewInfo", Integer.valueOf(i)));
                }
                return null;
            }
            previewInfo previewinfo = new previewInfo();
            previewinfo.readParams(abstractSerializedData, z);
            return previewinfo;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                botPreviewMedia TLdeserialize = botPreviewMedia.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.media.add(TLdeserialize);
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
            } else {
                int readInt324 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt324; i2++) {
                    this.lang_codes.add(abstractSerializedData.readString(z));
                }
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(212278628);
            abstractSerializedData.writeInt32(481674261);
            int size = this.media.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.media.get(i).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.lang_codes.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                abstractSerializedData.writeString(this.lang_codes.get(i2));
            }
        }
    }

    public static class reorderPreviewMedias extends TLObject {
        public static final int constructor = -1238895702;
        public TLRPC.InputUser bot;
        public String lang_code = "";
        public ArrayList<TLRPC.InputMedia> order = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1238895702);
            this.bot.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.lang_code);
            abstractSerializedData.writeInt32(481674261);
            int size = this.order.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.order.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class reorderUsernames extends TLObject {
        public static final int constructor = -1760972350;
        public TLRPC.InputUser bot;
        public ArrayList<String> order = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1760972350);
            this.bot.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.order.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                abstractSerializedData.writeString(this.order.get(i));
            }
        }
    }

    public static class setBotInfo extends TLObject {
        public static final int constructor = 282013987;
        public String about;
        public TLRPC.InputUser bot;
        public String description;
        public int flags;
        public String lang_code;
        public String name;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(282013987);
            abstractSerializedData.writeInt32(this.flags);
            if ((this.flags & 4) != 0) {
                this.bot.serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeString(this.lang_code);
            if ((this.flags & 8) != 0) {
                abstractSerializedData.writeString(this.name);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.about);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeString(this.description);
            }
        }
    }

    public static class setBotMenuButton extends TLObject {
        public static final int constructor = 1157944655;
        public BotMenuButton button;
        public TLRPC.InputUser user_id;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1157944655);
            this.user_id.serializeToStream(abstractSerializedData);
            this.button.serializeToStream(abstractSerializedData);
        }
    }

    public static class toggleUserEmojiStatusPermission extends TLObject {
        public static final int constructor = 115237778;
        public TLRPC.InputUser bot;
        public boolean enabled;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(115237778);
            this.bot.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeBool(this.enabled);
        }
    }

    public static class toggleUsername extends TLObject {
        public static final int constructor = 87861619;
        public boolean active;
        public TLRPC.InputUser bot;
        public String username;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(87861619);
            this.bot.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.username);
            abstractSerializedData.writeBool(this.active);
        }
    }
}

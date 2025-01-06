package org.telegram.tgnet.tl;

import android.graphics.Path;
import java.util.ArrayList;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1;
import org.telegram.tgnet.TLRPC$TL_updateBotCommands$$ExternalSyntheticLambda0;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_payments;

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
        public botVerifierSettings verifier_settings;
        public int version;

        public static BotInfo TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
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
                    tL_botInfo_layer192 = new TL_botInfo_layer195();
                    break;
                case 1300890265:
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
                tL_botInfo_layer192.readParams(inputSerializedData, z);
            }
            return tL_botInfo_layer192;
        }
    }

    public static abstract class BotMenuButton extends TLObject {
        public static BotMenuButton TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            BotMenuButton tL_botMenuButtonDefault = i != -944407322 ? i != 1113113093 ? i != 1966318984 ? null : new TL_botMenuButtonDefault() : new TL_botMenuButtonCommands() : new TL_botMenuButton();
            if (tL_botMenuButtonDefault == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in BotMenuButton", Integer.valueOf(i)));
            }
            if (tL_botMenuButtonDefault != null) {
                tL_botMenuButtonDefault.readParams(inputSerializedData, z);
            }
            return tL_botMenuButtonDefault;
        }
    }

    public static class TL_botInfo extends BotInfo {
        public static final int constructor = 1300890265;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.has_preview_medias = (readInt32 & 64) != 0;
            if ((readInt32 & 1) != 0) {
                this.user_id = inputSerializedData.readInt64(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo = TLRPC.Photo.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.description_document = TLRPC.Document.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 4) != 0) {
                this.commands = Vector.deserialize(inputSerializedData, new TLRPC$TL_updateBotCommands$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 8) != 0) {
                this.menu_button = BotMenuButton.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 128) != 0) {
                this.privacy_policy_url = inputSerializedData.readString(z);
            }
            if ((this.flags & 256) != 0) {
                this.app_settings = botAppSettings.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 512) != 0) {
                this.verifier_settings = botVerifierSettings.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1300890265);
            int i = this.has_preview_medias ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt64(this.user_id);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                this.description_document.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 4) != 0) {
                Vector.serialize(outputSerializedData, this.commands);
            }
            if ((this.flags & 8) != 0) {
                this.menu_button.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 128) != 0) {
                outputSerializedData.writeString(this.privacy_policy_url);
            }
            if ((this.flags & 256) != 0) {
                this.app_settings.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 512) != 0) {
                this.verifier_settings.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_botInfoEmpty_layer48 extends TL_botInfo {
        public static final int constructor = -1154598962;

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1154598962);
        }
    }

    public static class TL_botInfo_layer131 extends TL_botInfo {
        public static final int constructor = -1729618630;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.user_id = inputSerializedData.readInt32(z);
            this.description = inputSerializedData.readString(z);
            this.commands = Vector.deserialize(inputSerializedData, new TLRPC$TL_updateBotCommands$$ExternalSyntheticLambda0(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1729618630);
            outputSerializedData.writeInt32((int) this.user_id);
            outputSerializedData.writeString(this.description);
            Vector.serialize(outputSerializedData, this.commands);
        }
    }

    public static class TL_botInfo_layer139 extends BotInfo {
        public static final int constructor = 460632885;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.user_id = inputSerializedData.readInt64(z);
            this.description = inputSerializedData.readString(z);
            this.commands = Vector.deserialize(inputSerializedData, new TLRPC$TL_updateBotCommands$$ExternalSyntheticLambda0(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(460632885);
            outputSerializedData.writeInt64(this.user_id);
            outputSerializedData.writeString(this.description);
            Vector.serialize(outputSerializedData, this.commands);
        }
    }

    public static class TL_botInfo_layer140 extends TL_botInfo {
        public static final int constructor = -468280483;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.user_id = inputSerializedData.readInt64(z);
            this.description = inputSerializedData.readString(z);
            this.commands = Vector.deserialize(inputSerializedData, new TLRPC$TL_updateBotCommands$$ExternalSyntheticLambda0(), z);
            this.menu_button = BotMenuButton.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-468280483);
            outputSerializedData.writeInt64(this.user_id);
            outputSerializedData.writeString(this.description);
            Vector.serialize(outputSerializedData, this.commands);
            this.menu_button.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_botInfo_layer185 extends TL_botInfo {
        public static final int constructor = -1892676777;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.has_preview_medias = (readInt32 & 64) != 0;
            if ((readInt32 & 1) != 0) {
                this.user_id = inputSerializedData.readInt64(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo = TLRPC.Photo.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.description_document = TLRPC.Document.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 4) != 0) {
                this.commands = Vector.deserialize(inputSerializedData, new TLRPC$TL_updateBotCommands$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 8) != 0) {
                this.menu_button = BotMenuButton.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1892676777);
            int i = this.has_preview_medias ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt64(this.user_id);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                this.description_document.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 4) != 0) {
                Vector.serialize(outputSerializedData, this.commands);
            }
            if ((this.flags & 8) != 0) {
                this.menu_button.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_botInfo_layer192 extends TL_botInfo {
        public static final int constructor = -2109505932;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.has_preview_medias = (readInt32 & 64) != 0;
            if ((readInt32 & 1) != 0) {
                this.user_id = inputSerializedData.readInt64(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo = TLRPC.Photo.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.description_document = TLRPC.Document.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 4) != 0) {
                this.commands = Vector.deserialize(inputSerializedData, new TLRPC$TL_updateBotCommands$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 8) != 0) {
                this.menu_button = BotMenuButton.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 128) != 0) {
                this.privacy_policy_url = inputSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-2109505932);
            int i = this.has_preview_medias ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt64(this.user_id);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                this.description_document.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 4) != 0) {
                Vector.serialize(outputSerializedData, this.commands);
            }
            if ((this.flags & 8) != 0) {
                this.menu_button.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 128) != 0) {
                outputSerializedData.writeString(this.privacy_policy_url);
            }
        }
    }

    public static class TL_botInfo_layer195 extends TL_botInfo {
        public static final int constructor = 912290611;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.has_preview_medias = (readInt32 & 64) != 0;
            if ((readInt32 & 1) != 0) {
                this.user_id = inputSerializedData.readInt64(z);
            }
            if ((this.flags & 2) != 0) {
                this.description = inputSerializedData.readString(z);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo = TLRPC.Photo.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 32) != 0) {
                this.description_document = TLRPC.Document.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 4) != 0) {
                this.commands = Vector.deserialize(inputSerializedData, new TLRPC$TL_updateBotCommands$$ExternalSyntheticLambda0(), z);
            }
            if ((this.flags & 8) != 0) {
                this.menu_button = BotMenuButton.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 128) != 0) {
                this.privacy_policy_url = inputSerializedData.readString(z);
            }
            if ((this.flags & 256) != 0) {
                this.app_settings = botAppSettings.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(912290611);
            int i = this.has_preview_medias ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt64(this.user_id);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
            if ((this.flags & 16) != 0) {
                this.description_photo.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 32) != 0) {
                this.description_document.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 4) != 0) {
                Vector.serialize(outputSerializedData, this.commands);
            }
            if ((this.flags & 8) != 0) {
                this.menu_button.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 128) != 0) {
                outputSerializedData.writeString(this.privacy_policy_url);
            }
            if ((this.flags & 256) != 0) {
                this.app_settings.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_botInfo_layer48 extends TL_botInfo {
        public static final int constructor = 164583517;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.user_id = inputSerializedData.readInt32(z);
            this.version = inputSerializedData.readInt32(z);
            inputSerializedData.readString(z);
            this.description = inputSerializedData.readString(z);
            this.commands = Vector.deserialize(inputSerializedData, new TLRPC$TL_updateBotCommands$$ExternalSyntheticLambda0(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(164583517);
            outputSerializedData.writeInt32((int) this.user_id);
            outputSerializedData.writeInt32(this.version);
            outputSerializedData.writeString("");
            outputSerializedData.writeString(this.description);
            Vector.serialize(outputSerializedData, this.commands);
        }
    }

    public static class TL_botMenuButton extends BotMenuButton {
        public static final int constructor = -944407322;
        public String text;
        public String url;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.text = inputSerializedData.readString(z);
            this.url = inputSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-944407322);
            outputSerializedData.writeString(this.text);
            outputSerializedData.writeString(this.url);
        }
    }

    public static class TL_botMenuButtonCommands extends BotMenuButton {
        public static final int constructor = 1113113093;

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1113113093);
        }
    }

    public static class TL_botMenuButtonDefault extends BotMenuButton {
        public static final int constructor = 1966318984;

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1966318984);
        }
    }

    public static class TL_updateBotMenuButton extends TLRPC.Update {
        public static final int constructor = 347625491;
        public long bot_id;
        public BotMenuButton button;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.bot_id = inputSerializedData.readInt64(z);
            this.button = BotMenuButton.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(347625491);
            outputSerializedData.writeInt64(this.bot_id);
            this.button.serializeToStream(outputSerializedData);
        }
    }

    public static class addPreviewMedia extends TLObject {
        public static final int constructor = 397326170;
        public TLRPC.InputUser bot;
        public String lang_code = "";
        public TLRPC.InputMedia media;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return botPreviewMedia.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(397326170);
            this.bot.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.lang_code);
            this.media.serializeToStream(outputSerializedData);
        }
    }

    public static class allowSendMessage extends TLObject {
        public static final int constructor = -248323089;
        public TLRPC.InputUser bot;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-248323089);
            this.bot.serializeToStream(outputSerializedData);
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

        public static botAppSettings TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-912582320 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in botAppSettings", Integer.valueOf(i)));
                }
                return null;
            }
            botAppSettings botappsettings = new botAppSettings();
            botappsettings.readParams(inputSerializedData, z);
            return botappsettings;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                byte[] readByteArray = inputSerializedData.readByteArray(z);
                this.placeholder_path = readByteArray;
                this.placeholder_svg_path = SvgHelper.doPath(SvgHelper.decompress(readByteArray));
            }
            if ((this.flags & 2) != 0) {
                this.background_color = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 4) != 0) {
                this.background_dark_color = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 8) != 0) {
                this.header_color = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 16) != 0) {
                this.header_dark_color = inputSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-912582320);
            outputSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeByteArray(this.placeholder_path);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeInt32(this.background_color);
            }
            if ((this.flags & 4) != 0) {
                outputSerializedData.writeInt32(this.background_dark_color);
            }
            if ((this.flags & 8) != 0) {
                outputSerializedData.writeInt32(this.header_color);
            }
            if ((this.flags & 16) != 0) {
                outputSerializedData.writeInt32(this.header_dark_color);
            }
        }
    }

    public static class botPreviewMedia extends TLObject {
        public static final int constructor = 602479523;
        public int date;
        public TLRPC.MessageMedia media;

        public static botPreviewMedia TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (602479523 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in botPreviewMedia", Integer.valueOf(i)));
                }
                return null;
            }
            botPreviewMedia botpreviewmedia = new botPreviewMedia();
            botpreviewmedia.readParams(inputSerializedData, z);
            return botpreviewmedia;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.date = inputSerializedData.readInt32(z);
            this.media = TLRPC.MessageMedia.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(602479523);
            outputSerializedData.writeInt32(this.date);
            this.media.serializeToStream(outputSerializedData);
        }
    }

    public static class botVerification extends TLObject {
        public static final int constructor = -113453988;
        public long bot_id;
        public String description;
        public long icon;

        public static botVerification TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-113453988 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_bots.botVerification", Integer.valueOf(i)));
                }
                return null;
            }
            botVerification botverification = new botVerification();
            botverification.readParams(inputSerializedData, z);
            return botverification;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.bot_id = inputSerializedData.readInt64(z);
            this.icon = inputSerializedData.readInt64(z);
            this.description = inputSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-113453988);
            outputSerializedData.writeInt64(this.bot_id);
            outputSerializedData.writeInt64(this.icon);
            outputSerializedData.writeString(this.description);
        }
    }

    public static class botVerifierSettings extends TLObject {
        public static final int constructor = -1328716265;
        public boolean can_modify_custom_description;
        public String company;
        public String custom_description;
        public int flags;
        public long icon;

        public static botVerifierSettings TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-1328716265 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_bots.botVerifierSettings", Integer.valueOf(i)));
                }
                return null;
            }
            botVerifierSettings botverifiersettings = new botVerifierSettings();
            botverifiersettings.readParams(inputSerializedData, z);
            return botverifiersettings;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.can_modify_custom_description = (readInt32 & 2) != 0;
            this.icon = inputSerializedData.readInt64(z);
            this.company = inputSerializedData.readString(z);
            if ((this.flags & 1) != 0) {
                this.custom_description = inputSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1328716265);
            int i = this.can_modify_custom_description ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeString(this.company);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.custom_description);
            }
        }
    }

    public static class canSendMessage extends TLObject {
        public static final int constructor = 324662502;
        public TLRPC.InputUser bot;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(324662502);
            this.bot.serializeToStream(outputSerializedData);
        }
    }

    public static class checkDownloadFileParams extends TLObject {
        public static final int constructor = 1342666121;
        public TLRPC.InputUser bot;
        public String file_name;
        public String url;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1342666121);
            this.bot.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.file_name);
            outputSerializedData.writeString(this.url);
        }
    }

    public static class deletePreviewMedia extends TLObject {
        public static final int constructor = 755054003;
        public TLRPC.InputUser bot;
        public String lang_code = "";
        public ArrayList<TLRPC.InputMedia> media = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(755054003);
            this.bot.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.lang_code);
            Vector.serialize(outputSerializedData, this.media);
        }
    }

    public static class editPreviewMedia extends TLObject {
        public static final int constructor = -2061148049;
        public TLRPC.InputUser bot;
        public String lang_code = "";
        public TLRPC.InputMedia media;
        public TLRPC.InputMedia new_media;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return botPreviewMedia.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-2061148049);
            this.bot.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.lang_code);
            this.media.serializeToStream(outputSerializedData);
            this.new_media.serializeToStream(outputSerializedData);
        }
    }

    public static class getAdminedBots extends TLObject {
        public static final int constructor = -1334764157;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return Vector.TLDeserialize(inputSerializedData, i, z, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1());
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1334764157);
        }
    }

    public static class getBotInfo extends TLObject {
        public static final int constructor = -589753091;
        public TLRPC.InputUser bot;
        public int flags;
        public String lang_code;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return BotInfo.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-589753091);
            outputSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                this.bot.serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeString(this.lang_code);
        }
    }

    public static class getBotMenuButton extends TLObject {
        public static final int constructor = -1671369944;
        public TLRPC.InputUser user_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return BotMenuButton.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1671369944);
            this.user_id.serializeToStream(outputSerializedData);
        }
    }

    public static class getBotRecommendations extends TLObject {
        public static final int constructor = -1581840363;
        public TLRPC.InputUser bot;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Users.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1581840363);
            this.bot.serializeToStream(outputSerializedData);
        }
    }

    public static class getPopularAppBots extends TLObject {
        public static final int constructor = -1034878574;
        public int limit;
        public String offset;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return popularAppBots.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1034878574);
            outputSerializedData.writeString(this.offset);
            outputSerializedData.writeInt32(this.limit);
        }
    }

    public static class getPreviewInfo extends TLObject {
        public static final int constructor = 1111143341;
        public TLRPC.InputUser bot;
        public String lang_code = "";

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return previewInfo.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1111143341);
            this.bot.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.lang_code);
        }
    }

    public static class getPreviewMedias extends TLObject {
        public static final int constructor = -1566222003;
        public TLRPC.InputUser bot;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return Vector.TLDeserialize(inputSerializedData, i, z, new TL_bots$getPreviewMedias$$ExternalSyntheticLambda0());
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1566222003);
            this.bot.serializeToStream(outputSerializedData);
        }
    }

    public static class invokeWebViewCustomMethod extends TLObject {
        public static final int constructor = 142591463;
        public TLRPC.InputUser bot;
        public String custom_method;
        public TLRPC.TL_dataJSON params;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.TL_dataJSON.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(142591463);
            this.bot.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.custom_method);
            this.params.serializeToStream(outputSerializedData);
        }
    }

    public static class popularAppBots extends TLObject {
        public static final int constructor = 428978491;
        public int flags;
        public String next_offset;
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static popularAppBots TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (428978491 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_bots_popularAppBots", Integer.valueOf(i)));
                }
                return null;
            }
            popularAppBots popularappbots = new popularAppBots();
            popularappbots.readParams(inputSerializedData, z);
            return popularappbots;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            if ((readInt32 & 1) != 0) {
                this.next_offset = inputSerializedData.readString(z);
            }
            this.users = Vector.deserialize(inputSerializedData, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(428978491);
            outputSerializedData.writeInt32(this.flags);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.next_offset);
            }
            Vector.serialize(outputSerializedData, this.users);
        }
    }

    public static class previewInfo extends TLObject {
        public static final int constructor = 212278628;
        public ArrayList<botPreviewMedia> media = new ArrayList<>();
        public ArrayList<String> lang_codes = new ArrayList<>();

        public static previewInfo TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (212278628 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in previewInfo", Integer.valueOf(i)));
                }
                return null;
            }
            previewInfo previewinfo = new previewInfo();
            previewinfo.readParams(inputSerializedData, z);
            return previewinfo;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.media = Vector.deserialize(inputSerializedData, new TL_bots$getPreviewMedias$$ExternalSyntheticLambda0(), z);
            this.lang_codes = Vector.deserializeString(inputSerializedData, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(212278628);
            Vector.serialize(outputSerializedData, this.media);
            Vector.serializeString(outputSerializedData, this.lang_codes);
        }
    }

    public static class reorderPreviewMedias extends TLObject {
        public static final int constructor = -1238895702;
        public TLRPC.InputUser bot;
        public String lang_code = "";
        public ArrayList<TLRPC.InputMedia> order = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1238895702);
            this.bot.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.lang_code);
            Vector.serialize(outputSerializedData, this.order);
        }
    }

    public static class reorderUsernames extends TLObject {
        public static final int constructor = -1760972350;
        public TLRPC.InputUser bot;
        public ArrayList<String> order = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1760972350);
            this.bot.serializeToStream(outputSerializedData);
            Vector.serializeString(outputSerializedData, this.order);
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
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(282013987);
            outputSerializedData.writeInt32(this.flags);
            if ((this.flags & 4) != 0) {
                this.bot.serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeString(this.lang_code);
            if ((this.flags & 8) != 0) {
                outputSerializedData.writeString(this.name);
            }
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.about);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.description);
            }
        }
    }

    public static class setBotMenuButton extends TLObject {
        public static final int constructor = 1157944655;
        public BotMenuButton button;
        public TLRPC.InputUser user_id;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1157944655);
            this.user_id.serializeToStream(outputSerializedData);
            this.button.serializeToStream(outputSerializedData);
        }
    }

    public static class setCustomVerification extends TLObject {
        public static final int constructor = -1953898563;
        public TLRPC.InputUser bot;
        public String custom_description;
        public boolean enabled;
        public int flags;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1953898563);
            int i = this.enabled ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            if ((this.flags & 1) != 0) {
                this.bot.serializeToStream(outputSerializedData);
            }
            this.peer.serializeToStream(outputSerializedData);
            if ((this.flags & 4) != 0) {
                outputSerializedData.writeString(this.custom_description);
            }
        }
    }

    public static class toggleUserEmojiStatusPermission extends TLObject {
        public static final int constructor = 115237778;
        public TLRPC.InputUser bot;
        public boolean enabled;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(115237778);
            this.bot.serializeToStream(outputSerializedData);
            outputSerializedData.writeBool(this.enabled);
        }
    }

    public static class toggleUsername extends TLObject {
        public static final int constructor = 87861619;
        public boolean active;
        public TLRPC.InputUser bot;
        public String username;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(87861619);
            this.bot.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.username);
            outputSerializedData.writeBool(this.active);
        }
    }

    public static class updateStarRefProgram extends TLObject {
        public static final int constructor = 2005621427;
        public TLRPC.InputUser bot;
        public int commission_permille;
        public int duration_months;
        public int flags;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_payments.starRefProgram.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(2005621427);
            outputSerializedData.writeInt32(this.flags);
            this.bot.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.commission_permille);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt32(this.duration_months);
            }
        }
    }
}

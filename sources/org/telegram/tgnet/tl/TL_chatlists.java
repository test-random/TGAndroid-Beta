package org.telegram.tgnet.tl;

import java.util.ArrayList;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1;
import org.telegram.tgnet.TLRPC$TL_channels_adminLogResults$$ExternalSyntheticLambda1;
import org.telegram.tgnet.TLRPC$TL_contacts_found$$ExternalSyntheticLambda0;
import org.telegram.tgnet.Vector;

public class TL_chatlists {

    public static class TL_chatlists_chatlistInvite extends chatlist_ChatlistInvite {
        public static final int constructor = -250687953;
        public String emoticon;
        public int flags;
        public boolean title_noanimate;
        public TLRPC.TL_textWithEntities title = new TLRPC.TL_textWithEntities();
        public ArrayList<TLRPC.Peer> peers = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.title_noanimate = (readInt32 & 2) != 0;
            this.title = TLRPC.TL_textWithEntities.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) > 0) {
                this.emoticon = inputSerializedData.readString(z);
            }
            this.peers = Vector.deserialize(inputSerializedData, new TLRPC$TL_contacts_found$$ExternalSyntheticLambda0(), z);
            this.chats = Vector.deserialize(inputSerializedData, new TLRPC$TL_channels_adminLogResults$$ExternalSyntheticLambda1(), z);
            this.users = Vector.deserialize(inputSerializedData, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-250687953);
            int i = this.title_noanimate ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.title.serializeToStream(outputSerializedData);
            if ((this.flags & 1) > 0) {
                outputSerializedData.writeString(this.emoticon);
            }
            Vector.serialize(outputSerializedData, this.peers);
            Vector.serialize(outputSerializedData, this.chats);
            Vector.serialize(outputSerializedData, this.users);
        }
    }

    public static class TL_chatlists_chatlistInviteAlready extends chatlist_ChatlistInvite {
        public static final int constructor = -91752871;
        public int filter_id;
        public ArrayList<TLRPC.Peer> missing_peers = new ArrayList<>();
        public ArrayList<TLRPC.Peer> already_peers = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.filter_id = inputSerializedData.readInt32(z);
            int readInt32 = inputSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = inputSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TLRPC.Peer TLdeserialize = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.missing_peers.add(TLdeserialize);
            }
            int readInt323 = inputSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = inputSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.Peer TLdeserialize2 = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.already_peers.add(TLdeserialize2);
            }
            int readInt325 = inputSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = inputSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TLRPC.Chat TLdeserialize3 = TLRPC.Chat.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.chats.add(TLdeserialize3);
            }
            int readInt327 = inputSerializedData.readInt32(z);
            if (readInt327 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt327)));
                }
                return;
            }
            int readInt328 = inputSerializedData.readInt32(z);
            for (int i4 = 0; i4 < readInt328; i4++) {
                TLRPC.User TLdeserialize4 = TLRPC.User.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize4 == null) {
                    return;
                }
                this.users.add(TLdeserialize4);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-91752871);
            outputSerializedData.writeInt32(this.filter_id);
            outputSerializedData.writeInt32(481674261);
            int size = this.missing_peers.size();
            outputSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.missing_peers.get(i).serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeInt32(481674261);
            int size2 = this.chats.size();
            outputSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.chats.get(i2).serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeInt32(481674261);
            int size3 = this.users.size();
            outputSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.users.get(i3).serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_chatlists_chatlistInvite_layer195 extends TL_chatlists_chatlistInvite {
        public static final int constructor = 500007837;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.flags = inputSerializedData.readInt32(z);
            TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
            this.title = tL_textWithEntities;
            tL_textWithEntities.text = inputSerializedData.readString(z);
            if ((this.flags & 1) > 0) {
                this.emoticon = inputSerializedData.readString(z);
            }
            int readInt32 = inputSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = inputSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TLRPC.Peer TLdeserialize = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.peers.add(TLdeserialize);
            }
            int readInt323 = inputSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = inputSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.Chat TLdeserialize2 = TLRPC.Chat.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.chats.add(TLdeserialize2);
            }
            int readInt325 = inputSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = inputSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TLRPC.User TLdeserialize3 = TLRPC.User.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.users.add(TLdeserialize3);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(500007837);
            outputSerializedData.writeInt32(this.flags);
            this.title.serializeToStream(outputSerializedData);
            if ((this.flags & 1) > 0) {
                outputSerializedData.writeString(this.emoticon);
            }
            outputSerializedData.writeInt32(481674261);
            int size = this.peers.size();
            outputSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.peers.get(i).serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeInt32(481674261);
            int size2 = this.chats.size();
            outputSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.chats.get(i2).serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeInt32(481674261);
            int size3 = this.users.size();
            outputSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.users.get(i3).serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_chatlists_chatlistUpdates extends TLObject {
        public static final int constructor = -1816295539;
        public ArrayList<TLRPC.Peer> missing_peers = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_chatlists_chatlistUpdates TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-1816295539 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_chatlists_chatlistUpdates", Integer.valueOf(i)));
                }
                return null;
            }
            TL_chatlists_chatlistUpdates tL_chatlists_chatlistUpdates = new TL_chatlists_chatlistUpdates();
            tL_chatlists_chatlistUpdates.readParams(inputSerializedData, z);
            return tL_chatlists_chatlistUpdates;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = inputSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TLRPC.Peer TLdeserialize = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.missing_peers.add(TLdeserialize);
            }
            int readInt323 = inputSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = inputSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.Chat TLdeserialize2 = TLRPC.Chat.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.chats.add(TLdeserialize2);
            }
            int readInt325 = inputSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = inputSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TLRPC.User TLdeserialize3 = TLRPC.User.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.users.add(TLdeserialize3);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1816295539);
            outputSerializedData.writeInt32(481674261);
            int size = this.missing_peers.size();
            outputSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.missing_peers.get(i).serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeInt32(481674261);
            int size2 = this.chats.size();
            outputSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.chats.get(i2).serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeInt32(481674261);
            int size3 = this.users.size();
            outputSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.users.get(i3).serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_chatlists_checkChatlistInvite extends TLObject {
        public static final int constructor = 1103171583;
        public String slug;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return chatlist_ChatlistInvite.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1103171583);
            outputSerializedData.writeString(this.slug);
        }
    }

    public static class TL_chatlists_deleteExportedInvite extends TLObject {
        public static final int constructor = 1906072670;
        public TL_inputChatlistDialogFilter chatlist;
        public String slug;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1906072670);
            this.chatlist.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.slug);
        }
    }

    public static class TL_chatlists_editExportedInvite extends TLObject {
        public static final int constructor = 1698543165;
        public TL_inputChatlistDialogFilter chatlist;
        public int flags;
        public ArrayList<TLRPC.InputPeer> peers = new ArrayList<>();
        public boolean revoked;
        public String slug;
        public String title;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_exportedChatlistInvite.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1698543165);
            int i = this.revoked ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.chatlist.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.slug);
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 4) != 0) {
                outputSerializedData.writeInt32(481674261);
                int size = this.peers.size();
                outputSerializedData.writeInt32(size);
                for (int i2 = 0; i2 < size; i2++) {
                    this.peers.get(i2).serializeToStream(outputSerializedData);
                }
            }
        }
    }

    public static class TL_chatlists_exportChatlistInvite extends TLObject {
        public static final int constructor = -2072885362;
        public TL_inputChatlistDialogFilter chatlist;
        public ArrayList<TLRPC.InputPeer> peers = new ArrayList<>();
        public String title;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_chatlists_exportedChatlistInvite.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-2072885362);
            this.chatlist.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.title);
            outputSerializedData.writeInt32(481674261);
            int size = this.peers.size();
            outputSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.peers.get(i).serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_chatlists_exportedChatlistInvite extends TLObject {
        public static final int constructor = 283567014;
        public TLRPC.DialogFilter filter;
        public TL_exportedChatlistInvite invite;

        public static TL_chatlists_exportedChatlistInvite TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (283567014 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_chatlists_exportedChatlistInvite", Integer.valueOf(i)));
                }
                return null;
            }
            TL_chatlists_exportedChatlistInvite tL_chatlists_exportedChatlistInvite = new TL_chatlists_exportedChatlistInvite();
            tL_chatlists_exportedChatlistInvite.readParams(inputSerializedData, z);
            return tL_chatlists_exportedChatlistInvite;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.filter = TLRPC.DialogFilter.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.invite = TL_exportedChatlistInvite.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(283567014);
            this.filter.serializeToStream(outputSerializedData);
            this.invite.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_chatlists_exportedInvites extends TLObject {
        public static final int constructor = 279670215;
        public ArrayList<TL_exportedChatlistInvite> invites = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_chatlists_exportedInvites TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (279670215 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_chatlists_exportedInvites", Integer.valueOf(i)));
                }
                return null;
            }
            TL_chatlists_exportedInvites tL_chatlists_exportedInvites = new TL_chatlists_exportedInvites();
            tL_chatlists_exportedInvites.readParams(inputSerializedData, z);
            return tL_chatlists_exportedInvites;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = inputSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TL_exportedChatlistInvite TLdeserialize = TL_exportedChatlistInvite.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.invites.add(TLdeserialize);
            }
            int readInt323 = inputSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = inputSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                TLRPC.Chat TLdeserialize2 = TLRPC.Chat.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.chats.add(TLdeserialize2);
            }
            int readInt325 = inputSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = inputSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TLRPC.User TLdeserialize3 = TLRPC.User.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.users.add(TLdeserialize3);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(279670215);
            outputSerializedData.writeInt32(481674261);
            int size = this.invites.size();
            outputSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.invites.get(i).serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeInt32(481674261);
            int size2 = this.chats.size();
            outputSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.chats.get(i2).serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeInt32(481674261);
            int size3 = this.users.size();
            outputSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.users.get(i3).serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_chatlists_getChatlistUpdates extends TLObject {
        public static final int constructor = -1992190687;
        public TL_inputChatlistDialogFilter chatlist;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_chatlists_chatlistUpdates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1992190687);
            this.chatlist.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_chatlists_getExportedInvites extends TLObject {
        public static final int constructor = -838608253;
        public TL_inputChatlistDialogFilter chatlist;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_chatlists_exportedInvites.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-838608253);
            this.chatlist.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_chatlists_getLeaveChatlistSuggestions extends TLObject {
        public static final int constructor = -37955820;
        public TL_inputChatlistDialogFilter chatlist;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return Vector.TLDeserialize(inputSerializedData, i, z, new TLRPC$TL_contacts_found$$ExternalSyntheticLambda0());
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-37955820);
            this.chatlist.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_chatlists_hideChatlistUpdates extends TLObject {
        public static final int constructor = 1726252795;
        public TL_inputChatlistDialogFilter chatlist;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1726252795);
            this.chatlist.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_chatlists_joinChatlistInvite extends TLObject {
        public static final int constructor = -1498291302;
        public ArrayList<TLRPC.InputPeer> peers = new ArrayList<>();
        public String slug;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1498291302);
            outputSerializedData.writeString(this.slug);
            outputSerializedData.writeInt32(481674261);
            int size = this.peers.size();
            outputSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.peers.get(i).serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_chatlists_joinChatlistUpdates extends TLObject {
        public static final int constructor = -527828747;
        public TL_inputChatlistDialogFilter chatlist;
        public ArrayList<TLRPC.InputPeer> peers = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-527828747);
            this.chatlist.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(481674261);
            int size = this.peers.size();
            outputSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.peers.get(i).serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_chatlists_leaveChatlist extends TLObject {
        public static final int constructor = 1962598714;
        public TL_inputChatlistDialogFilter chatlist;
        public ArrayList<TLRPC.InputPeer> peers = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1962598714);
            this.chatlist.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(481674261);
            int size = this.peers.size();
            outputSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.peers.get(i).serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_exportedChatlistInvite extends TLObject {
        public static final int constructor = 206668204;
        public int flags;
        public ArrayList<TLRPC.Peer> peers = new ArrayList<>();
        public boolean revoked;
        public String title;
        public String url;

        public static TL_exportedChatlistInvite TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (206668204 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_exportedChatlistInvite", Integer.valueOf(i)));
                }
                return null;
            }
            TL_exportedChatlistInvite tL_exportedChatlistInvite = new TL_exportedChatlistInvite();
            tL_exportedChatlistInvite.readParams(inputSerializedData, z);
            return tL_exportedChatlistInvite;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.revoked = (readInt32 & 1) != 0;
            this.title = inputSerializedData.readString(z);
            this.url = inputSerializedData.readString(z);
            int readInt322 = inputSerializedData.readInt32(z);
            if (readInt322 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                }
                return;
            }
            int readInt323 = inputSerializedData.readInt32(z);
            for (int i = 0; i < readInt323; i++) {
                TLRPC.Peer TLdeserialize = TLRPC.Peer.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.peers.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(206668204);
            int i = this.revoked ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeString(this.title);
            outputSerializedData.writeString(this.url);
            outputSerializedData.writeInt32(481674261);
            int size = this.peers.size();
            outputSerializedData.writeInt32(size);
            for (int i2 = 0; i2 < size; i2++) {
                this.peers.get(i2).serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_inputChatlistDialogFilter extends TLObject {
        public static final int constructor = -203367885;
        public int filter_id;

        public static TL_inputChatlistDialogFilter TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-203367885 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_inputChatlistDialogFilter", Integer.valueOf(i)));
                }
                return null;
            }
            TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_inputChatlistDialogFilter();
            tL_inputChatlistDialogFilter.readParams(inputSerializedData, z);
            return tL_inputChatlistDialogFilter;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.filter_id = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-203367885);
            outputSerializedData.writeInt32(this.filter_id);
        }
    }

    public static abstract class chatlist_ChatlistInvite extends TLObject {
        public static chatlist_ChatlistInvite TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            chatlist_ChatlistInvite tL_chatlists_chatlistInvite_layer195 = i != -250687953 ? i != -91752871 ? i != 500007837 ? null : new TL_chatlists_chatlistInvite_layer195() : new TL_chatlists_chatlistInviteAlready() : new TL_chatlists_chatlistInvite();
            if (tL_chatlists_chatlistInvite_layer195 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in chatlist_ChatlistInvite", Integer.valueOf(i)));
            }
            if (tL_chatlists_chatlistInvite_layer195 != null) {
                tL_chatlists_chatlistInvite_layer195.readParams(inputSerializedData, z);
            }
            return tL_chatlists_chatlistInvite_layer195;
        }
    }
}

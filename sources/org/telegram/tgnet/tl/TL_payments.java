package org.telegram.tgnet.tl;

import java.util.ArrayList;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;

public class TL_payments {

    public static class connectStarRefBot extends TLObject {
        public static final int constructor = 2127901834;
        public TLRPC.InputUser bot;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return connectedStarRefBots.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(2127901834);
            this.peer.serializeToStream(abstractSerializedData);
            this.bot.serializeToStream(abstractSerializedData);
        }
    }

    public static class connectedBotStarRef extends TLObject {
        public static final int constructor = 429997937;
        public long bot_id;
        public int commission_permille;
        public int date;
        public int duration_months;
        public int flags;
        public long participants;
        public long revenue;
        public boolean revoked;
        public String url;

        public static connectedBotStarRef TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (429997937 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_payments.connectedBotStarRef", Integer.valueOf(i)));
                }
                return null;
            }
            connectedBotStarRef connectedbotstarref = new connectedBotStarRef();
            connectedbotstarref.readParams(abstractSerializedData, z);
            return connectedbotstarref;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.revoked = (readInt32 & 2) != 0;
            this.url = abstractSerializedData.readString(z);
            this.date = abstractSerializedData.readInt32(z);
            this.bot_id = abstractSerializedData.readInt64(z);
            this.commission_permille = abstractSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                this.duration_months = abstractSerializedData.readInt32(z);
            }
            this.participants = abstractSerializedData.readInt64(z);
            this.revenue = abstractSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(429997937);
            int i = this.revoked ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            abstractSerializedData.writeString(this.url);
            abstractSerializedData.writeInt32(this.date);
            abstractSerializedData.writeInt64(this.bot_id);
            abstractSerializedData.writeInt32(this.commission_permille);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(this.duration_months);
            }
            abstractSerializedData.writeInt64(this.participants);
            abstractSerializedData.writeInt64(this.revenue);
        }
    }

    public static class connectedStarRefBots extends TLObject {
        public static final int constructor = -1730811363;
        public int count;
        public ArrayList<connectedBotStarRef> connected_bots = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static connectedStarRefBots TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-1730811363 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_payments.connectedStarRefBots", Integer.valueOf(i)));
                }
                return null;
            }
            connectedStarRefBots connectedstarrefbots = new connectedStarRefBots();
            connectedstarrefbots.readParams(abstractSerializedData, z);
            return connectedstarrefbots;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.count = abstractSerializedData.readInt32(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                this.connected_bots.add(connectedBotStarRef.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
            } else {
                int readInt324 = abstractSerializedData.readInt32(z);
                for (int i2 = 0; i2 < readInt324; i2++) {
                    this.users.add(TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
                }
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1730811363);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(481674261);
            int size = this.connected_bots.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.connected_bots.get(i).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.users.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.users.get(i2).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class editConnectedStarRefBot extends TLObject {
        public static final int constructor = -453204829;
        public int flags;
        public String link;
        public TLRPC.InputPeer peer;
        public boolean revoked;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return connectedStarRefBots.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-453204829);
            int i = this.revoked ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.link);
        }
    }

    public static class getConnectedStarRefBot extends TLObject {
        public static final int constructor = -1210476304;
        public TLRPC.InputUser bot;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return connectedStarRefBots.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1210476304);
            this.peer.serializeToStream(abstractSerializedData);
            this.bot.serializeToStream(abstractSerializedData);
        }
    }

    public static class getConnectedStarRefBots extends TLObject {
        public static final int constructor = 1483318611;
        public int flags;
        public int limit;
        public int offset_date;
        public String offset_link;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return connectedStarRefBots.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1483318611);
            abstractSerializedData.writeInt32(this.flags);
            this.peer.serializeToStream(abstractSerializedData);
            if ((this.flags & 4) != 0) {
                abstractSerializedData.writeInt32(this.offset_date);
                abstractSerializedData.writeString(this.offset_link);
            }
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class getSuggestedStarRefBots extends TLObject {
        public static final int constructor = 225134839;
        public int flags;
        public int limit;
        public String offset;
        public boolean order_by_date;
        public boolean order_by_revenue;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return suggestedStarRefBots.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(225134839);
            int i = this.order_by_revenue ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.order_by_date ? i | 2 : i & (-3);
            this.flags = i2;
            abstractSerializedData.writeInt32(i2);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeString(this.offset);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class starRefProgram extends TLObject {
        public static final int constructor = -586389774;
        public long bot_id;
        public int commission_permille;
        public TL_stars.StarsAmount daily_revenue_per_user = new TL_stars.StarsAmount(0);
        public int duration_months;
        public int end_date;
        public int flags;

        public static starRefProgram TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-586389774 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_payments.starRefProgram", Integer.valueOf(i)));
                }
                return null;
            }
            starRefProgram starrefprogram = new starRefProgram();
            starrefprogram.readParams(abstractSerializedData, z);
            return starrefprogram;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.bot_id = abstractSerializedData.readInt64(z);
            this.commission_permille = abstractSerializedData.readInt32(z);
            if ((this.flags & 1) != 0) {
                this.duration_months = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 2) != 0) {
                this.end_date = abstractSerializedData.readInt32(z);
            }
            if ((this.flags & 4) != 0) {
                this.daily_revenue_per_user = TL_stars.StarsAmount.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-586389774);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt64(this.bot_id);
            abstractSerializedData.writeInt32(this.commission_permille);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt32(this.duration_months);
            }
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(this.end_date);
            }
            if ((this.flags & 4) != 0) {
                this.daily_revenue_per_user.serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class suggestedStarRefBots extends TLObject {
        public static final int constructor = -1261053863;
        public int count;
        public int flags;
        public String next_offset;
        public ArrayList<starRefProgram> suggested_bots = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static suggestedStarRefBots TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-1261053863 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_payments.suggestedStarRefBots", Integer.valueOf(i)));
                }
                return null;
            }
            suggestedStarRefBots suggestedstarrefbots = new suggestedStarRefBots();
            suggestedstarrefbots.readParams(abstractSerializedData, z);
            return suggestedstarrefbots;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.count = abstractSerializedData.readInt32(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                this.suggested_bots.add(starRefProgram.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
            }
            int readInt323 = abstractSerializedData.readInt32(z);
            if (readInt323 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                }
                return;
            }
            int readInt324 = abstractSerializedData.readInt32(z);
            for (int i2 = 0; i2 < readInt324; i2++) {
                this.users.add(TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1261053863);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(481674261);
            int size = this.suggested_bots.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.suggested_bots.get(i).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.users.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.users.get(i2).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
            }
        }
    }
}

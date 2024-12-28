package org.telegram.tgnet.tl;

import java.util.ArrayList;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1;
import org.telegram.tgnet.TLRPC$TL_channels_adminLogResults$$ExternalSyntheticLambda1;
import org.telegram.tgnet.TLRPC$TL_contacts_found$$ExternalSyntheticLambda0;
import org.telegram.tgnet.TLRPC$TL_updateGroupCallParticipants$$ExternalSyntheticLambda0;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_phone;

public class TL_phone {

    public static abstract class PhoneCall extends TLObject {
        public long access_hash;
        public long admin_id;
        public TLRPC.TL_inputGroupCall conference_call;
        public ArrayList<TLRPC.PhoneConnection> connections = new ArrayList<>();
        public TLRPC.TL_dataJSON custom_parameters;
        public int date;
        public int duration;
        public int flags;
        public byte[] g_a_hash;
        public byte[] g_a_or_b;
        public byte[] g_b;
        public long id;
        public long key_fingerprint;
        public boolean need_debug;
        public boolean need_rating;
        public boolean p2p_allowed;
        public long participant_id;
        public PhoneCallProtocol protocol;
        public TLRPC.PhoneCallDiscardReason reason;
        public int receive_date;
        public int start_date;
        public boolean video;

        public static PhoneCall TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            PhoneCall tL_phoneCall_layer176;
            switch (i) {
                case -1770029977:
                    tL_phoneCall_layer176 = new TL_phoneCall_layer176();
                    break;
                case -987599081:
                    tL_phoneCall_layer176 = new TL_phoneCallWaiting_layer195();
                    break;
                case -288085928:
                    tL_phoneCall_layer176 = new TL_phoneCallWaiting();
                    break;
                case -103656189:
                    tL_phoneCall_layer176 = new TL_phoneCallDiscarded();
                    break;
                case 347139340:
                    tL_phoneCall_layer176 = new phoneCallRequested_layer195();
                    break;
                case 587035009:
                    tL_phoneCall_layer176 = new TL_phoneCallAccepted();
                    break;
                case 810769141:
                    tL_phoneCall_layer176 = new TL_phoneCall_layer195();
                    break;
                case 1000707084:
                    tL_phoneCall_layer176 = new TL_phoneCall();
                    break;
                case 1161174115:
                    tL_phoneCall_layer176 = new phoneCallRequested();
                    break;
                case 1355435489:
                    tL_phoneCall_layer176 = new TL_phoneCallDiscarded_layer195();
                    break;
                case 1399245077:
                    tL_phoneCall_layer176 = new TL_phoneCallEmpty();
                    break;
                default:
                    tL_phoneCall_layer176 = null;
                    break;
            }
            if (tL_phoneCall_layer176 == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in PhoneCall", Integer.valueOf(i)));
            }
            if (tL_phoneCall_layer176 != null) {
                tL_phoneCall_layer176.readParams(inputSerializedData, z);
            }
            return tL_phoneCall_layer176;
        }
    }

    public static abstract class PhoneCallProtocol extends TLObject {
        public int flags;
        public ArrayList<String> library_versions = new ArrayList<>();
        public int max_layer;
        public int min_layer;
        public boolean udp_p2p;
        public boolean udp_reflector;

        public static PhoneCallProtocol TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            PhoneCallProtocol tL_phoneCallProtocol = i != -1564789301 ? i != -58224696 ? null : new TL_phoneCallProtocol() : new TL_phoneCallProtocol_layer110();
            if (tL_phoneCallProtocol == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in PhoneCallProtocol", Integer.valueOf(i)));
            }
            if (tL_phoneCallProtocol != null) {
                tL_phoneCallProtocol.readParams(inputSerializedData, z);
            }
            return tL_phoneCallProtocol;
        }
    }

    public static class TL_groupCallStreamChannel extends TLObject {
        public static final int constructor = -2132064081;
        public int channel;
        public long last_timestamp_ms;
        public int scale;

        public static TL_groupCallStreamChannel TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-2132064081 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_groupCallStreamChannel", Integer.valueOf(i)));
                }
                return null;
            }
            TL_groupCallStreamChannel tL_groupCallStreamChannel = new TL_groupCallStreamChannel();
            tL_groupCallStreamChannel.readParams(inputSerializedData, z);
            return tL_groupCallStreamChannel;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.channel = inputSerializedData.readInt32(z);
            this.scale = inputSerializedData.readInt32(z);
            this.last_timestamp_ms = inputSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-2132064081);
            outputSerializedData.writeInt32(this.channel);
            outputSerializedData.writeInt32(this.scale);
            outputSerializedData.writeInt64(this.last_timestamp_ms);
        }
    }

    public static class TL_phoneCall extends PhoneCall {
        public static final int constructor = 1000707084;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.p2p_allowed = (readInt32 & 32) != 0;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.access_hash = inputSerializedData.readInt64(z);
            this.date = inputSerializedData.readInt32(z);
            this.admin_id = inputSerializedData.readInt64(z);
            this.participant_id = inputSerializedData.readInt64(z);
            this.g_a_or_b = inputSerializedData.readByteArray(z);
            this.key_fingerprint = inputSerializedData.readInt64(z);
            this.protocol = PhoneCallProtocol.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.connections = Vector.deserialize(inputSerializedData, new TL_phone$TL_phoneCall$$ExternalSyntheticLambda0(), z);
            this.start_date = inputSerializedData.readInt32(z);
            if ((this.flags & 128) != 0) {
                this.custom_parameters = TLRPC.TL_dataJSON.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 256) != 0) {
                this.conference_call = TLRPC.TL_inputGroupCall.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1000707084);
            int i = this.p2p_allowed ? this.flags | 32 : this.flags & (-33);
            this.flags = i;
            int i2 = this.video ? i | 64 : i & (-65);
            this.flags = i2;
            outputSerializedData.writeInt32(i2);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeInt64(this.access_hash);
            outputSerializedData.writeInt32(this.date);
            outputSerializedData.writeInt64(this.admin_id);
            outputSerializedData.writeInt64(this.participant_id);
            outputSerializedData.writeByteArray(this.g_a_or_b);
            outputSerializedData.writeInt64(this.key_fingerprint);
            this.protocol.serializeToStream(outputSerializedData);
            Vector.serialize(outputSerializedData, this.connections);
            outputSerializedData.writeInt32(this.start_date);
            if ((this.flags & 128) != 0) {
                this.custom_parameters.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 256) != 0) {
                this.conference_call.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_phoneCallAccepted extends PhoneCall {
        public static final int constructor = 587035009;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.access_hash = inputSerializedData.readInt64(z);
            this.date = inputSerializedData.readInt32(z);
            this.admin_id = inputSerializedData.readInt64(z);
            this.participant_id = inputSerializedData.readInt64(z);
            this.g_b = inputSerializedData.readByteArray(z);
            this.protocol = PhoneCallProtocol.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 256) != 0) {
                this.conference_call = TLRPC.TL_inputGroupCall.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(587035009);
            int i = this.video ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeInt64(this.access_hash);
            outputSerializedData.writeInt32(this.date);
            outputSerializedData.writeInt64(this.admin_id);
            outputSerializedData.writeInt64(this.participant_id);
            outputSerializedData.writeByteArray(this.g_b);
            this.protocol.serializeToStream(outputSerializedData);
            if ((this.flags & 256) != 0) {
                this.conference_call.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_phoneCallAccepted_layer195 extends TL_phoneCallAccepted {
        public static final int constructor = 912311057;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.access_hash = inputSerializedData.readInt64(z);
            this.date = inputSerializedData.readInt32(z);
            this.admin_id = inputSerializedData.readInt64(z);
            this.participant_id = inputSerializedData.readInt64(z);
            this.g_b = inputSerializedData.readByteArray(z);
            this.protocol = PhoneCallProtocol.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(912311057);
            int i = this.video ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeInt64(this.access_hash);
            outputSerializedData.writeInt32(this.date);
            outputSerializedData.writeInt64(this.admin_id);
            outputSerializedData.writeInt64(this.participant_id);
            outputSerializedData.writeByteArray(this.g_b);
            this.protocol.serializeToStream(outputSerializedData);
        }
    }

    public static class TL_phoneCallDiscarded extends PhoneCall {
        public static final int constructor = -103656189;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.need_rating = (readInt32 & 4) != 0;
            this.need_debug = (readInt32 & 8) != 0;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.reason = TLRPC.PhoneCallDiscardReason.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 2) != 0) {
                this.duration = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 256) != 0) {
                this.conference_call = TLRPC.TL_inputGroupCall.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-103656189);
            int i = this.need_rating ? this.flags | 4 : this.flags & (-5);
            this.flags = i;
            int i2 = this.need_debug ? i | 8 : i & (-9);
            this.flags = i2;
            int i3 = this.video ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            outputSerializedData.writeInt32(i3);
            outputSerializedData.writeInt64(this.id);
            if ((this.flags & 1) != 0) {
                this.reason.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeInt32(this.duration);
            }
            if ((this.flags & 256) != 0) {
                this.conference_call.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_phoneCallDiscarded_layer195 extends TL_phoneCallDiscarded {
        public static final int constructor = 1355435489;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.need_rating = (readInt32 & 4) != 0;
            this.need_debug = (readInt32 & 8) != 0;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            if ((this.flags & 1) != 0) {
                this.reason = TLRPC.PhoneCallDiscardReason.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
            if ((this.flags & 2) != 0) {
                this.duration = inputSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1355435489);
            int i = this.need_rating ? this.flags | 4 : this.flags & (-5);
            this.flags = i;
            int i2 = this.need_debug ? i | 8 : i & (-9);
            this.flags = i2;
            int i3 = this.video ? i2 | 64 : i2 & (-65);
            this.flags = i3;
            outputSerializedData.writeInt32(i3);
            outputSerializedData.writeInt64(this.id);
            if ((this.flags & 1) != 0) {
                this.reason.serializeToStream(outputSerializedData);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeInt32(this.duration);
            }
        }
    }

    public static class TL_phoneCallEmpty extends PhoneCall {
        public static final int constructor = 1399245077;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.id = inputSerializedData.readInt64(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1399245077);
            outputSerializedData.writeInt64(this.id);
        }
    }

    public static class TL_phoneCallProtocol extends PhoneCallProtocol {
        public static final int constructor = -58224696;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.udp_p2p = (readInt32 & 1) != 0;
            this.udp_reflector = (readInt32 & 2) != 0;
            this.min_layer = inputSerializedData.readInt32(z);
            this.max_layer = inputSerializedData.readInt32(z);
            this.library_versions = Vector.deserializeString(inputSerializedData, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-58224696);
            int i = this.udp_p2p ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.udp_reflector ? i | 2 : i & (-3);
            this.flags = i2;
            outputSerializedData.writeInt32(i2);
            outputSerializedData.writeInt32(this.min_layer);
            outputSerializedData.writeInt32(this.max_layer);
            Vector.serializeString(outputSerializedData, this.library_versions);
        }
    }

    public static class TL_phoneCallProtocol_layer110 extends TL_phoneCallProtocol {
        public static final int constructor = -1564789301;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.udp_p2p = (readInt32 & 1) != 0;
            this.udp_reflector = (readInt32 & 2) != 0;
            this.min_layer = inputSerializedData.readInt32(z);
            this.max_layer = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1564789301);
            int i = this.udp_p2p ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.udp_reflector ? i | 2 : i & (-3);
            this.flags = i2;
            outputSerializedData.writeInt32(i2);
            outputSerializedData.writeInt32(this.min_layer);
            outputSerializedData.writeInt32(this.max_layer);
        }
    }

    public static class TL_phoneCallWaiting extends PhoneCall {
        public static final int constructor = -288085928;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.access_hash = inputSerializedData.readInt64(z);
            this.date = inputSerializedData.readInt32(z);
            this.admin_id = inputSerializedData.readInt64(z);
            this.participant_id = inputSerializedData.readInt64(z);
            this.protocol = PhoneCallProtocol.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.receive_date = inputSerializedData.readInt32(z);
            }
            if ((this.flags & 256) != 0) {
                this.conference_call = TLRPC.TL_inputGroupCall.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-288085928);
            int i = this.video ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeInt64(this.access_hash);
            outputSerializedData.writeInt32(this.date);
            outputSerializedData.writeInt64(this.admin_id);
            outputSerializedData.writeInt64(this.participant_id);
            this.protocol.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt32(this.receive_date);
            }
            if ((this.flags & 256) != 0) {
                this.conference_call.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_phoneCallWaiting_layer195 extends TL_phoneCallWaiting {
        public static final int constructor = -987599081;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.access_hash = inputSerializedData.readInt64(z);
            this.date = inputSerializedData.readInt32(z);
            this.admin_id = inputSerializedData.readInt64(z);
            this.participant_id = inputSerializedData.readInt64(z);
            this.protocol = PhoneCallProtocol.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.receive_date = inputSerializedData.readInt32(z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-987599081);
            int i = this.video ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeInt64(this.access_hash);
            outputSerializedData.writeInt32(this.date);
            outputSerializedData.writeInt64(this.admin_id);
            outputSerializedData.writeInt64(this.participant_id);
            this.protocol.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeInt32(this.receive_date);
            }
        }
    }

    public static class TL_phoneCall_layer176 extends TL_phoneCall {
        public static final int constructor = -1770029977;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.p2p_allowed = (readInt32 & 32) != 0;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.access_hash = inputSerializedData.readInt64(z);
            this.date = inputSerializedData.readInt32(z);
            this.admin_id = inputSerializedData.readInt64(z);
            this.participant_id = inputSerializedData.readInt64(z);
            this.g_a_or_b = inputSerializedData.readByteArray(z);
            this.key_fingerprint = inputSerializedData.readInt64(z);
            this.protocol = PhoneCallProtocol.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.connections = Vector.deserialize(inputSerializedData, new TL_phone$TL_phoneCall$$ExternalSyntheticLambda0(), z);
            this.start_date = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1770029977);
            int i = this.p2p_allowed ? this.flags | 32 : this.flags & (-33);
            this.flags = i;
            int i2 = this.video ? i | 64 : i & (-65);
            this.flags = i2;
            outputSerializedData.writeInt32(i2);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeInt64(this.access_hash);
            outputSerializedData.writeInt32(this.date);
            outputSerializedData.writeInt64(this.admin_id);
            outputSerializedData.writeInt64(this.participant_id);
            outputSerializedData.writeByteArray(this.g_a_or_b);
            outputSerializedData.writeInt64(this.key_fingerprint);
            this.protocol.serializeToStream(outputSerializedData);
            Vector.serialize(outputSerializedData, this.connections);
            outputSerializedData.writeInt32(this.start_date);
        }
    }

    public static class TL_phoneCall_layer195 extends TL_phoneCall {
        public static final int constructor = 810769141;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.p2p_allowed = (readInt32 & 32) != 0;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.access_hash = inputSerializedData.readInt64(z);
            this.date = inputSerializedData.readInt32(z);
            this.admin_id = inputSerializedData.readInt64(z);
            this.participant_id = inputSerializedData.readInt64(z);
            this.g_a_or_b = inputSerializedData.readByteArray(z);
            this.key_fingerprint = inputSerializedData.readInt64(z);
            this.protocol = PhoneCallProtocol.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.connections = Vector.deserialize(inputSerializedData, new TL_phone$TL_phoneCall$$ExternalSyntheticLambda0(), z);
            this.start_date = inputSerializedData.readInt32(z);
            if ((this.flags & 128) != 0) {
                this.custom_parameters = TLRPC.TL_dataJSON.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(810769141);
            int i = this.p2p_allowed ? this.flags | 32 : this.flags & (-33);
            this.flags = i;
            int i2 = this.video ? i | 64 : i & (-65);
            this.flags = i2;
            outputSerializedData.writeInt32(i2);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeInt64(this.access_hash);
            outputSerializedData.writeInt32(this.date);
            outputSerializedData.writeInt64(this.admin_id);
            outputSerializedData.writeInt64(this.participant_id);
            outputSerializedData.writeByteArray(this.g_a_or_b);
            outputSerializedData.writeInt64(this.key_fingerprint);
            this.protocol.serializeToStream(outputSerializedData);
            Vector.serialize(outputSerializedData, this.connections);
            outputSerializedData.writeInt32(this.start_date);
            if ((this.flags & 128) != 0) {
                this.custom_parameters.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class TL_phone_phoneCall extends TLObject {
        public static final int constructor = -326966976;
        public PhoneCall phone_call;
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_phone_phoneCall TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-326966976 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_phone_phoneCall", Integer.valueOf(i)));
                }
                return null;
            }
            TL_phone_phoneCall tL_phone_phoneCall = new TL_phone_phoneCall();
            tL_phone_phoneCall.readParams(inputSerializedData, z);
            return tL_phone_phoneCall;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.phone_call = PhoneCall.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.users = Vector.deserialize(inputSerializedData, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-326966976);
            this.phone_call.serializeToStream(outputSerializedData);
            Vector.serialize(outputSerializedData, this.users);
        }
    }

    public static class acceptCall extends TLObject {
        public static final int constructor = 1003664544;
        public byte[] g_b;
        public TLRPC.TL_inputPhoneCall peer;
        public TL_phoneCallProtocol protocol;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_phone_phoneCall.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1003664544);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeByteArray(this.g_b);
            this.protocol.serializeToStream(outputSerializedData);
        }
    }

    public static class checkGroupCall extends TLObject {
        public static final int constructor = -1248003721;
        public TLRPC.TL_inputGroupCall call;
        public ArrayList<Integer> sources = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return Vector.TLDeserializeInt(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1248003721);
            this.call.serializeToStream(outputSerializedData);
            Vector.serializeInt(outputSerializedData, this.sources);
        }
    }

    public static class confirmCall extends TLObject {
        public static final int constructor = 788404002;
        public byte[] g_a;
        public long key_fingerprint;
        public TLRPC.TL_inputPhoneCall peer;
        public TL_phoneCallProtocol protocol;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_phone_phoneCall.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(788404002);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeByteArray(this.g_a);
            outputSerializedData.writeInt64(this.key_fingerprint);
            this.protocol.serializeToStream(outputSerializedData);
        }
    }

    public static class createConferenceCall extends TLObject {
        public static final int constructor = -540472917;
        public long key_fingerprint;
        public TLRPC.TL_inputGroupCall peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return PhoneCall.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-540472917);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt64(this.key_fingerprint);
        }
    }

    public static class createGroupCall extends TLObject {
        public static final int constructor = 1221445336;
        public int flags;
        public TLRPC.InputPeer peer;
        public int random_id;
        public int schedule_date;
        public String title;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1221445336);
            outputSerializedData.writeInt32(this.flags);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.random_id);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeInt32(this.schedule_date);
            }
        }
    }

    public static class discardCall extends TLObject {
        public static final int constructor = -1295269440;
        public long connection_id;
        public int duration;
        public int flags;
        public TLRPC.TL_inputPhoneCall peer;
        public TLRPC.PhoneCallDiscardReason reason;
        public boolean video;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1295269440);
            int i = this.video ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.duration);
            this.reason.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt64(this.connection_id);
        }
    }

    public static class discardGroupCall extends TLObject {
        public static final int constructor = 2054648117;
        public TLRPC.TL_inputGroupCall call;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(2054648117);
            this.call.serializeToStream(outputSerializedData);
        }
    }

    public static class editGroupCallParticipant extends TLObject {
        public static final int constructor = -1524155713;
        public TLRPC.TL_inputGroupCall call;
        public int flags;
        public boolean muted;
        public TLRPC.InputPeer participant;
        public boolean presentation_paused;
        public boolean raise_hand;
        public boolean video_paused;
        public boolean video_stopped;
        public int volume;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1524155713);
            outputSerializedData.writeInt32(this.flags);
            this.call.serializeToStream(outputSerializedData);
            this.participant.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeBool(this.muted);
            }
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeInt32(this.volume);
            }
            if ((this.flags & 4) != 0) {
                outputSerializedData.writeBool(this.raise_hand);
            }
            if ((this.flags & 8) != 0) {
                outputSerializedData.writeBool(this.video_stopped);
            }
            if ((this.flags & 16) != 0) {
                outputSerializedData.writeBool(this.video_paused);
            }
            if ((this.flags & 32) != 0) {
                outputSerializedData.writeBool(this.presentation_paused);
            }
        }
    }

    public static class editGroupCallTitle extends TLObject {
        public static final int constructor = 480685066;
        public TLRPC.TL_inputGroupCall call;
        public String title;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(480685066);
            this.call.serializeToStream(outputSerializedData);
            outputSerializedData.writeString(this.title);
        }
    }

    public static class exportGroupCallInvite extends TLObject {
        public static final int constructor = -425040769;
        public TLRPC.TL_inputGroupCall call;
        public boolean can_self_unmute;
        public int flags;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return exportedGroupCallInvite.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-425040769);
            int i = this.can_self_unmute ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.call.serializeToStream(outputSerializedData);
        }
    }

    public static class exportedGroupCallInvite extends TLObject {
        public static final int constructor = 541839704;
        public String link;

        public static exportedGroupCallInvite TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (541839704 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_phone_exportedGroupCallInvite", Integer.valueOf(i)));
                }
                return null;
            }
            exportedGroupCallInvite exportedgroupcallinvite = new exportedGroupCallInvite();
            exportedgroupcallinvite.readParams(inputSerializedData, z);
            return exportedgroupcallinvite;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.link = inputSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(541839704);
            outputSerializedData.writeString(this.link);
        }
    }

    public static class getCallConfig extends TLObject {
        public static final int constructor = 1430593449;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.TL_dataJSON.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1430593449);
        }
    }

    public static class getGroupCall extends TLObject {
        public static final int constructor = 68699611;
        public TLRPC.TL_inputGroupCall call;
        public int limit;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return groupCall.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(68699611);
            this.call.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.limit);
        }
    }

    public static class getGroupCallJoinAs extends TLObject {
        public static final int constructor = -277077702;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return joinAsPeers.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-277077702);
            this.peer.serializeToStream(outputSerializedData);
        }
    }

    public static class getGroupCallStreamChannels extends TLObject {
        public static final int constructor = 447879488;
        public TLRPC.TL_inputGroupCall call;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return groupCallStreamChannels.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(447879488);
            this.call.serializeToStream(outputSerializedData);
        }
    }

    public static class getGroupCallStreamRtmpUrl extends TLObject {
        public static final int constructor = -558650433;
        public TLRPC.InputPeer peer;
        public boolean revoke;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return groupCallStreamRtmpUrl.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-558650433);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeBool(this.revoke);
        }
    }

    public static class getGroupParticipants extends TLObject {
        public static final int constructor = -984033109;
        public TLRPC.TL_inputGroupCall call;
        public int limit;
        public String offset;
        public ArrayList<TLRPC.InputPeer> ids = new ArrayList<>();
        public ArrayList<Integer> sources = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return groupParticipants.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-984033109);
            this.call.serializeToStream(outputSerializedData);
            Vector.serialize(outputSerializedData, this.ids);
            Vector.serializeInt(outputSerializedData, this.sources);
            outputSerializedData.writeString(this.offset);
            outputSerializedData.writeInt32(this.limit);
        }
    }

    public static class groupCall extends TLObject {
        public static final int constructor = -1636664659;
        public TLRPC.GroupCall call;
        public String participants_next_offset;
        public ArrayList<TLRPC.TL_groupCallParticipant> participants = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static groupCall TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-1636664659 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_phone_groupCall", Integer.valueOf(i)));
                }
                return null;
            }
            groupCall groupcall = new groupCall();
            groupcall.readParams(inputSerializedData, z);
            return groupcall;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.call = TLRPC.GroupCall.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            this.participants = Vector.deserialize(inputSerializedData, new TLRPC$TL_updateGroupCallParticipants$$ExternalSyntheticLambda0(), z);
            this.participants_next_offset = inputSerializedData.readString(z);
            this.chats = Vector.deserialize(inputSerializedData, new TLRPC$TL_channels_adminLogResults$$ExternalSyntheticLambda1(), z);
            this.users = Vector.deserialize(inputSerializedData, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1636664659);
            this.call.serializeToStream(outputSerializedData);
            Vector.serialize(outputSerializedData, this.participants);
            outputSerializedData.writeString(this.participants_next_offset);
            Vector.serialize(outputSerializedData, this.chats);
            Vector.serialize(outputSerializedData, this.users);
        }
    }

    public static class groupCallStreamChannels extends TLObject {
        public static final int constructor = -790330702;
        public ArrayList<TL_groupCallStreamChannel> channels = new ArrayList<>();

        public static groupCallStreamChannels TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-790330702 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_phone_groupCallStreamChannels", Integer.valueOf(i)));
                }
                return null;
            }
            groupCallStreamChannels groupcallstreamchannels = new groupCallStreamChannels();
            groupcallstreamchannels.readParams(inputSerializedData, z);
            return groupcallstreamchannels;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.channels = Vector.deserialize(inputSerializedData, new Vector.TLDeserializer() {
                @Override
                public final TLObject deserialize(InputSerializedData inputSerializedData2, int i, boolean z2) {
                    return TL_phone.TL_groupCallStreamChannel.TLdeserialize(inputSerializedData2, i, z2);
                }
            }, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-790330702);
            Vector.serialize(outputSerializedData, this.channels);
        }
    }

    public static class groupCallStreamRtmpUrl extends TLObject {
        public static final int constructor = 767505458;
        public String key;
        public String url;

        public static groupCallStreamRtmpUrl TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (767505458 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_phone_groupCallStreamRtmpUrl", Integer.valueOf(i)));
                }
                return null;
            }
            groupCallStreamRtmpUrl groupcallstreamrtmpurl = new groupCallStreamRtmpUrl();
            groupcallstreamrtmpurl.readParams(inputSerializedData, z);
            return groupcallstreamrtmpurl;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.url = inputSerializedData.readString(z);
            this.key = inputSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(767505458);
            outputSerializedData.writeString(this.url);
            outputSerializedData.writeString(this.key);
        }
    }

    public static class groupParticipants extends TLObject {
        public static final int constructor = -193506890;
        public int count;
        public String next_offset;
        public int version;
        public ArrayList<TLRPC.TL_groupCallParticipant> participants = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static groupParticipants TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-193506890 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_phone_groupParticipants", Integer.valueOf(i)));
                }
                return null;
            }
            groupParticipants groupparticipants = new groupParticipants();
            groupparticipants.readParams(inputSerializedData, z);
            return groupparticipants;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.count = inputSerializedData.readInt32(z);
            this.participants = Vector.deserialize(inputSerializedData, new TLRPC$TL_updateGroupCallParticipants$$ExternalSyntheticLambda0(), z);
            this.next_offset = inputSerializedData.readString(z);
            this.chats = Vector.deserialize(inputSerializedData, new TLRPC$TL_channels_adminLogResults$$ExternalSyntheticLambda1(), z);
            this.users = Vector.deserialize(inputSerializedData, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1(), z);
            this.version = inputSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-193506890);
            outputSerializedData.writeInt32(this.count);
            Vector.serialize(outputSerializedData, this.participants);
            outputSerializedData.writeString(this.next_offset);
            Vector.serialize(outputSerializedData, this.chats);
            Vector.serialize(outputSerializedData, this.users);
            outputSerializedData.writeInt32(this.version);
        }
    }

    public static class inviteToGroupCall extends TLObject {
        public static final int constructor = 2067345760;
        public TLRPC.TL_inputGroupCall call;
        public ArrayList<TLRPC.InputUser> users = new ArrayList<>();

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(2067345760);
            this.call.serializeToStream(outputSerializedData);
            Vector.serialize(outputSerializedData, this.users);
        }
    }

    public static class joinAsPeers extends TLObject {
        public static final int constructor = -1343921601;
        public ArrayList<TLRPC.Peer> peers = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static joinAsPeers TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
            if (-1343921601 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_phone_joinAsPeers", Integer.valueOf(i)));
                }
                return null;
            }
            joinAsPeers joinaspeers = new joinAsPeers();
            joinaspeers.readParams(inputSerializedData, z);
            return joinaspeers;
        }

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            this.peers = Vector.deserialize(inputSerializedData, new TLRPC$TL_contacts_found$$ExternalSyntheticLambda0(), z);
            this.chats = Vector.deserialize(inputSerializedData, new TLRPC$TL_channels_adminLogResults$$ExternalSyntheticLambda1(), z);
            this.users = Vector.deserialize(inputSerializedData, new TLRPC$TL_attachMenuBots$$ExternalSyntheticLambda1(), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1343921601);
            Vector.serialize(outputSerializedData, this.peers);
            Vector.serialize(outputSerializedData, this.chats);
            Vector.serialize(outputSerializedData, this.users);
        }
    }

    public static class joinGroupCall extends TLObject {
        public static final int constructor = -1322057861;
        public TLRPC.TL_inputGroupCall call;
        public int flags;
        public String invite_hash;
        public TLRPC.InputPeer join_as;
        public boolean muted;
        public TLRPC.TL_dataJSON params;
        public boolean video_stopped;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1322057861);
            int i = this.muted ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.video_stopped ? i | 4 : i & (-5);
            this.flags = i2;
            outputSerializedData.writeInt32(i2);
            this.call.serializeToStream(outputSerializedData);
            this.join_as.serializeToStream(outputSerializedData);
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.invite_hash);
            }
            this.params.serializeToStream(outputSerializedData);
        }
    }

    public static class joinGroupCallPresentation extends TLObject {
        public static final int constructor = -873829436;
        public TLRPC.TL_inputGroupCall call;
        public TLRPC.TL_dataJSON params;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-873829436);
            this.call.serializeToStream(outputSerializedData);
            this.params.serializeToStream(outputSerializedData);
        }
    }

    public static class leaveGroupCall extends TLObject {
        public static final int constructor = 1342404601;
        public TLRPC.TL_inputGroupCall call;
        public int source;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1342404601);
            this.call.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.source);
        }
    }

    public static class leaveGroupCallPresentation extends TLObject {
        public static final int constructor = 475058500;
        public TLRPC.TL_inputGroupCall call;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(475058500);
            this.call.serializeToStream(outputSerializedData);
        }
    }

    public static class phoneCallRequested extends PhoneCall {
        public static final int constructor = 1161174115;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.access_hash = inputSerializedData.readInt64(z);
            this.date = inputSerializedData.readInt32(z);
            this.admin_id = inputSerializedData.readInt64(z);
            this.participant_id = inputSerializedData.readInt64(z);
            this.g_a_hash = inputSerializedData.readByteArray(z);
            this.protocol = PhoneCallProtocol.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            if ((this.flags & 256) != 0) {
                this.conference_call = TLRPC.TL_inputGroupCall.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
            }
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1161174115);
            int i = this.video ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeInt64(this.access_hash);
            outputSerializedData.writeInt32(this.date);
            outputSerializedData.writeInt64(this.admin_id);
            outputSerializedData.writeInt64(this.participant_id);
            outputSerializedData.writeByteArray(this.g_a_hash);
            this.protocol.serializeToStream(outputSerializedData);
            if ((this.flags & 256) != 0) {
                this.conference_call.serializeToStream(outputSerializedData);
            }
        }
    }

    public static class phoneCallRequested_layer195 extends phoneCallRequested {
        public static final int constructor = 347139340;

        @Override
        public void readParams(InputSerializedData inputSerializedData, boolean z) {
            int readInt32 = inputSerializedData.readInt32(z);
            this.flags = readInt32;
            this.video = (readInt32 & 64) != 0;
            this.id = inputSerializedData.readInt64(z);
            this.access_hash = inputSerializedData.readInt64(z);
            this.date = inputSerializedData.readInt32(z);
            this.admin_id = inputSerializedData.readInt64(z);
            this.participant_id = inputSerializedData.readInt64(z);
            this.g_a_hash = inputSerializedData.readByteArray(z);
            this.protocol = PhoneCallProtocol.TLdeserialize(inputSerializedData, inputSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(347139340);
            int i = this.video ? this.flags | 64 : this.flags & (-65);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            outputSerializedData.writeInt64(this.id);
            outputSerializedData.writeInt64(this.access_hash);
            outputSerializedData.writeInt32(this.date);
            outputSerializedData.writeInt64(this.admin_id);
            outputSerializedData.writeInt64(this.participant_id);
            outputSerializedData.writeByteArray(this.g_a_hash);
            this.protocol.serializeToStream(outputSerializedData);
        }
    }

    public static class receivedCall extends TLObject {
        public static final int constructor = 399855457;
        public TLRPC.TL_inputPhoneCall peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(399855457);
            this.peer.serializeToStream(outputSerializedData);
        }
    }

    public static class requestCall extends TLObject {
        public static final int constructor = -1497079796;
        public TLRPC.TL_inputGroupCall conference_call;
        public int flags;
        public byte[] g_a_hash;
        public TL_phoneCallProtocol protocol;
        public int random_id;
        public TLRPC.InputUser user_id;
        public boolean video;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TL_phone_phoneCall.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-1497079796);
            int i = this.video ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.user_id.serializeToStream(outputSerializedData);
            if ((this.flags & 2) != 0) {
                this.conference_call.serializeToStream(outputSerializedData);
            }
            outputSerializedData.writeInt32(this.random_id);
            outputSerializedData.writeByteArray(this.g_a_hash);
            this.protocol.serializeToStream(outputSerializedData);
        }
    }

    public static class saveCallDebug extends TLObject {
        public static final int constructor = 662363518;
        public TLRPC.TL_dataJSON debug;
        public TLRPC.TL_inputPhoneCall peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(662363518);
            this.peer.serializeToStream(outputSerializedData);
            this.debug.serializeToStream(outputSerializedData);
        }
    }

    public static class saveCallLog extends TLObject {
        public static final int constructor = 1092913030;
        public TLRPC.InputFile file;
        public TLRPC.TL_inputPhoneCall peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1092913030);
            this.peer.serializeToStream(outputSerializedData);
            this.file.serializeToStream(outputSerializedData);
        }
    }

    public static class saveDefaultGroupCallJoinAs extends TLObject {
        public static final int constructor = 1465786252;
        public TLRPC.InputPeer join_as;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1465786252);
            this.peer.serializeToStream(outputSerializedData);
            this.join_as.serializeToStream(outputSerializedData);
        }
    }

    public static class sendSignalingData extends TLObject {
        public static final int constructor = -8744061;
        public byte[] data;
        public TLRPC.TL_inputPhoneCall peer;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-8744061);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeByteArray(this.data);
        }
    }

    public static class setCallRating extends TLObject {
        public static final int constructor = 1508562471;
        public String comment;
        public int flags;
        public TLRPC.TL_inputPhoneCall peer;
        public int rating;
        public boolean user_initiative;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1508562471);
            int i = this.user_initiative ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.peer.serializeToStream(outputSerializedData);
            outputSerializedData.writeInt32(this.rating);
            outputSerializedData.writeString(this.comment);
        }
    }

    public static class startScheduledGroupCall extends TLObject {
        public static final int constructor = 1451287362;
        public TLRPC.TL_inputGroupCall call;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1451287362);
            this.call.serializeToStream(outputSerializedData);
        }
    }

    public static class toggleGroupCallRecord extends TLObject {
        public static final int constructor = -248985848;
        public TLRPC.TL_inputGroupCall call;
        public int flags;
        public boolean start;
        public String title;
        public boolean video;
        public boolean video_portrait;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(-248985848);
            int i = this.start ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            int i2 = this.video ? i | 4 : i & (-5);
            this.flags = i2;
            outputSerializedData.writeInt32(i2);
            this.call.serializeToStream(outputSerializedData);
            if ((this.flags & 2) != 0) {
                outputSerializedData.writeString(this.title);
            }
            if ((this.flags & 4) != 0) {
                outputSerializedData.writeBool(this.video_portrait);
            }
        }
    }

    public static class toggleGroupCallSettings extends TLObject {
        public static final int constructor = 1958458429;
        public TLRPC.TL_inputGroupCall call;
        public int flags;
        public boolean join_muted;
        public boolean reset_invite_hash;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(1958458429);
            int i = this.reset_invite_hash ? this.flags | 2 : this.flags & (-3);
            this.flags = i;
            outputSerializedData.writeInt32(i);
            this.call.serializeToStream(outputSerializedData);
            if ((this.flags & 1) != 0) {
                outputSerializedData.writeBool(this.join_muted);
            }
        }
    }

    public static class toggleGroupCallStartSubscription extends TLObject {
        public static final int constructor = 563885286;
        public TLRPC.TL_inputGroupCall call;
        public boolean subscribed;

        @Override
        public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
            return TLRPC.Updates.TLdeserialize(inputSerializedData, i, z);
        }

        @Override
        public void serializeToStream(OutputSerializedData outputSerializedData) {
            outputSerializedData.writeInt32(563885286);
            this.call.serializeToStream(outputSerializedData);
            outputSerializedData.writeBool(this.subscribed);
        }
    }
}

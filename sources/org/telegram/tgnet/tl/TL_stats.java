package org.telegram.tgnet.tl;

import java.util.ArrayList;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;

public class TL_stats {

    public static class BroadcastRevenueTransaction extends TLObject {
        public static BroadcastRevenueTransaction TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            BroadcastRevenueTransaction tL_broadcastRevenueTransactionWithdrawal = i != 1121127726 ? i != 1434332356 ? i != 1515784568 ? null : new TL_broadcastRevenueTransactionWithdrawal() : new TL_broadcastRevenueTransactionProceeds() : new TL_broadcastRevenueTransactionRefund();
            if (tL_broadcastRevenueTransactionWithdrawal == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in BroadcastRevenueTransaction", Integer.valueOf(i)));
            }
            if (tL_broadcastRevenueTransactionWithdrawal != null) {
                tL_broadcastRevenueTransactionWithdrawal.readParams(abstractSerializedData, z);
            }
            return tL_broadcastRevenueTransactionWithdrawal;
        }
    }

    public static abstract class PostInteractionCounters extends TLObject {
        public static PostInteractionCounters TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            PostInteractionCounters tL_postInteractionCountersMessage = i != -1974989273 ? i != -419066241 ? null : new TL_postInteractionCountersMessage() : new TL_postInteractionCountersStory();
            if (tL_postInteractionCountersMessage == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in PostInteractionCounters", Integer.valueOf(i)));
            }
            if (tL_postInteractionCountersMessage != null) {
                tL_postInteractionCountersMessage.readParams(abstractSerializedData, z);
            }
            return tL_postInteractionCountersMessage;
        }
    }

    public static abstract class PublicForward extends TLObject {
        public static PublicForward TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            PublicForward tL_publicForwardMessage = i != -302797360 ? i != 32685898 ? null : new TL_publicForwardMessage() : new TL_stories.TL_publicForwardStory();
            if (tL_publicForwardMessage == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in PublicForward", Integer.valueOf(i)));
            }
            if (tL_publicForwardMessage != null) {
                tL_publicForwardMessage.readParams(abstractSerializedData, z);
            }
            return tL_publicForwardMessage;
        }
    }

    public static abstract class StatsGraph extends TLObject {
        public float rate;

        public static StatsGraph TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            StatsGraph tL_statsGraphAsync = i != -1901828938 ? i != -1092839390 ? i != 1244130093 ? null : new TL_statsGraphAsync() : new TL_statsGraphError() : new TL_statsGraph();
            if (tL_statsGraphAsync == null && z) {
                throw new RuntimeException(String.format("can't parse magic %x in StatsGraph", Integer.valueOf(i)));
            }
            if (tL_statsGraphAsync != null) {
                tL_statsGraphAsync.readParams(abstractSerializedData, z);
            }
            return tL_statsGraphAsync;
        }
    }

    public static class TL_broadcastRevenueStats extends TLObject {
        public static final int constructor = 1409802903;
        public TLRPC.BroadcastRevenueBalances balances;
        public StatsGraph revenue_graph;
        public StatsGraph top_hours_graph;
        public double usd_rate;

        public static TL_broadcastRevenueStats TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (1409802903 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stats_broadcastRevenueStats", Integer.valueOf(i)));
                }
                return null;
            }
            TL_broadcastRevenueStats tL_broadcastRevenueStats = new TL_broadcastRevenueStats();
            tL_broadcastRevenueStats.readParams(abstractSerializedData, z);
            return tL_broadcastRevenueStats;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.top_hours_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.revenue_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.balances = TLRPC.BroadcastRevenueBalances.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.usd_rate = abstractSerializedData.readDouble(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1409802903);
            this.top_hours_graph.serializeToStream(abstractSerializedData);
            this.revenue_graph.serializeToStream(abstractSerializedData);
            this.balances.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeDouble(this.usd_rate);
        }
    }

    public static class TL_broadcastRevenueTransactionProceeds extends BroadcastRevenueTransaction {
        public static final int constructor = 1434332356;
        public long amount;
        public int from_date;
        public int to_date;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.amount = abstractSerializedData.readInt64(z);
            this.from_date = abstractSerializedData.readInt32(z);
            this.to_date = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1434332356);
            abstractSerializedData.writeInt64(this.amount);
            abstractSerializedData.writeInt32(this.from_date);
            abstractSerializedData.writeInt32(this.to_date);
        }
    }

    public static class TL_broadcastRevenueTransactionRefund extends BroadcastRevenueTransaction {
        public static final int constructor = 1121127726;
        public long amount;
        public int from_date;
        public String provider;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.amount = abstractSerializedData.readInt64(z);
            this.from_date = abstractSerializedData.readInt32(z);
            this.provider = abstractSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1121127726);
            abstractSerializedData.writeInt64(this.amount);
            abstractSerializedData.writeInt32(this.from_date);
            abstractSerializedData.writeString(this.provider);
        }
    }

    public static class TL_broadcastRevenueTransactionWithdrawal extends BroadcastRevenueTransaction {
        public static final int constructor = 1515784568;
        public long amount;
        public int date;
        public boolean failed;
        public int flags;
        public boolean pending;
        public String provider;
        public int transaction_date;
        public String transaction_url;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            int readInt32 = abstractSerializedData.readInt32(z);
            this.flags = readInt32;
            this.pending = (readInt32 & 1) != 0;
            this.failed = (readInt32 & 4) != 0;
            this.amount = abstractSerializedData.readInt64(z);
            this.date = abstractSerializedData.readInt32(z);
            this.provider = abstractSerializedData.readString(z);
            if ((this.flags & 2) != 0) {
                this.transaction_date = abstractSerializedData.readInt32(z);
                this.transaction_url = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1515784568);
            int i = this.pending ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            this.flags = this.failed ? i | 1 : i & (-2);
            abstractSerializedData.writeInt64(this.amount);
            abstractSerializedData.writeInt32(this.date);
            abstractSerializedData.writeString(this.provider);
            if ((this.flags & 2) != 0) {
                abstractSerializedData.writeInt32(this.transaction_date);
                abstractSerializedData.writeString(this.transaction_url);
            }
        }
    }

    public static class TL_broadcastRevenueTransactions extends TLObject {
        public static final int constructor = -2028632986;
        public int count;
        public ArrayList<BroadcastRevenueTransaction> transactions = new ArrayList<>();

        public static TL_broadcastRevenueTransactions TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-2028632986 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stats_broadcastRevenueTransactions", Integer.valueOf(i)));
                }
                return null;
            }
            TL_broadcastRevenueTransactions tL_broadcastRevenueTransactions = new TL_broadcastRevenueTransactions();
            tL_broadcastRevenueTransactions.readParams(abstractSerializedData, z);
            return tL_broadcastRevenueTransactions;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.count = abstractSerializedData.readInt32(z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
            } else {
                int readInt322 = abstractSerializedData.readInt32(z);
                for (int i = 0; i < readInt322; i++) {
                    this.transactions.add(BroadcastRevenueTransaction.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z));
                }
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-2028632986);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(481674261);
            int size = this.transactions.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.transactions.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_broadcastRevenueWithdrawalUrl extends TLObject {
        public static final int constructor = -328886473;
        public String url;

        public static TL_broadcastRevenueWithdrawalUrl TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-328886473 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stats_broadcastRevenueWithdrawalUrl", Integer.valueOf(i)));
                }
                return null;
            }
            TL_broadcastRevenueWithdrawalUrl tL_broadcastRevenueWithdrawalUrl = new TL_broadcastRevenueWithdrawalUrl();
            tL_broadcastRevenueWithdrawalUrl.readParams(abstractSerializedData, z);
            return tL_broadcastRevenueWithdrawalUrl;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.url = abstractSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-328886473);
            abstractSerializedData.writeString(this.url);
        }
    }

    public static class TL_broadcastStats extends TLObject {
        public static int constructor = 963421692;
        public TL_statsPercentValue enabled_notifications;
        public TL_statsAbsValueAndPrev followers;
        public StatsGraph followers_graph;
        public StatsGraph growth_graph;
        public StatsGraph interactions_graph;
        public StatsGraph iv_interactions_graph;
        public StatsGraph languages_graph;
        public StatsGraph mute_graph;
        public StatsGraph new_followers_by_source_graph;
        public TL_statsDateRangeDays period;
        public StatsGraph reactions_by_emotion_graph;
        public TL_statsAbsValueAndPrev reactions_per_post;
        public TL_statsAbsValueAndPrev reactions_per_story;
        public ArrayList<PostInteractionCounters> recent_posts_interactions = new ArrayList<>();
        public TL_statsAbsValueAndPrev shares_per_post;
        public TL_statsAbsValueAndPrev shares_per_story;
        public StatsGraph story_interactions_graph;
        public StatsGraph story_reactions_by_emotion_graph;
        public StatsGraph top_hours_graph;
        public StatsGraph views_by_source_graph;
        public TL_statsAbsValueAndPrev views_per_post;
        public TL_statsAbsValueAndPrev views_per_story;

        public static TL_broadcastStats TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (constructor != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stats_broadcastStats", Integer.valueOf(i)));
                }
                return null;
            }
            TL_broadcastStats tL_broadcastStats = new TL_broadcastStats();
            tL_broadcastStats.readParams(abstractSerializedData, z);
            return tL_broadcastStats;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.period = TL_statsDateRangeDays.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.followers = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.views_per_post = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.shares_per_post = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.reactions_per_post = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.views_per_story = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.shares_per_story = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.reactions_per_story = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.enabled_notifications = TL_statsPercentValue.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.growth_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.followers_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.mute_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.top_hours_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.interactions_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.iv_interactions_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.views_by_source_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.new_followers_by_source_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.languages_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.reactions_by_emotion_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.story_interactions_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.story_reactions_by_emotion_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                PostInteractionCounters TLdeserialize = PostInteractionCounters.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.recent_posts_interactions.add(TLdeserialize);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(constructor);
            this.period.serializeToStream(abstractSerializedData);
            this.followers.serializeToStream(abstractSerializedData);
            this.views_per_post.serializeToStream(abstractSerializedData);
            this.shares_per_post.serializeToStream(abstractSerializedData);
            this.reactions_per_post.serializeToStream(abstractSerializedData);
            this.views_per_story.serializeToStream(abstractSerializedData);
            this.shares_per_story.serializeToStream(abstractSerializedData);
            this.reactions_per_story.serializeToStream(abstractSerializedData);
            this.enabled_notifications.serializeToStream(abstractSerializedData);
            this.growth_graph.serializeToStream(abstractSerializedData);
            this.followers_graph.serializeToStream(abstractSerializedData);
            this.mute_graph.serializeToStream(abstractSerializedData);
            this.top_hours_graph.serializeToStream(abstractSerializedData);
            this.interactions_graph.serializeToStream(abstractSerializedData);
            this.iv_interactions_graph.serializeToStream(abstractSerializedData);
            this.views_by_source_graph.serializeToStream(abstractSerializedData);
            this.new_followers_by_source_graph.serializeToStream(abstractSerializedData);
            this.languages_graph.serializeToStream(abstractSerializedData);
            this.reactions_by_emotion_graph.serializeToStream(abstractSerializedData);
            this.story_interactions_graph.serializeToStream(abstractSerializedData);
            this.story_reactions_by_emotion_graph.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.recent_posts_interactions.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.recent_posts_interactions.get(i).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_getBroadcastRevenueStats extends TLObject {
        public static final int constructor = -142021095;
        public boolean dark;
        public int flags;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_broadcastRevenueStats.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-142021095);
            int i = this.dark ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.peer.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_getBroadcastRevenueTransactions extends TLObject {
        public static final int constructor = 1889078125;
        public int limit;
        public int offset;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_broadcastRevenueTransactions.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1889078125);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.offset);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class TL_getBroadcastRevenueWithdrawalUrl extends TLObject {
        public static final int constructor = -1644889427;
        public TLRPC.InputCheckPasswordSRP password;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_broadcastRevenueWithdrawalUrl.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1644889427);
            this.peer.serializeToStream(abstractSerializedData);
            this.password.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_getBroadcastStats extends TLObject {
        public static final int constructor = -1421720550;
        public TLRPC.InputChannel channel;
        public boolean dark;
        public int flags;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_broadcastStats.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1421720550);
            int i = this.dark ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.channel.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_getMegagroupStats extends TLObject {
        public static final int constructor = -589330937;
        public TLRPC.InputChannel channel;
        public boolean dark;
        public int flags;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_megagroupStats.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-589330937);
            int i = this.dark ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.channel.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_getMessagePublicForwards extends TLObject {
        public static final int constructor = 1595212100;
        public TLRPC.InputChannel channel;
        public int limit;
        public int msg_id;
        public String offset;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_publicForwards.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1595212100);
            this.channel.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.msg_id);
            abstractSerializedData.writeString(this.offset);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class TL_getMessageStats extends TLObject {
        public static final int constructor = -1226791947;
        public TLRPC.InputChannel channel;
        public boolean dark;
        public int flags;
        public int msg_id;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_messageStats.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1226791947);
            int i = this.dark ? this.flags | 1 : this.flags & (-2);
            this.flags = i;
            abstractSerializedData.writeInt32(i);
            this.channel.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.msg_id);
        }
    }

    public static class TL_getStoryPublicForwards extends TLObject {
        public static final int constructor = -1505526026;
        public int id;
        public int limit;
        public String offset;
        public TLRPC.InputPeer peer;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return TL_publicForwards.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1505526026);
            this.peer.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(this.id);
            abstractSerializedData.writeString(this.offset);
            abstractSerializedData.writeInt32(this.limit);
        }
    }

    public static class TL_loadAsyncGraph extends TLObject {
        public static final int constructor = 1646092192;
        public int flags;
        public String token;
        public long x;

        @Override
        public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            return StatsGraph.TLdeserialize(abstractSerializedData, i, z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1646092192);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeString(this.token);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeInt64(this.x);
            }
        }
    }

    public static class TL_megagroupStats extends TLObject {
        public static final int constructor = -276825834;
        public StatsGraph actions_graph;
        public StatsGraph growth_graph;
        public StatsGraph languages_graph;
        public TL_statsAbsValueAndPrev members;
        public StatsGraph members_graph;
        public TL_statsAbsValueAndPrev messages;
        public StatsGraph messages_graph;
        public StatsGraph new_members_by_source_graph;
        public TL_statsDateRangeDays period;
        public TL_statsAbsValueAndPrev posters;
        public StatsGraph top_hours_graph;
        public TL_statsAbsValueAndPrev viewers;
        public StatsGraph weekdays_graph;
        public ArrayList<TL_statsGroupTopPoster> top_posters = new ArrayList<>();
        public ArrayList<TL_statsGroupTopAdmin> top_admins = new ArrayList<>();
        public ArrayList<TL_statsGroupTopInviter> top_inviters = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_megagroupStats TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-276825834 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stats_megagroupStats", Integer.valueOf(i)));
                }
                return null;
            }
            TL_megagroupStats tL_megagroupStats = new TL_megagroupStats();
            tL_megagroupStats.readParams(abstractSerializedData, z);
            return tL_megagroupStats;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.period = TL_statsDateRangeDays.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.members = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.messages = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.viewers = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.posters = TL_statsAbsValueAndPrev.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.growth_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.members_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.new_members_by_source_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.languages_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.messages_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.actions_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.top_hours_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.weekdays_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            int readInt32 = abstractSerializedData.readInt32(z);
            if (readInt32 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                }
                return;
            }
            int readInt322 = abstractSerializedData.readInt32(z);
            for (int i = 0; i < readInt322; i++) {
                TL_statsGroupTopPoster TLdeserialize = TL_statsGroupTopPoster.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.top_posters.add(TLdeserialize);
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
                TL_statsGroupTopAdmin TLdeserialize2 = TL_statsGroupTopAdmin.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.top_admins.add(TLdeserialize2);
            }
            int readInt325 = abstractSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = abstractSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TL_statsGroupTopInviter TLdeserialize3 = TL_statsGroupTopInviter.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.top_inviters.add(TLdeserialize3);
            }
            int readInt327 = abstractSerializedData.readInt32(z);
            if (readInt327 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt327)));
                }
                return;
            }
            int readInt328 = abstractSerializedData.readInt32(z);
            for (int i4 = 0; i4 < readInt328; i4++) {
                TLRPC.User TLdeserialize4 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize4 == null) {
                    return;
                }
                this.users.add(TLdeserialize4);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-276825834);
            this.period.serializeToStream(abstractSerializedData);
            this.members.serializeToStream(abstractSerializedData);
            this.messages.serializeToStream(abstractSerializedData);
            this.viewers.serializeToStream(abstractSerializedData);
            this.posters.serializeToStream(abstractSerializedData);
            this.growth_graph.serializeToStream(abstractSerializedData);
            this.members_graph.serializeToStream(abstractSerializedData);
            this.new_members_by_source_graph.serializeToStream(abstractSerializedData);
            this.languages_graph.serializeToStream(abstractSerializedData);
            this.messages_graph.serializeToStream(abstractSerializedData);
            this.actions_graph.serializeToStream(abstractSerializedData);
            this.top_hours_graph.serializeToStream(abstractSerializedData);
            this.weekdays_graph.serializeToStream(abstractSerializedData);
            abstractSerializedData.writeInt32(481674261);
            int size = this.top_posters.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.top_posters.get(i).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.top_admins.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.top_admins.get(i2).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size3 = this.top_inviters.size();
            abstractSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.top_inviters.get(i3).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size4 = this.users.size();
            abstractSerializedData.writeInt32(size4);
            for (int i4 = 0; i4 < size4; i4++) {
                this.users.get(i4).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_messageStats extends TLObject {
        public static final int constructor = 2145983508;
        public StatsGraph reactions_by_emotion_graph;
        public StatsGraph views_graph;

        public static TL_messageStats TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (2145983508 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stats_messageStats", Integer.valueOf(i)));
                }
                return null;
            }
            TL_messageStats tL_messageStats = new TL_messageStats();
            tL_messageStats.readParams(abstractSerializedData, z);
            return tL_messageStats;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.views_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.reactions_by_emotion_graph = StatsGraph.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(2145983508);
            this.views_graph.serializeToStream(abstractSerializedData);
            this.reactions_by_emotion_graph.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_postInteractionCountersMessage extends PostInteractionCounters {
        public static final int constructor = -419066241;
        public int forwards;
        public int msg_id;
        public int reactions;
        public int views;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.msg_id = abstractSerializedData.readInt32(z);
            this.views = abstractSerializedData.readInt32(z);
            this.forwards = abstractSerializedData.readInt32(z);
            this.reactions = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-419066241);
            abstractSerializedData.writeInt32(this.msg_id);
            abstractSerializedData.writeInt32(this.views);
            abstractSerializedData.writeInt32(this.forwards);
            abstractSerializedData.writeInt32(this.reactions);
        }
    }

    public static class TL_postInteractionCountersStory extends PostInteractionCounters {
        public static final int constructor = -1974989273;
        public int forwards;
        public int reactions;
        public int story_id;
        public int views;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.story_id = abstractSerializedData.readInt32(z);
            this.views = abstractSerializedData.readInt32(z);
            this.forwards = abstractSerializedData.readInt32(z);
            this.reactions = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1974989273);
            abstractSerializedData.writeInt32(this.story_id);
            abstractSerializedData.writeInt32(this.views);
            abstractSerializedData.writeInt32(this.forwards);
            abstractSerializedData.writeInt32(this.reactions);
        }
    }

    public static class TL_publicForwardMessage extends PublicForward {
        public static final int constructor = 32685898;
        public TLRPC.Message message;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.message = TLRPC.Message.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(32685898);
            this.message.serializeToStream(abstractSerializedData);
        }
    }

    public static class TL_publicForwards extends TLObject {
        public static final int constructor = -1828487648;
        public int count;
        public int flags;
        public String next_offset;
        public ArrayList<PublicForward> forwards = new ArrayList<>();
        public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        public ArrayList<TLRPC.User> users = new ArrayList<>();

        public static TL_publicForwards TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-1828487648 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_stats_publicForwards", Integer.valueOf(i)));
                }
                return null;
            }
            TL_publicForwards tL_publicForwards = new TL_publicForwards();
            tL_publicForwards.readParams(abstractSerializedData, z);
            return tL_publicForwards;
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
                PublicForward TLdeserialize = PublicForward.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize == null) {
                    return;
                }
                this.forwards.add(TLdeserialize);
            }
            if ((this.flags & 1) != 0) {
                this.next_offset = abstractSerializedData.readString(z);
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
                TLRPC.Chat TLdeserialize2 = TLRPC.Chat.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize2 == null) {
                    return;
                }
                this.chats.add(TLdeserialize2);
            }
            int readInt325 = abstractSerializedData.readInt32(z);
            if (readInt325 != 481674261) {
                if (z) {
                    throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                }
                return;
            }
            int readInt326 = abstractSerializedData.readInt32(z);
            for (int i3 = 0; i3 < readInt326; i3++) {
                TLRPC.User TLdeserialize3 = TLRPC.User.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
                if (TLdeserialize3 == null) {
                    return;
                }
                this.users.add(TLdeserialize3);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1828487648);
            abstractSerializedData.writeInt32(this.flags);
            abstractSerializedData.writeInt32(this.count);
            abstractSerializedData.writeInt32(481674261);
            int size = this.forwards.size();
            abstractSerializedData.writeInt32(size);
            for (int i = 0; i < size; i++) {
                this.forwards.get(i).serializeToStream(abstractSerializedData);
            }
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.next_offset);
            }
            abstractSerializedData.writeInt32(481674261);
            int size2 = this.chats.size();
            abstractSerializedData.writeInt32(size2);
            for (int i2 = 0; i2 < size2; i2++) {
                this.chats.get(i2).serializeToStream(abstractSerializedData);
            }
            abstractSerializedData.writeInt32(481674261);
            int size3 = this.users.size();
            abstractSerializedData.writeInt32(size3);
            for (int i3 = 0; i3 < size3; i3++) {
                this.users.get(i3).serializeToStream(abstractSerializedData);
            }
        }
    }

    public static class TL_statsAbsValueAndPrev extends TLObject {
        public static final int constructor = -884757282;
        public double current;
        public double previous;

        public static TL_statsAbsValueAndPrev TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-884757282 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_statsAbsValueAndPrev", Integer.valueOf(i)));
                }
                return null;
            }
            TL_statsAbsValueAndPrev tL_statsAbsValueAndPrev = new TL_statsAbsValueAndPrev();
            tL_statsAbsValueAndPrev.readParams(abstractSerializedData, z);
            return tL_statsAbsValueAndPrev;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.current = abstractSerializedData.readDouble(z);
            this.previous = abstractSerializedData.readDouble(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-884757282);
            abstractSerializedData.writeDouble(this.current);
            abstractSerializedData.writeDouble(this.previous);
        }
    }

    public static class TL_statsDateRangeDays extends TLObject {
        public static final int constructor = -1237848657;
        public int max_date;
        public int min_date;

        public static TL_statsDateRangeDays TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-1237848657 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_statsDateRangeDays", Integer.valueOf(i)));
                }
                return null;
            }
            TL_statsDateRangeDays tL_statsDateRangeDays = new TL_statsDateRangeDays();
            tL_statsDateRangeDays.readParams(abstractSerializedData, z);
            return tL_statsDateRangeDays;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.min_date = abstractSerializedData.readInt32(z);
            this.max_date = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1237848657);
            abstractSerializedData.writeInt32(this.min_date);
            abstractSerializedData.writeInt32(this.max_date);
        }
    }

    public static class TL_statsGraph extends StatsGraph {
        public static final int constructor = -1901828938;
        public int flags;
        public TLRPC.TL_dataJSON json;
        public String zoom_token;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.flags = abstractSerializedData.readInt32(z);
            this.json = TLRPC.TL_dataJSON.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            if ((this.flags & 1) != 0) {
                this.zoom_token = abstractSerializedData.readString(z);
            }
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1901828938);
            abstractSerializedData.writeInt32(this.flags);
            this.json.serializeToStream(abstractSerializedData);
            if ((this.flags & 1) != 0) {
                abstractSerializedData.writeString(this.zoom_token);
            }
        }
    }

    public static class TL_statsGraphAsync extends StatsGraph {
        public static final int constructor = 1244130093;
        public String token;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.token = abstractSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1244130093);
            abstractSerializedData.writeString(this.token);
        }
    }

    public static class TL_statsGraphError extends StatsGraph {
        public static final int constructor = -1092839390;
        public String error;

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.error = abstractSerializedData.readString(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1092839390);
            abstractSerializedData.writeString(this.error);
        }
    }

    public static class TL_statsGroupTopAdmin extends TLObject {
        public static final int constructor = -682079097;
        public int banned;
        public int deleted;
        public int kicked;
        public long user_id;

        public static TL_statsGroupTopAdmin TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-682079097 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_statsGroupTopAdmin", Integer.valueOf(i)));
                }
                return null;
            }
            TL_statsGroupTopAdmin tL_statsGroupTopAdmin = new TL_statsGroupTopAdmin();
            tL_statsGroupTopAdmin.readParams(abstractSerializedData, z);
            return tL_statsGroupTopAdmin;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.user_id = abstractSerializedData.readInt64(z);
            this.deleted = abstractSerializedData.readInt32(z);
            this.kicked = abstractSerializedData.readInt32(z);
            this.banned = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-682079097);
            abstractSerializedData.writeInt64(this.user_id);
            abstractSerializedData.writeInt32(this.deleted);
            abstractSerializedData.writeInt32(this.kicked);
            abstractSerializedData.writeInt32(this.banned);
        }
    }

    public static class TL_statsGroupTopInviter extends TLObject {
        public static final int constructor = 1398765469;
        public int invitations;
        public long user_id;

        public static TL_statsGroupTopInviter TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (1398765469 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_statsGroupTopInviter", Integer.valueOf(i)));
                }
                return null;
            }
            TL_statsGroupTopInviter tL_statsGroupTopInviter = new TL_statsGroupTopInviter();
            tL_statsGroupTopInviter.readParams(abstractSerializedData, z);
            return tL_statsGroupTopInviter;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.user_id = abstractSerializedData.readInt64(z);
            this.invitations = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(1398765469);
            abstractSerializedData.writeInt64(this.user_id);
            abstractSerializedData.writeInt32(this.invitations);
        }
    }

    public static class TL_statsGroupTopPoster extends TLObject {
        public static final int constructor = -1660637285;
        public int avg_chars;
        public int messages;
        public long user_id;

        public static TL_statsGroupTopPoster TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-1660637285 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_statsGroupTopPoster", Integer.valueOf(i)));
                }
                return null;
            }
            TL_statsGroupTopPoster tL_statsGroupTopPoster = new TL_statsGroupTopPoster();
            tL_statsGroupTopPoster.readParams(abstractSerializedData, z);
            return tL_statsGroupTopPoster;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.user_id = abstractSerializedData.readInt64(z);
            this.messages = abstractSerializedData.readInt32(z);
            this.avg_chars = abstractSerializedData.readInt32(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-1660637285);
            abstractSerializedData.writeInt64(this.user_id);
            abstractSerializedData.writeInt32(this.messages);
            abstractSerializedData.writeInt32(this.avg_chars);
        }
    }

    public static class TL_statsPercentValue extends TLObject {
        public static final int constructor = -875679776;
        public double part;
        public double total;

        public static TL_statsPercentValue TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
            if (-875679776 != i) {
                if (z) {
                    throw new RuntimeException(String.format("can't parse magic %x in TL_statsPercentValue", Integer.valueOf(i)));
                }
                return null;
            }
            TL_statsPercentValue tL_statsPercentValue = new TL_statsPercentValue();
            tL_statsPercentValue.readParams(abstractSerializedData, z);
            return tL_statsPercentValue;
        }

        @Override
        public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
            this.part = abstractSerializedData.readDouble(z);
            this.total = abstractSerializedData.readDouble(z);
        }

        @Override
        public void serializeToStream(AbstractSerializedData abstractSerializedData) {
            abstractSerializedData.writeInt32(-875679776);
            abstractSerializedData.writeDouble(this.part);
            abstractSerializedData.writeDouble(this.total);
        }
    }
}

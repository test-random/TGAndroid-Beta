package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;

public class DatabaseMigrationHelper {
    public static int migrate(MessagesStorage messagesStorage, int i) {
        SQLiteDatabase sQLiteDatabase;
        MessagesStorage messagesStorage2;
        SQLiteCursor sQLiteCursor;
        SQLiteCursor sQLiteCursor2;
        SQLiteDatabase sQLiteDatabase2;
        int i2;
        NativeByteBuffer nativeByteBuffer;
        int i3;
        int i4;
        int i5;
        SQLiteCursor sQLiteCursor3;
        SQLiteCursor sQLiteCursor4;
        SQLiteCursor sQLiteCursor5;
        SQLiteCursor sQLiteCursor6;
        SQLiteCursor sQLiteCursor7;
        SQLiteCursor sQLiteCursor8;
        SQLiteDatabase database = messagesStorage.getDatabase();
        int i6 = 4;
        int i7 = i;
        if (i7 < 4) {
            database.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
            database.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
            database.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
            database.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
            database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
            messagesStorage.fixNotificationSettings();
            database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
            i7 = 4;
        }
        int i8 = 6;
        int i9 = 2;
        int i10 = 1;
        int i11 = 0;
        if (i7 == 4) {
            database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
            database.beginTransaction();
            SQLiteCursor queryFinalized = database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast = database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            if (queryFinalized.next()) {
                int intValue = queryFinalized.intValue(0);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(1);
                if (byteBufferValue != null) {
                    int limit = byteBufferValue.limit();
                    for (int i12 = 0; i12 < limit / 4; i12++) {
                        executeFast.requery();
                        executeFast.bindInteger(1, byteBufferValue.readInt32(false));
                        executeFast.bindInteger(2, intValue);
                        executeFast.step();
                    }
                    byteBufferValue.reuse();
                }
            }
            executeFast.dispose();
            queryFinalized.dispose();
            database.commitTransaction();
            database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
            database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
            i7 = 6;
        }
        if (i7 == 6) {
            database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
            i7 = 7;
        }
        if (i7 == 7 || i7 == 8 || i7 == 9) {
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
            i7 = 10;
        }
        if (i7 == 10) {
            database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
            i7 = 11;
        }
        if (i7 == 11 || i7 == 12) {
            database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
            database.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
            database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 13").stepThis().dispose();
            i7 = 13;
        }
        if (i7 == 13) {
            database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
            i7 = 14;
        }
        if (i7 == 14) {
            database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
            i7 = 15;
        }
        if (i7 == 15) {
            database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
            i7 = 16;
        }
        if (i7 == 16) {
            database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
            i7 = 17;
        }
        if (i7 == 17) {
            database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
            i7 = 18;
        }
        if (i7 == 18) {
            database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
            i7 = 19;
        }
        if (i7 == 19) {
            database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
            i7 = 20;
        }
        if (i7 == 20) {
            database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
            i7 = 21;
        }
        if (i7 == 21) {
            database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            SQLiteCursor queryFinalized2 = database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
            SQLitePreparedStatement executeFast2 = database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
            while (queryFinalized2.next()) {
                long intValue2 = queryFinalized2.intValue(0);
                NativeByteBuffer byteBufferValue2 = queryFinalized2.byteBufferValue(1);
                if (byteBufferValue2 != null) {
                    TLRPC.ChatParticipants TLdeserialize = TLRPC.ChatParticipants.TLdeserialize(byteBufferValue2, byteBufferValue2.readInt32(false), false);
                    byteBufferValue2.reuse();
                    if (TLdeserialize != null) {
                        TLRPC.TL_chatFull tL_chatFull = new TLRPC.TL_chatFull();
                        tL_chatFull.id = intValue2;
                        tL_chatFull.chat_photo = new TLRPC.TL_photoEmpty();
                        tL_chatFull.notify_settings = new TLRPC.TL_peerNotifySettingsEmpty_layer77();
                        tL_chatFull.exported_invite = null;
                        tL_chatFull.participants = TLdeserialize;
                        NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(tL_chatFull.getObjectSize());
                        tL_chatFull.serializeToStream(nativeByteBuffer2);
                        executeFast2.requery();
                        executeFast2.bindLong(1, intValue2);
                        executeFast2.bindByteBuffer(2, nativeByteBuffer2);
                        executeFast2.step();
                        nativeByteBuffer2.reuse();
                    }
                }
            }
            executeFast2.dispose();
            queryFinalized2.dispose();
            database.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
            database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
            database.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 22").stepThis().dispose();
            i7 = 22;
        }
        if (i7 == 22) {
            database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
            i7 = 23;
        }
        if (i7 == 23 || i7 == 24) {
            database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
            i7 = 25;
        }
        if (i7 == 25 || i7 == 26) {
            database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
            i7 = 27;
        }
        if (i7 == 27) {
            database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
            i7 = 28;
        }
        if (i7 == 28 || i7 == 29) {
            database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
            i7 = 30;
        }
        if (i7 == 30) {
            database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
            i7 = 31;
        }
        if (i7 == 31) {
            database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
            i7 = 32;
        }
        if (i7 == 32) {
            database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
            database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
            i7 = 33;
        }
        if (i7 == 33) {
            database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
            i7 = 34;
        }
        if (i7 == 34) {
            database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
            i7 = 35;
        }
        if (i7 == 35) {
            database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
            i7 = 36;
        }
        if (i7 == 36) {
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
            i7 = 37;
        }
        if (i7 == 37) {
            database.executeFast("CREATE TABLE IF NOT EXISTS botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 38").stepThis().dispose();
            i7 = 38;
        }
        if (i7 == 38) {
            database.executeFast("ALTER TABLE dialogs ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 39").stepThis().dispose();
            i7 = 39;
        }
        if (i7 == 39) {
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN admin_id INTEGER default 0").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 40").stepThis().dispose();
            i7 = 40;
        }
        if (i7 == 40) {
            messagesStorage.fixNotificationSettings();
            database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
            i7 = 41;
        }
        if (i7 == 41) {
            database.executeFast("ALTER TABLE messages ADD COLUMN mention INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE user_contacts_v6 ADD COLUMN imported INTEGER default 0").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 42").stepThis().dispose();
            i7 = 42;
        }
        if (i7 == 42) {
            database.executeFast("CREATE TABLE IF NOT EXISTS sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 43").stepThis().dispose();
            i7 = 43;
        }
        if (i7 == 43) {
            database.executeFast("PRAGMA user_version = 44").stepThis().dispose();
            i7 = 44;
        }
        if (i7 == 44) {
            database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 45").stepThis().dispose();
            i7 = 45;
        }
        if (i7 == 45) {
            database.executeFast("ALTER TABLE enc_chats ADD COLUMN mtproto_seq INTEGER default 0").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 46").stepThis().dispose();
            i7 = 46;
        }
        if (i7 == 46) {
            database.executeFast("DELETE FROM botcache WHERE 1").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 47").stepThis().dispose();
            i7 = 47;
        }
        if (i7 == 47) {
            database.executeFast("ALTER TABLE dialogs ADD COLUMN flags INTEGER default 0").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 48").stepThis().dispose();
            i7 = 48;
        }
        if (i7 == 48) {
            database.executeFast("CREATE TABLE IF NOT EXISTS unread_push_messages(uid INTEGER, mid INTEGER, random INTEGER, date INTEGER, data BLOB, fm TEXT, name TEXT, uname TEXT, flags INTEGER, PRIMARY KEY(uid, mid))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_date ON unread_push_messages(date);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS unread_push_messages_idx_random ON unread_push_messages(random);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 49").stepThis().dispose();
            i7 = 49;
        }
        if (i7 == 49) {
            database.executeFast("CREATE TABLE IF NOT EXISTS user_settings(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS user_settings_pinned_idx ON user_settings(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 50").stepThis().dispose();
            i7 = 50;
        }
        if (i7 == 50) {
            database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            database.executeFast("ALTER TABLE sent_files_v2 ADD COLUMN parent TEXT").stepThis().dispose();
            database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            database.executeFast("ALTER TABLE download_queue ADD COLUMN parent TEXT").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 51").stepThis().dispose();
            i7 = 51;
        }
        if (i7 == 51) {
            database.executeFast("ALTER TABLE media_counts_v2 ADD COLUMN old INTEGER").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 52").stepThis().dispose();
            i7 = 52;
        }
        if (i7 == 52) {
            database.executeFast("CREATE TABLE IF NOT EXISTS polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS polls_id ON polls_v2(id);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 53").stepThis().dispose();
            i7 = 53;
        }
        if (i7 == 53) {
            database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN online INTEGER default 0").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 54").stepThis().dispose();
            i7 = 54;
        }
        if (i7 == 54) {
            database.executeFast("DROP TABLE IF EXISTS wallpapers;").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 55").stepThis().dispose();
            i7 = 55;
        }
        if (i7 == 55) {
            database.executeFast("CREATE TABLE IF NOT EXISTS wallpapers2(uid INTEGER PRIMARY KEY, data BLOB, num INTEGER)").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS wallpapers_num ON wallpapers2(num);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 56").stepThis().dispose();
            i7 = 56;
        }
        if (i7 == 56 || i7 == 57) {
            database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_v2(lang TEXT, keyword TEXT, emoji TEXT, PRIMARY KEY(lang, keyword, emoji));").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS emoji_keywords_info_v2(lang TEXT PRIMARY KEY, alias TEXT, version INTEGER);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 58").stepThis().dispose();
            i7 = 58;
        }
        if (i7 == 58) {
            database.executeFast("CREATE INDEX IF NOT EXISTS emoji_keywords_v2_keyword ON emoji_keywords_v2(keyword);").stepThis().dispose();
            database.executeFast("ALTER TABLE emoji_keywords_info_v2 ADD COLUMN date INTEGER default 0").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 59").stepThis().dispose();
            i7 = 59;
        }
        if (i7 == 59) {
            database.executeFast("ALTER TABLE dialogs ADD COLUMN folder_id INTEGER default 0").stepThis().dispose();
            database.executeFast("ALTER TABLE dialogs ADD COLUMN data BLOB default NULL").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON dialogs(folder_id);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 60").stepThis().dispose();
            i7 = 60;
        }
        if (i7 == 60) {
            database.executeFast("DROP TABLE IF EXISTS channel_admins;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS blocked_users;").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 61").stepThis().dispose();
            i7 = 61;
        }
        if (i7 == 61) {
            database.executeFast("DROP INDEX IF EXISTS send_state_idx_messages;").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages2 ON messages(mid, send_state, date);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 62").stepThis().dispose();
            i7 = 62;
        }
        if (i7 == 62) {
            database.executeFast("CREATE TABLE IF NOT EXISTS scheduled_messages(mid INTEGER PRIMARY KEY, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB)").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages ON scheduled_messages(mid, send_state, date);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages ON scheduled_messages(uid, date);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 63").stepThis().dispose();
            i7 = 63;
        }
        if (i7 == 63) {
            database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 64").stepThis().dispose();
            i7 = 64;
        }
        if (i7 == 64) {
            database.executeFast("CREATE TABLE IF NOT EXISTS dialog_filter(id INTEGER PRIMARY KEY, ord INTEGER, unread_count INTEGER, flags INTEGER, title TEXT)").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS dialog_filter_ep(id INTEGER, peer INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 65").stepThis().dispose();
            i7 = 65;
        }
        if (i7 == 65) {
            database.executeFast("CREATE INDEX IF NOT EXISTS flags_idx_dialogs ON dialogs(flags);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 66").stepThis().dispose();
            i7 = 66;
        }
        if (i7 == 66) {
            database.executeFast("CREATE TABLE dialog_filter_pin_v2(id INTEGER, peer INTEGER, pin INTEGER, PRIMARY KEY (id, peer))").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 67").stepThis().dispose();
            i7 = 67;
        }
        if (i7 == 67) {
            database.executeFast("CREATE TABLE IF NOT EXISTS stickers_dice(emoji TEXT PRIMARY KEY, data BLOB, date INTEGER);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 68").stepThis().dispose();
            i7 = 68;
        }
        if (i7 == 68) {
            messagesStorage.executeNoException("ALTER TABLE messages ADD COLUMN forwards INTEGER default 0");
            database.executeFast("PRAGMA user_version = 69").stepThis().dispose();
            i7 = 69;
        }
        if (i7 == 69) {
            messagesStorage.executeNoException("ALTER TABLE messages ADD COLUMN replies_data BLOB default NULL");
            messagesStorage.executeNoException("ALTER TABLE messages ADD COLUMN thread_reply_id INTEGER default 0");
            database.executeFast("PRAGMA user_version = 70").stepThis().dispose();
            i7 = 70;
        }
        if (i7 == 70) {
            database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned_v2(uid INTEGER, mid INTEGER, data BLOB, PRIMARY KEY (uid, mid));").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 71").stepThis().dispose();
            i7 = 71;
        }
        if (i7 == 71) {
            messagesStorage.executeNoException("ALTER TABLE sharing_locations ADD COLUMN proximity INTEGER default 0");
            database.executeFast("PRAGMA user_version = 72").stepThis().dispose();
            i7 = 72;
        }
        if (i7 == 72) {
            database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned_count(uid INTEGER PRIMARY KEY, count INTEGER, end INTEGER);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 73").stepThis().dispose();
            i7 = 73;
        }
        if (i7 == 73) {
            messagesStorage.executeNoException("ALTER TABLE chat_settings_v2 ADD COLUMN inviter INTEGER default 0");
            database.executeFast("PRAGMA user_version = 74").stepThis().dispose();
            i7 = 74;
        }
        if (i7 == 74) {
            database.executeFast("CREATE TABLE IF NOT EXISTS shortcut_widget(id INTEGER, did INTEGER, ord INTEGER, PRIMARY KEY (id, did));").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS shortcut_widget_did ON shortcut_widget(did);").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 75").stepThis().dispose();
            i7 = 75;
        }
        if (i7 == 75) {
            messagesStorage.executeNoException("ALTER TABLE chat_settings_v2 ADD COLUMN links INTEGER default 0");
            database.executeFast("PRAGMA user_version = 76").stepThis().dispose();
            i7 = 76;
        }
        if (i7 == 76) {
            messagesStorage.executeNoException("ALTER TABLE enc_tasks_v2 ADD COLUMN media INTEGER default -1");
            database.executeFast("PRAGMA user_version = 77").stepThis().dispose();
            i7 = 77;
        }
        if (i7 == 77) {
            database.executeFast("DROP TABLE IF EXISTS channel_admins_v2;").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS channel_admins_v3(did INTEGER, uid INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 78").stepThis().dispose();
            i7 = 78;
        }
        if (i7 == 78) {
            database.executeFast("DROP TABLE IF EXISTS bot_info;").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS bot_info_v2(uid INTEGER, dialogId INTEGER, info BLOB, PRIMARY KEY(uid, dialogId))").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 79").stepThis().dispose();
            i7 = 79;
        }
        int i13 = 3;
        if (i7 == 79) {
            database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v3(mid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, media))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v3 ON enc_tasks_v3(date);").stepThis().dispose();
            database.beginTransaction();
            SQLiteCursor queryFinalized3 = database.queryFinalized("SELECT mid, date, media FROM enc_tasks_v2 WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast3 = database.executeFast("REPLACE INTO enc_tasks_v3 VALUES(?, ?, ?)");
            if (queryFinalized3.next()) {
                long longValue = queryFinalized3.longValue(0);
                int intValue3 = queryFinalized3.intValue(1);
                int intValue4 = queryFinalized3.intValue(2);
                executeFast3.requery();
                executeFast3.bindLong(1, longValue);
                executeFast3.bindInteger(2, intValue3);
                executeFast3.bindInteger(3, intValue4);
                executeFast3.step();
            }
            executeFast3.dispose();
            queryFinalized3.dispose();
            database.commitTransaction();
            database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks_v2;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS enc_tasks_v2;").stepThis().dispose();
            database.executeFast("PRAGMA user_version = 80").stepThis().dispose();
            i7 = 80;
        }
        int i14 = 5;
        if (i7 == 80) {
            database.executeFast("CREATE TABLE IF NOT EXISTS scheduled_messages_v2(mid INTEGER, uid INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB, PRIMARY KEY(mid, uid))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, send_state, date);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_idx_scheduled_messages_v2 ON scheduled_messages_v2(uid, date);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid_v2 ON bot_keyboard(mid, uid);").stepThis().dispose();
            database.executeFast("DROP INDEX IF EXISTS bot_keyboard_idx_mid;").stepThis().dispose();
            database.beginTransaction();
            try {
                sQLiteCursor8 = database.queryFinalized("SELECT mid, uid, send_state, date, data, ttl, replydata FROM scheduled_messages_v2 WHERE 1", new Object[0]);
            } catch (Exception e) {
                FileLog.e(e);
                sQLiteCursor8 = null;
            }
            if (sQLiteCursor8 != null) {
                SQLitePreparedStatement executeFast4 = database.executeFast("REPLACE INTO scheduled_messages_v2 VALUES(?, ?, ?, ?, ?, ?, ?)");
                while (sQLiteCursor8.next()) {
                    NativeByteBuffer byteBufferValue3 = sQLiteCursor8.byteBufferValue(i6);
                    if (byteBufferValue3 != null) {
                        int intValue5 = sQLiteCursor8.intValue(i11);
                        long longValue2 = sQLiteCursor8.longValue(1);
                        int intValue6 = sQLiteCursor8.intValue(2);
                        int intValue7 = sQLiteCursor8.intValue(3);
                        int intValue8 = sQLiteCursor8.intValue(i14);
                        NativeByteBuffer byteBufferValue4 = sQLiteCursor8.byteBufferValue(6);
                        executeFast4.requery();
                        executeFast4.bindInteger(1, intValue5);
                        executeFast4.bindLong(2, longValue2);
                        executeFast4.bindInteger(3, intValue6);
                        executeFast4.bindByteBuffer(4, byteBufferValue3);
                        executeFast4.bindInteger(5, intValue7);
                        executeFast4.bindInteger(6, intValue8);
                        if (byteBufferValue4 != null) {
                            executeFast4.bindByteBuffer(7, byteBufferValue4);
                        } else {
                            executeFast4.bindNull(7);
                        }
                        executeFast4.step();
                        if (byteBufferValue4 != null) {
                            byteBufferValue4.reuse();
                        }
                        byteBufferValue3.reuse();
                        i6 = 4;
                        i11 = 0;
                        i14 = 5;
                    }
                }
                sQLiteCursor8.dispose();
                executeFast4.dispose();
            }
            database.executeFast("DROP INDEX IF EXISTS send_state_idx_scheduled_messages;").stepThis().dispose();
            database.executeFast("DROP INDEX IF EXISTS uid_date_idx_scheduled_messages;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS scheduled_messages;").stepThis().dispose();
            database.commitTransaction();
            database.executeFast("PRAGMA user_version = 81").stepThis().dispose();
            i7 = 81;
        }
        if (i7 == 81) {
            database.executeFast("CREATE TABLE IF NOT EXISTS media_v3(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v3 ON media_v3(uid, mid, type, date);").stepThis().dispose();
            database.beginTransaction();
            try {
                sQLiteCursor7 = database.queryFinalized("SELECT mid, uid, date, type, data FROM media_v2 WHERE 1", new Object[0]);
            } catch (Exception e2) {
                FileLog.e(e2);
                sQLiteCursor7 = null;
            }
            if (sQLiteCursor7 != null) {
                SQLitePreparedStatement executeFast5 = database.executeFast("REPLACE INTO media_v3 VALUES(?, ?, ?, ?, ?)");
                while (sQLiteCursor7.next()) {
                    NativeByteBuffer byteBufferValue5 = sQLiteCursor7.byteBufferValue(4);
                    if (byteBufferValue5 != null) {
                        int intValue9 = sQLiteCursor7.intValue(0);
                        long longValue3 = sQLiteCursor7.longValue(1);
                        if (((int) longValue3) == 0) {
                            longValue3 = DialogObject.makeEncryptedDialogId((int) (longValue3 >> 32));
                        }
                        int intValue10 = sQLiteCursor7.intValue(2);
                        int intValue11 = sQLiteCursor7.intValue(3);
                        executeFast5.requery();
                        executeFast5.bindInteger(1, intValue9);
                        executeFast5.bindLong(2, longValue3);
                        executeFast5.bindInteger(3, intValue10);
                        executeFast5.bindInteger(4, intValue11);
                        executeFast5.bindByteBuffer(5, byteBufferValue5);
                        executeFast5.step();
                        byteBufferValue5.reuse();
                    }
                }
                sQLiteCursor7.dispose();
                executeFast5.dispose();
            }
            database.executeFast("DROP INDEX IF EXISTS uid_mid_type_date_idx_media;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS media_v2;").stepThis().dispose();
            database.commitTransaction();
            database.executeFast("PRAGMA user_version = 82").stepThis().dispose();
            i7 = 82;
        }
        if (i7 == 82) {
            database.executeFast("CREATE TABLE IF NOT EXISTS randoms_v2(random_id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (random_id, mid, uid))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms_v2 ON randoms_v2(mid, uid);").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v4(mid INTEGER, uid INTEGER, date INTEGER, media INTEGER, PRIMARY KEY(mid, uid, media))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v4 ON enc_tasks_v4(date);").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS polls_v2(mid INTEGER, uid INTEGER, id INTEGER, PRIMARY KEY (mid, uid));").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS polls_id_v2 ON polls_v2(id);").stepThis().dispose();
            database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending_v2(id INTEGER, mid INTEGER, uid INTEGER, PRIMARY KEY (id, mid, uid));").stepThis().dispose();
            database.beginTransaction();
            try {
                sQLiteCursor3 = database.queryFinalized("SELECT r.random_id, r.mid, m.uid FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e3) {
                FileLog.e(e3);
                sQLiteCursor3 = null;
            }
            if (sQLiteCursor3 != null) {
                SQLitePreparedStatement executeFast6 = database.executeFast("REPLACE INTO randoms_v2 VALUES(?, ?, ?)");
                while (sQLiteCursor3.next()) {
                    long longValue4 = sQLiteCursor3.longValue(0);
                    int intValue12 = sQLiteCursor3.intValue(1);
                    long longValue5 = sQLiteCursor3.longValue(2);
                    if (((int) longValue5) == 0) {
                        longValue5 = DialogObject.makeEncryptedDialogId((int) (longValue5 >> 32));
                    }
                    executeFast6.requery();
                    executeFast6.bindLong(1, longValue4);
                    executeFast6.bindInteger(2, intValue12);
                    executeFast6.bindLong(3, longValue5);
                    executeFast6.step();
                }
                sQLiteCursor3.dispose();
                executeFast6.dispose();
            }
            try {
                sQLiteCursor4 = database.queryFinalized("SELECT p.mid, m.uid, p.id FROM polls as p INNER JOIN messages as m ON p.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e4) {
                FileLog.e(e4);
                sQLiteCursor4 = null;
            }
            if (sQLiteCursor4 != null) {
                SQLitePreparedStatement executeFast7 = database.executeFast("REPLACE INTO polls_v2 VALUES(?, ?, ?)");
                while (sQLiteCursor4.next()) {
                    int intValue13 = sQLiteCursor4.intValue(0);
                    long longValue6 = sQLiteCursor4.longValue(1);
                    long longValue7 = sQLiteCursor4.longValue(2);
                    if (((int) longValue6) == 0) {
                        longValue6 = DialogObject.makeEncryptedDialogId((int) (longValue6 >> 32));
                    }
                    executeFast7.requery();
                    executeFast7.bindInteger(1, intValue13);
                    executeFast7.bindLong(2, longValue6);
                    executeFast7.bindLong(3, longValue7);
                    executeFast7.step();
                }
                sQLiteCursor4.dispose();
                executeFast7.dispose();
            }
            try {
                sQLiteCursor5 = database.queryFinalized("SELECT wp.id, wp.mid, m.uid FROM webpage_pending as wp INNER JOIN messages as m ON wp.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e5) {
                FileLog.e(e5);
                sQLiteCursor5 = null;
            }
            if (sQLiteCursor5 != null) {
                SQLitePreparedStatement executeFast8 = database.executeFast("REPLACE INTO webpage_pending_v2 VALUES(?, ?, ?)");
                while (sQLiteCursor5.next()) {
                    long longValue8 = sQLiteCursor5.longValue(0);
                    int intValue14 = sQLiteCursor5.intValue(1);
                    long longValue9 = sQLiteCursor5.longValue(2);
                    if (((int) longValue9) == 0) {
                        longValue9 = DialogObject.makeEncryptedDialogId((int) (longValue9 >> 32));
                    }
                    executeFast8.requery();
                    executeFast8.bindLong(1, longValue8);
                    executeFast8.bindInteger(2, intValue14);
                    executeFast8.bindLong(3, longValue9);
                    executeFast8.step();
                }
                sQLiteCursor5.dispose();
                executeFast8.dispose();
            }
            try {
                sQLiteCursor6 = database.queryFinalized("SELECT et.mid, m.uid, et.date, et.media FROM enc_tasks_v3 as et INNER JOIN messages as m ON et.mid = m.mid WHERE 1", new Object[0]);
            } catch (Exception e6) {
                FileLog.e(e6);
                sQLiteCursor6 = null;
            }
            if (sQLiteCursor6 != null) {
                SQLitePreparedStatement executeFast9 = database.executeFast("REPLACE INTO enc_tasks_v4 VALUES(?, ?, ?, ?)");
                while (sQLiteCursor6.next()) {
                    int intValue15 = sQLiteCursor6.intValue(0);
                    long longValue10 = sQLiteCursor6.longValue(1);
                    int intValue16 = sQLiteCursor6.intValue(2);
                    int intValue17 = sQLiteCursor6.intValue(3);
                    if (((int) longValue10) == 0) {
                        longValue10 = DialogObject.makeEncryptedDialogId((int) (longValue10 >> 32));
                    }
                    executeFast9.requery();
                    executeFast9.bindInteger(1, intValue15);
                    executeFast9.bindLong(2, longValue10);
                    executeFast9.bindInteger(3, intValue16);
                    executeFast9.bindInteger(4, intValue17);
                    executeFast9.step();
                }
                sQLiteCursor6.dispose();
                executeFast9.dispose();
            }
            database.executeFast("DROP INDEX IF EXISTS mid_idx_randoms;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS randoms;").stepThis().dispose();
            database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks_v3;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS enc_tasks_v3;").stepThis().dispose();
            database.executeFast("DROP INDEX IF EXISTS polls_id;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS polls;").stepThis().dispose();
            database.executeFast("DROP TABLE IF EXISTS webpage_pending;").stepThis().dispose();
            database.commitTransaction();
            database.executeFast("PRAGMA user_version = 83").stepThis().dispose();
            i7 = 83;
        }
        if (i7 == 83) {
            database.executeFast("CREATE TABLE IF NOT EXISTS messages_v2(mid INTEGER, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, PRIMARY KEY(mid, uid))").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_v2 ON messages_v2(uid, mid, read_state, out);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_v2 ON messages_v2(uid, date, mid);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_v2 ON messages_v2(mid, out);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_v2 ON messages_v2(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_v2 ON messages_v2(mid, send_state, date);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_v2 ON messages_v2(uid, mention, read_state);").stepThis().dispose();
            database.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_v2 ON messages_v2(mid, is_channel);").stepThis().dispose();
            database.beginTransaction();
            try {
                sQLiteCursor2 = database.queryFinalized("SELECT mid, uid, read_state, send_state, date, data, out, ttl, media, replydata, imp, mention, forwards, replies_data, thread_reply_id FROM messages WHERE 1", new Object[0]);
            } catch (Exception e7) {
                FileLog.e(e7);
                sQLiteCursor2 = null;
            }
            if (sQLiteCursor2 != null) {
                SQLitePreparedStatement executeFast10 = database.executeFast("REPLACE INTO messages_v2 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                while (sQLiteCursor2.next()) {
                    NativeByteBuffer byteBufferValue6 = sQLiteCursor2.byteBufferValue(5);
                    if (byteBufferValue6 != null) {
                        long intValue18 = sQLiteCursor2.intValue(0);
                        long longValue11 = sQLiteCursor2.longValue(i10);
                        if (((int) longValue11) == 0) {
                            longValue11 = DialogObject.makeEncryptedDialogId((int) (longValue11 >> 32));
                        }
                        int intValue19 = sQLiteCursor2.intValue(i9);
                        int intValue20 = sQLiteCursor2.intValue(i13);
                        int intValue21 = sQLiteCursor2.intValue(4);
                        int intValue22 = sQLiteCursor2.intValue(i8);
                        int intValue23 = sQLiteCursor2.intValue(7);
                        int intValue24 = sQLiteCursor2.intValue(8);
                        NativeByteBuffer byteBufferValue7 = sQLiteCursor2.byteBufferValue(9);
                        int intValue25 = sQLiteCursor2.intValue(10);
                        SQLiteDatabase sQLiteDatabase3 = database;
                        int intValue26 = sQLiteCursor2.intValue(11);
                        int intValue27 = sQLiteCursor2.intValue(12);
                        NativeByteBuffer byteBufferValue8 = sQLiteCursor2.byteBufferValue(13);
                        int intValue28 = sQLiteCursor2.intValue(14);
                        SQLiteCursor sQLiteCursor9 = sQLiteCursor2;
                        int i15 = (int) (longValue11 >> 32);
                        if (intValue23 < 0) {
                            TLRPC.Message TLdeserialize2 = TLRPC.Message.TLdeserialize(byteBufferValue6, byteBufferValue6.readInt32(false), false);
                            i3 = intValue22;
                            i4 = i15;
                            if (TLdeserialize2 != null) {
                                i2 = intValue28;
                                TLdeserialize2.readAttachPath(byteBufferValue6, messagesStorage.getUserConfig().clientUserId);
                                if (TLdeserialize2.params == null) {
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    TLdeserialize2.params = hashMap;
                                    StringBuilder sb = new StringBuilder();
                                    nativeByteBuffer = byteBufferValue7;
                                    sb.append("");
                                    sb.append(intValue23);
                                    hashMap.put("fwd_peer", sb.toString());
                                } else {
                                    nativeByteBuffer = byteBufferValue7;
                                }
                                byteBufferValue6.reuse();
                                byteBufferValue6 = new NativeByteBuffer(TLdeserialize2.getObjectSize());
                                TLdeserialize2.serializeToStream(byteBufferValue6);
                            } else {
                                i2 = intValue28;
                                nativeByteBuffer = byteBufferValue7;
                            }
                            i5 = 0;
                        } else {
                            i2 = intValue28;
                            nativeByteBuffer = byteBufferValue7;
                            i3 = intValue22;
                            i4 = i15;
                            i5 = intValue23;
                        }
                        executeFast10.requery();
                        executeFast10.bindInteger(1, (int) intValue18);
                        executeFast10.bindLong(2, longValue11);
                        executeFast10.bindInteger(3, intValue19);
                        executeFast10.bindInteger(4, intValue20);
                        executeFast10.bindInteger(5, intValue21);
                        executeFast10.bindByteBuffer(6, byteBufferValue6);
                        executeFast10.bindInteger(7, i3);
                        executeFast10.bindInteger(8, i5);
                        executeFast10.bindInteger(9, intValue24);
                        NativeByteBuffer nativeByteBuffer3 = nativeByteBuffer;
                        if (nativeByteBuffer != null) {
                            executeFast10.bindByteBuffer(10, nativeByteBuffer3);
                        } else {
                            executeFast10.bindNull(10);
                        }
                        executeFast10.bindInteger(11, intValue25);
                        executeFast10.bindInteger(12, intValue26);
                        executeFast10.bindInteger(13, intValue27);
                        if (byteBufferValue8 != null) {
                            executeFast10.bindByteBuffer(14, byteBufferValue8);
                        } else {
                            executeFast10.bindNull(14);
                        }
                        executeFast10.bindInteger(15, i2);
                        executeFast10.bindInteger(16, i4 > 0 ? 1 : 0);
                        executeFast10.step();
                        if (nativeByteBuffer3 != null) {
                            nativeByteBuffer3.reuse();
                        }
                        if (byteBufferValue8 != null) {
                            byteBufferValue8.reuse();
                        }
                        byteBufferValue6.reuse();
                        database = sQLiteDatabase3;
                        sQLiteCursor2 = sQLiteCursor9;
                        i8 = 6;
                        i9 = 2;
                        i10 = 1;
                        i13 = 3;
                    }
                }
                sQLiteDatabase2 = database;
                sQLiteCursor2.dispose();
                executeFast10.dispose();
            } else {
                sQLiteDatabase2 = database;
            }
            int i16 = 0;
            sQLiteDatabase = sQLiteDatabase2;
            SQLiteCursor queryFinalized4 = sQLiteDatabase.queryFinalized("SELECT did, last_mid, last_mid_i FROM dialogs WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast11 = sQLiteDatabase.executeFast("UPDATE dialogs SET last_mid = ?, last_mid_i = ? WHERE did = ?");
            ArrayList arrayList = null;
            ArrayList arrayList2 = null;
            while (queryFinalized4.next()) {
                long longValue12 = queryFinalized4.longValue(i16);
                int i17 = (int) longValue12;
                int i18 = (int) (longValue12 >> 32);
                if (i17 == 0) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(i18));
                } else if (i18 == 2) {
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(Integer.valueOf(i17));
                }
                executeFast11.requery();
                executeFast11.bindInteger(1, queryFinalized4.intValue(1));
                executeFast11.bindInteger(2, queryFinalized4.intValue(2));
                executeFast11.bindLong(3, longValue12);
                executeFast11.step();
                i16 = 0;
            }
            executeFast11.dispose();
            queryFinalized4.dispose();
            int i19 = 0;
            SQLiteCursor queryFinalized5 = sQLiteDatabase.queryFinalized("SELECT uid, mid FROM unread_push_messages WHERE 1", new Object[0]);
            SQLitePreparedStatement executeFast12 = sQLiteDatabase.executeFast("UPDATE unread_push_messages SET mid = ? WHERE uid = ? AND mid = ?");
            while (queryFinalized5.next()) {
                long longValue13 = queryFinalized5.longValue(i19);
                int intValue29 = queryFinalized5.intValue(1);
                executeFast12.requery();
                executeFast12.bindInteger(1, intValue29);
                executeFast12.bindLong(2, longValue13);
                executeFast12.bindInteger(3, intValue29);
                executeFast12.step();
                i19 = 0;
            }
            executeFast12.dispose();
            queryFinalized5.dispose();
            if (arrayList != null) {
                SQLitePreparedStatement executeFast13 = sQLiteDatabase.executeFast("UPDATE dialogs SET did = ? WHERE did = ?");
                SQLitePreparedStatement executeFast14 = sQLiteDatabase.executeFast("UPDATE dialog_filter_pin_v2 SET peer = ? WHERE peer = ?");
                SQLitePreparedStatement executeFast15 = sQLiteDatabase.executeFast("UPDATE dialog_filter_ep SET peer = ? WHERE peer = ?");
                int size = arrayList.size();
                for (int i20 = 0; i20 < size; i20++) {
                    long intValue30 = ((Integer) arrayList.get(i20)).intValue();
                    long makeEncryptedDialogId = DialogObject.makeEncryptedDialogId(intValue30);
                    long j = intValue30 << 32;
                    executeFast13.requery();
                    executeFast13.bindLong(1, makeEncryptedDialogId);
                    executeFast13.bindLong(2, j);
                    executeFast13.step();
                    executeFast14.requery();
                    executeFast14.bindLong(1, makeEncryptedDialogId);
                    executeFast14.bindLong(2, j);
                    executeFast14.step();
                    executeFast15.requery();
                    executeFast15.bindLong(1, makeEncryptedDialogId);
                    executeFast15.bindLong(2, j);
                    executeFast15.step();
                }
                executeFast13.dispose();
                executeFast14.dispose();
                executeFast15.dispose();
            }
            if (arrayList2 != null) {
                SQLitePreparedStatement executeFast16 = sQLiteDatabase.executeFast("UPDATE dialogs SET did = ? WHERE did = ?");
                int size2 = arrayList2.size();
                for (int i21 = 0; i21 < size2; i21++) {
                    int intValue31 = ((Integer) arrayList2.get(i21)).intValue();
                    long makeFolderDialogId = DialogObject.makeFolderDialogId(intValue31);
                    executeFast16.requery();
                    executeFast16.bindLong(1, makeFolderDialogId);
                    executeFast16.bindLong(2, intValue31 | 8589934592L);
                    executeFast16.step();
                }
                executeFast16.dispose();
            }
            sQLiteDatabase.executeFast("DROP INDEX IF EXISTS uid_mid_read_out_idx_messages;").stepThis().dispose();
            sQLiteDatabase.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_messages;").stepThis().dispose();
            sQLiteDatabase.executeFast("DROP INDEX IF EXISTS mid_out_idx_messages;").stepThis().dispose();
            sQLiteDatabase.executeFast("DROP INDEX IF EXISTS task_idx_messages;").stepThis().dispose();
            sQLiteDatabase.executeFast("DROP INDEX IF EXISTS send_state_idx_messages2;").stepThis().dispose();
            sQLiteDatabase.executeFast("DROP INDEX IF EXISTS uid_mention_idx_messages;").stepThis().dispose();
            sQLiteDatabase.executeFast("DROP TABLE IF EXISTS messages;").stepThis().dispose();
            sQLiteDatabase.commitTransaction();
            sQLiteDatabase.executeFast("PRAGMA user_version = 84").stepThis().dispose();
            i7 = 84;
        } else {
            sQLiteDatabase = database;
        }
        if (i7 == 84) {
            sQLiteDatabase.executeFast("CREATE TABLE IF NOT EXISTS media_v4(mid INTEGER, uid INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, type))").stepThis().dispose();
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteCursor = sQLiteDatabase.queryFinalized("SELECT mid, uid, date, type, data FROM media_v3 WHERE 1", new Object[0]);
            } catch (Exception e8) {
                FileLog.e(e8);
                sQLiteCursor = null;
            }
            if (sQLiteCursor != null) {
                SQLitePreparedStatement executeFast17 = sQLiteDatabase.executeFast("REPLACE INTO media_v4 VALUES(?, ?, ?, ?, ?)");
                while (sQLiteCursor.next()) {
                    NativeByteBuffer byteBufferValue9 = sQLiteCursor.byteBufferValue(4);
                    if (byteBufferValue9 != null) {
                        int intValue32 = sQLiteCursor.intValue(0);
                        long longValue14 = sQLiteCursor.longValue(1);
                        if (((int) longValue14) == 0) {
                            longValue14 = DialogObject.makeEncryptedDialogId((int) (longValue14 >> 32));
                        }
                        int intValue33 = sQLiteCursor.intValue(2);
                        int intValue34 = sQLiteCursor.intValue(3);
                        executeFast17.requery();
                        executeFast17.bindInteger(1, intValue32);
                        executeFast17.bindLong(2, longValue14);
                        executeFast17.bindInteger(3, intValue33);
                        executeFast17.bindInteger(4, intValue34);
                        executeFast17.bindByteBuffer(5, byteBufferValue9);
                        executeFast17.step();
                        byteBufferValue9.reuse();
                    }
                }
                sQLiteCursor.dispose();
                executeFast17.dispose();
            }
            sQLiteDatabase.commitTransaction();
            sQLiteDatabase.executeFast("DROP TABLE IF EXISTS media_v3;").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 85").stepThis().dispose();
            i7 = 85;
        }
        if (i7 == 85) {
            messagesStorage2 = messagesStorage;
            messagesStorage2.executeNoException("ALTER TABLE messages_v2 ADD COLUMN reply_to_message_id INTEGER default 0");
            messagesStorage2.executeNoException("ALTER TABLE scheduled_messages_v2 ADD COLUMN reply_to_message_id INTEGER default 0");
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_v2 ON messages_v2(mid, reply_to_message_id);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_scheduled_messages_v2 ON scheduled_messages_v2(mid, reply_to_message_id);").stepThis().dispose();
            messagesStorage2.executeNoException("UPDATE messages_v2 SET replydata = NULL");
            messagesStorage2.executeNoException("UPDATE scheduled_messages_v2 SET replydata = NULL");
            sQLiteDatabase.executeFast("PRAGMA user_version = 86").stepThis().dispose();
            i7 = 86;
        } else {
            messagesStorage2 = messagesStorage;
        }
        if (i7 == 86) {
            sQLiteDatabase.executeFast("CREATE TABLE IF NOT EXISTS reactions(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 87").stepThis().dispose();
            i7 = 87;
        }
        if (i7 == 87) {
            sQLiteDatabase.executeFast("ALTER TABLE dialogs ADD COLUMN unread_reactions INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE reaction_mentions(message_id INTEGER PRIMARY KEY, state INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 88").stepThis().dispose();
            i7 = 88;
        }
        if (i7 == 88 || i7 == 89) {
            sQLiteDatabase.executeFast("DROP TABLE IF EXISTS reaction_mentions;").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE IF NOT EXISTS reaction_mentions(message_id INTEGER, state INTEGER, dialog_id INTEGER, PRIMARY KEY(dialog_id, message_id));").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_did ON reaction_mentions(dialog_id);").stepThis().dispose();
            sQLiteDatabase.executeFast("DROP INDEX IF EXISTS uid_mid_type_date_idx_media_v3").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_v4 ON media_v4(uid, mid, type, date);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 90").stepThis().dispose();
            i7 = 90;
        }
        if (i7 == 90 || i7 == 91) {
            sQLiteDatabase.executeFast("DROP TABLE IF EXISTS downloading_documents;").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE downloading_documents(data BLOB, hash INTEGER, id INTEGER, state INTEGER, date INTEGER, PRIMARY KEY(hash, id));").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 92").stepThis().dispose();
            i7 = 92;
        }
        if (i7 == 92) {
            sQLiteDatabase.executeFast("CREATE TABLE IF NOT EXISTS attach_menu_bots(data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 93").stepThis().dispose();
            i7 = 95;
        }
        if (i7 == 95 || i7 == 93) {
            messagesStorage2.executeNoException("ALTER TABLE messages_v2 ADD COLUMN custom_params BLOB default NULL");
            sQLiteDatabase.executeFast("PRAGMA user_version = 96").stepThis().dispose();
            i7 = 96;
        }
        if (i7 == 96) {
            sQLiteDatabase.executeFast("CREATE TABLE IF NOT EXISTS premium_promo(data BLOB, date INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("UPDATE stickers_v2 SET date = 0");
            sQLiteDatabase.executeFast("PRAGMA user_version = 97").stepThis().dispose();
            i7 = 97;
        }
        if (i7 == 97) {
            sQLiteDatabase.executeFast("DROP TABLE IF EXISTS stickers_featured;").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash INTEGER, premium INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 98").stepThis().dispose();
            i7 = 98;
        }
        if (i7 == 98) {
            sQLiteDatabase.executeFast("CREATE TABLE animated_emoji(document_id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 99").stepThis().dispose();
            i7 = 99;
        }
        if (i7 == 99) {
            sQLiteDatabase.executeFast("ALTER TABLE stickers_featured ADD COLUMN emoji INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 100").stepThis().dispose();
            i7 = 100;
        }
        if (i7 == 100) {
            sQLiteDatabase.executeFast("CREATE TABLE emoji_statuses(data BLOB, type INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 101").stepThis().dispose();
            i7 = 101;
        }
        if (i7 == 101) {
            sQLiteDatabase.executeFast("ALTER TABLE messages_v2 ADD COLUMN group_id INTEGER default NULL").stepThis().dispose();
            sQLiteDatabase.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_group INTEGER default NULL").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_groupid_messages_v2 ON messages_v2(uid, mid, group_id);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 102").stepThis().dispose();
            i7 = 102;
        }
        if (i7 == 102) {
            sQLiteDatabase.executeFast("CREATE TABLE messages_holes_topics(uid INTEGER, topic_id INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, topic_id, start));").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes_topics(uid, topic_id, end);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE messages_topics(mid INTEGER, uid INTEGER, topic_id INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER, forwards INTEGER, replies_data BLOB, thread_reply_id INTEGER, is_channel INTEGER, reply_to_message_id INTEGER, custom_params BLOB, PRIMARY KEY(mid, topic_id, uid))").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_topics ON messages_topics(uid, mid, read_state, out);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages_topics ON messages_topics(uid, date, mid);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages_topics ON messages_topics(mid, out);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages_topics ON messages_topics(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages_topics ON messages_topics(mid, send_state, date);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_topics ON messages_topics(uid, mention, read_state);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS is_channel_idx_messages_topics ON messages_topics(mid, is_channel);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_messages_topics ON messages_topics(mid, reply_to_message_id);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS mid_uid_messages_topics ON messages_topics(mid, uid);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS mid_uid_topic_id_messages_topics ON messages_topics(mid, topic_id, uid);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE media_topics(mid INTEGER, uid INTEGER, topic_id INTEGER, date INTEGER, type INTEGER, data BLOB, PRIMARY KEY(mid, uid, topic_id, type))").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media_topics ON media_topics(uid, topic_id, mid, type, date);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE media_holes_topics(uid INTEGER, topic_id INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, topic_id, type, start));").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_topics ON media_holes_topics(uid, topic_id, type, end);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE topics(did INTEGER, topic_id INTEGER, data BLOB, top_message INTEGER, topic_message BLOB, unread_count INTEGER, max_read_id INTEGER, unread_mentions INTEGER, unread_reactions INTEGER, PRIMARY KEY(did, topic_id));").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS did_top_message_topics ON topics(did, top_message);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 103").stepThis().dispose();
            i7 = 103;
        }
        if (i7 == 103) {
            sQLiteDatabase.executeFast("CREATE TABLE IF NOT EXISTS media_counts_topics(uid INTEGER, topic_id INTEGER, type INTEGER, count INTEGER, old INTEGER, PRIMARY KEY(uid, topic_id, type))").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE IF NOT EXISTS reaction_mentions_topics(message_id INTEGER, state INTEGER, dialog_id INTEGER, topic_id INTEGER, PRIMARY KEY(message_id, dialog_id, topic_id))").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reaction_mentions_topics_did ON reaction_mentions_topics(dialog_id, topic_id);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 104").stepThis().dispose();
            i7 = 104;
        }
        if (i7 == 104) {
            sQLiteDatabase.executeFast("ALTER TABLE topics ADD COLUMN read_outbox INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 105").stepThis().dispose();
            i7 = 105;
        }
        if (i7 == 105) {
            sQLiteDatabase.executeFast("ALTER TABLE topics ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 106").stepThis().dispose();
            i7 = 106;
        }
        if (i7 == 106) {
            sQLiteDatabase.executeFast("DROP INDEX IF EXISTS uid_mid_read_out_idx_messages_topics").stepThis().dispose();
            sQLiteDatabase.executeFast("DROP INDEX IF EXISTS uid_mention_idx_messages_topics").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_read_out_idx_messages_topics ON messages_topics(uid, topic_id, mid, read_state, out);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages_topics ON messages_topics(uid, topic_id, mention, read_state);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_topic_id_messages_topics ON messages_topics(uid, topic_id);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_topic_id_date_mid_messages_topics ON messages_topics(uid, topic_id, date, mid);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS uid_topic_id_mid_messages_topics ON messages_topics(uid, topic_id, mid);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS did_topics ON topics(did);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 107").stepThis().dispose();
            i7 = 107;
        }
        if (i7 == 107) {
            sQLiteDatabase.executeFast("ALTER TABLE topics ADD COLUMN total_messages_count INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 108").stepThis().dispose();
            i7 = 108;
        }
        if (i7 == 108) {
            sQLiteDatabase.executeFast("ALTER TABLE topics ADD COLUMN hidden INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 109").stepThis().dispose();
            i7 = 109;
        }
        if (i7 == 109) {
            sQLiteDatabase.executeFast("ALTER TABLE dialogs ADD COLUMN ttl_period INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 110").stepThis().dispose();
            i7 = 110;
        }
        if (i7 == 110) {
            sQLiteDatabase.executeFast("CREATE TABLE stickersets(id INTEGER PRIMATE KEY, data BLOB, hash INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 111").stepThis().dispose();
            i7 = 111;
        }
        if (i7 == 111) {
            sQLiteDatabase.executeFast("CREATE TABLE emoji_groups(type INTEGER PRIMARY KEY, data BLOB)").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 112").stepThis().dispose();
            i7 = 112;
        }
        if (i7 == 112) {
            sQLiteDatabase.executeFast("CREATE TABLE app_config(data BLOB)").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 113").stepThis().dispose();
            i7 = 113;
        }
        if (i7 == 113) {
            messagesStorage.reset();
            sQLiteDatabase.executeFast("PRAGMA user_version = 114").stepThis().dispose();
            i7 = 114;
        }
        if (i7 == 114) {
            sQLiteDatabase.executeFast("CREATE TABLE bot_keyboard_topics(uid INTEGER, tid INTEGER, mid INTEGER, info BLOB, PRIMARY KEY(uid, tid))").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_topics_idx_mid_v2 ON bot_keyboard_topics(mid, uid, tid);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 115").stepThis().dispose();
            i7 = 115;
        }
        if (i7 == 115) {
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS idx_to_reply_messages_v2 ON messages_v2(reply_to_message_id, mid);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS idx_to_reply_scheduled_messages_v2 ON scheduled_messages_v2(reply_to_message_id, mid);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS idx_to_reply_messages_topics ON messages_topics(reply_to_message_id, mid);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 117").stepThis().dispose();
            i7 = 117;
        }
        if (i7 == 116 || i7 == 117 || i7 == 118) {
            sQLiteDatabase.executeFast("DROP TABLE IF EXISTS stories").stepThis().dispose();
            sQLiteDatabase.executeFast("DROP TABLE IF EXISTS stories_counter").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE stories (dialog_id INTEGER, story_id INTEGER, data BLOB, local_path TEXT, local_thumb_path TEXT, PRIMARY KEY (dialog_id, story_id));").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE stories_counter (dialog_id INTEGER PRIMARY KEY, count INTEGER, max_read INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 119").stepThis().dispose();
            messagesStorage.getMessagesController().getStoriesController().cleanup();
            i7 = 119;
        }
        if (i7 == 119) {
            sQLiteDatabase.executeFast("ALTER TABLE messages_v2 ADD COLUMN reply_to_story_id INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("ALTER TABLE messages_topics ADD COLUMN reply_to_story_id INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 120").stepThis().dispose();
            i7 = 120;
        }
        if (i7 == 120) {
            sQLiteDatabase.executeFast("CREATE TABLE profile_stories (dialog_id INTEGER, story_id INTEGER, data BLOB, PRIMARY KEY(dialog_id, story_id));").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE archived_stories (story_id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 121").stepThis().dispose();
            i7 = 121;
        }
        if (i7 == 121) {
            sQLiteDatabase.executeFast("CREATE TABLE story_drafts (id INTEGER PRIMARY KEY, date INTEGER, data BLOB);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 122").stepThis().dispose();
            i7 = 122;
        }
        if (i7 == 122) {
            sQLiteDatabase.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN participants_count INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 123").stepThis().dispose();
            i7 = 123;
        }
        if (i7 == 123) {
            sQLiteDatabase.executeFast("CREATE TABLE story_pushes (uid INTEGER PRIMARY KEY, minId INTEGER, maxId INTEGER, date INTEGER, localName TEXT);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 124").stepThis().dispose();
            i7 = 124;
        }
        if (i7 == 124) {
            sQLiteDatabase.executeFast("DROP TABLE IF EXISTS story_pushes;").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE story_pushes (uid INTEGER, sid INTEGER, date INTEGER, localName TEXT, PRIMARY KEY(uid, sid));").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 125").stepThis().dispose();
            i7 = 125;
        }
        if (i7 == 125) {
            sQLiteDatabase.executeFast("ALTER TABLE story_pushes ADD COLUMN flags INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 126").stepThis().dispose();
            i7 = 126;
        }
        if (i7 == 126) {
            sQLiteDatabase.executeFast("ALTER TABLE story_pushes ADD COLUMN expire_date INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 127").stepThis().dispose();
            i7 = 127;
        }
        if (i7 == 127) {
            sQLiteDatabase.executeFast("ALTER TABLE stories ADD COLUMN custom_params BLOB default NULL").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 128").stepThis().dispose();
            i7 = 128;
        }
        if (i7 == 128) {
            sQLiteDatabase.executeFast("ALTER TABLE story_drafts ADD COLUMN type INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 129").stepThis().dispose();
            i7 = 129;
        }
        if (i7 == 129) {
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS stickers_featured_emoji_index ON stickers_featured(emoji);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 130").stepThis().dispose();
            i7 = 130;
        }
        if (i7 == 130) {
            sQLiteDatabase.executeFast("DROP TABLE archived_stories").stepThis().dispose();
            sQLiteDatabase.executeFast("ALTER TABLE profile_stories ADD COLUMN type INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 131").stepThis().dispose();
            i7 = 131;
        }
        if (i7 == 131) {
            sQLiteDatabase.executeFast("ALTER TABLE stories DROP COLUMN local_path").stepThis().dispose();
            sQLiteDatabase.executeFast("ALTER TABLE stories DROP COLUMN local_thumb_path").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 132").stepThis().dispose();
            i7 = 132;
        }
        if (i7 == 132) {
            sQLiteDatabase.executeFast("CREATE TABLE unconfirmed_auth (data BLOB);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 133").stepThis().dispose();
            i7 = 133;
        }
        if (i7 == 133) {
            sQLiteDatabase.executeFast("ALTER TABLE unread_push_messages ADD COLUMN topicId INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 134").stepThis().dispose();
            i7 = 134;
        }
        if (i7 == 134) {
            sQLiteDatabase.executeFast("DROP TABLE user_photos").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE dialog_photos(uid INTEGER, id INTEGER, num INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE dialog_photos_count(uid INTEGER PRIMARY KEY, count INTEGER)").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 135").stepThis().dispose();
            i7 = 135;
        }
        if (i7 == 135) {
            sQLiteDatabase.executeFast("CREATE TABLE stickersets2(id INTEGER PRIMATE KEY, data BLOB, hash INTEGER, date INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS stickersets2_id_index ON stickersets2(id);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 136").stepThis().dispose();
            i7 = 136;
        }
        if (i7 == 136) {
            sQLiteDatabase.executeFast("CREATE TABLE saved_dialogs(did INTEGER PRIMARY KEY, date INTEGER, last_mid INTEGER, pinned INTEGER, flags INTEGER, folder_id INTEGER, last_mid_group INTEGER, count INTEGER)").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON saved_dialogs(date);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON saved_dialogs(last_mid);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS folder_id_idx_dialogs ON saved_dialogs(folder_id);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS flags_idx_dialogs ON saved_dialogs(flags);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 137").stepThis().dispose();
            i7 = 137;
        }
        if (i7 == 137) {
            sQLiteDatabase.executeFast("ALTER TABLE unread_push_messages ADD COLUMN is_reaction INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 138").stepThis().dispose();
            i7 = 138;
        }
        if (i7 == 138 || i7 == 139 || i7 == 140 || i7 == 141) {
            sQLiteDatabase.executeFast("DROP TABLE IF EXISTS tag_message_id;").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE tag_message_id(mid INTEGER, topic_id INTEGER, tag INTEGER, text TEXT);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS tag_idx_tag_message_id ON tag_message_id(tag);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS tag_text_idx_tag_message_id ON tag_message_id(tag, text);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS tag_topic_idx_tag_message_id ON tag_message_id(topic_id, tag);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS tag_topic_text_idx_tag_message_id ON tag_message_id(topic_id, tag, text);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 142").stepThis().dispose();
            i7 = 142;
        }
        if (i7 == 142) {
            sQLiteDatabase.executeFast("DROP TABLE IF EXISTS saved_reaction_tags;").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE TABLE saved_reaction_tags (topic_id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 143").stepThis().dispose();
            i7 = 143;
        }
        if (i7 == 143) {
            sQLiteDatabase.executeFast("ALTER TABLE dialog_filter ADD COLUMN color INTEGER default -1").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 144").stepThis().dispose();
            i7 = 144;
        }
        if (i7 == 144) {
            sQLiteDatabase.executeFast("PRAGMA user_version = 145").stepThis().dispose();
            i7 = 145;
        }
        if (i7 == 145) {
            sQLiteDatabase.executeFast("CREATE TABLE business_replies(topic_id INTEGER PRIMARY KEY, name TEXT, order_value INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 146").stepThis().dispose();
            i7 = 146;
        }
        if (i7 == 146) {
            sQLiteDatabase.executeFast("CREATE TABLE quick_replies_messages(mid INTEGER, topic_id INTEGER, send_state INTEGER, date INTEGER, data BLOB, ttl INTEGER, replydata BLOB, reply_to_message_id INTEGER, PRIMARY KEY(mid, topic_id))").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_quick_replies_messages ON quick_replies_messages(mid, send_state, date);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS topic_date_idx_quick_replies_messages ON quick_replies_messages(topic_id, date);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS reply_to_idx_quick_replies_messages ON quick_replies_messages(mid, reply_to_message_id);").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS idx_to_reply_quick_replies_messages ON quick_replies_messages(reply_to_message_id, mid);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 147").stepThis().dispose();
            i7 = 147;
        }
        if (i7 == 147) {
            sQLiteDatabase.executeFast("ALTER TABLE business_replies ADD COLUMN count INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 148").stepThis().dispose();
            i7 = 148;
        }
        if (i7 == 148) {
            sQLiteDatabase.executeFast("ALTER TABLE topics ADD COLUMN edit_date INTEGER default 0").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 149").stepThis().dispose();
            i7 = 149;
        }
        if (i7 == 149) {
            sQLiteDatabase.executeFast("ALTER TABLE stickersets2 ADD COLUMN short_name TEXT;").stepThis().dispose();
            sQLiteDatabase.executeFast("CREATE INDEX IF NOT EXISTS stickersets2_id_short_name ON stickersets2(id, short_name);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 150").stepThis().dispose();
            i7 = 150;
        }
        if (i7 == 150) {
            sQLiteDatabase.executeFast("CREATE TABLE business_links(data BLOB, order_value INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 151").stepThis().dispose();
            i7 = 151;
        }
        if (i7 == 151) {
            sQLiteDatabase.executeFast("ALTER TABLE profile_stories ADD COLUMN seen INTEGER default 0;").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 152").stepThis().dispose();
            i7 = 152;
        }
        if (i7 == 152) {
            sQLiteDatabase.executeFast("ALTER TABLE profile_stories ADD COLUMN pin INTEGER default 0;").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 153").stepThis().dispose();
            i7 = 153;
        }
        if (i7 == 153) {
            sQLiteDatabase.executeFast("CREATE TABLE effects(data BLOB)").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 154").stepThis().dispose();
            i7 = 154;
        }
        if (i7 == 154) {
            sQLiteDatabase.executeFast("CREATE TABLE fact_checks(hash INTEGER PRIMARY KEY, data BLOB, expires INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 155").stepThis().dispose();
            i7 = 155;
        }
        if (i7 == 155) {
            sQLiteDatabase.executeFast("CREATE TABLE popular_bots(uid INTEGER PRIMARY KEY, time INTEGER, offset TEXT);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 156").stepThis().dispose();
            i7 = 156;
        }
        if (i7 == 156 || i7 == 157) {
            sQLiteDatabase.executeFast("CREATE TABLE star_gifts2(id INTEGER PRIMARY KEY, data BLOB, hash INTEGER, time INTEGER);").stepThis().dispose();
            sQLiteDatabase.executeFast("PRAGMA user_version = 158").stepThis().dispose();
            i7 = 158;
        }
        if (i7 != 158) {
            return i7;
        }
        sQLiteDatabase.executeFast("DELETE FROM star_gifts2").stepThis().dispose();
        sQLiteDatabase.executeFast("ALTER TABLE star_gifts2 ADD COLUMN pos INTEGER default 0;").stepThis().dispose();
        sQLiteDatabase.executeFast("PRAGMA user_version = 159").stepThis().dispose();
        return 159;
    }

    public static boolean recoverDatabase(java.io.File r21, java.io.File r22, java.io.File r23, int r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DatabaseMigrationHelper.recoverDatabase(java.io.File, java.io.File, java.io.File, int):boolean");
    }
}

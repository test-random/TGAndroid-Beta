package org.telegram.messenger.audioinfo;

import android.graphics.Bitmap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import org.telegram.messenger.audioinfo.m4a.M4AInfo;
import org.telegram.messenger.audioinfo.mp3.MP3Info;

public abstract class AudioInfo {
    protected String album;
    protected String albumArtist;
    protected String artist;
    protected String brand;
    protected String comment;
    protected boolean compilation;
    protected String composer;
    protected String copyright;
    protected Bitmap cover;
    protected short disc;
    protected short discs;
    protected long duration;
    protected String genre;
    protected String grouping;
    protected String lyrics;
    protected Bitmap smallCover;
    protected String title;
    protected short track;
    protected short tracks;
    protected String version;
    protected short year;

    public static AudioInfo getAudioInfo(File file) {
        byte b;
        try {
            byte[] bArr = new byte[12];
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.readFully(bArr, 0, 8);
            randomAccessFile.close();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            if (bArr[4] == 102 && bArr[5] == 116 && bArr[6] == 121 && bArr[7] == 112) {
                return new M4AInfo(bufferedInputStream);
            }
            if (bArr[0] == 102 && bArr[1] == 76 && bArr[2] == 97 && bArr[3] == 99) {
                OtherAudioInfo otherAudioInfo = new OtherAudioInfo(file);
                if (otherAudioInfo.failed) {
                    return null;
                }
                return otherAudioInfo;
            }
            if (!file.getAbsolutePath().endsWith("mp3") && (((b = bArr[0]) != 73 || bArr[1] != 68 || bArr[2] != 51) && (b != 84 || bArr[1] != 65 || bArr[2] != 71))) {
                OtherAudioInfo otherAudioInfo2 = new OtherAudioInfo(file);
                if (otherAudioInfo2.failed) {
                    return null;
                }
                return otherAudioInfo2;
            }
            return new MP3Info(bufferedInputStream, file.length());
        } catch (Exception unused) {
            return null;
        }
    }

    public String getAlbum() {
        return this.album;
    }

    public String getAlbumArtist() {
        return this.albumArtist;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getComment() {
        return this.comment;
    }

    public String getComposer() {
        return this.composer;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public Bitmap getCover() {
        return this.cover;
    }

    public short getDisc() {
        return this.disc;
    }

    public short getDiscs() {
        return this.discs;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getGenre() {
        return this.genre;
    }

    public String getGrouping() {
        return this.grouping;
    }

    public String getLyrics() {
        return this.lyrics;
    }

    public Bitmap getSmallCover() {
        return this.smallCover;
    }

    public String getTitle() {
        return this.title;
    }

    public short getTrack() {
        return this.track;
    }

    public short getTracks() {
        return this.tracks;
    }

    public short getYear() {
        return this.year;
    }

    public boolean isCompilation() {
        return this.compilation;
    }
}

package org.telegram.messenger.secretmedia;

import android.content.Context;
import android.net.Uri;
import android.util.LongSparseArray;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.ContentDataSource;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheSpan;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.upstream.cache.ContentMetadataMutations;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import org.telegram.messenger.FileStreamLoadOperation;

public final class ExtendedDefaultDataSource implements DataSource {
    private static final String SCHEME_ASSET = "asset";
    private static final String SCHEME_CONTENT = "content";
    private static final String SCHEME_RAW = "rawresource";
    private static final String SCHEME_RTMP = "rtmp";
    private static final String TAG = "ExtendedDefaultDataSource";
    private DataSource assetDataSource;
    private final DataSource baseDataSource;
    private final Cache cache;
    private DataSource contentDataSource;
    private final Context context;
    private DataSource dataSchemeDataSource;
    private DataSource dataSource;
    private DataSource encryptedFileDataSource;
    private DataSource fileDataSource;
    private final LongSparseArray<Uri> mtprotoUris;
    private DataSource rawResourceDataSource;
    private DataSource rtmpDataSource;
    private FileStreamLoadOperation streamLoadOperation;
    private final List<TransferListener> transferListeners;

    public ExtendedDefaultDataSource(Context context, DataSource dataSource, LongSparseArray<Uri> longSparseArray) {
        this.cache = new Cache() {
            public NavigableSet<CacheSpan> addListener(String str, Cache.Listener listener) {
                return null;
            }

            public void applyContentMetadataMutations(String str, ContentMetadataMutations contentMetadataMutations) {
            }

            public void commitFile(File file, long j) {
            }

            public long getCacheSpace() {
                return 0L;
            }

            public long getCachedBytes(String str, long j, long j2) {
                return 0L;
            }

            public long getCachedLength(String str, long j, long j2) {
                return 0L;
            }

            public NavigableSet<CacheSpan> getCachedSpans(String str) {
                return null;
            }

            public ContentMetadata getContentMetadata(String str) {
                return null;
            }

            public Set<String> getKeys() {
                return null;
            }

            public long getUid() {
                return 0L;
            }

            public boolean isCached(String str, long j, long j2) {
                return false;
            }

            public void release() {
            }

            public void releaseHoleSpan(CacheSpan cacheSpan) {
            }

            public void removeListener(String str, Cache.Listener listener) {
            }

            public void removeResource(String str) {
            }

            public void removeSpan(CacheSpan cacheSpan) {
            }

            public File startFile(String str, long j, long j2) {
                return null;
            }

            public CacheSpan startReadWrite(String str, long j, long j2) {
                return null;
            }

            public CacheSpan startReadWriteNonBlocking(String str, long j, long j2) {
                return null;
            }
        };
        this.context = context.getApplicationContext();
        this.baseDataSource = (DataSource) Assertions.checkNotNull(dataSource);
        this.transferListeners = new ArrayList();
        this.mtprotoUris = longSparseArray;
    }

    @Deprecated
    public ExtendedDefaultDataSource(Context context, TransferListener transferListener, DataSource dataSource, LongSparseArray<Uri> longSparseArray) {
        this(context, dataSource, longSparseArray);
        if (transferListener != null) {
            this.transferListeners.add(transferListener);
            dataSource.addTransferListener(transferListener);
        }
    }

    public ExtendedDefaultDataSource(Context context, String str, int i, int i2, boolean z) {
        this(context, new DefaultHttpDataSource(str, i, i2, z, null), (LongSparseArray<Uri>) null);
    }

    public ExtendedDefaultDataSource(Context context, String str, boolean z) {
        this(context, str, 8000, 8000, z);
    }

    private void addListenersToDataSource(DataSource dataSource) {
        for (int i = 0; i < this.transferListeners.size(); i++) {
            dataSource.addTransferListener(this.transferListeners.get(i));
        }
    }

    private DataSource getAssetDataSource() {
        if (this.assetDataSource == null) {
            AssetDataSource assetDataSource = new AssetDataSource(this.context);
            this.assetDataSource = assetDataSource;
            addListenersToDataSource(assetDataSource);
        }
        return this.assetDataSource;
    }

    private DataSource getContentDataSource() {
        if (this.contentDataSource == null) {
            ContentDataSource contentDataSource = new ContentDataSource(this.context);
            this.contentDataSource = contentDataSource;
            addListenersToDataSource(contentDataSource);
        }
        return this.contentDataSource;
    }

    private DataSource getDataSchemeDataSource() {
        if (this.dataSchemeDataSource == null) {
            DataSchemeDataSource dataSchemeDataSource = new DataSchemeDataSource();
            this.dataSchemeDataSource = dataSchemeDataSource;
            addListenersToDataSource(dataSchemeDataSource);
        }
        return this.dataSchemeDataSource;
    }

    private DataSource getEncryptedFileDataSource() {
        if (this.encryptedFileDataSource == null) {
            EncryptedFileDataSource encryptedFileDataSource = new EncryptedFileDataSource();
            this.encryptedFileDataSource = encryptedFileDataSource;
            addListenersToDataSource(encryptedFileDataSource);
        }
        return this.encryptedFileDataSource;
    }

    private DataSource getFileDataSource() {
        if (this.fileDataSource == null) {
            FileDataSource fileDataSource = new FileDataSource();
            this.fileDataSource = fileDataSource;
            addListenersToDataSource(fileDataSource);
        }
        return this.fileDataSource;
    }

    private DataSource getRawResourceDataSource() {
        if (this.rawResourceDataSource == null) {
            RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this.context);
            this.rawResourceDataSource = rawResourceDataSource;
            addListenersToDataSource(rawResourceDataSource);
        }
        return this.rawResourceDataSource;
    }

    private DataSource getRtmpDataSource() {
        if (this.rtmpDataSource == null) {
            try {
                DataSource dataSource = (DataSource) Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource").getConstructor(null).newInstance(null);
                this.rtmpDataSource = dataSource;
                addListenersToDataSource(dataSource);
            } catch (ClassNotFoundException unused) {
                Log.w("ExtendedDefaultDataSource", "Attempting to play RTMP stream without depending on the RTMP extension");
            } catch (Exception e) {
                throw new RuntimeException("Error instantiating RTMP extension", e);
            }
            if (this.rtmpDataSource == null) {
                this.rtmpDataSource = this.baseDataSource;
            }
        }
        return this.rtmpDataSource;
    }

    private DataSource getStreamDataSource() {
        if (this.streamLoadOperation == null) {
            FileStreamLoadOperation fileStreamLoadOperation = new FileStreamLoadOperation();
            this.streamLoadOperation = fileStreamLoadOperation;
            addListenersToDataSource(fileStreamLoadOperation);
        }
        return this.streamLoadOperation;
    }

    private void maybeAddListenerToDataSource(DataSource dataSource, TransferListener transferListener) {
        if (dataSource != null) {
            dataSource.addTransferListener(transferListener);
        }
    }

    @Override
    public void addTransferListener(TransferListener transferListener) {
        this.baseDataSource.addTransferListener(transferListener);
        this.transferListeners.add(transferListener);
        maybeAddListenerToDataSource(this.fileDataSource, transferListener);
        maybeAddListenerToDataSource(this.assetDataSource, transferListener);
        maybeAddListenerToDataSource(this.contentDataSource, transferListener);
        maybeAddListenerToDataSource(this.rtmpDataSource, transferListener);
        maybeAddListenerToDataSource(this.dataSchemeDataSource, transferListener);
        maybeAddListenerToDataSource(this.rawResourceDataSource, transferListener);
    }

    @Override
    public void close() {
        DataSource dataSource = this.dataSource;
        if (dataSource != null) {
            try {
                dataSource.close();
            } finally {
                this.dataSource = null;
            }
        }
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        DataSource dataSource = this.dataSource;
        return dataSource == null ? Collections.emptyMap() : dataSource.getResponseHeaders();
    }

    @Override
    public Uri getUri() {
        DataSource dataSource = this.dataSource;
        if (dataSource == null) {
            return null;
        }
        return dataSource.getUri();
    }

    @Override
    public long open(DataSpec dataSpec) {
        DataSource contentDataSource;
        Assertions.checkState(this.dataSource == null);
        Uri uri = dataSpec.uri;
        if ("mtproto".equals(uri.getScheme())) {
            uri = this.mtprotoUris.get(Long.parseLong(dataSpec.uri.toString().substring(8)));
            dataSpec.uri = uri;
        }
        String scheme = uri.getScheme();
        if (Util.isLocalFileUri(uri)) {
            String path = uri.getPath();
            if (path == null || !path.startsWith("/android_asset/")) {
                contentDataSource = uri.getPath().endsWith(".enc") ? getEncryptedFileDataSource() : getFileDataSource();
            }
            contentDataSource = getAssetDataSource();
        } else if ("tg".equals(scheme)) {
            contentDataSource = getStreamDataSource();
        } else {
            if (!"asset".equals(scheme)) {
                contentDataSource = "content".equals(scheme) ? getContentDataSource() : "rtmp".equals(scheme) ? getRtmpDataSource() : "data".equals(scheme) ? getDataSchemeDataSource() : "rawresource".equals(scheme) ? getRawResourceDataSource() : this.baseDataSource;
            }
            contentDataSource = getAssetDataSource();
        }
        this.dataSource = contentDataSource;
        return contentDataSource.open(dataSpec);
    }

    @Override
    public int read(byte[] bArr, int i, int i2) {
        return ((DataSource) Assertions.checkNotNull(this.dataSource)).read(bArr, i, i2);
    }
}

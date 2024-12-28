package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.SessionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.SessionBottomSheet;
import org.telegram.ui.SessionsActivity;

public class SessionsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private TLRPC.TL_authorization currentSession;
    private int currentSessionRow;
    private int currentSessionSectionRow;
    private int currentType;
    private Delegate delegate;
    private EmptyTextProgressView emptyView;
    private boolean fragmentOpened;
    private FlickerLoadingView globalFlickerLoadingView;
    private boolean highlightLinkDesktopDevice;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private int noOtherSessionsRow;
    private int otherSessionsEndRow;
    private int otherSessionsSectionRow;
    private int otherSessionsStartRow;
    private int otherSessionsTerminateDetail;
    private int passwordSessionsDetailRow;
    private int passwordSessionsEndRow;
    private int passwordSessionsSectionRow;
    private int passwordSessionsStartRow;
    private int qrCodeDividerRow;
    private int qrCodeRow;
    private int rowCount;
    private int terminateAllSessionsDetailRow;
    private int terminateAllSessionsRow;
    private int ttlDays;
    private int ttlDivideRow;
    private int ttlHeaderRow;
    private int ttlRow;
    private UndoView undoView;
    private ArrayList sessions = new ArrayList();
    private ArrayList passwordSessions = new ArrayList();
    private int repeatLoad = 0;
    private final int VIEW_TYPE_TEXT = 0;
    private final int VIEW_TYPE_INFO = 1;
    private final int VIEW_TYPE_HEADER = 2;
    private final int VIEW_TYPE_SESSION = 4;
    private final int VIEW_TYPE_SCANQR = 5;
    private final int VIEW_TYPE_SETTINGS = 6;

    public class AnonymousClass4 extends UndoView {
        AnonymousClass4(Context context) {
            super(context);
        }

        public void lambda$hide$0(TLRPC.TL_error tL_error, TLRPC.TL_authorization tL_authorization) {
            if (tL_error == null) {
                SessionsActivity.this.sessions.remove(tL_authorization);
                SessionsActivity.this.passwordSessions.remove(tL_authorization);
                SessionsActivity.this.updateRows();
                if (SessionsActivity.this.listAdapter != null) {
                    SessionsActivity.this.listAdapter.notifyDataSetChanged();
                }
                SessionsActivity.this.lambda$loadSessions$17(true);
            }
        }

        public void lambda$hide$1(final TLRPC.TL_authorization tL_authorization, TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SessionsActivity.AnonymousClass4.this.lambda$hide$0(tL_error, tL_authorization);
                }
            });
        }

        @Override
        public void hide(boolean z, int i) {
            if (!z) {
                final TLRPC.TL_authorization tL_authorization = (TLRPC.TL_authorization) getCurrentInfoObject();
                TL_account.resetAuthorization resetauthorization = new TL_account.resetAuthorization();
                resetauthorization.hash = tL_authorization.hash;
                ConnectionsManager.getInstance(((BaseFragment) SessionsActivity.this).currentAccount).sendRequest(resetauthorization, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SessionsActivity.AnonymousClass4.this.lambda$hide$1(tL_authorization, tLObject, tL_error);
                    }
                });
            }
            super.hide(z, i);
        }
    }

    public class AnonymousClass5 implements SessionBottomSheet.Callback {
        AnonymousClass5() {
        }

        public void lambda$onSessionTerminated$0(TLRPC.TL_error tL_error, TLRPC.TL_authorization tL_authorization) {
            if (tL_error == null) {
                SessionsActivity.this.sessions.remove(tL_authorization);
                SessionsActivity.this.passwordSessions.remove(tL_authorization);
                SessionsActivity.this.updateRows();
                if (SessionsActivity.this.listAdapter != null) {
                    SessionsActivity.this.listAdapter.notifyDataSetChanged();
                }
            }
        }

        public void lambda$onSessionTerminated$1(final TLRPC.TL_authorization tL_authorization, TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SessionsActivity.AnonymousClass5.this.lambda$onSessionTerminated$0(tL_error, tL_authorization);
                }
            });
        }

        @Override
        public void onSessionTerminated(final TLRPC.TL_authorization tL_authorization) {
            TL_account.resetAuthorization resetauthorization = new TL_account.resetAuthorization();
            resetauthorization.hash = tL_authorization.hash;
            ConnectionsManager.getInstance(((BaseFragment) SessionsActivity.this).currentAccount).sendRequest(resetauthorization, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.AnonymousClass5.this.lambda$onSessionTerminated$1(tL_authorization, tLObject, tL_error);
                }
            });
        }
    }

    public class AnonymousClass6 implements CameraScanActivity.CameraScanActivityDelegate {
        private TLObject response = null;
        private TLRPC.TL_error error = null;

        AnonymousClass6() {
        }

        public void lambda$didFindQr$0() {
            String str;
            String str2 = this.error.text;
            if (str2 == null || !str2.equals("AUTH_TOKEN_EXCEPTION")) {
                str = LocaleController.getString(R.string.ErrorOccurred) + "\n" + this.error.text;
            } else {
                str = LocaleController.getString(R.string.AccountAlreadyLoggedIn);
            }
            AlertsCreator.showSimpleAlert(SessionsActivity.this, LocaleController.getString(R.string.AuthAnotherClient), str);
        }

        public void lambda$processQr$1(TLObject tLObject, TLRPC.TL_error tL_error, Runnable runnable) {
            this.response = tLObject;
            this.error = tL_error;
            runnable.run();
        }

        public void lambda$processQr$2(final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SessionsActivity.AnonymousClass6.this.lambda$processQr$1(tLObject, tL_error, runnable);
                }
            });
        }

        public void lambda$processQr$3() {
            AlertsCreator.showSimpleAlert(SessionsActivity.this, LocaleController.getString(R.string.AuthAnotherClient), LocaleController.getString(R.string.ErrorOccurred));
        }

        public void lambda$processQr$4(String str, final Runnable runnable) {
            try {
                byte[] decode = Base64.decode(str.substring(17).replaceAll("\\/", "_").replaceAll("\\+", "-"), 8);
                TLRPC.TL_auth_acceptLoginToken tL_auth_acceptLoginToken = new TLRPC.TL_auth_acceptLoginToken();
                tL_auth_acceptLoginToken.token = decode;
                SessionsActivity.this.getConnectionsManager().sendRequest(tL_auth_acceptLoginToken, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SessionsActivity.AnonymousClass6.this.lambda$processQr$2(runnable, tLObject, tL_error);
                    }
                });
            } catch (Exception e) {
                FileLog.e("Failed to pass qr code auth", e);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        SessionsActivity.AnonymousClass6.this.lambda$processQr$3();
                    }
                });
                runnable.run();
            }
        }

        @Override
        public void didFindMrzInfo(MrzRecognizer.Result result) {
            CameraScanActivity.CameraScanActivityDelegate.CC.$default$didFindMrzInfo(this, result);
        }

        @Override
        public void didFindQr(String str) {
            TLObject tLObject = this.response;
            if (!(tLObject instanceof TLRPC.TL_authorization)) {
                if (this.error != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            SessionsActivity.AnonymousClass6.this.lambda$didFindQr$0();
                        }
                    });
                    return;
                }
                return;
            }
            TLRPC.TL_authorization tL_authorization = (TLRPC.TL_authorization) tLObject;
            if (tL_authorization.password_pending) {
                SessionsActivity.this.passwordSessions.add(0, tL_authorization);
                SessionsActivity.this.repeatLoad = 4;
                SessionsActivity.this.lambda$loadSessions$17(false);
            } else {
                SessionsActivity.this.sessions.add(0, tL_authorization);
            }
            SessionsActivity.this.updateRows();
            SessionsActivity.this.listAdapter.notifyDataSetChanged();
            SessionsActivity.this.undoView.showWithAction(0L, 11, this.response);
        }

        @Override
        public String getSubtitleText() {
            return CameraScanActivity.CameraScanActivityDelegate.CC.$default$getSubtitleText(this);
        }

        @Override
        public void onDismiss() {
            CameraScanActivity.CameraScanActivityDelegate.CC.$default$onDismiss(this);
        }

        @Override
        public boolean processQr(final String str, final Runnable runnable) {
            this.response = null;
            this.error = null;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SessionsActivity.AnonymousClass6.this.lambda$processQr$4(str, runnable);
                }
            }, 750L);
            return true;
        }
    }

    public interface Delegate {
        void sessionsLoaded();
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
            setHasStableIds(true);
        }

        @Override
        public int getItemCount() {
            return SessionsActivity.this.rowCount;
        }

        @Override
        public long getItemId(int i) {
            int hash;
            if (i == SessionsActivity.this.terminateAllSessionsRow) {
                hash = Objects.hash(0, 0);
            } else if (i == SessionsActivity.this.terminateAllSessionsDetailRow) {
                hash = Objects.hash(0, 1);
            } else if (i == SessionsActivity.this.otherSessionsTerminateDetail) {
                hash = Objects.hash(0, 2);
            } else if (i == SessionsActivity.this.passwordSessionsDetailRow) {
                hash = Objects.hash(0, 3);
            } else if (i == SessionsActivity.this.qrCodeDividerRow) {
                hash = Objects.hash(0, 4);
            } else if (i == SessionsActivity.this.ttlDivideRow) {
                hash = Objects.hash(0, 5);
            } else if (i == SessionsActivity.this.noOtherSessionsRow) {
                hash = Objects.hash(0, 6);
            } else if (i == SessionsActivity.this.currentSessionSectionRow) {
                hash = Objects.hash(0, 7);
            } else if (i == SessionsActivity.this.otherSessionsSectionRow) {
                hash = Objects.hash(0, 8);
            } else if (i == SessionsActivity.this.passwordSessionsSectionRow) {
                hash = Objects.hash(0, 9);
            } else if (i == SessionsActivity.this.ttlHeaderRow) {
                hash = Objects.hash(0, 10);
            } else if (i == SessionsActivity.this.currentSessionRow) {
                hash = Objects.hash(0, 11);
            } else if (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow) {
                TLObject tLObject = (TLObject) SessionsActivity.this.sessions.get(i - SessionsActivity.this.otherSessionsStartRow);
                if (tLObject instanceof TLRPC.TL_authorization) {
                    hash = Objects.hash(1, Long.valueOf(((TLRPC.TL_authorization) tLObject).hash));
                } else {
                    if (tLObject instanceof TLRPC.TL_webAuthorization) {
                        hash = Objects.hash(1, Long.valueOf(((TLRPC.TL_webAuthorization) tLObject).hash));
                    }
                    hash = Objects.hash(0, -1);
                }
            } else if (i >= SessionsActivity.this.passwordSessionsStartRow && i < SessionsActivity.this.passwordSessionsEndRow) {
                TLObject tLObject2 = (TLObject) SessionsActivity.this.passwordSessions.get(i - SessionsActivity.this.passwordSessionsStartRow);
                if (tLObject2 instanceof TLRPC.TL_authorization) {
                    hash = Objects.hash(2, Long.valueOf(((TLRPC.TL_authorization) tLObject2).hash));
                } else {
                    if (tLObject2 instanceof TLRPC.TL_webAuthorization) {
                        hash = Objects.hash(2, Long.valueOf(((TLRPC.TL_webAuthorization) tLObject2).hash));
                    }
                    hash = Objects.hash(0, -1);
                }
            } else if (i == SessionsActivity.this.qrCodeRow) {
                hash = Objects.hash(0, 12);
            } else {
                if (i == SessionsActivity.this.ttlRow) {
                    hash = Objects.hash(0, 13);
                }
                hash = Objects.hash(0, -1);
            }
            return hash;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == SessionsActivity.this.terminateAllSessionsRow) {
                return 0;
            }
            if (i == SessionsActivity.this.terminateAllSessionsDetailRow || i == SessionsActivity.this.otherSessionsTerminateDetail || i == SessionsActivity.this.passwordSessionsDetailRow || i == SessionsActivity.this.qrCodeDividerRow || i == SessionsActivity.this.ttlDivideRow || i == SessionsActivity.this.noOtherSessionsRow) {
                return 1;
            }
            if (i == SessionsActivity.this.currentSessionSectionRow || i == SessionsActivity.this.otherSessionsSectionRow || i == SessionsActivity.this.passwordSessionsSectionRow || i == SessionsActivity.this.ttlHeaderRow) {
                return 2;
            }
            if (i == SessionsActivity.this.currentSessionRow) {
                return 4;
            }
            if (i >= SessionsActivity.this.otherSessionsStartRow && i < SessionsActivity.this.otherSessionsEndRow) {
                return 4;
            }
            if (i >= SessionsActivity.this.passwordSessionsStartRow && i < SessionsActivity.this.passwordSessionsEndRow) {
                return 4;
            }
            if (i == SessionsActivity.this.qrCodeRow) {
                return 5;
            }
            return i == SessionsActivity.this.ttlRow ? 6 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == SessionsActivity.this.terminateAllSessionsRow || (adapterPosition >= SessionsActivity.this.otherSessionsStartRow && adapterPosition < SessionsActivity.this.otherSessionsEndRow) || ((adapterPosition >= SessionsActivity.this.passwordSessionsStartRow && adapterPosition < SessionsActivity.this.passwordSessionsEndRow) || adapterPosition == SessionsActivity.this.currentSessionRow || adapterPosition == SessionsActivity.this.ttlRow);
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r6, int r7) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SessionsActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textCell;
            if (i != 0) {
                if (i == 1) {
                    textCell = new TextInfoPrivacyCell(this.mContext);
                } else if (i == 2) {
                    textCell = new HeaderCell(this.mContext);
                } else if (i != 5) {
                    textCell = i != 6 ? new SessionCell(this.mContext, SessionsActivity.this.currentType) : new TextSettingsCell(this.mContext);
                } else {
                    textCell = new ScanQRCodeView(this.mContext);
                }
                return new RecyclerListView.Holder(textCell);
            }
            textCell = new TextCell(this.mContext);
            textCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(textCell);
        }
    }

    public class ScanQRCodeView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        TextView buttonTextView;
        CellFlickerDrawable flickerDrawable;
        BackupImageView imageView;
        TextView textView;

        public ScanQRCodeView(Context context) {
            super(context);
            this.flickerDrawable = new CellFlickerDrawable();
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(120, 120.0f, 1, 0.0f, 16.0f, 0.0f, 0.0f));
            CellFlickerDrawable cellFlickerDrawable = this.flickerDrawable;
            cellFlickerDrawable.repeatEnabled = false;
            cellFlickerDrawable.animationSpeedScale = 1.2f;
            this.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation() == null || ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation().isRunning()) {
                        return;
                    }
                    ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    ScanQRCodeView.this.imageView.getImageReceiver().getLottieAnimation().restart();
                }
            });
            int i = Theme.key_windowBackgroundWhiteBlackText;
            Theme.getColor(i);
            int i2 = Theme.key_windowBackgroundWhite;
            Theme.getColor(i2);
            int i3 = Theme.key_featuredStickers_addButton;
            Theme.getColor(i3);
            Theme.getColor(i2);
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
            this.textView = linksTextView;
            addView(linksTextView, LayoutHelper.createFrame(-1, -2.0f, 0, 36.0f, 152.0f, 36.0f, 0.0f));
            this.textView.setGravity(1);
            this.textView.setTextColor(Theme.getColor(i));
            this.textView.setTextSize(1, 15.0f);
            this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            this.textView.setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
            setBackgroundColor(Theme.getColor(i2));
            String string = LocaleController.getString(R.string.AuthAnotherClientInfo4);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
            int indexOf = string.indexOf(42);
            int i4 = indexOf + 1;
            int indexOf2 = string.indexOf(42, i4);
            if (indexOf != -1 && indexOf2 != -1 && indexOf != indexOf2) {
                this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                spannableStringBuilder.replace(indexOf2, indexOf2 + 1, (CharSequence) "");
                spannableStringBuilder.replace(indexOf, i4, (CharSequence) "");
                spannableStringBuilder.setSpan(new URLSpanNoUnderline(LocaleController.getString(R.string.AuthAnotherClientDownloadClientUrl)), indexOf, indexOf2 - 1, 33);
            }
            String spannableStringBuilder2 = spannableStringBuilder.toString();
            int indexOf3 = spannableStringBuilder2.indexOf(42);
            int i5 = indexOf3 + 1;
            int indexOf4 = spannableStringBuilder2.indexOf(42, i5);
            if (indexOf3 != -1 && indexOf4 != -1 && indexOf3 != indexOf4) {
                this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                spannableStringBuilder.replace(indexOf4, indexOf4 + 1, (CharSequence) "");
                spannableStringBuilder.replace(indexOf3, i5, (CharSequence) "");
                spannableStringBuilder.setSpan(new URLSpanNoUnderline(LocaleController.getString(R.string.AuthAnotherWebClientUrl)), indexOf3, indexOf4 - 1, 33);
            }
            this.textView.setText(spannableStringBuilder);
            TextView textView = new TextView(context) {
                @Override
                public void draw(Canvas canvas) {
                    super.draw(canvas);
                    ScanQRCodeView scanQRCodeView = ScanQRCodeView.this;
                    if (scanQRCodeView.flickerDrawable.progress <= 1.0f && SessionsActivity.this.highlightLinkDesktopDevice && SessionsActivity.this.fragmentOpened) {
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                        ScanQRCodeView.this.flickerDrawable.setParentWidth(getMeasuredWidth());
                        ScanQRCodeView.this.flickerDrawable.draw(canvas, rectF, AndroidUtilities.dp(8.0f), null);
                        invalidate();
                    }
                }
            };
            this.buttonTextView = textView;
            textView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            this.buttonTextView.setGravity(17);
            this.buttonTextView.setTextSize(1, 14.0f);
            this.buttonTextView.setTypeface(AndroidUtilities.bold());
            SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder();
            spannableStringBuilder3.append((CharSequence) ".  ").append((CharSequence) LocaleController.getString(R.string.LinkDesktopDevice));
            spannableStringBuilder3.setSpan(new ColoredImageSpan(ContextCompat.getDrawable(getContext(), R.drawable.msg_mini_qr)), 0, 1, 0);
            this.buttonTextView.setText(spannableStringBuilder3);
            this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(i3), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
            this.buttonTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SessionsActivity.ScanQRCodeView.this.lambda$new$0(view);
                }
            });
            addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 15.0f, 16.0f, 16.0f));
            setSticker();
        }

        public void lambda$new$0(View view) {
            int checkSelfPermission;
            if (SessionsActivity.this.getParentActivity() == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                checkSelfPermission = SessionsActivity.this.getParentActivity().checkSelfPermission("android.permission.CAMERA");
                if (checkSelfPermission != 0) {
                    SessionsActivity.this.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 34);
                    return;
                }
            }
            SessionsActivity.this.openCameraScanActivity();
        }

        private void setSticker() {
            TLRPC.TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(((BaseFragment) SessionsActivity.this).currentAccount).getStickerSetByName("tg_placeholders_android");
            if (stickerSetByName == null) {
                stickerSetByName = MediaDataController.getInstance(((BaseFragment) SessionsActivity.this).currentAccount).getStickerSetByEmojiOrName("tg_placeholders_android");
            }
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSetByName;
            TLRPC.Document document = (tL_messages_stickerSet == null || tL_messages_stickerSet.documents.size() <= 6) ? null : tL_messages_stickerSet.documents.get(6);
            SvgHelper.SvgDrawable svgThumb = document != null ? DocumentObject.getSvgThumb(document.thumbs, Theme.key_emptyListPlaceholder, 0.2f) : null;
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }
            if (document == null) {
                MediaDataController.getInstance(((BaseFragment) SessionsActivity.this).currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, tL_messages_stickerSet == null);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(document), "130_130", "tgs", svgThumb, tL_messages_stickerSet);
                this.imageView.getImageReceiver().setAutoRepeat(2);
            }
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.diceStickersDidLoad && "tg_placeholders_android".equals((String) objArr[0])) {
                setSticker();
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            setSticker();
            NotificationCenter.getInstance(((BaseFragment) SessionsActivity.this).currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(((BaseFragment) SessionsActivity.this).currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(276.0f), 1073741824));
        }
    }

    public SessionsActivity(int i) {
        this.currentType = i;
    }

    public static void lambda$createView$0(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$createView$1(AlertDialog.Builder builder, View view) {
        builder.getDismissRunnable().run();
        Integer num = (Integer) view.getTag();
        int i = num.intValue() == 0 ? 7 : num.intValue() == 1 ? 90 : num.intValue() == 2 ? 183 : num.intValue() == 3 ? 365 : 0;
        TL_account.setAuthorizationTTL setauthorizationttl = new TL_account.setAuthorizationTTL();
        setauthorizationttl.authorization_ttl_days = i;
        this.ttlDays = i;
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        getConnectionsManager().sendRequest(setauthorizationttl, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SessionsActivity.lambda$createView$0(tLObject, tL_error);
            }
        });
    }

    public void lambda$createView$10(AlertDialog alertDialog, TLRPC.TL_error tL_error, TLRPC.TL_webAuthorization tL_webAuthorization) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tL_error == null) {
            this.sessions.remove(tL_webAuthorization);
            updateRows();
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    public void lambda$createView$11(final AlertDialog alertDialog, final TLRPC.TL_webAuthorization tL_webAuthorization, TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SessionsActivity.this.lambda$createView$10(alertDialog, tL_error, tL_webAuthorization);
            }
        });
    }

    public void lambda$createView$12(int i, boolean[] zArr, DialogInterface dialogInterface, int i2) {
        if (getParentActivity() == null) {
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        alertDialog.setCanCancel(false);
        alertDialog.show();
        if (this.currentType == 0) {
            int i3 = this.otherSessionsStartRow;
            final TLRPC.TL_authorization tL_authorization = (TLRPC.TL_authorization) ((i < i3 || i >= this.otherSessionsEndRow) ? this.passwordSessions.get(i - this.passwordSessionsStartRow) : this.sessions.get(i - i3));
            TL_account.resetAuthorization resetauthorization = new TL_account.resetAuthorization();
            resetauthorization.hash = tL_authorization.hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(resetauthorization, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.lambda$createView$9(alertDialog, tL_authorization, tLObject, tL_error);
                }
            });
            return;
        }
        final TLRPC.TL_webAuthorization tL_webAuthorization = (TLRPC.TL_webAuthorization) this.sessions.get(i - this.otherSessionsStartRow);
        TL_account.resetWebAuthorization resetwebauthorization = new TL_account.resetWebAuthorization();
        resetwebauthorization.hash = tL_webAuthorization.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(resetwebauthorization, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SessionsActivity.this.lambda$createView$11(alertDialog, tL_webAuthorization, tLObject, tL_error);
            }
        });
        if (zArr[0]) {
            MessagesController.getInstance(this.currentAccount).blockPeer(tL_webAuthorization.bot_id);
        }
    }

    public void lambda$createView$13(View view, final int i) {
        CharSequence charSequence;
        TextView textView;
        TLRPC.TL_authorization tL_authorization;
        int i2;
        boolean z = true;
        if (i == this.ttlRow) {
            if (getParentActivity() == null) {
                return;
            }
            int i3 = this.ttlDays;
            int i4 = i3 <= 7 ? 0 : i3 <= 93 ? 1 : i3 <= 183 ? 2 : 3;
            final AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.SessionsSelfDestruct));
            String[] strArr = {LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.formatPluralString("Months", 3, new Object[0]), LocaleController.formatPluralString("Months", 6, new Object[0]), LocaleController.formatPluralString("Years", 1, new Object[0])};
            LinearLayout linearLayout = new LinearLayout(getParentActivity());
            linearLayout.setOrientation(1);
            builder.setView(linearLayout);
            int i5 = 0;
            while (i5 < 4) {
                RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
                radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                radioColorCell.setTag(Integer.valueOf(i5));
                radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
                radioColorCell.setTextAndValue(strArr[i5], i4 == i5);
                linearLayout.addView(radioColorCell);
                radioColorCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
                radioColorCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        SessionsActivity.this.lambda$createView$1(builder, view2);
                    }
                });
                i5++;
            }
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            showDialog(builder.create());
            return;
        }
        if (i == this.terminateAllSessionsRow) {
            if (getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
            if (this.currentType == 0) {
                builder2.setMessage(LocaleController.getString(R.string.AreYouSureSessions));
                builder2.setTitle(LocaleController.getString(R.string.AreYouSureSessionsTitle));
                i2 = R.string.Terminate;
            } else {
                builder2.setMessage(LocaleController.getString(R.string.AreYouSureWebSessions));
                builder2.setTitle(LocaleController.getString(R.string.TerminateWebSessionsTitle));
                i2 = R.string.Disconnect;
            }
            builder2.setPositiveButton(LocaleController.getString(i2), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i6) {
                    SessionsActivity.this.lambda$createView$6(dialogInterface, i6);
                }
            });
            builder2.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog create = builder2.create();
            showDialog(create);
            textView = (TextView) create.getButton(-1);
            if (textView == null) {
                return;
            }
        } else {
            if (((i < this.otherSessionsStartRow || i >= this.otherSessionsEndRow) && ((i < this.passwordSessionsStartRow || i >= this.passwordSessionsEndRow) && i != this.currentSessionRow)) || getParentActivity() == null) {
                return;
            }
            if (this.currentType == 0) {
                if (i == this.currentSessionRow) {
                    tL_authorization = this.currentSession;
                } else {
                    int i6 = this.otherSessionsStartRow;
                    tL_authorization = (TLRPC.TL_authorization) ((i < i6 || i >= this.otherSessionsEndRow) ? this.passwordSessions.get(i - this.passwordSessionsStartRow) : this.sessions.get(i - i6));
                    z = false;
                }
                showSessionBottomSheet(tL_authorization, z);
                return;
            }
            AlertDialog.Builder builder3 = new AlertDialog.Builder(getParentActivity());
            final boolean[] zArr = new boolean[1];
            if (this.currentType == 0) {
                builder3.setMessage(LocaleController.getString(R.string.TerminateSessionText));
                builder3.setTitle(LocaleController.getString(R.string.AreYouSureSessionTitle));
                charSequence = LocaleController.getString(R.string.Terminate);
            } else {
                TLRPC.TL_webAuthorization tL_webAuthorization = (TLRPC.TL_webAuthorization) this.sessions.get(i - this.otherSessionsStartRow);
                builder3.setMessage(LocaleController.formatString("TerminateWebSessionText", R.string.TerminateWebSessionText, tL_webAuthorization.domain));
                builder3.setTitle(LocaleController.getString(R.string.TerminateWebSessionTitle));
                CharSequence string = LocaleController.getString(R.string.Disconnect);
                FrameLayout frameLayout = new FrameLayout(getParentActivity());
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tL_webAuthorization.bot_id));
                String firstName = user != null ? UserObject.getFirstName(user) : "";
                CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
                checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                checkBoxCell.setText(LocaleController.formatString("TerminateWebSessionStop", R.string.TerminateWebSessionStop, firstName), "", false, false);
                checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                checkBoxCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        SessionsActivity.lambda$createView$7(zArr, view2);
                    }
                });
                builder3.setCustomViewOffset(16);
                builder3.setView(frameLayout);
                charSequence = string;
            }
            builder3.setPositiveButton(charSequence, new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i7) {
                    SessionsActivity.this.lambda$createView$12(i, zArr, dialogInterface, i7);
                }
            });
            builder3.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog create2 = builder3.create();
            showDialog(create2);
            textView = (TextView) create2.getButton(-1);
            if (textView == null) {
                return;
            }
        }
        textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
    }

    public void lambda$createView$2(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (getParentActivity() != null && tL_error == null && (tLObject instanceof TLRPC.TL_boolTrue)) {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.AllSessionsTerminated)).show();
            lambda$loadSessions$17(false);
        }
    }

    public void lambda$createView$3(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SessionsActivity.this.lambda$createView$2(tL_error, tLObject);
            }
        });
        for (int i = 0; i < 4; i++) {
            UserConfig userConfig = UserConfig.getInstance(i);
            if (userConfig.isClientActivated()) {
                userConfig.registeredForPush = false;
                userConfig.saveConfig(false);
                MessagesController.getInstance(i).registerForPush(SharedConfig.pushType, SharedConfig.pushString);
                ConnectionsManager.getInstance(i).setUserId(userConfig.getClientUserId());
            }
        }
    }

    public void lambda$createView$4(TLRPC.TL_error tL_error, TLObject tLObject) {
        BulletinFactory of;
        int i;
        int i2;
        if (getParentActivity() == null) {
            return;
        }
        if (tL_error == null && (tLObject instanceof TLRPC.TL_boolTrue)) {
            of = BulletinFactory.of(this);
            i = R.raw.contact_check;
            i2 = R.string.AllWebSessionsTerminated;
        } else {
            of = BulletinFactory.of(this);
            i = R.raw.error;
            i2 = R.string.UnknownError;
        }
        of.createSimpleBulletin(i, LocaleController.getString(i2)).show();
        lambda$loadSessions$17(false);
    }

    public void lambda$createView$5(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SessionsActivity.this.lambda$createView$4(tL_error, tLObject);
            }
        });
    }

    public void lambda$createView$6(DialogInterface dialogInterface, int i) {
        TLObject resetwebauthorizations;
        ConnectionsManager connectionsManager;
        RequestDelegate requestDelegate;
        if (this.currentType == 0) {
            resetwebauthorizations = new TLRPC.TL_auth_resetAuthorizations();
            connectionsManager = ConnectionsManager.getInstance(this.currentAccount);
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.lambda$createView$3(tLObject, tL_error);
                }
            };
        } else {
            resetwebauthorizations = new TL_account.resetWebAuthorizations();
            connectionsManager = ConnectionsManager.getInstance(this.currentAccount);
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.lambda$createView$5(tLObject, tL_error);
                }
            };
        }
        connectionsManager.sendRequest(resetwebauthorizations, requestDelegate);
    }

    public static void lambda$createView$7(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            boolean z = !zArr[0];
            zArr[0] = z;
            ((CheckBoxCell) view).setChecked(z, true);
        }
    }

    public void lambda$createView$8(AlertDialog alertDialog, TLRPC.TL_error tL_error, TLRPC.TL_authorization tL_authorization) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tL_error == null) {
            this.sessions.remove(tL_authorization);
            this.passwordSessions.remove(tL_authorization);
            updateRows();
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    public void lambda$createView$9(final AlertDialog alertDialog, final TLRPC.TL_authorization tL_authorization, TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SessionsActivity.this.lambda$createView$8(alertDialog, tL_error, tL_authorization);
            }
        });
    }

    public void lambda$loadSessions$15(TLRPC.TL_error tL_error, TLObject tLObject, final boolean z) {
        this.loading = false;
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.getItemCount();
        }
        if (tL_error == null) {
            this.sessions.clear();
            this.passwordSessions.clear();
            TL_account.authorizations authorizationsVar = (TL_account.authorizations) tLObject;
            int size = authorizationsVar.authorizations.size();
            for (int i = 0; i < size; i++) {
                TLRPC.TL_authorization tL_authorization = authorizationsVar.authorizations.get(i);
                if ((tL_authorization.flags & 1) != 0) {
                    this.currentSession = tL_authorization;
                } else {
                    (tL_authorization.password_pending ? this.passwordSessions : this.sessions).add(tL_authorization);
                }
            }
            this.ttlDays = authorizationsVar.authorization_ttl_days;
            updateRows();
            Delegate delegate = this.delegate;
            if (delegate != null) {
                delegate.sessionsLoaded();
            }
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        Delegate delegate2 = this.delegate;
        if (delegate2 != null) {
            delegate2.sessionsLoaded();
        }
        int i2 = this.repeatLoad;
        if (i2 > 0) {
            int i3 = i2 - 1;
            this.repeatLoad = i3;
            if (i3 > 0) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        SessionsActivity.this.lambda$loadSessions$14(z);
                    }
                }, 2500L);
            }
        }
    }

    public void lambda$loadSessions$16(final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SessionsActivity.this.lambda$loadSessions$15(tL_error, tLObject, z);
            }
        });
    }

    public void lambda$loadSessions$18(TLRPC.TL_error tL_error, TLObject tLObject, final boolean z) {
        this.loading = false;
        if (tL_error == null) {
            this.sessions.clear();
            TL_account.webAuthorizations webauthorizations = (TL_account.webAuthorizations) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(webauthorizations.users, false);
            this.sessions.addAll(webauthorizations.authorizations);
            updateRows();
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        Delegate delegate = this.delegate;
        if (delegate != null) {
            delegate.sessionsLoaded();
        }
        int i = this.repeatLoad;
        if (i > 0) {
            int i2 = i - 1;
            this.repeatLoad = i2;
            if (i2 > 0) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        SessionsActivity.this.lambda$loadSessions$17(z);
                    }
                }, 2500L);
            }
        }
    }

    public void lambda$loadSessions$19(final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SessionsActivity.this.lambda$loadSessions$18(tL_error, tLObject, z);
            }
        });
    }

    public void lambda$onRequestPermissionsResultFragment$20(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void openCameraScanActivity() {
        CameraScanActivity.showAsSheet((BaseFragment) this, false, 2, (CameraScanActivity.CameraScanActivityDelegate) new AnonymousClass6());
    }

    private void showSessionBottomSheet(TLRPC.TL_authorization tL_authorization, boolean z) {
        if (tL_authorization == null) {
            return;
        }
        new SessionBottomSheet(this, tL_authorization, z, new AnonymousClass5()).show();
    }

    public void updateRows() {
        this.rowCount = 0;
        int i = -1;
        this.currentSessionSectionRow = -1;
        this.currentSessionRow = -1;
        this.terminateAllSessionsRow = -1;
        this.terminateAllSessionsDetailRow = -1;
        this.passwordSessionsSectionRow = -1;
        this.passwordSessionsStartRow = -1;
        this.passwordSessionsEndRow = -1;
        this.passwordSessionsDetailRow = -1;
        this.otherSessionsSectionRow = -1;
        this.otherSessionsStartRow = -1;
        this.otherSessionsEndRow = -1;
        this.otherSessionsTerminateDetail = -1;
        this.noOtherSessionsRow = -1;
        this.qrCodeRow = -1;
        this.qrCodeDividerRow = -1;
        this.ttlHeaderRow = -1;
        this.ttlRow = -1;
        this.ttlDivideRow = -1;
        if (this.currentType == 0 && getMessagesController().qrLoginCamera) {
            int i2 = this.rowCount;
            this.qrCodeRow = i2;
            this.rowCount = i2 + 2;
            this.qrCodeDividerRow = i2 + 1;
        }
        if (this.loading) {
            if (this.currentType == 0) {
                int i3 = this.rowCount;
                this.currentSessionSectionRow = i3;
                this.rowCount = i3 + 2;
                this.currentSessionRow = i3 + 1;
                return;
            }
            return;
        }
        if (this.currentSession != null) {
            int i4 = this.rowCount;
            this.currentSessionSectionRow = i4;
            this.rowCount = i4 + 2;
            this.currentSessionRow = i4 + 1;
        }
        if (this.passwordSessions.isEmpty() && this.sessions.isEmpty()) {
            this.terminateAllSessionsRow = -1;
            this.terminateAllSessionsDetailRow = -1;
            if (this.currentType == 1 || this.currentSession != null) {
                i = this.rowCount;
                this.rowCount = i + 1;
            }
        } else {
            int i5 = this.rowCount;
            this.terminateAllSessionsRow = i5;
            this.rowCount = i5 + 2;
            this.terminateAllSessionsDetailRow = i5 + 1;
        }
        this.noOtherSessionsRow = i;
        if (!this.passwordSessions.isEmpty()) {
            int i6 = this.rowCount;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.passwordSessionsSectionRow = i6;
            this.passwordSessionsStartRow = i7;
            int size = i7 + this.passwordSessions.size();
            this.passwordSessionsEndRow = size;
            this.rowCount = size + 1;
            this.passwordSessionsDetailRow = size;
        }
        if (!this.sessions.isEmpty()) {
            int i8 = this.rowCount;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.otherSessionsSectionRow = i8;
            this.otherSessionsStartRow = i9;
            this.otherSessionsEndRow = i9 + this.sessions.size();
            int size2 = this.rowCount + this.sessions.size();
            this.rowCount = size2 + 1;
            this.otherSessionsTerminateDetail = size2;
        }
        if (this.ttlDays > 0) {
            int i10 = this.rowCount;
            this.ttlHeaderRow = i10;
            this.ttlRow = i10 + 1;
            this.rowCount = i10 + 3;
            this.ttlDivideRow = i10 + 2;
        }
    }

    @Override
    public View createView(Context context) {
        ActionBar actionBar;
        int i;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.globalFlickerLoadingView = flickerLoadingView;
        int i2 = 1;
        flickerLoadingView.setIsSingleCell(true);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            actionBar = this.actionBar;
            i = R.string.Devices;
        } else {
            actionBar = this.actionBar;
            i = R.string.WebSessionsTitle;
        }
        actionBar.setTitle(LocaleController.getString(i));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    SessionsActivity.this.lambda$onBackPressed$321();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 17));
        RecyclerListView recyclerListView = new RecyclerListView(context) {
            @Override
            public Integer getSelectorColor(int i3) {
                return Integer.valueOf(i3 == SessionsActivity.this.terminateAllSessionsRow ? Theme.multAlpha(getThemedColor(Theme.key_text_RedRegular), 0.1f) : getThemedColor(Theme.key_listSelector));
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, i2, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(150L);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        defaultItemAnimator.setMoveInterpolator(cubicBezierInterpolator);
        defaultItemAnimator.setTranslationInterpolator(cubicBezierInterpolator);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i3) {
                SessionsActivity.this.lambda$createView$13(view, i3);
            }
        });
        if (this.currentType == 0) {
            AnonymousClass4 anonymousClass4 = new AnonymousClass4(context);
            this.undoView = anonymousClass4;
            frameLayout.addView(anonymousClass4, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        updateRows();
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.newSessionReceived) {
            lambda$loadSessions$17(true);
        }
    }

    public int getSessionsCount() {
        if (this.sessions.size() == 0 && this.loading) {
            return 0;
        }
        return this.sessions.size() + (this.currentType == 0 ? 1 : 0);
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, SessionCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        int i3 = Theme.key_text_RedRegular;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        int i5 = Theme.key_windowBackgroundWhiteGrayText3;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{SessionCell.class}, new String[]{"onlineTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SessionCell.class}, new String[]{"detailExTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        int i6 = Theme.key_undo_infoColor;
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        return arrayList;
    }

    public void lambda$loadSessions$17(final boolean z) {
        TLObject getwebauthorizations;
        ConnectionsManager connectionsManager;
        RequestDelegate requestDelegate;
        if (this.loading) {
            return;
        }
        if (!z) {
            this.loading = true;
        }
        if (this.currentType == 0) {
            getwebauthorizations = new TL_account.getAuthorizations();
            connectionsManager = ConnectionsManager.getInstance(this.currentAccount);
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.lambda$loadSessions$16(z, tLObject, tL_error);
                }
            };
        } else {
            getwebauthorizations = new TL_account.getWebAuthorizations();
            connectionsManager = ConnectionsManager.getInstance(this.currentAccount);
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SessionsActivity.this.lambda$loadSessions$19(z, tLObject, tL_error);
                }
            };
        }
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(connectionsManager.sendRequest(getwebauthorizations, requestDelegate), this.classGuid);
    }

    @Override
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        lambda$loadSessions$17(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newSessionReceived);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newSessionReceived);
    }

    @Override
    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override
    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (getParentActivity() != null && i == 34) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                new AlertDialog.Builder(getParentActivity()).setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.QRCodePermissionNoCameraWithHint))).setPositiveButton(LocaleController.getString(R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        SessionsActivity.this.lambda$onRequestPermissionsResultFragment$20(dialogInterface, i2);
                    }
                }).setNegativeButton(LocaleController.getString(R.string.ContactsPermissionAlertNotNow), null).setTopAnimation(R.raw.permission_request_camera, 72, false, Theme.getColor(Theme.key_dialogTopBackground)).show();
            } else {
                openCameraScanActivity();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        super.onTransitionAnimationEnd(z, z2);
        if (!z || z2) {
            return;
        }
        this.fragmentOpened = true;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof ScanQRCodeView) {
                ((ScanQRCodeView) childAt).buttonTextView.invalidate();
            }
        }
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public SessionsActivity setHighlightLinkDesktopDevice() {
        this.highlightLinkDesktopDevice = true;
        return this;
    }
}

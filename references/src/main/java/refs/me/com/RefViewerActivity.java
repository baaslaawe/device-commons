package refs.me.com;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import refs.me.com.keep.Reference;
import utils.app.com.installs.InstallsComponent;

public class RefViewerActivity extends AppCompatActivity {

    public static final String EXTRA_REFERENCE = "kdscuyu772872782";

    private WebView webView;

    private Reference reference;

    public static Intent createIntent(Context context, Reference reference) {
        Intent intent = new Intent(context, RefViewerActivity.class);
        intent.putExtra(EXTRA_REFERENCE, reference);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ref_viewer_activity);
        webView = findViewById(R.id.webView);
        reference = (Reference) getIntent().getSerializableExtra(EXTRA_REFERENCE);
        init();
        webView.loadUrl(reference.getLink());
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void init() {
        webView.getSettings().setDomStorageEnabled(true);
        WebChromeClient webChromeClient = new WebChromeClient();
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*if (url.contains("market://")) {
                    ReferencesComponent.get().clearActiveReferences();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    webView.getContext().startActivity(intent);
                    finish();
                    return false;
                }*/
                if (RefUtils.isMarketTypeLink(url)) {
                    reference.setLink(url);
                    finish();
                    ReferencesComponent.get().onReferenceReceived(reference);
                    return false;
                }
                view.loadUrl(url);
                return true;
            }
        };
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setSaveFormData(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                InstallsComponent.get().onUrlReceived(url, "");
                ReferencesComponent.get().sendRefEvent(reference.getId());
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}

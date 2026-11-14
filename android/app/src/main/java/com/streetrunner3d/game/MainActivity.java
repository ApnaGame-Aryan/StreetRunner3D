package com.streetrunner3d.game;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends Activity {
    // Debug builds always use Google's TEST ad unit. Release builds use the live one.
    private static final String TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String LIVE_REWARDED_ID = "ca-app-pub-3289477301560269/2622754240";

    private WebView gameView;
    private RewardedAd rewardedAd;
    private boolean loadingAd = false;
    private boolean rewardEarned = false;

    private String rewardedUnitId() {
        boolean debug = (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        return debug ? TEST_REWARDED_ID : LIVE_REWARDED_ID;
    }

    private void callJs(final String js) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if (gameView != null) gameView.evaluateJavascript(js, null);
            }
        });
    }

    private void loadRewarded() {
        if (loadingAd || rewardedAd != null) return;
        loadingAd = true;
        RewardedAd.load(this, rewardedUnitId(), new AdRequest.Builder().build(),
            new RewardedAdLoadCallback() {
                @Override public void onAdLoaded(RewardedAd ad) {
                    rewardedAd = ad;
                    loadingAd = false;
                }
                @Override public void onAdFailedToLoad(LoadAdError error) {
                    rewardedAd = null;
                    loadingAd = false;
                }
            });
    }

    private void showRewarded() {
        if (rewardedAd == null) {
            loadRewarded();
            callJs("window.onAdFailed&&window.onAdFailed('notready')");
            return;
        }
        final RewardedAd ad = rewardedAd;
        rewardedAd = null;
        rewardEarned = false;
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override public void onAdDismissedFullScreenContent() {
                loadRewarded();
                if (rewardEarned) callJs("window.onAdReward&&window.onAdReward()");
                else callJs("window.onAdFailed&&window.onAdFailed('closed')");
                rewardEarned = false;
            }
            @Override public void onAdFailedToShowFullScreenContent(AdError error) {
                loadRewarded();
                callJs("window.onAdFailed&&window.onAdFailed('error')");
            }
        });
        ad.show(this, rewardItem -> { rewardEarned = true; });
    }

    // Exposed to the game as window.AndroidAds
    private class AdBridge {
        @JavascriptInterface
        public void showRewarded() {
            runOnUiThread(new Runnable() {
                @Override public void run() { MainActivity.this.showRewarded(); }
            });
        }
    }

    private void applyImmersiveMode() {
        if (gameView == null) return;
        gameView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameView = new WebView(this);
        gameView.setBackgroundColor(0xFF0A0E19);
        WebSettings settings = gameView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        gameView.setWebChromeClient(new WebChromeClient());
        gameView.setWebViewClient(new WebViewClient());
        gameView.addJavascriptInterface(new AdBridge(), "AndroidAds");
        setContentView(gameView);
        applyImmersiveMode();

        MobileAds.initialize(this, initializationStatus -> loadRewarded());

        // The game currently loads Three.js r128 from a HTTPS CDN.
        gameView.loadUrl("file:///android_asset/www/index.html");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) applyImmersiveMode();
    }

    @Override protected void onPause() {
        if (gameView != null) gameView.onPause();
        super.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        if (gameView != null) gameView.onResume();
        applyImmersiveMode();
    }

    @Override public void onBackPressed() {
        if (gameView != null && gameView.canGoBack()) gameView.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (gameView != null) {
            gameView.loadUrl("about:blank");
            gameView.stopLoading();
            gameView.destroy();
            gameView = null;
        }
        super.onDestroy();
    }
}

package com.android.wf_companion_app;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.wear.remote.interactions.RemoteActivityHelper;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.Executors;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;

public class MainActivity extends AppCompatActivity {

    private TextView watchStatus;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private MaterialButton openAppButton;
    private BottomNavigationView bottomNavigationView;
    private View coordinatorLayoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        coordinatorLayoutRoot = findViewById(android.R.id.content);

        watchStatus = findViewById(R.id.watch_status);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        handler = new Handler(Looper.getMainLooper());

        swipeRefreshLayout.setColorSchemeResources(R.color.md_theme_onPrimary);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.md_theme_primary);

        swipeRefreshLayout.setRefreshing(true);

        checkWatchConnection();
        handler.postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            showSnackbar();
        }, 1500);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            checkWatchConnection();

            handler.postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                showSnackbar();
            }, 1500);
        });

        openAppButton = findViewById(R.id.open_app_button);
        openAppButton.setEnabled(false);
        openAppButton.setAlpha(.5f);

        openAppButton.setOnClickListener(v -> {

            showOpenAppSnackbar();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("market://details?id=" + getPackageName()));

            RemoteActivityHelper helper = new RemoteActivityHelper(getApplicationContext(), Executors.newSingleThreadExecutor());

            Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            nodeListTask.addOnSuccessListener(nodes -> {
                for (Node node : nodes) {
                    if (node.isNearby()) {
                        String watchNodeId = node.getId();
                        helper.startRemoteActivity(intent, watchNodeId);
                    }
                }
            });
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.openDevPage) {
                String dev_id = getString(R.string.dev_id);
                String url = "https://play.google.com/store/apps/dev?id=" + dev_id;
                Uri uri = Uri.parse(url);
                Intent intentDevPage = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intentDevPage);
                return true;
            } else if (id == R.id.rateAppButton) {
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(goToMarket);
                }
                return true;
            } else if (id == R.id.policyButton) {
                String policyUrl = getString(R.string.policy_url);
                Intent intentPolicy = new Intent(Intent.ACTION_VIEW, Uri.parse(policyUrl));
                startActivity(intentPolicy);
                return true;
            } else {
                return false;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(swipeRefreshLayout, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, v.getPaddingBottom());

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float density = displayMetrics.density;
            int indicatorEndOffsetDp = 64;
            int indicatorEndOffsetPx = (int) (indicatorEndOffsetDp * density);

            swipeRefreshLayout.setProgressViewOffset(false, insets.top, insets.top + indicatorEndOffsetPx);

            return windowInsets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            lp.bottomMargin = insets.bottom;
            v.setLayoutParams(lp);
            return windowInsets;
        });

        checkWatchConnection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void checkWatchConnection() {

        Drawable inactiveIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.watch_off);
        Drawable activeIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.watch_on);

        Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
        nodeListTask.addOnSuccessListener(nodes -> {
            for (Node node : nodes) {
                if (node.isNearby()) {
                    handler.postDelayed(() -> {
                        watchStatus.setText(R.string.status_on);
                        openAppButton.setEnabled(true);
                        openAppButton.setAlpha(1f);
                        openAppButton.setIcon(activeIcon);
                    }, 1500);

                    return;
                }
            }
            handler.postDelayed(() -> {
                watchStatus.setText(R.string.status_off);
                openAppButton.setEnabled(false);
                openAppButton.setAlpha(.5f);
                openAppButton.setIcon(inactiveIcon);
            }, 1500);
        });
    }

    private void showSnackbar() {
        View snackbarParentView = coordinatorLayoutRoot;
        View visualAnchorView = bottomNavigationView;

        Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
        nodeListTask.addOnSuccessListener(nodes -> {
            for (Node node : nodes) {
                if (node.isNearby()) {
                    String displayName = node.getDisplayName();
                    String message = "Wearable connected: " + displayName;
                    Snackbar snackbar = Snackbar.make(snackbarParentView, message, Snackbar.LENGTH_LONG);
                    snackbar.setAnchorView(visualAnchorView);
                    snackbar.show();
                    return;
                }
            }
            Snackbar snackbar = Snackbar.make(snackbarParentView, "No Wear OS devices found.", Snackbar.LENGTH_LONG);
            snackbar.setAnchorView(visualAnchorView);
            snackbar.show();
        });
    }

    private void showOpenAppSnackbar() {
        View snackbarParentView = coordinatorLayoutRoot;
        View visualAnchorView = bottomNavigationView;

        Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
        nodeListTask.addOnSuccessListener(nodes -> {
            for (Node node : nodes) {
                if (node.isNearby()) {
                    Snackbar snackbar = Snackbar.make(snackbarParentView, "Check your watch.", Snackbar.LENGTH_LONG);
                    snackbar.setAnchorView(visualAnchorView);
                    snackbar.show();
                    return;
                }
            }
            Snackbar snackbar = Snackbar.make(snackbarParentView, "No Wear OS devices found.", Snackbar.LENGTH_LONG);
            snackbar.setAnchorView(visualAnchorView);
            snackbar.show();
        });
    }
}
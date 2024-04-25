package com.android.wf_companion_app;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    private TextView watchStatus;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private MaterialButton openAppButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        watchStatus = findViewById(R.id.watch_status);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        handler = new Handler();

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.openDevPage) {
                //PUT YOUR GOOGLE PLAY DEVELOPER ID HERE
                String url = "https://play.google.com/store/apps/dev?id=8530728375127376092";
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
            } else {
                return false;
            }
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
        Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
        nodeListTask.addOnSuccessListener(nodes -> {
            View swipeRefreshLayout = findViewById(R.id.bottom_navigation);
            for (Node node : nodes) {
                if (node.isNearby()) {
                    String displayName = node.getDisplayName();
                    String message = "Wearable connected: " + displayName;
                    Snackbar snackbar = Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_LONG);
                    snackbar.setAnchorView(swipeRefreshLayout);
                    snackbar.show();
                    return;
                }
            }
            Snackbar snackbar = Snackbar.make(swipeRefreshLayout, "No Wear OS devices found.", Snackbar.LENGTH_LONG);
            snackbar.setAnchorView(swipeRefreshLayout);
            snackbar.show();
        });
    }

    private void showOpenAppSnackbar() {
        Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
        nodeListTask.addOnSuccessListener(nodes -> {
            View swipeRefreshLayout = findViewById(R.id.bottom_navigation);
            for (Node node : nodes) {
                if (node.isNearby()) {
                    Snackbar snackbar = Snackbar.make(swipeRefreshLayout, "Check your watch.", Snackbar.LENGTH_LONG);
                    snackbar.setAnchorView(swipeRefreshLayout);
                    snackbar.show();
                    return;
                }
            }
            Snackbar snackbar = Snackbar.make(swipeRefreshLayout, "No Wear OS devices found.", Snackbar.LENGTH_LONG);
            snackbar.setAnchorView(swipeRefreshLayout);
            snackbar.show();
        });
    }
}

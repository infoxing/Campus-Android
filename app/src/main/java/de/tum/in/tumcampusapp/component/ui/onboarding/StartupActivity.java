package de.tum.in.tumcampusapp.component.ui.onboarding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import de.tum.in.tumcampusapp.BuildConfig;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.app.AuthenticationManager;
import de.tum.in.tumcampusapp.component.ui.overview.MainActivity;
import de.tum.in.tumcampusapp.service.DownloadService;
import de.tum.in.tumcampusapp.service.StartSyncReceiver;
import de.tum.in.tumcampusapp.utils.Const;
import de.tum.in.tumcampusapp.utils.Utils;
import io.fabric.sdk.android.Fabric;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Entrance point of the App.
 */
public class StartupActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 0;
    private static final String[] PERMISSIONS_LOCATION = {ACCESS_COARSE_LOCATION,
                                                          ACCESS_FINE_LOCATION};

    final AtomicBoolean initializationFinished = new AtomicBoolean(false);
    private int tapCounter; // for easter egg
    private boolean hasShownRationaleDialog;

    /**
     * Broadcast receiver gets notified if {@link de.tum.in.tumcampusapp.service.BackgroundService}
     * has prepared cards to be displayed
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.BROADCAST_NAME.equals(intent.getAction())) {
                openMainActivityIfInitializationFinished();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Only use Crashlytics if we are not compiling debug
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        initEasterEgg();

        new Thread(this::init).start();
    }

    private void initEasterEgg() {
        if (Utils.getSettingBool(this, Const.RAINBOW_MODE, false)) {
            ImageView tumLogo = findViewById(R.id.startupTumLogo);
            tumLogo.setImageResource(R.drawable.tum_logo_rainbow);
        }

        tapCounter = 0;
        View background = findViewById(R.id.container);
        background.setOnClickListener(view -> {
            tapCounter++;
            if (tapCounter % 3 == 0) {
                tapCounter = 0;

                // use the other logo and invert the setting
                boolean rainbowEnabled = Utils.getSettingBool(this, Const.RAINBOW_MODE, false);
                rainbowEnabled = !rainbowEnabled;
                ImageView tumLogo = findViewById(R.id.startupTumLogo);

                if (rainbowEnabled) {
                    tumLogo.setImageResource(R.drawable.tum_logo_rainbow);
                } else {
                    tumLogo.setImageResource(R.drawable.tum_logo_blue);
                }

                Utils.setSetting(this, Const.RAINBOW_MODE, rainbowEnabled);
            }
        });
        background.setSoundEffectsEnabled(false);
    }

    private void init() {
        // Migrate all settingsPrefix - we somehow ended up having two different shared prefs: join them back together
        Utils.migrateSharedPreferences(this.getApplicationContext());

        // Check that we have a private key setup in order to authenticate this device
        AuthenticationManager am = new AuthenticationManager(this);
        am.generatePrivateKey(null);

        // On first setup show remark that loading could last longer than normally
        runOnUiThread(() -> {
            ContentLoadingProgressBar progressBar = findViewById(R.id.startupLoadingProgressBar);
            progressBar.show();
        });

        // Register receiver for background service
        IntentFilter filter = new IntentFilter(DownloadService.BROADCAST_NAME);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        // Start background service and ensure cards are set
        Intent i = new Intent(this, StartSyncReceiver.class);
        i.putExtra(Const.APP_LAUNCHES, true);
        sendBroadcast(i);

        // Request Permissions for Android 6.0
        requestLocationPermission();
    }

    /**
     * Request the Location Permission
     */
    private void requestLocationPermission() {
        if (hasPermissions()) {
            openMainActivityIfInitializationFinished();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
    }

    private boolean shouldShowRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION);
    }

    private void showRationaleDialog() {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.permission_location_explanation))
                    .setPositiveButton(R.string.ok, (dialogInterface, id) -> {
                        ActivityCompat.requestPermissions(
                                this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                    })
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(
                        R.drawable.rounded_corners_background);
            }

            dialog.show();
        });
    }

    /**
     * Callback when the user allowed or denied Permissions
     * We do not care, if we got the permission or not, since the LocationManager needs to handle
     * missing permissions anyway
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (shouldShowRationale() && !hasShownRationaleDialog) {
                hasShownRationaleDialog = true;
                showRationaleDialog();
            } else {
                // Open MainActivity even if we did not get the location permission
                openMainActivityIfInitializationFinished();
            }
        }
    }

    private void openMainActivityIfInitializationFinished() {
        if (initializationFinished.compareAndSet(false, true) || isFinishing()) {
            // If the initialization process is not yet finished or if the Activity is
            // already being finished, there's no need to open MainActivity.
            return;
        }
        openMainActivity();
    }

    /**
     * Animates the TUM logo into place (left upper corner) and animates background up.
     * Afterwards {@link MainActivity} gets started
     */
    private void openMainActivity() {
        Intent intent = new Intent(StartupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister the BroadcastReceiver in onStop() (rather than onDestroy()),
        // so the BroadcastReceiver is unregistered when MainActivity.onCreate() is called
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

}

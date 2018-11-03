package de.tum.in.tumcampusapp.ui.onboarding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import java.net.UnknownHostException;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.tumonline.TUMOnlineClient;
import de.tum.in.tumcampusapp.api.tumonline.exception.InactiveTokenException;
import de.tum.in.tumcampusapp.ui.generic.activity.ProgressActivity;
import de.tum.in.tumcampusapp.model.person.Identity;
import de.tum.in.tumcampusapp.model.person.IdentitySet;
import de.tum.in.tumcampusapp.core.Const;
import de.tum.in.tumcampusapp.core.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WizNavCheckTokenActivity extends ProgressActivity<Void> {

    private Toast mToast;

    private Call<IdentitySet> mIdentityCall;

    public WizNavCheckTokenActivity() {
        super(R.layout.activity_wiznav_checktoken);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableRefresh();
    }

    /**
     * If next is pressed, check if token has been activated.
     *
     * @param next Next button handle
     */
    public void onClickNext(View next) {
        loadIdentitySet();
    }

    public void onClickTUMOnline(View next) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Const.TUM_CAMPUS_URL));
        startActivity(intent);
    }

    private void loadIdentitySet() {
        mToast = Toast.makeText(this, R.string.checking_if_token_enabled, Toast.LENGTH_LONG);
        mToast.show();

        mIdentityCall = TUMOnlineClient.getInstance(this).getIdentity();
        mIdentityCall.enqueue(new Callback<IdentitySet>() {
            @Override
            public void onResponse(@NonNull Call<IdentitySet> call,
                                   @NonNull Response<IdentitySet> response) {
                mToast.cancel();
                IdentitySet identitySet = response.body();
                if (identitySet != null) {
                    handleDownloadSuccess(identitySet);
                } else {
                    displayErrorToast(R.string.error_unknown);
                }
                mIdentityCall = null;
            }

            @Override
            public void onFailure(@NonNull Call<IdentitySet> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }

                handleDownloadFailure(t);
                mIdentityCall = null;
            }
        });
    }

    private void handleDownloadSuccess(@NonNull IdentitySet identitySet) {
        Identity identity = identitySet.getIds().get(0);
        Utils.setSetting(this, Const.CHAT_ROOM_DISPLAY_NAME, identity.toString());

        // Save the TUMonline ID to preferences
        Utils.setSetting(this, Const.TUMO_PIDENT_NR, identity.getObfuscated_ids()
                .getStudierende()); // Switch to identity.getObfuscated_id() in the future
        Utils.setSetting(this, Const.TUMO_STUDENT_ID, identity.getObfuscated_ids()
                .getStudierende());
        Utils.setSetting(this, Const.TUMO_EXTERNAL_ID, identity.getObfuscated_ids()
                .getExtern());
        Utils.setSetting(this, Const.TUMO_EMPLOYEE_ID, identity.getObfuscated_ids()
                .getBedienstete());
        if (!identity.getObfuscated_ids().getBedienstete().isEmpty()
                && identity.getObfuscated_ids().getStudierende().isEmpty()
                && identity.getObfuscated_ids().getExtern().isEmpty()) {
            Utils.setSetting(this, Const.EMPLOYEE_MODE, true);
        }

        // can't upload the obfuscated ids here since we might not have a (chat) member yet

        startActivity(new Intent(this, WizNavExtrasActivity.class));
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        finish();
    }

    private void handleDownloadFailure(Throwable t) {
        int messageResId;

        if (t instanceof UnknownHostException) {
            messageResId = R.string.no_internet_connection;
        } else if (t instanceof InactiveTokenException) {
            messageResId = R.string.error_access_token_inactive;
        } else {
            messageResId = R.string.error_unknown;
        }

        displayErrorToast(messageResId);
    }

    private void displayErrorToast(@StringRes int resId) {
        Utils.showToast(this, resId);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mIdentityCall != null) {
            mIdentityCall.cancel();
        }
    }
}

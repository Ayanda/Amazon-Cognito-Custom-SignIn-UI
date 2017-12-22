package com.ipragmatech.aws.cognito.cognitousersample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.amazonaws.mobile.auth.core.DefaultSignInResultHandler;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.core.IdentityProvider;
import com.amazonaws.mobile.auth.core.StartupAuthErrorDetails;
import com.amazonaws.mobile.auth.core.StartupAuthResult;
import com.amazonaws.mobile.auth.core.StartupAuthResultHandler;
import com.amazonaws.mobile.auth.core.signin.AuthException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.ipragmatech.aws.cognito.cognitousersample.R;
import com.ipragmatech.aws.cognito.cognitousersample.UILApplication;
import com.ipragmatech.aws.cognito.cognitousersample.utils.AppHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Eugen Kyrmyza
 */
public class StartActivity extends Activity implements StartupAuthResultHandler {
    private static final String LOG_TAG = "CognitoUserTAG";;
    private String username;

    //private static MobileAnalyticsManager analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final IdentityManager identityManager = IdentityManager.getDefaultIdentityManager();
        identityManager.resumeSession(this,this);

    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onComplete(StartupAuthResult authResult) {
        final IdentityManager identityManager = authResult.getIdentityManager();

        if (authResult.isUserSignedIn()) {
            final IdentityProvider provider = identityManager.getCurrentIdentityProvider();
            // If we were signed in previously with a provider indicate that to the user with a toast.
            getDetails();
        } else {

            final StartupAuthErrorDetails errors = authResult.getErrorDetails();

            if (errors.didErrorOccurRefreshingProvider()) {
                final AuthException providerAuthException = errors.getProviderRefreshException();
                Log.w(LOG_TAG, String.format(
                        "Credentials for Previously signed-in provider %s could not be refreshed.",
                        providerAuthException.getProvider().getDisplayName()), providerAuthException);
            }

            doMandatorySignIn(identityManager);
            return;
        }
    }


    private void doMandatorySignIn(final IdentityManager identityManager) {
        final WeakReference<StartActivity> self = new WeakReference<StartActivity>(this);

        identityManager.login(this, new DefaultSignInResultHandler() {

            @Override
            public void onSuccess(Activity activity, IdentityProvider identityProvider) {
                // User has signed in
                Log.e("NotError", "User signed in");
                Activity callingActivity = self.get();
                getDetails();
            }

            @Override
            public boolean onCancel(Activity activity) {
                // This
                return false;
            }
        });
        SignInActivity.startSignInActivity(this, UILApplication.sAuthUIConfiguration);
    }


    // Implement callback handler for getting details
    GetDetailsHandler getDetailsHandler = new GetDetailsHandler() {
        @Override
        public void onSuccess(CognitoUserDetails cognitoUserDetails) {
            // The user detail are in cognitoUserDetails
            AppHelper.setUserDetails(cognitoUserDetails);
            String displayName="";
            if(AppHelper.getItemCount()>0) {
                displayName = String.format("%s",AppHelper.getItemForDisplay(0).getDataText());

            }else{
               displayName=username;
            }
            Toast.makeText(StartActivity.this, String.format(getString(R.string.sign_in_message),
                    displayName), Toast.LENGTH_LONG).show();
            startActivity(new Intent(StartActivity.this,MainActivity.class));
        }

        @Override
        public void onFailure(Exception exception) {
            //closeWaitDialog();
            // Fetch user details failed, check exception for the cause
            Log.e(LOG_TAG,"Failed to fetch user details "+ exception.getMessage());
        }
    };

    // Get user details from CIP service
    private void getDetails() {
        //showWaitDialog("Signing in...");
        username = AppHelper.getPool().getCurrentUser().getUserId();
        if(username!=null) {
            AppHelper.getPool().getUser(username).getDetailsInBackground(getDetailsHandler);
        }else{
            Toast.makeText(this,"Unable to fetch user details", Toast.LENGTH_LONG).show();
        }
    }


}


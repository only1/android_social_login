package com.only1.demo_oauth_sdk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Google
    private static final String GOOGLE_WEB_CLIENT_ID = "78937794485-7un7kmbi1jkuocvjb2pi3io9cm7pb45r.apps.googleusercontent.com";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient = null;


    // Facebook
    private CallbackManager mCallbackManager = null;


    // UI Class
    private TextView mLogText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //////////////////////////////
        // Google
        //////////////////////////////
        initGoogle();


        //////////////////////////////
        // Facebook
        //////////////////////////////
        initFacebook();


        //////////////////////////////
        // Common
        //////////////////////////////
        mLogText = (TextView)findViewById(R.id.log_text);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google Login
        // Result returned from launching the Intent
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


        // Facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    ////////////////////////////////////////////////////////
    //
    // private methods (Google)
    //
    ////////////////////////////////////////////////////////
    private void initGoogle() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                .build();

        // Build a GoogleApiClient with access to the Google Sign-in API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "[Google::onConnectionFailed] Occurred >>> " + connectionResult.isSuccess());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.google_sign_in).setOnClickListener(this);
        findViewById(R.id.google_sign_out).setOnClickListener(this);
        findViewById(R.id.google_disconnect).setOnClickListener(this);
    }

    private void signIn() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "[Google::handleSignInResult] result : " + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "[Google::handleSignInResult] acct.toString() : " + acct.toString());


            StringBuffer strBuffer = new StringBuffer("acct >>> ");
            strBuffer
                    .append("DisplayName : " + acct.getDisplayName() + "\n")
                    .append("Email : " + acct.getEmail() + "\n")
                    .append("Id : " + acct.getId() + "\n")
                    .append("IdToken : " + acct.getIdToken() + "\n")
                    .append("ServerAuthCode : " + acct.getServerAuthCode() + "\n")
                    .append("GrantedScopes : " + acct.getGrantedScopes().toString() + "\n");
            mLogText.setText(strBuffer);

        } else {
            // Signed out, show unauthenticated UI
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "[Google::signOut] callback : " + status.toString());
                    }
                }
        );
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "[Google::revokeAccess] callback : " + status.toString());
                    }
                }
        );
    }


    ////////////////////////////////////////////////////////
    //
    // private methods (Facebook)
    //
    ////////////////////////////////////////////////////////
    private void initFacebook() {
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        //FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        ((LoginButton)findViewById(R.id.facebook_login)).setReadPermissions("user_friends", "email");
        // (1) type : register callback to "login-button" ----> thi is applied!!
        // (2) type : register callback to "LoginManager"
        ((LoginButton)findViewById(R.id.facebook_login)).registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "[Facebook::onSuccess] " + loginResult.toString());
                Log.d(TAG, "[Facebook::onSuccess] AccessToken : " + loginResult.getAccessToken());
                Log.d(TAG, "[Facebook::onSuccess] GrantedPermissions : " + loginResult.getRecentlyGrantedPermissions());
                Log.d(TAG, "[Facebook::onSuccess] DeniedPermissions : " + loginResult.getRecentlyDeniedPermissions());
                showCurrentLogInInformation();
                queryInformationToFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "[Facebook::onCancel] ");
                showCurrentLogInInformation();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "[Facebook::onError] ");
                showCurrentLogInInformation();
            }
        });
    }

    private void showCurrentLogInInformation() {
        try {
            Log.d(TAG, "[Facebook::showCurrentLogInInformation] AccessToken : " + AccessToken.getCurrentAccessToken().toString());
            Log.d(TAG, "[Facebook::showCurrentLogInInformation] Profile(id) : " + Profile.getCurrentProfile().getId());
            Log.d(TAG, "[Facebook::showCurrentLogInInformation] Profile(Name) : " + Profile.getCurrentProfile().getName());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void queryInformationToFacebook(AccessToken token) {
        new AsyncTask<AccessToken, Void, Void>() {
            @Override
            protected Void doInBackground(AccessToken... params) {
                GraphRequest request = GraphRequest.newMeRequest(params[0], new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d(TAG, "[Facebook::onCompleted] response : " + response.toString());
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAndWait();
                return null;
            }
        }.execute(token);
    }

    private void logIn() {
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        LoginManager.getInstance().logInWithReadPermissions(this, null);
    }


    ////////////////////////////////////////////////////////
    //Common
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.google_sign_in:
                signIn();
                break;
            case R.id.google_sign_out:
                signOut();
                break;
            case R.id.google_disconnect:
                revokeAccess();
                break;
            case R.id.facebook_login:
                showCurrentLogInInformation();
                logIn();
                break;
        }
    }
}

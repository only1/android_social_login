package com.only1.demo_oauth_sdk;

import android.content.Intent;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String GOOGLE_WEB_CLIENT_ID = "78937794485-7un7kmbi1jkuocvjb2pi3io9cm7pb45r.apps.googleusercontent.com";
    private static final int RC_SIGN_IN = 9001;

    // Login Class
    private GoogleApiClient mGoogleApiClient = null;

    // UI Class
    private TextView mLogText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                .build();

        // Build a GoogleApiClient with access to the Google Sign-in API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.google_sign_in).setOnClickListener(this);
        findViewById(R.id.google_sign_out).setOnClickListener(this);
        findViewById(R.id.google_disconnect).setOnClickListener(this);

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

        // Result returned from launching the Intent
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    ////////////////////////////////////////////////////////
    //
    // private methods
    //
    ////////////////////////////////////////////////////////
    private void signIn() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "[handleSignInResult] result : " + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "[handleSignInResult] acct.toString() : " + acct.toString());


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
                        Log.d(TAG, "[signOut] callback : " + status.toString());
                    }
                }
        );
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "[revokeAccess] callback : " + status.toString());
                    }
                }
        );
    }


    ////////////////////////////////////////////////////////
    //
    // related to "implements interface"
    //
    ////////////////////////////////////////////////////////

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

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
        }
    }
}

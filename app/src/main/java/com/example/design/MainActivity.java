package com.example.design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView signup;
    private EditText in_mail,in_pass;
    private String str_in_mail,str_in_pass;
    private Button login;
    private ImageButton imageButton;
    private LoginButton fb_login_button;
    private static final int RC_SIGN_IN = 321;
    public static GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mfirebaseAuth;
    private FirebaseUser mfirebaseUser;
    private ConstraintLayout constraintLayoutLogin;
    private ProgressDialog progressDialog1;
    private CallbackManager callbackManager;

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        mfirebaseAuth = FirebaseAuth.getInstance();
        if(mfirebaseAuth.getCurrentUser() != null)
        {
           mfirebaseUser = mfirebaseAuth.getCurrentUser();
        }
        updateUI(account,mfirebaseUser);
    }

    private void updateUI(GoogleSignInAccount account,FirebaseUser mFirebaseUser) {
        if(account != null){
            startActivity(new Intent(getApplicationContext(), Prayer.class));
            finish();
        }
        else
        {
            if(mFirebaseUser != null)
            {
                startActivity(new Intent(getApplicationContext(),Prayer.class));
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.button);
        signup = findViewById(R.id.textView10);
        imageButton = findViewById(R.id.imageButton4);
        fb_login_button = findViewById(R.id.fb_login_button);
        in_mail = findViewById(R.id.editTextTextEmailAddress);
        in_pass = findViewById(R.id.editTextTextPassword);
        constraintLayoutLogin = findViewById(R.id.background);

        mfirebaseAuth = FirebaseAuth.getInstance();

        progressDialog1 = new ProgressDialog(this);
        progressDialog1.setMessage("Loading  \n Please Wait...");
        progressDialog1.setCancelable(false);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        fb_login_button.setPermissions("email");
        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fun_login();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });

        fb_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("fb_login","onSuccess"+loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void handleFacebookToken(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mfirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mfirebaseAuth.getCurrentUser();
                    updateUI(null,user);
                }
            }
        });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account,null);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("eTAG", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null,null);
        }
    }

    private void fun_login(){
        str_in_mail = in_mail.getText().toString().trim();
        str_in_pass = in_pass.getText().toString().trim();

        if(TextUtils.isEmpty(str_in_mail)){ in_mail.setError("Email is Required"); return; }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(str_in_mail).matches()) { in_mail.setError("invalid Email address");return; }
        if(TextUtils.isEmpty(str_in_pass)){ in_pass.setError("Please Enter the Password "); return;}
        if(str_in_pass.length() < 6) {in_pass.setError("Password must be minimum of 6 characters"); return; }
        progressDialog1.show();

        mfirebaseAuth.signInWithEmailAndPassword(str_in_mail,str_in_pass)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mfirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            Snackbar.make(constraintLayoutLogin,"Login SuccessFully",Snackbar.LENGTH_SHORT).show();
                            progressDialog1.dismiss();
                            updateUI(null,mfirebaseUser);
                        }
                        else{
                            Snackbar.make(constraintLayoutLogin,"Login Failed Please Enter Correct Email or Password",Snackbar.LENGTH_SHORT).show();
                            progressDialog1.dismiss();
                        }

                    }
                });

    }
}
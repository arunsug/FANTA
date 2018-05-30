package com.example.android.projectfanta;

import android.content.Intent;
import android.icu.text.IDNA;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.ObjectInputStream;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private SignInButton SignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    public static final int RC_SIGN_IN = 1;
    public static final int ACCOUNT_INFO_FILLED = 2;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        SignIn = (SignInButton) findViewById(R.id.sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignIn.setOnClickListener( getGoogleSignIn() );
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // If user is signed in on start-up
        if (currentUser != null) {

            // Part 2: Update DB using Information object IF connected to internet?? ARUN

            // NOT SURE IF THIS LINE STILL NEEDS TO EXIST
            handleUsers(currentUser);

            Intent calcIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivityForResult(calcIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            // read
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void handleUsers(FirebaseUser user) {

        final DatabaseReference singleUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        final String userid = user.getUid();
        final Callback readCallBack = new Callback() {
            @Override
            public void onComplete(Object o) {
                Information.information = (Information)o;
                Intent calcIntent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivityForResult(calcIntent, RC_SIGN_IN);
            }
        };

        singleUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // If user does not have an account
                // JOYAAN #3
                if (!snapshot.exists()) {
                    // TODO REQUEST USER INFO TO SET USER VALUES CORRECTLY
                    String username = "Baby john doe";
                    UserInfo ui = new UserInfo(userid, username, 10, 8, 0);
                    User u = new User(userid, username);
                    DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
                    Information.information = new Information(ui, u);
                    mData.child("users").child(userid).setValue(u);
                    mData.child(userid).setValue(Information.information);
                    Intent calcIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivityForResult(calcIntent, RC_SIGN_IN);
                } else {
                    // JOYAAN #2
                    FirebaseUser user = mAuth.getCurrentUser();
                    Information.read(user.getUid(), readCallBack);
                }

                // Write Information Object to memory
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * moves you to the home activity on succsesful sign in
     * @param completedTask
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    /**
     * move you to homeActivity when sign-in is successful
     * @param acct
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            handleUsers(user);
                            Intent calcIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivityForResult(calcIntent, RC_SIGN_IN);
                        } else {
                            // TODO If sign in fails, display a message to the user.
                        }
                    }
                });
    }

    public View.OnClickListener getGoogleSignIn(){
        return new View.OnClickListener(){
            @Override
             public void onClick(View v) {
                 if(NetworkStatus.getInstance(getApplicationContext()).isOnline()){
                     Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                     startActivityForResult(signInIntent, RC_SIGN_IN);
             }else {
                 Toast toast = Toast.makeText(getApplicationContext(),
                         "No Internet Connection!", Toast.LENGTH_SHORT);
                 toast.show();
             }
         }
        };
    }


    /*public void onClick(View v) {
        if(NetworkStatus.getInstance(getApplicationContext()).isOnline()) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No Internet Connection!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }*/
}

package com.example.design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import java.util.HashMap;

public class Register extends AppCompatActivity {
    private TextView signin;
    private Button signup2;
    private RadioGroup radioGroup;
    private RadioButton radioButton1,radioButton2;
    private EditText full_name,last_name,up_email,pno,up_Pass1,up_Pass2;
    private FirebaseAuth firebaseAuth;
    private String str_radio_value,str_full_name,str_last_name,str_pno,str_up_email,str_up_pass1,str_up_pass2;
    private ConstraintLayout constraintLayout_SignUp;
    private HashMap name;
    private ProgressDialog progressDialog2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        constraintLayout_SignUp = findViewById(R.id.cl_signUp);

        name = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();

        radioGroup = findViewById(R.id.rGroup);
        radioButton1 = findViewById(R.id.radioButton);
        radioButton2 = findViewById(R.id.radioButton2);
        full_name = findViewById(R.id.editTextTextPersonName);
        last_name = findViewById(R.id.editTextTextPersonName2);
        up_email = findViewById(R.id.editTextTextEmailAddress2);
        pno = findViewById(R.id.editTextPhone);
        up_Pass1 = findViewById(R.id.etPassword);
        up_Pass2 = findViewById(R.id.editTextTextPassword3);
        signup2 = findViewById(R.id.button2);
        signin = findViewById(R.id.textView17);

        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Loading  \n Please Wait...");
        progressDialog2.setCancelable(false);

        signup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                int selectedId = 0;
                selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                if(selectedId == 0){
                    Snackbar.make(constraintLayout_SignUp, "Please Select User or Provider from above", Snackbar.LENGTH_SHORT).show();
                }
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                str_radio_value = radioButton.getText().toString().trim();
                str_full_name   = full_name.getText().toString().trim();
                str_last_name   = last_name.getText().toString().trim();
                str_up_email    = up_email.getText().toString().trim();
                str_pno         = pno.getText().toString().trim();
                str_up_pass1    = up_Pass1.getText().toString().trim();
                str_up_pass2    = up_Pass2.getText().toString().trim();

                if(TextUtils.isEmpty(str_full_name)){ full_name.setError("Please Enter Your First Name"); return;}
                if(TextUtils.isEmpty(str_last_name)){ last_name.setError("Please Enter Your Last Name"); return;}
                if(TextUtils.isEmpty(str_up_email)){ up_email.setError("Email is Required"); return; }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(str_up_email).matches()) { up_email.setError("invalid Email address");return; }
                if(TextUtils.isEmpty(str_pno)){ pno.setError("Please Enter Your Phone-Number"); return;}
                if(TextUtils.isEmpty(str_up_pass1)){ up_Pass1.setError("Please Enter the Password "); return;}
                if(TextUtils.isEmpty(str_up_pass2)){ up_Pass2.setError("Please Enter the Password Here also"); return;}
                if(str_up_pass1.length() < 6) {up_Pass1.setError("Password must be minimum of 6 characters"); return; }
                if(!str_up_pass2.equals(str_up_pass1)){up_Pass2.setError("Passwords Are Not Same Please Enter the Same Passwords");return;}
                name.put("First_Name",str_full_name);
                name.put("Last_Name",str_last_name);
                name.put("E-mail",str_up_email);
                name.put("Phone-number",str_pno);
                name.put("password",str_up_pass2);
                progressDialog2.show();

                firebaseAuth.createUserWithEmailAndPassword(str_up_email,str_up_pass2)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference(str_radio_value)
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Snackbar.make(constraintLayout_SignUp, "Account Created SuccessFully", Snackbar.LENGTH_SHORT).show();
                                                progressDialog2.dismiss();
                                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                finish();
                                            }
                                            else{
                                                Snackbar.make(constraintLayout_SignUp, "Some Error! in Database ", Snackbar.LENGTH_SHORT).show();
                                                progressDialog2.dismiss();
                                            }
                                        }
                                    });

                                }
                                else{
                                    Snackbar.make(constraintLayout_SignUp, "Some Error! in Server", Snackbar.LENGTH_SHORT).show();
                                    progressDialog2.dismiss();
                                }
                            }
                        });

            }

        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}
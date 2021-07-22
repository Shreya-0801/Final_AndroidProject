package com.example.note_trial;
//MainActivity is the login page.Here, the user can login.
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note_trial.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class MainActivity extends AppCompatActivity {

    //Initializing the ID's
    private EditText mloginemail,mloginpassword;
    private RelativeLayout mlogin;
    // To save email and password of the user,we are using firebase auth(authentication).
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        //We are assigning xml id's to java variables
        mloginemail=findViewById(R.id.loginemail);
        mloginpassword=findViewById(R.id.loginpassword);
        mlogin=findViewById(R.id.login);

        // We are obtaining an instance of firebaseAuth class
        firebaseAuth=FirebaseAuth.getInstance();
        //getCurrentUser() returns the currently logged in user in firebase
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        //If the user is logged in,then firebaseUser!=null. So, the user is directly navigated to the Notesactivity
        if(firebaseUser!=null)
        {
            startActivity(new Intent(MainActivity.this,NotesActivity.class));
        }

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=mloginemail.getText().toString().trim();
                String password=mloginpassword.getText().toString().trim();

                if(mail.isEmpty()|| password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"All Field Are Required",Toast.LENGTH_SHORT).show();
                }
                else
                {//Here it is taking mail and password and checking it with firebase authentication.
                    firebaseAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                //If user has successfully logged in, it will give a toast msg Logged in.
                                Toast.makeText(getApplicationContext(),"Logged In",Toast.LENGTH_SHORT).show();
                                finish();
                                // The page will move from MainActivity to NotesActivity.
                                startActivity(new Intent(MainActivity.this,NotesActivity.class));

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Account Doesn't Exist",Toast.LENGTH_SHORT).show();

                            }


                        }
                    });




                }
            }
        });

    }


    public void reset(View view) {
        //AlertDialog can be used to display the dialog message with Reset and Cancel buttons.
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        //View will display the editText
        View v = LayoutInflater.from(this).inflate(R.layout.reset,null,false);
        EditText m = v.findViewById(R.id.reemail);
        b.setView(v);
        //setCancelable(false) means that back key doesn't close the dialog.
        b.setCancelable(false);
        b.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = m.getText().toString();
                if (s.isEmpty()){
                    m.setError("Can't be Empty");
                }else{
                    firebaseAuth.sendPasswordResetEmail(s).addOnCompleteListener(MainActivity.this,
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Reset Mail sent",
                                                Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MainActivity.this, "failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.show();

    }


    public void Signup(View view) {

        startActivity(new Intent(this,RegisterActivity.class));
    }

}
package com.example.note_trial;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.note_trial.databinding.ActivityCreateNoteBinding;
import com.example.note_trial.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateNote extends AppCompatActivity {
    //Initializing the ID's
    EditText createTitle,createContent;
    FloatingActionButton saveNote;
    // To save particular id for particular person we are using firebase authentication.
    FirebaseAuth auth;
    // To identify the user we are using firebase user
    FirebaseUser user;
    //To access the database we need FirebaseDatabase
    FirebaseDatabase database;
    Model model;
    /*FirebaseStorage storage;
    StorageReference storageReference;*/

   /* private static final int REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int REQUEST_CODE_SELECT_IMAGE =2;
    private ImageView imageView;
    private String selectedImagePath;*/



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        Toolbar toolbar=findViewById(R.id.toolbarofcreatenote);
        setSupportActionBar(toolbar);
        //To display return button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Assigning the ID's
        createTitle=findViewById(R.id.createtitleofnote);
        createContent=findViewById(R.id.createcontentofnote);
        /*imageView=findViewById(R.id.image);
         selectedImagePath="";*/
        //storing the Instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        /*storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();*/
        //It will give the instance of current user.
        user =FirebaseAuth.getInstance().getCurrentUser();
        //Here we are creating a string userid. So, that every user will get a particular id.
        String userId= user.getUid();
        saveNote=findViewById(R.id.savenote);
        //we  are creating a set Onclick listener so that, when we click save note it will show an action.
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Here we are creating a string and fetching the data which is entered by the user.
                String title = createTitle.getText().toString();
                String content = createContent.getText().toString();
                //Here we are giving the condition if any of the field(title or content) is empty then it will give a toast message Fields cannot be empty.
                if(title.isEmpty() || content.isEmpty())
                {
                    Toast.makeText(CreateNote.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    //Reference
                    // Here we are creating the object of document reference because we are storing the data in Firebase database using document reference.
                    // By writing userid we will get id of the current user and we can easily store the data of that particular user
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes/"+userId);
                    Map<String ,Object> note= new HashMap<>();
                    //  Here we are passing the data to note
                    note.put("title",title);
                    note.put("content",content);
                    // here we are pushing the data to reference.
                    reference.push().setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(CreateNote.this, "Note created successfully!", Toast.LENGTH_SHORT).show();
                            //If note created successfully then we will move from CreateNote to NotesActivity.
                            startActivity(new Intent(CreateNote.this,NotesActivity.class));

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //If note not created successfully then it will show a msg that Failed to create Note.
                                    Toast.makeText(CreateNote.this, "Failed To Create Note", Toast.LENGTH_SHORT).show();



                                }
                            });
                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /*public void addImage(View view) {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                    CreateNote.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION
            );
        }
        else
        {
            selectImage();
        }

    }
    private void selectImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                selectImage();
            }
            else
            {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_SELECT_IMAGE && resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                Uri selectedImageUri =data.getData();
                if(selectedImageUri!=null)
                {
                    try {
                        InputStream inputStream=getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                        StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
                        ref.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(CreateNote.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                String url = selectedImagePath;
                                model.setUrl(url);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateNote.this, "failed", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    private  String getPathFromUri(Uri contentUri)
    {
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri,null,null,null,null);
        if(cursor==null)
        {
            filePath=contentUri.getPath();
        }
        else
        {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("data");
            filePath=cursor.getString(index);
            cursor.close();

        }
        return filePath;
    }*/
}
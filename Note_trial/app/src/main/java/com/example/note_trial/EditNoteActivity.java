package com.example.note_trial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class EditNoteActivity extends AppCompatActivity {
Intent data;
EditText noteTitle,noteContent;
FloatingActionButton saveEdit;
FirebaseDatabase database;
FirebaseUser user;

    FirebaseStorage storage;
    StorageReference storageReference;

    private static final int REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int REQUEST_CODE_SELECT_IMAGE =2;
    private ImageView imageView;
    //private String selectedImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        imageView=findViewById(R.id.image);
        //selectedImagePath="";
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        noteTitle=findViewById(R.id.edittitleofnote);
        noteContent=findViewById(R.id.editcontentofnote);
        saveEdit=findViewById(R.id.saveeditnote);
//Retrieving the data that we stored in intent(from Notes Activity)
        data=getIntent();
        database = FirebaseDatabase.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        String userId= user.getUid();

        Toolbar toolbar=findViewById(R.id.toolbarofeditnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //we  are creating a set Onclick listener so that, when we click save note it will show an action.
        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newtitle=noteTitle.getText().toString();
                String newcontent=noteContent.getText().toString();

                if(newtitle.isEmpty()||newcontent.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Something is empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    //We are retrieving the noteId and storing it in the form of a string.
                    String noteID = data.getStringExtra("noteID");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes/"+userId);
                    Map<String ,Object> note= new HashMap<>();
                    note.put("title",newtitle);
                    note.put("content",newcontent);
                    //here, we are updating the data in firebase
                    reference.child(noteID).setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditNoteActivity.this, "Note has been updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditNoteActivity.this,NotesActivity.class));

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditNoteActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();



                                }
                            });
                }

            }
        });
        //Here, we are creating two more new strings and storing the data that we have just updated.
        String notetitle=data.getStringExtra("title");
        String notecontent=data.getStringExtra("content");
        //Here, we are displaying the data stored in strings.
        noteContent.setText(notecontent);
        noteTitle.setText(notetitle);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    public void addImage(View view) {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                    EditNoteActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                        //selectedImagePath = getPathFromUri(selectedImageUri);
                        StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
                        ref.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(EditNoteActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditNoteActivity.this, "failed", Toast.LENGTH_SHORT).show();

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
    }
}



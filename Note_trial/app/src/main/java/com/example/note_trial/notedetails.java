//This activity displays a note i.e. title and content
package com.example.note_trial;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class notedetails extends AppCompatActivity {


    TextView mtitleofnotedetail,mcontentofnotedetail;
    FloatingActionButton mgotoeditnote;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To connect with xml
        setContentView(R.layout.activity_notedetails);
        mtitleofnotedetail=findViewById(R.id.titleofnotedetail);
        mcontentofnotedetail=findViewById(R.id.contentofnotedetail);
        mgotoeditnote=findViewById(R.id.gotoeditnote);
        //imageView=findViewById(R.id.imageView);
        Toolbar toolbar=findViewById(R.id.toolbarofnotedetail);
        setSupportActionBar(toolbar);
        //setDisplayHomeAsUpEnabled(true) will make the icon clickable and add the < at the left of the icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data=getIntent();

        mgotoeditnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(v.getContext(),EditNoteActivity.class);
                intent.putExtra("title",data.getStringExtra("title"));
                intent.putExtra("content",data.getStringExtra("content"));
                intent.putExtra("noteID",data.getStringExtra("noteID"));
                v.getContext().startActivity(intent);

            }
        });
        //If we do not perform update process,it will display the title and content
        mcontentofnotedetail.setText(data.getStringExtra("content"));
        mtitleofnotedetail.setText(data.getStringExtra("title"));
      //  imageView.setImageBitmap(BitmapFactory.decodeFile(data.));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
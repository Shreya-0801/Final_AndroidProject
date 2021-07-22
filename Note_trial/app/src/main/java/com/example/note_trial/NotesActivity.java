package com.example.note_trial;
//NotesActivity is responsible for displaying all the notes saved by the user.
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.example.note_trial.databinding.ActivityNotesBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {
    // Declaring the variables
    FloatingActionButton createNote;
    private FirebaseAuth auth;
    // RecyclerView is a container used for displaying large amount of data sets that can be scrolled very efficiently by maintaining a limited number of views.
    RecyclerView recyclerView;
    // Staggered grid layout manager automatically sets the item views in a grid according to its height and width.
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    //FirebaseUser Represents a user's profile information in your Firebase project's user database.
    FirebaseUser firebaseUser;
    //FirebaseDatabase allows to read and write user-specific and public data from and to the Firebase Realtime Database.
    FirebaseDatabase database;
    //A Firebase reference represents a particular location in your Database and can be used for reading or writing data to that Database location
    DatabaseReference reference;
    //The FirebaseRecyclerAdapter binds a Query to a RecyclerView. When data is added, removed, or changed these updates are automatically applied to your UI in real time.
    FirebaseRecyclerAdapter<Model,NoteViewHolder> noteAdapter;
    //Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        auth=FirebaseAuth.getInstance();
        //It will give the instance of the current user
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        //Here, we are creating a string userId and storing the ID.
        String userId= firebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        //the action bar displays the title for the activity on one side and a menu on the other side.
        getSupportActionBar().setTitle("All Notes");
        //Background colour for Action bar
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.shape1));
        createNote=findViewById(R.id.createnotefab);

        createNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this,CreateNote.class));
            }
        });

        reference= FirebaseDatabase.getInstance().getReference("notes/"+userId);
        //Here, we are connecting reference and model.
        FirebaseRecyclerOptions<Model> allusernotes = new FirebaseRecyclerOptions.Builder<Model>().setQuery(reference,Model.class).build();
        //We are creating an adapter "noteAdapter" to edit and delete the notes.
        noteAdapter = new FirebaseRecyclerAdapter<Model, NotesActivity.NoteViewHolder>(allusernotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NotesActivity.NoteViewHolder holder, int position, @NonNull Model model) {
               ImageView popupbutton = holder.itemView.findViewById(R.id.menupopbutton);
                int colourcode = getRandomColor();
                //A ViewHolder describes an item view and metadata about its place within the RecyclerView.
                holder.mnote.setBackgroundColor(holder.itemView.getResources().getColor(colourcode,null));
                holder.notetitle.setText(model.getTitle());
                holder.notecontent.setText(model.getContent());



                // holder.layoutimage.setImageResource(model.getImage());

                String docId=noteAdapter.getRef(position).getKey();




                //When we click on a note that we have created, this action will take place.
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(v.getContext(),notedetails.class);
                        intent.putExtra("title",model.getTitle());
                        intent.putExtra("content",model.getContent());
                        intent.putExtra("noteID",docId);
                        // intent.putExtra("image",Uri.parse(model.getImage()));
                        // intent.putExtra("image",);

                        v.getContext().startActivity(intent);
                    }
                });

                //We will get two options- edit and delete.
                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setGravity(Gravity.END);
                        //When we click on edit, the following action takes place.
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent(v.getContext(), EditNoteActivity.class);
                                intent.putExtra("title", model.getTitle());
                                intent.putExtra("content", model.getContent());
                                intent.putExtra("noteID", docId);
                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });
                        //When we click on delete, the following action takes place.
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
                                builder.setTitle(R.string.app_name);
                                builder.setIcon(R.mipmap.ic_launcher);
                                builder.setMessage("Do you want to delete?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes/"+userId);
                                                reference.child(docId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(NotesActivity.this, "This note is deleted", Toast.LENGTH_SHORT).show();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull  Exception e) {
                                                        Toast.makeText(NotesActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();


                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });




            }


            @NonNull
            @Override
            //NoteViewHolder will display notes layout.xml
            public NoteViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NotesActivity.NoteViewHolder(view);
            }
        };
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView notetitle;
        TextView notecontent;
        ImageView layoutimage;
        LinearLayout mnote;
        int imagevalue;
        public NoteViewHolder(@NonNull  View itemView) {
            super(itemView);
            notetitle=itemView.findViewById(R.id.notetitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            mnote=itemView.findViewById(R.id.note);
            //layoutimage=itemView.findViewById(R.id.layoutimage);

/*

            Bundle bundle = getIntent().getExtras();

            // if bundle is not null then get the image value
            if (bundle != null) {
                imagevalue = bundle.getInt("image");
            }
            layoutimage.setImageResource(imagevalue);
*/

        }
    }
    //To get menu bar on action bar(log out)
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    //When we click on log out, this action will take place
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.logout:

                AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage("Do you want to Logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                auth.signOut();
                                finish();
                                startActivity(new Intent(NotesActivity.this,MainActivity.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();



        }

        return super.onOptionsItemSelected(item);

    }

    //The FirebaseRecyclerAdapter uses an event listener to monitor changes to the Firebase query. To begin listening for data, call the startListening() method. You may want to call this in your onStart() method. Make sure you have finished any authentication necessary to read the data before calling startListening() or your query will fail.
    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }
    //the stopListening() call removes the event listener and all data in the adapter. Call this method when the containing Activity or Fragment stops.
    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null)
        {
            noteAdapter.stopListening();
        }
    }
    private int getRandomColor() {
        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.green);

        Random random=new Random();
        int number=random.nextInt(colorcode.size());
        return colorcode.get(number);
    }

}
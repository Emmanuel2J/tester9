package com.example.tester;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class community_chat extends AppCompatActivity {

    // Declare database reference
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<ChatMessage, ChatAdapter.ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String email = getIntent().getStringExtra("email");
        setContentView(R.layout.activity_community_chat);

        // Initialize database reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child("messages");

        // Log the initial state of the data
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("InitialDataSnapshot", "Value: " + dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("InitialDataSnapshot", "Failed to read value.", databaseError.toException());
            }
        });

        // RecyclerView to display chat messages
        RecyclerView chatRecyclerView = findViewById(R.id.chatRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);

        // Configure FirebaseRecyclerOptions
        Query query = mDatabase;
        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .build();

        // Initialize FirebaseRecyclerAdapter
        adapter = new FirebaseRecyclerAdapter<ChatMessage, ChatAdapter.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position, @NonNull ChatMessage model) {
                holder.bindData(model);
            }

            @NonNull
            @Override
            public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
                return new ChatAdapter.ViewHolder(view);
            }
        };

        chatRecyclerView.setAdapter(adapter);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText messageEditText = findViewById(R.id.messageEditText);
                String message = messageEditText.getText().toString().trim();

                // Push message to Firebase Realtime Database
                // Generate a unique key for the message
                String messageId = mDatabase.push().getKey();

                // Create a HashMap to hold the message data
                HashMap<String, Object> messageMap = new HashMap<>();
                messageMap.put("sender_email", email); // Assuming 'email' is the sender's email
                messageMap.put("message_text", message);
                messageMap.put("timestamp", ServerValue.TIMESTAMP); // Optional: Include timestamp

                // Push the message data to the database
                if (messageId != null) {
                    mDatabase.child(messageId).setValue(messageMap);
                }

                // Clear EditText after sending message
                messageEditText.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

package com.example.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class WhatsAppChatActivity extends AppCompatActivity implements View.OnClickListener {

    //    private EditText edtSendText;
    private Button btnSendMessage;

    private ListView chatListView;
    private ArrayList<String>  chatsList;
    private ArrayAdapter arrayAdapter;
    private String selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_chat);

        selectedUser = getIntent().getStringExtra("selectedUser");
        setTitle(selectedUser);
//        FancyToast.makeText(this,
//                "Chat with "+selectedUser + " Now " ,
//                FancyToast.LENGTH_SHORT, FancyToast.INFO,
//                true).show();


        findViewById(R.id.btnSendMessage).setOnClickListener(this);

        chatListView = findViewById(R.id.chatListView);
        chatsList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,chatsList);

        chatListView.setAdapter(arrayAdapter);
        try {
            ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
            ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

            //When current logged in user is sending message to selected user of listview.
            firstUserChatQuery.whereEqualTo("waSender", ParseUser.getCurrentUser().getUsername());
            firstUserChatQuery.whereEqualTo("waTargetRecipient", selectedUser);

            //when Current logged in user is receiving the message from the selected user of listview.
            secondUserChatQuery.whereEqualTo("waSender", selectedUser);
            secondUserChatQuery.whereEqualTo("waTargetRecipient", ParseUser.getCurrentUser().getUsername());


            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            allQueries.add(firstUserChatQuery);
            allQueries.add(secondUserChatQuery);

            //new message is below older one
            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);
            myQuery.orderByAscending("createdAt");

            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (objects.size() > 0 && e == null) {

                        for (ParseObject chatObject : objects) {

                            String waMessage = chatObject.get("waMessage") + "";

                            if (chatObject.get("waSender").equals(ParseUser.getCurrentUser().getUsername())) {

                                waMessage = "You: "+ waMessage; // or ParseUser.getCurrentUser().getUsername() + ": "+waMessage;
                            }
                            if (chatObject.get("waSender").equals(selectedUser)) {

                                waMessage = selectedUser + ": " + waMessage;
                            }

                            chatsList.add(waMessage);


                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            });

        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {

        final EditText edtSendText = findViewById(R.id.edtSendText);

        //Create Chat PArseObject
        ParseObject chat = new ParseObject("Chat");
        chat.put("waSender", ParseUser.getCurrentUser().getUsername());
        chat.put("waTargetRecipient", selectedUser);
        chat.put("waMessage", edtSendText.getText().toString());

        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    FancyToast.makeText(WhatsAppChatActivity.this,
                            "Message from "+ ParseUser.getCurrentUser().getUsername() + " Sent to "+selectedUser ,
                            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,
                            true).show();

                    chatsList.add(ParseUser.getCurrentUser().getUsername() + ": "+ edtSendText.getText().toString());
                    arrayAdapter.notifyDataSetChanged(); //update the chatlistview
                    edtSendText.setText("");
                }
            }
        });


    }
}

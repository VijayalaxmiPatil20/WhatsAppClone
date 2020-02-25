package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class WhatsAppUserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<String> arrayList; //ArrayList<String> to make sure I'm passing the string variable.
    private ArrayAdapter arrayAdapter;
    private String followedUser = ""; // if we don't initialise we will have null as 0 element.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_user);
        setTitle("WhatsApp Clone!");

        listView = findViewById(R.id.listView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        arrayList = new ArrayList();
        arrayAdapter = new ArrayAdapter(WhatsAppUserActivity.this,android.R.layout.simple_list_item_1,arrayList);
        //checked mode
//        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(WhatsAppUserActivity.this);
//        listView.setOnItemLongClickListener(WhatsAppUserActivity.this);

        final TextView txtLoadingUsers = findViewById(R.id.txtLoadingUsers);

        try {

            //get the current user from the server and is of type ParseUser
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());


            //get list of objects from parse server
            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if (e == null) {
                        if (users.size() > 0) {
                            for (ParseUser user : users) {
                                arrayList.add(user.getUsername());
                            }
                            listView.setAdapter(arrayAdapter);
                            txtLoadingUsers.animate().alpha(0).setDuration(1000);
                            listView.setVisibility(View.VISIBLE);


//                            for (String twitterUser : arrayList) {
//                                if (ParseUser.getCurrentUser().getList("fanOf") != null) {
//                                    if (ParseUser.getCurrentUser().getList("fanOf").contains(twitterUser)) { // gets the list of fanOf
//                                        followedUser += twitterUser + "\n";
//                                        listView.setItemChecked(arrayList.indexOf(twitterUser), true);   //keep the followed users checked
//                                        FancyToast.makeText(WhatsAppUserActivity.this, ParseUser.getCurrentUser().getUsername() + " is following " + followedUser,
//                                                FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,
//                                                true).show();
//                                    }
//                                }
//                            }

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                    parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    parseQuery.whereNotContainedIn("username", arrayList);
                    parseQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (objects.size() > 0 && e == null){
                                for (ParseUser user : objects) {
                                    arrayList.add(user.getUsername());
                                }
                                arrayAdapter.notifyDataSetChanged();
                                if( swipeRefreshLayout.isRefreshing()){
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            } else {
                               if( swipeRefreshLayout.isRefreshing()){
                                   swipeRefreshLayout.setRefreshing(false);
                                }

                            }
                        }
                    });
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
        Log.i("test","onitemClick");
        Intent intent = new Intent(WhatsAppUserActivity.this, WhatsAppChatActivity.class);
        intent.putExtra("selectedUser", arrayList.get(position));
        startActivity(intent);

    }
//
//    @Override
//    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
//        parseQuery.whereEqualTo("username", arrayList.get(position));
//        parseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
//            @Override
//            public void done(ParseUser user, ParseException e) {
//                if (user != null && e == null){
//                    FancyToast.makeText(WhatsAppUserActivity.this,
//                            user.get("profileProfession") + "" ,
//                            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,
//                            true).show();
//
////                    final  PrettyDialog prettyDialog =  new PrettyDialog(getContext());
////                    prettyDialog.setTitle(user.getUsername() +"'s Info")
////                            .setMessage(user.get("profileBio") + "\n"
////                                    + user.get("profileProfession") + "\n"
////                                    + user.get("profileHobby") + "\n"
////                                    + user.get("profileFavSport"))
////                            .setIcon(R.drawable.person)
////                            .addButton("OK",
////                                    R.color.pdlg_color_white,
////                                    R.color.pdlg_color_green,
////                                    new PrettyDialogCallback() {
////                                        @Override
////                                        public void onClick() {
////                                            prettyDialog.dismiss();
////                                        }
////                                    })
////                            .show();
//                }
//            }
//        });
//
//
//
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
//            case R.id.sendTweetItem:
//
//                Intent intent = new Intent(SocialMediaActivity.this, SendTweetActivity.class);
//                startActivity(intent);
//
//                break;
            case R.id.logoutUserItem:
                FancyToast.makeText(WhatsAppUserActivity.this, ParseUser.getCurrentUser().getUsername() +
                        " is Logged out successfully", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {

                        Intent intent = new Intent(WhatsAppUserActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                    }
                });

                break;

        }
        return super.onOptionsItemSelected(item);
    }
}

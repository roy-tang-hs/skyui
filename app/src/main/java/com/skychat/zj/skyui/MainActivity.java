package com.skychat.zj.skyui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.skychat.zj.chat.Message;
import com.skychat.zj.chat.MessagesListAdapter;
import com.skychat.zj.slidingtab.SlidingTabLayout;
import com.skychat.zj.slidingtab.ViewPagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Channel","Chat"};
    int Numboftabs =2;

    String TITLES[] = {"Search", "Search", "Search", "Search"};
    int ICONS[] = {R.drawable.ic_h, R.drawable.ic_h, R.drawable.ic_h, R.drawable.ic_h};


    String NAME = "Roy Tang";
    String EMAIL = "roy.tang@hootsuite.com";
    int PROFILE = R.drawable.avatar;
    // Declaring the Toolbar Object
    Context passedContext = this;

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;


    //socket
    private Button btnSend;
    private EditText inputMsg;

    private String username = "tangree";
    private int user_id = 13122;
    private int recipientId = 31806;
    private String avatar = "http://skycitizencdn-1272.kxcdn.com/avatar/1-g2.jpg";

    // Chat messages list adapter
    private MessagesListAdapter MsgAdapter;
    private List<Message> listMessages;
    private ListView listViewMessages;

    private String target = "channel message";

    //AJAX
    private String baseURL = "http://www.skycitizen.net/";



    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://54.169.49.211:8080?" +
                    "username=" + username
                    + "&userid=" + user_id
                    + "&avatar=" + avatar);

        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.primary_light);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);


        mRecyclerView = (RecyclerView) findViewById(R.id.left_drawer); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new DrawerAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE, this);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }


        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State


        //Chat
        // Getting the person name from previous screen
////        Intent i = getIntent();
////        Bundle b = i.getExtras();
////        final String target =
        final String target = "channel message";
//
        Log.d("Target", target);
//
        mSocket.on(target, onNewMessage);
//
//
        mSocket.connect();
//
        // ajax call to send message Use: droidQuery
        //  $.ajax(new AjaxOptions().url(baseURL + "api/chat/post/user/"+recipientId)
//
        if (target.equals("channel message")) {
            recipientId = 6;
            JSONObject joinChat = new JSONObject();
            try {
                joinChat.put("roomid", recipientId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("join channel", joinChat);

        }
            Log.i("recipientId", "Value" + recipientId);

            btnSend = (Button) tabs.findViewById(R.id.btnSend);
            inputMsg = (EditText) tabs.findViewById(R.id.inputMsg);
            listViewMessages = (ListView) tabs.findViewById(R.id.list_view_messages);

            btnSend.setOnClickListener(new View.OnClickListener() {
                //            TODO: get ox and send pic
                @Override
                public void onClick(View v) {
//                    String msg = inputMsg.getText().toString();
//                    JSONObject msgObj = null;
//                    try {
//                        JSONObject jObj = new JSONObject();
//                        // JSONObject ox = new JSONObject();
//                        jObj.put("senderId", user_id);
//                        jObj.put("senderName", username);
//                        jObj.put("senderAvatar", avatar);
//                        jObj.put("type", 100);
//                        jObj.put("content", msg);
//                        jObj.put("user_ox", 0);
//                        jObj.put("user_ox_style", "shield-grey");
//                        jObj.put("recipientId", recipientId);
//                        msgObj = jObj;
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (msg != null) {
//                        inputMsg.setText("");
//                        Log.d("ActivityName: ", "socket connected");
//                        mSocket.emit(target, msgObj);
//                        Log.w("myApp", msgObj.toString());
//
//                    } else {
//                        inputMsg.setText("");
//                        Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
//                    }
////                inputMsg.setText("");

                }
            });
//
            listMessages = new ArrayList<Message>();

            MsgAdapter = new MessagesListAdapter(this, listMessages);
            listViewMessages.setAdapter(MsgAdapter);

//

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        mSocket.disconnect();

        target = getIntent().getExtras().getString("target");
        Log.d("Target Destory",target);
        mSocket.off(target, onNewMessage);
    }

    //todo: update view, append list
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.d("Message",data.toString());
                    try {

                        showToast(data.getString("senderId"));
                        String fromName = data.getString("senderName");

                        String message = data.getString("content");

                        showToast(fromName + " + " + message);

//                        Message m = new Message(fromName, message, true);
//                        appendMessage(m);

                        Message m = new Message(fromName, message, false);

                        // Appending the message to chat list
                        appendMessage(m);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };


    private void showToast(final String message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listMessages.add(m);

                MsgAdapter.notifyDataSetChanged();

            }
        });
    }
}


//
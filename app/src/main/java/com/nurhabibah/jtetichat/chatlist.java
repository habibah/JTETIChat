package com.nurhabibah.jtetichat;

import android.content.BroadcastReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

//import com.quickblox.core.QBEntityCallbackImpl;
//import com.quickblox.chat.model.QBDialog;
//import com.nurhabibah.jtetichat.ChatService;
//import com.nurhabibah.jtetichat.pushnotifications.Consts;
//import com.nurhabibah.jtetichat.PlayServicesHelper;
//import com.nurhabibah.jtetichat.R;
//import com.nurhabibah.jtetichat.adapters.DialogsAdapter;

//import org.jivesoftware.smack.ConnectionListener;
//import org.jivesoftware.smack.SmackException;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class chatlist extends MainActivity {

    private static final String TAG = chatlist.class.getSimpleName();

    private ListView ChatListView;
    private Button NewChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        //pushnotificatiom
        //playServicesHelper = new PlayServicesHelper(this);

        ChatListView =(ListView) findViewById(R.id.chatview);
        NewChat =(Button) findViewById(R.id.newchatbtn);
        //Register to receive push notification events
        //LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver, new Intent.FilterComparison(Consts.NEW_PUSH_EVENT));

        //get chat list
        if(isSessionActive()){
            getChats();
        }
    }

    private void getChats(){
        //get chats
        ChatService.getInstance().getChats(new QBEntityCallbackImpl(){
            @Override
            public void onSuccess(Object object, Bundle bundle){
                final ArrayList<QBDialog> dialogs =(ArrayList<QBDialog>)object;
            //build view
                buildListView(dialogs);
            }

            @Override
            public void onError(List errors){
                AlertDialog.Builder dialog = new AlertDialog.Builder(chatlist.this);
                dialog.setMessage("get chat errors: " + errors).create().show();
            }
        });
    }

    void buildListView(List<QBDialog> dialogs){
        final DialogsAdapter adapter = new DialogsAdapter(dialogs, chatlist.this);
        ChatListView.setAdapter(adapter);

        //pilih chat

        ChatListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                QBDialog selectedDialog = (QBDialog) adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.EXTRA_DIALOG, selectedDialog);
                //open chat
                ChatActivity.start(chatlist.this, bundle);

                finish();
                return (false);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        playServicesHepler.checkPlayServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chatlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            //new dialog activity
            Intent intent = new Intent(chatlist.this, newchatactivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //handler for received intent
    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //get extra data included in the intent
            String message = intent.getStringExtra(Const.EXTRA_MESSAGE);

            Log.i(TAG, "Receiving event" + Consts.NEW_PUSH_EVENT + "with data:" + message);
        }
    };

    //AppSessionStateCllback

 //   @Override
    public void onStartSessionRecreation(){

    }
 //   @Override
    public void onFinishSessionRecreation(final boolean success){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(success){
                    getChats();
                }
            }
        });
    }
}

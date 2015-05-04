package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 *
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String[] REMOTE_PORT_LIST = new String[]{"11108", "11112", "11116", "11120", "11124"};
    //static final String[] REMOTE_PORT_LIST = new String[]{"11108", "11112"};
    static final int SERVER_PORT = 10000;
    private ServerSocket serverSocket;
    private MyContentResolver myContentResolver;
    static int seqCount = -1;
    static int localCount = 0;
    String myPort;
    private HashMap<Integer, String> bufferHashMap;

    //vector
    //static int vector[1,]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
        bufferHashMap = new HashMap<Integer, String>();
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        //Initialize mycontentResolver
        myContentResolver = new MyContentResolver(getContentResolver());

        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             *
             * AsyncTask is a simplified thread construct that Android provides. Please make sure
             * you know how it works by reading
             * http://developer.android.com/reference/android/os/AsyncTask.html
             */
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            /*
             * Log is a good way to debug your code. LogCat prints out all the messages that
             * Log class writes.
             *
             * Please read http://developer.android.com/tools/debugging/debugging-projects.html
             * and http://developer.android.com/tools/debugging/debugging-log.html
             * for more information on debugging.
             */
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());

        final EditText editText = (EditText) findViewById(R.id.editText1);

        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));

        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs in a total-causal order.
         */

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String msg = editText.getText().toString() + "\n";
                editText.setText(""); // This is one way to reset the input box.
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    /* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
    @Override
    protected void onDestroy() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    /***
     * ServerTask is an AsyncTask that should handle incoming messages. It is created by
     * ServerTask.executeOnExecutor() call in SimpleMessengerActivity.
     *
     * Please make sure you understand how AsyncTask works by reading
     * http://developer.android.com/reference/android/os/AsyncTask.html
     *
     * @author stevko
     *
     */
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        private ObjectInputStream inStream;
        private ObjectOutputStream outStream;
        private Socket socket;
        private String message;
        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            serverSocket = sockets[0];

            //Code to receive the message from the client
            try {
                //Accept connection in a loop
                while(true){
                    socket = serverSocket.accept();
                    inStream = new ObjectInputStream(socket.getInputStream());
                    MulticastMessage msg = (MulticastMessage) inStream.readObject();
                    //while((message = bufReader.readLine()) != null){
                    //invoke onProgressUpdate
                    message = msg.getMessage();
                    String key = "" + msg.getSeqNumber();
                    /// If this server is a sequencce server. Process the messages accordingly

                    if(myPort.equals("11108") && message.equalsIgnoreCase("request")){
                        //send the sequence to the calling avd
                        Log.d(TAG, "REQUEST RECEIVED ");
                        seqCount++;
                        MulticastMessage response = new MulticastMessage(seqCount, "response");
                        outStream = new ObjectOutputStream(socket.getOutputStream());
                        outStream.writeObject(response);
                        outStream.flush();
                        outStream.close();

                    }
                    else{
                        Log.d(TAG, "Publishing message ");

                        // insert in DB
                        String strKey = key.trim();
                        String strReceived = message.trim();

                        int currentKey = Integer.parseInt(strKey);
                        if(currentKey == localCount){
                            localCount++;
                            publishProgress(strReceived + " " + localCount + "\t\n");
                            myContentResolver.callInsert(strKey, strReceived);
                            //check if hashmap has any values
                            String nextMsg;
                            while((nextMsg = bufferHashMap.get(localCount)) != null){
                                myContentResolver.callInsert(Integer.toString(localCount), nextMsg);
                                //remoteTextView = (TextView) findViewById(R.id.textView1);
                                //remoteTextView.append(nextMsg + " " + localCount + "\t\n");
                                publishProgress(nextMsg + " " + localCount + "\t\n");
                                localCount++;
                            }

                        }
                        else{
                            //save it to hashmap
                            bufferHashMap.put(currentKey, strReceived);
                        }

                        //publishProgress(key,message);

                    }



                    //}
                    inStream.close();
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            TextView remoteTextView = (TextView) findViewById(R.id.textView1);
            remoteTextView.append(strings[0] + "\t\n");

        	/*String strKey = strings[0].trim();
            String strReceived = strings[1].trim();

            int currentKey = Integer.parseInt(strKey);
            if(currentKey == localCount){
            	localCount++;
            	TextView remoteTextView = (TextView) findViewById(R.id.textView1);
                remoteTextView.append(strReceived + " " + localCount + "\t\n");
            	myContentResolver.callInsert(strKey, strReceived);
            	//check if hashmap has any values
            	String nextMsg;
            	while((nextMsg = bufferHashMap.get(localCount)) != null){
            		myContentResolver.callInsert(Integer.toString(localCount), nextMsg);
            		//remoteTextView = (TextView) findViewById(R.id.textView1);
                    remoteTextView.append(nextMsg + " " + localCount + "\t\n");
            		localCount++;
            	}

            }
            else{
            	//save it to hashmap
            	bufferHashMap.put(currentKey, strReceived);
            }*/

            return;
        }
    }

    /***
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     *
     * @author stevko
     *
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {

        //private BufferedWriter bufWriter;
        private ObjectOutputStream outStream;
        private ObjectInputStream inStream;

        @Override
        protected Void doInBackground(String... msgs) {
            try {
                String remotePort = REMOTE_PORT0;
                if(remotePort.equals("5554")){

                }
                if (msgs[1].equals(REMOTE_PORT0))
                    remotePort = REMOTE_PORT1;

                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(REMOTE_PORT_LIST[0]));

                String msgToSend = msgs[0];

                //Get the sequence number from the sequencer
                MulticastMessage msgObj = new MulticastMessage(seqCount, "request");
                outStream = new ObjectOutputStream(socket.getOutputStream());
                outStream.writeObject(msgObj);
                outStream.flush();
                //outStream.close();
                //outStream.close();

                //receive the response
                int seqnumber = -1;
                inStream = new ObjectInputStream(socket.getInputStream());
                MulticastMessage responseMsg = (MulticastMessage) inStream.readObject();
                if(responseMsg.getMessage().equalsIgnoreCase("response")){
                    seqnumber = responseMsg.getSeqNumber();
                    Log.d(TAG, "Sequence number back from sequencer: " + seqnumber);
                }

                outStream.close();
                socket.close();

                msgObj = new MulticastMessage(seqnumber,"" + seqnumber + " - " + msgToSend);

                //Code to send message to the server
                //Multicast with the sequence number
                Log.d(TAG, "Before socket in client");
                for(int i = 0; i < REMOTE_PORT_LIST.length; i++){
                    socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(REMOTE_PORT_LIST[i]));
                    outStream = new ObjectOutputStream(socket.getOutputStream());
                    outStream.writeObject(msgObj);
                    outStream.flush();
                    outStream.close();
                    socket.close();
                }

            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ClientTask socket IOException ");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
            }

        }
    }

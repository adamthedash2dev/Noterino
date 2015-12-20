package org.adamsawesomeapps.adam.noterino;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String defaultShareSubject = "Default Subject";
    private static final String defaultShareMessage = "Default message";
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    private static final String PREF_NOTES = "noteList";

    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.UK);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
    SimpleDateFormat timeAndDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.UK);

    Button mainButton;
    EditText mainEditText;
    ListView mainListView;
    ArrayAdapter<String> mainArrayAdapter;
    ArrayList<String> mainNoteList = new ArrayList<>();
    ShareActionProvider mainShareActionProvider;

    private Intent mainShareIntent;
    private SharedPreferences mainSharedPreferences;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayWelcome();

        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        mainEditText = (EditText) findViewById(R.id.main_editText);

        mainListView = (ListView) findViewById(R.id.main_listView);
        mainArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mainNoteList);
        mainListView.setAdapter(mainArrayAdapter);
        mainListView.setOnItemClickListener(this);
        mainListView.setOnItemLongClickListener(this);

        setShareIntent(defaultShareSubject, defaultShareMessage);

        readArray();


//        Pop-up don't touch

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Kappa", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    @Override
    protected void onStop() {
        super.onStop();

//        Saves notes
        saveArray(PREF_NOTES, mainNoteList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);

//        Creates and adds share button to the action bar
/*        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        if(shareItem != null){
            mainShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }*/

        return true;
    }

    private void setShareIntent(String subject, String message){
        if(mainShareActionProvider != null){
//            Sets up share button

            mainShareIntent = new Intent(Intent.ACTION_SEND);
            mainShareIntent.setType("text/plain");
            mainShareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            mainShareIntent.putExtra(Intent.EXTRA_TEXT, message + "\r\n- sent from " + getResources().getText(R.string.app_name) + "!");

            mainShareActionProvider.setShareIntent(mainShareIntent);


        }
    }

    @Override
    public void onClick(View v) {
//        Saves time and message sent
        String timeSent = getTimestamp(timeAndDateFormat);
        String message = mainEditText.getText().toString();

        if(!message.isEmpty()){
            message = mainEditText.getText() + "\r\n~ "/* + name*/;

//        Adds the message to the list
            mainNoteList.add(0, message + " " + timeSent);
            mainArrayAdapter.notifyDataSetChanged();
        }


//        Clears text input
        mainEditText.setText("");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.d("Short pressed", position + ": " + mainNoteList.get(position));
//        Creates alert box to share message
        final AlertDialog.Builder shareConfirmation = new AlertDialog.Builder(this);
        shareConfirmation.setTitle("Share message");
        shareConfirmation.setMessage("Would you like to share your message?");

        shareConfirmation.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

//        Shares the selected message
        shareConfirmation.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainShareIntent = new Intent(Intent.ACTION_SEND);
                mainShareIntent.setType("text/plain");
                mainShareIntent.putExtra(Intent.EXTRA_SUBJECT, name + " shared a note!");
                mainShareIntent.putExtra(Intent.EXTRA_TEXT, mainNoteList.get(position).split("~")[0] + "- sent by " + name + " from " + getResources().getText(R.string.app_name));

                startActivity(Intent.createChooser(mainShareIntent, "Select"));
            }
        });

        shareConfirmation.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Long pressed", position + ": " + mainNoteList.get(position));

        mainNoteList.remove(position);
        mainArrayAdapter.notifyDataSetChanged();

        return true;
    }

    public void displayWelcome(){

        mainSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        name = mainSharedPreferences.getString(PREF_NAME, "");

        if(name.length() > 0){
            Toast.makeText(this, "Welcome back " + name + "!", Toast.LENGTH_LONG).show();
        } else {
//            Show a dialog
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Welcome!");
            alert.setMessage("What is your name?");

//            Create text box for name
            final EditText input = new EditText(this);
            alert.setView(input);

//            Create Save button
            alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String inputName = input.getText().toString();

//                    Save name into memory
                    SharedPreferences.Editor e = mainSharedPreferences.edit();
                    e.putString(PREF_NAME, inputName);
                    e.apply();

                    Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();

                }
            });

//            Make cancel button to dismiss dialog
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });

            alert.show();

        }

    }

    private String formatDate(String time, SimpleDateFormat mDateFormat) throws ParseException{

        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.UK).parse(time);

        return mDateFormat.format(date);

    }

    public String getTimestamp(SimpleDateFormat mDateFormat){
//        Gets timestamp for current time
        java.sql.Timestamp time = new java.sql.Timestamp(System.currentTimeMillis());

        String timeSent = "";

        try {
            timeSent = formatDate(time.toString(), mDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeSent;
    }

    private void saveArray(String prefKey, ArrayList<String> arr){
//        JSON array for saving in prefs
        JSONArray mJSONArray = new JSONArray();
        for(String x : arr){
            mJSONArray.put(x);

        }

//        Saves notes to memory
        SharedPreferences.Editor e = mainSharedPreferences.edit();
        e.putString(prefKey, mJSONArray.toString());
        e.apply();

    }

    private void readArray(){
//        Reads saved notes and puts them in the list
        try{
            JSONArray mJSONArray  = new JSONArray(mainSharedPreferences.getString(PREF_NOTES, "[]"));

            for(int i = 0; i < mJSONArray.length(); i++){
                mainNoteList.add(0, mJSONArray.getString(i));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}

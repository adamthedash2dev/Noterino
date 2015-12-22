package org.adamsawesomeapps.adam.noterino;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/*
 * Created by Adam on 21/12/2015.
 */
public class ReminderActivity extends AppCompatActivity implements View.OnClickListener{

    Button rButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





    }

    @Override
    protected void onStart() {
        super.onStart();

        setContentView(R.layout.reminders);
        rButton = (Button) findViewById(R.id.rButton);
        rButton.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.rButton:
                Log.d("Reminder button", "Clicked");
                finish();

            default:
                Log.d("Button", "default");

        }




    }
}

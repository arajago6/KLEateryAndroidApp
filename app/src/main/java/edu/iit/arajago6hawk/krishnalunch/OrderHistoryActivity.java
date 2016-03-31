package edu.iit.arajago6hawk.krishnalunch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rasuishere on 2/27/16.
 */
public class OrderHistoryActivity extends Activity {
    DbMain mydb; private ListView obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historydetails);

        mydb = new DbMain(this);

        ArrayList array_list = mydb.getAllOrders();
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list);

        obj = (ListView)findViewById(R.id.ohmobile_list);
        obj.setAdapter(arrayAdapter);

        int ordCount = mydb.numberOfOrders();
        if (ordCount>0){
            final TextView stTxt = (TextView)findViewById(R.id.ohtextView);
            stTxt.setText("We have found "+Integer.toString(ordCount)+" order(s) in your history.");
        }

        final Button button = (Button) findViewById(R.id.ohbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mydb.deleteAllOrders();
                String cbMsg = "Entire order history has been reset";
                String htmlString = " <font color=\"#F75B5D\"><b><i>NOTIFICATION</font></i></b><br/>" + cbMsg;
                Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrderHistoryActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}

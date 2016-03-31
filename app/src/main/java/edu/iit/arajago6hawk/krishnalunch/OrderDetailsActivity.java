package edu.iit.arajago6hawk.krishnalunch;

/**
 * Created by rasuishere on 2/20/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Timestamp;
import java.util.Date;

public class OrderDetailsActivity extends Activity {
    CustomizedBaseAdapter custAdapter = null;
    double cost = 0.00;
    ArrayList<CustomItemCollection> finalArr = new ArrayList<CustomItemCollection>();
    String items = "";  double subCost = 0;
    DbMain mydb;


    /*
    ArrayList<CustomItemCollection> savedArr = new ArrayList<CustomItemCollection>();
    ArrayList<CustomItemCollection> combinedArr = new ArrayList<CustomItemCollection>();

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList("MyList", finalArr);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        savedArr = savedInstanceState.getParcelableArrayList("MyList");
    }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orderdetails);

        mydb = new DbMain(this);

        Bundle b = getIntent().getExtras();
        ArrayList<CustomItemCollection> resultArr = b.getParcelableArrayList("cicKey");
        ListView lv = (ListView) findViewById(R.id.odmobile_list);

        int itCount = 0;
        for(int i=0;i<resultArr.size();i++){
            CustomItemCollection cic = resultArr.get(i);
            if (cic.isSelected()==1){
                cic.setQuantity(cic.getQuantity() + 1);
                finalArr.add(cic);
                if (itCount == 0){
                    items = cic.getName();
                    subCost = cic.getPrice();
                }
                else{
                    items = items + " - " + cic.getName();
                    subCost = subCost + cic.getPrice();
                }
                itCount = itCount + 1;
            }
            else if(cic.getQuantity()>0){
                finalArr.add(cic);
            }
        }


        for(int i=0;i<finalArr.size();i++){
            CustomItemCollection cic = finalArr.get(i);
            cost = cost + cic.getPrice()*cic.getQuantity();
        }

        if (finalArr.size()==0){
            final TextView stTxt = (TextView)findViewById(R.id.odtextView5);
            stTxt.setText("No item has been confirmed yet!\n\nSelect some items in first screen and click \"confirm order\" button to finalize.");
        }

        if (cost>0.00){
        final TextView costTxt = (TextView)findViewById(R.id.textView6);
        costTxt.setText("Cost: $" + Double.toString(cost));
        }

        final Button button = (Button) findViewById(R.id.odbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailsActivity.this, MapActivity.class);
                startActivity(intent);
                /*
                Intent intent = new Intent(getApplicationContext(),
                        MainActivity.class);
                Bundle b = new Bundle();
                //b.putString("result", "Total: $" + Double.toString(cost));
                //intent.putExtras(b);
                setResult(RESULT_CANCELED, intent);
                finish();
                */
            }
        });

        final Button button1 = (Button) findViewById(R.id.odbutton1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (items == "") {
                    String cbMsg = "There were no new items in the order to confirm";
                    String htmlString = " <font color=\"#F75B5D\"><b><i>CONFIRM WARNING</font></i></b><br/>" + cbMsg;
                    Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Calendar calendar = Calendar.getInstance();
                    Timestamp tstamp = new Timestamp(calendar.getTime().getTime());
                    mydb.insertOrder(tstamp.toString(), items, subCost);
                }

                Intent intent = new Intent(getApplicationContext(),
                        MainActivity.class);
                Bundle b = new Bundle();
                b.putString("result", "Total: $" + Double.toString(cost));
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        custAdapter = new CustomizedBaseAdapter(this, R.layout.activity_orderdetails, finalArr);

        lv.setAdapter(custAdapter);
    }

    private class CustomizedBaseAdapter extends ArrayAdapter<CustomItemCollection> {

        private ArrayList<CustomItemCollection> getArrayList;
        private LayoutInflater mInflater;

        public CustomizedBaseAdapter(Context context, int resource, ArrayList<CustomItemCollection> results) {
            super(context,resource,results);
            getArrayList = results;
            mInflater = LayoutInflater.from(context);
        }

        class ViewHolder {
            TextView txtName;
            TextView txtPrice;
            TextView txtType;
            TextView txtMake;
            TextView txtQuantity;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder Vwhldr;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_odrow_view, null);
                Vwhldr = new ViewHolder();
                Vwhldr.txtName = (TextView) convertView.findViewById(R.id.name);
                Vwhldr.txtPrice = (TextView) convertView.findViewById(R.id.price);
                Vwhldr.txtType = (TextView) convertView.findViewById(R.id.type);
                Vwhldr.txtMake = (TextView) convertView.findViewById(R.id.make);
                Vwhldr.txtQuantity = (TextView) convertView.findViewById(R.id.quantity);
                convertView.setTag(Vwhldr);

            } else {
                Vwhldr = (ViewHolder) convertView.getTag();
            }

            Vwhldr.txtName.setText(getArrayList.get(position).getName());
            Vwhldr.txtPrice.setText("$"+Double.toString(getArrayList.get(position).getPrice()));
            Vwhldr.txtType.setText(getArrayList.get(position).getType());
            Vwhldr.txtMake.setText(getArrayList.get(position).getMake());
            Vwhldr.txtQuantity.setText("X "+Integer.toString(getArrayList.get(position).getQuantity()));

            return convertView;
        }

    }
}

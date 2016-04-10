package edu.iit.arajago6hawk.krishnalunch;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import edu.iit.arajago6hawk.krishnalunch.GDriveServices.GDriveDataLoader;
import edu.iit.arajago6hawk.krishnalunch.GDriveServices.UserService;

public class MainActivity extends AppCompatActivity {

    ArrayList<CustomItemCollection> results = new ArrayList<CustomItemCollection>();
    ArrayList<CustomItemCollection> searchResults = new ArrayList<CustomItemCollection>();
    CustomizedBaseAdapter custAdapter;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList("MyList", results);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkButtonClick();

        //View backgroundimage = findViewById(R.id.topFrame1);
        //Drawable background = backgroundimage.getBackground();
        //background.setAlpha(150);

        final ListView lv = (ListView) findViewById(R.id.mobile_list);
        /*
        if(savedInstanceState != null){
            searchResults = savedInstanceState.getParcelableArrayList("MyList");
        }
        else{
            searchResults = GetSearchResults(results);
        }
        */
        searchResults = GetSearchResults(results);
        custAdapter = new CustomizedBaseAdapter(this, R.layout.activity_main, searchResults);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int selCount = 0;
                for(int i=0;i<results.size();i++){
                    CustomItemCollection cic = results.get(i);
                    if (cic.isSelected()==1){
                        cic.setSelected(0);
                        selCount++;
                    }
                }
                if (selCount > 0) {
                    custAdapter.notifyDataSetChanged();
                    String cbMsg = "All your selections have been reset";
                    String htmlString = " <font color=\"#00D0FF\"><b><i>SELECTIONS CLEARED</font></i></b><br/>" + cbMsg;
                    Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();
                }
                else{
                    String cbMsg = "You have not selected any item yet";
                    String htmlString = " <font color=\"#F75B5D\"><b><i>RESET WARNING</font></i></b><br/>" + cbMsg;
                    Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Intent intent = new Intent(getBaseContext(), OrderHistoryActivity.class);

                startActivity(intent);
            }
        });

        lv.setAdapter(custAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object obj = lv.getItemAtPosition(position);
                CustomItemCollection itemClickObject = (CustomItemCollection) obj;
                String cMsg = "Name: " + itemClickObject.getName() + "<br/>" + "Nature: " + itemClickObject.getNature() + "<br/>" + "About: " + itemClickObject.getBriefDes();
                String htmlString = " <font color=\"#00D0FF\"><b><i>DISH DETAILS</font></i></b><br/>" + cMsg;
                Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {


                Object obj1 = lv.getItemAtPosition(position);
                CustomItemCollection itemLongClickObject = (CustomItemCollection) obj1;
                String lcMsg = itemLongClickObject.getName() + " has been removed from the menu";
                searchResults.remove(position);
                String htmlString = " <font color=\"#F75B5D\"><b><i>NOTIFICATION</b></i></font><br/>" + lcMsg;
                Toast.makeText(getApplicationContext(), Html.fromHtml(htmlString), Toast.LENGTH_SHORT).show();

                custAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    public void take_to_quickorder(View v)
    {

        Intent intent = new Intent(MainActivity.this, QuickOrderActivity.class);
        startActivity(intent);
    }

    public void take_to_map(View v)
    {

        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    private class CustomizedBaseAdapter extends ArrayAdapter<CustomItemCollection> {

        private ArrayList<CustomItemCollection> getArrayList;
        private LayoutInflater mInflater;
        boolean[] checkBoxState = new boolean[10];

        public CustomizedBaseAdapter(Context context, int resource, ArrayList<CustomItemCollection> results) {
            super(context, resource, results);
            getArrayList = results;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getViewTypeCount() {
            return getCount();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder {
            CheckBox checkBox;
            TextView txtName;
            TextView txtPrice;
            TextView txtType;
            TextView txtMake;
            TextView txtBriefDes;
            ImageView imgVNV;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder Vwhldr;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_rowview, null);
                Vwhldr = new ViewHolder();
                Vwhldr.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
                Vwhldr.txtName = (TextView) convertView.findViewById(R.id.name);
                Vwhldr.txtPrice = (TextView) convertView.findViewById(R.id.price);
                Vwhldr.txtType = (TextView) convertView.findViewById(R.id.type);
                Vwhldr.imgVNV = (ImageView) convertView.findViewById(R.id.imageView1);
                Vwhldr.txtBriefDes = (TextView) convertView.findViewById(R.id.briefdes);
                convertView.setTag(Vwhldr);

                Vwhldr.checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox box = (CheckBox)v.findViewById(R.id.checkBox1);
                        CustomItemCollection cic = (CustomItemCollection)box.getTag();
                        if (box.isChecked()){
                            checkBoxState[position]=true;
                            results.get(position).setSelected(1);
                            String cbMsg = results.get(position).getName() + " is selected for order";
                            String htmlString = " <font color=\"#00D0FF\"><b><i>ITEM SELECTED</font></i></b><br/>" + cbMsg;
                            Toast.makeText(getApplicationContext(),Html.fromHtml(htmlString),Toast.LENGTH_SHORT).show();
                        }
                        else{
                            checkBoxState[position]=false;
                            results.get(position).setSelected(0);
                            String cbMsg = results.get(position).getName() + " is removed from selection";
                            String htmlString = " <font color=\"#F75B5D\"><b><i>ITEM DESELECTED</font></i></b><br/>" + cbMsg;
                            Toast.makeText(getApplicationContext(),Html.fromHtml(htmlString),Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            } else {
                Vwhldr = (ViewHolder) convertView.getTag();
            }

            Vwhldr.txtName.setText(getArrayList.get(position).getName());
            Vwhldr.txtPrice.setText("$"+Double.toString(getArrayList.get(position).getPrice()));
            Vwhldr.txtType.setText(getArrayList.get(position).getType()+"  ");
            Vwhldr.txtBriefDes.setText(getArrayList.get(position).getBriefDes());

            if(getArrayList.get(position).getMake()=="Vegetarian"){
                Vwhldr.imgVNV.setImageResource(R.drawable.v);
            }
            else{
                Vwhldr.imgVNV.setImageResource(R.drawable.nv);
            }
            
            if(getArrayList.get(position).isSelected()==1)
                Vwhldr.checkBox.setChecked(true);
            else
                Vwhldr.checkBox.setChecked(false);

            return convertView;
        }

    }

    private void checkButtonClick() {

        Button myButton = (Button) findViewById(R.id.button1);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ArrayList<CustomItemCollection> cic = custAdapter.getArrayList;

                Intent intent = new Intent(getApplicationContext(),
                        OrderDetailsActivity.class);

                Bundle b = new Bundle();
                b.putParcelableArrayList("cicKey", cic);

                intent.putExtras(b);
                startActivityForResult(intent, 1);

            }
        });

    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==1 && resultCode == RESULT_OK)
        {
            String message=data.getStringExtra("result");
            //TextView rtTv = (TextView) findViewById(R.id.textView5);
            //rtTv.setText(message);

            for(int i=0;i<results.size();i++){
                CustomItemCollection cic = results.get(i);
                if (cic.isSelected()==1){
                    cic.setQuantity(cic.getQuantity()+1);
                    cic.setSelected(0);
                }
            }
            custAdapter.notifyDataSetChanged();
        }

        if(requestCode==1 && resultCode == RESULT_CANCELED)
        {
            for(int i=0;i<results.size();i++){
                CustomItemCollection cic = results.get(i);
                cic.setSelected(0);
            }
            custAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<CustomItemCollection> GetSearchResults(ArrayList<CustomItemCollection> results){

        CustomItemCollection sr1 = new CustomItemCollection();
        sr1.setName("Cheesy Zucchini Cakes");
        sr1.setPrice(50.75);
        sr1.setType("Continental");
        sr1.setMake("Vegetarian");
        sr1.setNature("Sweet");
        sr1.setBriefDes("Prepared with delicious cheesy rice, grated zucchini rice and eggs");
        results.add(sr1);

        CustomItemCollection sr2 = new CustomItemCollection();
        sr2.setName("Fragrant Thai Veg Curry");
        sr2.setPrice(83.50);
        sr2.setType("Italian");
        sr2.setMake("Vegetarian");
        sr2.setNature("Sour");
        sr2.setBriefDes("Mouth watering noodles steamed with cocnut milk and broccoli");
        results.add(sr2);

        CustomItemCollection sr3 = new CustomItemCollection();
        sr3.setName("Idli-Sambhar");
        sr3.setPrice(10.25);
        sr3.setType("Indian");
        sr3.setMake("Vegetarian");
        sr3.setNature("Sour");
        sr3.setBriefDes("Cooked with quality Indian rice, sugar, dal and spices");
        results.add(sr3);

        CustomItemCollection sr4 = new CustomItemCollection();
        sr4.setName("Vada");
        sr4.setPrice(25.50);
        sr4.setType("Indian");
        sr4.setMake("Vegetarian");
        sr4.setNature("Spicy");
        sr4.setBriefDes("Prepared with dal and handpicked Indian spices");
        results.add(sr4);

        CustomItemCollection sr5 = new CustomItemCollection();
        sr5.setName("Rasamalai");
        sr5.setPrice(15.00);
        sr5.setType("Indian");
        sr5.setMake("Vegetarian");
        sr5.setNature("Sweet");
        sr5.setBriefDes("Ingredients are condensed milk, milkmaid and sugar");
        results.add(sr5);

        //DailyMenuUpdater updater = new DailyMenuUpdater();
        //updater.execute();

        return results;
    }

    private class DailyMenuUpdater extends AsyncTask<Void, Void, Void > {
        private String[] daysOfWeek = {"monday", "tuesday", "wednesday", "thursday", "friday"};

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://www.iskconchicago.com/krishna-lunch/");
                URLConnection connection = url.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) connection;

                if(httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    // start pulling the page
                    InputStream in = httpConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    // the page is extremely malformed, need to filter out stuff
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    String page = builder.toString();
                    // take off all tags except for div, br, h2
                    page = page.substring(page.indexOf("<div id=\"g1-section-1\""), page.lastIndexOf("<span id=\"g1-space-1\""))
                                        .replaceAll("<(?!/?div)(?!br/?)(?!/?h2)[^>]*>", "")
                                        .replaceAll("<[^>]*>", "\n")
                                        .replaceAll("&nbsp;", " ");
                    String [] contentList = page.split("(\n)+");

                    results.clear();

                    for(int i = 0 ; i < contentList.length;){
                        String content = contentList[i].trim();
                        if(content != null && content != ""){
                            if(isDayOfWeek(content)){
                                CustomItemCollection item = new CustomItemCollection();
                                item.setName(content);
                                StringBuilder sb = new StringBuilder();
                                for(++i; i < contentList.length; i++){
                                    if(isDayOfWeek(contentList[i])) break;
                                    sb.append(contentList[i] + "\n");
                                }
                                item.setBriefDes(sb.toString());
                                results.add(item);
                                Log.d("parser2", sb.toString());
                            }else{
                                i++;
                            }
                        }else{
                            i++;
                        }
                    }

                    custAdapter.notifyDataSetChanged();
                }
            }catch(Exception e) {
                Log.d("parser", e.getMessage());
            }
            return null;
        }

        private boolean isDayOfWeek(String value){
            for(String day : daysOfWeek){
                if(value.toLowerCase().equals(day)){
                    return true;
                }
            }

            return false;
        }
    }
}

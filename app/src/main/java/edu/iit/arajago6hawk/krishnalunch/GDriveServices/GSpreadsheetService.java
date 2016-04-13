package edu.iit.arajago6hawk.krishnalunch.GDriveServices;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.IEntry;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * Created by dkwon on 4/6/2016.
 */
public abstract class GSpreadsheetService {

    private static GSpreadsheetService _instance = null;
    private static final String SERVICE_ACCOUNT = "dbmanager@kleatery.iam.gserviceaccount.com";
    private static final String KEY_PASSWORD = "notasecret";
    private static final String KEY_FILE = "KLEatery.p12";
    private static final String SPREADSHEET_KEY = "1izgMvtJ4gscbfD60V-nDm1gTcg0HS0aZhDQXJHRQPIk";
    private static final String SPREADSHEET_FEED_URL = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";
    private SpreadsheetService _service;
    private static WorksheetFeed _worksheetFeed;
    protected static SpreadsheetEntry _spreadSheet;

    protected WorksheetEntry GetWorksheet(String name){
        try {
            if(_worksheetFeed == null) {
                _worksheetFeed = (WorksheetFeed) GetFeed(_spreadSheet.getWorksheetFeedUrl(), WorksheetFeed.class);
            }

            for (WorksheetEntry entry : _worksheetFeed.getEntries() ) {
                if(entry.getTitle().getPlainText().toLowerCase().equals(name.toLowerCase()))
                    return entry;
            }
        }catch (Exception e){
            Log.e("GSpreadsheetService", e.getMessage());
        }

        return null;
    }

    protected void InitService(Context context){
        if(_spreadSheet == null) {
            try {
                File keyFile = File.createTempFile("key", ".tmp");
                FileOutputStream os = new FileOutputStream(keyFile);
                IOUtils.copy(context.getAssets().open(KEY_FILE), os);

                GoogleCredential credential = new GoogleCredential.Builder().setTransport(new NetHttpTransport())
                        .setJsonFactory(JacksonFactory.getDefaultInstance())
                        .setServiceAccountId(SERVICE_ACCOUNT)
                        .setServiceAccountScopes(Collections.singleton("https://spreadsheets.google.com/feeds"))
                        .setServiceAccountPrivateKeyFromP12File(keyFile)
                        .build();

                _service = new SpreadsheetService("DBSpreadSheet");
                _service.setOAuth2Credentials(credential);
                SpreadsheetFeed feed = (SpreadsheetFeed)GetFeed(new URL(SPREADSHEET_FEED_URL), SpreadsheetFeed.class);
                List<SpreadsheetEntry> spreadsheets = feed.getEntries();

                if (spreadsheets.size() == 0) {
                    Log.e("GSpreadsheetService", "No spreadsheet found");
                    return;
                }

                for (SpreadsheetEntry entry : spreadsheets) {
                    // found db spreadsheet
                    if (entry.getKey().equals(SPREADSHEET_KEY)) {
                        _spreadSheet = entry;
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("GSpreadsheetService", e.getMessage());
            }
        }
    }

    protected BaseFeed GetFeed(URL url, Class pClass){
        try {
            GetFeedTaskParams param = new GetFeedTaskParams(pClass, url);
            return new GetFeedTask().execute(param).get();
        }catch (Exception e){
            Log.e("GSpreadsheetService", e.getMessage());
        }

        return null;
    }

    protected void InsertEntry(URL url, IEntry entry){
        try {
            InsertFeedTaskParams param = new InsertFeedTaskParams(entry, url);
            new InsertFeedTask().execute(param).get();
        }catch(Exception e){
            Log.e("GSpreadsheetService", e.getMessage());
        }
    }

    protected void UpdateListEntry(ListEntry entry){
        try{
            new UpdateListEntryTask().execute(entry).get();
        }catch (Exception e){
            Log.e("GSpreadsheetService", e.getMessage());
        }
    }

    private class GetFeedTask extends AsyncTask<GetFeedTaskParams, Void, BaseFeed> {

        @Override
        protected BaseFeed doInBackground(GetFeedTaskParams... params) {
            try {
                return _service.getFeed(params[0].url, params[0].pClass);
            }catch(Exception e){
                Log.e("GetFeedTask", e.getMessage());
            }

            return null;
        }
    }

    private class InsertFeedTask extends AsyncTask<InsertFeedTaskParams, Void, Void>{

        @Override
        protected Void doInBackground(InsertFeedTaskParams... params) {
            try{
                _service.insert(params[0].url, params[0].entry);
            }catch (Exception e){
                Log.e("InsertFeedTask", e.getMessage());
            }

            return null;
        }
    }

    private class UpdateListEntryTask extends AsyncTask<ListEntry, Void, Void>{

        @Override
        protected Void doInBackground(ListEntry... params) {
            try {
                params[0].update();
            }catch (Exception e){
                Log.e("UpdateFeedTask", e.getMessage());
            }
            return null;
        }
    }

    private static class GetFeedTaskParams{
        Class<BaseFeed> pClass;
        URL url;

        GetFeedTaskParams(Class pClass, URL url){
            this.pClass = pClass;
            this.url = url;
        }
    }

    private static class InsertFeedTaskParams{
        IEntry entry;
        URL url;

        InsertFeedTaskParams(IEntry entry, URL url){
            this.entry = entry;
            this.url = url;
        }
    }
}

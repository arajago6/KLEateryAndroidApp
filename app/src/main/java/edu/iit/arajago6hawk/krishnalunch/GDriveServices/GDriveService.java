package edu.iit.arajago6hawk.krishnalunch.GDriveServices;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by dkwon on 4/12/2016.
 */
public class GDriveService{
    private static final String SERVICE_ACCOUNT = "dbmanager@kleatery.iam.gserviceaccount.com";
    private static final String KEY_PASSWORD = "notasecret";
    private static final String KEY_FILE = "KLEatery.p12";
    private static Drive _drive;
    private static List<com.google.api.services.drive.model.File> _fileList;

    public GDriveService(Context context) {
        try {
            File keyFile = File.createTempFile("key", ".tmp");
            FileOutputStream os = new FileOutputStream(keyFile);
            IOUtils.copy(context.getAssets().open(KEY_FILE), os);

            GoogleCredential credential = new GoogleCredential.Builder().setTransport(new NetHttpTransport())
                    .setJsonFactory(JacksonFactory.getDefaultInstance())
                    .setServiceAccountId(SERVICE_ACCOUNT)
                    .setServiceAccountScopes(Collections.singleton("https://www.googleapis.com/auth/drive"))
                    .setServiceAccountPrivateKeyFromP12File(keyFile)
                    .build();

            _drive = new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential).build();
        }catch(Exception ex){
            Log.e("GDriveService", ex.getMessage());
        }
    }

    public void UploadFile(File image, String title){
        String extension = MimeTypeMap.getFileExtensionFromUrl(image.getAbsolutePath());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        com.google.api.services.drive.model.File meta = new com.google.api.services.drive.model.File();
        meta.setName(title);
        meta.setMimeType(mimeType);

        FileContent mediaContent = new FileContent(mimeType, image);
        InsertFileTaskParam param = new InsertFileTaskParam(meta, mediaContent);
        try {
            new InsertFileTask().execute(param).get();
        }catch(Exception e){
            Log.e("GDriveService", e.getMessage());
        }
    }

    public InputStream DownloadFile(String title){
        try {
            if (_fileList == null) {
                _fileList = new GetFilesTask().execute().get();
            }

            String imageUrl = null;
            for (com.google.api.services.drive.model.File file : _fileList) {
                if (file.getName().equals(title)) {
                    return new DownloadFileTask().execute(file.getId()).get();
                }
            }
        }catch(Exception e){
            Log.e("GDriveService", e.getMessage());
        }

        return null;
    }

    private class InsertFileTask extends AsyncTask<InsertFileTaskParam, Void, Void>{

        @Override
        protected Void doInBackground(InsertFileTaskParam... params) {
            try{
                _drive.files().create(params[0].file, params[0].content).execute();
            }catch(Exception e){
                Log.e("InsertFileTask", e.getMessage());
            }
            return null;
        }
    }

    private class GetFilesTask extends AsyncTask<Void, Void, List<com.google.api.services.drive.model.File>>{

        @Override
        protected List<com.google.api.services.drive.model.File> doInBackground(Void... params) {
            try {
                FileList result = _drive.files().list().setFields("nextPageToken, files(id,name)")
                        .execute();
                return result.getFiles();
            }catch(Exception e){
                Log.e("GetFilesTask", e.getMessage());
            }
            return null;
        }
    }

    private class DownloadFileTask extends AsyncTask<String, Void, InputStream>{

        @Override
        protected InputStream doInBackground(String... params) {
            try {
                return _drive.files().get(params[0]).executeMediaAsInputStream();
            }catch(Exception e){
                Log.e("DownloadImageTask", e.getMessage());
            }

            return null;
        }
    }

    private class InsertFileTaskParam{
        public com.google.api.services.drive.model.File file;
        public FileContent content;

        InsertFileTaskParam(com.google.api.services.drive.model.File file, FileContent content){
            this.file = file;
            this.content = content;
        }
    }
}

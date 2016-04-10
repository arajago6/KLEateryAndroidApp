package edu.iit.arajago6hawk.krishnalunch.GDriveServices;

import android.content.Context;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

/**
 * Created by dkwon on 4/7/2016.
 */
public class UserService extends GDriveDataLoader{
    private static final String USER_TABLE = "Users";
    private static final String EMAIL_COLUMN = "email";
    private static final String USER_ID_COLUMN = "userId";

    private WorksheetEntry _worksheet;
    private static ListFeed _listFeed;

    public UserService(Context context){
        InitService(context);
        _worksheet = GetWorksheet(USER_TABLE);
    }

    /**
     * Checks whether a user account exists with matching email address
     * @param userEmail: email address of the logged in user
     * @return boolean
     */
    public boolean CheckUserExists(String userEmail){
        if(_listFeed == null) {
            _listFeed = (ListFeed) GetFeed(_worksheet.getListFeedUrl(), ListFeed.class);
        }

        for(ListEntry entry : _listFeed.getEntries()){
            if(entry.getCustomElements().getValue(EMAIL_COLUMN).toLowerCase().equals(userEmail.toLowerCase())){
                return true;
            }
        }

        return false;
    }

    /**
     * Adds a new user
     * @param userEmail
     * @param userId
     */
    public void AddUser(String userEmail, String userId){
        ListEntry row = new ListEntry();
        row.getCustomElements().setValueLocal(EMAIL_COLUMN, userEmail);
        row.getCustomElements().setValueLocal(USER_ID_COLUMN, userId);
        InsertEntry(_worksheet.getListFeedUrl(), row);
    }
}

package edu.iit.arajago6hawk.krishnalunch.GDriveServices;

import android.content.Context;

import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;

/**
 * Created by dkwon on 4/7/2016.
 */
public class OrderService extends GSpreadsheetService {
    private static final String ORDER_TABLE = "Orders";
    private static final String ORDER_ID_COLUMN = "orderId";
    private static final String USER_ID_COLUMN = "userId";
    private static final String MADE_WHEN_COLUMN = "madeWhen";
    private static final String TARGET_DATE_COLUMN = "targetDate";
    private static final String PROCESSED_COLUMN = "processed";

    private WorksheetEntry _worksheet;
    private static ListFeed _listFeed;

    public OrderService(Context context){
        InitService(context);
        _worksheet = GetWorksheet(ORDER_TABLE);
        _listFeed = (ListFeed) GetFeed(_worksheet.getListFeedUrl(), ListFeed.class);
    }

    /**
     * Adds new order
     * @param orderId
     * @param userId
     * @param madeWhen
     * @param targetDate
     * @param isProcessed
     */
    public void AddOrder(String orderId, String userId, String madeWhen, String targetDate, boolean isProcessed){
        ListEntry row = new ListEntry();
        row.getCustomElements().setValueLocal(ORDER_ID_COLUMN, orderId);
        row.getCustomElements().setValueLocal(USER_ID_COLUMN, userId);
        row.getCustomElements().setValueLocal(MADE_WHEN_COLUMN, madeWhen);
        row.getCustomElements().setValueLocal(TARGET_DATE_COLUMN, targetDate);
        row.getCustomElements().setValueLocal(PROCESSED_COLUMN, Boolean.toString(isProcessed));
        InsertEntry(_worksheet.getListFeedUrl(), row);
    }

    /**
     * Marks an order as processed
     * @param orderId
     */
    public void MarkOrderProcessed(String orderId){
        ListEntry row = GetOrderEntry(orderId);

        if(row != null){
            row.getCustomElements().setValueLocal(PROCESSED_COLUMN, Boolean.toString(true));
            UpdateListEntry(row);
        }
    }

    /* ToDo:
        Make Order model.
     */
    private ListEntry GetOrderEntry(String orderId){
        for(ListEntry entry : _listFeed.getEntries()){
            if(entry.getCustomElements().getValue(ORDER_ID_COLUMN).toLowerCase().equals(orderId.toLowerCase())){
                return entry;
            }
        }

        return null;
    }
}

package com.sam_chordas.android.stockhawk.Widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by Sunnny on 29/12/16.
 */

public class WidgetRemoteViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data=null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                  if(data!=null)
                  {
                      data.close();
                  }

                final long identityToken = Binder.clearCallingIdentity();
                data=getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, new String[]{ QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data==null?0:data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if(i== AdapterView.INVALID_POSITION||data==null||!data.moveToPosition(i))
                {
                    return null;
                }
                RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.list_item_quote);
                data.moveToPosition(i);
                String symbol=data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
                String bid=data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
                String change=data.getString(data.getColumnIndex(QuoteColumns.CHANGE));

                remoteViews.setTextViewText(R.id.stock_symbol,symbol);
                remoteViews.setTextViewText(R.id.change,change);
                remoteViews.setTextViewText(R.id.bid_price,bid);


                final Intent fillInIntent = new Intent(getPackageName());
                fillInIntent.putExtra("symbol",symbol);
                remoteViews.setOnClickFillInIntent(R.id.list_item1,fillInIntent);
                return remoteViews;
            }


            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);

            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {if (data.moveToPosition(i))
                return data.getLong(0);
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

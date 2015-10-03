package com.MeuPonto;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class TelaWidget extends AppWidgetProvider{

	public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget"; 
	
	String myUri = "content://com.MeuPonto.MeuPontoProvider/FolhaPonto";
	Uri contentUri = Uri.parse(myUri);
	Context context;
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		//Toast.makeText(context, "onUpdate", Toast.LENGTH_SHORT).show();
		this.context = context;
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		
		Intent active = new Intent(context, TelaWidget.class);
		active.setAction(ACTION_WIDGET_RECEIVER);
		active.putExtra("msg", context.getString(R.string.ponto_batido));
		
		PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
		
		remoteViews.setOnClickPendingIntent(R.id.btn_chegada, actionPendingIntent);

		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		// v1.5 fix that doesn't call onDelete Action
		final String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[] { appWidgetId });
			}
		} else {
			// check, if our Action was called
			if (intent.getAction().equals(ACTION_WIDGET_RECEIVER)) {
				String msg = "null";
				try {
					msg = intent.getStringExtra("msg");
				} catch (NullPointerException e) {
					Log.e("Error", "msg = null");
				}
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				
				//PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
				NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				ContentResolver crInstance = context.getContentResolver();
				
				
				crInstance.insert(contentUri, null);
				/*TODO 
				Notification noty = new Notification.Builder(context)
		         .setContentText(msg)
		         .setSmallIcon(R.drawable.relogio)
		         .build();
				
				notificationManager.notify(1, noty);
				*/
			} else {
				// do nothing
			}
			
			super.onReceive(context, intent);
		}
	}
}

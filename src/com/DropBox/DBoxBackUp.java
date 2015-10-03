package com.DropBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.MeuPonto.R;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
public class DBoxBackUp extends Activity {
	//private static final String TAG = "DBRoulette";

	final static private String APP_KEY = "vslig8l3v3kw3yc";
	final static private String APP_SECRET = "z3c27p1bznur89a";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	/*final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	private AccessTokenPair tokens;
	*/
	private DropboxAPI<AndroidAuthSession> mDBApi;
	//private Button mUpload;
	//private Button mDownload;
	public static final String FILENAME = "/MeuPontobackUp.txt";
	public static final String FILEPATH = Environment.getExternalStorageDirectory()+"/MeuPonto";
	
	public DBoxBackUp(){
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_back_up);
		
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		
		// MyActivity below should be your activity class name
		mDBApi.getSession().startAuthentication(DBoxBackUp.this);

	}
	
	
	protected void onResume() {
	    super.onResume();

	    if (mDBApi.getSession().authenticationSuccessful()) {
	        try {
	            // Required to complete auth, sets the access token on the session
	            mDBApi.getSession().finishAuthentication();

	            //tokens = mDBApi.getSession().getAccessTokenPair();
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	        }
	    }
	}
	
	
	public void doBackUp(View view){
		
		try{
			File dbFile = new File(getString(R.string.db_path));
			FileInputStream fis = new FileInputStream(dbFile);
			
			File file = new File(FILEPATH);
			file.mkdir();

			file = new File(FILEPATH + FILENAME);
			file.createNewFile();


			// Open the empty db as the output stream
			OutputStream output = new FileOutputStream(FILEPATH + FILENAME);

			// Transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer))>0){
				output.write(buffer, 0, length);
			}
			// Close the streams
			output.flush();
			output.close();
			fis.close();
		
		
		UploadFile upDropBox = new UploadFile(this,mDBApi,"",file);
		upDropBox.execute();
		//Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
		
		}catch(Exception e){
			Log.e("ERROR", e.getMessage());
		}
	}
	
	public void restoreBackUp(View view){
		DownloadFile downloadDropBox = new DownloadFile(this,mDBApi);
		downloadDropBox.execute();
	}
	
	
}
package com.MeuPonto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.DropBox.DownloadFile;
import com.DropBox.UploadFile;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class BackUpFragment extends Fragment {
	
	final static private String APP_KEY = "vslig8l3v3kw3yc";
	final static private String APP_SECRET = "z3c27p1bznur89a";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private DropboxAPI<AndroidAuthSession> mDBApi;
	public static final String FILENAME = "/MeuPontobackUp.txt";
	public static final String FILEPATH = Environment.getExternalStorageDirectory()+"/MeuPonto";
	
	public BackUpFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_back_up, container, false);
		
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		Button backUp = (Button) rootView.findViewById(R.id.btn_upload);
		Button restoreBackUp = (Button) rootView.findViewById(R.id.btn_download);
		
		backUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doBackUp(v.getContext());
			}
		});
		
		restoreBackUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				restoreBackUp(v.getContext());
			}
		});	
		// MyActivity below should be your activity class name
		mDBApi.getSession().startAuthentication(rootView.getContext());
		
		return rootView;
	}
	
	public void onResume() {
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
	
	public void doBackUp(Context context){
		
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
		
		
		UploadFile upDropBox = new UploadFile(context,mDBApi,"",file);
		upDropBox.execute();
		//Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
		
		}catch(Exception e){
			Log.e("ERROR", e.getMessage());
		}
	}
	
	public void restoreBackUp(Context context){
		DownloadFile downloadDropBox = new DownloadFile(context,mDBApi);
		downloadDropBox.execute();
	}

}

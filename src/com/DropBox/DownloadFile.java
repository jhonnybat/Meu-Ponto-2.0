/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */


package com.DropBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.MeuPonto.R;
import com.dropbox.client2.DropboxAPI;

/**
 * Here we show getting metadata for a directory and downloading a file in a
 * background thread, trying to show typical exception handling and flow of
 * control for an app that downloads a file from Dropbox.
 */

public class DownloadFile extends AsyncTask<Void, Long, Boolean> {


	private Context mContext;
	private final ProgressDialog mDialog;
	private DropboxAPI<?> mApi;

	private FileOutputStream mFos;

	private boolean mCanceled;
	private Long mFileLen;
	private String mErrorMsg;

	// Note that, since we use a single file name here for simplicity, you
	// won't be able to use this code for two simultaneous downloads.
	//private final static String FILE_NAME = "FolhaPonto";

	public DownloadFile(Context context, DropboxAPI<?> api) {
		// We set the context this way so we don't accidentally leak activities
		mContext = context.getApplicationContext();

		mApi = api;

		mDialog = new ProgressDialog(context);
		mDialog.setMessage("Downloading DB");
		mDialog.setButton("Cancel", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mCanceled = true;
				mErrorMsg = "Canceled";

				// This will cancel the getThumbnail operation by closing
				// its stream
				if (mFos != null) {
					try {
						mFos.close();
					} catch (IOException e) {
					}
				}
			}
		});

		mDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (mCanceled) {
			return false;
		}

		File localFile = new File(DBoxBackUp.FILEPATH + DBoxBackUp.FILENAME);
		FileOutputStream outputStream = null;
		try {
			if (!localFile.exists()) {
				localFile.createNewFile(); //otherwise dropbox client will fail silently
			}                
			outputStream = new FileOutputStream(DBoxBackUp.FILEPATH + DBoxBackUp.FILENAME);
			mApi.getFile( DBoxBackUp.FILENAME, null, outputStream, null);
		} catch (Exception e ) {
			System.out.println("Something went wrong: " + e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {}
			}
		}
		
		try{
			File dbFile = new File(mContext.getString(R.string.db_path));
			FileInputStream fis = new FileInputStream(DBoxBackUp.FILEPATH + DBoxBackUp.FILENAME);
			
			// Open the empty db as the output stream
			OutputStream output = new FileOutputStream(dbFile);

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
		
		
		}catch(Exception e){
			Log.e("ERROR", e.getMessage());
		}
		
		
		return true;

	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		int percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
		mDialog.setProgress(percent);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mDialog.dismiss();
		if (result) {
			// Set the image now that we have it
			//mView.setImageDrawable(mDrawable);
			Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
		} else {
			// Couldn't download it, so show an error
			showToast(mErrorMsg);
		}
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		error.show();
	}


}

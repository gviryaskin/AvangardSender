package com.kristijandraca.backgroundmaillibrary;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.kristijandraca.backgroundmaillibrary.mail.GmailSender;

import java.util.ArrayList;

/**
 * @author Kristijan Dra�a https://plus.google.com/u/0/+KristijanDra�a
 */
public class BackgroundMail {
	String TAG = "Bacground Mail Library";
	String username, password, mailto, subject, body;
	boolean processVisibility = true;
	ArrayList<String> attachments = new ArrayList<String>();
	Context mContext;
    private BackgroundMailListener listener;
	public BackgroundMail(Context context) {
		this.mContext = context;
	}

	public void setGmailUserName(String string) {
		this.username = string;
	}

	public void setGmailPassword(String string) {
		this.password = string;
	}

	public void setProcessVisibility(boolean state) {
		this.processVisibility = state;
	}

	public void setMailTo(String string) {
		this.mailto = string;
	}

	public void setFormSubject(String string) {
		this.subject = string;
	}

	public void setFormBody(String string) {
		this.body = string;
	}

    public void setListener(BackgroundMailListener listener){this.listener=listener;}
	
	public void setAttachment(String attachments) {
		this.attachments.add(attachments);

	}

	public void send() {
		boolean valid = true;
		if (username == null || username.length()==0) {
			Log.e(TAG, "You didn't set Gmail username!");
			valid = false;
		}
		if (password == null || password.length()==0) {
			Log.e(TAG, "You didn't set Gmail password!");
			valid = false;
		}
		if (mailto == null || mailto.length()==0) {
			Log.e(TAG, "You didn't set email recipient!");
			valid = false;
		}
		if (Utils.isNetworkAvailable(mContext) == false) {
			Log.e(TAG, "User don't have internet connection!");
			valid = false;
		}
		if (valid == true) {
			new startSendingEmail().execute();
		}
	}

	public class startSendingEmail extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			if (processVisibility != false) {
				pd = new ProgressDialog(mContext);
				if (listener != null && listener.getSendingProcessMessage()!=null && listener.getSendingProcessMessage().length()!=0) {
					pd.setMessage(listener.getSendingProcessMessage());
				} else {
					Log.d(TAG, "We dont have sending message so we use generic");
					pd.setMessage("Loading...");
				}
				pd.setCancelable(false);
				pd.show();
			}
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				GmailSender sender = new GmailSender(username, password);
				if(!attachments.isEmpty()){
					for (int i = 0; i < attachments.size(); i++) {
							if(attachments.get(i).length()!=0){
								sender.addAttachment(attachments.get(i));
							}
					}
				}
				sender.sendMail(subject, body, username, mailto);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage().toString());
                return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
            if(processVisibility)
                pd.dismiss();

            if (listener != null) {
				listener.onSendingDone(result);
			}else{
                Log.d(TAG,"We dont have sending success message so we use generic");
                String message=result? "Your message was sent successfully." : "Error during sending message";
                Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
            }
			super.onPostExecute(result);
		}
	}

    public interface BackgroundMailListener{
        public String getSendingProcessMessage();
        public void onSendingDone(boolean result);
    }
}

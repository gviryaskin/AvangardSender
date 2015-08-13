package com.gviryaskin.avangardsender;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gviryaskin.avangardsender.util.Formatter;
import com.gviryaskin.avangardsender.util.History;
import com.kristijandraca.backgroundmaillibrary.BackgroundMail;
import com.kristijandraca.backgroundmaillibrary.Utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

//TODO update background library
//TODO make history file piker dialog
//TODO Add History from json file
//TODO Make year graph in History activity
public class MainActivity extends ActionBarActivity implements View.OnClickListener, View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener, TextWatcher, BackgroundMail.BackgroundMailListener {
    private final static String TAG="values";
    public final static String  KEY_FLAT="flat",
                                KEY_EMAIL="email",
                                KEY_PREV_COLD="prev_cold",
                                KEY_PREV_HOT="prev_hot",
                                KEY_CUR_COLD="cur_cold",
                                KEY_DIF_COLD="dif_cold",
                                KEY_CUR_HOT="cur_hot",
                                KEY_DIF_HOT="dif_hot",
                                KEY_FROM="from",
                                KEY_TO="to",
                                KEY_CUSTOM_MAIL ="auto_send";

    private SharedPreferences mSharedPreferences;
    private Button mSend;
    private EditText mFlat,
                     mEmail,
                     mFrom,
                     mTo,
                     mPrevCold,
                     mPrevHot,
                     mCurCold,
                     mCurHot;
    private TextView mDifCold,
                     mDifHot;

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setContentView(R.layout.activity_main);
        mSharedPreferences=getSharedPreferences(TAG,MODE_PRIVATE);
        mSend= (Button) findViewById(R.id.main_send);
        mSend.setOnClickListener(this);
        mFlat= (EditText) findViewById(R.id.main_flat);
        mEmail= (EditText) findViewById(R.id.main_email);
        mFrom=(EditText) findViewById(R.id.main_from);
        mFrom.setOnFocusChangeListener(this);
        mTo=(EditText)findViewById(R.id.main_to);
        mTo.setOnFocusChangeListener(this);
        mPrevCold= (EditText) findViewById(R.id.main_prev_cold);
        mPrevCold.addTextChangedListener(this);
        mPrevHot= (EditText) findViewById(R.id.main_prev_hot);
        mPrevHot.addTextChangedListener(this);
        mCurCold= (EditText) findViewById(R.id.main_cur_cold);
        mCurCold.addTextChangedListener(this);
        mCurHot= (EditText) findViewById(R.id.main_cur_hot);
        mCurHot.addTextChangedListener(this);
        mDifCold =(TextView) findViewById(R.id.main_dif_cold);
        mDifHot= (TextView) findViewById(R.id.main_dif_hot);
        if(aSavedInstanceState==null){
            resetValues();
        }else{
            mFlat.setText(aSavedInstanceState.getString(KEY_FLAT));
            mEmail.setText(aSavedInstanceState.getString(KEY_EMAIL));
            mFrom.setText(aSavedInstanceState.getString(KEY_FROM));
            mTo.setText(aSavedInstanceState.getString(KEY_TO));
            mPrevCold.setText(aSavedInstanceState.getString(KEY_PREV_COLD));
            mPrevHot.setText(aSavedInstanceState.getString(KEY_PREV_HOT));
            mCurCold.setText(aSavedInstanceState.getString(KEY_CUR_COLD));
            mCurHot.setText(aSavedInstanceState.getString(KEY_CUR_HOT));
            calcDiffs(mCurCold, mPrevCold, mDifCold);
            calcDiffs(mCurHot, mPrevHot, mDifHot);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle aOutState) {
        super.onSaveInstanceState(aOutState);
        aOutState.putString(KEY_FLAT, mFlat.getText().toString());
        aOutState.putString(KEY_EMAIL, mEmail.getText().toString());
        aOutState.putString(KEY_FROM, mFrom.getText().toString());
        aOutState.putString(KEY_TO, mTo.getText().toString());
        aOutState.putString(KEY_PREV_COLD, mPrevCold.getText().toString());
        aOutState.putString(KEY_PREV_HOT, mPrevHot.getText().toString());
        aOutState.putString(KEY_CUR_COLD, mCurCold.getText().toString());
        aOutState.putString(KEY_CUR_HOT, mCurHot.getText().toString());
    }

    private void resetValues(){
        mSharedPreferences=getSharedPreferences(TAG,MODE_PRIVATE);
        String curDate = Formatter.DATE.format(Calendar.getInstance().getTime());
        mFlat.setText(mSharedPreferences.getString(KEY_FLAT,""));
        mEmail.setText(mSharedPreferences.getString(KEY_EMAIL,""));
        mFrom.setText(mSharedPreferences.getString(KEY_FROM,""));
        mTo.setText(curDate);
        mPrevCold.setText(mSharedPreferences.getString(KEY_PREV_COLD,""));
        mPrevHot.setText(mSharedPreferences.getString(KEY_PREV_HOT,""));
        mCurHot.setText("");
        mCurCold.setText("");
        calcDiffs(mCurCold, mPrevCold, mDifCold);
        calcDiffs(mCurHot,mPrevHot,mDifHot);
    }

    private void calcDiffs(EditText aCur, EditText aPrev, TextView aDif){
        String cur=aCur.getText().toString(),
               prev=aPrev.getText().toString();

        String dif= (cur.length()==0 ||prev.length()==0)? "" :
                 Formatter.DECIMAL.format(Double.valueOf(cur) - Double.valueOf(prev));
        aDif.setText(dif);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu aMenu) {
        getMenuInflater().inflate(R.menu.main, aMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem aItem) {
        int id = aItem.getItemId();
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view=getLayoutInflater().inflate(R.layout.dialog_settings,null);
            CheckBox checkBox =((CheckBox) view.findViewById(R.id.settings_mail));
            checkBox.setChecked(mSharedPreferences.getBoolean(KEY_CUSTOM_MAIL,false));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mSharedPreferences.edit().putBoolean(KEY_CUSTOM_MAIL, isChecked).commit();
                }
            });
            builder.setTitle(R.string.action_settings)
                   .setView(view)
                   .setPositiveButton(android.R.string.ok,null)
                   .show();
            return true;
        }else if(id == R.id.action_history){
            ArrayList<Bundle> items = History.getHistory(this);
            if(items.size()==0){
                Toast.makeText(this,R.string.msg_history_empty,Toast.LENGTH_SHORT).show();
                return true;
            }
            HistoryAdapter adapter=new HistoryAdapter(items);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setAdapter(adapter,adapter)
                   .setTitle(R.string.action_history)
                   .setPositiveButton(android.R.string.ok, null)
                   .show();
            return true;
        }
        return super.onOptionsItemSelected(aItem);
    }

    private class HistoryAdapter extends BaseAdapter implements DialogInterface.OnClickListener {
        private ArrayList<Bundle> mItems;
        public HistoryAdapter(ArrayList<Bundle> aItems){
            mItems=aItems;
        }
        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder{
            private TextView mDate,
                     mCold,
                     mHot;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                convertView=getLayoutInflater().inflate(R.layout.item_history,parent,false);
                viewHolder=new ViewHolder();
                viewHolder.mDate=(TextView)convertView.findViewById(R.id.item_history_date);
                viewHolder.mHot=(TextView)convertView.findViewById(R.id.item_history_hot);
                viewHolder.mCold=(TextView)convertView.findViewById(R.id.item_history_cold);
                convertView.setTag(viewHolder);
            }else viewHolder=(ViewHolder)convertView.getTag();
            Bundle bundle= mItems.get(position);
            viewHolder.mDate.setText(bundle.getString(KEY_TO));
            viewHolder.mHot.setText(String.format(
                    getString(R.string.main_history_hot),bundle.getString(KEY_CUR_HOT),
                    bundle.getString(KEY_DIF_HOT)));
            viewHolder.mCold.setText(String.format(
                    getString(R.string.main_history_cold),bundle.getString(KEY_CUR_COLD),
                    bundle.getString(KEY_DIF_COLD)));
            return convertView;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }
    @Override
    public void onClick(View aView) {
        if(aView.equals(mSend)){
            Pair<Boolean,String> result=validateValues();
            if(result.first) {
                if(!Utils.isNetworkAvailable(this)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.error)
                           .setMessage(R.string.msg_ethernet_fail)
                           .setPositiveButton(android.R.string.ok,null)
                           .show();
                    return;
                }
                String subject=String.format(getString(R.string.email_title),mFlat.getText().toString());
                String message;//="Период с %s по %s \nХ.С.В. Предыдущие %s Текущие %s Разница %s\nГ.С.В. Предыдущие %s Текущие %s Разница %s\n\nemail %s";
                message=String.format(getString(R.string.email_message),
                        mFrom.getText().toString(),
                        mTo.getText().toString(),
                        mPrevCold.getText().toString(),
                        mCurCold.getText().toString(),
                        mDifCold.getText().toString(),
                        mPrevHot.getText().toString(),
                        mCurHot.getText().toString(),
                        mDifHot.getText().toString(),
                        mEmail.getText().toString()
                );
                if(!mSharedPreferences.getBoolean(KEY_CUSTOM_MAIL,true)){
                    BackgroundMail backgroundMail=new BackgroundMail(this);
                    backgroundMail.setGmailUserName("gviryaskin@gmail.com");
                    backgroundMail.setGmailPassword("primary01");
                    backgroundMail.setMailTo(String.format("%s,%s", getString(R.string.email_avangard), mEmail.getText().toString()));
                    backgroundMail.setFormSubject(subject);
                    backgroundMail.setFormBody(message);
                    backgroundMail.setListener(this);
                    backgroundMail.send();
                }else{
                    Intent mailIntent = new Intent();
                    mailIntent.setAction(Intent.ACTION_SEND)
                    .setType("message/rfc822")
                    .putExtra(Intent.EXTRA_EMAIL,new String[]{String.format(getResources().getString(R.string.email_avangard))})
                    .putExtra(Intent.EXTRA_SUBJECT,subject)
                    .putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(mailIntent, "Выберите почтовый клиент:"));
                    saveValues();
                    finish();
                }

            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.error)
                       .setMessage(result.second)
                        .setPositiveButton(android.R.string.ok,null)
                        .show();
            }
        }
    }

    private void saveValues(){
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putString(KEY_FLAT,mFlat.getText().toString())
              .putString(KEY_EMAIL,mEmail.getText().toString())
              .putString(KEY_FROM,mTo.getText().toString())
              .putString(KEY_PREV_COLD,mCurCold.getText().toString())
              .putString(KEY_PREV_HOT,mCurHot.getText().toString())
              .commit();
        Bundle bundle=new Bundle();
        bundle.putString(KEY_TO,mTo.getText().toString());
        bundle.putString(KEY_CUR_COLD,mCurCold.getText().toString());
        bundle.putString(KEY_DIF_COLD,mDifCold.getText().toString());
        bundle.putString(KEY_CUR_HOT,mCurHot.getText().toString());
        bundle.putString(KEY_DIF_HOT,mDifHot.getText().toString());
        History.saveToHistory(this,bundle);
    }
    private Pair<Boolean,String> validateValues(){
        Boolean result=true;
        StringBuilder builderError=new StringBuilder(getString(R.string.main_validation_fail));
        result= !validateValue(mFlat.getText().toString(), builderError, R.string.main_flat_fail) ?false:result;
        result= !validateValue(mEmail.getText().toString(), builderError, R.string.main_email_fail) ?false:result;
        result= !validateValue(mFrom.getText().toString(), builderError, R.string.main_from_fail) ?false:result;
        result= !validateValue(mTo.getText().toString(), builderError, R.string.main_to_fail) ?false:result;
        result= !validateValue(mPrevCold.getText().toString(), builderError, R.string.main_prev_cold_fail) ?false:result;
        result= !validateValue(mPrevHot.getText().toString(), builderError, R.string.main_prev_hot_fail) ?false:result;
        result= !validateValue(mCurCold.getText().toString(), builderError, R.string.main_cur_cold_fail) ?false:result;
        result= !validateValue(mCurHot.getText().toString(), builderError, R.string.main_cur_hot_fail) ?false:result;
        return new Pair<Boolean, String>(result,builderError.toString());
    }

    private boolean validateValue(String aValue,StringBuilder aErrorBuilder, int errorStringId){
        if(aValue.length()==0){
            aErrorBuilder.append(getString(errorStringId));
            return false;
        }
        return true;
    }

    @Override
    public void onFocusChange(View aView, boolean aHasFocus) {
        if(aHasFocus){
            if(aView.equals(mFrom)||aView.equals(mTo)){
                Calendar calendar=Calendar.getInstance();
                mDateHolder= ((EditText)aView);
                try {
                    calendar.setTime(Formatter.DATE.parse(((EditText) aView).getText().toString()));
                }catch (Exception e){
                    /*Do nothing */
                }
                DatePickerDialog datePickerDialog=new DatePickerDialog(this,this,calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        }
    }
    private EditText mDateHolder;
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(mDateHolder==null)
            return;
        Calendar calendar =Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        mDateHolder.setText(Formatter.DATE.format(calendar.getTime()));
        mDateHolder=null;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        /*DO NOTHING */
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        /*DO NOTHING */
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s==mPrevCold.getEditableText() ||s==mCurCold.getEditableText())
            calcDiffs(mCurCold,mPrevCold,mDifCold);
        else if(s==mPrevHot.getEditableText() || s==mCurHot.getEditableText())
            calcDiffs(mCurHot,mPrevHot,mDifHot);
    }

    @Override
    public String getSendingProcessMessage() {
        return getString(R.string.msg_email_sending);
    }

    @Override
    public void onSendingDone(boolean result) {
        if(result){
            Toast.makeText(this,R.string.msg_email_sending_success,Toast.LENGTH_SHORT).show();
            saveValues();
            resetValues();
        }else{
            Toast.makeText(this,R.string.msg_email_sending_fail,Toast.LENGTH_SHORT).show();
        }
    }
}

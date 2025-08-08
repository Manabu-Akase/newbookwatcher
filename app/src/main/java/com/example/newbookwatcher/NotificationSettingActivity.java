package com.example.newbookwatcher;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;


public class NotificationSettingActivity extends AppCompatActivity {
    private static final String KEY_NOTIFICATION_SWITCH = "notification_enabled";
    private static final String KEY_NOTIFICATION_SPINNER ="notification_spinner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_setting);


        //スピナーの処理
        Spinner spinner = findViewById(R.id.NotificationDaysSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.NotificationDays_list,
                android.R.layout.simple_spinner_dropdown_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //状態を保存する処理
        SharedPreferences sharedPreferences2 = getSharedPreferences(getString(R.string.PREF_NAME),Context.MODE_PRIVATE);
        int savedPosition = sharedPreferences2.getInt(KEY_NOTIFICATION_SPINNER,0);
        spinner.setSelection(savedPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?>parent , View view , int position , long id){
                SharedPreferences.Editor editor =sharedPreferences2.edit();
                editor.putInt(KEY_NOTIFICATION_SPINNER,position);
                editor.apply();

            }
            @Override
            public void onNothingSelected(AdapterView<?>parent){}
        });

        //スイッチの設定状態を保存する処理
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);

        //スイッチが押された時の処理
        Switch notificationSwitch= findViewById(R.id.NotificationSwitch);
        //保存されている状態を読み込んでスイッチに反映させる
        boolean savedState = sharedPref.getBoolean(KEY_NOTIFICATION_SWITCH,false);

        notificationSwitch.setChecked(savedState);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(KEY_NOTIFICATION_SWITCH,isChecked);

                if (isChecked){
                    Toast.makeText(getApplicationContext(),"通知はONになりました！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"通知はOFFになりました！",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //戻るボタンが押された血の処理→キャンセルボタンが押さらた時とは違っているのでこちらにも設定内容を変更した際のダイアログを入れた方がいいかも。

        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //保存ボタンが押された時の処理

        Button SaveButton = findViewById(R.id.SaveButton);
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isSwitchChecked = notificationSwitch.isChecked();
                int selectedSpinnerPosition = spinner.getSelectedItemPosition();

                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_key),Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_NOTIFICATION_SWITCH,isSwitchChecked);
                editor.putInt(KEY_NOTIFICATION_SPINNER,selectedSpinnerPosition);
                editor.apply();

                Toast.makeText(getApplicationContext(),"通知設定を保存しました！",Toast.LENGTH_SHORT).show();

                }

        });

        //キャンセルボタンが押された時の処理
        Button CancelButton =findViewById(R.id.CancelButton);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(NotificationSettingActivity.this)
                        .setTitle("キャンセルの確認")
                        .setMessage("設定の変更を破棄してもよろしいですか？")
                        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("いいえ",null)
                        .show();
            }
        });

       getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
                boolean savedSwitch = sharedPref.getBoolean(KEY_NOTIFICATION_SWITCH, false);
                int savedSpinner = sharedPref.getInt(KEY_NOTIFICATION_SPINNER, 0);

                Switch notificationSwitch = findViewById(R.id.NotificationSwitch);
                Spinner spinner = findViewById(R.id.NotificationDaysSpinner);

                boolean currentSwitch = notificationSwitch.isChecked();
                int currentSpinner = spinner.getSelectedItemPosition();

                //スピナー、スイッチどちらかの状態が変わったならアラートダイアログで変更確認を表示。
                if (savedSwitch != currentSwitch || savedSpinner != currentSpinner) {
                    new AlertDialog.Builder(NotificationSettingActivity.this)

                            .setCancelable(false)
                            .setTitle("確認")
                            .setMessage("変更内容を破棄してもよろしいですか。")
                            .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            })
                            .show();
                } else {
                    finish();
                }
            }
        });
    }

    /*onBackPressed()が非推奨のため削除

    @Override
    public void onBackPressed() {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_key),Context.MODE_PRIVATE);
        boolean savedSwitch = sharedPref.getBoolean(KEY_NOTIFICATION_SWITCH,false);
        int savedSpinner = sharedPref.getInt(KEY_NOTIFICATION_SPINNER,0);

        Switch notificationSwitch = findViewById(R.id.NotificationSwitch);
        Spinner spinner = findViewById(R.id.NotificationDaysSpinner);

        boolean currentSwitch = notificationSwitch.isChecked();
        int currentSpinner = spinner.getSelectedItemPosition();

        if (savedSwitch!=currentSwitch || savedSpinner != currentSpinner) {
            new AlertDialog.Builder(NotificationSettingActivity.this)

                    .setCancelable(false)
                    .setTitle("確認")
                    .setMessage("変更内容を破棄してもよろしいですか。")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

    }else{
        super.onBackPressed();

        }
    }
    */
}
package com.example.newbookwatcher;
import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;


public class NotificationSettingActivity extends AppCompatActivity {
    private static final String KEY_NOTIFICATION_SWITCH = "notification_enabled";
    private static final String KEY_NOTIFICATION_SPINNER ="notification_spinner";
    private static final String PREF_NAME = "NotificationSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_setting);
        NotificationHelper.createNotificationChannel(this);


        //スピナーの処理
        Spinner spinner = findViewById(R.id.NotificationDaysSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.NotificationDays_list,
                android.R.layout.simple_spinner_dropdown_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //スピナーの設定状態を保存する処理
        SharedPreferences sharedPreferences2 = getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        int savedPosition = sharedPreferences2.getInt(KEY_NOTIFICATION_SPINNER,0);
        spinner.setSelection(savedPosition);

        //スイッチの設定状態を保存する処理
        SharedPreferences sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

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
                editor.apply();

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
                int daysBefore = Integer.parseInt(spinner.getSelectedItem().toString());

                SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_NOTIFICATION_SWITCH,isSwitchChecked);
                editor.putInt(KEY_NOTIFICATION_SPINNER,spinner.getSelectedItemPosition());
                editor.putInt("daysBefore", daysBefore);
                editor.apply();

                Toast.makeText(getApplicationContext(),"通知設定を保存しました！",Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppDatabase db = AppDatabase.getInstance(NotificationSettingActivity.this);
                        List<Book> favoriteBooks = db.bookDao().getFavoriteBookWithReleaseDate();

                        for (Book book : favoriteBooks){
                            if(book.release_date != null ){
                                long releaseTime = book.release_date.getTime();
                                long notifytime = releaseTime - daysBefore * 24L * 60L * 60L * 1000L ;

                                if (notifytime > System.currentTimeMillis()){
                                    scheduleNotification(
                                            "発売" + daysBefore + "日前",
                                            book.title + "がもうすぐ発売されます！",
                                            notifytime,
                                            book.bookId
                                    );
                                }
                            }
                        }
                    }
                }).start();

                //選んだ日数前に通知タイミングを設定する処理
               long triggerTime = System.currentTimeMillis() + 10 * 1000 ;

                NotificationHelper.showNotification(NotificationSettingActivity.this,"テスト通知","通知が正しく表示されています");

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                    if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                        //設定画面に遷移
                        //クラッシュ Android12以上　→　設定画面に遷移 Android11以下　→スキップ
                        try {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        return;
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(NotificationSettingActivity.this,"アラームの設定をひらけませんでした",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                Intent intent = new Intent(NotificationSettingActivity.this,NotificationReceiver.class);
                intent.putExtra("title","新刊通知");
                intent.putExtra("message","保存したスケジュールでテスト");

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        NotificationSettingActivity.this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                if(alarmManager != null){
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent
                    );
                }
                scheduleNotification("新刊通知","保存したスケジュール", triggerTime ,0);
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

                SharedPreferences sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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

       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
               ActivityCompat.requestPermissions(this,
                       new String[]{Manifest.permission.POST_NOTIFICATIONS},
                       1001);
           }
       }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if(requestCode==1001){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.showNotification(this, "テスト通知", "通知が許可されました");
            }else{
                Toast.makeText(this,"通知権限が拒否されました",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void scheduleNotification(String title , String message , long triggerTime , int requestCode){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title" , title);
        intent.putExtra("message",message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (alarmManager != null ){
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }
}
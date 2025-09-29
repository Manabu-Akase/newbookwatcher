package com.example.newbookwatcher;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReminderScheduler {
    public static final String PREF_NAME = "NotificationSettings";
    public static final String KEY_NOTIFICATION_SPINNER = "notification_spinner";

    //スピナー値の何日前に通知するかの定義
    public static int getDaysBefore(Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        int pos = sharedPreferences.getInt(KEY_NOTIFICATION_SPINNER , 0 );
        return Math.max(1,Math.min(7, pos +1));
    }
    //楽天APIから取得した書籍の発売日をミリ秒に変換
    public static long parseRakutenSalesDateToMillis(String salesDate){
        if(salesDate == null)
            return  -1;
        //年/月/日を取得
        Pattern p = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日");
        Matcher m = p.matcher(salesDate);
        if(!m.find()) return -1 ;
        int y = Integer.parseInt(m.group(1));
        int mo = Integer.parseInt(m.group(2));
        int d = Integer.parseInt(m.group(3));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR , y);
        cal.set(Calendar.MONTH , mo-1);
        cal.set(Calendar.DAY_OF_MONTH , d);
        cal.set(Calendar.HOUR_OF_DAY , 9);
        cal.set(Calendar.MINUTE , 0 );
        cal.set(Calendar.SECOND , 0);
        cal.set(Calendar.MILLISECOND , 0);



        return cal.getTimeInMillis();
    }
    //ISBNごとにrequestCodeを作る
    private static int requestCodeOf(String  isbn){
        return (isbn == null ? 0 : isbn.hashCode());
    }
    public static void scheduleReminder(Context ctx , Book book , String salesDateFromApi){
        if(book == null || book.isbn == null ) return;

        long releaseAt =
                (book.release_date != null ) ? book.release_date.getTime() : parseRakutenSalesDateToMillis(salesDateFromApi);
        if(releaseAt <= 0 )return;

        int daysBefore = getDaysBefore(ctx);
        long triggerAt = releaseAt - daysBefore * 24L * 60L * 60L * 1000L ;
        //すでに過ぎている時刻はスキップ
        if (triggerAt <= System.currentTimeMillis()){
            return;
        }

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx , NotificationReceiver.class)
                .putExtra("title",book.title + "の発売日が近づきました！")
                .putExtra("message", daysBefore + "日前になりました。");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCodeOf(book.isbn),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if(am != null){
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
                } else {
                    am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
                }
            } catch(SecurityException e){
                    am.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
            }
        }
    }
    public static void scheduleReminder(Context ctx, Book book, long notifyTime) {
        if (book == null || book.isbn == null) return;

        // すでに過ぎている時刻はスキップ
        if (notifyTime <= System.currentTimeMillis()) {
            return;
        }

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx , NotificationReceiver.class)
                .putExtra("title", book.title + "の発売日が近づきました！")
                .putExtra("message", "発売" + getDaysBefore(ctx) + "日前になりました。");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCodeOf(book.isbn),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (am != null) {
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntent);
                } else {
                    am.setExact(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntent);
                }
            } catch(SecurityException e){
                am.set(AlarmManager.RTC_WAKEUP, notifyTime, pendingIntent);
            }
        }
    }
    public static void cancelBookReminder(Context ctx , String isbn){
        if(isbn == null)return;
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx , NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,requestCodeOf(isbn),intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (am != null) {
            am.cancel(pendingIntent);
        }
    }
    public static void scheduleTestReminder(Context ctx, Book book) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        // Android 12以上 → 正確なアラームが許可されているか確認
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!am.canScheduleExactAlarms()) {
                // 許可がなければリターン、または設定画面に飛ばす
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(android.net.Uri.parse("package:" + ctx.getPackageName()));
                ctx.startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(ctx, NotificationReceiver.class)
                .putExtra("title", "テスト通知")
                .putExtra("message", book.title + " のテスト通知です");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerAt = System.currentTimeMillis() + 10 * 1000; // 10秒後
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            // fallback処理（例えば通常の set() に切り替える）
            am.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        }
    }
}

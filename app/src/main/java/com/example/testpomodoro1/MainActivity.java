package com.example.testpomodoro1;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 1500000; //1500000
    private static final long START_TIME_IN_MILLIS2 = 300000;  //300000
    private TextView mTextViewCountDown;
    private TextView SessionTextView;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button b1;
    private Button b2;
    private Button buttonShowCustomDialog;
    private ImageView b3;
    private ImageView b4;
    private ImageView b5;
    private ImageView b6;
    private ImageView b7;
    private ImageView ring;
    private Dialog myDialog;
    private TextView session_View;
    int count = 1;
    Animation rotate;


    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;

    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private long mTimeLeftInMillis2 = START_TIME_IN_MILLIS2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        myDialog = new Dialog(this);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        SessionTextView = findViewById(R.id.session);
        session_View = findViewById(R.id.sessionView);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        b7 = findViewById(R.id.ig);
        b6 = findViewById(R.id.popUp);
        b5 = findViewById(R.id.mail);
        b4 = findViewById(R.id.facebook);
        b3 = findViewById(R.id.ShareButton);
        b1 = findViewById(R.id.BreakTimer);
        b2 = findViewById(R.id.StartBreak);
        buttonShowCustomDialog = findViewById(R.id.buttonShowCustomDialog);
        b1.setVisibility(View.INVISIBLE);
        b2.setVisibility(View.INVISIBLE);

        // import font
        Typeface MLight = Typeface.createFromAsset(getAssets(), "fonts/ML.ttf");
        Typeface MMedium = Typeface.createFromAsset(getAssets(), "fonts/MM.ttf");

        //implementing fonts
        b1.setTypeface(MMedium);
        b2.setTypeface(MMedium);
        mButtonStartPause.setTypeface(MMedium);
        mButtonReset.setTypeface(MMedium);
        SessionTextView.setTypeface(MLight);
        buttonShowCustomDialog.setTypeface(MMedium);


        //toast for focus
        buttonShowCustomDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleableToast.makeText(MainActivity.this, "Focus and get the work done!", R.style.toastCustom1).show();

            }
        });

        //ring
        ring = findViewById(R.id.ring);
        //animation
        rotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotation);

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","sboro2899@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Please let me know if you like the app!");
                intent.putExtra(Intent.EXTRA_TEXT, "Please let me know if you like the app!");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = openFacebook(MainActivity.this);
                startActivity(facebookIntent);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "//LINK TO THE APP";
                String shareSub = "Subject";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share Using!"));
            }
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/middlechild.x/");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/")));
                }
            }
        });

        //NormalTimer
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ring.startAnimation(rotate);
                if (mTimerRunning) {
                    pauseTimer();
                    rotate.cancel();
                } else {
                    startTimer();
                    ring.startAnimation(rotate);
                    b1.setVisibility(View.INVISIBLE);
                }
            }
        });

        //BreakTimerStartPause
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer2();
                    rotate.cancel();
                } else {
                    startTimer2();
                    ring.startAnimation(rotate);
                    b1.setVisibility(View.INVISIBLE);
                }
            }
        });

        //ResetTimer
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session_View.setText("Work Session");
                b2.setVisibility(View.INVISIBLE);
                b1.setVisibility(View.VISIBLE);
                resetTimer();
                rotate.cancel();
            }
        });
        updateCountDownText();


        //BreakTimerReset
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (count){
                    case 1:
                        session_View.setText("Short Break Session");
                        session_View.setTextSize(35);
                        resetTimer2();
                        b1.setVisibility(View.INVISIBLE);
                        b2.setVisibility(View.VISIBLE);
                        mButtonStartPause.setVisibility(View.INVISIBLE);
                        SessionTextView.setText("3 sessions left until big break!");
                        break;
                    case 2:
                        session_View.setText("Short Break Session");
                        session_View.setTextSize(35);
                        resetTimer2();
                        b1.setVisibility(View.INVISIBLE);
                        b2.setVisibility(View.VISIBLE);
                        mButtonStartPause.setVisibility(View.INVISIBLE);
                        SessionTextView.setText("2 sessions left until big break!");
                        break;
                    case 3:
                        session_View.setText("Short Break Session");
                        session_View.setTextSize(35);
                        resetTimer2();
                        b1.setVisibility(View.INVISIBLE);
                        b2.setVisibility(View.VISIBLE);
                        mButtonStartPause.setVisibility(View.INVISIBLE);
                        SessionTextView.setText("Final session left until big break!");
                        break;
                    case 4:
                        session_View.setText("Short Break Session");
                        session_View.setTextSize(35);
                        resetTimer2();
                        b1.setVisibility(View.INVISIBLE);
                        b2.setVisibility(View.VISIBLE);
                        mButtonStartPause.setVisibility(View.INVISIBLE);
                        //SessionTextView.setText("Final session left");
                        break;
                }
                count++;
                if (count == 5){
                    Intent toNext = new Intent(MainActivity.this, MainActivity2.class);
                    startActivity(toNext);
                    finish();
                }
            }
        });
        //updateCountDownText2();
    }

    //Notification Channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //StartTimer
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                rotate.cancel();
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_ONE_SHOT);
                //Notification1
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "123")
                        .setSmallIcon(R.drawable.timer)
                        .setAutoCancel(true)
                        .setContentTitle("Work Session")
                        .setContentIntent(pendingIntent)
                        .setContentText("Work Session Up! Get ready for the next one")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                notificationManager.notify(1, builder.build());

                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("Pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    //StartTimer2
    private void startTimer2(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis2, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis2 = millisUntilFinished;
                updateCountDownText2();
            }

            @Override
            public void onFinish() {
                rotate.cancel();
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, myIntent, PendingIntent.FLAG_ONE_SHOT);
                //Notification2
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "123")
                        .setSmallIcon(R.drawable.timer)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Short Break Session")
                        .setContentText("Short Break is up! Return to work!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                notificationManager.notify(2, builder.build());
                mTimerRunning = false;
                b2.setText("Start Break");
                b2.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerRunning = true;
        b2.setText("Pause break");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    //pauseTimer
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
    }
    //pauseTimer2
    private void pauseTimer2() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        b2.setText("Start Break");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    //resetTimer
    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    //resetTimer2
    private void resetTimer2() {
        mTimeLeftInMillis2 = START_TIME_IN_MILLIS2;
        updateCountDownText2();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    //updateCountdownText2
    private void updateCountDownText2() {
        int minutes = (int) (mTimeLeftInMillis2 / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis2 / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown. setText(timeLeftFormatted);
    }

    //updateCountdownText
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    //info popup
    public void showPopUp (View v){
        TextView txtClose;
        ImageView bulb;
        myDialog.setContentView(R.layout.custom_popup);
        txtClose = myDialog.findViewById(R.id.txtClose);
        bulb = myDialog.findViewById(R.id.bulb);
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.show();

        bulb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://en.m.wikipedia.org/wiki/Pomodoro_Technique"));
                startActivity(intent);
            }
        });
    }

    public static Intent openFacebook(Context context) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/404slimboysavage"));
        } catch (Exception e){
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/404slimboysavage"));
        }

    }

}

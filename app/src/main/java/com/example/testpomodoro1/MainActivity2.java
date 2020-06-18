package com.example.testpomodoro1;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.testpomodoro1.MainActivity.openFacebook;

public class MainActivity2 extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 900000;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button b3;
    private ImageView mailButton;
    private ImageView shareButton;
    private ImageView facebookButton;
    private ImageView igButton;
    private ImageView infoButton;
    private TextView break_session_text;
    private TextView breakSessionView;
    private ImageView ring;
    private Button buttonShowCustomDialog;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private Dialog myDialog;
    Animation rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main2);
        createNotificationChannel();

        myDialog = new Dialog(this);

        // import font
        Typeface MLight = Typeface.createFromAsset(getAssets(), "fonts/ML.ttf");
        Typeface MMedium = Typeface.createFromAsset(getAssets(), "fonts/MM.ttf");

        break_session_text = findViewById(R.id.break_session_text);
        break_session_text.setTypeface(MLight);


        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        //media links
        igButton = findViewById(R.id.ig);
        shareButton = findViewById(R.id.forward);
        facebookButton = findViewById(R.id.fb);
        mailButton = findViewById(R.id.mail);
        infoButton = findViewById(R.id.popUp);
        buttonShowCustomDialog = findViewById(R.id.buttonShowCustomDialog);

        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        b3 = findViewById(R.id.WorkTimer);
        breakSessionView = findViewById(R.id.breakSessionView);


        break_session_text.setText("Big Break! To return click work!");
        break_session_text.setVisibility(View.VISIBLE);

        //implementing fonts
        mButtonStartPause.setTypeface(MMedium);
        mButtonReset.setTypeface(MMedium);
        buttonShowCustomDialog.setTypeface(MMedium);

        //toast for focus
        buttonShowCustomDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleableToast.makeText(MainActivity2.this, "Grab a coffee!", R.style.toastCustom2).show();

            }
        });

        //instagram
        igButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/middlechild.x/");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/xxx")));
                }
            }
        });

        //facebook
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = openFacebook(MainActivity2.this);
                startActivity(facebookIntent);
            }
        });

        //mail
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","sboro2899@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Please let me know if you like the app!");
                intent.putExtra(Intent.EXTRA_TEXT, "Please let me know if you like the app!");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });

        //forward
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "//LINK TO THE APP";
                String shareSub = "Like and share my App!";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share Using!"));
            }
        });

        //ring
        ring = findViewById(R.id.ring);
        //animation
        rotate = AnimationUtils.loadAnimation(MainActivity2.this, R.anim.rotation);

        b3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toPrevWin = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(toPrevWin);

            }
        });
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                    rotate.cancel();
                } else {
                    startTimer();
                    ring.startAnimation(rotate);
                }
            }
        });
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        updateCountDownText();
        rotate.cancel();
    }
    //Notification Channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("12345", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

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
                //Notification3
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity2.this, "123")
                        .setSmallIcon(R.drawable.timer)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Break Session")
                        .setContentText("Your long break just got over! Please return to work!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity2.this);
                notificationManager.notify(3, builder.build());
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();


        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

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

    //dialog popup
    public void dialogPopup (View v){
        TextView record, focus, break_time, txtclose;
        EditText focus_text, break_time_text;
        Button save_btn, cancel_btn;

        myDialog.setContentView(R.layout.custom_dialog);
        record = myDialog.findViewById(R.id.dialogTitle);
        focus = myDialog.findViewById(R.id.focus);
        break_time = myDialog.findViewById(R.id.break_);
        break_time_text = myDialog.findViewById(R.id.breakText);
        focus_text = myDialog.findViewById(R.id.focusText);
        cancel_btn = myDialog.findViewById(R.id.cancelBtn);
        save_btn = myDialog.findViewById(R.id.saveBtn);
        txtclose = myDialog.findViewById(R.id.txtCloses);

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();

    }


}
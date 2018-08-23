package com.htetznaing.vivomyanmarfonts;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.htetznaing.vivomyanmarfonts.Utils.CustomSpanTypeface;
import com.htetznaing.vivomyanmarfonts.Utils.FontToolkit.ReadTTF;
import com.htetznaing.vivomyanmarfonts.Utils.TextToImage;
import com.htetznaing.vivomyanmarfonts.Utils.myWorker;

import java.io.File;
import java.util.List;

import me.myatminsoe.mdetect.MDetect;
import me.myatminsoe.mdetect.MMToast;

import static com.htetznaing.vivomyanmarfonts.Constants.FONT_PATH;
import static com.htetznaing.vivomyanmarfonts.Constants.KEY_PATH;
import static com.htetznaing.vivomyanmarfonts.Constants.ROOT_PATH;
import static com.htetznaing.vivomyanmarfonts.Constants.TTF_PATH;

public class ReceivedActivity extends AppCompatActivity {
    int count =0;
    myWorker fucker = new myWorker();
    TextView title,tvIcon,tvGuide;
    Typeface typeface;
    Button btnInstall,btnChange;
    List<ActivityManager.RunningAppProcessInfo> processes;
    ActivityManager am;
    InterstitialAd interstitialAd;
    AdRequest adRequest;
    AdView banner;
    int showAds_code = 007;
    String mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received);

        initAds();
        MDetect.INSTANCE.init(this);
        am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        count = getIntent().getIntExtra("count",0);

        mTitle = ReadTTF.getNames(new File(TTF_PATH).listFiles()[count].toString());
        typeface = Typeface.createFromFile(new File(TTF_PATH).listFiles()[count]);
        title = findViewById(R.id.title);
        title.setText(mTitle);
        title.setTypeface(typeface);

        btnChange = findViewById(R.id.btnChange);
        btnChange.setTypeface(typeface);

        btnInstall = findViewById(R.id.btnInstall);
        btnInstall.setTypeface(typeface);

        tvIcon = findViewById(R.id.tvIcon);
        tvIcon.setTypeface(typeface);

        tvGuide = findViewById(R.id.tvGuide);
        tvGuide.setTypeface(typeface);
        tvGuide.setText("Install ကိုႏွိပ္ပါ။\n" +
                "ၿပီးရင္ Change Font ကိုႏွိပ္ပါ။\n" +
                ""+mTitle+" ကိုေ႐ြးၿပီး Apply လုပ္ေပးလိုက္ပါ။");
//        new install().execute();
    }

    public void changeFont(View view) {
        processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            if (info.processName.equalsIgnoreCase("com.bbk.theme")) {
                android.os.Process.killProcess(info.pid);
                android.os.Process.sendSignal(info.pid, android.os.Process.SIGNAL_KILL);
                am.killBackgroundProcesses(info.processName);
                am.restartPackage("com.bbk.theme");
            }
        }

        File file = new File(FONT_PATH+mTitle.replace(" ","")+".itz");
        if (file.exists()){
            try{
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.bbk.theme");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }catch (Exception e){
                    Intent localIntent = new Intent(Intent.ACTION_MAIN);
                    localIntent.setComponent(new ComponentName("com.bbk.theme", "com.bbk.theme.Theme"));
                    localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    localIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(localIntent);
                }

            }catch(Exception outerEx){
                MMToast.INSTANCE.showShortToast(this,"သင့်ဖုန်းသည် Vivo အမျိုးအစားမဟုတ်ဟု ယူဆရပါသည်။");
            }
        }else{
            MMToast.INSTANCE.showShortToast(this,"ပထမဦးစွာဖောင့်ထည့်သွင်းပေးရန် လိုအပ်သည်ပါ။");
        }
    }

    public void installFont(View view) {
        SpannableString mMessage = new SpannableString("သင့္ဖုန္းထဲကို "+mTitle+" ေဖာင့္\nထည့္သြင္းမွာေသခ်ာပါသလား ?");
        mMessage.setSpan(new CustomSpanTypeface("" , typeface), 0 , mMessage.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString mTitle = new SpannableString("အသိေပးခ်က္");
        mTitle.setSpan(new CustomSpanTypeface("" , typeface), 0 , mTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setCancelable(false)
                .setPositiveButton("ထည့္သြင္းမည္", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new install().execute();
                    }
                })
                .setNegativeButton("မထည့္ေတာ့ပါ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        showAds();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface Adialog) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(typeface);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(typeface);
            }
        });
        dialog.show();
    }

    class install extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            File font = new File(TTF_PATH).listFiles()[count];
            File key = new File(KEY_PATH).listFiles()[count];

            fucker.copy(key.toString(),ROOT_PATH+key.getName());
            fucker.createDirectory(ROOT_PATH+"TEMP");
            fucker.unZip(ROOT_PATH+key.getName(),ROOT_PATH+"TEMP");
            fucker.deleteFile(ROOT_PATH+key.getName());

             File [] files = new File(ROOT_PATH+"TEMP/fonts/").listFiles();
             for (int i=0;i<files.length;i++){
                 String name = files[i].getName();
                 files[i].delete();
                 fucker.copy(font.toString(),ROOT_PATH+"TEMP/fonts/"+name);
             }

            Typeface typeface = Typeface.createFromFile(font);
            TextToImage textToImage = new TextToImage();
             String name = ReadTTF.getNames(font.toString());
            String text = name + "\nCreated by zFont" + "\n\nABCDEFGHIJKLMNOPQRSTUVWXYZ\nabcdefghijklmnopqrstuvwxyz\n1234567890!@#$%^&*()";
            Bitmap bitmap = textToImage.thumb(text, 30, 0, Color.BLACK, typeface);
            fucker.deleteFile(ROOT_PATH+"TEMP/preview/preview_fonts_0.jpg");
            textToImage.saveImage(bitmap,ROOT_PATH+"TEMP/preview/preview_fonts_0.jpg");

            bitmap = textToImage.textAsBitmap(name,40,typeface);
            fucker.deleteFile(ROOT_PATH+"TEMP/preview/preview_fonts_small_0.png");
            textToImage.saveImage(bitmap,ROOT_PATH+"TEMP/preview/preview_fonts_small_0.png");

            fucker.createDirectory(FONT_PATH);

            fucker.zipDirectory(ROOT_PATH+"TEMP",FONT_PATH+name.replace(" ","")+".itz");
            fucker.deleteDirectory(ROOT_PATH+"TEMP");

            return ReadTTF.getNames(font.toString());
        }

        @Override
        protected void onPostExecute(String font) {
            super.onPostExecute(font);
            MMToast.INSTANCE.showShortToast(ReceivedActivity.this,"သင့်ဖုန်းထဲကို "+font+" ထည့်သွင်းပြီးပါပြီ\n" +
                    "ဖောင့်ပြောင်းမည် ကိုနှိပ်ပြီး "+font+" ကို Apply လုပ်ပေးပါ။");
        }
    }

    private void initAds() {
        adRequest = new AdRequest.Builder().build();
        banner = findViewById(R.id.adView);
        banner.loadAd(adRequest);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial));
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdOpened() {
                super.onAdOpened();
                loadAds();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadAds();
            }
        });
    }

    private void loadAds(){
        if (!interstitialAd.isLoaded()){
            interstitialAd.loadAd(adRequest);
        }
    }

    private void showAds(){
        if (interstitialAd.isLoaded()){
            interstitialAd.show();
        }else{
            interstitialAd.loadAd(adRequest);
        }
    }

    @Override
    public void onBackPressed() {
        showAds();
        super.onBackPressed();
    }

    public void gotoDeveloper(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("fb://profile/100011339710114"));
            startActivityForResult(intent,showAds_code);
        }catch (Exception e){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://m.facebook.com/100011339710114"));
            startActivityForResult(intent,showAds_code);
        }
    }

    public void gotoAuthor(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("fb://profile/100003281416337"));
            startActivityForResult(intent,showAds_code);
        }catch (Exception e){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://m.facebook.com/100003281416337"));
            startActivityForResult(intent,showAds_code);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==showAds_code){
            showAds();
        }
    }
}

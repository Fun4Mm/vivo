package com.htetznaing.vivomyanmarfonts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.htetznaing.vivomyanmarfonts.Adapter.ListAdapter;
import com.htetznaing.vivomyanmarfonts.Utils.BuildConfigz;
import com.htetznaing.vivomyanmarfonts.Utils.CheckInternet;
import com.htetznaing.vivomyanmarfonts.Utils.CheckUpdate;
import com.htetznaing.vivomyanmarfonts.Utils.CustomSpanTypeface;
import com.htetznaing.vivomyanmarfonts.Utils.FontToolkit.ReadTTF;
import com.htetznaing.vivomyanmarfonts.Utils.myWorker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.myatminsoe.mdetect.MDetect;
import me.myatminsoe.mdetect.MMToast;

import static com.htetznaing.vivomyanmarfonts.Constants.KEY_PATH;
import static com.htetznaing.vivomyanmarfonts.Constants.ROOT_PATH;
import static com.htetznaing.vivomyanmarfonts.Constants.TTF_PATH;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    myWorker fucker = new myWorker();
    ArrayList<Typeface> typeface = new ArrayList<>();
    InterstitialAd interstitialAd;
    AdRequest adRequest;
    private DownloadManager mDownloadManager;
    private long mDownloadedFileID;
    private DownloadManager.Request mRequest;
    String downloadPath,iThemeAPK;
    int showAds_code = 0007;
    Typeface mm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mm = Typeface.createFromAsset(getAssets(),"mm.ttf");

        if (BuildConfigz.getAppName(this)) {
            initApp();
        }else{
            SpannableString mMessage = new SpannableString("သင္ေဒါင္းယူထားေသာေဆာ့ဝဲမွာ\n" +
                    "ကြၽန္ုပ္တို႔၏ တရားဝင္ေဆာ့ဝဲမဟုတ္ပါ\n" +
                    "ထိုေၾကာင့္ Play Store မွျဖစ္ေစ\n" +
                    "Download  ကိုႏွိပ္၍ျဖစ္ေစ\n" +
                    "ထပ္မံေဒါင္းယူထည့္သြင္းေပးပါ။");
            mMessage.setSpan(new CustomSpanTypeface("" , mm), 0 , mMessage.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableString mTitle = new SpannableString("ဝမ္းနည္းပါတယ္");
            mTitle.setSpan(new CustomSpanTypeface("" , mm), 0 , mTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(mTitle)
                    .setMessage(mMessage)
                    .setCancelable(false)
                    .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://bit.ly/2w63yYt")));
                            finish();
                        }
                    })
                    .setNegativeButton("Play Store", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String playstore = "com.mmz.vivomyanmarfonts";
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + playstore)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + playstore)));
                            }
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void initApp(){
        new CheckUpdate(this,false).check();
        MDetect.INSTANCE.init(this);
        downloadPath = Environment.getExternalStorageDirectory()+"/Download/";
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        iThemeAPK = downloadPath+"iThemez.apk";

        initAds();
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (checkiTheme()) {
                    Intent intent = new Intent(MainActivity.this, ReceivedActivity.class);
                    intent.putExtra("count", i);
                    startActivity(intent);
                    showAds();
                }
            }
        });
        checkPermissions();
    }


    private void initAds() {
        adRequest = new AdRequest.Builder().build();
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

    public void stepOne(){
        fucker.createDirectory(ROOT_PATH);
        fucker.createDirectory(TTF_PATH);
        fucker.createDirectory(KEY_PATH);

        File  ttfTemp[] = new File(TTF_PATH).listFiles();
        if (ttfTemp==null || ttfTemp.length<27) {
            fucker.deleteAllInDirectory(TTF_PATH);
            fucker.Assets2SD(this, "zg.zip", TTF_PATH, "zg.zip");
            fucker.unZip(TTF_PATH + "zg.zip", TTF_PATH);
            fucker.deleteFile(TTF_PATH + "zg.zip");
        }

        File  keyTemp[] = new File(KEY_PATH).listFiles();
        if (keyTemp==null || keyTemp.length<27) {
            fucker.deleteAllInDirectory(KEY_PATH);
            fucker.Assets2SD(this, "z.zip", KEY_PATH, "z.zip");
            fucker.unZip(KEY_PATH + "z.zip", KEY_PATH);
            fucker.deleteFile(KEY_PATH + "z.zip");
        }

        File []file = new File(TTF_PATH).listFiles();
        if (file!=null && file.length>1) {
            for (int i = 0; i < file.length; i++) {
                typeface.add(Typeface.createFromFile(file[i]));
            }
        }

        listView.setAdapter(new ListAdapter(this,getFontName(),typeface));
//        work();
    }

    private boolean checkPermissions() {
        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final List<String> listPermissionsNeeded = new ArrayList<>();
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), 5217);
            return false;
        }
        stepOne();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 5217: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    stepOne();
                } else {
                    checkPermissions();
                    Toast.makeText(this, "You need to Allow Write Storage Permission!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public ArrayList<String> getFontName(){
        ArrayList<String> temp = new ArrayList<>();
        File []file = new File(TTF_PATH).listFiles();
        if (file!=null && file.length>1) {
            for (int i = 0; i < file.length; i++) {
                if (file[i].isFile() && file[i].toString().endsWith(".ttf")) {
                    temp.add(ReadTTF.getNames(file[i].toString()));
                }
            }
        }
        return temp;
    }


    public void work(){
        fucker.Assets2SD(this,"itz.zip",ROOT_PATH,"itz.zip");
        fucker.unZip(ROOT_PATH+"itz.zip",ROOT_PATH);
        fucker.deleteFile(ROOT_PATH+"itz.zip");

        File n [] = new File(ROOT_PATH).listFiles();
        for (int i=0;i<n.length;i++){
            String name = ROOT_PATH+n[i].getName().replace(".itz","");
            File file = new File(name);
            if (!file.exists()){
                file.mkdir();
            }
            fucker.unZip(n[i].toString(),name);
            n[i].delete();

            File fileFontName[] = new File(name+"/fonts/").listFiles();
            for (int p=0;p<fileFontName.length;p++){
                String font = fileFontName[p].getName();
                fucker.deleteFile(name+"/fonts/"+font);
                fucker.Assets2SD(this,"因为爱过.ttf",name+"/fonts/",font);
            }

            fucker.deleteFile(name+"/preview/preview_fonts_0.jpg");
            fucker.deleteFile(name+"/preview/preview_fonts_small_0.png");
            fucker.Assets2SD(this,"preview_fonts_0.jpg",name+"/preview/","preview_fonts_0.jpg");
            fucker.Assets2SD(this,"preview_fonts_small_0.png",name+"/preview/","preview_fonts_small_0.png");
            int count = 1+i;
            fucker.zipDirectory(name,ROOT_PATH+"z"+count+".zip");
            fucker.deleteDirectory(name);
        }
        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (iThemeAPK!=null) {
            checkiTheme();
        }
    }


    private void uninstall(String mPackage){
        MMToast.INSTANCE.showShortToast(this,"သင့်ဖုန်းထဲက iTheme အဟောင်းကိုဖြုတ်ပစ်ပြီးမှ\n" +
                "အသစ်ကိုထပ်မံထည့်သွင်းပေးရန်လိုအပ်ပါသည်။");
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:"+mPackage));
        startActivityForResult(intent,showAds_code);
        showAds();
    }

    public boolean checkiTheme() {
        boolean b = false;
        File file = new File(iThemeAPK);
        String version = null;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.bbk.theme", 0);
            version = packageInfo.versionName;
            version = version.replace(".", "");
            int current = Integer.parseInt(version);
            int need = 4001;
            if (current == need) {
                b = true;
            }

            if (current<need){
                b = false;
                if (file.exists()){
                    openFile(iThemeAPK);
                }else {
                    updateiTheme(false);
                }
            }

            if (current>need){
                b = false;
                if (file.exists()){
                    openFile(iThemeAPK);
                    uninstall("com.bbk.theme");
                }else {
                    updateiTheme(true);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            MMToast.INSTANCE.showShortToast(this,"သင့်ဖုန်းသည် Vivo အမျိုးအစားမဟုတ်ဟု ယူဆရပါသည်။");
            b = true;
            e.printStackTrace();
        }
        return b;
    }

    public void updateiTheme(final boolean uninstall){

        SpannableString mMessage = new SpannableString("ေဖာင့္ေျပာင္းဖို႔အတြက္သင့္ဖုန္းမွာ\n" +
                "iTheme ဗားရွင္း 4.0.0.1 ကို\n" +
                        "Install ေပးဖို႔လိုပါတယ္။\n" +
                        "ေအာက္ပါ Download ခလုတ္ကိုႏွိပ္ၿပီး\n" +
                        "iTheme ဗားရွင္း 4.0.0.1 ကိုေဒါင္းယူပါ။\n" +
                        "ၿပီးရင္ Install လုပ္ေပးၿပီးမွ\n" +
                        "ေဖာင့္ေျပာင္းဖို႔ျပန္လည္ႀကိဳးစားပါ။");
        mMessage.setSpan(new CustomSpanTypeface("" , mm), 0 , mMessage.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);


        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Sorry")
                .setMessage(mMessage)
                .setCancelable(false)
                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (checkInternet(uninstall)) {
                            dlFile("https://github.com/KhunHtetzNaing/Files/releases/download/5/iTheme_Latest.apk", "iThemez.apk");
                            if (uninstall) {
                                uninstall("com.bbk.theme");
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        showAds();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dlFile(String url, String fileName){
        try {
            String mBaseFolderPath = downloadPath;
            if (!new File(mBaseFolderPath).exists()) {
                new File(mBaseFolderPath).mkdir();
            }
            String mFilePath = "file://" + mBaseFolderPath + fileName;
            Uri downloadUri = Uri.parse(url);
            mRequest = new DownloadManager.Request(downloadUri);
            mRequest.setDestinationUri(Uri.parse(mFilePath));
            mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            mDownloadedFileID = mDownloadManager.enqueue(mRequest);
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(downloadReceiver, filter);
            Toast.makeText(this, "Starting Download : "+fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(url)),showAds_code);
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Uri uri = mDownloadManager.getUriForDownloadedFile(mDownloadedFileID);
            iThemeAPK = getRealPathFromURI(uri);
            openFile(iThemeAPK);
        }
    };


    public void openFile(String apk){
        MMToast.INSTANCE.showShortToast(this,"iTheme ကိုထပ်မံ Install ပြုလုပ်ပေးရန်လိုအပ်ပါသည်။");
        File file = new File(apk);
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = file.getName().substring(file.getName().indexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkURI = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            install.setDataAndType(apkURI, type);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else{
            install.setDataAndType(Uri.fromFile(file), type);
        }
        startActivity(install);
    }
    public String getRealPathFromURI (Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==showAds_code){
            showAds();
        }
    }

    @Override
    public void onBackPressed() {
        SpannableString mMessage = new SpannableString("ထြက္ေတာ့မွာေသခ်ာပါသလား ?");
        mMessage.setSpan(new CustomSpanTypeface("" , mm), 0 , mMessage.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString mTitle = new SpannableString("အသိေပးခ်က္");
        mTitle.setSpan(new CustomSpanTypeface("" , mm), 0 , mTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setCancelable(false)
                .setPositiveButton("ထြက္မည္", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAds();
                        finish();
                    }
                })
                .setNegativeButton("မထြက္ပါ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        showAds();
                    }
                })
                .setNeutralButton("အမွတ္ေပးမည္", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        rate();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface Adialog) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(mm);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(mm);
                dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTypeface(mm);
            }
        });
        dialog.show();
    }

    public boolean checkInternet(final boolean uninstall){
        boolean what = false;
        CheckInternet checkNet = new CheckInternet(this);
        if (checkNet.isInternetOn()){
            what = true;
        }else{
            what = false;


            SpannableString mMessage = new SpannableString("သင့္ဖုန္းတြင္အင္တာနက္ခ်ိတ္ဆက္ထားရန္လိုအပ္ပါသည္။ ?");
            mMessage.setSpan(new CustomSpanTypeface("" , mm), 0 , mMessage.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableString mTitle = new SpannableString("အသိေပးခ်က္");
            mTitle.setSpan(new CustomSpanTypeface("" , mm), 0 , mTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(mTitle)
                    .setMessage(mMessage)
                    .setCancelable(false)
                    .setPositiveButton("ထပ္မံႀကိဳးစားမည္", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (checkInternet(uninstall)){
                                dlFile("https://github.com/KhunHtetzNaing/Files/releases/download/5/iTheme_Latest.apk", "iThemez.apk");
                                if (uninstall) {
                                    uninstall("com.bbk.theme");
                                }
                            }
                        }
                    })
                    .setNegativeButton("မလုပ္ေတာ့ပါ", new DialogInterface.OnClickListener() {
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
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(mm);
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(mm);
                }
            });
            dialog.show();

        }

        return what;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                share();
                break;
            case R.id.rate:
                rate();
                break;
            case R.id.update:
                new CheckUpdate(this,true).check();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"Vivo မွာစာလုံးအလွေလးေတြထည့္သြင္းေပးႏိုင္တဲ့\n" +
                "Myanmar Font Style For Vivo ေဆာ့ဝဲေလးပါ။\n" +
                "အခေၾကးေပးစရာမလိုပဲ အခမဲ့ေဒါင္းယူအသုံးျပဳႏိုင္ပါတယ္။\n\nDownload at Google Play Store : play.google.com/store/apps/details?id="+getPackageName()+"\n\nDirect Download : http://bit.ly/2w63yYt\n#mmVideo #MyanmarVideo");
        startActivityForResult(Intent.createChooser(intent,"Share App..."),showAds_code);
    }

    public void rate(){
        View view = getLayoutInflater().inflate(R.layout.rate_view,null);
        TextView tv = view.findViewById(R.id.tv);
        tv.setText("ဒီေဆာ့ဝဲေလးကိုႀကိဳက္ႏွစ္သက္ရင္\n" +
                "Rate Now ကိုႏွိပ္ၿပီး Play Store မွာ\n" +
                "ၾကယ္ ၅ လုံးေပးၿပီး\n" +
                "ႏွစ္သက္ေၾကာင္းေရးေပးခဲ့ပါ။");
        tv.setTypeface(mm);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appPackageName = "com.mmz.vivomyanmarfonts";
                try {
                    startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)),showAds_code);
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)),showAds_code);
                }
            }
        });


        SpannableString mTitle = new SpannableString("ကြၽန္ုပ္တို႔ကိုကူညီပါ");
        mTitle.setSpan(new CustomSpanTypeface("" , mm), 0 , mTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(mTitle)
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String appPackageName = "com.mmz.vivomyanmarfonts";
                        try {
                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)),showAds_code);
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)),showAds_code);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAds();
                    }
                });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

}

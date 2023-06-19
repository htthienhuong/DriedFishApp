package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Locale;

public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    public static Translator Vietnamese2EnglishTranslator;

    private static final TranslatorOptions en2vi = new TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.VIETNAMESE)
            .build();

    private static final TranslatorOptions vi2en = new TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.VIETNAMESE)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build();

    public static void onCreate(Context context) {
        String lang;
        if(getLanguage(context).isEmpty()){
            lang = getPersistedData(context, Locale.getDefault().getLanguage());
        }else {
            lang = getLanguage(context);
        }
        setLocale(context, lang);
    }

    public static void onCreate(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        setLocale(context, lang);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, Locale.getDefault().getLanguage());
    }

    public static void setLocale(Context context, String language) {
        persist(context, language);
        updateResources(context, language);
    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static void showChangeLanguageDialog(Context context, Activity activity) {
        String lang = LocaleHelper.getLanguage(context);
        int defaultChecked;
        switch (lang) {
            case "en" : defaultChecked = 0; break;
            case "vi" : defaultChecked = 1; break;
            default : defaultChecked = -1; break;
        }

        final String[] listItems = {"English", context.getString(R.string.vietnamese)};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle(context.getString(R.string.choose_language));

        mBuilder.setSingleChoiceItems(listItems, defaultChecked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) LocaleHelper.setLocale(
                        context,
                        "en"
                );
                else if (which == 1) LocaleHelper.setLocale(
                        context,
                        "vi"
                );
                activity.recreate();
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    public interface OnTranslateSuccess {
        void returnTranslatedString(String translatedString);
    }

    public static void translate(Context context, String sourceLang, String targetLang, String text, OnTranslateSuccess returner) {
        // Download ML model if needed
        Vietnamese2EnglishTranslator = Translation.getClient(vi2en);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        Vietnamese2EnglishTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Vietnamese2EnglishTranslator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String translatedString) {
                        returner.returnTranslatedString(translatedString);
                        //Vietnamese2EnglishTranslator.close();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TRANSLATION", "Download Model OK, but Translation failed");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TRANSLATION", "Download ML Model failed");
                Toast.makeText(context,
                        context.getResources().getString(R.string.failed_download_translation_model),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}

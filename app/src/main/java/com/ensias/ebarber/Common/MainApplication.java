package com.ensias.ebarber.Common;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;


import com.ensias.ebarber.R;
import com.izikode.izilib.roguin.endpoint.FacebookEndpoint;
import com.izikode.izilib.roguin.endpoint.GoogleEndpoint;
import com.izikode.izilib.roguin.endpoint.TwitterEndpoint;
import com.zeugmasolutions.localehelper.LocaleAwareApplication;

import java.util.Locale;

public class MainApplication extends LocaleAwareApplication {

    public void onCreate(){
        super.onCreate();
        initializeSocialLogin();
//        LocaleHelper.setLocale(new Locale("el"));
//        LocaleHelper.updateConfig(this, getBaseContext().getResources().getConfiguration());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // LocaleHelper.updateConfig(this, newConfig);
    }
    private void initializeSocialLogin(){
        GoogleEndpoint.initialize(this);
        FacebookEndpoint.initialize(this);
        TwitterEndpoint.initialize(this);
    }
}

package com.ensias.ebarber;

import androidx.appcompat.app.AppCompatActivity;

import com.ensias.ebarber.Common.LocaleHelper;
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity;

public class BaseActivity extends LocaleAwareCompatActivity {
    public BaseActivity() {
       // LocaleHelper.updateConfig(this);
    }
}

package net.derohimat.popularmovies.view.fragment.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import net.derohimat.popularmovies.R;

public class MyPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);


    }

}
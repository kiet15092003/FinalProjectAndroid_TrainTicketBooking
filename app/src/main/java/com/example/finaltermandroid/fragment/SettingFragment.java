package com.example.finaltermandroid.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.Resource;
import com.example.finaltermandroid.R;

import java.util.Locale;

public class SettingFragment extends Fragment {

    Spinner spinner;
    public static final String[] languages = {"English","English", "Vietnamese"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                if (position == 0) {

                } else if (position == 1) {
                    setLocal("en");
                } else if (position == 2) {
                    setLocal("vi");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    public void setLocal(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Restart the activity to apply the language change
        requireActivity().recreate();

        if (langCode.equals("vi")) {
            languages[0] = "Tiếng Việt";
            languages[1] = "Tiếng Anh";
            languages[2] = "Tiếng Việt";
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            adapter.notifyDataSetChanged();
        } else {
            languages[0] = "English";
            languages[1] = "English";
            languages[2] = "Vietnamese";
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }

}

package com.qihoo360.replugin.sample.host;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qihoo360.replugin.RePlugin;

public class SimpleFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.framgment_simple, null);
        rootView.findViewById(R.id.start_simple_a_by_package).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_simple_a_by_package:
                // 使用包名打开 Simple A 的 MainActivity
                RePlugin.startActivity(getContext(), RePlugin.createIntent("com.qihoo360.replugin.sample.a", "com.qihoo360.replugin.sample.a.MainActivity"));

//                Intent intent = new Intent();
//                intent.setComponent(new ComponentName("demo1", "com.qihoo360.replugin.sample.demo1.activity.for_result.ForResultActivity"));
//                RePlugin.startActivityForResult(MainActivity.this, intent, REQUEST_CODE_DEMO1, null);
                break;
        }
    }
}

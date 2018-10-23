package com.qihoo360.replugin.sample.host;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qihoo360.replugin.RePlugin;

public class SimpleFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CODE_SIMPLE_A = 0x011;
    private static final int RESULT_CODE_SIMPLE_A = 0x012;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.framgment_simple, null);
        rootView.findViewById(R.id.start_sample_a_by_package).setOnClickListener(this);
        rootView.findViewById(R.id.start_sample_b_by_alias).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_sample_a_by_package:
                // 使用包名打开 Simple A 的 MainActivity
                if (RePlugin.isPluginInstalled("com.qihoo360.replugin.sample.a")) {
                    RePlugin.startActivity(getContext(), RePlugin.createIntent("com.qihoo360.replugin.sample.a", "com.qihoo360.replugin.sample.a.MainActivity"));
                } else {
                    Toast.makeText(getContext(), "插件 Simple A 没有安装", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.start_sample_b_by_alias:
                // 使用别名打开 Simple B 的 MainActivity
                if (RePlugin.isPluginInstalled("sample-b")) {
                    RePlugin.startActivity(getContext(), RePlugin.createIntent("sample-b", "com.qihoo360.replugin.sample.b.MainActivity"));
                } else {
                    Toast.makeText(getContext(), "插件 Simple B 没有安装", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIMPLE_A && resultCode == RESULT_CODE_SIMPLE_A) {
            String result = data.getExtras().getString("data");
            Toast.makeText(getContext(), "来自于 Simple B 的 result " + result, Toast.LENGTH_LONG).show();
        }
    }
}

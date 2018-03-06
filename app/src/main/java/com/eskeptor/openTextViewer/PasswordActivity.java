package com.eskeptor.openTextViewer;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import in.arjsna.passcodeview.PassCodeView;

public class PasswordActivity extends AppCompatActivity {
    private String mPass;
    private boolean isRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        boolean isSetPassword = sharedPreferences.getBoolean(Constant.APP_PASSWORD_SET, false);

        final PassCodeView passCodeView = (PassCodeView)findViewById(R.id.pass_code);
        final TextView txtComment = (TextView)findViewById(R.id.pass_txtComment);

        if (isSetPassword) {
            txtComment.setText("비밀번호 재설정");
        } else {
            txtComment.setText("비밀번호 설정");
        }

        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                if (text.length() == 4) {
                    if (isRepeat) {
                        if (text.equals(mPass)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constant.APP_PASSWORD_KEY, text);
                            editor.putBoolean(Constant.APP_PASSWORD_SET, true);
                            editor.apply();
                            finish();
                        } else {
                            passCodeView.setError(true);
                        }
                    } else {
                        mPass = text;
                        passCodeView.reset();
                        isRepeat = true;
                        txtComment.setText("다시 한 번 입력하세요");
                    }
                }
            }
        });
    }
}

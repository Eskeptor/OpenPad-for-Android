package com.eskeptor.openTextViewer;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import in.arjsna.passcodeview.PassCodeView;


/**
 * Password Test
 * Alpha V0.1
 */
public class PasswordActivity extends AppCompatActivity {
    private String mPass;
    private boolean isRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);

        final PassCodeView passCodeView = findViewById(R.id.pass_code);
        final TextView txtComment = findViewById(R.id.pass_txtComment);

        final int passwordType = getIntent().getIntExtra(Constant.INTENT_EXTRA_PASSWORD, Constant.PasswordIntentType.Set.getValue());

        if (passwordType == Constant.PasswordIntentType.Set.getValue()) {
            txtComment.setText("비밀번호 설정");
        } else if (passwordType == Constant.PasswordIntentType.Reset.getValue()) {
            txtComment.setText("비밀번호 재설정");
        } else {
            txtComment.setText("비밀번호 입력");
        }

        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                if (passwordType == Constant.PasswordIntentType.Execute.getValue()) {
                    String realPass = sharedPreferences.getString(Constant.APP_PASSWORD_KEY, null);
                    if (text.length() == 4) {
                        if (text.equals(realPass)) {
                            getIntent().putExtra(Constant.INTENT_EXTRA_PASSWORD_MATCH, 1);
                            finish();
                        } else {
                            mPass = text;
                            passCodeView.reset();
                            txtComment.setText("비밀번호가 틀렸습니다.");
                        }
                    }
                } else {
                    if (text.length() == 4) {
                        if (isRepeat) {
                            if (text.equals(mPass)) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constant.APP_PASSWORD_KEY, text);
                                editor.putBoolean(Constant.APP_PASSWORD_SET, true);
                                editor.apply();
                                getIntent().putExtra(Constant.INTENT_EXTRA_PASSWORD_SET, 1);
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
            }
        });
    }
}

package com.eskeptor.openTextViewer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import in.arjsna.passcodeview.PassCodeView;
import util.AES256Util;
import util.TestLog;


/**
 * Password Test
 * Alpha V0.1
 */
public class PasswordActivity extends AppCompatActivity {
    private String mPass;
    private boolean isRepeat;
    private AES256Util mAES256;
    private String mKeyToken;
    private int mPasswordType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.APP_SETTINGS_PREFERENCE, MODE_PRIVATE);
        mKeyToken = sharedPreferences.getString(Constant.APP_PASSWORD_KEY, Constant.APP_PASSWORD_KEY_DEFAULT);
        try {
            mAES256 = new AES256Util(mKeyToken);
        } catch (Exception e) {
            TestLog.Tag("PasswordActivity").Logging(TestLog.LogType.ERROR, "AES Init Error: " + e.getMessage());
        }

        final PassCodeView passCodeView = findViewById(R.id.pass_code);
        final TextView txtComment = findViewById(R.id.pass_txtComment);

        mPasswordType = getIntent().getIntExtra(Constant.INTENT_EXTRA_PASSWORD, Constant.PasswordIntentType.Set.getValue());

        if (mPasswordType == Constant.PasswordIntentType.Set.getValue()) {
            txtComment.setText(getString(R.string.password_set));
        } else if (mPasswordType == Constant.PasswordIntentType.Reset.getValue()) {
            txtComment.setText(getString(R.string.password_reset));
        } else {
            txtComment.setText(getString(R.string.password_enter));
        }

        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                if (mPasswordType == Constant.PasswordIntentType.Execute.getValue() ||
                        mPasswordType == Constant.PasswordIntentType.MainExecute.getValue()) {
                    try {
                        String realPass = mAES256.aesDecode(sharedPreferences.getString(Constant.APP_PASSWORD_VALUE, null));
                        if (text.length() == 4) {
                            if (text.equals(realPass)) {
                                Intent intent = new Intent();
                                intent.putExtra(Constant.INTENT_EXTRA_PASSWORD_MATCH, 1);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                mPass = text;
                                passCodeView.reset();
                                txtComment.setText(getString(R.string.password_incorrect));
                            }
                        }
                    } catch (Exception e) {
                        TestLog.Tag("PasswordActivity").Logging(TestLog.LogType.ERROR, "AES Error: " + e.getMessage());
                    }
                } else {
                    if (text.length() == 4) {
                        if (isRepeat) {
                            if (text.equals(mPass)) {
                                try {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(Constant.APP_PASSWORD_VALUE, mAES256.aesEncode(text));
                                    editor.putBoolean(Constant.APP_PASSWORD_SET, true);
                                    editor.apply();
                                    Intent intent = new Intent();
                                    intent.putExtra(Constant.INTENT_EXTRA_PASSWORD_SET, 1);
                                    setResult(RESULT_OK, intent);
                                } catch (Exception e) {
                                    TestLog.Tag("PasswordActivity").Logging(TestLog.LogType.ERROR, "AES Error: " + e.getMessage());
                                }
                                finish();
                            } else {
                                passCodeView.setError(true);
                            }
                        } else {
                            mPass = text;
                            passCodeView.reset();
                            isRepeat = true;
                            txtComment.setText(getString(R.string.password_again));
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAES256 = null;
    }

    @Override
    public void onBackPressed() {
        if (mPasswordType != Constant.PasswordIntentType.MainExecute.getValue()) {
            super.onBackPressed();
        }
    }
}

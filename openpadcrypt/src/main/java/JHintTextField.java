import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class JHintTextField extends JTextField implements FocusListener {
    private String mHint;
    private boolean mShowingHint;

    JHintTextField() {
        super();
        mShowingHint = true;
        super.addFocusListener(this);
    }

    JHintTextField(final String hint) {
        super(hint);
        mHint = hint;
        mShowingHint = true;
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setText("");
            mShowingHint = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setText(mHint);
            mShowingHint = true;
        }
    }

    public String getHintText() {
        return mHint;
    }

    public void setHint(final String hint) {
        mHint = hint;
    }

    @Override
    public String getText() {
        return mShowingHint ? "" : super.getText();
    }
}

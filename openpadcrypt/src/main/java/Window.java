import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Window {
    private final static int KEY = 1;
    private final static int MEMO = 2;
    private final static int BUTTON_WIDTH = 120;
    private final static int BUTTON_HEIGHT = 25;

    private static JFrame mFrame;
    private static File mOpenedKeyFile;
    private static File mOpenedMemoFile;
    private static JLabel mLblOpenKeyFileName;
    private static JLabel mLblOpenMemoFileName;
    private static JButton mBtnOpenKeyFile;
    private static JButton mBtnOpenEncryptedFile;
    private static JButton mBtnDecryption;
    private static JButton mBtnSaveFile;

    private static String mDecryptedContents;

    private static void initWindow() {
        JPanel panUP = new JPanel();
        panUP.setLayout(new FlowLayout(FlowLayout.LEFT));
        mBtnOpenKeyFile = new JButton("Open Key");
        mBtnOpenKeyFile.addActionListener(new OpenActionListener(KEY));
        mBtnOpenKeyFile.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        mLblOpenKeyFileName = new JLabel();
        panUP.add(mBtnOpenKeyFile);
        panUP.add(mLblOpenKeyFileName);

        JPanel panMiddle = new JPanel();
        panMiddle.setLayout(new FlowLayout(FlowLayout.LEFT));
        mBtnOpenEncryptedFile = new JButton("Open Memo");
        mBtnOpenEncryptedFile.addActionListener(new OpenActionListener(MEMO));
        mBtnOpenEncryptedFile.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        mLblOpenMemoFileName = new JLabel();
        panMiddle.add(mBtnOpenEncryptedFile);
        panMiddle.add(mLblOpenMemoFileName);

        JPanel panDown = new JPanel();
        panDown.setLayout(new FlowLayout(FlowLayout.LEFT));
        mBtnDecryption = new JButton("Decryption");
        mBtnDecryption.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        mBtnDecryption.addActionListener(new DecryptionActionListener());
        mBtnDecryption.setEnabled(false);
        mBtnSaveFile = new JButton("Save as");
        mBtnSaveFile.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        mBtnSaveFile.addActionListener(new SaveActionListener());
        mBtnSaveFile.setEnabled(false);
        panDown.add(mBtnDecryption);
        panDown.add(mBtnSaveFile);


        JPanel panMain = new JPanel();
        panMain.setLayout(new GridLayout(3, 1));
        panMain.add(panUP);
        panMain.add(panMiddle);
        panMain.add(panDown);

        mFrame.add(panMain);
    }

    public static void main(String[] args) {
        mFrame = new JFrame("OpenPad Encrypt Tools");
        mFrame.setSize(500, 150);
        mFrame.setResizable(false);
        mFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initWindow();
        mFrame.setVisible(true);
    }

    static class OpenActionListener implements ActionListener {
        private JFileChooser mFileChooser;
        private int mType;

        OpenActionListener(final int type) {
            mFileChooser = new JFileChooser();
            mType = type;
        }

        public void actionPerformed(ActionEvent e) {
            FileNameExtensionFilter filter = null;
            if (mType == KEY) {
                filter = new FileNameExtensionFilter("Key File", "opkdc");
            } else {
                filter = new FileNameExtensionFilter("Memo File", "txt");
            }
            mFileChooser.setFileFilter(filter);

            int ret = mFileChooser.showOpenDialog(null);
            if (ret == JFileChooser.APPROVE_OPTION) {
                if (mType == KEY) {
                    mOpenedKeyFile = mFileChooser.getSelectedFile();
                    mLblOpenKeyFileName.setText(mOpenedKeyFile.getAbsolutePath());
                } else {
                    mOpenedMemoFile = mFileChooser.getSelectedFile();
                    mLblOpenMemoFileName.setText(mOpenedMemoFile.getAbsolutePath());
                }

                if (!mLblOpenMemoFileName.getText().equals("") && !mLblOpenKeyFileName.getText().equals("")) {
                    mBtnDecryption.setEnabled(true);
                }
            }
        }
    }

    static class DecryptionActionListener implements ActionListener {
        private String mContents;
        private String mKey;
        private AES256Util mAES256Util;
        DecryptionActionListener() {

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                mKey = FileManager.read(mOpenedKeyFile);
                mContents = FileManager.read(mOpenedMemoFile);
                System.out.println("key:" + mKey);
                System.out.println("contents: " + mContents);
                mAES256Util = new AES256Util(mKey);

                mDecryptedContents = mAES256Util.aesDecode(mContents);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (mDecryptedContents != null && !mDecryptedContents.equals("")) {
                mBtnSaveFile.setEnabled(true);
            }
        }
    }

    static class SaveActionListener implements ActionListener {
        private JFileChooser mFileChooser;

        SaveActionListener() {
            mFileChooser = new JFileChooser();
        }

        public void actionPerformed(ActionEvent e) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Memo File", "txt");
            mFileChooser.setFileFilter(filter);

            int ret = mFileChooser.showSaveDialog(null);
            if (ret == JFileChooser.APPROVE_OPTION) {
                FileManager.write(mFileChooser.getSelectedFile(), mDecryptedContents);
            }
        }
    }
}

package com.eskeptor.openTextViewer;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.MessageDigest;

/**
 * Created by eskeptor on 17. 1. 25.
 * Copyright (C) 2017 Eskeptor(Jeon Ye Chan)
 */

public class TextManager
{
    private boolean fileopen;
    private boolean saved ;
    private String fileopen_name;
    private String MD5;
    private String format;

    public TextManager()
    {
        initManager();
    }

    public void initManager()
    {
        fileopen = false;
        fileopen_name = "";
        saved = false;
        MD5 = "";
        format = "";
    }

    public String getFileopen_name()
    {
        return fileopen_name;
    }

    public boolean isFileopen()
    {
        return fileopen;
    }

    public String getMD5()
    {
        return MD5;
    }

    public boolean saveText(final String strData, final String filename)
    {
        if(strData == null || strData.isEmpty())
        {
            return false;
        }
        FileOutputStream fos = null;
        FileChannel channel = null;
        ByteBuffer buffer = null;

        if(isFileopen())
        {
            try
            {
                fos = new FileOutputStream(new File(fileopen_name));
                channel = fos.getChannel();
                buffer = ByteBuffer.allocateDirect(strData.getBytes().length);
                buffer.put(strData.getBytes());
                buffer.flip();
                channel.write(buffer);

            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                try{buffer.clear();}
                catch (Exception e){e.printStackTrace();}
                try{channel.close();}
                catch (Exception e){e.printStackTrace();}
                try{fos.close();}
                catch (Exception e){e.printStackTrace();}
            }
        }
        else
        {
            try
            {
                fos = new FileOutputStream(new File(filename));
                channel = fos.getChannel();
                buffer = ByteBuffer.allocateDirect(strData.getBytes().length);
                buffer.put(strData.getBytes());
                buffer.flip();
                channel.write(buffer);

            }
            catch (Exception e) {e.printStackTrace();}
            finally {
                try{buffer.clear();}
                catch (Exception e){e.printStackTrace();}
                try{channel.close();}
                catch (Exception e){e.printStackTrace();}
                try{fos.close();}
                catch (Exception e){e.printStackTrace();}
            }
        }

        saved = true;
        return true;
    }

    public String openText(final String filename)
    {
        if(filename != null)
        {
            FileInputStream fis = null;
            FileChannel channel = null;
            ByteBuffer byteBuffer = null;

            try
            {
                fis = new FileInputStream(new File(filename));

                channel = fis.getChannel();
                byteBuffer = ByteBuffer.allocateDirect((int)channel.size());
                if(fis.available() != 0)
                {
                    channel.read(byteBuffer);
                    byteBuffer.flip();
                    if(formatDetector(byteBuffer) != null)
                    {
                        format = Constant.ENCODE_TYPE_UTF8;
                        Log.e("Debug", "UTF-8");
                    }
                    else
                    {
                        format = Constant.ENCODE_TYPE_EUCKR;
                        Log.e("Debug", "EUC-KR");
                    }
                    fileopen = true;
                    fileopen_name = filename;
                    MD5 = createMD5(byteBuffer.array());
                    Log.i("Debug", "Open MD5 : " + MD5);
                    return new String(byteBuffer.array(), format);
                }
                else
                {
                    fileopen = false;
                    fileopen_name = "";
                }

            }
            catch (Exception e){e.printStackTrace();}
            finally {
                try{byteBuffer.clear();}
                catch (Exception e){e.printStackTrace();}
                try{channel.close();}
                catch (Exception e){e.printStackTrace();}
                try{fis.close();}
                catch (Exception e){e.printStackTrace();}
            }

        }
        return "";
    }

    public String createMD5(final byte[] message)
    {
        Log.d("Debug", "message length : " + message.length);
        MessageDigest messageDigest;
        StringBuilder sbuilder = new StringBuilder();
        CharBuffer charBuffer = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
            charBuffer = Charset.forName(format).newDecoder().decode(ByteBuffer.wrap(message));
            messageDigest.update(charBuffer.toString().getBytes());
            byte[] hash = messageDigest.digest();
            for (int i = 0; i < hash.length; i++)
            {
                sbuilder.append(String.format("%02x", hash[i] & 0xff));
            }
        }
        catch (Exception e){e.printStackTrace();}
        finally {charBuffer.clear();}
        return sbuilder.toString();
    }

    public String createMD5(final String message)
    {
        Log.d("Debug", "message length : " + message.length());
        MessageDigest messageDigest;
        StringBuilder sbuilder = new StringBuilder();
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
            // 타입에 상관없이 UTF8인 이유는 이미 UTF8로 변환되어있는 것을 체크하기 때문
            messageDigest.update(message.getBytes(Charset.forName(Constant.ENCODE_TYPE_UTF8)));
            byte[] hash = messageDigest.digest();
            for (int i = 0; i < hash.length; i++)
            {
                sbuilder.append(String.format("%02x", hash[i] & 0xff));
            }
        }
        catch (Exception e){e.printStackTrace();}
        return sbuilder.toString();
    }

    private CharBuffer formatDetector(final ByteBuffer buffer)
    {
        CharBuffer charBuffer = null;
        try
        {
            CharsetDecoder decoder = Charset.forName(Constant.ENCODE_TYPE_UTF8).newDecoder();
            charBuffer = decoder.decode(buffer);
        }
        catch (CharacterCodingException cce)
        {
            return null;
        }
        return charBuffer;
    }
}

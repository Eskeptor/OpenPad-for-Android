package com.eskeptor.openTextViewer.textManager;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by narut on 2017-02-20.
 */

// 로그파일 생성용 클래스
public class LogManager
{

    public boolean saveLog(final String _strData, final String _filename)
    {
        if(_strData == null || _strData.isEmpty())
        {
            return false;
        }

        FileOutputStream fos = null;
        FileChannel channel = null;
        ByteBuffer buffer = null;
        try
        {
            fos = new FileOutputStream(new File(_filename));
            channel = fos.getChannel();
            buffer = ByteBuffer.allocateDirect(_strData.getBytes().length);
            buffer.put(_strData.getBytes());
            buffer.flip();
            channel.write(buffer);
        }
        catch (Exception e) {e.printStackTrace();}
        finally
        {
            if (buffer != null)
            {
                try {
                    buffer.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (channel != null) {
                try {
                    channel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public String openLog(final String _filename)
    {
        if(_filename != null)
        {
            FileInputStream fis = null;
            FileChannel channel = null;
            ByteBuffer byteBuffer = null;
            try
            {
                fis = new FileInputStream(new File(_filename));

                channel = fis.getChannel();
                byteBuffer = ByteBuffer.allocateDirect((int)channel.size());
                if(fis.available() != 0)
                {
                    channel.read(byteBuffer);
                    byteBuffer.flip();
                    return new String(byteBuffer.array()).trim();
                }
            }
            catch (Exception e){e.printStackTrace();}
            finally {
                if (byteBuffer != null)
                {
                    try {byteBuffer.clear();} catch (Exception e) {e.printStackTrace();}
                }
                if (channel != null)
                {
                    try {channel.close();} catch (Exception e) {e.printStackTrace();}
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "";
    }
}

package com.eskeptor.openTextViewer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by narut on 2017-02-20.
 */
public class LogManager
{

    public boolean saveLog(final String strData, final String filename)
    {
        if(strData == null || strData.isEmpty())
        {
            return false;
        }

        FileOutputStream fos = null;
        FileChannel channel = null;
        ByteBuffer buffer = null;
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
        return true;
    }

    public String openLog(final String filename)
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
                    return new String(byteBuffer.array()).trim();
                    //return new String(byteBuffer.array());
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
}

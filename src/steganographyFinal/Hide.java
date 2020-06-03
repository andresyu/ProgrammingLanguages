/*
* <steganography using threads>
        Copyright (C) <2020>  <Andres Yunis>

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*
*  */
package steganographyFinal;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

import javax.imageio.ImageIO;

public class Hide extends Thread{
    BufferedImage srcImg;
    File dstFile;
    String messageToHide;
    BufferedImage result;
    HideThreadPoolManager tpm;

    public Hide (BufferedImage si, File df, String mth) {
        srcImg = si;
        dstFile = df;
        messageToHide = mth;
        result = deepCopy(srcImg);
    }
    //Run method
    @Override
    public void run () {
        System.out.println("Coding...");
        int h = srcImg.getHeight();
        int w = srcImg.getWidth();
        int n = w*h;
        char[] message = messageToHide.toCharArray();
        int messageLength = message.length;
        int step = (int) Math.floor(n/(messageLength+1));
        result.setRGB(0, 0, step);
        this.hideChar(1, 0, messageLength);
        tpm = new HideThreadPoolManager(messageToHide, srcImg, result, step, 1);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(tpm);
        try {
            ImageIO.write(tpm.result, "jpg", dstFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        decode();
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    void hideChar(int j, int i, int num){
        int rgb = srcImg.getRGB(j, i);
        int red = rgb & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = (rgb >> 16) & 0xFF;
        blue = num;
        int color;
        color = red;
        color = (color << 8) | green;
        color = (color << 8) | blue;
        result.setRGB(j, i, color);
    }

    String decode(){
        System.out.println("Decoding...");
        int h = result.getHeight();
        int w = result.getWidth();
        int x = 0;
        String message = "";
        int step = 0;
        int messageLength = 0;
        int messageIndex = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (i == 0 && j == 0) {
                    step = 16777216+result.getRGB(j, i);
                } else if (i == 0 && j == 1) {
                    messageLength = this.getCharInt(j, i);
                } else {
                    if (x%step == 0 && messageIndex < messageLength) {
                        message = message.concat(Character.toString( (char) this.getCharInt(j, i)));
                        messageIndex++;
                    }
                }
                x++;
            }
        }
        System.out.println("Message: "+message);
        return message;
    }

    int getCharInt(int j, int i) {
        int rgb = result.getRGB(j, i);
        int blue = rgb & 0xFF;
        return blue;
    }
}

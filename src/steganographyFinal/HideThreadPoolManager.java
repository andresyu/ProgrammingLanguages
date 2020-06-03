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
import java.util.concurrent.RecursiveAction;

@SuppressWarnings("serial")
public class HideThreadPoolManager extends RecursiveAction {
    BufferedImage srcImg;
    String messageToHide;
    BufferedImage result;
    final int charsPerThread = 1;
    int step;
    int index;

    public HideThreadPoolManager (String mth, BufferedImage im, BufferedImage res, int sp, int in) {
        srcImg = im;
        messageToHide = mth;
        result = res;
        step = sp;
        index = in;
    }

    void computeDirectly(){
        System.out.println("Hiding Thread ID: " + Thread.currentThread().getId());
        System.out.println("Hiding Original Message Index: " + index);
        System.out.println("Message To Hide: " + messageToHide);
        int w = srcImg.getWidth();
        char[] message = messageToHide.toCharArray();
        int nh;
        int nw;
        for (int i=0; i<messageToHide.length(); i++) {
            nw = ((index+i)*step)%w;
            nh = (int) ((index+i)*step)/w;
            this.hideChar(nw, nh, (int) message[i]);
        }
    }

    @Override protected void compute() {
        if (messageToHide.length() <= charsPerThread) {
            computeDirectly();
            return;
        }
        final int mid = messageToHide.length() / 2;
        String[] parts = {messageToHide.substring(0, mid),messageToHide.substring(mid)};
        invokeAll(new HideThreadPoolManager(parts[0], srcImg, result, step, index),
                new HideThreadPoolManager(parts[1], srcImg, result, step, index+mid));
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
}

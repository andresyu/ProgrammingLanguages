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
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageManager {

    public String imgPath;
    public String imgName;
    public String messageToHide;
    private BufferedImage auxImg;

    public void getAndCheckImg() {
        System.out.println("<steganography with threads>  Copyright (C) <2020>  <Andres Yunis>\n" +
                "    This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.\n" +
                "    This is free software, and you are welcome to redistribute it\n" +
                "    under certain conditions; type `show c' for details.");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Enter The Path Where The Image Is Located: ");
        imgPath = scanner.nextLine();
        System.out.println("Please Enter The Image Name: ");
        imgName = scanner.nextLine();
        System.out.println("Checking That The Image Exists...");
        try {
            auxImg = ImageIO.read(new File(imgPath+imgName));
        } catch (IOException e) {
        }
        System.out.println("Success! The Image was found");
        System.out.println("Please Enter The Message: ");
        messageToHide = scanner.nextLine();
        scanner.close();
    }
    public void computeAll() {
        Hide hide = new Hide(auxImg,
                new File(imgPath+"hidden.jpg"),
                messageToHide);
        hide.start();
        try {
            hide.join();
        } catch (InterruptedException e) {
        }
    }

    public static void main(String[] args) {
        ImageManager im = new ImageManager();
        im.getAndCheckImg();
        im.computeAll();
    }
}

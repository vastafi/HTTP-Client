
import java.io.*;
import java.net.URL;

public class ImagesDonwloader extends Thread {

    public static void downloadImage(String imageURL) {

        String imageName = imageURL.substring(imageURL.lastIndexOf("/") + 1);

        System.out.println("Download image: " + imageName + " from: " + imageURL);

        try {

            URL urlImage = new URL(imageURL);
            InputStream inputStream = urlImage.openStream();

            byte[] bytes = new byte[4096];
            int length;

            OutputStream outputStream =
                    new FileOutputStream("src\\main\\resources\\images" + "\\" + imageName);

            while ((length = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
            }

            inputStream.close();
            outputStream.close();

            System.out.println("The image " + imageName + " has been saved in the folder");

        }
        catch (IOException e) {
            System.err.println("Not found: " + imageURL);
        }

    }

}

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Map;


public class Authentification {
    private static String LOGIN_INPUT = "login_username";
    private static String PASSWORD_INPUT = "login_password";
    private static String LINK = "https://thebox.md/my-account/login/";

    public static Map<String, String> getCookies() {
        Authenticator.setDefault(
                new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("user","password".toCharArray());
                    }
                }
        );
        System.setProperty("http.proxyHost", " 91.239.85.255 ");
        System.setProperty("http.proxyPort", "8090");
        Connection.Response response = null;
         try {
             response = Jsoup.connect(LINK)
                    .referrer(LINK)
                    .method(Connection.Method.POST)
                    .data(LOGIN_INPUT, ConnexionDate.LOGIN)
                    .data(PASSWORD_INPUT, ConnexionDate.PASSWORD)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Login error");
        }
        return response.cookies();
    }
}


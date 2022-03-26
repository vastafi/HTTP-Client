import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jsoup.Connection.Method.*;
import static org.jsoup.Jsoup.connect;

public class Requests {
    private static Map<String, String> cookies = Authentification.getCookies();

    public static String headRequest() throws IOException {
        Response response = connect("https://thebox.md/my-account/orders").method(HEAD).cookies(cookies).execute();
        return response.contentType();
    }

    public static Map<String, List<String>> optionsRequest() throws IOException {
        Response response = connect("https://thebox.md/my-account/orders").method(OPTIONS).cookies(cookies).execute();
        return response.multiHeaders();
    }

    public static String searchName(String link) throws Exception {
        Response response = connect(link).method(GET).cookies(cookies).execute();
        String text = response.body();
        Pattern pattern = Pattern.compile("Val", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return "Account name is: " + matcher.group();
        }
        throw new Exception("Account name not found on this page.");
    }

    public static void searchEmails() throws Exception {
        Response response = connect("https://thebox.md/contacts/").method(GET).cookies(cookies).execute();
        String text = response.body();
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
        Matcher matcher = pattern.matcher(text);
        Set<String> email = new HashSet<String>();
        while (matcher.find()) {
            email.add(matcher.group());
        }
        System.out.println(email);
    }

    public static void getLinks() throws Exception {
        Document doc;
        doc = Jsoup.connect("https://thebox.md/history/").get();
        Elements elements = doc.select("a[href]");
        Set<String> links = new HashSet<String>();
        for (Element e : elements) {
            links.add(e.attr("abs:href"));
        }
        System.out.println("\n" + links);
    }

    public static void getAllImages(String link) throws IOException, InterruptedException {
        Document page = Jsoup.connect(link).cookies(cookies).get();
        ExecutorService exec = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(2);
        Elements imageElements = page.getElementsByTag("img");
        for (Element element : imageElements) {
            exec.submit(() -> {
                ImagesDownloader.downloadImage(element.absUrl("src"));
                latch.countDown();
                System.out.println(Thread.currentThread().getName());
            });
        }
        latch.await();
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Content-Type: " + headRequest());
        System.out.println("Options response: " + optionsRequest());
        System.out.println("\n" + searchName("https://thebox.md/my-account/orders"));
        System.out.println("The list of all links: ");
        getLinks();
        System.out.println("\n" + "The list of all emails: ");
        searchEmails();
        System.out.println("\n" + "The list of all images: ");
        getAllImages("https://thebox.md/#pizza");
    }
}

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

    public static String head() throws IOException {
        Response response = connect("https://thebox.md/my-account/orders").method(HEAD).cookies(cookies).execute();
        return response.contentType();
    }

    public static Map<String, List<String>> optionsResponse() throws IOException {
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
        Document document;
        document = Jsoup.connect("https://thebox.md/history/").get();
        Elements elements = document.select("a[href]");
        Set<String> links = new HashSet<String>();
        for (Element element : elements) {
            links.add(element.attr("abs:href"));
        }
        System.out.println("\n" + links);
    }

    public static void getAllImages(String link) throws IOException, InterruptedException {
        Document page = Jsoup.connect(link).cookies(cookies).get();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Elements imageElements = page.getElementsByTag("img");
        for (Element element : imageElements) {
            executorService.submit(() -> {
                ImagesDownloader.downloadImage(element.absUrl("src"));
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName());
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }
  }

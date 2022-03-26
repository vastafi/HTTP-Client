public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println("Content-Type: " + Requests.head());
        System.out.println("Options response: " + Requests.optionsResponse());
        System.out.println("\n" + Requests.searchName("https://thebox.md/my-account/orders"));
        System.out.println("The list of all links: ");
        Requests.getLinks();
        System.out.println("\n" + "All emails: ");
        Requests.searchEmails();
        System.out.println("\n" + "The list of all images: ");
        Requests.getAllImages("https://thebox.md/#pizza");
    }
}

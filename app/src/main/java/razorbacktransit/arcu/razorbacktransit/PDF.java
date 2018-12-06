package razorbacktransit.arcu.razorbacktransit;

/**
 * Created by Andrew on 10/5/17.
 */

public class PDF {

    private String title;
    private String filePath;

    PDF(String t, String fp) {
        title = t;
        filePath = fp;
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }
}

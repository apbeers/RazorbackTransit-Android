package razorbacktransit.arcu.razorbacktransit;

/**
 * Created by Andrew on 10/5/17.
 */

public class PDFFactory {

    private PDF route11 = new PDF("Route 11", "route11");
    private PDF route13 = new PDF("Route 13", "route13");
    private PDF route17 = new PDF("Route 17", "route17");
    private PDF route21 = new PDF("Route 21", "route21");
    private PDF route26 = new PDF("Route 26", "route26");
    private PDF route33 = new PDF("Route 33", "route33");
    private PDF route35 = new PDF("Route 35", "route35");
    private PDF route44 = new PDF("Route 44", "route44");
    private PDF route48 = new PDF("Route 48", "route48");

    private PDF schedule11 = new PDF("Schedule 11", "schedule11");
    private PDF schedule13 = new PDF("Schedule 13", "schedule13");
    private PDF schedule17 = new PDF("Schedule 17", "schedule17");
    private PDF schedule21 = new PDF("Schedule 21", "schedule21");
    private PDF schedule26 = new PDF("Schedule 26", "schedule26");
    private PDF schedule33 = new PDF("Schedule 33", "schedule33");
    private PDF schedule35 = new PDF("Schedule 35", "schedule35");
    private PDF schedule44 = new PDF("Schedule 44", "schedule44");
    private PDF schedule48 = new PDF("Schedule 48", "schedule48");


    public PDF[] getAllSChedules() {

        return new PDF[] {schedule11, schedule13, schedule17, schedule21, schedule26, schedule33, schedule35, schedule44, schedule48};
    }

    public PDF[] getAllRoutes() {

        return new PDF[] {route11, route13, route17, route21, route26, route33, route35, route44, route48};
    }
}

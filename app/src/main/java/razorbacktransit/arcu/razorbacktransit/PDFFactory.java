package razorbacktransit.arcu.razorbacktransit;

/**
 * Created by Andrew on 10/5/17.
 */

public class PDFFactory {

    // schedules
    private PDF BLUE22 = new PDF("Blue 22", "blue_22_schedule");
    private PDF BROWN17 = new PDF("Brown 17", "brown_17_schedule");
    private PDF GREEN11 = new PDF("Green 11", "green_11_schedule");
    private PDF ORANGE33 = new PDF("Orange 33", "orange_33_schedule");
    private PDF PURPLE44 = new PDF("Purple 44", "purple_44_schedule");
    private PDF RED26 = new PDF("Red 26", "red_26_schedule");
    private PDF REMOTEEXPRESS = new PDF("Remote Express 48", "remoteexpress_48_schedule");
    private PDF ROUTE13 = new PDF("Route 13", "route_13_schedule");
    private PDF TAN35 = new PDF("Tan 35", "tan_35_schedule");
    private PDF YELLOW12 = new PDF("Yellow 12", "yellow_12_schedule");
    private PDF GRAY21 = new PDF("Gray 21", "gray_21_schedule");
    private PDF BLUEREDUCED = new PDF("Blue Reduced 02", "bluereduced_02_schedule");
    private PDF GREENREDUCED = new PDF("Green Reduced 01", "greenreduced_01_schedule");
    private PDF ORANGEREDUCED = new PDF("Orange Reduced 03", "orangereduced_03_schedule");
    private PDF PURPLEREDUCED = new PDF("Purple Reduced 04", "purplereduced_04_schedule");
    private PDF REDREDUCED = new PDF("Red Reduced 06", "redreduced_06_schedule");
    private PDF TANREDUCED = new PDF("Tan Reduced 05", "tanreduced_05_schedule");
    private PDF BROWNREDUCED = new PDF("Brown Reduced 07", "brownreduced_07_schedule");

    // route maps
    private PDF BLUE22_ROUTE = new PDF("Blue 22", "blue_22_route");
    private PDF BROWN17_ROUTE = new PDF("Brown 17", "brown_17_route");
    private PDF GREEN11_ROUTE = new PDF("Green 11", "green_11_route");
    private PDF ORANGE33_ROUTE = new PDF("Orange 33", "orange_33_route");
    private PDF PURPLE44_ROUTE = new PDF("Purple 44", "purple_44_route");
    private PDF RED26_ROUTE = new PDF("Red 26", "red_26_route");
    private PDF REMOTEEXPRESS_ROUTE = new PDF("Remote Express 48", "remoteexpress_48_route");
    private PDF ROUTE13_ROUTE = new PDF("Route 13", "route_13_route");
    private PDF TAN35_ROUTE = new PDF("Tan 35", "tan_35_route");
    private PDF YELLOW12_ROUTE = new PDF("Yellow 12", "yellow_12_route");
    private PDF GRAY21_ROUTE = new PDF("Gray 21", "gray_21_route");
    private PDF BLUEREDUCED_ROUTE = new PDF("Blue Reduced 02", "bluereduced_02_route");
    private PDF GREENREDUCED_ROUTE = new PDF("Green Reduced 01", "greenreduced_01_route");
    private PDF ORANGEREDUCED_ROUTE = new PDF("Orange Reduced 03", "orangereduced_03_route");
    private PDF PURPLEREDUCED_ROUTE = new PDF("Purple Reduced 04", "purplereduced_04_route");
    private PDF REDREDUCED_ROUTE = new PDF("Red Reduced 06", "redreduced_06_route");
    private PDF TANREDUCED_ROUTE = new PDF("Tan Reduced 05", "tanreduced_05_route");
    private PDF BROWNREDUCED_ROUTE = new PDF("Brown Reduced 07", "brownreduced_07_route");


    public PDF[] getAllSChedules() {

        return new PDF[] {GREEN11, YELLOW12, ROUTE13, BROWN17, GRAY21, BLUE22, RED26, ORANGE33, TAN35, PURPLE44, REMOTEEXPRESS, GREENREDUCED, BLUEREDUCED, ORANGEREDUCED, PURPLEREDUCED, TANREDUCED, REDREDUCED, BROWNREDUCED};
    }

    public PDF[] getAllRoutes() {

        return  new PDF[] {GREEN11_ROUTE, YELLOW12_ROUTE, ROUTE13_ROUTE, BROWN17_ROUTE, GRAY21_ROUTE, BLUE22_ROUTE, RED26_ROUTE, ORANGE33_ROUTE, TAN35_ROUTE, PURPLE44_ROUTE, REMOTEEXPRESS_ROUTE, GREENREDUCED_ROUTE, BLUEREDUCED_ROUTE, ORANGEREDUCED_ROUTE, PURPLEREDUCED_ROUTE, TANREDUCED_ROUTE, REDREDUCED_ROUTE, BROWNREDUCED_ROUTE};
    }
}

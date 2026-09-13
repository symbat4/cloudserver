package cloudserver;
public class Main{
    public static void main(String[] args) {
        CloudServer server = new CloudServer.Builder()
                .setRegion("eu-west-1") //region code for Ireland
                .setcpuCores(4)
                .setRamGB(16)
                .build();

        System.out.println(server.toSummaryLine());

        //ready Director configurations
        CloudServerDirector director = new CloudServerDirector();

        CloudServer devServer = director.makeDevServer(new CloudServer.Builder());
        CloudServer prodServer = director.makeProductionServer(new CloudServer.Builder());

        System.out.println(devServer.toSummaryLine());
        System.out.println(prodServer.toSummaryLine());

        try {
            CloudServer invalidServer = new CloudServer.Builder()
                    .setcpuCores(4)
                    .setRamGB(8)
                    .build();
        } catch (IllegalStateException e) {
            System.out.println("Rejected as expected: " + e.getMessage());
        }
    }
}
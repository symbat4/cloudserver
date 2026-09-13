package cloudserver;

public class CloudServerDirector {
    public CloudServer makeDevServer(CloudServer.Builder builder) {
        return builder
                .setRegion("eu-west-1")
                .setcpuCores(2)
                .setRamGB(4)
                .build();
    }

    public CloudServer makeProductionServer(CloudServer.Builder builder) {
        return builder
                .setRegion("us-east-1")
                .setcpuCores(8)
                .setRamGB(32)
                .build();
    }
}

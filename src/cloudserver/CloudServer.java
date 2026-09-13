package cloudserver;
public class CloudServer {
    private final String region;
    private final int cpuCores;
    private final int ramGB;

    private CloudServer(Builder builder) {
        this.region = builder.region;
        this.cpuCores = builder.cpuCores;
        this.ramGB = builder.ramGB;
    }

    public String getRegion() {
        return region;
    }
    public int getCpuCores() {
        return cpuCores;
    }
    public int getRamGB() {
        return ramGB;
    }

    public String toSummaryLine(){
        return region + " | " + cpuCores + " vCPU | " + ramGB + "GB RAM";
    }

    @Override
    public String toString() {
        return toSummaryLine();
    }

    public static class Builder {
        private String region;
        private int cpuCores;
        private int ramGB;


        public Builder setRegion(String region) {
            this.region = region;
            return this;
        }
        public Builder setcpuCores(int cpuCores) {
            this.cpuCores = cpuCores;
            return this;
        }
        public Builder setRamGB(int ramGB) {
            this.ramGB = ramGB;
            return this;
        }

        public CloudServer build() {
            if (region == null || region.isBlank()) {
                throw new IllegalStateException("region is null or blank");
            }
            if  (cpuCores <= 0) {
                throw new IllegalStateException("cpuCores must be set");
            }
            if (ramGB <= 0) {
                throw new IllegalStateException("ramGB must be set");
            }
            return new CloudServer(this);
        }
    }
}
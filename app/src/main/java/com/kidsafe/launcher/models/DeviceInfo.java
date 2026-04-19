package com.kidsafe.launcher.models;

/**
 * Model representing device hardware information.
 */
public class DeviceInfo {
    private final String deviceName;
    private final String manufacturer;
    private final String model;
    private final String androidVersion;
    private final int sdkVersion;
    private final String cpuArchitecture;
    private final long totalRam;
    private final long availableRam;
    private final long totalStorage;
    private final long availableStorage;
    private final int screenWidth;
    private final int screenHeight;
    private final float screenDensity;
    private final int batteryLevel;
    private final String serialNumber;

    private DeviceInfo(Builder builder) {
        this.deviceName = builder.deviceName;
        this.manufacturer = builder.manufacturer;
        this.model = builder.model;
        this.androidVersion = builder.androidVersion;
        this.sdkVersion = builder.sdkVersion;
        this.cpuArchitecture = builder.cpuArchitecture;
        this.totalRam = builder.totalRam;
        this.availableRam = builder.availableRam;
        this.totalStorage = builder.totalStorage;
        this.availableStorage = builder.availableStorage;
        this.screenWidth = builder.screenWidth;
        this.screenHeight = builder.screenHeight;
        this.screenDensity = builder.screenDensity;
        this.batteryLevel = builder.batteryLevel;
        this.serialNumber = builder.serialNumber;
    }

    public String getDeviceName() { return deviceName; }
    public String getManufacturer() { return manufacturer; }
    public String getModel() { return model; }
    public String getAndroidVersion() { return androidVersion; }
    public int getSdkVersion() { return sdkVersion; }
    public String getCpuArchitecture() { return cpuArchitecture; }
    public long getTotalRam() { return totalRam; }
    public long getAvailableRam() { return availableRam; }
    public long getTotalStorage() { return totalStorage; }
    public long getAvailableStorage() { return availableStorage; }
    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }
    public float getScreenDensity() { return screenDensity; }
    public int getBatteryLevel() { return batteryLevel; }
    public String getSerialNumber() { return serialNumber; }

    public String getFormattedRam() {
        return formatBytes(availableRam) + " / " + formatBytes(totalRam);
    }

    public String getFormattedStorage() {
        return formatBytes(availableStorage) + " / " + formatBytes(totalStorage);
    }

    public String getScreenResolution() {
        return screenWidth + " x " + screenHeight;
    }

    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format("%.1f KB", kb);
        double mb = kb / 1024.0;
        if (mb < 1024) return String.format("%.1f MB", mb);
        double gb = mb / 1024.0;
        return String.format("%.1f GB", gb);
    }

    public static class Builder {
        private String deviceName = "";
        private String manufacturer = "";
        private String model = "";
        private String androidVersion = "";
        private int sdkVersion;
        private String cpuArchitecture = "";
        private long totalRam;
        private long availableRam;
        private long totalStorage;
        private long availableStorage;
        private int screenWidth;
        private int screenHeight;
        private float screenDensity;
        private int batteryLevel;
        private String serialNumber = "";

        public Builder setDeviceName(String deviceName) { this.deviceName = deviceName; return this; }
        public Builder setManufacturer(String manufacturer) { this.manufacturer = manufacturer; return this; }
        public Builder setModel(String model) { this.model = model; return this; }
        public Builder setAndroidVersion(String androidVersion) { this.androidVersion = androidVersion; return this; }
        public Builder setSdkVersion(int sdkVersion) { this.sdkVersion = sdkVersion; return this; }
        public Builder setCpuArchitecture(String cpuArchitecture) { this.cpuArchitecture = cpuArchitecture; return this; }
        public Builder setTotalRam(long totalRam) { this.totalRam = totalRam; return this; }
        public Builder setAvailableRam(long availableRam) { this.availableRam = availableRam; return this; }
        public Builder setTotalStorage(long totalStorage) { this.totalStorage = totalStorage; return this; }
        public Builder setAvailableStorage(long availableStorage) { this.availableStorage = availableStorage; return this; }
        public Builder setScreenWidth(int screenWidth) { this.screenWidth = screenWidth; return this; }
        public Builder setScreenHeight(int screenHeight) { this.screenHeight = screenHeight; return this; }
        public Builder setScreenDensity(float screenDensity) { this.screenDensity = screenDensity; return this; }
        public Builder setBatteryLevel(int batteryLevel) { this.batteryLevel = batteryLevel; return this; }
        public Builder setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; return this; }

        public DeviceInfo build() {
            return new DeviceInfo(this);
        }
    }
}

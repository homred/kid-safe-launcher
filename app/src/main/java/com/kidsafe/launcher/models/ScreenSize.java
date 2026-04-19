package com.kidsafe.launcher.models;

/**
 * Enumeration for screen size categories.
 */
public enum ScreenSize {
    WATCH(0, 320),
    PHONE(320, 480),
    PHABLET(480, 600),
    TABLET(600, 720),
    TV(720, Integer.MAX_VALUE);

    private final int minWidthDp;
    private final int maxWidthDp;

    ScreenSize(int minWidthDp, int maxWidthDp) {
        this.minWidthDp = minWidthDp;
        this.maxWidthDp = maxWidthDp;
    }

    public int getMinWidthDp() {
        return minWidthDp;
    }

    public int getMaxWidthDp() {
        return maxWidthDp;
    }

    public boolean shouldShowStatusBar() {
        return this == TABLET || this == TV || this == PHABLET;
    }

    public int getGridColumns() {
        switch (this) {
            case WATCH: return 2;
            case PHONE: return 3;
            case PHABLET: return 4;
            case TABLET: return 5;
            case TV: return 6;
            default: return 3;
        }
    }

    public static ScreenSize fromWidthDp(int widthDp) {
        for (ScreenSize size : values()) {
            if (widthDp >= size.minWidthDp && widthDp < size.maxWidthDp) {
                return size;
            }
        }
        return PHONE;
    }
}

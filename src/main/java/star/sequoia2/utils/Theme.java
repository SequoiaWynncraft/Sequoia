package star.sequoia2.utils;

public class Theme {
    String NAME;
    public int LIGHT;
    public int NORMAL;
    public int DARK;
    public int ACCENT1;
    public int ACCENT2;
    public int ACCENT3;
    public int ERROR;

    public Theme(String name, int light, int normal, int dark, int accent1, int accent2, int accent3) {
        this.NAME = name;
        this.LIGHT = light;
        this.NORMAL = normal;
        this.DARK = dark;
        this.ACCENT1 = accent1;
        this.ACCENT2 = accent2;
        this.ACCENT3 = accent3;
        this.ERROR = 0xFF5555;
    }

    public Theme() {}

    public String getName() { return this.NAME; }
}
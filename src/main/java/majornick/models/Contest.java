package majornick.models;

import java.time.LocalDate;

public class Contest{
    private String name;
    private LocalDate time;
    private String url;
    private String phase;
    private String startTimeSeconds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getStartTimeSeconds() {
        return startTimeSeconds;
    }

    public void setStartTimeSeconds(String startTimeSeconds) {
        this.startTimeSeconds = startTimeSeconds;
    }
}

package majornick.cfreminderbot.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(value={ "id","type","frozen","durationSeconds","relativeTimeSeconds",
                            "preparedBy","websiteUrl","description","kind","icpcRegion","country","city","season"}, allowGetters=true)
public class Contest{
    private String name;
    private String phase;
    private String startTimeSeconds;
    private LocalDateTime startTime;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", startTime ='" + startTime + '\'' ;
    }
}

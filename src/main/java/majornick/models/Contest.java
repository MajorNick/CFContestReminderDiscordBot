package majornick.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(value={ "id","type","frozen","durationSeconds","relativeTimeSeconds",
                            "preparedBy","websiteUrl","description","kind","icpcRegion","country","city","season"}, allowGetters=true)
public class Contest{
    private String name;
    private String phase;
    private String startTimeSeconds;



    private  int difficulty;

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

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "Contest{" +
                "name='" + name + '\'' +
                ", phase='" + phase + '\'' +
                ", startTimeSeconds='" + startTimeSeconds + '\'' +
                ", difficulty=" + difficulty +
                '}';
    }
}

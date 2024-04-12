package majornick.models;

import java.util.List;

public class CFResponse{
    String status;
    List<Contest> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Contest> getResult() {
        return result;
    }

    public void setResult(List<Contest> result) {
        this.result = result;
    }
}

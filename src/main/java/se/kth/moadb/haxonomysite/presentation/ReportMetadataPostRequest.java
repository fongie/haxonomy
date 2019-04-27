package se.kth.moadb.haxonomysite.presentation;

/**
 * Represents a serialized JSON POST request to /reportData.
 * Also used to return result.
 */
public class ReportMetadataPostRequest {

    private String url;
    private String title;
    private Double bounty;
    private String vulnerability;

    public ReportMetadataPostRequest() {
        this.url = "";
    }


    public ReportMetadataPostRequest(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getBounty() {
        return bounty;
    }

    public void setBounty(Double bounty) {
        this.bounty = bounty;
    }

    public String getVulnerability() {
        return vulnerability;
    }

    public void setVulnerability(String vulnerability) {
        this.vulnerability = vulnerability;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



}

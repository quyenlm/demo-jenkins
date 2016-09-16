package phn.nts.trader.fe.domain;

import phn.nts.ams.fe.domain.IbInfo;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 4/15/13 1:23 PM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class IBInfo extends IbInfo {

    private String fbShareUrl;
    private String fbShareThumbnail;
    private String fbShareTitle;
    private String fbShareSummary;
    private String twShareContent;
    private String twShareThumbnail;
    private String twShareTitle;
    private String twShareSummary;
    private String gpShareContent;
    private String gpShareThumbnail;
    private String gpShareTitle;
    private String gpShareSummary;

    public String getFacebookLink(){
        return "http://www.facebook.com/sharer/sharer.php?s=100&p[url]=" + (fbShareUrl == null ? "" : fbShareUrl) ;
    	//return "http://www.facebook.com/sharer/sharer.php?u=" + (fbShareUrl == null ? "" : fbShareUrl) ;
    }

    public String getTwitterLink(){
        //return "http://twitter.com/home?status=" + (twShareContent == null ? "" : twShareContent);
        return "http://twitter.com/share?text=";
    }

    public String getGooglePlusLink(){
        return "https://plus.google.com/share?url=" + (gpShareContent == null ? "" : gpShareContent);
    }

    public String getFbShareUrl() {
        return fbShareUrl;
    }

    public void setFbShareUrl(String fbShareUrl) {
        this.fbShareUrl = fbShareUrl;
    }

    public String getFbShareThumbnail() {
        return fbShareThumbnail;
    }

    public void setFbShareThumbnail(String fbShareThumbnail) {
        this.fbShareThumbnail = fbShareThumbnail;
    }

    public String getFbShareTitle() {
        return fbShareTitle;
    }

    public void setFbShareTitle(String fbShareTitle) {
        this.fbShareTitle = fbShareTitle;
    }

    public String getFbShareSummary() {
        return fbShareSummary;
    }

    public void setFbShareSummary(String fbShareSummary) {
        this.fbShareSummary = fbShareSummary;
    }

    public String getTwShareContent() {
        return twShareContent;
    }

    public void setTwShareContent(String twShareContent) {
        this.twShareContent = twShareContent;
    }

    public String getGpShareContent() {
        return gpShareContent;
    }

    public void setGpShareContent(String gpShareContent) {
        this.gpShareContent = gpShareContent;
    }

	/**
	 * @return the twShareThumbnail
	 */
	public String getTwShareThumbnail() {
		return twShareThumbnail;
	}

	/**
	 * @param twShareThumbnail the twShareThumbnail to set
	 */
	public void setTwShareThumbnail(String twShareThumbnail) {
		this.twShareThumbnail = twShareThumbnail;
	}

	/**
	 * @return the twShareTitle
	 */
	public String getTwShareTitle() {
		return twShareTitle;
	}

	/**
	 * @param twShareTitle the twShareTitle to set
	 */
	public void setTwShareTitle(String twShareTitle) {
		this.twShareTitle = twShareTitle;
	}

	/**
	 * @return the twShareSummary
	 */
	public String getTwShareSummary() {
		return twShareSummary;
	}

	/**
	 * @param twShareSummary the twShareSummary to set
	 */
	public void setTwShareSummary(String twShareSummary) {
		this.twShareSummary = twShareSummary;
	}

	/**
	 * @return the gpShareThumbnail
	 */
	public String getGpShareThumbnail() {
		return gpShareThumbnail;
	}

	/**
	 * @param gpShareThumbnail the gpShareThumbnail to set
	 */
	public void setGpShareThumbnail(String gpShareThumbnail) {
		this.gpShareThumbnail = gpShareThumbnail;
	}

	/**
	 * @return the gpShareTitle
	 */
	public String getGpShareTitle() {
		return gpShareTitle;
	}

	/**
	 * @param gpShareTitle the gpShareTitle to set
	 */
	public void setGpShareTitle(String gpShareTitle) {
		this.gpShareTitle = gpShareTitle;
	}

	/**
	 * @return the gpShareSummary
	 */
	public String getGpShareSummary() {
		return gpShareSummary;
	}

	/**
	 * @param gpShareSummary the gpShareSummary to set
	 */
	public void setGpShareSummary(String gpShareSummary) {
		this.gpShareSummary = gpShareSummary;
	}
}

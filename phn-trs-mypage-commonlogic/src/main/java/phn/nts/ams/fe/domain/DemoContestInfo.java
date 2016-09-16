package phn.nts.ams.fe.domain;

/**
 * @description DemoContestInfo
 * @version NTS1.0
 * @author anh.nguyen.ngoc
 * @CrDate Jan 4, 2013
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class DemoContestInfo {

	private Integer contestId;
	private String contestTitle;
	private String shortContent;
	private String image1;
	
	public Integer getContestId() {
		return contestId;
	}
	public void setContestId(Integer contestId) {
		this.contestId = contestId;
	}
	public String getContestTitle() {
		return contestTitle;
	}
	public void setContestTitle(String contestTitle) {
		this.contestTitle = contestTitle;
	}
	public String getShortContent() {
		return shortContent;
	}
	public void setShortContent(String shortContent) {
		this.shortContent = shortContent;
	}
	public String getImage1() {
		return image1;
	}
	public void setImage1(String image1) {
		this.image1 = image1;
	}
	
}

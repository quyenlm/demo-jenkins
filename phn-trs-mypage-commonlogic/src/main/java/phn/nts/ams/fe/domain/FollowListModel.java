package phn.nts.ams.fe.domain;

import phn.com.nts.ams.web.condition.FollowListItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 3/11/13 9:49 AM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class FollowListModel {

    private boolean guestMode;
    private String ajaxMsg;
    private boolean ajaxSuccess;
    private List<FollowListItemInfo> followDetails = new ArrayList<FollowListItemInfo>();
    private String currentUserName;
    private Integer followerNo;

    public boolean isGuestMode() {
        return guestMode;
    }

    public void setGuestMode(boolean guestMode) {
        this.guestMode = guestMode;
    }

    public String getAjaxMsg() {
        return ajaxMsg;
    }

    public void setAjaxMsg(String ajaxMsg) {
        this.ajaxMsg = ajaxMsg;
    }

    public boolean isAjaxSuccess() {
        return ajaxSuccess;
    }

    public void setAjaxSuccess(boolean ajaxSuccess) {
        this.ajaxSuccess = ajaxSuccess;
    }

    public List<FollowListItemInfo> getFollowDetails() {
        return followDetails;
    }

    public void setFollowDetails(List<FollowListItemInfo> followDetails) {
        this.followDetails = followDetails;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public Integer getFollowerNo() {
        return followerNo;
    }

    public void setFollowerNo(Integer followerNo) {
        this.followerNo = followerNo;
    }
}

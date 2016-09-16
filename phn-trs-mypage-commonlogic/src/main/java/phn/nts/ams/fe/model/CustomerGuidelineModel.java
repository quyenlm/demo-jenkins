package phn.nts.ams.fe.model;

/**
 * @description
 * @CrBy: dai.nguyen.van
 * @CrDate: 4/9/13 5:38 PM
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class CustomerGuidelineModel {
    private boolean displayed;
    private boolean completed;
    private Integer step;

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }
}

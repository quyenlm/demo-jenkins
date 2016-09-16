package phn.nts.ams.fe.domain;

import java.io.Serializable;

public class CountryInfo implements Serializable {
	private Integer countryId;
	private String countryCode;
	private String countryName;
    private String phoneCountryCd;
	/**
	 * @return the countryId
	 */
	public Integer getCountryId() {
		return countryId;
	}
	/**
	 * @param countryId the countryId to set
	 */
	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	/**
	 * @return the countryName
	 */
	public String getCountryName() {
		return countryName;
	}
	/**
	 * @param countryName the countryName to set
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

    public String getPhoneCountryCd() {
        return phoneCountryCd;
    }

    public void setPhoneCountryCd(String phoneCountryCd) {
        this.phoneCountryCd = phoneCountryCd;
    }
}

package commons.app.com.keep;

import com.google.gson.annotations.SerializedName;

public class DeviceInfo {

    private String country;
    private String countryCode;
    @SerializedName("org")
    private String organization;
    @SerializedName("region")
    private String regionCode;

    public DeviceInfo(String country, String countryCode, String organization, String regionCode) {
        this.country = country;
        this.countryCode = countryCode;
        this.organization = organization;
        this.regionCode = regionCode;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getOrganization() {
        return organization;
    }

    public String getRegionCode() {
        return regionCode;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "country='" + country + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", organization='" + organization + '\'' +
                ", regionCode='" + regionCode + '\'' +
                '}';
    }
}

package uz.codebyz.auth.location;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class AddressResponse {

    private List<Result> results;

    public void setQuery(Query query) {
        this.query = query;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    private Query query;

    public List<Result> getResults() {
        return results;
    }

    public Query getQuery() {
        return query;
    }

    // ===================== RESULT =====================
    public static class Result {

        private Datasource datasource;
        private String name;

        @SerializedName("other_names")
        private Map<String, String> otherNames;

        private String country;

        @SerializedName("country_code")
        private String countryCode;

        private String state;
        private String county;
        private String city;
        private String postcode;
        private String suburb;

        @SerializedName("iso3166_2")
        private String iso3166_2;

        private Double lon;
        private Double lat;
        private String street;

        @SerializedName("state_code")
        private String stateCode;

        private Double distance;

        @SerializedName("result_type")
        private String resultType;

        private String formatted;

        @SerializedName("address_line1")
        private String addressLine1;

        @SerializedName("address_line2")
        private String addressLine2;

        private Timezone timezone;

        @SerializedName("plus_code")
        private String plusCode;

        @SerializedName("plus_code_short")
        private String plusCodeShort;

        private Rank rank;

        @SerializedName("place_id")
        private String placeId;

        private Bbox bbox;

        public Datasource getDatasource() {
            return datasource;
        }

        public void setDatasource(Datasource datasource) {
            this.datasource = datasource;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, String> getOtherNames() {
            return otherNames;
        }

        public void setOtherNames(Map<String, String> otherNames) {
            this.otherNames = otherNames;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPostcode() {
            return postcode;
        }

        public void setPostcode(String postcode) {
            this.postcode = postcode;
        }

        public String getSuburb() {
            return suburb;
        }

        public void setSuburb(String suburb) {
            this.suburb = suburb;
        }

        public String getIso3166_2() {
            return iso3166_2;
        }

        public void setIso3166_2(String iso3166_2) {
            this.iso3166_2 = iso3166_2;
        }

        public Double getLon() {
            return lon;
        }

        public void setLon(Double lon) {
            this.lon = lon;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getStateCode() {
            return stateCode;
        }

        public void setStateCode(String stateCode) {
            this.stateCode = stateCode;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }

        public void setFormatted(String formatted) {
            this.formatted = formatted;
        }

        public String getAddressLine1() {
            return addressLine1;
        }

        public void setAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
        }

        public String getAddressLine2() {
            return addressLine2;
        }

        public void setAddressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
        }

        public void setTimezone(Timezone timezone) {
            this.timezone = timezone;
        }

        public String getPlusCode() {
            return plusCode;
        }

        public void setPlusCode(String plusCode) {
            this.plusCode = plusCode;
        }

        public String getPlusCodeShort() {
            return plusCodeShort;
        }

        public void setPlusCodeShort(String plusCodeShort) {
            this.plusCodeShort = plusCodeShort;
        }

        public Rank getRank() {
            return rank;
        }

        public void setRank(Rank rank) {
            this.rank = rank;
        }

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public Bbox getBbox() {
            return bbox;
        }

        public void setBbox(Bbox bbox) {
            this.bbox = bbox;
        }

        // getters (kerak bo‘lganlarini qo‘shib borasiz)
        public String getFormatted() {
            return formatted;
        }

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        public Timezone getTimezone() {
            return timezone;
        }
    }

    // ===================== DATASOURCE =====================
    public static class Datasource {
        private String sourcename;
        private String attribution;

        public String getSourcename() {
            return sourcename;
        }

        public void setSourcename(String sourcename) {
            this.sourcename = sourcename;
        }

        public String getAttribution() {
            return attribution;
        }

        public void setAttribution(String attribution) {
            this.attribution = attribution;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private String license;
        private String url;
    }

    // ===================== TIMEZONE =====================
    public static class Timezone {

        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getOffsetStd() {
            return offsetStd;
        }

        public void setOffsetStd(String offsetStd) {
            this.offsetStd = offsetStd;
        }

        public Integer getOffsetStdSeconds() {
            return offsetStdSeconds;
        }

        public void setOffsetStdSeconds(Integer offsetStdSeconds) {
            this.offsetStdSeconds = offsetStdSeconds;
        }

        public String getOffsetDst() {
            return offsetDst;
        }

        public void setOffsetDst(String offsetDst) {
            this.offsetDst = offsetDst;
        }

        public Integer getOffsetDstSeconds() {
            return offsetDstSeconds;
        }

        public void setOffsetDstSeconds(Integer offsetDstSeconds) {
            this.offsetDstSeconds = offsetDstSeconds;
        }

        @SerializedName("offset_STD")
        private String offsetStd;

        @SerializedName("offset_STD_seconds")
        private Integer offsetStdSeconds;

        @SerializedName("offset_DST")
        private String offsetDst;

        @SerializedName("offset_DST_seconds")
        private Integer offsetDstSeconds;

        public String getName() {
            return name;
        }
    }

    // ===================== RANK =====================
    public static class Rank {
        private Double importance;
        private Double popularity;

        public Double getImportance() {
            return importance;
        }

        public void setImportance(Double importance) {
            this.importance = importance;
        }

        public Double getPopularity() {
            return popularity;
        }

        public void setPopularity(Double popularity) {
            this.popularity = popularity;
        }
    }

    // ===================== BBOX =====================
    public static class Bbox {
        public Double getLon1() {
            return lon1;
        }

        public void setLon1(Double lon1) {
            this.lon1 = lon1;
        }

        public Double getLat1() {
            return lat1;
        }

        public void setLat1(Double lat1) {
            this.lat1 = lat1;
        }

        public Double getLon2() {
            return lon2;
        }

        public void setLon2(Double lon2) {
            this.lon2 = lon2;
        }

        public Double getLat2() {
            return lat2;
        }

        public void setLat2(Double lat2) {
            this.lat2 = lat2;
        }

        private Double lon1;
        private Double lat1;
        private Double lon2;
        private Double lat2;
    }

    // ===================== QUERY =====================
    public static class Query {
        private Double lat;
        private Double lon;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLon() {
            return lon;
        }

        public void setLon(Double lon) {
            this.lon = lon;
        }

        public String getPlusCode() {
            return plusCode;
        }

        public void setPlusCode(String plusCode) {
            this.plusCode = plusCode;
        }

        @SerializedName("plus_code")
        private String plusCode;
    }
}

package uz.codebyz.auth.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.annotate.JsonIgnore;

public class AddressResponse {
    @JsonProperty("full_address")
    String display_name;
    private Address address;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}

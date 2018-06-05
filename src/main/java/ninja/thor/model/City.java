package ninja.thor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class City {
    private String name;
    private String country;
    private float lat;
    private float lon;
    private int population;
    @JsonIgnore
    private List<City> sisterCities;


    public void addSisterCity(City city) {
        if(city == null) {
            return;
        }
        if(sisterCities == null) {
            sisterCities = new ArrayList<>();
        }
        sisterCities.add(city);
    }
}

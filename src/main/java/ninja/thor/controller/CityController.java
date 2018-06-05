package ninja.thor.controller;

import com.sun.tools.javac.util.Pair;
import ninja.thor.model.*;
import ninja.thor.scraper.WikipediaScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class CityController {

    private final WikipediaScraper wikipedia;

    private Lines lines;
    private List<Image> images;

    @Autowired
    public CityController(WikipediaScraper wikipedia) {
        this.wikipedia = wikipedia;
        lines = new Lines();
        images = new ArrayList<>();
        initLines();
        initImages();
    }

    private void initImages() {
        int i = 0;
        for(City city : wikipedia.getUniqueCities()) {
            images.add(
                    Image.builder()
                            .id(String.valueOf(i))
                            .title(city.getName() + " " + city.getCountry())
                            .latitude(city.getLat())
                            .longitude(city.getLon())
                            .svgPath("M9,0C4.029,0,0,4.029,0,9s4.029,9,9,9s9-4.029,9-9S13.971,0,9,0z M9,15.93 c-3.83,0-6.93-3.1-6.93-6.93S5.17,2.07,9,2.07s6.93,3.1,6.93,6.93S12.83,15.93,9,15.93 M12.5,9c0,1.933-1.567,3.5-3.5,3.5S5.5,10.933,5.5,9S7.067,5.5,9,5.5 S12.5,7.067,12.5,9z")
                            .scale(.5)
                            .build());
            i++;
        }
    }

    private void initLines() {
        for(City city : wikipedia.getCityList()) {
            for(City sisterCity : city.getSisterCities()) {
                List<Float> lats = new ArrayList<>();
                Collections.addAll(lats, city.getLat(), sisterCity.getLat());
                List<Float> lons = new ArrayList<>();
                Collections.addAll(lons, city.getLon(), sisterCity.getLon());


                lines.addLine(Line.builder()
                        .latitudes(lats)
                        .longitudes(lons)
                        .build()
                );
            }
        }
    }

    @RequestMapping("/getTwinCities")
    public Lines getTwinCities() {
        return lines;
    }

    @RequestMapping("/getCities")
    public List<City> getCities() {
        return wikipedia.getUniqueCities();
    }

    @RequestMapping("/getDataProvider")
    public DataProvider getDataProvider() {
        return DataProvider.builder()
                .map("worldLow")
                .zoomLevel(3.5)
                .zoomLongitude(-20.1341)
                .zoomLatitude(49.1712)
                .lines(lines.getLines())
                .images(images)
                .build();
    }
}

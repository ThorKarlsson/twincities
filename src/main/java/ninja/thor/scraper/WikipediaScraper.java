package ninja.thor.scraper;

import lombok.extern.slf4j.Slf4j;
import ninja.thor.model.City;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class WikipediaScraper {

    private String rootUrl = "https://en.wikipedia.org/wiki/Lists_of_twin_towns_and_sister_cities";

    private Map<String, City> cities = new HashMap<>();

    private List<City> cityList;

    public WikipediaScraper() throws IOException {
        cityList = startScraper();
    }

    private List<City> startScraper() throws IOException {
        Document doc = Jsoup.connect(rootUrl).get();
        Elements headings = doc.select("h2");
        for(Element h2 : headings) {
            if(h2.selectFirst("a") != null) {
                Element el = h2.nextElementSibling();
                return scrapeContinent(el.selectFirst("a").attr("abs:href"));
            }
        }
        return null;
    }

    private List<City> scrapeContinent(String link) throws IOException {
        log.info("Scraping twin cities of countries in {}", link);
        Document doc = Jsoup.connect(link).get();
        Elements countries = doc.select("h2");
        List<City> cities = new ArrayList<>();
        for(Element country : countries) {
            if(country.selectFirst("span") != null) {
                log.info("Scraping twin cities in {}", country.selectFirst("span").text());
                cities.addAll(scrapeCountry(country));
            }
        }
        return cities;
    }

    private List<City> scrapeCountry(Element country) {
        List<City> cities = new ArrayList<>();
        Element city = country.nextElementSibling();
        while(city.is("ul")) {
            String link = city.selectFirst("li").selectFirst("a").attr("abs:href");
            log.info(link);
            City parentCity = addCityToMap(link);
            if(parentCity == null) {
                log.info("Couldn't scrape info on {}", link);
                city = city.nextElementSibling().nextElementSibling();
                continue;
            }

            Element dl = city.nextElementSibling();
            for (Element dd : dl.select("dd")) {
                Elements links = dd.select("a");
                if(!links.isEmpty()) {
                    Element twinnedCity = (dd.selectFirst("span") == null ? links.get(0) : links.get(1));
                    parentCity.addSisterCity(addCityToMap(twinnedCity.attr("abs:href")));
                }
            }
            cities.add(parentCity);
            city = dl.nextElementSibling();
        }
        return cities;
//        String link = el.selectFirst("ul").selectFirst("li").selectFirst("a").attr("abs:href");
//        log.info(link);
//        addCityToMap(link);

    }

    private City addCityToMap(String link) {
        if(cities.get(link) == null) {
            City city  = scrapeCityInfo(link);
            if(city != null) {
                cities.put(link, city);
            }
            return city;
        }
        log.info("City {} already added, skipping extraction", link);
        return cities.get(link);
    }

    private City scrapeCityInfo(String link) {
        try {
            Document doc = Jsoup.connect(link).get();
            String name = doc.selectFirst("h1.firstHeading").text();

            Element infoBox = doc.selectFirst("table.infobox");
            if(infoBox == null) {
                log.info("No geography info for {}", name);
                return null;
            }
            Element latEl = infoBox.selectFirst("span.latitude");
            Element lonEl = infoBox.selectFirst("span.longitude");
            String lat;
            String lon;
            if(latEl != null && lonEl != null) {
                lat = latEl.text();
                lon = lonEl.text();
            } else {
                return null;
            }

            Element countryEl = infoBox.selectFirst("th:contains(Country)");

            String country = "";
            if(countryEl != null) {
                country = countryEl.nextElementSibling().text();
            } else {
                log.info("No country info found for city");
            }

            log.info("Adding city {} ({}, {}) in {}", name, lat, lon, country);

            if(lat == null || lon == null ||lat.equals("") || lon.equals("")) {
                return null;
            }

            lat = lat.replaceAll("\\.", "").replaceAll("°", ".").replaceAll("′", "").replaceAll("″", "");
            lon = lon.replaceAll("\\.", "").replaceAll("°", ".").replaceAll("′", "").replaceAll("″", "");


            if(lat.contains("S")) {
                lat = "-".concat(lat);
            }
            if(lon.contains("W")) {
                lon = "-".concat(lon);
            }
            lat = lat.replaceAll("N", "").replaceAll("S", "");
            lon = lon.replaceAll("E", "").replaceAll("W", "");

            return City.builder()
                    .name(name)
                    .country(country)
                    .lat(Float.valueOf(lat))
                    .lon(Float.valueOf(lon))
                    .sisterCities(new ArrayList<>())
                    .build();
        } catch (IOException e) {
            log.error("Error scraping city {}", link);
        }
        return null;
    }

    public List<City> getCityList () {
        return cityList;
    }

    public List<City> getUniqueCities() {
        List<City> citieList = new ArrayList<>();
        for (Map.Entry<String, City> entry : cities.entrySet()) {
            citieList.add(entry.getValue());
        }
        return citieList;
    }
}

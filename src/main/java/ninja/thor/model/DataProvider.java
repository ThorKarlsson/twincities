package ninja.thor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DataProvider {
    private String map;
    private double zoomLevel;
    private double zoomLongitude;
    private double zoomLatitude;
    private List<Line> lines;
    private List<Image> images;

    public void addLine(Line line) {
        if(lines == null) {
            lines = new ArrayList<>();
        }
        lines.add(line);
    }

    public void addImage(Image image) {
        if(image == null) {
            images = new ArrayList<>();
        }
        images.add(image);
    }

}

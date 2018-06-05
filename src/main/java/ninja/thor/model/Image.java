package ninja.thor.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Image {
    private String id;
    private String svgPath;
    private String title;
    private float latitude;
    private float longitude;
    private double scale;
}

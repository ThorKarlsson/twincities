package ninja.thor.model;

import com.sun.tools.javac.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class Line {
    private List<Float> latitudes;
    private List<Float> longitudes;
}

package ninja.thor.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Lines {
    private List<Line> lines = new ArrayList<>();

    public void addLine(Line line) {
        lines.add(line);
    }
}

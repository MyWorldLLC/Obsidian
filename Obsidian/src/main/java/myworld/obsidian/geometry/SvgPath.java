package myworld.obsidian.geometry;

public record SvgPath(String path, SvgDist... moves) {

    public String interpolate(float width, float height){
        String interpolated = path;
        for(var move : moves){
            interpolated = interpolated.replaceFirst("%",
                    switch (move.direction()){
                        case HORIZONTAL -> distanceToString(move.distance(), width);
                        case VERTICAL -> distanceToString(move.distance(), height);
            });
        }
        return interpolated;
    }

    protected String distanceToString(Distance d, float size){
        return Integer.toString(Math.round(d.toPixels(size)));
    }
}

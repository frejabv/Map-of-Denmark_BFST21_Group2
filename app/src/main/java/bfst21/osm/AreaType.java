package bfst21.osm;

public enum AreaType {
    BOROUGH(30, 0),
    CITY(250, 0),
    HAMLET(20, 0),
    ISLAND(250, 20),
    ISLET(10, 0),
    NEIGHBOURHOOD(15, 0),
    QUARTER(30, 0),
    SUBURB(30, 0),
    TOWN(50, 0),
    VILLAGE(50, 0);

    public float zoomMax;
    public float zoomMin;

    AreaType(float zoomMax, float zoomMin) {
        this.zoomMax = zoomMax;
        this.zoomMin = zoomMin;
    }
}

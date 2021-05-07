package bfst21.pathfinding;

public enum TransportType {
    CAR(130),
    BICYCLE(15),
    WALK(5);

    public int maxSpeed;

    TransportType(int speed){ this.maxSpeed = speed; }
}

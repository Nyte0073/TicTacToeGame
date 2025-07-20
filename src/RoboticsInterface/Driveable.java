package RoboticsInterface;

import java.util.Map;

public interface Driveable {
    boolean needsToStop();
    void drive();
    void updateTelemetry(Map <Object, Object> telemetryMap);

    String frontLeftMotor = "frontLeft",
    frontRightMotor = "frontRight",
    backLeftMotor = "backLeft",
    backRightMotor = "backRight",
    frontLeftDriving = "frontLeftDriving",
    frontRightDriving = "frontRightDriving",
    backLeftDriving = "backLeftDriving",
    backRightDriving = "backRightDriving";
}

package RoboticsInterface;

import java.util.Map;

public class SwerveDrivetrain implements Driveable {

    @Override
    public boolean needsToStop() {
        return false;
    }

    @Override
    public void drive() {

    }

    @Override
    public void updateTelemetry(Map<Object, Object> telemetryMap) {

    }
}

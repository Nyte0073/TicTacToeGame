package MainFolder;

public class Main {

    public static void main(String[] args) {
        final double VECTOR_SCALAR = 1.4142135623730950488016887242097;
        IMU imu = new IMU();
        Controller controller = new Controller();
        Angles angles = new Angles();
        imu.getOrientation(110);
        int previousHeading = 52;
        controller.getControllerInputs(0,0.01);

        int targetHeading = (Math.abs(controller.x) <= 0.01 && Math.abs(controller.y) <= 0.01) ? 0 : (int) Math.round(Math.toDegrees(Math.atan2(controller.y, controller.x))) - 90;

        System.out.println(STR."IMU Orientation in Degrees: \{imu.orientationInDegrees} degrees");
        System.out.println(STR."Controller X: \{controller.x}, Controller Y: \{controller.y}");
        System.out.println(STR."Target Heading: \{targetHeading} degrees");

        angles.setCurrentHeading(imu.orientationInDegrees);
        angles.setTargetHeading(targetHeading);

        int normalizedHeading = angles.normalizedHeading(angles.currentHeading, angles.targetHeading);
        int normalizedPreviousHeading = angles.normalizedHeading(previousHeading, normalizedHeading);
        System.out.println(STR."Normalized Heading: \{normalizedHeading} degrees");
        System.out.println(STR."Vector Motor Power: \{Math.hypot(controller.x, controller.y) / VECTOR_SCALAR}");

        System.out.println(STR."Normalized Previous Heading: \{normalizedPreviousHeading}");

        double totalHeading = previousHeading + normalizedPreviousHeading;

        int reversedHeading = Math.abs(totalHeading) > 180 ? angles.normalizedHeading(previousHeading,
                (normalizedHeading != Math.abs(normalizedHeading) ? normalizedHeading + 180 : normalizedHeading - 180)) : 0;

        System.out.println(STR."Reversed heading: \{reversedHeading}");

        boolean motorPowerReversed = Math.abs(totalHeading) > 180;

        System.out.println("Motor Power Reversed: " + motorPowerReversed);

        double previousHeadingFieldOriented = Math.abs(totalHeading) > 180 ? previousHeading + reversedHeading : normalizedHeading;

        System.out.println("Previous Heading Field Oriented: " + previousHeadingFieldOriented);
    }
}

class IMU {
    public int orientationInDegrees = 0;
    public void getOrientation(int d) {
        orientationInDegrees = d;
    }
}

class Angles {
    int currentHeading, targetHeading;

    public void setTargetHeading(int targetHeading) {
        this.targetHeading = targetHeading;
    }

    public void setCurrentHeading(int currentHeading) {
        this.currentHeading = currentHeading;
    }

    public int normalizedHeading(int currentHeading, int targetHeading) {
        return (targetHeading - currentHeading + 540) % 360 - 180;
    }
}

class Controller {
    public double x, y;
    public void getControllerInputs(double forward, double side) {
        x = side;
        y = forward;
    }
}

class SecondMain {
    public static void main(String[] args) {
        System.out.println((-207 + 540) % 360 - 180);
        System.out.println(300 % 45);
    }
}



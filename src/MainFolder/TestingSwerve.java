package MainFolder;

public class TestingSwerve {

    double previousHeading = 14;

    public double normalizeHeading(double currentHeading, double targetHeading) {
        return (targetHeading - currentHeading + 540) % 360 - 180;
    }

    public void rotateByPower(double power, double headingDegrees, boolean turningLeft) {
        double headingFrontRightBackLeft = turningLeft ? 45 : -45, headingFrontLeftBackRight = turningLeft ? -45 : 45,
                reversedHeadingFRBL, reversedHeadingFLBR, normalizedHeadingPreFRBL, normalizedHeadingPreFLBR;

        boolean headingReversedFRBL, headingReversedFLBR;

        double normalizedHeadingFRBL = normalizeHeading(headingDegrees, headingFrontRightBackLeft);
        normalizedHeadingPreFRBL = normalizeHeading(previousHeading, normalizedHeadingFRBL);
        double totalHeadingFRBL = previousHeading + normalizedHeadingPreFRBL;

        double normalizedHeadingFLBR = normalizeHeading(headingDegrees, headingFrontLeftBackRight);
        normalizedHeadingPreFLBR = normalizeHeading(previousHeading, normalizedHeadingFLBR);
        double totalHeadingFLBR = previousHeading + normalizedHeadingFLBR;

        reversedHeadingFRBL = Math.abs(totalHeadingFRBL) > 180 ? normalizeHeading(previousHeading,
                normalizedHeadingFRBL != Math.abs(normalizedHeadingFRBL) ? normalizedHeadingFRBL + 180 : normalizedHeadingFRBL - 180) : 0;

        reversedHeadingFLBR = Math.abs(totalHeadingFLBR) > 180 ? normalizeHeading(previousHeading,
                normalizedHeadingFLBR != Math.abs(normalizedHeadingFLBR) ? normalizedHeadingFLBR + 180 : normalizedHeadingFLBR - 180) : 0;

        headingReversedFRBL = Math.abs(totalHeadingFRBL) > 180;
        headingReversedFLBR = Math.abs(totalHeadingFLBR) > 180;

        System.out.println("Normalized Heading Pre FLBR: " + normalizedHeadingPreFLBR);
        System.out.println("Normalized Heading Pre FRBL: " + normalizedHeadingFRBL);

        double frontLeftBackRightPosition = ((headingReversedFLBR ? reversedHeadingFLBR : normalizedHeadingPreFLBR) / 360) * 1440;
        double frontRightBackLeftPosition = ((headingReversedFRBL ? reversedHeadingFRBL : normalizedHeadingPreFRBL) / 360) * 1440;

        System.out.println("FrontLeft/BackRight position: " + frontLeftBackRightPosition);
        System.out.println("FrontRight/BackLeft position: " + frontRightBackLeftPosition);

        setPowerForRotation(power, turningLeft, headingReversedFLBR, headingReversedFRBL);
    }

    public void setPowerForRotation(double turnVector, boolean turningLeft, boolean headingReversedFLBR, boolean headingReversedFRBL) {
        double driveVectorFLBL = headingReversedFLBR ? (turningLeft ? turnVector : -turnVector) : (turningLeft ? -turnVector : turnVector);
        double driveVectorFRBR = headingReversedFRBL ? (turningLeft ? -turnVector : turnVector) : (turningLeft ? turnVector : -turnVector);

        System.out.println("Drive Vector FL/BL: " + driveVectorFLBL);
        System.out.println("Drive Vector FR/BR: " + driveVectorFRBR);
    }
    public static void main(String[] args) {
        double imuOrientation = 0;
        double controllerRightX = 0.5;

        new TestingSwerve().rotateByPower(controllerRightX, imuOrientation, true);
    }
}

package MainFolder;

public class Main {
    static int[] individualTargetPositions = new int[4];

    public static int calculateTotalHeading(int currentHeading, int targetHeading, int wheelNumber) {
        int normalizedHeadingForWheel = normalizeHeading(currentHeading, targetHeading);
        System.out.println(STR."Normalized Heading For Wheel \{wheelNumber + 1}: \{normalizedHeadingForWheel}");
        individualTargetPositions[wheelNumber] = normalizedHeadingForWheel;
        return currentHeading + normalizedHeadingForWheel;
    }

    public static int calculateReverseHeading(int totalHeading, int normalizedHeading, int wheelHeading) {
        return Math.abs(totalHeading) > 180 ? normalizeHeading(wheelHeading,
                normalizedHeading != Math.abs(normalizedHeading) ? normalizedHeading + 180 : normalizedHeading - 180) : 2000;
    }

    public static int normalizeHeading(int currentPosition, int targetPosition) {
        return (targetPosition - currentPosition + 540) % 360 - 180;
    }

    public static void main(String[] args) {
        final double VECTOR_SCALAR = 1.4142135623730950488016887242097;

        int[] individualWheelHeadings = {100, 180, -67, 84};

        boolean[] headingsReversed = new boolean[4];

        int imuHeading = 110;

        double y = 1;
        double x = 0;

       int targetHeading = (Math.abs(x) < 0.05 && Math.abs(y) < 0.05) ? 0 : (int) Math.toDegrees(Math.atan2(y, x)) - 90;

        System.out.println(STR."Target Heading: \{targetHeading}");

        double forwardVector = Math.hypot(y, x) / VECTOR_SCALAR;

        System.out.println(STR."Forward Vector: \{forwardVector}");

        int normalizedHeading = normalizeHeading(imuHeading, targetHeading);

        System.out.println(STR."Normalized Heading: \{normalizedHeading}");

        int totalHeadingFrontLeft = calculateTotalHeading(individualWheelHeadings[0], normalizedHeading, 0);
        int reversedHeadingFrontLeft = calculateReverseHeading(totalHeadingFrontLeft, normalizedHeading, individualWheelHeadings[0]);

        int totalHeadingFrontRight = calculateTotalHeading(individualWheelHeadings[1], normalizedHeading, 1);
        int reversedHeadingFrontRight =  calculateReverseHeading(totalHeadingFrontRight, normalizedHeading, individualWheelHeadings[1]);

        int totalHeadingBackLeft = calculateTotalHeading(individualWheelHeadings[2], normalizedHeading, 2);
        int reversedHeadingBackLeft = calculateReverseHeading(totalHeadingBackLeft, normalizedHeading, individualWheelHeadings[2]);

        int totalHeadingBackRight = calculateTotalHeading(individualWheelHeadings[3], normalizedHeading, 3);
        int reversedHeadingBackRight = calculateReverseHeading(totalHeadingBackRight, normalizedHeading, individualWheelHeadings[3]);

        headingsReversed[0] = reversedHeadingFrontLeft != 2000;
        headingsReversed[1] = reversedHeadingFrontRight != 2000;
        headingsReversed[2] = reversedHeadingBackLeft != 2000;
        headingsReversed[3] = reversedHeadingBackRight != 2000;

        System.out.println(STR."Heading reversed for motor 1: \{headingsReversed[0]}");
        System.out.println(STR."Heading reversed for motor 2: \{headingsReversed[1]}");
        System.out.println(STR."Heading reversed for motor 3: \{headingsReversed[2]}");
        System.out.println(STR."Heading reversed for motor 4: \{headingsReversed[3]}");

        System.out.println(STR."Total Heading Front Left: \{totalHeadingFrontLeft}");
        System.out.println(STR."Reversed Heading Front Left: \{reversedHeadingFrontLeft}");
        System.out.println(STR."Calculated Heading Front Left: \{individualTargetPositions[0]}");

        System.out.println(STR."Total Heading Front Right: \{totalHeadingFrontRight}");
        System.out.println(STR."Reversed Heading Front Right: \{reversedHeadingFrontRight}");
        System.out.println(STR."Calculated Heading Front Right: \{individualTargetPositions[1]}");

        System.out.println(STR."Total Heading Back Left: \{totalHeadingBackLeft}");
        System.out.println(STR."Reversed Heading Back Left: \{reversedHeadingBackLeft}");
        System.out.println(STR."Calculated Heading Back Left: \{individualTargetPositions[2]}");

        System.out.println(STR."Total Heading Back Right: \{totalHeadingBackRight}");
        System.out.println(STR."Reversed Heading Back Right: \{reversedHeadingBackRight}");
        System.out.println(STR."Calculated Heading Back Right: \{individualTargetPositions[3]}");

    }
}



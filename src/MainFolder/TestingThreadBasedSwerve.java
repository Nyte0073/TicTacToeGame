package MainFolder;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class TestingThreadBasedSwerve {

    public static void main(String[] args) {
        ThreadBasedSwerveDrive swerveDrive = new ThreadBasedSwerveDrive();
        swerveDrive.launchFTCSwerve();
    }

    public static int normalizeHeading(int targetHeading, int currentHeading) {
        return (targetHeading - currentHeading + 540) % 360 - 180;
    }

    public static int calculateTotalHeading(int currentHeading, int normalizedHeading) {
        return currentHeading + normalizedHeading;
    }

    public static int calculateReversedHeading(int totalHeading, int currentHeading) {
        return Math.abs(totalHeading) > 180 ? normalizeHeading(
                totalHeading < 0 ? totalHeading + 180 : totalHeading - 180, currentHeading
        ) : 2000;
    }
}

class ThreadBasedSwerveDrive {

    JFrame wheelsFrame = new JFrame("Wheel Frame");

    Motor[] drivingMotors = new Motor[] {
            new Motor(1),
            new Motor(2),
            new Motor(3),
            new Motor(4)
    },

    turningMotors = new Motor[] {
            new Motor(5),
            new Motor(6),
            new Motor(7),
            new Motor(8)
    };

    HashMap <Motor, Integer> normalizedHeadingsRelativeToWheels = new HashMap<>(),
    totalHeadings = new HashMap<>(), reversedHeadings = new HashMap<>();
    final Object driveLock = new Object();
    Runnable[] runnable = new Runnable[4];
    static String driveType;
    volatile SwerveState swerveState = SwerveState.IDLE;
    Thread[] threads = {
        new Thread( // Driving thread
                () -> {
                    while(!Thread.currentThread().isInterrupted()) {
                        synchronized(driveLock) {
                           while(swerveState != SwerveState.DRIVE && swerveState != SwerveState.IDLE) {
                               try {
                                   driveLock.wait();
                               } catch(Exception e) {
                                   Thread.currentThread().interrupt();
                                   break;
                               }
                           }

                           if(swerveState == SwerveState.DRIVE) {
                               runnable[0].run();
                               driveLock.notifyAll();
                               try {
                                   Thread.sleep(50);
                               } catch(Exception e) {
                                   Thread.currentThread().interrupt();
                               }
                               swerveState = SwerveState.IDLE;
                               driveLock.notifyAll();
                           }

                        }

                        try {
                            Thread.sleep(25);
                        } catch(Exception e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
        , "DriveThread"),

            new Thread( //Complete rotate thread.
                    () -> {
                        while(!Thread.currentThread().isInterrupted()) {
                            synchronized (driveLock) {
                                while(swerveState != SwerveState.COMPLETE_ROTATE && swerveState != SwerveState.IDLE) {
                                    try {
                                        driveLock.wait();
                                    } catch(Exception e) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }

                                    if(swerveState == SwerveState.COMPLETE_ROTATE) {
                                        driveLock.notifyAll();
                                        runnable[1].run();
                                        runnable[2].run();
                                        try {
                                            Thread.sleep(50);
                                        } catch(Exception e) {
                                            Thread.currentThread().interrupt();
                                        }
                                        swerveState = SwerveState.IDLE;
                                        driveLock.notifyAll();
                                    }
                            }


                            try {
                                Thread.sleep(25);
                            } catch(Exception e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
            , "CompleteRotateThread"),

            new Thread( //Reset wheel heading thread.
                    () -> {
                        while(!Thread.currentThread().isInterrupted()) {
                            synchronized (driveLock) {
                                while(swerveState != SwerveState.RESET_WHEEL && swerveState != SwerveState.IDLE) {
                                    try {
                                        driveLock.wait();
                                    } catch(Exception e) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }

                                    if(swerveState == SwerveState.RESET_WHEEL) {
                                        driveLock.notifyAll();
                                        runnable[3].run();
                                        try {
                                            Thread.sleep(50);
                                        } catch(Exception e) {
                                            Thread.currentThread().interrupt();
                                        }
                                        swerveState = SwerveState.IDLE;
                                        driveLock.notifyAll();
                                    }
                            }

                            try{
                                Thread.sleep(25);
                            } catch(Exception e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
            , "ResetWheelHeadingThread"),

            new Thread(
                    () -> {
                        JFrame frame = new JFrame("Robotics JFrame");
                        frame.setSize(new Dimension(800, 800));
                        frame.setLayout(null);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
                        panel.setBounds(100, 100, 600, 600);
                        JButton[] buttons = {
                                new JButton(),
                                new JButton(),
                                new JButton(),
                                new JButton()
                        };

                        for(JButton button : buttons) {
                            panel.add(button);
                        }

                        frame.add(panel);
                        frame.setVisible(true);

                        wheelsFrame.setSize(new Dimension(1000, 1000));
                        wheelsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                        RoboticsGUI roboticsGUI = new RoboticsGUI();
                        wheelsFrame.add(roboticsGUI);

                        wheelsFrame.setVisible(true);

                        while(!Thread.currentThread().isInterrupted()) {
                            int n = 0;
                            Thread.State[] states = threadStates();

                            for(int i = 0; i < states.length; i++) {
                                if(states[i] == Thread.State.BLOCKED) {
                                    n++;
                                    buttons[i].setBackground(Color.LIGHT_GRAY);
                                } else if(states[i] == Thread.State.TIMED_WAITING) {
                                    buttons[i].setBackground(Color.WHITE);
                                }
                            }

                            if(n >= 2) {
                                buttons[3].setBackground(Color.GREEN);
                            } else if(n == 1 && swerveState != SwerveState.IDLE) {
                                buttons[3].setBackground(Color.RED);
                            }


                            try {
                                Thread.sleep(20);
                            } catch(Exception e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
            , "GUIThread")
    };

    public Thread.State[] threadStates() {
        return new Thread.State[] {
                threads[0].getState(),
                threads[1].getState(),
                threads[2].getState()
        };
    }

    enum SwerveState {
        IDLE,
        DRIVE,
        COMPLETE_ROTATE,
        RESET_WHEEL
    }

    public void launchFTCSwerve() {
        GamepadEx gamepadEx = new GamepadEx();
        IMU imu = new IMU();

        int[] reversedHeads = {0, 0, 0, 0}, normalizedHeads = {0, 0, 0, 0},
                totalHeads = {0, 0, 0, 0};

        for(Thread t : threads) {
            t.start();
        }

        while(!Thread.currentThread().isInterrupted()) {

            try {
                Thread.sleep(50);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                break;
            }

                gamepadEx.generateRandomGamepadExMovements();
                imu.generateRandomIMUOrientation();

                switch (driveType) {
                    case "regular movement":
                        int[] targetAngleAndNormalizedRelativeRobotHeading = gamepadEx.calculateNormalizedHeadingRelativeToRobotAndTargetAngle(
                                gamepadEx.getLeftX(), gamepadEx.getLeftY(), imu.getOrientationInDegrees()
                        );

                        runnable[0] = () -> {

                            double forwardVector = Math.hypot(gamepadEx.getLeftX(), gamepadEx.getLeftY()) / Motor.vectorScalar;

                            for (Motor motor : turningMotors) {
                                int normalizedAngleRelativeToWheel = gamepadEx.calculateNormalizedHeadingRelativeToWheel(
                                        targetAngleAndNormalizedRelativeRobotHeading[1], motor.getPreviousHeading()
                                );
                                int totalHeading = motor.getPreviousHeading() + normalizedAngleRelativeToWheel;
                                int reversedWheelHeading = gamepadEx.calculateReversedHeadingForWheel(motor.getPreviousHeading(), totalHeading);

                                reversedHeads[motor.getMotorNumber() - 5] = reversedWheelHeading;
                                totalHeads[motor.getMotorNumber() - 5] = totalHeading;
                                normalizedHeads[motor.getMotorNumber() - 5] = normalizedAngleRelativeToWheel;


                                normalizedHeadingsRelativeToWheels.put(motor, normalizedAngleRelativeToWheel);
                                totalHeadings.put(motor, totalHeading);
                                reversedHeadings.put(motor, reversedWheelHeading);

                                int targetPosition = reversedWheelHeading != 2000 ? motor.getPreviousHeading() + reversedWheelHeading : motor.getPreviousHeading() + normalizedAngleRelativeToWheel;

                                targetPosition = gamepadEx.normalizedHeading(0, targetPosition);

                                motor.setPreviousHeading(targetPosition);
                                motor.setTargetPosition(targetPosition);

                                drivingMotors[motor.getMotorNumber() - 5].set(targetPosition == reversedWheelHeading ? -forwardVector : forwardVector);
                            }

                            int[] currentWheelHeadings = new int[4];
                            for(int i = 0; i < currentWheelHeadings.length; i++) {
                                currentWheelHeadings[i] = turningMotors[i].getPreviousHeading();
                            }

                            RoboticsGUI.setTargetAngle(targetAngleAndNormalizedRelativeRobotHeading[0]);
                            RoboticsGUI.setCurrentWheelAngles(currentWheelHeadings);
                            RoboticsGUI.setReversedHeadings(reversedHeads);
                            RoboticsGUI.setNormalizedHeadings(normalizedHeads);
                            RoboticsGUI.setTotalHeadings(totalHeads);
                        };

                        if (swerveState == SwerveState.IDLE) {
                            swerveState = SwerveState.DRIVE;
                        }
                        break;

                    case "complete rotation":
                        runnable[1] = () -> {

                            for (Motor motor : turningMotors) {
                                int targetHeading;

                                if (motor.getMotorNumber() == 5 || motor.getMotorNumber() == 8) {
                                    targetHeading = -45;
                                } else {
                                    targetHeading = 45;
                                }

                                int normalizedHeadingRelativeToWheel = gamepadEx.normalizedHeading(motor.getPreviousHeading(), targetHeading);
                                int totalHeading = motor.getPreviousHeading() + normalizedHeadingRelativeToWheel;
                                int reversedHeading = gamepadEx.calculateReversedHeadingForWheel(motor.getPreviousHeading(),
                                        totalHeading);

                                reversedHeads[motor.getMotorNumber() - 5] = reversedHeading;
                                totalHeads[motor.getMotorNumber() - 5] = totalHeading;
                                normalizedHeads[motor.getMotorNumber() - 5] = normalizedHeadingRelativeToWheel;

                                int targetPosition = reversedHeading != 2000 ? motor.getPreviousHeading() + reversedHeading : motor.getPreviousHeading() + normalizedHeadingRelativeToWheel; //This is just distance, NOT the total target position

                                targetPosition = gamepadEx.normalizedHeading(0, targetPosition);

                                motor.setPreviousHeading(targetPosition);
                                motor.setTargetPosition(targetPosition);

                                normalizedHeadingsRelativeToWheels.put(motor, normalizedHeadingRelativeToWheel);
                                totalHeadings.put(motor, totalHeading);
                                reversedHeadings.put(motor, reversedHeading);
                            }

                            int[] currentWheelHeadings = new int[4];
                            for(int i = 0; i < currentWheelHeadings.length; i++) {
                                currentWheelHeadings[i] = turningMotors[i].getPreviousHeading();
                            }

                            RoboticsGUI.setCurrentWheelAngles(currentWheelHeadings);
                            RoboticsGUI.setReversedHeadings(reversedHeads);
                            RoboticsGUI.setNormalizedHeadings(normalizedHeads);
                            RoboticsGUI.setTotalHeadings(totalHeads);
                        };

                        runnable[2] = () -> {
                        };

                        if (swerveState == SwerveState.IDLE) {
                            swerveState = SwerveState.COMPLETE_ROTATE;
                        }
                        break;

                    case "reset wheel":
                        runnable[3] = () -> {
                        };
                        if (swerveState == SwerveState.IDLE) {
                            swerveState = SwerveState.RESET_WHEEL;
                        }
                        break;
                }
            }


        }
    }


class Motor {

    public static final double vectorScalar = 1.4142135623730950488016887242097;
    final private int motorNumber;
    private volatile double motorPower = 0 , targetPosition = 0;
    private int previousHeading = 0;

    public Motor(int motorNumber) {
        this.motorNumber = motorNumber;
    }

    public void setTargetPosition(double targetPosition) {
        this.targetPosition = targetPosition;
    }

    public void set(double power) {
        motorPower = power;
    }

    public void setPreviousHeading(int previousHeading) {
        this.previousHeading = previousHeading;
    }

    public int getPreviousHeading() {
        return previousHeading;
    }

    public int getMotorNumber() {
        return motorNumber;
    }

    public double getMotorPower() {
        return motorPower;
    }

    public double getTargetPosition() {
        return targetPosition;
    }

    public boolean atTargetPosition() {
        return true;
    }
}

class IMU {

    private int orientationInDegrees;

    public IMU() {

    }

    public void generateRandomIMUOrientation() {
        setOrientationInDegrees((int) Math.round(Math.random() * 360));
    }

    public void setOrientation(Scanner sc) {
        System.out.println("Enter IMU orientation (in degrees).");
        int s = sc.nextInt();
        sc.nextLine();

        setOrientationInDegrees(s);
    }

    public void setOrientationInDegrees(int orientationInDegrees) {
        this.orientationInDegrees = orientationInDegrees;
    }

    public int getOrientationInDegrees() {
        return orientationInDegrees;
    }
}

class GamepadEx {
    private double rightX = 0, leftX = 0, leftY = 0;
    String command = "";

    String[] driveTypes = {
           "regular movement",
           "complete rotation",
           "reset wheel"
    };

    public static Scanner s = new Scanner(System.in);

    int i = 0;

    public void generateRandomGamepadExMovements() {

        int randomNum = ((int) Math.round(Math.random() * 3));
        ThreadBasedSwerveDrive.driveType = driveTypes[randomNum == 0 ? 0 : randomNum - 1];

        double leftXNegative = Math.random();
        double leftYNegative = Math.random();

        switch(ThreadBasedSwerveDrive.driveType) {
            case "regular movement":
                setLeftX(leftXNegative > 0.5 ? -Math.random() : Math.random());
                setLeftY(leftYNegative > 0.5 ? -Math.random() : Math.random());
                setRightX(0);
                break;

            case "complete rotation":
                setLeftX(0);
                setLeftY(0);
                setRightX(Math.random());

                System.out.println("Turning vector: " + getRightX());
                break;
        }

        i++;
    }

    public void setGamepadExMovements() {

        System.out.println("Enter command.");
        String command = s.nextLine();
        this.command = command;
        ThreadBasedSwerveDrive.driveType = command;

        switch(command) {
            case "complete rotation":

                System.out.println("Enter turning vector.");
                double turningVector = s.nextDouble();
                s.nextLine();

                setRightX(turningVector);
                break;

            case "regular movement":

                System.out.println("Enter x value.");
                double x = s.nextDouble();
                s.nextLine();

                System.out.println("Enter y value.");
                double y = s.nextDouble();
                s.nextLine();

                setLeftX(x);
                setLeftY(y);
                break;
        }
    }

    public void setLeftX(double leftX) {
        this.leftX = leftX;
    }

    public void setLeftY(double leftY) {
        this.leftY = leftY;
    }

    public void setRightX(double rightX) {
        this.rightX = rightX;
    }

    public double getLeftX() {
        return leftX;
    }

    public double getLeftY() {
        return leftY;
    }

    public double getRightX() {
        return rightX;
    }

    public int[] calculateNormalizedHeadingRelativeToRobotAndTargetAngle(double x, double y, int robotHeading) {
        int targetAngle = (int) Math.round(Math.toDegrees(Math.atan2(y, x))) - 90;
        int normalizedHeadingRelativeToRobot = normalizedHeading(robotHeading, targetAngle);

        return new int[] {targetAngle, normalizedHeadingRelativeToRobot};
    }

    public int calculateNormalizedHeadingRelativeToWheel(int normalizedRobotHeading, int currentWheelHeading) {
        return normalizedHeading(currentWheelHeading, normalizedRobotHeading);
    }

    public int normalizedHeading(int currentHeading, int targetHeading) {
        return (targetHeading - currentHeading + 540) % 360 - 180;
    }

    public int calculateReversedHeadingForWheel(int currentHeadingOfWheel, int totalHeading) {
        return Math.abs(totalHeading) > 180 ? normalizedHeading(currentHeadingOfWheel,
                totalHeading < 0 ? totalHeading + 180 : totalHeading - 180) : 2000;
    }

}

/*= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = REAL TESTING DOWN HERE = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =*/

interface Driveable {
    void stop();
    void updateTelemetry(Telemetry telemetry);
    void drive() throws Exception;
    void resetGyro(IMU imu);

    class Telemetry {

    }
}

class SwerveVector {
    public int imuHeadingInDegrees;
    public double forwardPower,
    sidePower,
    turningVector;
    public boolean fieldOriented,
    turningLeft;

    public SwerveVector(int imuHeadingInDegrees, double forwardPower, double sidePower, double turningVector, boolean fieldOriented, boolean turningLeft) {
        this.imuHeadingInDegrees = imuHeadingInDegrees;
        this.forwardPower  = forwardPower;
        this.sidePower = sidePower;
        this.turningVector = turningVector;
        this.fieldOriented = fieldOriented;
        this.turningLeft = turningLeft;
    }
    public double degreesToRadians() {
        return Math.toRadians(imuHeadingInDegrees);
    }

    public double getForwardPower() {
        return forwardPower;
    }

    public double getSidePower() {
        return sidePower;
    }

    public double getTurningVector() {
        return turningVector;
    }

    public int getImuHeadingInDegrees() {
        return imuHeadingInDegrees;
    }

    public boolean getFieldOriented() {
        return fieldOriented;
    }

    public boolean getTurningLeft() {
        return turningLeft;
    }
}

abstract class RealSwerve implements Driveable {
    double forwardPower, sidePower, turningVector;
    int headingInDegrees;
    boolean fieldOriented, turningLeft;

    @Override
    public void stop() {

    }

    @Override
    public void updateTelemetry(Telemetry telemetry) {

    }

    @Override
    public void drive() throws Exception {
        SwerveVector robotVector = getRobotVector();

        forwardPower = robotVector.forwardPower;
        sidePower = robotVector.sidePower;
        turningVector = robotVector.turningVector;
        headingInDegrees = robotVector.imuHeadingInDegrees;

        fieldOriented = robotVector.fieldOriented;
        turningLeft = robotVector.turningLeft;

        setSwerveModuleState(fieldOriented, forwardPower, sidePower, headingInDegrees, turningVector, turningLeft);
    }

    @Override
    public void resetGyro(IMU imu) {

    }

    public abstract void setSwerveModuleState(boolean fieldOriented, double forwardPower, double sidePower, int headingInDegrees, double turningVector, boolean turningLeft) throws Exception;

    public abstract void stopMotors();

    public abstract void resetWheelHeading();

    public abstract void completeRotate(boolean turningLeft, int imuHeadingInDegrees, double turnVector, boolean fieldOriented);

    public abstract void setPowerForCompleteRotate(boolean turningLeft, boolean[] headingsReversed, double turningVector, int[] targetPositions, boolean goToPosition, boolean[] headingsDirectionsNegative);

    public abstract int normalizeHeading(int currentPosition, int targetPosition);

    public abstract SwerveVector getRobotVector();

    public abstract void setPower(boolean[] headingsReversed, double forwardVector);
}

class SwerveDriveFields {

    public volatile IMU imu;
    public volatile double forwardVector = 0;
    public volatile int normalizedHeading = 0, totalHeadingFrontLeft = 0, reversedHeadingFrontLeft = 0,
            totalHeadingFrontRight = 0, reversedHeadingFrontRight = 0, totalHeadingBackLeft = 0,
            reversedHeadingBackLeft = 0, totalHeadingBackRight = 0, reversedHeadingBackRight = 0, previousTurningVector = 0;
    public volatile boolean[] headingsReversed = new boolean[4], headingsNegativeOrNot = new boolean[4], wheelsHaveRotated = new boolean[4];
    public volatile int[] individualWheelHeadings = {0, 0, 0, 0}, individualTargetPositions = new int[4], previousTargetPositions = new int[4];
    public volatile boolean previousTurningLeft = false, fieldOriented = false, alreadyRotated = false;

    public volatile GamepadEx gamepadEx;
    public static volatile boolean stopMotorsIsRunning = false;
    public volatile Motor[] turningMotors, drivingMotors;
}

class CompletionThreadBasedSwerveDrive extends RealSwerve {

    enum SwerveState {
        IDLE,
        DRIVE,
        COMPLETE_ROTATE,
        RESET_WHEEL
    }

    enum CompleteRotations {
        ROTATION_WAS_DONE,
        ROTATION_WAS_NOT_DONE
    }

    Runnable[] runnables = new Runnable[] {
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
    };

    public static final double vectorScalar = 1.4142135623730950488016887242097;
    public static final int NO_REVERSAL = 2000;

    final Object driveLock = new Object();
    final SwerveDriveFields swerveDriveFields = new SwerveDriveFields();
    volatile SwerveState swerveState = SwerveState.IDLE;
    volatile CompleteRotations rotations = CompleteRotations.ROTATION_WAS_NOT_DONE;

    Thread[] threads = {
            new Thread( // Driving thread
                    () -> {
                        while(!Thread.currentThread().isInterrupted()) {
                            synchronized(driveLock) {
                                while(swerveState != SwerveState.DRIVE && swerveState != SwerveState.IDLE) {
                                    try {
                                        driveLock.wait();
                                    } catch(Exception e) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }

                                if(swerveState == SwerveState.DRIVE) {
                                    driveLock.notifyAll();
                                    runnables[0].run();
                                    runnables[1].run();
                                    try {
                                        Thread.sleep(50);
                                    } catch(Exception e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    swerveState = SwerveState.IDLE;
                                    driveLock.notifyAll();
                                }

                            }

                            try {
                                Thread.sleep(25);
                            } catch(Exception e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                    , "DriveThread"),

            new Thread( //Complete rotate thread.
                    () -> {
                        while(!Thread.currentThread().isInterrupted()) {
                            synchronized (driveLock) {
                                while(swerveState != SwerveState.COMPLETE_ROTATE && swerveState != SwerveState.IDLE) {
                                    try {
                                        driveLock.wait();
                                    } catch(Exception e) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }

                                if(swerveState == SwerveState.COMPLETE_ROTATE) {
                                    driveLock.notifyAll();
                                    runnables[2].run();
                                    runnables[3].run();
                                    try {
                                        Thread.sleep(50);
                                    } catch(Exception e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    swerveState = SwerveState.IDLE;
                                    rotations = CompleteRotations.ROTATION_WAS_DONE;
                                    driveLock.notifyAll();
                                }
                            }


                            try {
                                Thread.sleep(25);
                            } catch(Exception e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                    , "CompleteRotateThread"),

            new Thread( //Reset wheel heading thread.
                    () -> {
                        while(!Thread.currentThread().isInterrupted()) {
                            synchronized (driveLock) {
                                while(swerveState != SwerveState.RESET_WHEEL && swerveState != SwerveState.IDLE) {
                                    try {
                                        driveLock.wait();
                                    } catch(Exception e) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }

                                if(swerveState == SwerveState.RESET_WHEEL) {
                                    driveLock.notifyAll();
                                    runnables[4].run();
                                    try {
                                        Thread.sleep(50);
                                    } catch(Exception e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    swerveState = SwerveState.IDLE;
                                    rotations = CompleteRotations.ROTATION_WAS_NOT_DONE;
                                    System.out.println("Wheels reset.");
                                    driveLock.notifyAll();
                                }
                            }

                            try{
                                Thread.sleep(25);
                            } catch(Exception e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                    , "ResetWheelHeadingThread"),

            new Thread(
                    () -> {
                        JFrame frame = new JFrame("Robotics JFrame");
                        frame.setSize(new Dimension(800, 800));
                        frame.setLayout(null);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
                        panel.setBounds(100, 100, 600, 600);
                        JButton[] buttons = {
                                new JButton(),
                                new JButton(),
                                new JButton(),
                                new JButton()
                        };

                        for(JButton button : buttons) {
                            panel.add(button);
                        }

                        frame.add(panel);
                        frame.setVisible(true);

                        while(!Thread.currentThread().isInterrupted()) {
                            int n = 0;
                            Thread.State[] states = threadStates();

                            for(int i = 0; i < states.length; i++) {
                                if(states[i] == Thread.State.BLOCKED) {
                                    n++;
                                    buttons[i].setBackground(Color.LIGHT_GRAY);
                                } else if(states[i] == Thread.State.TIMED_WAITING) {
                                    buttons[i].setBackground(Color.WHITE);
                                }
                            }

                            if(n >= 2) {
                                buttons[3].setBackground(Color.GREEN);
                            } else if(n == 1 && swerveState != SwerveState.IDLE) {
                                buttons[3].setBackground(Color.RED);
                            }


                            try {
                                Thread.sleep(20);
                            } catch(Exception e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                    , "GUIThread")
    };



    private Thread.State[] threadStates() {
        return new Thread.State[] {
                threads[0].getState(),
                threads[1].getState(),
                threads[2].getState()
        };
    }

    public CompletionThreadBasedSwerveDrive(Motor[] turningMotors, Motor[] drivingMotors, IMU imu, GamepadEx gamepadEx, boolean fieldOriented) {
        swerveDriveFields.turningMotors = turningMotors;
        swerveDriveFields.drivingMotors = drivingMotors;
        swerveDriveFields.imu = imu;
        swerveDriveFields.gamepadEx = gamepadEx;
        swerveDriveFields.fieldOriented = fieldOriented;

        for(Thread thread : threads) {
            thread.start();
        }
    }

    @Override
    public void setSwerveModuleState(boolean fieldOriented, double forwardPower, double sidePower, int headingInDegrees, double turningVector, boolean turningLeft) throws Exception {

        Thread.sleep(50);
        if(swerveState != SwerveState.IDLE) {
            return;
        }

        if(SwerveDriveFields.stopMotorsIsRunning) {
            stop();
            return;
        }

        int heading = (Math.abs(forwardPower) <= 0.01 && Math.abs(sidePower) <= 0.01) ? 0 :
                (int) Math.toDegrees(Math.atan2(forwardPower, sidePower)) - 90;

        if(fieldOriented) { //If the robot wants to drive field-oriented, drive field-oriented. Otherwise, not.
            applyFieldOrientedSwerve(heading, forwardPower, sidePower, headingInDegrees, turningVector, turningLeft);
        } else {
            applyRobotOrientedSwerve(heading, forwardPower, sidePower, turningVector, turningLeft);
        }
    }

    public void stopThreads() {
        for(Thread thread : threads) {
            thread.interrupt();
        }
    }

    public void applyFieldOrientedSwerve(int heading, double forwardPower, double sidePower, int headingInDegrees, double turningVector, boolean turningLeft) {

//        System.out.println(swerveState);

        if(Math.abs(turningVector) >= 0.05 && swerveState == SwerveState.IDLE) {
            runnables[2] = () -> completeRotate(turningLeft, headingInDegrees, turningVector, true);
            runnables[3] = () ->
                    setPowerForCompleteRotate(turningLeft, swerveDriveFields.headingsReversed, Math.abs(turningVector),
                            swerveDriveFields.individualTargetPositions, true, swerveDriveFields.headingsNegativeOrNot);
            swerveState = SwerveState.COMPLETE_ROTATE;
            return;
        } else if(rotations == CompleteRotations.ROTATION_WAS_DONE && swerveState == SwerveState.IDLE) {
            runnables[4] = this::resetWheelHeading;
            swerveState = SwerveState.RESET_WHEEL;
            return;
        }
        //Implement regular driving operations.

        runnables[0] = () -> {
            swerveDriveFields.forwardVector = Math.hypot(sidePower, forwardPower) / vectorScalar;
            swerveDriveFields.normalizedHeading = normalizeHeading(headingInDegrees, heading);

//            System.out.println("Target Angle: " + swerveDriveFields.normalizedHeading);

            for(int i = 0; i < swerveDriveFields.turningMotors.length; i++) {
//                System.out.println("Wheel heading for wheel " + swerveDriveFields.turningMotors[i].getMotorNumber() + ": " + swerveDriveFields.individualWheelHeadings[i]);

                int normalizedHeadingForWheel = normalizeHeading(swerveDriveFields.individualWheelHeadings[i], swerveDriveFields.normalizedHeading);
                int totalHeadingForWheel = swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;
                int reversedHeadingForWheel = calculateReverseHeading(totalHeadingForWheel,
                        swerveDriveFields.individualWheelHeadings[i]);

                int targetPosition = reversedHeadingForWheel != NO_REVERSAL ?  swerveDriveFields.individualWheelHeadings[i] +
                        reversedHeadingForWheel : swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;

                swerveDriveFields.individualWheelHeadings[i] = normalizeHeading(0, swerveDriveFields.individualWheelHeadings[i]);
                targetPosition = normalizeHeading(0, targetPosition);
                swerveDriveFields.individualTargetPositions[i] = targetPosition;

                swerveDriveFields.headingsNegativeOrNot[i] = targetPosition != Math.abs(targetPosition);

                swerveDriveFields.turningMotors[i].setTargetPosition(((double) targetPosition / 360) * 1440);
                swerveDriveFields.individualWheelHeadings[i] = reversedHeadingForWheel != NO_REVERSAL ? swerveDriveFields.individualWheelHeadings[i] +
                        reversedHeadingForWheel : swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;
            }
        };

        runnables[1] = () -> setPower(swerveDriveFields.headingsNegativeOrNot, swerveDriveFields.forwardVector);
        swerveState = SwerveState.DRIVE;
    }

    public int calculateReverseHeading(int totalHeading, int wheelHeading) {
        return Math.abs(totalHeading) > 180 ? normalizeHeading(wheelHeading,
                totalHeading < 0 ? totalHeading + 180 : totalHeading - 180) : NO_REVERSAL;
    }

    public void applyRobotOrientedSwerve(int heading, double forwardPower, double sidePower, double turningVector, boolean turningLeft) {
        if(Math.abs(turningVector) >= 0.05 && swerveState == SwerveState.IDLE) {
            runnables[2] = () -> completeRotate(turningLeft, 0, turningVector, false);
            runnables[3] = () ->
                    setPowerForCompleteRotate(turningLeft, swerveDriveFields.headingsReversed, turningVector,
                            swerveDriveFields.individualTargetPositions, false, swerveDriveFields.headingsNegativeOrNot);
            swerveState = SwerveState.COMPLETE_ROTATE;
            return;
        } else if(rotations == CompleteRotations.ROTATION_WAS_DONE && swerveState == SwerveState.IDLE) {
            runnables[4] = this::resetWheelHeading;
            swerveState = SwerveState.RESET_WHEEL;
            return;
        }

        //Implement regular driving operations.

        runnables[0] = () -> {
            swerveDriveFields.forwardVector = Math.hypot(sidePower, forwardPower) / vectorScalar;
            swerveDriveFields.normalizedHeading = normalizeHeading(0, heading);

            for (int i = 0; i < swerveDriveFields.turningMotors.length; i++) {
                int normalizedHeadingForWheel = normalizeHeading(swerveDriveFields.individualWheelHeadings[i], swerveDriveFields.normalizedHeading);
                int totalHeadingForWheel = swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;
                int reversedHeadingForWheel = calculateReverseHeading(totalHeadingForWheel,
                        swerveDriveFields.individualWheelHeadings[i]);

                int targetPosition = reversedHeadingForWheel != NO_REVERSAL ? swerveDriveFields.individualWheelHeadings[i] +
                        reversedHeadingForWheel : swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;

                swerveDriveFields.individualWheelHeadings[i] = normalizeHeading(0, swerveDriveFields.individualWheelHeadings[i]);
                targetPosition = normalizeHeading(0, targetPosition);
                swerveDriveFields.individualTargetPositions[i] = targetPosition;

                swerveDriveFields.headingsNegativeOrNot[i] = targetPosition != Math.abs(targetPosition);

                swerveDriveFields.turningMotors[i].setTargetPosition(((double) targetPosition / 360) * 1440);
                swerveDriveFields.individualWheelHeadings[i] = reversedHeadingForWheel != NO_REVERSAL ? swerveDriveFields.individualWheelHeadings[i] +
                        reversedHeadingForWheel : swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;
            }
        };

        runnables[1] = () -> setPower(swerveDriveFields.headingsNegativeOrNot, swerveDriveFields.forwardVector);
        swerveState = SwerveState.DRIVE;
    }

    @Override
    public void stopMotors() {
        for(Motor m : swerveDriveFields.turningMotors) {
            m.set(0);
        }

        for(Motor m : swerveDriveFields.drivingMotors) {
            m.set(0);
        }
    }

    @Override
    public void resetWheelHeading() {
        int botHeading = swerveDriveFields.imu.getOrientationInDegrees();

        for(int i = 0; i < swerveDriveFields.turningMotors.length; i++) {
            int normalizedHeadingForWheel = normalizeHeading(swerveDriveFields.individualWheelHeadings[i], botHeading);
            int totalHeading = swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;
            int reversedHeading = calculateReverseHeading(totalHeading, swerveDriveFields.individualWheelHeadings[i]);

            int targetPosition = reversedHeading != NO_REVERSAL ? swerveDriveFields.individualWheelHeadings[i] + reversedHeading :
                    swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;

            swerveDriveFields.individualWheelHeadings[i] = normalizeHeading(0, swerveDriveFields.individualWheelHeadings[i]);
            targetPosition = normalizeHeading(0, targetPosition);
            swerveDriveFields.individualTargetPositions[i] = targetPosition;
            swerveDriveFields.headingsReversed[i] = reversedHeading != NO_REVERSAL;
            swerveDriveFields.headingsNegativeOrNot[i] = targetPosition != Math.abs(targetPosition);

            swerveDriveFields.turningMotors[i].setTargetPosition(((double) targetPosition / 360) * 1440);
            swerveDriveFields.individualWheelHeadings[i] = reversedHeading != NO_REVERSAL ? swerveDriveFields.individualWheelHeadings[i] +
                    reversedHeading : swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;
        }

        for(int i = 0; i < swerveDriveFields.turningMotors.length; i++) {
            setPowerToIndividualWheel(swerveDriveFields.turningMotors[i].getMotorNumber() - 1, swerveDriveFields.headingsNegativeOrNot[i]);
        }

        Arrays.fill(swerveDriveFields.wheelsHaveRotated, false);
        swerveDriveFields.alreadyRotated = false;
    }

    @Override
    public void completeRotate(boolean turningLeft, int imuHeadingInDegrees, double turnVector, boolean fieldOriented) {
        System.out.println("Turn Vector: " + turnVector);

        if(swerveDriveFields.alreadyRotated && turningLeft == swerveDriveFields.previousTurningLeft) {
            System.out.println("Already rotated.");
            setPowerForCompleteRotate(swerveDriveFields.previousTurningLeft, swerveDriveFields.headingsReversed,
                    Math.abs(turnVector), new int[] {},
                    false, swerveDriveFields.headingsNegativeOrNot);
            return;
        }

        swerveDriveFields.previousTurningLeft = turningLeft;

        int headingFLBR = -45, headingFRBL = 45;

        for(int i = 0; i < swerveDriveFields.turningMotors.length; i++) {
            if(i == 0 || i == 3) {
//                System.out.println("Wheel heading for wheel " + swerveDriveFields.turningMotors[i].getMotorNumber() + ": " + swerveDriveFields.individualWheelHeadings[i]);

                int normalizedHeadingForWheel = normalizeHeading(swerveDriveFields.individualWheelHeadings[i], headingFLBR);
                int totalHeadingForWheel = swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;
                int reversedHeadingForWheel = calculateReverseHeading(totalHeadingForWheel, swerveDriveFields.individualWheelHeadings[i]);

                int targetPosition = reversedHeadingForWheel != NO_REVERSAL ? swerveDriveFields.individualWheelHeadings[i] + reversedHeadingForWheel :
                        swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;

                swerveDriveFields.individualWheelHeadings[i] = normalizeHeading(0, swerveDriveFields.individualWheelHeadings[i]);
                targetPosition = normalizeHeading(0, targetPosition);
                swerveDriveFields.individualTargetPositions[i] = targetPosition;
                swerveDriveFields.headingsReversed[i] = reversedHeadingForWheel != NO_REVERSAL;

                swerveDriveFields.turningMotors[i].setTargetPosition(((double) targetPosition / 360) * 1440);
                swerveDriveFields.individualWheelHeadings[i] = reversedHeadingForWheel != NO_REVERSAL ? swerveDriveFields.individualWheelHeadings[i] +
                        reversedHeadingForWheel : swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;
            } else {
//                System.out.println("Wheel heading for wheel " + swerveDriveFields.turningMotors[i].getMotorNumber() + ": " + swerveDriveFields.individualWheelHeadings[i]);
                int normalizedHeadingForWheel = normalizeHeading(swerveDriveFields.individualWheelHeadings[i], headingFRBL);
                int totalHeadingForWheel = swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;
                int reversedHeadingForWheel = calculateReverseHeading(totalHeadingForWheel, swerveDriveFields.individualWheelHeadings[i]);

                int targetPosition = reversedHeadingForWheel != NO_REVERSAL ? swerveDriveFields.individualWheelHeadings[i] + reversedHeadingForWheel :
                        swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;

                swerveDriveFields.individualWheelHeadings[i] = normalizeHeading(0, swerveDriveFields.individualWheelHeadings[i]);
                targetPosition = normalizeHeading(0, targetPosition);
                swerveDriveFields.individualTargetPositions[i] = targetPosition;
                swerveDriveFields.headingsReversed[i] = reversedHeadingForWheel != NO_REVERSAL;

                swerveDriveFields.turningMotors[i].setTargetPosition(((double) targetPosition / 360) * 1440);
                swerveDriveFields.individualWheelHeadings[i] = reversedHeadingForWheel != NO_REVERSAL ? swerveDriveFields.individualWheelHeadings[i] +
                        reversedHeadingForWheel : swerveDriveFields.individualWheelHeadings[i] + normalizedHeadingForWheel;
            }
        }
        System.out.println("Complete rotate done.");
    }

    @Override
    public void setPowerForCompleteRotate(boolean turningLeft, boolean[] headingsReversed, double turningVector, int[] targetPositions, boolean goToPosition, boolean[] headingDirectionsNegative) {
        if(goToPosition) {
            setPowerToIndividualWheel(0, headingDirectionsNegative[0]);
            setPowerToIndividualWheel(1, headingDirectionsNegative[1]);
            setPowerToIndividualWheel(2, headingDirectionsNegative[2]);
            setPowerToIndividualWheel(3, headingDirectionsNegative[3]);

            while(!allWheelsHaveRotatedToPosition()) {
                try {
                    Thread.sleep(10);
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

//        System.out.println("Heading reversed for motor 1: " + headingsReversed[0]);
//        System.out.println("Heading reversed for motor 2: " + headingsReversed[1]);
//        System.out.println("Heading reversed for motor 3: " + headingsReversed[2]);
//        System.out.println("Heading reversed for motor 4: " + headingsReversed[3]);
//
//        System.out.println("Turning left: " + turningLeft);

        swerveDriveFields.drivingMotors[0].set(headingsReversed[0] ? (turningLeft ? turningVector : -turningVector) : (turningLeft ? -turningVector : turningVector));
        swerveDriveFields.drivingMotors[1].set(headingsReversed[1] ? (turningLeft ? -turningVector : turningVector) : (turningLeft ? turningVector : -turningVector));
        swerveDriveFields.drivingMotors[2].set(headingsReversed[2] ? (turningLeft ? turningVector : -turningVector) : (turningLeft ? -turningVector : turningVector));
        swerveDriveFields.drivingMotors[3].set(headingsReversed[3] ? (turningLeft ? -turningVector : turningVector) : (turningLeft ? turningVector : -turningVector));

//        System.out.println("Drive motor 1 power: " + swerveDriveFields.drivingMotors[0].getMotorPower());
//        System.out.println("Drive motor 2 power: " + swerveDriveFields.drivingMotors[1].getMotorPower());
//        System.out.println("Drive motor 3 power: " + swerveDriveFields.drivingMotors[2].getMotorPower());
//        System.out.println("Drive motor 4 power: " + swerveDriveFields.drivingMotors[3].getMotorPower());
//
//        Arrays.fill(swerveDriveFields.wheelsHaveRotated, false);
//        swerveDriveFields.alreadyRotated = true;
//        System.out.println("Set power for complete rotate done.");
    }

    @Override
    public int normalizeHeading(int currentPosition, int targetPosition) {
        return (targetPosition - currentPosition + 540) % 360 - 180;
    }

    @Override
    public SwerveVector getRobotVector() {
        swerveDriveFields.gamepadEx.generateRandomGamepadExMovements();
        swerveDriveFields.imu.generateRandomIMUOrientation();

//        System.out.println("IMU orientation: " + swerveDriveFields.imu.getOrientationInDegrees());

        return new SwerveVector(swerveDriveFields.imu.getOrientationInDegrees(),
                swerveDriveFields.gamepadEx.getLeftY(), swerveDriveFields.gamepadEx.getLeftX(), swerveDriveFields.gamepadEx.getRightX(), swerveDriveFields.fieldOriented,
                swerveDriveFields.gamepadEx.getRightX() < 0);
    }

    public boolean allWheelsHaveRotatedToPosition() {
        return swerveDriveFields.wheelsHaveRotated[0]
                && swerveDriveFields.wheelsHaveRotated[1] &&
                swerveDriveFields.wheelsHaveRotated[2] &&
                swerveDriveFields.wheelsHaveRotated[3];
    }

    public void setPowerToIndividualWheel(int wheelNumber, boolean headingIsNegative) {
        swerveDriveFields.turningMotors[wheelNumber].set(headingIsNegative ? -1 : 1);
        while(!swerveDriveFields.turningMotors[wheelNumber].atTargetPosition()) {
            try {
                Thread.sleep(10);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        swerveDriveFields.turningMotors[wheelNumber].set(0);
        swerveDriveFields.wheelsHaveRotated[wheelNumber] = true;
    }

    @Override
    public void setPower(boolean[] headingIsNegative, double forwardVector) {
        setPowerToIndividualWheel(0, headingIsNegative[0]);
        setPowerToIndividualWheel(1, headingIsNegative[1]);
        setPowerToIndividualWheel(2, headingIsNegative[2]);
        setPowerToIndividualWheel(3, headingIsNegative[3]);

        while(!allWheelsHaveRotatedToPosition()) {
            try {
                Thread.sleep(10);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        for(int i = 0; i < swerveDriveFields.turningMotors.length; i++) {
            swerveDriveFields.drivingMotors[i].set(swerveDriveFields.headingsReversed[i] ? -forwardVector : forwardVector);
        }

        Arrays.fill(swerveDriveFields.wheelsHaveRotated, false);
//        System.out.println("Set power done.");
    }
}

class CompletionSwerveDriveCommand {
    public static void main(String[] args) throws Exception {

        Motor[] turningMotors = {
                new Motor(1),
                new Motor(2),
                new Motor(3),
                new Motor(4),
        },

        drivingMotors = {
                new Motor(5),
                new Motor(6),
                new Motor(7),
                new Motor(8)
        };

        CompletionThreadBasedSwerveDrive swerveDrive = new CompletionThreadBasedSwerveDrive(
            turningMotors, drivingMotors, new IMU(), new GamepadEx(), false
        );

        while(!Thread.currentThread().isInterrupted()) {
            swerveDrive.drive();
            try {
                Thread.sleep(10);
            } catch(Exception e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}


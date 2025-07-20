package MainFolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Scanner;

public class RoboticsGUI extends JPanel {

    static int firstWheelAngle = 0, secondWheelAngle = 0, thirdWheelAngle = 0, fourthWheelAngle = 0;
    static int firstWheelReversedHeading = 0, secondWheelReversedHeading = 0, thirdWheelReversedHeading = 0, fourthWheelReversedHeading = 0;
    static int firstWheelNormalizedHeading = 0, secondWheelNormalizedHeading = 0, thirdWheelNormalizedHeading = 0, fourthWheelNormalizedHeading = 0;
    static int firstWheelTotalHeading = 0, secondWheelTotalHeading = 0, thirdWheelTotalHeading = 0, fourthWheelTotalHeading=  0, targetAngle = 0;

    public static void setCurrentWheelAngles(int[] currentWheelAngles) {
        firstWheelAngle = currentWheelAngles[0];
        secondWheelAngle = currentWheelAngles[1];
        thirdWheelAngle = currentWheelAngles[2];
        fourthWheelAngle = currentWheelAngles[3];
    }

    public static void setReversedHeadings(int[] reversedHeadings) {
        firstWheelReversedHeading = reversedHeadings[0];
        secondWheelReversedHeading = reversedHeadings[1];
        thirdWheelReversedHeading = reversedHeadings[2];
        fourthWheelReversedHeading = reversedHeadings[3];
    }

    public static void setNormalizedHeadings(int[] normalizedHeadings) {
        firstWheelNormalizedHeading = normalizedHeadings[0];
        secondWheelNormalizedHeading = normalizedHeadings[1];
        thirdWheelNormalizedHeading = normalizedHeadings[2];
        fourthWheelNormalizedHeading = normalizedHeadings[3];
    }

    public static void setTotalHeadings(int[] totalHeadings) {
        firstWheelTotalHeading = totalHeadings[0];
        secondWheelTotalHeading = totalHeadings[1];
        thirdWheelTotalHeading = totalHeadings[2];
        fourthWheelTotalHeading = totalHeadings[3];
    }

    public static void setTargetAngle(int targetAngle) {
        RoboticsGUI.targetAngle = targetAngle;
    }

    public RoboticsGUI() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    System.out.println("Repainted.");
                    repaint();
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        int robotLength = 100, robotHeight = 200, screenHeight = getHeight(), screenWidth = getWidth();
        int firstCircleCenterX = (screenWidth / 5) / 2, secondCircleCenterX = ((screenWidth / 5) + robotLength) + firstCircleCenterX;
        int circleRadius = 70;

        int firstCircleAngleX = (int) (firstCircleCenterX - (circleRadius * Math.cos(Math.toRadians(firstWheelAngle - 90)))), firstCircleAngleY = (int) ((screenHeight / 10) + circleRadius + (circleRadius * Math.sin(Math.toRadians(firstWheelAngle - 90))));
        int secondCircleAngleX = (int) (secondCircleCenterX - (circleRadius * Math.cos(Math.toRadians(secondWheelAngle - 90)))), secondCircleAngleY = (int) ((screenHeight / 10) + circleRadius + (circleRadius * Math.sin(Math.toRadians(secondWheelAngle - 90))));
        int thirdCircleAngleX = (int) (firstCircleCenterX - (circleRadius * Math.cos(Math.toRadians(thirdWheelAngle - 90)))), thirdCircleAngleY = (int) ((screenHeight / 10 + 200) + circleRadius + (circleRadius * Math.sin(Math.toRadians(thirdWheelAngle - 90))));
        int fourthCircleAngleX = (int) (secondCircleCenterX - (circleRadius * Math.cos(Math.toRadians(fourthWheelAngle - 90)))), fourthCircleAngleY = (int) ((screenHeight / 10 + 200) + circleRadius + (circleRadius * Math.sin(Math.toRadians(fourthWheelAngle - 90))));

        g2d.fillRect(screenWidth / 5, screenHeight / 5, robotLength, robotHeight);
        g2d.drawOval(firstCircleCenterX - circleRadius, screenHeight / 10, circleRadius * 2, circleRadius * 2);
        g2d.drawOval(secondCircleCenterX - circleRadius, screenHeight / 10, circleRadius * 2, circleRadius * 2);
        g2d.drawOval(firstCircleCenterX - circleRadius, (screenHeight / 10) + 200, circleRadius * 2, circleRadius * 2);
        g2d.drawOval(secondCircleCenterX - circleRadius, (screenHeight / 10) + 200, circleRadius * 2, circleRadius * 2);

        g2d.setFont(new Font("Fira Code", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Robot", ((screenWidth / 5) + robotLength) - 80, ((screenHeight/ 4) + robotHeight) - 180);

        g2d.setColor(Color.RED);
        g2d.drawLine(firstCircleCenterX, (screenHeight / 10) + circleRadius, firstCircleAngleX, firstCircleAngleY);
        g2d.drawLine(secondCircleCenterX, (screenHeight / 10) + circleRadius, secondCircleAngleX, secondCircleAngleY);
        g2d.drawLine(firstCircleCenterX, (screenHeight / 10) + 200 + circleRadius, thirdCircleAngleX, thirdCircleAngleY);
        g2d.drawLine(secondCircleCenterX, (screenHeight / 10) + 200 + circleRadius, fourthCircleAngleX, fourthCircleAngleY);

        g2d.setFont(new Font("Fira Code", Font.BOLD, 15));
        g2d.setColor(Color.BLACK);
        g2d.drawString(STR."Motor 1 Current Heading: \{firstWheelAngle}", 700, 100);
        g2d.drawString(STR."Motor 2 Current Heading: \{secondWheelAngle}", 700, 130);
        g2d.drawString(STR."Motor 3 Current Heading: \{thirdWheelAngle}", 700, 160);
        g2d.drawString(STR."Motor 4 Current Heading: \{fourthWheelAngle}", 700, 190);

        g2d.drawString(STR."Motor 1 Reversed Heading: \{firstWheelReversedHeading}", 700, 250);
        g2d.drawString(STR."Motor 2 Reversed Heading: \{secondWheelReversedHeading}", 700, 280);
        g2d.drawString(STR."Motor 3 Reversed Heading: \{thirdWheelReversedHeading}", 700, 310);
        g2d.drawString(STR."Motor 4 Reversed Heading: \{fourthWheelReversedHeading}", 700, 340);

        g2d.drawString(STR."Motor 1 Normalized Heading: \{firstWheelNormalizedHeading}", 1000, 100);
        g2d.drawString(STR."Motor 2 Normalized Heading: \{secondWheelNormalizedHeading}", 1000, 130);
        g2d.drawString(STR."Motor 3 Normaized Heading: \{thirdWheelNormalizedHeading}", 1000, 160);
        g2d.drawString(STR."Motor 4 Normalized Heading: \{fourthWheelNormalizedHeading}", 1000, 190);

        g2d.drawString(STR."Motor 1 Total Heading: \{firstWheelTotalHeading}", 1000, 250);
        g2d.drawString(STR."Motor 2 Total Heading: \{secondWheelTotalHeading}", 1000, 280);
        g2d.drawString(STR."Motor 3 Total Heading: \{thirdWheelTotalHeading}", 1000, 310);
        g2d.drawString(STR."Motor 4 Total Heading: \{fourthWheelTotalHeading}", 1000, 340);
        g2d.drawString(STR."Target Angle: \{targetAngle}", 1000, 400);
    }

}

class Swerve {
    public static void main(String[] args) {
       GamepadEx gamepadEx = new GamepadEx();
       IMU imu = new IMU();
       Scanner sc = new Scanner(System.in);
       Motor[] turningMotors = new Motor[] {
             new Motor(0),
             new Motor(1),
             new Motor(2),
             new Motor(3)
       };
       int[] reversedHeads = {0, 0, 0, 0}, normalizedHeads = {0, 0, 0, 0},
        totalHeads = {0, 0, 0, 0};


        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1000, 1000));
        frame.add(new RoboticsGUI());
        frame.setVisible(true);

       while(!Thread.currentThread().isInterrupted()) {
           System.out.println("Enter drive action.");
           String action = sc.nextLine();

           switch(action) {
               case "drive":
                   System.out.println("Enter x value.");
                   double x = sc.nextDouble();
                   sc.nextLine();

                   System.out.println("Enter y value.");
                   double y = sc.nextDouble();
                   sc.nextLine();

                   System.out.println("Enter IMU orientation.");
                   int orientation = sc.nextInt();
                   sc.nextLine();

                   gamepadEx.setLeftX(x);
                   gamepadEx.setLeftY(y);
                   imu.setOrientationInDegrees(orientation);

                   int targetHeading = (int) Math.round(Math.toDegrees(Math.atan2(gamepadEx.getLeftY(), gamepadEx.getLeftX()))) - 90;
                   RoboticsGUI.setTargetAngle(targetHeading);
                   int normalizedHeadingRelativeToRobot = gamepadEx.normalizedHeading(imu.getOrientationInDegrees(), targetHeading);

                   for(Motor m : turningMotors) {
                       int normalizedHeadingForWheel = gamepadEx.normalizedHeading(m.getPreviousHeading(), normalizedHeadingRelativeToRobot);
                       int totalHeadingForWheel = m.getPreviousHeading() + normalizedHeadingForWheel;
                       int reversedHeading = gamepadEx.calculateReversedHeadingForWheel(m.getPreviousHeading(), totalHeadingForWheel);
                       reversedHeads[m.getMotorNumber()] = reversedHeading;
                       normalizedHeads[m.getMotorNumber()] = normalizedHeadingForWheel;
                       totalHeads[m.getMotorNumber()] = totalHeadingForWheel;

                       boolean headingReversed = reversedHeading != 2000;
                       int targetPosition = headingReversed ? m.getPreviousHeading() + reversedHeading : m.getPreviousHeading() + normalizedHeadingForWheel;
                       targetPosition = gamepadEx.normalizedHeading(0, targetPosition);
                       m.setTargetPosition(targetPosition);
                       m.setPreviousHeading(targetPosition);
                   }

                   int[] currentWheelHeadings = new int[4];
                   for(int i = 0; i < currentWheelHeadings.length; i++) {
                       currentWheelHeadings[i] = turningMotors[i].getPreviousHeading();
                   }
                   RoboticsGUI.setCurrentWheelAngles(currentWheelHeadings);
                   RoboticsGUI.setReversedHeadings(reversedHeads);
                   RoboticsGUI.setNormalizedHeadings(normalizedHeads);
                   RoboticsGUI.setTotalHeadings(totalHeads);
                   break;

               case "rotation":
                   sc.nextLine();
                   System.out.println("Enter right x value.");
                   int rightX = sc.nextInt();
                   sc.nextLine();
                   gamepadEx.setRightX(rightX);

                   int targetHead;

                   for(Motor m : turningMotors) {
                       if(m.getMotorNumber() == 0 || m.getMotorNumber() == 3) {
                           targetHead = -45;
                       } else {
                           targetHead = 45;
                       }

                       int normalizedHeadingForWheel = gamepadEx.normalizedHeading(m.getPreviousHeading(), targetHead);
                       int totalHeadingForWheel = m.getPreviousHeading() + normalizedHeadingForWheel;
                       int reversedHeadingForWheel = gamepadEx.calculateReversedHeadingForWheel(m.getPreviousHeading(), totalHeadingForWheel);
                       reversedHeads[m.getMotorNumber()] = reversedHeadingForWheel;
                       normalizedHeads[m.getMotorNumber()] = normalizedHeadingForWheel;
                       totalHeads[m.getMotorNumber()] = totalHeadingForWheel;
                       boolean headingReversed = reversedHeadingForWheel != 2000;

                       int targetPosition = headingReversed ? m.getPreviousHeading() + reversedHeadingForWheel : m.getPreviousHeading() + normalizedHeadingForWheel;
                       targetPosition = gamepadEx.normalizedHeading(0, targetPosition);
                       m.setTargetPosition(targetPosition);
                       m.setPreviousHeading(targetPosition);
                   }

                   int[] currentWheelHeads = new int[4];
                   for(int i = 0; i < currentWheelHeads.length; i++) {
                       currentWheelHeads[i] = turningMotors[i].getPreviousHeading();
                   }
                   RoboticsGUI.setCurrentWheelAngles(currentWheelHeads);
                   RoboticsGUI.setReversedHeadings(reversedHeads);
                   RoboticsGUI.setNormalizedHeadings(normalizedHeads);
                   RoboticsGUI.setTotalHeadings(totalHeads);
                   break;

               case "reset":
                   break;
           }
       }

       sc.close();
    }
}
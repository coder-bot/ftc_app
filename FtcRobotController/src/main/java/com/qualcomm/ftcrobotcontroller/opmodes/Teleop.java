/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class Teleop extends OpMode {

	//motor declarations
	DcMotor leftDrive;
	DcMotor rightDrive;

	DcMotor winch;
	DcMotor armOne;
	DcMotor armTwo;

	//motor power variables
	double throttleRight, throttleLeft;
	double armOneThrottle, armTwoThrottle;

	//servo declarations
	Servo armThree;
	Servo armFour;
	Servo wingCatchRight;
	Servo wingCatchLeft;

// --Commented out by Inspection START (10/31/2015 2:27 PM):
//	/**
//	 * Constructor
//	 */
//	public Teleop() {
//
//	}
// --Commented out by Inspection STOP (10/31/2015 2:27 PM)

	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {
		/*
		 * Use the hardwareMap to get the dc motors and servos by name. Note
		 * that the names of the devices must match the names used when you
		 * configured your robot and created the configuration file.
		 */

		//motor hardware map assignments
		rightDrive = hardwareMap.dcMotor.get("motor_1");
		leftDrive = hardwareMap.dcMotor.get("motor_2");
		winch = hardwareMap.dcMotor.get("motor_3");
		rightDrive.setDirection(DcMotor.Direction.REVERSE);

		//servo hardware map assignments
		armThree = hardwareMap.servo.get("servo_1");
		armFour = hardwareMap.servo.get("servo_2");
		wingCatchLeft = hardwareMap.servo.get("servo_3");
		wingCatchRight = hardwareMap.servo.get("servo_4");

		//Initial servo position values
		armThree.setPosition(0.5);
		armFour.setPosition(0.5);
		wingCatchLeft.setPosition(0);
		wingCatchRight.setPosition(0);
	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {
		// left_stick_y ranges from -1 to 1, where -1 is full up, and
		// 1 is full down
		// direction: left_stick_x ranges from -1 to 1, where -1 is full left
		// and 1 is full right

		//setting motor power levels
		throttleLeft = -gamepad1.left_stick_y;
		throttleRight = -gamepad1.right_stick_y;

		// clip the right/left values so that the values never exceed +/- 1
		throttleLeft = Range.clip(throttleLeft, -1, 1);
		throttleRight = Range.clip(throttleRight, -1, 1);

		// scale the joystick value to make it easier to control
		// the robot more precisely at slower speeds.
		throttleLeft =  (float)scaleInput(throttleLeft);
		throttleRight = (float)scaleInput(throttleRight);

		// write the values to the motors
		leftDrive.setPower(throttleLeft);
		rightDrive.setPower(throttleRight);

		//control of winch motor
		if (gamepad1.dpad_down) winch.setPower(-1);
		else if (gamepad1.dpad_up) winch.setPower(1);
		else winch.setPower(0);

		//control of wing-mounted catches for releasing climber
		if (gamepad2.x) wingCatchRight.setPosition(0.5);
		else if (gamepad2.a) wingCatchRight.setPosition(0);

		if (gamepad2.dpad_left) wingCatchLeft.setPosition(0.5);
		if (gamepad2.dpad_right) wingCatchLeft.setPosition(0);

		//motor control of primary arm components
		armOneThrottle = -gamepad2.left_stick_y;
		armOneThrottle = gamepad2.left_stick_y;

		armOneThrottle = Range.clip(armOneThrottle, -1, 1);
		armTwoThrottle = Range.clip(armTwoThrottle, -1, 1);

		armOneThrottle = (float)scaleInput(armOneThrottle);
		armTwoThrottle = (float)scaleInput(armTwoThrottle);

		armOne.setPower(armOneThrottle);
		armTwo.setPower(armTwoThrottle);

		//servo control of secondary arm components
		if (gamepad2.left_stick_x >= 0.9) armThree.setPosition(0);
		else if (gamepad2.left_stick_x <= -0.9) armThree.setPosition(1);
		else armThree.setPosition(0.5);

		if (gamepad2.right_stick_x >= 0.9) armFour.setPosition(0);
		else if (gamepad2.right_stick_x <= -0.9) armFour.setPosition(1);
		else armFour.setPosition(0.5);

		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */
		telemetry.addData("Text", "(We are not currently reporting robot data.)");

	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {
	}

	/*
	 * This method scales the joystick input so for low joystick values, the 
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
	double scaleInput(double dVal)  {
		double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
				0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

		// get the corresponding index for the scaleInput array.
		int index = (int) (dVal * 16.0);

		// index should be positive.
		if (index < 0) {
			index = -index;
		}

		// index cannot exceed size of array minus 1.
		if (index > 16) {
			index = 16;
		}

		// get value from the array.
		double dScale;
		if (dVal < 0) {
			dScale = -scaleArray[index];
		} else {
			dScale = scaleArray[index];
		}

		// return scaled value.
		return dScale;
	}
}

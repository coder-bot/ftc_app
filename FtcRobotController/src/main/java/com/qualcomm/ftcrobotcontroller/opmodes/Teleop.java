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
	DcMotor leftDrive, rightDrive;
	DcMotor winchExtension, winchPivot;

	//servo declarations
	Servo leftPermaHook, rightPermaHook, leftWing, rightWing;

	//motor power variables
	double rightThrottle, leftThrottle;

    //servo control variables
    double leftWingPosition = 0;
    double rightWingPosition = 1;
    double servoDelta = 0.005;

	//constructor
	public Teleop() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {

		//motor hardware map assignments
		leftDrive = hardwareMap.dcMotor.get("motor_2");
		rightDrive = hardwareMap.dcMotor.get("motor_1");
		winchExtension = hardwareMap.dcMotor.get("motor_4");
		winchPivot = hardwareMap.dcMotor.get("motor_3");

		rightDrive.setDirection(DcMotor.Direction.REVERSE);

		//servo hardware map assignments
		leftPermaHook = hardwareMap.servo.get("s1");
        rightPermaHook = hardwareMap.servo.get("s2");
        leftWing = hardwareMap.servo.get("lw");
        rightWing = hardwareMap.servo.get("rw");

		//Initial servo position values
		leftPermaHook.setPosition(0.5);
		rightPermaHook.setPosition(0.5);
        leftWing.setPosition(leftWingPosition);
        rightWing.setPosition(rightWingPosition);
	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		if (gamepad2.dpad_right) {
			winchExtension.setPower(0.2);
			leftThrottle = (float)scaleInput(-gamepad1.left_stick_y);
			rightThrottle = (float)scaleInput(-gamepad1.right_stick_y);
			}
		else if (gamepad2.dpad_left) {
			if (!gamepad2.left_bumper) {
				//these statements are run when left_bumper is false
				winchExtension.setPower(-0.2);
				leftThrottle = 0;
				rightThrottle = 0;
			}
			else {
				//these statements are run when left_bumper is true
				winchExtension.setPower(-1);
				leftThrottle = 1;
				rightThrottle = 1;
			}
		}
		else {
			winchExtension.setPower(0);
			leftThrottle = (float)scaleInput(-gamepad1.left_stick_y);
			rightThrottle = (float)scaleInput(-gamepad1.right_stick_y);
			}

		leftDrive.setPower(leftThrottle);
		rightDrive.setPower(rightThrottle);

		//experiment with custom exception handling upon motor assignments outside of [-1, 1] bounds to avoid system crash

		if (gamepad2.dpad_down) winchPivot.setPower(0.15);
		else if (gamepad2.dpad_up) winchPivot.setPower(-0.15);
		else winchPivot.setPower(0);

		//control of servos

		if (gamepad2.a) leftPermaHook.setPosition(0);
		else if (gamepad2.y) leftPermaHook.setPosition(1);
		else leftPermaHook.setPosition(0.5);

		if (gamepad2.b) rightPermaHook.setPosition(0);
		else if (gamepad2.x) rightPermaHook.setPosition(1);
		else rightPermaHook.setPosition(0.5);

        if (gamepad1.dpad_left) leftWingPosition += servoDelta;
        else if (gamepad1.dpad_right) leftWingPosition -= servoDelta;

        if (gamepad1.x) rightWingPosition += servoDelta;
        else if (gamepad1.b) rightWingPosition -= servoDelta;

        leftWingPosition = Range.clip(leftWingPosition, 0, 1);
        rightWingPosition = Range.clip(rightWingPosition, 0, 1);

        leftWing.setPosition(leftWingPosition);
        rightWing.setPosition(rightWingPosition);

		//telemetry data to be sent back to the driver station
		telemetry.addData("Robot Status", "This is Bionicus, programmed by Max. No other data to report at this time.");

	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {
	}

	//this is the method used to scale the joystick values for easier driving at lower speeds.
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

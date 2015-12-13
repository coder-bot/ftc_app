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
	//Servo wingCatchRight, wingCatchLeft;

	//motor power variables
	double throttleRight, throttleLeft;

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
		rightDrive = hardwareMap.dcMotor.get("motor_1");
		leftDrive = hardwareMap.dcMotor.get("motor_2");
		winchExtension = hardwareMap.dcMotor.get("motor_3");
		winchPivot = hardwareMap.dcMotor.get("motor_4");
		rightDrive.setDirection(DcMotor.Direction.REVERSE);

		//servo hardware map assignments
		//wingCatchLeft = hardwareMap.servo.get("servo_3");
		//wingCatchRight = hardwareMap.servo.get("servo_4");

		//Initial servo position values
		//wingCatchLeft.setPosition(0);
		//wingCatchRight.setPosition(1);
	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		//first, we read the joystick values and assign them to variables
		throttleLeft = gamepad1.left_stick_y;
		throttleRight = gamepad1.right_stick_y;

		//next, we clip the right and left values so that they remain in the interval [-1,1]
		throttleLeft = Range.clip(throttleLeft, -1, 1);
		throttleRight = Range.clip(throttleRight, -1, 1);

		//then, we scale the values for easier driving at low speeds
		throttleLeft =  (float)scaleInput(throttleLeft);
		throttleRight = (float)scaleInput(throttleRight);

		//finally, we write the values to the motors
		leftDrive.setPower(throttleLeft);
		rightDrive.setPower(throttleRight);

		//winch motor controls
		if (gamepad1.dpad_right) winchExtension.setPower(-0.2);
		else if (gamepad1.dpad_left & !gamepad1.left_bumper) winchExtension.setPower(0.2);
		else if (gamepad1.dpad_left & gamepad1.left_bumper) winchExtension.setPower(1);
		else winchExtension.setPower(0);

		if (gamepad1.dpad_down) winchPivot.setPower(0.15);
		else if (gamepad1.dpad_up) winchPivot.setPower(-0.15);
		else winchPivot.setPower(0);

		//control of wing-mounted catches for releasing climbers

		//if (gamepad2.dpad_left) wingCatchLeft.setPosition(0.5);
		//if (gamepad2.dpad_right) wingCatchLeft.setPosition(0);

		//if (gamepad2.b) wingCatchRight.setPosition(0.5);
		//else if (gamepad2.x) wingCatchRight.setPosition(1);

		//telemetry data to be sent back to the driver station
		telemetry.addData("Text", "This is Bionicus, programmed by Max. No other data to report at this time");

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

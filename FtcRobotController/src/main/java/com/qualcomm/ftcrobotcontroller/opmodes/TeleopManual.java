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

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class TeleopManual extends OpMode {

	//motor declarations
	DcMotor leftDrive, rightDrive;
	DcMotor winchExtension, winchPivot;

	//servo declarations
	Servo rightWing, leftWing, permaHook;

	//motor power variables
	double rightThrottle, leftThrottle;

    //drive motor regulation variables
    boolean isPrimed = false;
    double primingStatus = 0;
    double primingStartupDelta = 0.2;
    double primingCooldownDelta = 0.1;

	//constructor
	public TeleopManual() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 *
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {

		//motor hardware map assignments
		leftDrive = hardwareMap.dcMotor.get("motor_1");
		rightDrive = hardwareMap.dcMotor.get("motor_2");
		winchExtension = hardwareMap.dcMotor.get("motor_3");
		winchPivot = hardwareMap.dcMotor.get("motor_4");
		leftDrive.setDirection(DcMotor.Direction.REVERSE);

		//servo hardware map assignments
		leftWing = hardwareMap.servo.get("s1");
		rightWing = hardwareMap.servo.get("s2");
		permaHook = hardwareMap.servo.get("s3");

		//Initial servo position values
		leftWing.setPosition(1);
		rightWing.setPosition(0);
		permaHook.setPosition(0.5);
	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		if (gamepad2.dpad_left) {
			if (!gamepad2.left_bumper) winchExtension.setPower(0.2);
			else {
				//these statements are run when left_bumper is true
				winchExtension.setPower(1);
				leftThrottle = -1;
				rightThrottle = -1;
			}
		}
		else if (gamepad2.dpad_right) {
			winchExtension.setPower(-0.2);
			leftThrottle = (float)scaleInput(gamepad2.left_stick_y);
			rightThrottle = (float)scaleInput(gamepad2.right_stick_y);
			}
		else {
			winchExtension.setPower(0);
			leftThrottle = (float)scaleInput(gamepad2.left_stick_y);
			rightThrottle = (float)scaleInput(gamepad2.right_stick_y);
			}

        if (isPrimed) {
            leftDrive.setPower(leftThrottle);
            rightDrive.setPower(rightThrottle);
        }

		if (gamepad2.dpad_down) winchPivot.setPower(0.15);
		else if (gamepad2.dpad_up) winchPivot.setPower(-0.15);
		else winchPivot.setPower(0);

		//control of servos

		if (gamepad2.right_trigger > 0.3) leftWing.setPosition(0.5);
		if (gamepad2.right_bumper) leftWing.setPosition(1);

		if (gamepad2.y) rightWing.setPosition(0.5);
		else if (gamepad2.a) rightWing.setPosition(0);

		if (gamepad2.b) permaHook.setPosition(0);
		else if (gamepad2.x) permaHook.setPosition(1);
		else permaHook.setPosition(0.5);

        //priming implementation
        if (!isPrimed) {
            if (gamepad1.left_bumper & gamepad2.right_bumper) primingStatus += primingStartupDelta;
            else if (primingStatus > 0) primingStatus -= primingCooldownDelta;
        }

        if (primingStatus == 100) isPrimed = true;

		//telemetry data to be sent back to the driver station
        if (!isPrimed) {
            telemetry.addData("Priming Status", primingStatus + "%");
            telemetry.addData("Alert", "Engines are not primed!");
        }
        else {
            //implies isPrimed is true
            telemetry.addData("Priming Status", primingStatus + "%");
            telemetry.addData("No alerts to show", "Engines are primed!");
            telemetry.addData("Robot Status", "This is Bionicus, programmed by Max. No other data to report at this time.");
        }

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

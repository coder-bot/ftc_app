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
public class SARM_Start_Automated_Robot_Matrix extends OpMode {

	//motor declarations
	DcMotor leftDrive, rightDrive;
	DcMotor winchExtension, winchPivot;

	//servo declarations
	Servo rightWing, climberCarrier, permaHook;

	//boolean value for checking initial motor assignment status
    boolean motorsConfigured, climbersDeposited;

    //servo and motor assignment variables
    double driveMotorPower = 1;
    double carrierReleasePosition

	//constructor
	public SARM_Start_Automated_Robot_Matrix() {

	}

    public void configureMotors() {

    }

    public void depositClimbers() {
        climberCarrier.setPosition(0.5);
    }

    public void allStop() {
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }

	@Override
	public void init() {

		//motor hardware map assignments
		leftDrive = hardwareMap.dcMotor.get("motor_1");
		rightDrive = hardwareMap.dcMotor.get("motor_2");
		winchExtension = hardwareMap.dcMotor.get("motor_3");
		winchPivot = hardwareMap.dcMotor.get("motor_4");

		rightDrive.setDirection(DcMotor.Direction.REVERSE);

		//servo hardware map assignments
		climberCarrier = hardwareMap.servo.get("s1");
		rightWing = hardwareMap.servo.get("s2");
		permaHook = hardwareMap.servo.get("s3");

		//Initial servo position values
		climberCarrier.setPosition(1);
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

		//telemetry data to be sent back to the driver station
		telemetry.addData("Robot Status", "Running autonomous...");

        if (getRuntime() < 3) {
            if (!motorsConfigured) {
                leftDrive.setPower(1);
                rightDrive.setPower(1);
                motorsConfigured = true;
            }
            else if (motorsConfigured) {
                //don't update motor power values if they've already been configured
            }
        }
        else if (getRuntime() > 3) {
            if (!climbersDeposited) {
                allStop();
                //depositClimbers();
            }
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
}

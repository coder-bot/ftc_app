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
	Servo permaHook, climberCarrier;

    //servo and motor assignment variables
    double driveMotorPower = 1;
    double carrierStandbyPosition = 1;
    double carrierDepositPosition = 0.4;

	//constructor
	public SARM_Start_Automated_Robot_Matrix() {

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
        permaHook = hardwareMap.servo.get("s1");
		climberCarrier = hardwareMap.servo.get("s2");

        //initial servo values
        permaHook.setPosition(0.5);
        climberCarrier.setPosition(1);
	}

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

        telemetry.addData("Robot Status", "10 second waiting period. Please hold...");

        while(getRuntime() < 10) {
            //10 second delay
        }
        telemetry.addData("Updated Status", "Wait complete! Running the SARM...");
        leftDrive.setPower(driveMotorPower);
        rightDrive.setPower(driveMotorPower);
        double driveStartTime = getRuntime();

        while ((getRuntime() - driveStartTime) < 3) {
            //wait while 3 seconds pass
        }

        allStop();
        climberCarrier.setPosition(carrierDepositPosition);
        double initialTime = getRuntime();
        while (getRuntime() - initialTime < 1) {
            //wait while the servo moves
        }
        climberCarrier.setPosition(carrierStandbyPosition);
        while (true) {
        //wait for end of autonomous
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

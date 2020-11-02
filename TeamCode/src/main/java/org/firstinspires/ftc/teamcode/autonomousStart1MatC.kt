package org.firstinspires.ftc.teamcode

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumConstraints

object autonomousStart1MatC {
    private val driveConstraints = DriveConstraints(60.0, 60.0, 0.0, 270.0.toRadians, 270.0.toRadians, 0.0)

    // Remember to set your track width to an estimate of your actual bot to get accurate trajectory profile duration!
    private const val trackWidth = 16.0

    private val combinedConstraints = MecanumConstraints(driveConstraints, trackWidth)

    private val startPose = Pose2d(-62.0, -50.0, 0.0.toRadians)

    fun createTrajectory(): ArrayList<Trajectory> {
        val list = ArrayList<Trajectory>()

        val builder1 = TrajectoryBuilder(startPose, startPose.heading, combinedConstraints)
        val builder2 = TrajectoryBuilder(Pose2d(54.0, -60.0, 0.0), 0.0, combinedConstraints)
        val builder3 = TrajectoryBuilder(Pose2d(-20.0, -36.0, -12.88), 0.0, combinedConstraints)
        val builder4 = TrajectoryBuilder(Pose2d(-60.0, -25.0, 0.0), 0.0, combinedConstraints)
        val builder5 = TrajectoryBuilder(Pose2d(-23.0, -36.0, 12.88), 0.0, combinedConstraints)
        val builder6 = TrajectoryBuilder(Pose2d(54.0, -60.0, 0.0), 0.0, combinedConstraints)

        builder1
                //go to box c
                .splineTo(Vector2d(52.0, -60.0), 0.0)
        builder2
                //back to second wobble
                .lineToSplineHeading(Pose2d(-20.0, -36.0, -12.88));
        builder3
                .lineToSplineHeading(Pose2d(-60.0, -25.0, 0.0));
        builder4
                //go to shooter location
                .splineTo(Vector2d(-23.0, -36.0), 12.88);
        builder5
                //back to box c
                .lineToSplineHeading(Pose2d(54.0, -60.0, 0.0));
        //line
        builder6
                .splineToConstantHeading(Vector2d(10.0, -60.0), 0.0);

        list.add(builder1.build())
        list.add(builder2.build())
        list.add(builder3.build())
        list.add(builder4.build())
        list.add(builder5.build())
        list.add(builder6.build())

        return list
    }
}

val Double.toRadians get() = (Math.toRadians(this))

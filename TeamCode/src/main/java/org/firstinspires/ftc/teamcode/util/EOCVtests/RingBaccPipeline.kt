package org.firstinspires.ftc.teamcode.util.EOCVtests

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline

class RingBaccPipeline (
        private val telemetry: Telemetry? = null,
        var debug: Boolean = false,
): OpenCvPipeline() {
    /** variables that will be reused for calculations **/
    private var mat: Mat
    private var ret: Mat

    /** companion object to store all static variables needed **/
    companion object Config {
        /** values used for inRange calculation
         * set to var in-case user wants to use their own tuned values
         * stored in YCrCb format **/
        var lowerOrange = Scalar(0.0, 141.0, 0.0)
        var upperOrange = Scalar(255.0, 230.0, 95.0)

        /** width of the camera in use, defaulted to 320 as that is most common in examples **/
        var CAMERA_WIDTH = 320

        /** Horizon value in use, anything above this value (less than the value) since
         * (0, 0) is the top left of the camera frame **/
        var HORIZON: Int = ((100.0 / 320.0) * CAMERA_WIDTH).toInt()

        /** algorithmically calculated minimum width for width check based on camera width **/
        val MIN_WIDTH
            get() = (50.0 / 320.0) * CAMERA_WIDTH

        /** if the calculated aspect ratio is greater then this, height is 4, otherwise its 1 **/
        const val BOUND_RATIO = 0.7
    }

    /**
     * default init call, body of constructors
     */
    init {
        ret = Mat()
        mat = Mat()
    }


    override fun processFrame(input: Mat?): Mat {
        ret.release() // releasing mat to release backing buffer
        // must release at the start of function since this is the variable being returned

        ret = Mat() // resetting pointer held in ret
        try { // try catch in order for opMode to not crash and force a restart
            /**converting from RGB color space to YCrCb color space**/
            Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2YCrCb)

            /**checking if any pixel is within the orange bounds to make a black and white mask**/
            val mask = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1) // variable to store mask in
            Core.inRange(mat, lowerOrange, upperOrange, mask)

            /**applying to input and putting it on ret in black or yellow**/
            Core.bitwise_and(input, input, ret, mask)

            /**applying GaussianBlur to reduce noise when finding contours**/
            Imgproc.GaussianBlur(mask, mask, Size(5.0, 15.0), 0.00)

            /**finding contours on mask**/
            val contours: List<MatOfPoint> = ArrayList()
            val hierarchy = Mat()
            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE)

            /**drawing contours to ret in green**/
            Imgproc.drawContours(ret, contours, -1, Scalar(0.0, 255.0, 0.0), 3)

            /**finding widths of each contour, comparing, and storing the widest**/
            var maxWidth = 0
            var maxRect = Rect()
            for (c: MatOfPoint in contours) {
                val copy = MatOfPoint2f(*c.toArray())
                val rect: Rect = Imgproc.boundingRect(copy)

                val w = rect.width
                // checking if the rectangle is below the horizon
                if (w > maxWidth && rect.y + rect.height > HORIZON) {
                    maxWidth = w
                    maxRect = rect
                }

                c.release() // releasing the buffer of the contour, since after use, it is no longer needed
                copy.release() // releasing the buffer of the copy of the contour, since after use, it is no longer needed
            }

            /**drawing widest bounding rectangle to ret in blue**/
            Imgproc.rectangle(ret, maxRect, Scalar(0.0, 0.0, 255.0), 2)

            /** drawing a red line to show the horizon (any above the horizon is not checked to be a ring stack **/
            Imgproc.line(
                    ret,
                    Point(
                            .0,
                            HORIZON.toDouble()
                    ),
                    Point(
                            CAMERA_WIDTH.toDouble(),
                            HORIZON.toDouble()
                    ),
                    Scalar(
                            255.0,
                            .0,
                            255.0)
            )

            if (debug) telemetry?.addData("Vision: maxW", maxWidth)
            // releasing all mats after use
            mat.release()
            mask.release()
            hierarchy.release()

        } catch (e: Exception) {
            /**error handling, prints stack trace for specific debug**/
            telemetry?.addData("[ERROR]", e)
            e.stackTrace.toList().stream().forEach { x -> telemetry?.addLine(x.toString()) }
        }
        telemetry?.update()


        /**returns the black and orange mask with contours drawn to see logic in action**/
        return ret
    }
}

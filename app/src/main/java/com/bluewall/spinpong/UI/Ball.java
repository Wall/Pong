package com.bluewall.spinpong.UI;

import android.opengl.Matrix;

import com.bluewall.spinpong.gles.Shape;

/**
 * Created by david on 12/21/14.
 */
public class Ball extends Shape {

    private static final float RADIUS = 60f;

    /* Increases spin rate with regards to animation */
    private static final int SPIN_ANIMATION_EXAGGERATION = 6;
    /* Determines how much spin affects direction of ball upon hitting a wall */
    private static final int COLLISION_SPIN_EXAGGERATION = 15;
    /* Loss of speed upon hitting a wall */
    private static final float SPEED_DECAY = 0.96f;
    /* Affects how hard the pad can hit the ball */
    private static final float SPEED_ONHIT = 0.1f;
    /* Rate at which spin is lost per frame */
    private static final float SPIN_DECAY = 0.4f;
    /* Spin decays faster when rolling */
    private static final float SPIN_ROLLING_DECAY = 4*SPIN_DECAY;
    /* Level of spin at which ball transitions from spinning to rolling and visa versa */
    private static final float SPIN_ANIMATION_THRESHOLD = 0.4f;
    /* Affects how hard the pad can spin the ball */
    private static final float SPIN_ONHIT = 0.4f;
    /* How many frames are interpolated during the collision detection process */
    private static final int INTERPOLATION_NUMBER = 16;
    /* Determines size of bounding field,
     * collision detection calculations are skipped if ball is outside of pads bounding field */
    private static final int BOUNDING_COLLISION_SIZE = 4;

    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////

    /* Current x and y velocity */
    private float xspeed = 900;
    private float yspeed = 400;
    /* Vector [u, v] the direction in which the ball is rolling */
    private float u = 0;
    private float v = 0;
    /* Current rotation about the vector [u, v] (state of rotation regarding roll) */
    private float rot = 0;
    /* Current rotation about the z-axis (state of rotation regarding spin) */
    private float rotZ = 0;
    /* Rate at which ball is spinning in radians per second */
    private float spin = 0.0f;
    private boolean isSpinning = false;

    private Pad pad;

    private void init() {

        new Thread(new Runnable() {

            private static final int SLEEP = 15;
            private long clock = System.currentTimeMillis() - SLEEP;
            private float scale;
            private float lastX, lastY;
            private float lastPadX, lastPadY;
            private boolean hadCollided = false;
            private float maxSpeed;

            @Override
            public void run() {

                while (pad == null);

                while (true) {
                    setClockAndScale();
                    updatePosition();
                    checkCollision();
                    updateOpenGLData();
                    setPreviousFrameData();

                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void checkCollision() {
                //LEFT WALL
                if (x <= RADIUS - ScreenInfo.RES_X / 2) {
                    collision();
                    xspeed = Math.abs(xspeed);
                }
                //RIGHT WALL
                else if (x >= ScreenInfo.RES_X / 2 - RADIUS) {
                    collision();
                    xspeed = -Math.abs(xspeed);
                }
                //TOP WALL
                if (y <= RADIUS - ScreenInfo.RES_Y / 2) {
                    collision();
                    yspeed = Math.abs(yspeed);
                }
                //BOTTOM WALL
                else if (y >= ScreenInfo.RES_Y / 2 - RADIUS) {
                    collision();
                    yspeed = -Math.abs(yspeed);
                }
                //Checks if pad and ball are in the near vicinity of each other
                if (Math.abs(pad.getX() - x) + Math.abs(pad.getY() - y) <= BOUNDING_COLLISION_SIZE*(Pad.HEIGHT/2 + Pad.WIDTH/2 + 2*RADIUS)) {
                    boolean hit = false;
                    if (intersects()) {
                        bpCollision();
                        hit = true;
                    } else {
                        float stepX = (x - lastX) / INTERPOLATION_NUMBER;
                        float stepY = (y - lastY) / INTERPOLATION_NUMBER;
                        float stepPadX = (pad.getX() - lastPadX) / INTERPOLATION_NUMBER;
                        float stepPadY = (pad.getY() - lastPadY) / INTERPOLATION_NUMBER;
                        for (int i = 1; i <= INTERPOLATION_NUMBER; ++i) {
                            if (intersects(lastX + stepX * i, lastY + stepY * i, lastPadX + stepPadX * i, lastPadY + stepPadY * i)) {
                                bpCollision();
                                hit = true;
                                break;
                            }
                        }
                    }
                    if (!hit) {
                        if (hadCollided) {
                            xspeed = -maxSpeed;
                        }
                        hadCollided = false;
                    }
                }
            }

            private void setPreviousFrameData() {
                lastX = x;
                lastY = y;
                lastPadX = pad.getX();
                lastPadY = pad.getY();
            }

            private void setClockAndScale() {
                long time = System.currentTimeMillis();
                scale = ((float) (time - clock))/1000;
                clock = time;
            }

            private void updatePosition() {
                x += xspeed*scale;
                y += yspeed*scale;

                rot += 0.1;
                rotZ += SPIN_ANIMATION_EXAGGERATION*spin*scale;

                rotate(spin);
                spin *= (1 - (isSpinning ? SPIN_DECAY : SPIN_ROLLING_DECAY)*scale);
            }

            private void updateOpenGLData() {
                float m = (float) Math.sqrt(yspeed * yspeed + xspeed * xspeed);
                u = -yspeed/m;
                v = -xspeed/m;
                isSpinning = spin > SPIN_ANIMATION_THRESHOLD;
            }

            private void bpCollision() {
                int side = lastX < lastPadX ? -1 : 1;
                float sumX = side*((x - lastX) - (pad.getX() - lastPadX) * SPEED_ONHIT) / scale;

                spin += (pad.getY() - lastPadY) * scale * SPIN_ONHIT;
                collision();
                xspeed = -sumX;
                x = (RADIUS + Pad.WIDTH)*side + pad.getX();

                maxSpeed = hadCollided ? Math.abs(maxSpeed) > Math.abs(sumX) ? maxSpeed : sumX : sumX;
                hadCollided = true;
            }

            private void rotate(float angle) {
                float xspeedTemp = (float) (xspeed*Math.cos(angle*scale) + yspeed*Math.sin(angle*scale));
                float yspeedTemp = (float) (-xspeed*Math.sin(angle*scale) + yspeed*Math.cos(angle*scale));

                xspeed = xspeedTemp;
                yspeed = yspeedTemp;
            }

            private void collision() {
                xspeed *= SPEED_DECAY;
                yspeed *= SPEED_DECAY;

                spin *= (1 - SPIN_ROLLING_DECAY*scale);
                rotate(-COLLISION_SPIN_EXAGGERATION*spin);
            }

            private boolean intersects() {
                float cdx = Math.abs(x - pad.getX());
                float cdy = Math.abs(y - pad.getY());

                if (cdx > (Pad.WIDTH/2 + RADIUS)) { return false; }
                if (cdy > (Pad.HEIGHT/2 + RADIUS)) { return false; }

                if (cdx <= (Pad.WIDTH/2)) { return true; }
                if (cdy <= (Pad.HEIGHT/2)) { return true; }

                float cdsq = (cdx - Pad.WIDTH/2)*(cdx - Pad.WIDTH/2) +
                        (cdy - Pad.HEIGHT/2)*(cdy - Pad.HEIGHT/2);

                return cdsq <= RADIUS*RADIUS;
            }

            private boolean intersects(float x1, float y1, float x2, float y2) {
                float cdx = Math.abs(x1 - x2);
                float cdy = Math.abs(y1 - y2);

                if (cdx > (Pad.WIDTH/2 + RADIUS)) { return false; }
                if (cdy > (Pad.HEIGHT/2 + RADIUS)) { return false; }

                if (cdx <= (Pad.WIDTH/2)) { return true; }
                if (cdy <= (Pad.HEIGHT/2)) { return true; }

                float cdsq = (cdx - Pad.WIDTH/2)*(cdx - Pad.WIDTH/2) +
                        (cdy - Pad.HEIGHT/2)*(cdy - Pad.HEIGHT/2);

                return cdsq <= RADIUS*RADIUS;
            }

        }).start();
    }

    public void setPad(Pad pad) {
        this.pad = pad;
    }

    @Override
    public float[] genTransformationMatrix() {

        System.arraycopy(IDENTITY_MATRIX, 0, bufferMatrix, 0, 16);
        System.arraycopy(IDENTITY_MATRIX, 0, transformationMatrix, 0, 16);
        if (isSpinning) {
            transformationMatrix[0] = (float) Math.cos(rotZ);
            transformationMatrix[5] = transformationMatrix[0];
            transformationMatrix[4] = (float) -Math.sin(rotZ);
            transformationMatrix[1] = -transformationMatrix[4];
        } else {
           float cos = (float) Math.cos(rot);
           float sin = (float) Math.sin(rot);

           transformationMatrix[0] = u * u + (1 - u * u) * cos;
           transformationMatrix[1] = u * v * (1 - cos);
           transformationMatrix[2] = v * sin;

           transformationMatrix[4] = u * v * (1 - cos);
           transformationMatrix[5] = v * v + (1 - v * v) * cos;
           transformationMatrix[6] = -u * sin;

           transformationMatrix[8] = -v * sin;
           transformationMatrix[9] = u * sin;
           transformationMatrix[10] = cos;
        }

        bufferMatrix[12] += x * 2 / RESOLUTION_X;
        bufferMatrix[13] -= y * 2 / RESOLUTION_Y;

        Matrix.multiplyMM(resultMatrix, 0, bufferMatrix, 0, transformationMatrix, 0);

        return resultMatrix;
    }

    public Ball() {

        super(new float[] {
                        0.000000f*RADIUS, 0.000000f*RADIUS, -1.000000f*RADIUS,
                        0.203181f*RADIUS, -0.147618f*RADIUS, -0.967950f*RADIUS,
                        -0.077607f*RADIUS, -0.238853f*RADIUS, -0.967950f*RADIUS,
                        0.723607f*RADIUS, -0.525725f*RADIUS, -0.447220f*RADIUS,
                        0.609547f*RADIUS, -0.442856f*RADIUS, -0.657519f*RADIUS,
                        0.812729f*RADIUS, -0.295238f*RADIUS, -0.502301f*RADIUS,
                        -0.251147f*RADIUS, 0.000000f*RADIUS, -0.967949f*RADIUS,
                        -0.077607f*RADIUS, 0.238853f*RADIUS, -0.967950f*RADIUS,
                        0.203181f*RADIUS, 0.147618f*RADIUS, -0.967950f*RADIUS,
                        0.860698f*RADIUS, -0.442858f*RADIUS, -0.251151f*RADIUS,
                        -0.276388f*RADIUS, -0.850649f*RADIUS, -0.447220f*RADIUS,
                        -0.029639f*RADIUS, -0.864184f*RADIUS, -0.502302f*RADIUS,
                        -0.155215f*RADIUS, -0.955422f*RADIUS, -0.251152f*RADIUS,
                        -0.894426f*RADIUS, 0.000000f*RADIUS, -0.447216f*RADIUS,
                        -0.831051f*RADIUS, -0.238853f*RADIUS, -0.502299f*RADIUS,
                        -0.956626f*RADIUS, -0.147618f*RADIUS, -0.251149f*RADIUS,
                        -0.276388f*RADIUS, 0.850649f*RADIUS, -0.447220f*RADIUS,
                        -0.483971f*RADIUS, 0.716565f*RADIUS, -0.502302f*RADIUS,
                        -0.436007f*RADIUS, 0.864188f*RADIUS, -0.251152f*RADIUS,
                        0.723607f*RADIUS, 0.525725f*RADIUS, -0.447220f*RADIUS,
                        0.531941f*RADIUS, 0.681712f*RADIUS, -0.502302f*RADIUS,
                        0.687159f*RADIUS, 0.681715f*RADIUS, -0.251152f*RADIUS,
                        0.687159f*RADIUS, -0.681715f*RADIUS, -0.251152f*RADIUS,
                        -0.436007f*RADIUS, -0.864188f*RADIUS, -0.251152f*RADIUS,
                        -0.956626f*RADIUS, 0.147618f*RADIUS, -0.251149f*RADIUS,
                        -0.155215f*RADIUS, 0.955422f*RADIUS, -0.251152f*RADIUS,
                        0.860698f*RADIUS, 0.442858f*RADIUS, -0.251151f*RADIUS,
                        0.276388f*RADIUS, -0.850649f*RADIUS, 0.447220f*RADIUS,
                        0.483971f*RADIUS, -0.716565f*RADIUS, 0.502302f*RADIUS,
                        0.232822f*RADIUS, -0.716563f*RADIUS, 0.657519f*RADIUS,
                        -0.723607f*RADIUS, -0.525725f*RADIUS, 0.447220f*RADIUS,
                        -0.531941f*RADIUS, -0.681712f*RADIUS, 0.502302f*RADIUS,
                        -0.609547f*RADIUS, -0.442856f*RADIUS, 0.657519f*RADIUS,
                        -0.723607f*RADIUS, 0.525725f*RADIUS, 0.447220f*RADIUS,
                        -0.812729f*RADIUS, 0.295238f*RADIUS, 0.502301f*RADIUS,
                        -0.609547f*RADIUS, 0.442856f*RADIUS, 0.657519f*RADIUS,
                        0.276388f*RADIUS, 0.850649f*RADIUS, 0.447220f*RADIUS,
                        0.029639f*RADIUS, 0.864184f*RADIUS, 0.502302f*RADIUS,
                        0.232822f*RADIUS, 0.716563f*RADIUS, 0.657519f*RADIUS,
                        0.894426f*RADIUS, 0.000000f*RADIUS, 0.447216f*RADIUS,
                        0.831051f*RADIUS, 0.238853f*RADIUS, 0.502299f*RADIUS,
                        0.753442f*RADIUS, 0.000000f*RADIUS, 0.657515f*RADIUS,
                        -0.232822f*RADIUS, -0.716563f*RADIUS, -0.657519f*RADIUS,
                        -0.162456f*RADIUS, -0.499995f*RADIUS, -0.850654f*RADIUS,
                        0.052790f*RADIUS, -0.688185f*RADIUS, -0.723612f*RADIUS,
                        0.138199f*RADIUS, -0.425321f*RADIUS, -0.894429f*RADIUS,
                        0.262869f*RADIUS, -0.809012f*RADIUS, -0.525738f*RADIUS,
                        0.361805f*RADIUS, -0.587779f*RADIUS, -0.723611f*RADIUS,
                        0.531941f*RADIUS, -0.681712f*RADIUS, -0.502302f*RADIUS,
                        0.425323f*RADIUS, -0.309011f*RADIUS, -0.850654f*RADIUS,
                        0.812729f*RADIUS, 0.295238f*RADIUS, -0.502301f*RADIUS,
                        0.609547f*RADIUS, 0.442856f*RADIUS, -0.657519f*RADIUS,
                        0.850648f*RADIUS, 0.000000f*RADIUS, -0.525736f*RADIUS,
                        0.670817f*RADIUS, 0.162457f*RADIUS, -0.723611f*RADIUS,
                        0.670818f*RADIUS, -0.162458f*RADIUS, -0.723610f*RADIUS,
                        0.425323f*RADIUS, 0.309011f*RADIUS, -0.850654f*RADIUS,
                        0.447211f*RADIUS, -0.000001f*RADIUS, -0.894428f*RADIUS,
                        -0.753442f*RADIUS, 0.000000f*RADIUS, -0.657515f*RADIUS,
                        -0.525730f*RADIUS, 0.000000f*RADIUS, -0.850652f*RADIUS,
                        -0.638195f*RADIUS, -0.262864f*RADIUS, -0.723609f*RADIUS,
                        -0.361801f*RADIUS, -0.262864f*RADIUS, -0.894428f*RADIUS,
                        -0.688189f*RADIUS, -0.499997f*RADIUS, -0.525736f*RADIUS,
                        -0.447211f*RADIUS, -0.525729f*RADIUS, -0.723610f*RADIUS,
                        -0.483971f*RADIUS, -0.716565f*RADIUS, -0.502302f*RADIUS,
                        -0.232822f*RADIUS, 0.716563f*RADIUS, -0.657519f*RADIUS,
                        -0.162456f*RADIUS, 0.499995f*RADIUS, -0.850654f*RADIUS,
                        -0.447211f*RADIUS, 0.525727f*RADIUS, -0.723612f*RADIUS,
                        -0.361801f*RADIUS, 0.262863f*RADIUS, -0.894429f*RADIUS,
                        -0.688189f*RADIUS, 0.499997f*RADIUS, -0.525736f*RADIUS,
                        -0.638195f*RADIUS, 0.262863f*RADIUS, -0.723609f*RADIUS,
                        -0.831051f*RADIUS, 0.238853f*RADIUS, -0.502299f*RADIUS,
                        0.361803f*RADIUS, 0.587779f*RADIUS, -0.723612f*RADIUS,
                        0.138197f*RADIUS, 0.425321f*RADIUS, -0.894429f*RADIUS,
                        0.262869f*RADIUS, 0.809012f*RADIUS, -0.525738f*RADIUS,
                        0.052789f*RADIUS, 0.688186f*RADIUS, -0.723611f*RADIUS,
                        -0.029639f*RADIUS, 0.864184f*RADIUS, -0.502302f*RADIUS,
                        0.956626f*RADIUS, -0.147618f*RADIUS, 0.251149f*RADIUS,
                        0.956626f*RADIUS, 0.147618f*RADIUS, 0.251149f*RADIUS,
                        0.951058f*RADIUS, -0.309013f*RADIUS, -0.000000f*RADIUS,
                        1.000000f*RADIUS, 0.000000f*RADIUS, 0.000000f*RADIUS,
                        0.947213f*RADIUS, -0.162458f*RADIUS, -0.276396f*RADIUS,
                        0.951058f*RADIUS, 0.309013f*RADIUS, 0.000000f*RADIUS,
                        0.947213f*RADIUS, 0.162458f*RADIUS, -0.276396f*RADIUS,
                        0.155215f*RADIUS, -0.955422f*RADIUS, 0.251152f*RADIUS,
                        0.436007f*RADIUS, -0.864188f*RADIUS, 0.251152f*RADIUS,
                        0.000000f*RADIUS, -1.000000f*RADIUS, -0.000000f*RADIUS,
                        0.309017f*RADIUS, -0.951056f*RADIUS, 0.000000f*RADIUS,
                        0.138199f*RADIUS, -0.951055f*RADIUS, -0.276398f*RADIUS,
                        0.587786f*RADIUS, -0.809017f*RADIUS, 0.000000f*RADIUS,
                        0.447216f*RADIUS, -0.850648f*RADIUS, -0.276398f*RADIUS,
                        -0.860698f*RADIUS, -0.442858f*RADIUS, 0.251151f*RADIUS,
                        -0.687159f*RADIUS, -0.681715f*RADIUS, 0.251152f*RADIUS,
                        -0.951058f*RADIUS, -0.309013f*RADIUS, -0.000000f*RADIUS,
                        -0.809018f*RADIUS, -0.587783f*RADIUS, 0.000000f*RADIUS,
                        -0.861803f*RADIUS, -0.425324f*RADIUS, -0.276396f*RADIUS,
                        -0.587786f*RADIUS, -0.809017f*RADIUS, 0.000000f*RADIUS,
                        -0.670819f*RADIUS, -0.688191f*RADIUS, -0.276397f*RADIUS,
                        -0.687159f*RADIUS, 0.681715f*RADIUS, 0.251152f*RADIUS,
                        -0.860698f*RADIUS, 0.442858f*RADIUS, 0.251151f*RADIUS,
                        -0.587786f*RADIUS, 0.809017f*RADIUS, -0.000000f*RADIUS,
                        -0.809018f*RADIUS, 0.587783f*RADIUS, -0.000000f*RADIUS,
                        -0.670819f*RADIUS, 0.688191f*RADIUS, -0.276397f*RADIUS,
                        -0.951058f*RADIUS, 0.309013f*RADIUS, 0.000000f*RADIUS,
                        -0.861803f*RADIUS, 0.425324f*RADIUS, -0.276396f*RADIUS,
                        0.436007f*RADIUS, 0.864188f*RADIUS, 0.251152f*RADIUS,
                        0.155215f*RADIUS, 0.955422f*RADIUS, 0.251152f*RADIUS,
                        0.587786f*RADIUS, 0.809017f*RADIUS, -0.000000f*RADIUS,
                        0.309017f*RADIUS, 0.951056f*RADIUS, -0.000000f*RADIUS,
                        0.447216f*RADIUS, 0.850648f*RADIUS, -0.276398f*RADIUS,
                        0.000000f*RADIUS, 1.000000f*RADIUS, 0.000000f*RADIUS,
                        0.138199f*RADIUS, 0.951055f*RADIUS, -0.276398f*RADIUS,
                        0.670820f*RADIUS, -0.688190f*RADIUS, 0.276396f*RADIUS,
                        0.809019f*RADIUS, -0.587783f*RADIUS, -0.000002f*RADIUS,
                        0.688189f*RADIUS, -0.499997f*RADIUS, 0.525736f*RADIUS,
                        0.861804f*RADIUS, -0.425323f*RADIUS, 0.276394f*RADIUS,
                        0.831051f*RADIUS, -0.238853f*RADIUS, 0.502299f*RADIUS,
                        -0.447216f*RADIUS, -0.850648f*RADIUS, 0.276397f*RADIUS,
                        -0.309017f*RADIUS, -0.951056f*RADIUS, -0.000001f*RADIUS,
                        -0.262869f*RADIUS, -0.809012f*RADIUS, 0.525738f*RADIUS,
                        -0.138199f*RADIUS, -0.951055f*RADIUS, 0.276397f*RADIUS,
                        0.029639f*RADIUS, -0.864184f*RADIUS, 0.502302f*RADIUS,
                        -0.947213f*RADIUS, 0.162458f*RADIUS, 0.276396f*RADIUS,
                        -1.000000f*RADIUS, -0.000000f*RADIUS, 0.000001f*RADIUS,
                        -0.850648f*RADIUS, 0.000000f*RADIUS, 0.525736f*RADIUS,
                        -0.947213f*RADIUS, -0.162458f*RADIUS, 0.276397f*RADIUS,
                        -0.812729f*RADIUS, -0.295238f*RADIUS, 0.502301f*RADIUS,
                        -0.138199f*RADIUS, 0.951055f*RADIUS, 0.276397f*RADIUS,
                        -0.309016f*RADIUS, 0.951057f*RADIUS, -0.000000f*RADIUS,
                        -0.262869f*RADIUS, 0.809012f*RADIUS, 0.525738f*RADIUS,
                        -0.447215f*RADIUS, 0.850649f*RADIUS, 0.276397f*RADIUS,
                        -0.531941f*RADIUS, 0.681712f*RADIUS, 0.502302f*RADIUS,
                        0.861804f*RADIUS, 0.425322f*RADIUS, 0.276396f*RADIUS,
                        0.809019f*RADIUS, 0.587782f*RADIUS, 0.000000f*RADIUS,
                        0.688189f*RADIUS, 0.499997f*RADIUS, 0.525736f*RADIUS,
                        0.670821f*RADIUS, 0.688189f*RADIUS, 0.276397f*RADIUS,
                        0.483971f*RADIUS, 0.716565f*RADIUS, 0.502302f*RADIUS,
                        0.077607f*RADIUS, -0.238853f*RADIUS, 0.967950f*RADIUS,
                        0.251147f*RADIUS, 0.000000f*RADIUS, 0.967949f*RADIUS,
                        0.000000f*RADIUS, 0.000000f*RADIUS, 1.000000f*RADIUS,
                        0.162456f*RADIUS, -0.499995f*RADIUS, 0.850654f*RADIUS,
                        0.361800f*RADIUS, -0.262863f*RADIUS, 0.894429f*RADIUS,
                        0.447209f*RADIUS, -0.525728f*RADIUS, 0.723612f*RADIUS,
                        0.525730f*RADIUS, 0.000000f*RADIUS, 0.850652f*RADIUS,
                        0.638194f*RADIUS, -0.262864f*RADIUS, 0.723610f*RADIUS,
                        -0.203181f*RADIUS, -0.147618f*RADIUS, 0.967950f*RADIUS,
                        -0.425323f*RADIUS, -0.309011f*RADIUS, 0.850654f*RADIUS,
                        -0.138197f*RADIUS, -0.425319f*RADIUS, 0.894430f*RADIUS,
                        -0.361804f*RADIUS, -0.587778f*RADIUS, 0.723612f*RADIUS,
                        -0.052790f*RADIUS, -0.688185f*RADIUS, 0.723612f*RADIUS,
                        -0.203181f*RADIUS, 0.147618f*RADIUS, 0.967950f*RADIUS,
                        -0.425323f*RADIUS, 0.309011f*RADIUS, 0.850654f*RADIUS,
                        -0.447210f*RADIUS, 0.000000f*RADIUS, 0.894429f*RADIUS,
                        -0.670817f*RADIUS, 0.162457f*RADIUS, 0.723611f*RADIUS,
                        -0.670817f*RADIUS, -0.162457f*RADIUS, 0.723611f*RADIUS,
                        0.077607f*RADIUS, 0.238853f*RADIUS, 0.967950f*RADIUS,
                        0.162456f*RADIUS, 0.499995f*RADIUS, 0.850654f*RADIUS,
                        -0.138197f*RADIUS, 0.425319f*RADIUS, 0.894430f*RADIUS,
                        -0.052790f*RADIUS, 0.688185f*RADIUS, 0.723612f*RADIUS,
                        -0.361804f*RADIUS, 0.587778f*RADIUS, 0.723612f*RADIUS,
                        0.361800f*RADIUS, 0.262863f*RADIUS, 0.894429f*RADIUS,
                        0.638194f*RADIUS, 0.262864f*RADIUS, 0.723610f*RADIUS,
                        0.447209f*RADIUS, 0.525728f*RADIUS, 0.723612f*RADIUS
                },
                new short[] {
                        0, 1, 2,
                        3, 4, 5,
                        0, 2, 6,
                        0, 6, 7,
                        0, 7, 8,
                        3, 5, 9,
                        10, 11, 12,
                        13, 14, 15,
                        16, 17, 18,
                        19, 20, 21,
                        3, 9, 22,
                        10, 12, 23,
                        13, 15, 24,
                        16, 18, 25,
                        19, 21, 26,
                        27, 28, 29,
                        30, 31, 32,
                        33, 34, 35,
                        36, 37, 38,
                        39, 40, 41,
                        42, 11, 10,
                        43, 44, 42,
                        2, 45, 43,
                        42, 44, 11,
                        44, 46, 11,
                        43, 45, 44,
                        45, 47, 44,
                        44, 47, 46,
                        47, 48, 46,
                        2, 1, 45,
                        1, 49, 45,
                        45, 49, 47,
                        49, 4, 47,
                        47, 4, 48,
                        4, 3, 48,
                        50, 51, 19,
                        52, 53, 50,
                        5, 54, 52,
                        50, 53, 51,
                        53, 55, 51,
                        52, 54, 53,
                        54, 56, 53,
                        53, 56, 55,
                        56, 8, 55,
                        5, 4, 54,
                        4, 49, 54,
                        54, 49, 56,
                        49, 1, 56,
                        56, 1, 8,
                        1, 0, 8,
                        57, 14, 13,
                        58, 59, 57,
                        6, 60, 58,
                        57, 59, 14,
                        59, 61, 14,
                        58, 60, 59,
                        60, 62, 59,
                        59, 62, 61,
                        62, 63, 61,
                        6, 2, 60,
                        2, 43, 60,
                        60, 43, 62,
                        43, 42, 62,
                        62, 42, 63,
                        42, 10, 63,
                        64, 17, 16,
                        65, 66, 64,
                        7, 67, 65,
                        64, 66, 17,
                        66, 68, 17,
                        65, 67, 66,
                        67, 69, 66,
                        66, 69, 68,
                        69, 70, 68,
                        7, 6, 67,
                        6, 58, 67,
                        67, 58, 69,
                        58, 57, 69,
                        69, 57, 70,
                        57, 13, 70,
                        51, 20, 19,
                        55, 71, 51,
                        8, 72, 55,
                        51, 71, 20,
                        71, 73, 20,
                        55, 72, 71,
                        72, 74, 71,
                        71, 74, 73,
                        74, 75, 73,
                        8, 7, 72,
                        7, 65, 72,
                        72, 65, 74,
                        65, 64, 74,
                        74, 64, 75,
                        64, 16, 75,
                        76, 77, 39,
                        78, 79, 76,
                        9, 80, 78,
                        76, 79, 77,
                        79, 81, 77,
                        78, 80, 79,
                        80, 82, 79,
                        79, 82, 81,
                        82, 26, 81,
                        9, 5, 80,
                        5, 52, 80,
                        80, 52, 82,
                        52, 50, 82,
                        82, 50, 26,
                        50, 19, 26,
                        83, 84, 27,
                        85, 86, 83,
                        12, 87, 85,
                        83, 86, 84,
                        86, 88, 84,
                        85, 87, 86,
                        87, 89, 86,
                        86, 89, 88,
                        89, 22, 88,
                        12, 11, 87,
                        11, 46, 87,
                        87, 46, 89,
                        46, 48, 89,
                        89, 48, 22,
                        48, 3, 22,
                        90, 91, 30,
                        92, 93, 90,
                        15, 94, 92,
                        90, 93, 91,
                        93, 95, 91,
                        92, 94, 93,
                        94, 96, 93,
                        93, 96, 95,
                        96, 23, 95,
                        15, 14, 94,
                        14, 61, 94,
                        94, 61, 96,
                        61, 63, 96,
                        96, 63, 23,
                        63, 10, 23,
                        97, 98, 33,
                        99, 100, 97,
                        18, 101, 99,
                        97, 100, 98,
                        100, 102, 98,
                        99, 101, 100,
                        101, 103, 100,
                        100, 103, 102,
                        103, 24, 102,
                        18, 17, 101,
                        17, 68, 101,
                        101, 68, 103,
                        68, 70, 103,
                        103, 70, 24,
                        70, 13, 24,
                        104, 105, 36,
                        106, 107, 104,
                        21, 108, 106,
                        104, 107, 105,
                        107, 109, 105,
                        106, 108, 107,
                        108, 110, 107,
                        107, 110, 109,
                        110, 25, 109,
                        21, 20, 108,
                        20, 73, 108,
                        108, 73, 110,
                        73, 75, 110,
                        110, 75, 25,
                        75, 16, 25,
                        84, 28, 27,
                        88, 111, 84,
                        22, 112, 88,
                        84, 111, 28,
                        111, 113, 28,
                        88, 112, 111,
                        112, 114, 111,
                        111, 114, 113,
                        114, 115, 113,
                        22, 9, 112,
                        9, 78, 112,
                        112, 78, 114,
                        78, 76, 114,
                        114, 76, 115,
                        76, 39, 115,
                        91, 31, 30,
                        95, 116, 91,
                        23, 117, 95,
                        91, 116, 31,
                        116, 118, 31,
                        95, 117, 116,
                        117, 119, 116,
                        116, 119, 118,
                        119, 120, 118,
                        23, 12, 117,
                        12, 85, 117,
                        117, 85, 119,
                        85, 83, 119,
                        119, 83, 120,
                        83, 27, 120,
                        98, 34, 33,
                        102, 121, 98,
                        24, 122, 102,
                        98, 121, 34,
                        121, 123, 34,
                        102, 122, 121,
                        122, 124, 121,
                        121, 124, 123,
                        124, 125, 123,
                        24, 15, 122,
                        15, 92, 122,
                        122, 92, 124,
                        92, 90, 124,
                        124, 90, 125,
                        90, 30, 125,
                        105, 37, 36,
                        109, 126, 105,
                        25, 127, 109,
                        105, 126, 37,
                        126, 128, 37,
                        109, 127, 126,
                        127, 129, 126,
                        126, 129, 128,
                        129, 130, 128,
                        25, 18, 127,
                        18, 99, 127,
                        127, 99, 129,
                        99, 97, 129,
                        129, 97, 130,
                        97, 33, 130,
                        77, 40, 39,
                        81, 131, 77,
                        26, 132, 81,
                        77, 131, 40,
                        131, 133, 40,
                        81, 132, 131,
                        132, 134, 131,
                        131, 134, 133,
                        134, 135, 133,
                        26, 21, 132,
                        21, 106, 132,
                        132, 106, 134,
                        106, 104, 134,
                        134, 104, 135,
                        104, 36, 135,
                        136, 137, 138,
                        139, 140, 136,
                        29, 141, 139,
                        136, 140, 137,
                        140, 142, 137,
                        139, 141, 140,
                        141, 143, 140,
                        140, 143, 142,
                        143, 41, 142,
                        29, 28, 141,
                        28, 113, 141,
                        141, 113, 143,
                        113, 115, 143,
                        143, 115, 41,
                        115, 39, 41,
                        144, 136, 138,
                        145, 146, 144,
                        32, 147, 145,
                        144, 146, 136,
                        146, 139, 136,
                        145, 147, 146,
                        147, 148, 146,
                        146, 148, 139,
                        148, 29, 139,
                        32, 31, 147,
                        31, 118, 147,
                        147, 118, 148,
                        118, 120, 148,
                        148, 120, 29,
                        120, 27, 29,
                        149, 144, 138,
                        150, 151, 149,
                        35, 152, 150,
                        149, 151, 144,
                        151, 145, 144,
                        150, 152, 151,
                        152, 153, 151,
                        151, 153, 145,
                        153, 32, 145,
                        35, 34, 152,
                        34, 123, 152,
                        152, 123, 153,
                        123, 125, 153,
                        153, 125, 32,
                        125, 30, 32,
                        154, 149, 138,
                        155, 156, 154,
                        38, 157, 155,
                        154, 156, 149,
                        156, 150, 149,
                        155, 157, 156,
                        157, 158, 156,
                        156, 158, 150,
                        158, 35, 150,
                        38, 37, 157,
                        37, 128, 157,
                        157, 128, 158,
                        128, 130, 158,
                        158, 130, 35,
                        130, 33, 35,
                        137, 154, 138,
                        142, 159, 137,
                        41, 160, 142,
                        137, 159, 154,
                        159, 155, 154,
                        142, 160, 159,
                        160, 161, 159,
                        159, 161, 155,
                        161, 38, 155,
                        41, 40, 160,
                        40, 133, 160,
                        160, 133, 161,
                        133, 135, 161,
                        161, 135, 38,
                        135, 36, 38,

                },
                new float[] {
                        0.000000f, 0.000000f, -1.000000f,
                        0.210944f, -0.153264f, -0.965392f,
                        -0.080569f, -0.247963f, -0.965392f,
                        0.723594f, -0.525712f, -0.447218f,
                        0.604205f, -0.438978f, -0.664968f,
                        0.815180f, -0.285714f, -0.503800f,
                        -0.260750f, 0.000000f, -0.965392f,
                        -0.080569f, 0.247963f, -0.965392f,
                        0.210944f, 0.153264f, -0.965392f,
                        0.864986f, -0.438978f, -0.243049f,
                        -0.276376f, -0.850642f, -0.447218f,
                        -0.019837f, -0.863552f, -0.503800f,
                        -0.150212f, -0.958281f, -0.243049f,
                        -0.894406f, 0.000000f, -0.447188f,
                        -0.827448f, -0.247963f, -0.503800f,
                        -0.957823f, -0.153264f, -0.243049f,
                        -0.276376f, 0.850642f, -0.447218f,
                        -0.491531f, 0.710288f, -0.503800f,
                        -0.441725f, 0.863582f, -0.243049f,
                        0.723594f, 0.525712f, -0.447218f,
                        0.523637f, 0.686972f, -0.503800f,
                        0.684805f, 0.686972f, -0.243049f,
                        0.684805f, -0.686972f, -0.243049f,
                        -0.441725f, -0.863582f, -0.243049f,
                        -0.957823f, 0.153264f, -0.243049f,
                        -0.150212f, 0.958281f, -0.243049f,
                        0.864986f, 0.438978f, -0.243049f,
                        0.276376f, -0.850642f, 0.447218f,
                        0.491531f, -0.710288f, 0.503800f,
                        0.230781f, -0.710288f, 0.664968f,
                        -0.723594f, -0.525712f, 0.447218f,
                        -0.523637f, -0.686972f, 0.503800f,
                        -0.604205f, -0.438978f, 0.664968f,
                        -0.723594f, 0.525712f, 0.447218f,
                        -0.815180f, 0.285714f, 0.503800f,
                        -0.604205f, 0.438978f, 0.664968f,
                        0.276376f, 0.850642f, 0.447218f,
                        0.019837f, 0.863552f, 0.503800f,
                        0.230781f, 0.710288f, 0.664968f,
                        0.894406f, 0.000000f, 0.447188f,
                        0.827448f, 0.247963f, 0.503800f,
                        0.746849f, 0.000000f, 0.664968f,
                        -0.230781f, -0.710288f, -0.664968f,
                        -0.162450f, -0.499985f, -0.850642f,
                        0.059206f, -0.683493f, -0.727531f,
                        0.140629f, -0.432844f, -0.890408f,
                        0.262856f, -0.808985f, -0.525712f,
                        0.353832f, -0.587756f, -0.727531f,
                        0.523637f, -0.686972f, -0.503800f,
                        0.425306f, -0.309000f, -0.850642f,
                        0.815180f, 0.285714f, -0.503800f,
                        0.604205f, 0.438978f, -0.664968f,
                        0.850642f, 0.000000f, -0.525712f,
                        0.668325f, 0.154881f, -0.727531f,
                        0.668325f, -0.154881f, -0.727531f,
                        0.425306f, 0.309000f, -0.850642f,
                        0.455123f, 0.000000f, -0.890408f,
                        -0.746849f, 0.000000f, -0.664968f,
                        -0.525712f, 0.000000f, -0.850642f,
                        -0.631733f, -0.267495f, -0.727531f,
                        -0.368206f, -0.267495f, -0.890408f,
                        -0.688162f, -0.499985f, -0.525712f,
                        -0.449629f, -0.518143f, -0.727531f,
                        -0.491531f, -0.710288f, -0.503800f,
                        -0.230781f, 0.710288f, -0.664968f,
                        -0.162450f, 0.499985f, -0.850642f,
                        -0.449629f, 0.518143f, -0.727531f,
                        -0.368206f, 0.267495f, -0.890408f,
                        -0.688162f, 0.499985f, -0.525712f,
                        -0.631733f, 0.267495f, -0.727531f,
                        -0.827448f, 0.247963f, -0.503800f,
                        0.353832f, 0.587756f, -0.727531f,
                        0.140629f, 0.432844f, -0.890408f,
                        0.262856f, 0.808985f, -0.525712f,
                        0.059206f, 0.683493f, -0.727531f,
                        -0.019837f, 0.863552f, -0.503800f,
                        0.957823f, -0.153264f, 0.243049f,
                        0.957823f, 0.153264f, 0.243049f,
                        0.951048f, -0.309000f, 0.000000f,
                        0.999939f, 0.000000f, -0.008850f,
                        0.949614f, -0.154881f, -0.272408f,
                        0.951048f, 0.309000f, 0.000000f,
                        0.949614f, 0.154881f, -0.272408f,
                        0.150212f, -0.958281f, 0.243049f,
                        0.441725f, -0.863582f, 0.243049f,
                        0.000000f, -1.000000f, 0.000000f,
                        0.309000f, -0.951018f, -0.008850f,
                        0.146123f, -0.950987f, -0.272408f,
                        0.587756f, -0.809015f, 0.000000f,
                        0.440748f, -0.855251f, -0.272408f,
                        -0.864986f, -0.438978f, 0.243049f,
                        -0.684805f, -0.686972f, 0.243049f,
                        -0.951048f, -0.309000f, 0.000000f,
                        -0.808985f, -0.587756f, -0.008850f,
                        -0.859310f, -0.432844f, -0.272408f,
                        -0.587756f, -0.809015f, 0.000000f,
                        -0.677206f, -0.683493f, -0.272408f,
                        -0.684805f, 0.686972f, 0.243049f,
                        -0.864986f, 0.438978f, 0.243049f,
                        -0.587756f, 0.809015f, 0.000000f,
                        -0.808985f, 0.587756f, -0.008850f,
                        -0.677206f, 0.683493f, -0.272408f,
                        -0.951048f, 0.309000f, 0.000000f,
                        -0.859310f, 0.432844f, -0.272408f,
                        0.441725f, 0.863582f, 0.243049f,
                        0.150212f, 0.958281f, 0.243049f,
                        0.587756f, 0.809015f, 0.000000f,
                        0.309000f, 0.951018f, -0.008850f,
                        0.440748f, 0.855251f, -0.272408f,
                        0.000000f, 1.000000f, 0.000000f,
                        0.146123f, 0.950987f, -0.272408f,
                        0.677206f, -0.683493f, 0.272408f,
                        0.808985f, -0.587756f, 0.008850f,
                        0.688162f, -0.499985f, 0.525712f,
                        0.859310f, -0.432844f, 0.272408f,
                        0.827448f, -0.247963f, 0.503800f,
                        -0.440748f, -0.855251f, 0.272408f,
                        -0.309000f, -0.951018f, 0.008850f,
                        -0.262856f, -0.808985f, 0.525712f,
                        -0.146123f, -0.950987f, 0.272408f,
                        0.019837f, -0.863552f, 0.503800f,
                        -0.949614f, 0.154881f, 0.272408f,
                        -0.999939f, 0.000000f, 0.008850f,
                        -0.850642f, 0.000000f, 0.525712f,
                        -0.949614f, -0.154881f, 0.272408f,
                        -0.815180f, -0.285714f, 0.503800f,
                        -0.146123f, 0.951018f, 0.272408f,
                        -0.309000f, 0.951018f, 0.008850f,
                        -0.262856f, 0.808985f, 0.525712f,
                        -0.440748f, 0.855251f, 0.272408f,
                        -0.523637f, 0.686972f, 0.503800f,
                        0.859310f, 0.432844f, 0.272408f,
                        0.808985f, 0.587756f, 0.008850f,
                        0.688162f, 0.499985f, 0.525712f,
                        0.677206f, 0.683493f, 0.272408f,
                        0.491531f, 0.710288f, 0.503800f,
                        0.080569f, -0.247963f, 0.965392f,
                        0.260750f, 0.000000f, 0.965392f,
                        0.000000f, 0.000000f, 1.000000f,
                        0.162450f, -0.499985f, 0.850642f,
                        0.368206f, -0.267495f, 0.890408f,
                        0.449629f, -0.518143f, 0.727531f,
                        0.525712f, 0.000000f, 0.850642f,
                        0.631733f, -0.267495f, 0.727531f,
                        -0.210944f, -0.153264f, 0.965392f,
                        -0.425306f, -0.309000f, 0.850642f,
                        -0.140629f, -0.432844f, 0.890408f,
                        -0.353832f, -0.587725f, 0.727531f,
                        -0.059206f, -0.683493f, 0.727531f,
                        -0.210944f, 0.153264f, 0.965392f,
                        -0.425306f, 0.309000f, 0.850642f,
                        -0.455123f, 0.000000f, 0.890408f,
                        -0.668325f, 0.154881f, 0.727531f,
                        -0.668325f, -0.154881f, 0.727531f,
                        0.080569f, 0.247963f, 0.965392f,
                        0.162450f, 0.499985f, 0.850642f,
                        -0.140629f, 0.432844f, 0.890408f,
                        -0.059206f, 0.683493f, 0.727531f,
                        -0.353832f, 0.587725f, 0.727531f,
                        0.368206f, 0.267495f, 0.890408f,
                        0.631733f, 0.267495f, 0.727531f,
                        0.449629f, 0.518143f, 0.727531f
                }
        );
        init();
    }
}

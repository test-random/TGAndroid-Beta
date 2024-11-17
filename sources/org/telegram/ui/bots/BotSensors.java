package org.telegram.ui.bots;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.bots.BotSensors;
import org.telegram.ui.web.BotWebViewContainer;

public class BotSensors {
    private long absoluteOrientationDesiredRefreshRate;
    private Runnable absoluteOrientationListenerPostponed;
    private Sensor accelerometer;
    private long accelerometerDesiredRefreshRate;
    private Runnable accelerometerListenerPostponed;
    private Sensor gyroscope;
    private long gyroscopeDesiredRefreshRate;
    private Runnable gyroscopeListenerPostponed;
    private Sensor orientationAccelerometer;
    private Sensor orientationMagnetometer;
    private boolean paused;
    private long relativeOrientationDesiredRefreshRate;
    private Runnable relativeOrientationListenerPostponed;
    private Sensor rotation;
    private final SensorManager sensorManager;
    private BotWebViewContainer.MyWebView webView;
    private final SensorEventListener accelerometerListener = new AnonymousClass1();
    private final SensorEventListener gyroscopeListener = new AnonymousClass2();
    private final SensorEventListener absoluteOrientationListener = new AnonymousClass3();
    private final SensorEventListener relativeOrientationListener = new AnonymousClass4();

    public class AnonymousClass1 implements SensorEventListener {
        private long lastTime;
        private float[] xyz;

        AnonymousClass1() {
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (BotSensors.this.accelerometerListenerPostponed != null) {
                AndroidUtilities.cancelRunOnUIThread(BotSensors.this.accelerometerListenerPostponed);
                BotSensors.this.accelerometerListenerPostponed = null;
            }
            if (BotSensors.this.paused || BotSensors.this.webView == null) {
                return;
            }
            long currentTimeMillis = System.currentTimeMillis() - this.lastTime;
            this.xyz = sensorEvent.values;
            if (currentTimeMillis >= BotSensors.this.accelerometerDesiredRefreshRate) {
                post();
            } else {
                AndroidUtilities.runOnUIThread(BotSensors.this.accelerometerListenerPostponed = new Runnable() {
                    @Override
                    public final void run() {
                        BotSensors.AnonymousClass1.this.post();
                    }
                }, BotSensors.this.accelerometerDesiredRefreshRate - currentTimeMillis);
            }
        }

        public void post() {
            if (BotSensors.this.webView == null || this.xyz == null) {
                return;
            }
            this.lastTime = System.currentTimeMillis();
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("x", -this.xyz[0]);
                jSONObject.put("y", -this.xyz[1]);
                jSONObject.put("z", -this.xyz[2]);
                BotSensors.this.webView.evaluateJS("window.Telegram.WebView.receiveEvent('accelerometer_changed', " + jSONObject + ");");
            } catch (Exception unused) {
            }
        }
    }

    public class AnonymousClass2 implements SensorEventListener {
        private float[] captured = new float[3];
        private long lastTime;

        AnonymousClass2() {
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (BotSensors.this.gyroscopeListenerPostponed != null) {
                AndroidUtilities.cancelRunOnUIThread(BotSensors.this.gyroscopeListenerPostponed);
                BotSensors.this.gyroscopeListenerPostponed = null;
            }
            if (BotSensors.this.paused || BotSensors.this.webView == null) {
                return;
            }
            float[] fArr = this.captured;
            float f = fArr[0];
            float[] fArr2 = sensorEvent.values;
            fArr[0] = f + fArr2[0];
            fArr[1] = fArr[1] + fArr2[1];
            fArr[2] = fArr[2] + fArr2[2];
            long currentTimeMillis = System.currentTimeMillis() - this.lastTime;
            if (currentTimeMillis >= BotSensors.this.gyroscopeDesiredRefreshRate) {
                post();
            } else {
                AndroidUtilities.runOnUIThread(BotSensors.this.gyroscopeListenerPostponed = new Runnable() {
                    @Override
                    public final void run() {
                        BotSensors.AnonymousClass2.this.post();
                    }
                }, BotSensors.this.gyroscopeDesiredRefreshRate - currentTimeMillis);
            }
        }

        public void post() {
            if (BotSensors.this.webView == null) {
                return;
            }
            this.lastTime = System.currentTimeMillis();
            float[] fArr = this.captured;
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("x", fArr[0]);
                jSONObject.put("y", fArr[1]);
                jSONObject.put("z", fArr[2]);
                BotSensors.this.webView.evaluateJS("window.Telegram.WebView.receiveEvent('gyroscope_changed', " + jSONObject + ");");
            } catch (Exception unused) {
            }
            float[] fArr2 = this.captured;
            fArr2[0] = 0.0f;
            fArr2[1] = 0.0f;
            fArr2[2] = 0.0f;
        }
    }

    public class AnonymousClass3 implements SensorEventListener {
        private float[] geomagnetic;
        private float[] gravity;
        private long lastTime;

        AnonymousClass3() {
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (BotSensors.this.absoluteOrientationListenerPostponed != null) {
                AndroidUtilities.cancelRunOnUIThread(BotSensors.this.absoluteOrientationListenerPostponed);
                BotSensors.this.absoluteOrientationListenerPostponed = null;
            }
            if (BotSensors.this.paused || BotSensors.this.webView == null) {
                return;
            }
            long currentTimeMillis = System.currentTimeMillis() - this.lastTime;
            if (sensorEvent.sensor.getType() == 1) {
                this.gravity = sensorEvent.values;
            }
            if (sensorEvent.sensor.getType() == 2) {
                this.geomagnetic = sensorEvent.values;
            }
            if (currentTimeMillis >= BotSensors.this.absoluteOrientationDesiredRefreshRate) {
                post();
            } else {
                AndroidUtilities.runOnUIThread(BotSensors.this.absoluteOrientationListenerPostponed = new Runnable() {
                    @Override
                    public final void run() {
                        BotSensors.AnonymousClass3.this.post();
                    }
                }, BotSensors.this.absoluteOrientationDesiredRefreshRate - currentTimeMillis);
            }
        }

        public void post() {
            if (this.gravity == null || this.geomagnetic == null || BotSensors.this.webView == null) {
                return;
            }
            this.lastTime = System.currentTimeMillis();
            float[] fArr = new float[9];
            if (SensorManager.getRotationMatrix(fArr, new float[9], this.gravity, this.geomagnetic)) {
                SensorManager.getOrientation(fArr, new float[3]);
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("absolute", true);
                    jSONObject.put("alpha", -r0[0]);
                    jSONObject.put("beta", -r0[1]);
                    jSONObject.put("gamma", r0[2]);
                    BotSensors.this.webView.evaluateJS("window.Telegram.WebView.receiveEvent('device_orientation_changed', " + jSONObject + ");");
                } catch (Exception unused) {
                }
            }
        }
    }

    public class AnonymousClass4 implements SensorEventListener {
        private long lastTime;
        private float[] mDeviceRotationMatrix;
        private float[] mTruncatedRotationVector;
        private float[] values;

        AnonymousClass4() {
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (BotSensors.this.relativeOrientationListenerPostponed != null) {
                AndroidUtilities.cancelRunOnUIThread(BotSensors.this.relativeOrientationListenerPostponed);
                BotSensors.this.relativeOrientationListenerPostponed = null;
            }
            if (BotSensors.this.paused || BotSensors.this.webView == null) {
                return;
            }
            long currentTimeMillis = System.currentTimeMillis() - this.lastTime;
            if (currentTimeMillis < BotSensors.this.relativeOrientationDesiredRefreshRate) {
                AndroidUtilities.runOnUIThread(BotSensors.this.relativeOrientationListenerPostponed = new Runnable() {
                    @Override
                    public final void run() {
                        BotSensors.AnonymousClass4.this.post();
                    }
                }, BotSensors.this.relativeOrientationDesiredRefreshRate - currentTimeMillis);
            } else {
                if (sensorEvent.sensor.getType() == 15) {
                    this.values = sensorEvent.values;
                }
                post();
            }
        }

        public void post() {
            if (this.values == null || BotSensors.this.webView == null) {
                return;
            }
            this.lastTime = System.currentTimeMillis();
            if (this.mDeviceRotationMatrix == null) {
                this.mDeviceRotationMatrix = new float[9];
            }
            if (this.mTruncatedRotationVector == null) {
                this.mTruncatedRotationVector = new float[4];
            }
            float[] fArr = this.values;
            if (fArr.length > 4) {
                System.arraycopy(fArr, 0, this.mTruncatedRotationVector, 0, 4);
                SensorManager.getRotationMatrixFromVector(this.mDeviceRotationMatrix, this.mTruncatedRotationVector);
            } else {
                SensorManager.getRotationMatrixFromVector(this.mDeviceRotationMatrix, fArr);
            }
            SensorManager.getOrientation(this.mDeviceRotationMatrix, new float[3]);
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("absolute", false);
                jSONObject.put("alpha", -r0[0]);
                jSONObject.put("beta", -r0[1]);
                jSONObject.put("gamma", r0[2]);
                BotSensors.this.webView.evaluateJS("window.Telegram.WebView.receiveEvent('device_orientation_changed', " + jSONObject + ");");
            } catch (Exception unused) {
            }
        }
    }

    public BotSensors(Context context, long j) {
        this.sensorManager = (SensorManager) context.getSystemService("sensor");
    }

    private static int getSensorDelay(long j) {
        if (j >= 160) {
            return 3;
        }
        return j >= 60 ? 2 : 1;
    }

    public void attachWebView(BotWebViewContainer.MyWebView myWebView) {
        this.webView = myWebView;
    }

    public void detachWebView(BotWebViewContainer.MyWebView myWebView) {
        if (this.webView == myWebView) {
            this.webView = null;
            pause();
        }
    }

    public void pause() {
        if (this.paused) {
            return;
        }
        this.paused = true;
        SensorManager sensorManager = this.sensorManager;
        if (sensorManager != null) {
            Sensor sensor = this.accelerometer;
            if (sensor != null) {
                sensorManager.unregisterListener(this.accelerometerListener, sensor);
            }
            Runnable runnable = this.accelerometerListenerPostponed;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.accelerometerListenerPostponed = null;
            }
            Sensor sensor2 = this.gyroscope;
            if (sensor2 != null) {
                this.sensorManager.unregisterListener(this.gyroscopeListener, sensor2);
            }
            Runnable runnable2 = this.gyroscopeListenerPostponed;
            if (runnable2 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable2);
                this.gyroscopeListenerPostponed = null;
            }
            Sensor sensor3 = this.orientationAccelerometer;
            if (sensor3 != null) {
                this.sensorManager.unregisterListener(this.absoluteOrientationListener, sensor3);
            }
            Sensor sensor4 = this.orientationMagnetometer;
            if (sensor4 != null) {
                this.sensorManager.unregisterListener(this.absoluteOrientationListener, sensor4);
            }
            Runnable runnable3 = this.absoluteOrientationListenerPostponed;
            if (runnable3 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable3);
                this.absoluteOrientationListenerPostponed = null;
            }
            Sensor sensor5 = this.rotation;
            if (sensor5 != null) {
                this.sensorManager.unregisterListener(this.relativeOrientationListener, sensor5);
            }
            Runnable runnable4 = this.relativeOrientationListenerPostponed;
            if (runnable4 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable4);
                this.relativeOrientationListenerPostponed = null;
            }
        }
    }

    public void resume() {
        if (this.paused) {
            this.paused = false;
            SensorManager sensorManager = this.sensorManager;
            if (sensorManager != null) {
                Sensor sensor = this.accelerometer;
                if (sensor != null) {
                    sensorManager.registerListener(this.accelerometerListener, sensor, getSensorDelay(this.accelerometerDesiredRefreshRate));
                }
                Sensor sensor2 = this.gyroscope;
                if (sensor2 != null) {
                    this.sensorManager.registerListener(this.gyroscopeListener, sensor2, getSensorDelay(this.gyroscopeDesiredRefreshRate));
                }
                Sensor sensor3 = this.orientationAccelerometer;
                if (sensor3 != null) {
                    this.sensorManager.registerListener(this.absoluteOrientationListener, sensor3, getSensorDelay(this.absoluteOrientationDesiredRefreshRate));
                }
                Sensor sensor4 = this.orientationMagnetometer;
                if (sensor4 != null) {
                    this.sensorManager.registerListener(this.absoluteOrientationListener, sensor4, getSensorDelay(this.absoluteOrientationDesiredRefreshRate));
                }
                Sensor sensor5 = this.rotation;
                if (sensor5 != null) {
                    this.sensorManager.registerListener(this.relativeOrientationListener, sensor5, getSensorDelay(this.relativeOrientationDesiredRefreshRate));
                }
            }
        }
    }

    public boolean startAccelerometer(long j) {
        SensorManager sensorManager = this.sensorManager;
        if (sensorManager == null) {
            return false;
        }
        if (this.accelerometer != null) {
            return true;
        }
        Sensor defaultSensor = sensorManager.getDefaultSensor(1);
        this.accelerometer = defaultSensor;
        if (defaultSensor == null) {
            return false;
        }
        this.accelerometerDesiredRefreshRate = j;
        if (!this.paused) {
            this.sensorManager.registerListener(this.accelerometerListener, defaultSensor, getSensorDelay(j));
        }
        return true;
    }

    public boolean startGyroscope(long j) {
        SensorManager sensorManager = this.sensorManager;
        if (sensorManager == null) {
            return false;
        }
        if (this.gyroscope != null) {
            return true;
        }
        Sensor defaultSensor = sensorManager.getDefaultSensor(4);
        this.gyroscope = defaultSensor;
        if (defaultSensor == null) {
            return false;
        }
        this.gyroscopeDesiredRefreshRate = j;
        if (!this.paused) {
            this.sensorManager.registerListener(this.gyroscopeListener, defaultSensor, getSensorDelay(j));
        }
        return true;
    }

    public boolean startOrientation(boolean z, long j) {
        Sensor sensor;
        if (this.sensorManager == null) {
            return false;
        }
        if (z) {
            if (this.rotation != null) {
                Runnable runnable = this.relativeOrientationListenerPostponed;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.relativeOrientationListenerPostponed = null;
                }
                if (!this.paused && (sensor = this.rotation) != null) {
                    this.sensorManager.unregisterListener(this.relativeOrientationListener, sensor);
                }
                this.rotation = null;
            }
            if (this.orientationMagnetometer != null && this.orientationAccelerometer != null) {
                return true;
            }
            this.orientationAccelerometer = this.sensorManager.getDefaultSensor(1);
            Sensor defaultSensor = this.sensorManager.getDefaultSensor(2);
            this.orientationMagnetometer = defaultSensor;
            Sensor sensor2 = this.orientationAccelerometer;
            if (sensor2 == null || defaultSensor == null) {
                return false;
            }
            this.absoluteOrientationDesiredRefreshRate = j;
            if (!this.paused) {
                this.sensorManager.registerListener(this.absoluteOrientationListener, sensor2, getSensorDelay(j));
                this.sensorManager.registerListener(this.absoluteOrientationListener, this.orientationMagnetometer, getSensorDelay(j));
            }
        } else {
            if (this.orientationMagnetometer != null || this.orientationAccelerometer != null) {
                Runnable runnable2 = this.absoluteOrientationListenerPostponed;
                if (runnable2 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable2);
                    this.absoluteOrientationListenerPostponed = null;
                }
                if (!this.paused) {
                    Sensor sensor3 = this.orientationAccelerometer;
                    if (sensor3 != null) {
                        this.sensorManager.unregisterListener(this.absoluteOrientationListener, sensor3);
                    }
                    Sensor sensor4 = this.orientationMagnetometer;
                    if (sensor4 != null) {
                        this.sensorManager.unregisterListener(this.absoluteOrientationListener, sensor4);
                    }
                }
                this.orientationAccelerometer = null;
                this.orientationMagnetometer = null;
            }
            if (this.rotation != null) {
                return true;
            }
            Sensor defaultSensor2 = this.sensorManager.getDefaultSensor(15);
            this.rotation = defaultSensor2;
            if (defaultSensor2 == null) {
                return false;
            }
            this.relativeOrientationDesiredRefreshRate = j;
            if (!this.paused) {
                this.sensorManager.registerListener(this.relativeOrientationListener, defaultSensor2, getSensorDelay(j));
            }
        }
        return true;
    }

    public boolean stopAccelerometer() {
        SensorManager sensorManager = this.sensorManager;
        if (sensorManager == null) {
            return false;
        }
        Sensor sensor = this.accelerometer;
        if (sensor == null) {
            return true;
        }
        if (!this.paused) {
            sensorManager.unregisterListener(this.accelerometerListener, sensor);
        }
        Runnable runnable = this.accelerometerListenerPostponed;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.accelerometerListenerPostponed = null;
        }
        this.accelerometer = null;
        return true;
    }

    public void stopAll() {
        stopOrientation();
        stopGyroscope();
        stopAccelerometer();
    }

    public boolean stopGyroscope() {
        SensorManager sensorManager = this.sensorManager;
        if (sensorManager == null) {
            return false;
        }
        Sensor sensor = this.gyroscope;
        if (sensor == null) {
            return true;
        }
        if (!this.paused) {
            sensorManager.unregisterListener(this.gyroscopeListener, sensor);
        }
        Runnable runnable = this.gyroscopeListenerPostponed;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.gyroscopeListenerPostponed = null;
        }
        this.gyroscope = null;
        return true;
    }

    public boolean stopOrientation() {
        SensorManager sensorManager = this.sensorManager;
        if (sensorManager == null) {
            return false;
        }
        Sensor sensor = this.orientationAccelerometer;
        if (sensor == null && this.orientationMagnetometer == null && this.rotation == null) {
            return true;
        }
        if (!this.paused) {
            if (sensor != null) {
                sensorManager.unregisterListener(this.absoluteOrientationListener, sensor);
            }
            Sensor sensor2 = this.orientationMagnetometer;
            if (sensor2 != null) {
                this.sensorManager.unregisterListener(this.absoluteOrientationListener, sensor2);
            }
            Sensor sensor3 = this.rotation;
            if (sensor3 != null) {
                this.sensorManager.unregisterListener(this.relativeOrientationListener, sensor3);
            }
        }
        Runnable runnable = this.absoluteOrientationListenerPostponed;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.absoluteOrientationListenerPostponed = null;
        }
        Runnable runnable2 = this.relativeOrientationListenerPostponed;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.relativeOrientationListenerPostponed = null;
        }
        this.orientationAccelerometer = null;
        this.orientationMagnetometer = null;
        this.rotation = null;
        return true;
    }
}

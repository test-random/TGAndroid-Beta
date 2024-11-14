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
    private Sensor accelerometer;
    private long accelerometerDesiredRefreshRate;
    private Runnable accelerometerListenerPostponed;
    private Sensor gyroscope;
    private long gyroscopeDesiredRefreshRate;
    private Runnable gyroscopeListenerPostponed;
    private Sensor orientationAccelerometer;
    private long orientationDesiredRefreshRate;
    private Runnable orientationListenerPostponed;
    private Sensor orientationMagnetometer;
    private boolean paused;
    private final SensorManager sensorManager;
    private BotWebViewContainer.MyWebView webView;
    private final SensorEventListener accelerometerListener = new AnonymousClass1();
    private final SensorEventListener gyroscopeListener = new AnonymousClass2();
    private final SensorEventListener orientationListener = new AnonymousClass3();

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
            if (this.xyz == null) {
                return;
            }
            this.lastTime = System.currentTimeMillis();
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("x", this.xyz[0]);
                jSONObject.put("y", this.xyz[1]);
                jSONObject.put("z", this.xyz[2]);
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
            if (BotSensors.this.orientationListenerPostponed != null) {
                AndroidUtilities.cancelRunOnUIThread(BotSensors.this.orientationListenerPostponed);
                BotSensors.this.orientationListenerPostponed = null;
            }
            if (BotSensors.this.paused || BotSensors.this.webView == null) {
                return;
            }
            long currentTimeMillis = System.currentTimeMillis() - this.lastTime;
            if (currentTimeMillis < BotSensors.this.orientationDesiredRefreshRate) {
                AndroidUtilities.runOnUIThread(BotSensors.this.orientationListenerPostponed = new Runnable() {
                    @Override
                    public final void run() {
                        BotSensors.AnonymousClass3.this.post();
                    }
                }, BotSensors.this.orientationDesiredRefreshRate - currentTimeMillis);
                return;
            }
            if (sensorEvent.sensor.getType() == 1) {
                this.gravity = sensorEvent.values;
            }
            if (sensorEvent.sensor.getType() == 2) {
                this.geomagnetic = sensorEvent.values;
            }
            post();
        }

        public void post() {
            if (this.gravity == null || this.geomagnetic == null) {
                return;
            }
            this.lastTime = System.currentTimeMillis();
            float[] fArr = new float[9];
            if (SensorManager.getRotationMatrix(fArr, new float[9], this.gravity, this.geomagnetic)) {
                SensorManager.getOrientation(fArr, new float[3]);
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("alpha", r0[0]);
                    jSONObject.put("beta", -r0[1]);
                    jSONObject.put("gamma", r0[2]);
                    BotSensors.this.webView.evaluateJS("window.Telegram.WebView.receiveEvent('device_orientation_changed', " + jSONObject + ");");
                } catch (Exception unused) {
                }
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
                this.sensorManager.unregisterListener(this.orientationListener, sensor3);
            }
            Sensor sensor4 = this.orientationMagnetometer;
            if (sensor4 != null) {
                this.sensorManager.unregisterListener(this.orientationListener, sensor4);
            }
            Runnable runnable3 = this.orientationListenerPostponed;
            if (runnable3 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable3);
                this.orientationListenerPostponed = null;
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
                    this.sensorManager.registerListener(this.orientationListener, sensor3, getSensorDelay(this.orientationDesiredRefreshRate));
                }
                Sensor sensor4 = this.orientationMagnetometer;
                if (sensor4 != null) {
                    this.sensorManager.registerListener(this.orientationListener, sensor4, getSensorDelay(this.orientationDesiredRefreshRate));
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
        this.gyroscopeDesiredRefreshRate = j;
        if (!this.paused) {
            this.sensorManager.registerListener(this.gyroscopeListener, defaultSensor, getSensorDelay(j));
        }
        return true;
    }

    public boolean startOrientation(long j) {
        SensorManager sensorManager = this.sensorManager;
        if (sensorManager == null) {
            return false;
        }
        if (this.orientationMagnetometer != null && this.orientationAccelerometer != null) {
            return true;
        }
        this.orientationAccelerometer = sensorManager.getDefaultSensor(1);
        this.orientationMagnetometer = this.sensorManager.getDefaultSensor(2);
        this.orientationDesiredRefreshRate = j;
        if (!this.paused) {
            this.sensorManager.registerListener(this.orientationListener, this.orientationAccelerometer, getSensorDelay(j));
            this.sensorManager.registerListener(this.orientationListener, this.orientationMagnetometer, getSensorDelay(j));
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
        if (sensor == null && this.orientationMagnetometer == null) {
            return true;
        }
        if (!this.paused) {
            if (sensor != null) {
                sensorManager.unregisterListener(this.orientationListener, sensor);
            }
            Sensor sensor2 = this.orientationMagnetometer;
            if (sensor2 != null) {
                this.sensorManager.unregisterListener(this.orientationListener, sensor2);
            }
        }
        Runnable runnable = this.orientationListenerPostponed;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.orientationListenerPostponed = null;
        }
        this.orientationAccelerometer = null;
        this.orientationMagnetometer = null;
        return true;
    }
}

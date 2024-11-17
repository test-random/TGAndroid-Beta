package org.telegram.ui.Components;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class EarListener implements SensorEventListener {
    private Sensor accelerometerSensor;
    private boolean accelerometerVertical;
    private boolean attached;
    private final AudioManager audioManager;
    private final Context context;
    private int countLess;
    private VideoPlayer currentPlayer;
    private Sensor gravitySensor;
    private long lastAccelerometerDetected;
    private Sensor linearSensor;
    private final PowerManager powerManager;
    private float previousAccValue;
    private boolean proximityHasDifferentValues;
    private Sensor proximitySensor;
    private boolean proximityTouched;
    private PowerManager.WakeLock proximityWakeLock;
    private boolean raised;
    private int raisedToBack;
    private int raisedToTop;
    private int raisedToTopSign;
    private final SensorManager sensorManager;
    private long timeSinceRaise;
    private long lastTimestamp = 0;
    private float lastProximityValue = -100.0f;
    private float[] gravity = new float[3];
    private float[] gravityFast = new float[3];
    private float[] linearAcceleration = new float[3];

    public EarListener(Context context) {
        this.context = context;
        SensorManager sensorManager = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
        this.sensorManager = sensorManager;
        this.proximitySensor = sensorManager.getDefaultSensor(8);
        this.linearSensor = sensorManager.getDefaultSensor(10);
        Sensor defaultSensor = sensorManager.getDefaultSensor(9);
        this.gravitySensor = defaultSensor;
        if (this.linearSensor == null || defaultSensor == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("gravity or linear sensor not found");
            }
            this.accelerometerSensor = sensorManager.getDefaultSensor(1);
            this.linearSensor = null;
            this.gravitySensor = null;
        }
        PowerManager powerManager = (PowerManager) ApplicationLoader.applicationContext.getSystemService("power");
        this.powerManager = powerManager;
        this.proximityWakeLock = powerManager.newWakeLock(32, "telegram:proximity_lock2");
        this.audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService("audio");
    }

    private boolean disableWakeLockWhenNotUsed() {
        return !Build.MANUFACTURER.equalsIgnoreCase("samsung");
    }

    private boolean isNearToSensor(float f) {
        return f < 5.0f && f != this.proximitySensor.getMaximumRange();
    }

    public void attach() {
        if (this.attached) {
            return;
        }
        Sensor sensor = this.gravitySensor;
        if (sensor != null) {
            this.sensorManager.registerListener(this, sensor, 30000);
        }
        Sensor sensor2 = this.linearSensor;
        if (sensor2 != null) {
            this.sensorManager.registerListener(this, sensor2, 30000);
        }
        Sensor sensor3 = this.accelerometerSensor;
        if (sensor3 != null) {
            this.sensorManager.registerListener(this, sensor3, 30000);
        }
        this.sensorManager.registerListener(this, this.proximitySensor, 3);
        if (this.proximityWakeLock != null && !disableWakeLockWhenNotUsed()) {
            this.proximityWakeLock.acquire();
        }
        this.attached = true;
    }

    public void attachPlayer(VideoPlayer videoPlayer) {
        this.currentPlayer = videoPlayer;
        updateRaised();
    }

    public void detach() {
        if (this.attached) {
            Sensor sensor = this.gravitySensor;
            if (sensor != null) {
                this.sensorManager.unregisterListener(this, sensor);
            }
            Sensor sensor2 = this.linearSensor;
            if (sensor2 != null) {
                this.sensorManager.unregisterListener(this, sensor2);
            }
            Sensor sensor3 = this.accelerometerSensor;
            if (sensor3 != null) {
                this.sensorManager.unregisterListener(this, sensor3);
            }
            this.sensorManager.unregisterListener(this, this.proximitySensor);
            PowerManager.WakeLock wakeLock = this.proximityWakeLock;
            if (wakeLock != null && wakeLock.isHeld()) {
                this.proximityWakeLock.release();
            }
            this.attached = false;
        }
    }

    protected boolean forbidRaiseToListen() {
        AudioDeviceInfo[] devices;
        int type;
        boolean isSink;
        try {
            if (Build.VERSION.SDK_INT < 23) {
                return this.audioManager.isWiredHeadsetOn() || this.audioManager.isBluetoothA2dpOn() || this.audioManager.isBluetoothScoOn();
            }
            devices = this.audioManager.getDevices(2);
            for (AudioDeviceInfo audioDeviceInfo : devices) {
                type = audioDeviceInfo.getType();
                if (type == 8 || type == 7 || type == 26 || type == 27 || type == 4 || type == 3) {
                    isSink = audioDeviceInfo.isSink();
                    if (isSink) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onSensorChanged(android.hardware.SensorEvent r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EarListener.onSensorChanged(android.hardware.SensorEvent):void");
    }

    protected void updateRaised() {
        VideoPlayer videoPlayer = this.currentPlayer;
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.setStreamType(this.raised ? 0 : 3);
    }
}

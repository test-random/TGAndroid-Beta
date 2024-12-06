package org.telegram.ui.Components.Premium.GLIcon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.ActionBar.Theme;

public class Icon3D {
    public final int N;
    private int alphaHandle;
    Bitmap backgroundBitmap;
    private int[] buffers;
    int diffuseHandle;
    private int goldenHandle;
    public int gradientColor1;
    int gradientColor1Handle;
    public int gradientColor2;
    int gradientColor2Handle;
    int gradientPositionHandle;
    private int mBackgroundTextureHandle;
    private int mBackgroundTextureUniformHandle;
    private int mMVPMatrixHandle;
    private int mNormalCoordinateHandle;
    private int mNormalMapUniformHandle;
    private FloatBuffer[] mNormals;
    private int mProgramObject;
    private int mTextureCoordinateHandle;
    private int mTextureDataHandle;
    private int mTextureUniformHandle;
    private FloatBuffer[] mTextures;
    private FloatBuffer[] mVertices;
    private int mVerticesHandle;
    private int mWorldMatrixHandle;
    int modelIndexHandle;
    public boolean night;
    int nightHandle;
    int normalSpecColorHandle;
    int normalSpecHandle;
    int resolutionHandle;
    int specColorHandle;
    int specHandleBottom;
    int specHandleTop;
    Bitmap texture;
    int timeHandle;
    int[] trianglesCount;
    public final int type;
    int typeHandle;
    private int whiteHandle;
    float xOffset;
    private int xOffsetHandle;
    private static final String[] starModel = {"models/star.binobj"};
    private static final String[] coinModel = {"models/coin_outer.binobj", "models/coin_inner.binobj", "models/coin_logo.binobj", "models/coin_stars.binobj"};
    private static final String[] dealModel = {"models/coin_outer.binobj", "models/coin_inner.binobj", "models/deal_logo.binobj", "models/coin_stars.binobj"};
    float enterAlpha = 0.0f;
    public float spec1 = 2.0f;
    public float spec2 = 0.13f;
    public float diffuse = 1.0f;
    public float normalSpec = 0.2f;
    public int normalSpecColor = -1;
    public int specColor = -1;
    private float time = 0.0f;

    public Icon3D(Context context, int i) {
        this.type = i;
        String[] strArr = i == 1 ? coinModel : i == 3 ? dealModel : (i == 0 || i == 2) ? starModel : new String[0];
        int length = strArr.length;
        this.N = length;
        this.mVertices = new FloatBuffer[length];
        this.mTextures = new FloatBuffer[length];
        this.mNormals = new FloatBuffer[length];
        this.trianglesCount = new int[length];
        for (int i2 = 0; i2 < this.N; i2++) {
            ObjLoader objLoader = new ObjLoader(context, strArr[i2]);
            this.mVertices[i2] = ByteBuffer.allocateDirect(objLoader.positions.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.mVertices[i2].put(objLoader.positions).position(0);
            this.mTextures[i2] = ByteBuffer.allocateDirect(objLoader.textureCoordinates.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.mTextures[i2].put(objLoader.textureCoordinates).position(0);
            this.mNormals[i2] = ByteBuffer.allocateDirect(objLoader.normals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.mNormals[i2].put(objLoader.normals).position(0);
            this.trianglesCount[i2] = objLoader.positions.length;
        }
        generateTexture();
        int[] iArr = new int[1];
        int loadShader = GLIconRenderer.loadShader(35633, loadFromAsset(context, "shaders/vertex2.glsl"));
        int loadShader2 = GLIconRenderer.loadShader(35632, loadFromAsset(context, (i == 0 || i == 2) ? "shaders/fragment4.glsl" : "shaders/fragment3.glsl"));
        int glCreateProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(glCreateProgram, loadShader);
        GLES20.glAttachShader(glCreateProgram, loadShader2);
        GLES20.glLinkProgram(glCreateProgram);
        GLES20.glGetProgramiv(glCreateProgram, 35714, iArr, 0);
        this.mProgramObject = glCreateProgram;
        init(context);
    }

    private void generateTexture() {
        this.texture = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.texture);
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0.0f, 100.0f, 150.0f, 0.0f, new int[]{Theme.getColor(Theme.key_premiumGradient1), Theme.getColor(Theme.key_premiumGradient2), Theme.getColor(Theme.key_premiumGradient3), Theme.getColor(Theme.key_premiumGradient4)}, new float[]{0.0f, 0.5f, 0.78f, 1.0f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0.0f, 0.0f, 100.0f, 100.0f, paint);
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(3553, iArr[0]);
        GLES20.glTexParameteri(3553, 10241, 9728);
        GLES20.glTexParameteri(3553, 10240, 9728);
        GLUtils.texImage2D(3553, 0, this.texture, 0);
        this.mTextureDataHandle = iArr[0];
    }

    public static Bitmap getBitmapFromAsset(Context context, String str) {
        try {
            return BitmapFactory.decodeStream(context.getAssets().open(str));
        } catch (IOException unused) {
            return null;
        }
    }

    private void init(Context context) {
        Bitmap bitmap;
        String str;
        GLES20.glUseProgram(this.mProgramObject);
        this.mVerticesHandle = GLES20.glGetAttribLocation(this.mProgramObject, "vPosition");
        this.mTextureCoordinateHandle = GLES20.glGetAttribLocation(this.mProgramObject, "a_TexCoordinate");
        this.mNormalCoordinateHandle = GLES20.glGetAttribLocation(this.mProgramObject, "a_Normal");
        this.mTextureUniformHandle = GLES20.glGetUniformLocation(this.mProgramObject, "u_Texture");
        this.mNormalMapUniformHandle = GLES20.glGetUniformLocation(this.mProgramObject, "u_NormalMap");
        this.mBackgroundTextureUniformHandle = GLES20.glGetUniformLocation(this.mProgramObject, "u_BackgroundTexture");
        this.xOffsetHandle = GLES20.glGetUniformLocation(this.mProgramObject, "f_xOffset");
        this.alphaHandle = GLES20.glGetUniformLocation(this.mProgramObject, "f_alpha");
        this.mMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgramObject, "uMVPMatrix");
        this.mWorldMatrixHandle = GLES20.glGetUniformLocation(this.mProgramObject, "world");
        this.whiteHandle = GLES20.glGetUniformLocation(this.mProgramObject, "white");
        this.goldenHandle = GLES20.glGetUniformLocation(this.mProgramObject, "golden");
        this.specHandleTop = GLES20.glGetUniformLocation(this.mProgramObject, "spec1");
        this.specHandleBottom = GLES20.glGetUniformLocation(this.mProgramObject, "spec2");
        this.diffuseHandle = GLES20.glGetUniformLocation(this.mProgramObject, "u_diffuse");
        this.gradientColor1Handle = GLES20.glGetUniformLocation(this.mProgramObject, "gradientColor1");
        this.gradientColor2Handle = GLES20.glGetUniformLocation(this.mProgramObject, "gradientColor2");
        this.normalSpecColorHandle = GLES20.glGetUniformLocation(this.mProgramObject, "normalSpecColor");
        this.normalSpecHandle = GLES20.glGetUniformLocation(this.mProgramObject, "normalSpec");
        this.specColorHandle = GLES20.glGetUniformLocation(this.mProgramObject, "specColor");
        this.resolutionHandle = GLES20.glGetUniformLocation(this.mProgramObject, "resolution");
        this.gradientPositionHandle = GLES20.glGetUniformLocation(this.mProgramObject, "gradientPosition");
        this.modelIndexHandle = GLES20.glGetUniformLocation(this.mProgramObject, "modelIndex");
        this.typeHandle = GLES20.glGetUniformLocation(this.mProgramObject, "type");
        this.nightHandle = GLES20.glGetUniformLocation(this.mProgramObject, "night");
        this.timeHandle = GLES20.glGetUniformLocation(this.mProgramObject, "time");
        int i = this.N * 3;
        int[] iArr = new int[i];
        this.buffers = iArr;
        GLES20.glGenBuffers(i, iArr, 0);
        for (int i2 = 0; i2 < this.N; i2++) {
            int i3 = i2 * 3;
            GLES20.glBindBuffer(34962, this.buffers[i3]);
            this.mTextures[i2].position(0);
            GLES20.glBufferData(34962, this.mTextures[i2].capacity() * 4, this.mTextures[i2], 35044);
            GLES20.glEnableVertexAttribArray(this.mTextureCoordinateHandle);
            this.mTextures[i2].clear();
            GLES20.glBindBuffer(34962, this.buffers[i3 + 1]);
            this.mNormals[i2].position(0);
            GLES20.glBufferData(34962, this.mNormals[i2].capacity() * 4, this.mNormals[i2], 35044);
            GLES20.glEnableVertexAttribArray(this.mNormalCoordinateHandle);
            this.mNormals[i2].clear();
            GLES20.glBindBuffer(34962, this.buffers[i3 + 2]);
            this.mVertices[i2].position(0);
            GLES20.glBufferData(34962, this.mVertices[i2].capacity() * 4, this.mVertices[i2], 35044);
            GLES20.glEnableVertexAttribArray(this.mVerticesHandle);
            this.mVertices[i2].clear();
        }
        GLES20.glBindBuffer(34962, 0);
        int[] iArr2 = new int[1];
        GLES20.glGenTextures(1, iArr2, 0);
        int i4 = iArr2[0];
        this.mTextureDataHandle = i4;
        GLES20.glBindTexture(3553, i4);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glBindTexture(3553, this.mTextureDataHandle);
        Bitmap bitmapFromAsset = getBitmapFromAsset(context, "flecks.png");
        int[] iArr3 = new int[1];
        GLES20.glGenTextures(1, iArr3, 0);
        GLES20.glBindTexture(3553, iArr3[0]);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLUtils.texImage2D(3553, 0, bitmapFromAsset, 0);
        bitmapFromAsset.recycle();
        int[] iArr4 = new int[1];
        GLES20.glGenTextures(1, iArr4, 0);
        int i5 = iArr4[0];
        this.mBackgroundTextureHandle = i5;
        GLES20.glBindTexture(3553, i5);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glBindTexture(3553, this.mBackgroundTextureHandle);
        int i6 = this.type;
        if (i6 == 0 || i6 == 2) {
            bitmap = SvgHelper.getBitmap(R.raw.start_texture, 240, 240, -1);
        } else {
            if (i6 == 1) {
                str = "models/coin_border.png";
            } else if (i6 == 3) {
                str = "models/deal_border.png";
            } else {
                bitmap = null;
            }
            bitmap = getBitmapFromAsset(context, str);
        }
        if (bitmap != null) {
            int[] iArr5 = new int[1];
            GLES20.glGenTextures(1, iArr5, 0);
            GLES20.glBindTexture(3553, iArr5[0]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLUtils.texImage2D(3553, 0, bitmap, 0);
            bitmap.recycle();
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, iArr5[0]);
            GLES20.glUniform1i(this.mTextureUniformHandle, 0);
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, iArr3[0]);
            GLES20.glUniform1i(this.mNormalMapUniformHandle, 1);
        }
        GLES20.glActiveTexture(33986);
        GLES20.glBindTexture(3553, iArr4[0]);
        GLES20.glUniform1i(this.mBackgroundTextureUniformHandle, 2);
    }

    public void destroy() {
        GLES20.glDeleteProgram(this.mProgramObject);
    }

    public void draw(float[] fArr, float[] fArr2, int i, int i2, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        if (this.backgroundBitmap != null) {
            GLES20.glBindTexture(3553, this.mBackgroundTextureHandle);
            GLUtils.texImage2D(3553, 0, this.backgroundBitmap, 0);
            this.backgroundBitmap = null;
        }
        GLES20.glUniform1i(this.mTextureUniformHandle, 0);
        GLES20.glUniform1f(this.xOffsetHandle, this.xOffset);
        GLES20.glUniform1f(this.alphaHandle, this.enterAlpha);
        GLES20.glUniform1f(this.whiteHandle, f5);
        GLES20.glUniform1f(this.goldenHandle, f6);
        GLES20.glUniformMatrix4fv(this.mMVPMatrixHandle, 1, false, fArr, 0);
        GLES20.glUniformMatrix4fv(this.mWorldMatrixHandle, 1, false, fArr2, 0);
        GLES20.glUniform1f(this.specHandleTop, this.spec1);
        GLES20.glUniform1f(this.specHandleBottom, this.spec2);
        GLES20.glUniform1f(this.diffuseHandle, this.diffuse);
        GLES20.glUniform1f(this.normalSpecHandle, this.normalSpec);
        GLES20.glUniform3f(this.gradientColor1Handle, Color.red(this.gradientColor1) / 255.0f, Color.green(this.gradientColor1) / 255.0f, Color.blue(this.gradientColor1) / 255.0f);
        GLES20.glUniform3f(this.gradientColor2Handle, Color.red(this.gradientColor2) / 255.0f, Color.green(this.gradientColor2) / 255.0f, Color.blue(this.gradientColor2) / 255.0f);
        GLES20.glUniform3f(this.normalSpecColorHandle, Color.red(this.normalSpecColor) / 255.0f, Color.green(this.normalSpecColor) / 255.0f, Color.blue(this.normalSpecColor) / 255.0f);
        GLES20.glUniform3f(this.specColorHandle, Color.red(this.specColor) / 255.0f, Color.green(this.specColor) / 255.0f, Color.blue(this.specColor) / 255.0f);
        GLES20.glUniform2f(this.resolutionHandle, i, i2);
        GLES20.glUniform4f(this.gradientPositionHandle, f, f2, f3, f4);
        GLES20.glUniform1i(this.nightHandle, this.night ? 1 : 0);
        float f8 = this.time + f7;
        this.time = f8;
        GLES20.glUniform1f(this.timeHandle, f8);
        for (int i3 = 0; i3 < this.N; i3++) {
            int i4 = i3 * 3;
            GLES20.glBindBuffer(34962, this.buffers[i4]);
            GLES20.glVertexAttribPointer(this.mTextureCoordinateHandle, 2, 5126, false, 0, 0);
            GLES20.glBindBuffer(34962, this.buffers[i4 + 1]);
            GLES20.glVertexAttribPointer(this.mNormalCoordinateHandle, 3, 5126, false, 0, 0);
            GLES20.glBindBuffer(34962, this.buffers[i4 + 2]);
            GLES20.glVertexAttribPointer(this.mVerticesHandle, 3, 5126, false, 0, 0);
            GLES20.glUniform1i(this.modelIndexHandle, i3);
            GLES20.glUniform1i(this.typeHandle, this.type);
            GLES20.glDrawArrays(4, 0, this.trianglesCount[i3] / 3);
        }
        float f9 = this.enterAlpha;
        if (f9 < 1.0f) {
            float f10 = f9 + 0.07272727f;
            this.enterAlpha = f10;
            if (f10 > 1.0f) {
                this.enterAlpha = 1.0f;
            }
        }
        float f11 = this.xOffset + 5.0E-4f;
        this.xOffset = f11;
        if (f11 > 1.0f) {
            this.xOffset = f11 - 1.0f;
        }
    }

    public String loadFromAsset(Context context, String str) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream open = context.getAssets().open(str);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(open, StandardCharsets.UTF_8));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                sb.append(readLine);
                sb.append("\n");
            }
            bufferedReader.close();
            open.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void setBackground(Bitmap bitmap) {
        this.backgroundBitmap = bitmap;
    }
}

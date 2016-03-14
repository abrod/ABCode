package de.brod.carddealer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class GLProgram {
	private static final String U_MVPMATRIX = "u_MVPMatrix";
	private static final String A_POSITION = "a_Position";
	private static final String A_COLOR = "a_color";
	private static final String V_COLOR = "v_Color";

	private static final String vertexShader;
	private static final String fragmentShader;

	/** This will be used to pass in the transformation matrix. */
	private static int u_MVPMatrix_id;

	/** This will be used to pass in model position information. */
	private static int a_position_id;

	/** This will be used to pass in model color information. */
	private static int a_color_id;

	static {
		StringBuilder sb = new StringBuilder();
		sb.append("uniform mat4 ").append(U_MVPMATRIX).append(";");
		sb.append("attribute vec4 ").append(A_POSITION).append(";");
		sb.append("attribute vec4 ").append(A_COLOR).append(";");
		sb.append("varying vec4 ").append(V_COLOR).append(";");
		sb.append("void main()");
		sb.append("{");
		sb.append(V_COLOR).append(" = ").append(A_COLOR).append(";");
		sb.append("gl_Position = ").append(U_MVPMATRIX).append(" * ").append(A_POSITION).append(";");
		sb.append("}");
		vertexShader = sb.toString();

		sb.setLength(0);
		sb.append("precision mediump float;");
		sb.append("varying vec4 ").append(V_COLOR).append(";");
		sb.append("void main()");
		sb.append("{");
		sb.append("gl_FragColor = ").append(V_COLOR).append(";");
		sb.append("}");
		fragmentShader = sb.toString();
	}

	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	private float[] mMVPMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix
	 * transforms world space to eye space; it positions things relative to our
	 * eye.
	 */
	private float[] mViewMatrix = new float[16];
	/**
	 * Store the projection matrix. This is used to project the scene onto a 2D
	 * viewport.
	 */
	private float[] mProjectionMatrix = new float[16];

	public GLProgram(float eyeX, float eyeY, float eyeZ, float lookX, float lookY, float lookZ, float upX, float upY,
			float upZ) {

		// Set the view matrix. This matrix can be said to represent the camera
		// position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination
		// of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices
		// separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

		int vertextHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		int fragmentHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

		// Create a program object and store the handle to it.
		int programHandle = GLES20.glCreateProgram();

		if (programHandle != 0) {

			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertextHandle);

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentHandle);

			// Bind attributes
			GLES20.glBindAttribLocation(programHandle, 0, A_POSITION);
			GLES20.glBindAttribLocation(programHandle, 1, A_COLOR);

			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) {
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}

		if (programHandle == 0) {
			throw new RuntimeException("Error creating program.");
		}

		// Set program handles. These will later be used to pass in values to
		// the program.
		u_MVPMatrix_id = GLES20.glGetUniformLocation(programHandle, U_MVPMATRIX);
		a_position_id = GLES20.glGetAttribLocation(programHandle, A_POSITION);
		a_color_id = GLES20.glGetAttribLocation(programHandle, A_COLOR);

		// Tell OpenGL to use this program when rendering.
		GLES20.glUseProgram(programHandle);
	}

	private int compileShader(int glFragmentShader, String source) {

		int shaderHandle = GLES20.glCreateShader(glFragmentShader);

		if (shaderHandle != 0) {
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, source);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) {
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0) {
			throw new RuntimeException("Error creating shader.");
		}
		return shaderHandle;
	}

	/**
	 * Draws a triangle from the given vertex data.
	 *
	 * @param aTriangleBuffer
	 *            The buffer containing the vertex data.
	 */
	public void drawTriangle(final Vertice aTriangleBuffer) {

		// set the VertexAttributes
		aTriangleBuffer.enableVertexAttributes(a_position_id, a_color_id);

		// This multiplies the view matrix by the model matrix, and stores the
		// result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, aTriangleBuffer.getModelMatrix(), 0);

		// This multiplies the modelview matrix by the projection matrix, and
		// stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

		GLES20.glUniformMatrix4fv(u_MVPMatrix_id, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
	}

	void setProjectionMatrix(float left, float right, float bottom, float top, float near, float far) {
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}
}

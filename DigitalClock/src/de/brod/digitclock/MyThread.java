package de.brod.digitclock;
import android.graphics.*;
import android.view.*;
import android.content.*;

public class MyThread extends Thread

{

	boolean mRun;

	Canvas mcanvas;

	SurfaceHolder surfaceHolder;

	Context context;

	MyView msurfacePanel;



	public MyThread(SurfaceHolder sholder, Context ctx, MyView spanel)

	{

		surfaceHolder = sholder;

		context = ctx;

		mRun = false;

		msurfacePanel = spanel;

	}



	void setRunning(boolean bRun)

	{

		mRun = bRun;

	}



	@Override
	
	public void run()

	{

		super.run();

		while (mRun)

		{
			repaint();
			
			msurfacePanel.sleep();
		}

	}

	synchronized void repaint()
	{
		mcanvas = surfaceHolder.lockCanvas();

		if (mcanvas != null)

		{

			msurfacePanel.doDraw(mcanvas);

			surfaceHolder.unlockCanvasAndPost(mcanvas);

		}
		
	}

}

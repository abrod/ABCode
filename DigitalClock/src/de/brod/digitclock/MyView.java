package de.brod.digitclock;
import android.view.*;
import android.content.*;
import android.graphics.*;
import android.util.*;

public abstract class MyView extends SurfaceView 
implements SurfaceHolder.Callback,View.OnTouchListener
{


	private Context context;

	private SurfaceHolder holder;

	private MyThread mythread;

	int wd;

	int hg;

	public MyView(Context ctx)
	{

		super(ctx);

		context = ctx;

		holder = getHolder();

		holder.addCallback(this);
		setOnTouchListener(this);

	}

	abstract void sleep();

	@Override

	public void surfaceCreated(SurfaceHolder holder)

	{

		mythread = new MyThread(holder, context,this);

		mythread.setRunning(true);

		mythread.start();

	}



	@Override

	public void surfaceDestroyed(SurfaceHolder holder)

	{

		mythread.setRunning(false);

		boolean retry = true;

		while(retry)

		{

			try

			{

				mythread.join();

				retry = false;

			}

			catch(Exception e)

			{

				Log.v("Exception Occured", e.getMessage());

			}

		}

	}


	@Override

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height)
	{
		wd=width;
		hg=height;
		setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	}


	abstract void doDraw(Canvas canvas);

	@Override
	public boolean onTouch(View p1, MotionEvent event)
	{
		// get pointer index from the event object
		int pointerIndex = event.getActionIndex();

		// get pointer ID
		int pointerId = event.getPointerId(pointerIndex);

		// get masked (not specific to a pointer) action
		int maskedAction = event.getActionMasked();

		switch (maskedAction) {

			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN: {
					// TODO use data
					mouseDown(event.getX(),event.getY());
					break;
				}
			case MotionEvent.ACTION_MOVE: { // a pointer was moved
					// TODO use data
					mouseMove(event.getX(),event.getY());
					break;
				}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL: {
					// TODO use data
					mouseUp(event.getX(),event.getY());
					break;
				}
		}
		invalidate();

		return true;
	}
	
	public void repaint(){
		mythread.repaint();
	}

	abstract void mouseUp(float x, float y);

	abstract void mouseMove(float x, float y);

	abstract void mouseDown(float x, float y);
	
}

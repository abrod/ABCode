package de.brod.digitclock;
import android.graphics.*;
import android.content.*;
import java.text.*;
import java.util.*;
import android.view.*;

public class ClockView extends MyView
{

	private int r=0,g=0,b=192;

	@Override
	void mouseUp(float x, float y)
	{
		// TODO: Implement this method
	}

	@Override
	void mouseMove(float x, float y)
	{
		mouseDown(x,y);
	}

	@Override
	void mouseDown(float x, float y)
	{
		float col=x / wd * 255;
		float dy=y*3f/hg+0.5f;
		if (dy<1)
			b=(int)(col*Math.max(0,dy));
		else
			b=(int)(col*Math.max(0,2-dy));
		if (dy<2)
			r=(int)(col*Math.max(0,dy-1));
		else
			r=(int)(col*Math.max(0,3-dy));
		if (dy<3)
			g=(int)(col*Math.max(0,dy-2));
		else
			g=(int)(col*Math.max(0,4-dy));
		
		//	r=0;
		//	b=0;
		repaint();
		
	}




	@Override
	void sleep()
	{
		try
		{
			long l=	1001-System.currentTimeMillis() % 1000;
			Thread.sleep(l);
			
		}
		catch (InterruptedException e)
		{
			// stop
		}
	}


	public ClockView(Context ctx)
	{
		super(ctx);
	}
	
	@Override
	void doDraw(Canvas canvas)
	{
		
		canvas.drawColor(Color.BLACK);

		// canvas.drawLine(0, 0, wd, hg, paint);
		String s=new SimpleDateFormat("HHmmss").format(new Date());
		int w=wd/(s.length()+1);
		Paint[] paint = new Paint[Math.max(2,w/8)];
		for(int i=0;i<paint.length;i++){
			float size=(paint.length-i)*1f/paint.length;
			paint[i]= new Paint();
			paint[i].setColor(Color.argb(255,(int)(r*size),(int)(g*size),(int)(b*size)));
		}
		
		int w2=(int)(w*0.7);
		int h=Math.min(w2*2,hg);
		for(int i=0;i<s.length();i++){
			draw(canvas,w2,h,(int)((i+i/2*0.5f)*w),(hg-h)/2,s.charAt(i),paint);
		}
		// canvas.drawText(s,0,hg/2,paint);
	}

	private void draw(Canvas c, int w, int h, int x, int y,char ch,Paint[] p)
	{
		// TODO: Implement this method
		if( "23567890".indexOf(ch)>=0){
			drawLine(c,x,y,x+w,y,p);
		}
		if( "456890".indexOf(ch)>=0){
			drawLine(c,x,y,x,y+h/2,p);
		}
		if( "12347890".indexOf(ch)>=0){
			drawLine(c,x+w,y,x+w,y+h/2,p);
		}
		if( "2345689".indexOf(ch)>=0){
			drawLine(c,x,y+h/2,x+w,y+h/2,p);
		}
		if( "2680".indexOf(ch)>=0){
			drawLine(c,x,y+h/2,x,y+h,p);
		}
		if( "134567890".indexOf(ch)>=0){
			drawLine(c,x+w,y+h/2,x+w,y+h,p);
		}
		if( "2356890".indexOf(ch)>=0){
			drawLine(c,x,y+h,x+w,y+h,p);
		}
	}

	private void drawLine(Canvas c, int x, int y, int x2, int y2, Paint[] p)
	{
		int l=p.length-1;
		if (x==x2){
			y++;
			for (int i=-l;i<=l;i++)
				c.drawLine(x+i,y+Math.abs(i),x2+i,y2-Math.abs(i),p[Math.abs(i)]);
		} else {
			x++;
			for (int i=-l;i<=l;i++)
				c.drawLine(x+Math.abs(i),y+i,x2-Math.abs(i),y2+i,p[Math.abs(i)]);
		}
		
	}

}

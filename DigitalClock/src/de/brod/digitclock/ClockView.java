package de.brod.digitclock;
import android.graphics.*;
import android.content.*;
import java.text.*;
import java.util.*;
import android.view.*;

public class ClockView extends MyView
{

	private int r=0,g=0,b=192;

	private int w;

	private int w2;

	private int h;

	private int maxx;

	private int maxy;

	private int len;

	private int minx;

	private int miny;

	private int x,dx;

	private int y,dy;

	private int down=-1;

	@Override
	void mouseUp(float x, float y)
	{
		down=-1;
		repaint();
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
		
		b=blue(y,col);
		g=green(y,col);
		r=red(y,col);
		
		down=(int)(x);
		repaint();
		
	}

	private int red(float y, float col)
	{
		float dy=y*3f/hg+0.5f;
		int r;
		if (dy<2)
			r=(int)(col*Math.max(0,dy-1));
		else
			r=(int)(col*Math.max(0,3-dy));
	
		return r;
	}

	private int green(float y, float col)
	{
		float dy=y*3f/hg+0.5f;
		int g;
		if (dy<3)
			g=(int)(col*Math.max(0,dy-2));
		else
			g=(int)(col*Math.max(0,4-dy));

		if (dy<1)
			g=(int)(col*Math.max(0,1-dy));
		return g;
	}

	private int blue(float y,float col)
	{
		float dy=y*3f/hg+0.5f;
		int b;
		if (dy<1)
			b=(int)(col*Math.max(0,dy));
		else
			b=(int)(col*Math.max(0,2-dy));

		if (dy>3)
			b=(int)(col*Math.max(0,dy-3));
		return b;
	}




	@Override
	void sleep()
	{
		try
		{
			long l=	999-System.currentTimeMillis() % 1000;
			Thread.sleep(l+10);
			
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
		// s="888888";
		if(w==0){
			w=wd/(s.length()+2);
			w2=(int)(w*0.7);
			h=Math.min(w2*2,hg*7/8);
			maxx=(wd-w*6-w2);
			maxy=hg-h;
			len=Math.max(2,w/8);
			maxy-=len;
			maxx-=len;
			minx=len;
			miny=len;
			x=minx+(int)((maxx-minx)*Math.random());
			y=miny+(int)((maxy-miny)*Math.random());
			dx=1;
			dy=1;
		}
		if (x+dx>maxx)
			dx=-1;
		else if (x+dx<minx)
			dx=1;
		x+=dx;
		if (y+dy>maxy)
			dy=-1;
		else if (y+dy<miny)
			dy=1;
		y+=dy;
		
		Paint[] paint = new Paint[len];
		for(int i=0;i<paint.length;i++){
			float size=(paint.length-i)*1f/paint.length;
			paint[i]= new Paint();
			paint[i].setColor(Color.argb(255,(int)(r*size),(int)(g*size),(int)(b*size)));
		}
		
		if(down>0){
			Paint p = new Paint();
			float col=down *255/ wd ;
			for (int i=0;i<hg;i++){
				p.setColor(Color.argb(255,red(i,col),
					green(i,col),blue(i,col)));
				canvas.drawLine(down-len, i, down+len, i, p);
			}
		}
		
		for(int i=0;i<s.length();i++){
			draw(canvas,w2,h,(int)((i+(i/2)*0.5f)*w)+x,y,s.charAt(i),paint);
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

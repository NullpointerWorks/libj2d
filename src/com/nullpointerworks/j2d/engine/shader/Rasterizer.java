package com.nullpointerworks.j2d.engine.shader;

import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.core.buffer.concurrency.SyncIntBuffer;
import com.nullpointerworks.j2d.Request;
import com.nullpointerworks.math.geometry.g2d.Rectangle;
import com.nullpointerworks.util.pack.Array;

public class Rasterizer implements Runnable
{
	private Array<Request> l;
	private SyncIntBuffer s;
	private SyncIntBuffer d;
	
	public Rasterizer(Array<Request> l, SyncIntBuffer s, SyncIntBuffer d)
	{
		this.l = l;
		this.s = s;
		this.d = d;
	}
	
	@Override
	public void run() 
	{
		int[] dpx 	= d.content();
		int[] spx 	= s.content();
		int DEST_W 	= s.getWidth();
		int DEST_H 	= s.getHeight();
		
		/*
		 * images are sorted in ascending order. begin drawing from the 
		 */
		for (int leng=l.size()-1; leng>=0; leng--)
		{
			Request di = l.get(leng);
			draw(di, dpx, spx, DEST_W, DEST_H);
		}
	}
	
	public void draw(Request dr, 
					 int[] depthPX, 
					 int[] screenPX,
					 int DEST_W,
					 int DEST_H) 
	{
		IntBuffer source 	= dr.image;
		Rectangle aabb 		= dr.aabb;
		float[][] matrix 	= dr.transform;
		
		int OFF_X=0, OFF_Y=0;
		int SOURCE_W 	= source.getWidth();
		int SOURCE_H 	= source.getHeight();
		int tx 			= rnd(aabb.x);
		int ty 			= rnd(aabb.y);
		int AABB_W 		= rnd(aabb.w);
		int AABB_H 		= rnd(aabb.h);
		int layer		= dr.layer;
		int[] sourcePX 	= source.content();
		
		// edge clipping
		AABB_W += (tx+AABB_W > DEST_W)? (DEST_W-(tx+AABB_W)) :0;
		AABB_H += (ty+AABB_H > DEST_H)? (DEST_H-(ty+AABB_H)) :0;
		OFF_X += (tx<0)?(-tx):0;
		OFF_Y += (ty<0)?(-ty):0;
		
		// loop through the pixels in the AABB
		for (int j=OFF_Y, k=AABB_H; j<k; j++)
		{
			int stride = tx+(ty+j)*DEST_W;
			
			for (int i=OFF_X, l=AABB_W; i<l; i++)
			{
				float[] v = {i,j};
				transform(matrix, v);
				
				// check image clipping
				if (v[0] < 0f) continue;
				int x = rnd(v[0]);
				if (x >= SOURCE_W) continue;
				
				if (v[1] < 0f) continue;
				int y = rnd(v[1]);
				if (y >= SOURCE_H) continue;
				
				// check layering
				int indexS = i + stride;
				int lay = depthPX[indexS];
				if (layer < lay) continue;
				
				// get image color
				int indexP = x + y*SOURCE_W;
				int sourceCol = sourcePX[indexP];
				
				// if image pixel is 100% transparent, skip draw
				int alpha = (sourceCol>>24) & 0xFF;
				if (alpha == 0) continue;
				
				// plot color
				// only blend when alpha is less than 255
				// add 1 to 255 alpha value to reach 256
				if (alpha < 255) 
				{
					// get background color
					int screenCol = screenPX[indexS];
					sourceCol = lerp256(screenCol, sourceCol, alpha+1);
				}
				screenPX[indexS] = sourceCol; // sync plotting
			}
		}
	}
	
	/*
	 * JVM optimizes this function
	 */
	protected final void transform(float[][] m, float[] v)
	{
		float vx,vy;
		float[] mp = m[0];
		vx = mp[0]*v[0] + mp[1]*v[1] + mp[2];
		mp = m[1];
		vy = mp[0]*v[0] + mp[1]*v[1] + mp[2];
		v[0] = vx;
		v[1] = vy;
	}
	
	/**
	 * integer interpolation of 32bit colors
	 */
	public int lerp256(int c1, int c2, int lerp) 
	{
		int ag1 = c1 & 0xFF00FF00;
		int ag2 = c2 & 0xFF00FF00;
		int rb1 = c1 & 0x00FF00FF;
		int rb2 = c2 & 0x00FF00FF;
		int l 	= (256-lerp);
		ag1 = ag1*l + ag2*lerp;
		rb1 = rb1*l + rb2*lerp;
		ag1 = ag1>>8;
		rb1 = rb1>>8;
	    return (ag1 & 0xFF00FF00) + (rb1 & 0x00FF00FF);
	}
	
	protected final int rnd(float x)
	{
		return (int)(x+0.5f);
	}
}

/*
 * Creative Commons - Attribution, Share Alike 4.0 
 * Nullpointer Works (2019)
 * Use is subject to license terms.
 */
package com.nullpointerworks.j2d.engine.shader;

import java.util.List;

import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.j2d.BufferedRequest;
import com.nullpointerworks.math.geometry.g2d.Rectangle;

/**
 * 
 * @since 1.0.0
 * @author Michiel Drost - Nullpointer Works
 */
public class Rasterizer extends ShaderMath implements Runnable
{
	private List<BufferedRequest> l;
	private IntBuffer s;
	private IntBuffer d;
	private float a;
	
	/**
	 * 
	 * @since 1.0.0
	 */
	public Rasterizer(List<BufferedRequest> l, IntBuffer s, IntBuffer d, float a)
	{
		this.l = l;
		this.s = s;
		this.d = d;
		this.a = a;
	}
	
	/**
	 * 
	 * @since 1.0.0
	 */
	public void accuracy(float a)
	{
		this.a = a;
	}
	
	@Override
	public void run() 
	{
		int[] dpx 	= d.content();
		int[] spx 	= s.content();
		int DEST_W 	= s.getWidth();
		int DEST_H 	= s.getHeight();
		
		/*
		 * images are sorted in ascending order, and layering has been applied. begin drawing from the last, most far away image
		 */
		for (int leng=l.size()-1; leng>=0; leng--)
		{
			BufferedRequest di = l.get(leng);
			draw(di, dpx, spx, DEST_W, DEST_H);
		}
	}
	
	/**
	 * 
	 * @since 1.0.0
	 */
	public void draw(BufferedRequest dr, 
					 int[] depthPX, 
					 int[] screenPX,
					 int DEST_W,
					 int DEST_H) 
	{
		IntBuffer source 	= dr.image;
		Rectangle aabb 		= dr.aabb;
		float[][] matrix 	= dr.transform;
		int layer			= dr.layer;
		
		int SOURCE_W 	= source.getWidth();
		int SOURCE_H 	= source.getHeight();
		int[] sourcePX 	= source.content();
		
		float startx 	= aabb.x - 1f;
		float endx 		= aabb.w + aabb.x;
		float starty 	= aabb.y - 1f;
		float endy 		= aabb.h + aabb.y;

		// screen edge clipping
		startx = (startx < -0.5)?-0.5f: startx;
		starty = (starty < -0.5)?-0.5f: starty;
		endx = (endx >= DEST_W)? DEST_W-1: endx;
		endy = (endy >= DEST_H)? DEST_H-1: endy;
		
		for (float j=starty, k=endy; j<k; j+=a)
		{
			for (float i=startx, l=endx; i<l; i+=a)
			{				
				float[] v = {i,j};
				transform(matrix, v);
				
				if (v[0] < -0.5f) continue;
				int x = rnd(v[0]);
				if (x >= SOURCE_W) continue;
				
				if (v[1] < -0.5f) continue;
				int y = rnd(v[1]);
				if (y >= SOURCE_H) continue;
				
				int plotx = rnd(i);
				int ploty = rnd(j);
				int STRIDE = plotx + ploty*DEST_W;

				int lay = depthPX[STRIDE];
				if (layer < lay) continue;
				
				int color = sourcePX[x + y*SOURCE_W];
				int alpha = (color>>24) & 0xFF;
				if (alpha == 0) continue;
				
				if (alpha < 255) 
				{
					int screenCol = screenPX[STRIDE];
					color = lerp256(screenCol, color, alpha+1);
				}
				screenPX[STRIDE] = color;
			}
		}
	}
}

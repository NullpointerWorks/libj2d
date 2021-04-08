/*
 * This is free and unencumbered software released into the public domain.
 * (http://unlicense.org/)
 * Nullpointer Works (2021)
 */
package com.nullpointerworks.j2d.shader;

import java.util.List;

import com.nullpointerworks.core.buffer.IntBuffer;

/**
 * This is the third and final shader in the J2D engine. This shader performs a depth test and plots pixels. Pixel with transparency are blended with the rendered background.
 * @since 1.0.0
 * @author Michiel Drost - Nullpointer Works
 */
public class Rasterizer extends ShaderMath implements Runnable
{
	private List<BufferedRequest> l;
	private float a;
	private int[] dpx;
	private int[] spx;
	private int DEST_W;
	private int DEST_H;
	
	/**
	 * Instantiates a rasterizer shader used in the J2D engine.
	 * @param l - rendering queue
	 * @param s - rendering frame
	 * @param d - depth buffer
	 * @param a - engine rasterizer accuracy
	 * @since 1.0.0
	 */
	public Rasterizer(List<BufferedRequest> l, IntBuffer s, IntBuffer d, float a)
	{
		this.l = l;
		accuracy(a);
		dpx 	= d.content();
		spx 	= s.content();
		DEST_W 	= s.getWidth();
		DEST_H 	= s.getHeight();
	}
	
	/**
	 * Sets the pixel accuracy for rasterizer precision. The accuracy value is proportional to performance, but inversely proportional to rendering precision. A high accuracy value results in lower rendering precision, but also improves rendering performance. The accuracy domain is {@code 0 < x <= 1}. 
	 * @param acc - the rendering accuracy
	 * @since 1.0.0
	 */
	public void accuracy(float acc)
	{
		this.a = acc;
	}
	
	@Override
	public void run() 
	{
		/*
		 * The layering shader plots the depth buffer in ascending order(painter's algorithm). The rasterizer draws in the opposite order from back to front.
		 */
		for (int leng=l.size()-1; leng>=0; leng--)
		{
			BufferedRequest di = l.get(leng);
			draw(di, dpx, spx, DEST_W, DEST_H);
		}
	}
	
	/**
	 * @since 1.0.0
	 */
	private void draw(BufferedRequest dr, 
					 int[] depthPX, 
					 int[] screenPX,
					 int DEST_W,
					 int DEST_H) 
	{
		IntBuffer source 	= dr.image;
		float[][] matrix 	= dr.transform;
		int layer			= dr.layer;
		
		int SOURCE_W 	= source.getWidth();
		int SOURCE_H 	= source.getHeight();
		int[] sourcePX 	= source.content();
		
		// screen edge clipping
		float startx 	= dr.x;
		float endx 		= dr.w;
		float starty 	= dr.y;
		float endy 		= dr.h;
		startx = (startx < 0f)?0f: startx;
		starty = (starty < 0f)?0f: starty;
		endx = (endx >= DEST_W)? DEST_W-1: endx;
		endy = (endy >= DEST_H)? DEST_H-1: endy;
		
		for (float j=starty, k=endy; j<k; j+=a)
		{
			for (float i=startx, l=endx; i<l; i+=a)
			{				
				float[] v = {i,j};
				transform(matrix, v);
				
				if (v[0] < 0f) continue;
				if (v[1] < 0f) continue;
				
				int x = (int)(v[0]);
				if (x >= SOURCE_W) continue;
				
				int y = (int)(v[1]);
				if (y >= SOURCE_H) continue;
				
				int plotx = (int)(i);
				int ploty = (int)(j);
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

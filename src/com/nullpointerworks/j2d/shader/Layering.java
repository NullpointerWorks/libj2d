/*
 * This is free and unencumbered software released into the public domain.
 * (http://unlicense.org/)
 * Nullpointer Works (2021)
 */
package com.nullpointerworks.j2d.shader;

import java.util.List;

import com.nullpointerworks.core.buffer.IntBuffer;

/**
 * This is the second shader in the engine pipeline. This shader does a layer check and potentially culls requests from the rendering queue. 
 * @since 1.0.0
 * @author Michiel Drost - Nullpointer Works
 */
public class Layering extends ShaderMath implements Runnable
{
	private List<BufferedRequest> l;
	private IntBuffer d;
	
	/**
	 * Instantiates a layering shader for the J2D engine.
	 * @param l - engine's rendering queue
	 * @param d - engine's depth buffer
	 * @since 1.0.0
	 */
	public Layering(List<BufferedRequest> l, IntBuffer d)
	{
		this.l = l;
		this.d = d;
	}
	
	@Override
	public void run() 
	{
		int[] dPX 	= d.content();
		int DEST_W 	= d.getWidth();
		int DEST_H 	= d.getHeight();
		
		/*
		 * images are sorted on layer in ascending order. work our way back to
		 * detect obfuscated images. they are culled from rendering
		 */
		for (int leng=l.size()-1; leng>=0; leng--)
		{
			BufferedRequest di = l.get(leng);
			boolean c = draw(di, dPX, DEST_W, DEST_H);
			if (c) l.remove(leng); // cull obfuscated images
		}
	}
	
	/**
	 * 
	 * @since 1.0.0
	 */
	private boolean draw(BufferedRequest dr, 
						int[] depthPX,
						int DEST_W,
						int DEST_H) 
	{
		boolean cull		= true;	
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
		
		for (float j=starty, k=endy; j<k; j+=1f)
		{
			for (float i=startx, l=endx; i<l; i+=1f)
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
				
				int depth = depthPX[STRIDE];
				if (layer < depth) continue;
				
				int color = sourcePX[x + y*SOURCE_W];
				int alpha = (color>>24) & 0xFF;
				if (alpha == 255) continue;
				
				if (layer > depth) 
				{
					cull = false;
					depthPX[STRIDE] = layer;
				}
			}
		}
		return cull;
	}
}

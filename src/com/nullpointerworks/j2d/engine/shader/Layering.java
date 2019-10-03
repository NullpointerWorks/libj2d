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
public class Layering extends ShaderMath implements Runnable
{
	private List<BufferedRequest> l;
	private IntBuffer d;
	
	/**
	 * 
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
	public boolean draw(BufferedRequest dr, 
						int[] depthPX,
						int DEST_W,
						int DEST_H) 
	{
		boolean cull		= true;	
		IntBuffer source 	= dr.image;
		Rectangle aabb 		= dr.aabb;
		float[][] matrix 	= dr.transform;
		int layer			= dr.layer;
		
		int SOURCE_W 	= source.getWidth();
		int SOURCE_H 	= source.getHeight();
		int[] sourcePX 	= source.content();
		
		float startx 	= aabb.x - 1f;
		float endx 		= aabb.w + aabb.x + 1f;
		float starty 	= aabb.y - 1f;
		float endy 		= aabb.h + aabb.y + 1f;
		
		// screen edge clipping
		startx = (startx < -0.5)?-0.5f: startx;
		starty = (starty < -0.5)?-0.5f: starty;
		endx = (endx >= DEST_W)? DEST_W-1: endx;
		endy = (endy >= DEST_H)? DEST_H-1: endy;
		
		for (float j=starty, k=endy; j<k; j+=1f)
		{
			for (float i=startx, l=endx; i<l; i+=1f)
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

				int depth = depthPX[STRIDE];
				if (layer < depth) continue;
				
				int color = sourcePX[x + y*SOURCE_W];
				int alpha = (color>>24) & 0xFF;
				if (alpha==255) continue;
				
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

/*
 * Creative Commons - Attribution, Share Alike 4.0 
 * Nullpointer Works (2019)
 * Use is subject to license terms.
 */
package com.nullpointerworks.j2d.engine.shader;

import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.j2d.Request;
import com.nullpointerworks.math.geometry.g2d.Rectangle;
import com.nullpointerworks.util.pack.Array;

public class Layering implements Runnable
{
	private Array<Request> l;
	private IntBuffer d;
	
	public Layering(Array<Request> l, IntBuffer d)
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
			Request di = l.get(leng);
			boolean c = draw(di, dPX, DEST_W, DEST_H);
			if (c) l.remove(leng); // cull obfuscated images
		}
	}
	
	public boolean draw(Request dr, 
						 int[] depthPX,
						 int DEST_W,
						 int DEST_H) 
	{
		float[][] matrix 	= dr.transform;
		IntBuffer source 	= dr.image;
		Rectangle aabb 		= dr.aabb;
		int[] sourcePX 		= source.content();
		
		int OFF_X=0, OFF_Y=0;
		int SOURCE_W 	= source.getWidth();
		int SOURCE_H 	= source.getHeight();
		int tx 			= rnd(aabb.x);
		int ty 			= rnd(aabb.y);
		int BOUND_W 	= rnd(aabb.w);
		int BOUND_H 	= rnd(aabb.h);
		int layer		= dr.layer;
		boolean cull	= true;	
		
		// edge clipping
		BOUND_W += (tx+BOUND_W >= DEST_W)? (DEST_W-(tx+BOUND_W)) :0;
		BOUND_H += (ty+BOUND_H >= DEST_H)? (DEST_H-(ty+BOUND_H)) :0;
		OFF_X	+= (tx<0)?(-tx):0;
		OFF_Y	+= (ty<0)?(-ty):0;
		
		// loop through the pixels in the AABB
		for (int j=OFF_Y, k=BOUND_H; j<k; j++)
		{
			int stride = tx+(ty+j)*DEST_W;
			
			for (int i=OFF_X, l=BOUND_W; i<l; i++)
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
				
				// check layer values
				int indexD = i+stride;
				int depth = depthPX[indexD];
				if (layer < depth) continue;
				
				// check for alpha. all translucent pixels are skipped
				int indexP 	= x + y*SOURCE_W;
				int sourceCol = sourcePX[indexP];
				int alpha = (sourceCol>>24) & 0xFF;
				if (alpha!=255) continue;
				
				if (layer > depth) 
				{
					cull = false;
					depthPX[indexD] = layer;
				}
			}
		}
		return cull;
	}
	
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

	protected final int rnd(float x)
	{
		return (int)(x+0.5f);
	}
}

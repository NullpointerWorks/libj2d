/*
 * Creative Commons - Attribution, Share Alike 4.0 
 * Nullpointer Works (2019)
 * Use is subject to license terms.
 */
package com.nullpointerworks.j2d.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.j2d.BufferedRequest;
import com.nullpointerworks.j2d.Request;
import com.nullpointerworks.j2d.engine.shader.Layering;
import com.nullpointerworks.j2d.engine.shader.Rasterizer;
import com.nullpointerworks.j2d.engine.shader.Transform;

public class Engine 
{
	protected Transform transform;
	protected Layering layer;
	protected Rasterizer raster;
	
	private IntBuffer depth;
	private IntBuffer screen;
	private int[] depthPX, screenPX;
	private int CLEAR = 0xFF202020;
	private float accuracy = 0.49f;
	
	private List<Request> requests;
	private List<BufferedRequest> brequest;
	private Comparator<BufferedRequest> compare;
	
	public Engine(int width, int height)
	{
		depth 		= new IntBuffer(width, height);
		depthPX 	= depth.content();
		
		screen 		= new IntBuffer(width, height);
		screenPX 	= screen.content();
		
		requests 	= new ArrayList<Request>();
		brequest 	= new ArrayList<BufferedRequest>();
		compare 	= new Comparator<BufferedRequest>()
		{
			@Override
			public int compare(BufferedRequest o1, BufferedRequest o2) 
			{
				return o2.layer - o1.layer;
			}
		};
		
		transform 	= new Transform(requests, brequest);
		layer		= new Layering(brequest, depth);
		raster 		= new Rasterizer(brequest, screen, depth, accuracy);
	}
	
	public void accuracy(float acc)
	{
		accuracy = acc;
	}
	
	public void request(Request r)
	{
		requests.add(r);
	}
	
	public void generate()
	{
		clear(screenPX, CLEAR);
		clear(depthPX, 0);
		transform.run();
		brequest.sort(compare);
		layer.run();
		raster.run();
		clear();
	}
	
	private void clear()
	{
		for (int l=brequest.size()-1;l>=0; l--)
			brequest.get(l).free();
		requests.clear();
		brequest.clear();
	}
	
	public IntBuffer frame()
	{
		return screen;
	}
	
	public int[] content()
	{
		return screenPX;
	}

	protected void clear(int[] b, int c) 
	{
		for (int l=b.length-1; l>=0; l--) b[l]=c;
	}
}

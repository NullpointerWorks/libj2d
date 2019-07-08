package com.nullpointerworks.j2d.engine;

import java.util.Comparator;

import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.j2d.Request;
import com.nullpointerworks.j2d.engine.shader.Layering;
import com.nullpointerworks.j2d.engine.shader.Rasterizer;
import com.nullpointerworks.j2d.engine.shader.Transform;
import com.nullpointerworks.util.pack.Array;

public class Engine 
{
	protected Transform transform;
	protected Layering layer;
	protected Rasterizer raster;
	
	private IntBuffer depth;
	private IntBuffer screen;
	private int[] depthPX, screenPX;
	private int CLEAR = 0xFF202020;
	
	private Array<Request> requests;
	private Comparator<Request> compare;
	
	public Engine(int width, int height)
	{
		depth 		= new IntBuffer(width, height);
		depthPX 	= depth.content();
		
		screen 		= new IntBuffer(width, height);
		screenPX 	= screen.content();
		
		requests 	= new Array<Request>();
		compare 	= new Comparator<Request>()
		{
			@Override
			public int compare(Request o1, Request o2) 
			{
				return o2.layer - o1.layer;
			}
		};
		
		transform 	= new Transform(requests);
		layer		= new Layering(requests, depth);
		raster 		= new Rasterizer(requests, screen, depth);
	}
	
	public void addRequest(Request r)
	{
		requests.add(r);
	}
	
	public void generate()
	{
		clear(screenPX, CLEAR);
		clear(depthPX, 0);
		
		transform.run();
		requests.sort(compare);
		layer.run();
		raster.run();
		requests.clear();
	}
	
	public IntBuffer getFrame()
	{
		return screen;
	}
	
	public int[] getContent()
	{
		return screenPX;
	}

	protected void clear(int[] b, int c) 
	{
		for (int l=b.length-1; l>=0; l--) b[l]=c;
	}
}

/*
 * This is free and unencumbered software released into the public domain.
 * (http://unlicense.org/)
 * Nullpointer Works (2021)
 */
package com.nullpointerworks.j2d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.j2d.shader.BufferedRequest;
import com.nullpointerworks.j2d.shader.Layering;
import com.nullpointerworks.j2d.shader.Rasterizer;
import com.nullpointerworks.j2d.shader.Transform;

/**
 * Encapsulates the process of organizing, layering, transforming and rasterizing images. After each frame generation the content will be stored internally until the next generation method is called. 
 * @since 1.0.0
 * @author Michiel Drost - Nullpointer Works
 */
public class Engine 
{
	/*
	 * shaders
	 */
	private Transform transform;
	private Layering layer;
	private Rasterizer raster;
	
	/*
	 * plot data
	 */
	private IntBuffer depth;
	private IntBuffer screen;
	private int[] depthPX, screenPX;
	
	/*
	 * engine parameters
	 */
	private int CLEAR = 0xFF202020;
	private float accuracy = 0.495f;
	
	/*
	 * engine internals
	 */
	private List<Request> requests;
	private List<BufferedRequest> brequest;
	private final Comparator<BufferedRequest> compare;
	
	/**
	 * Creates a new J2D engine object rendering to a frame with the specified dimensions.
	 * @param width - the width of the desired frame
	 * @param height - the height of the desired frame
	 * @since 1.0.0
	 */
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
	
	/**
	 * Set the background clearing color to the specified 32bit ARGB integer color.
	 * @param color - the ARGB clearing color in 32bit format
	 * @since 1.0.0
	 */
	public void background(int color)
	{
		CLEAR = color;
	}
	
	/**
	 * Sets the pixel accuracy for rasterizer precision. The accuracy value is proportional to performance, but inversely proportional to rendering precision. A high accuracy value results in lower rendering precision, but also improves rendering performance. The accuracy domain is {@code 0 < x <= 1}. The default value is {@code 0.495}. 
	 * @param acc - the rendering accuracy
	 * @since 1.0.0
	 */
	public void accuracy(float acc)
	{
		if (acc > 1f) acc = 1f;
		if (acc < 0.0001f) acc = 0.0001f;
		raster.accuracy(acc);
	}
	
	/**
	 * Queues a rendering request to the engine for the next frame. All requests are then cleared from the queue.
	 * @param r - the rendering request
	 * @since 1.0.0
	 */
	public void request(Request r)
	{
		requests.add(r);
	}
	
	/**
	 * Generates a new frame from all the queued requests. The result can be accessed by calling the {@code Engine.frame()} or {@code Engine.content()} method. 
	 * @since 1.0.0
	 */
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
	
	/**
	 * Returns the previously generated frame contained in a {@code IntBuffer} object. To get the pixel content directly, use the {@code Engine.content()} method instead.
	 * @return the previously generated frame contained in a {@code IntBuffer} object
	 * @since 1.0.0
	 */
	public IntBuffer frame()
	{
		return screen;
	}
	
	/**
	 * Returns an array of 32-bit integer colors generated by the engine. The length of the content array is equal to the dimension specified at the constructor. If nothing was generated the previously, then the content would contain the background clearing color. 
	 * @return the pixel content of the previously generated frame
	 * @since 1.0.0
	 */
	public int[] content()
	{
		return screenPX;
	}
	
	/*
	 * clear all request buffers for the next frame
	 */
	private void clear()
	{
		requests.clear();
		for (int l=brequest.size()-1;l>=0; l--)
			brequest.get(l).free();
		brequest.clear();
	}
	
	/**
	 * Sets the value of each element in the given {@code int[]} to the specified value {@code c}.
	 * @param arr - the array to clear
	 * @param c - the value to clear to
	 * @since 1.0.0
	 */
	private void clear(int[] arr, int c) 
	{
		for (int l=arr.length-1; l>=0; l--) arr[l]=c;
	}
}

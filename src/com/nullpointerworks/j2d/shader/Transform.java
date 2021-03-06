/*
 * This is free and unencumbered software released into the public domain.
 * (http://unlicense.org/)
 * Nullpointer Works (2021)
 */
package com.nullpointerworks.j2d.shader;

import java.util.List;

import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.j2d.Request;
import com.nullpointerworks.math.Approximate;
import com.nullpointerworks.math.matrix.Matrix3;

/**
 * This is the first shader in the engine pipeline. It performs rotation, translation and scaling on the {@code List<Request>} engine queue. The result of these transformations are stored in the {@code List<BufferedRequest>} for the following shaders.
 * @since 1.0.0
 * @author Michiel Drost - Nullpointer Works
 */
public class Transform extends ShaderMath implements Runnable
{
	private List<Request> l;
	private List<BufferedRequest> b;
	private Matrix3 M3;
	
	/**
	 * Instantiated a transformation shader object for the J2D engine.
	 * @param l - engine's request queue
	 * @param b - engine's rendering queue
	 * @since 1.0.0
	 */
	public Transform(List<Request> l, List<BufferedRequest> b) 
	{
		this.l = l;
		this.b = b;
		M3 = new Matrix3();
	}
	
	@Override
	public void run()
	{
		for (Request di : l)
		{
			transform(di);
		}
	}
	
	/**
	 * @since 1.0.0
	 */
	private void transform(Request req)
	{
		/*
		 * get request data
		 */
		IntBuffer img 	= req.image;
		Float x			= req.vertex[0];
		Float y			= req.vertex[1];
		Float scaleW	= req.scale_w;
		Float scaleH	= req.scale_h;
		Float rotate 	= req.angle;
		float source_w 	= img.getWidth();
		float source_h 	= img.getHeight();
		
		/*
		 * scale image
		 */
		float scale_w 	= source_w;
		float scale_h 	= source_h;
		float inv_scalew = 1f;
		float inv_scaleh = 1f;
		if (scaleW != null)
		{
			inv_scalew = 1f / scaleW;
			scale_w = source_w * scaleW;
		}
		if (scaleH != null)
		{
			inv_scaleh = 1f / scaleH;
			scale_h = source_h * scaleH;
		}
		
		/*
		 * rotate image
		 */
		float rotate_w = scale_w;
		float rotate_h = scale_h;
		float sin = 0f;
		float cos = 1f;
		if (rotate != null)
		{
			sin = (float)Approximate.sin(rotate);
		    cos = (float)Approximate.cos(rotate);
		    float absin = (sin<0f)?-sin:sin;
		    float abcos = (cos<0f)?-cos:cos;
		    rotate_w 	= (abcos*scale_w + scale_h*absin);
		    rotate_h 	= (abcos*scale_h + scale_w*absin);
		}
		
		/*
		 * create rotation, scaling and translation matrices
		 */
	    float[][] m_rotate = 
    	{
    		{cos,-sin, scale_w*0.5f},
    		{sin, cos, scale_h*0.5f},
    		{ 0f,  0f, 1f}
    	};
	    float[][] m_scale = 
	    {
    		{inv_scalew,0f,0f},
    		{0f,inv_scaleh,0f},
    		{0f,0f,1f}
	    };
	    float[][] m_trans = 
	    {
    		{1f,0f,-x},
    		{0f,1f,-y},
    		{0f,0f,1f}
	    };
	    
	    /*
	     * create buffer for further engine processing
	     */
	    BufferedRequest br = new BufferedRequest();
	    br.image 		= img;
	    br.layer 		= req.layer;
	    br.transform 	= M3.mul(m_scale, m_rotate, m_trans);
	    br.x 	= x - 0.5f*rotate_w;
	    br.y 	= y - 0.5f*rotate_h;
	    br.w 	= br.x + rotate_w; // w' = x+w
		br.h 	= br.y + rotate_h; // h' = y+h
	    b.add(br);
	}
}

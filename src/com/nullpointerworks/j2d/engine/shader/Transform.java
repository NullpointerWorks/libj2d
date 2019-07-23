/*
 * Creative Commons - Attribution, Share Alike 4.0 
 * Nullpointer Works (2019)
 * Use is subject to license terms.
 */
package com.nullpointerworks.j2d.engine.shader;

import java.util.List;

import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.j2d.BufferedRequest;
import com.nullpointerworks.j2d.Request;
import com.nullpointerworks.math.Approximate;
import com.nullpointerworks.math.matrix.Matrix3;

/*
 * applies translations, rotations, scaling, etc to the given request
 */
public class Transform extends ShaderMath implements Runnable
{
	private List<Request> l;
	private List<BufferedRequest> b;
	private Matrix3 M3;
	
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
	
	public void transform(Request req)
	{
		/*
		 * transform source image
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
		float rotate_w 	= scale_w;
		float rotate_h 	= scale_h;
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
		 * create matrices
		 */
	    float[][] m_rotate = 
    	{
    		{cos,-sin, scale_w*0.5f-1f},
    		{sin, cos, scale_h*0.5f-1f},
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
	    float[][] tmat = M3.mul(m_scale, m_rotate, m_trans);
	    
	    
	    //*
	    
	    float[] v00 = {0f, 0f};
	    float[] v01 = {0f, rotate_w};
	    float[] v10 = {rotate_h, 0f};
	    float[] v11 = {rotate_h, rotate_w};
	    
	    transform(tmat, v00);
	    transform(tmat, v01);
	    transform(tmat, v10);
	    transform(tmat, v11);
	    
	    float minx = min(v00[0], v01[0], v10[0], v11[0]);
	    float miny = min(v00[1], v01[1], v10[1], v11[1]);
	    float maxx = max(v00[0], v01[0], v10[0], v11[0]);
	    float maxy = max(v00[1], v01[1], v10[1], v11[1]);
	    
	    BufferedRequest br = new BufferedRequest();
	    br.layer 		= req.layer;
	    br.image 		= img;
	    br.transform 	= tmat;
	    br.aabb.x 		= minx;
	    br.aabb.y 		= miny;
	    br.aabb.w 		= maxx;
		br.aabb.h 		= maxy;
	    b.add(br);

	    /*/
	    
	    BufferedRequest br = new BufferedRequest();
	    br.layer 		= req.layer;
	    br.image 		= img;
	    br.transform 	= tmat;
	    br.aabb.x 		= x - rotate_w*0.5f;
	    br.aabb.y 		= y - rotate_h*0.5f;
	    br.aabb.w 		= rotate_w;
		br.aabb.h 		= rotate_h;
	    b.add(br);
	    
	    
	    //*/
	}
}

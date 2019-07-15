/*
 * Creative Commons - Attribution, Share Alike 4.0 
 * Nullpointer Works (2019)
 * Use is subject to license terms.
 */
package com.nullpointerworks.j2d;

import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.math.geometry.g2d.Rectangle;

/**
 * Container object for image manipulation used in the J2D engine. 
 */
public class Request 
{
	/*
	 * input
	 */
	public IntBuffer image;
	public int layer 		= 1;
	public float x 			= 0f;
	public float y 			= 0f;
	public Float angle 		= null;
	public Float scale_w 	= null;
	public Float scale_h 	= null;
	public Integer chroma	= null;
	
	/*
	 * output
	 */
	public float[][] transform;
	public Rectangle aabb 	= new Rectangle(0f,0f,0f,0f);
}

/*
 * Creative Commons - Attribution, Share Alike 4.0 
 * Nullpointer Works (2019)
 * Use is subject to license terms.
 */
package com.nullpointerworks.j2d.shader;

import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.math.geometry.g2d.Rectangle;

/**
 * The engine performs various calculations before rendering. This object is an intermediate state between requesting and rendering.
 * @since 1.0.0
 */
public class BufferedRequest 
{
	/**
	 * @since 1.0.0
	 */
	public IntBuffer image = null;
	
	/**
	 * @since 1.0.0
	 */
	public float[][] transform = null;
	
	/**
	 * @since 1.0.0
	 */
	public int layer = 1;
	
	/**
	 * @since 1.0.0
	 */
	public Rectangle aabb = new Rectangle(0f,0f,0f,0f);
	
	/**
	 * @since 1.0.0
	 */
	public void free()
	{
		image		= null;
		transform 	= null;
		aabb 		= null;
	}
}

/*
 * Creative Commons - Attribution, Share Alike 4.0 
 * Nullpointer Works (2019)
 * Use is subject to license terms.
 */
package com.nullpointerworks.j2d;

import com.nullpointerworks.core.buffer.IntBuffer;

/**
 * Sprite container class used for image manipulation in the J2D engine. 
 * @since 1.0.0
 * @author Michiel Drost - Nullpointer Works
 */
public class Request 
{	
	/**
	 * IntBuffer object placeholder for the source image.
	 * @since 1.0.0
	 */
	public IntBuffer image	= null;
	
	/**
	 * Integer placeholder for the layering.
	 * @since 1.0.0
	 */
	public int layer 		= 1;
	
	/**
	 * Float array object location vertex.
	 * @since 1.0.0
	 */
	public float[] vertex 	= {0f,0f,1f};
	
	/**
	 * Float object placeholder for rotation.
	 * @since 1.0.0
	 */
	public Float angle 		= null;
	
	/**
	 * Float object placeholder for the width scaling.
	 * @since 1.0.0
	 */
	public Float scale_w 	= null;
	
	/**
	 * Float object placeholder for the height scaling.
	 * @since 1.0.0
	 */
	public Float scale_h 	= null;
	
	/**
	 * Integer object placeholder for the chroma key. 
	 * @since 1.0.0
	 */
	public Integer chroma	= null;
	
	/**
	 * The source image will act as a color reference during the engine's transformation cycle. The source image will not be modified in any way by engine internal processes.
	 * @param image - the source image/sprite to render
	 * @return a reference of this {@code Request} object
	 * @since 1.0.0
	 */
	public Request image(IntBuffer image)
	{
		this.image = image;
		return this;
	}
	
	/**
	 * Translates the center of the sprite to the given {@code (x,y)} coordinates. This is done after scaling and rotating the sprite.
	 * @param x - Cartesian x coordinate 
	 * @param y - Cartesian y coordinate
	 * @return a reference of this {@code Request} object
	 * @since 1.0.0
	 */
	public Request translate(float x, float y)
	{
		vertex = new float[] {x,y,1f};
		return this;
	}
	
	/**
	 * Scaling an image will render will be performed before rotating the sprite. A scaled image will appear wider or longer depending on user input.
	 * @param sw - the width scale
	 * @param sh - the height scale
	 * @return a reference of this {@code Request} object
	 * @since 1.0.0
	 */
	public Request scale(float sw, float sh)
	{
		this.scale_w=sw;
		this.scale_h=sh;
		return this;
	}
	
	/**
	 * The rotation of the sprite follows general mathematical rules. Positive rotation results in counter-clockwise image rotation.
	 * @param angle - the angle of rotation in radians
	 * @return a reference of this {@code Request} object
	 * @since 1.0.0
	 */
	public Request rotate(float angle)
	{
		this.angle = angle;
		return this;
	}
	
	/**
	 * The layer is equivalent to the depth of the image. Layers are treated like a stack. The higher the layer number, the higher up the stack it is. Everything on top of the stack will be rendered first. Occluded sprites will not be rendered.
	 * @param layer - the layer height
	 * @return a reference of this {@code Request} object
	 * @since 1.0.0
	 */
	public Request layer(int layer)
	{
		this.layer=layer;
		return this;
	}
	
	/**
	 * The chroma key is a color to be ignored by the engine's rasterizer. When colors are ignored, they will by rendered as 100% transparent.
	 * @param chroma - 32 bit value that represents an ARGB color
	 * @return a reference of this {@code Request} object
	 * @since 1.0.0
	 */
	public Request chroma(int chroma)
	{
		this.chroma=chroma;
		return this;
	}
	
	/**
	 * Clears any reference of objects contained in this {@code Request} object.
	 * @since 1.0.0
	 */
	public void free()
	{
		image	= null;
		vertex	= null;
		angle 	= null;
		scale_w = null;
		scale_h = null;
		chroma	= null;
	}
	
	/**
	 * Returns a new instance as a copy of this request. All other references stay the same, for example, the image {@code IntBuffer} source object.
	 * @return a new instance as a copy of this request
	 * @since 1.0.0
	 */
	public Request copy()
	{
		Request nr = new Request();
		nr.image(image);
		nr.translate(vertex[0], vertex[1]);
		nr.scale(scale_w, scale_h);
		nr.layer(layer);
		nr.rotate(angle);
		nr.chroma(chroma);
		return nr;
	}
}

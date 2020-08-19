/*
 * Creative Commons - Attribution, Share Alike 4.0 
 * Nullpointer Works (2020)
 * Use is subject to license terms.
 */
package com.nullpointerworks.j2d.shader;

/**
 * Contains some basic mathematical functions that are used in the engine shader objects.
 * @since 1.0.0
 * @author Michiel Drost - Nullpointer Works
 */
abstract class ShaderMath 
{
	/**
	 * Finds the lowest of the four given values and returns it.
	 * @return the lowest of the four given values
	 * @param a - value 1
	 * @param b - value 2
	 * @param c - value 3
	 * @param d - value 4
	 * @since 1.0.0
	 */
	protected float min(float a,float b,float c,float d)
	{
		float cab = (a<b)?a:b;
		float ccd = (c<d)?c:d;
		return (cab<ccd)?cab:ccd;
	}
	
	/**
	 * Finds the highest of the four given values and returns it.
	 * @return the highest of the four given values
	 * @param a - value 1
	 * @param b - value 2
	 * @param c - value 3
	 * @param d - value 4
	 * @since 1.0.0
	 */
	protected float max(float a,float b,float c,float d)
	{
		float cab = (a>b)?a:b;
		float ccd = (c>d)?c:d;
		return (cab>ccd)?cab:ccd;
	}
	
	/**
	 * Performs a transformation multiplication on a vector with the given matrix.
	 * @param m - the 3x3 transformation matrix
	 * @param v - the 3 element vector to be transformed
	 * @since 1.0.0
	 */
	protected void transform(float[][] m, float[] v)
	{
		float vx,vy; float[] mp = m[0];
		vx = mp[0]*v[0] + mp[1]*v[1] + mp[2];
		mp = m[1];
		vy = mp[0]*v[0] + mp[1]*v[1] + mp[2];
		v[0] = vx; v[1] = vy;
	}
	
	/**
	 * Integer interpolation of ARGB 32-bit colors. Interpolates between the start and end color with an interpolation factor ranging from 0(0%) to 256(100%).
	 * @param c1 - starting color
	 * @param c2 - end color
	 * @param lerp - interpolation factor
	 * @since 1.0.0
	 */
	protected int lerp256(int c1, int c2, int lerp) 
	{
		int ag1 = c1 & 0xFF00FF00;
		int ag2 = c2 & 0xFF00FF00;
		int rb1 = c1 & 0x00FF00FF;
		int rb2 = c2 & 0x00FF00FF;
		int l 	= (256-lerp);
		ag1 = ag1*l + ag2*lerp;
		rb1 = rb1*l + rb2*lerp;
		ag1 = ag1>>8;
		rb1 = rb1>>8;
	    return (ag1 & 0xFF00FF00) + (rb1 & 0x00FF00FF);
	}
}

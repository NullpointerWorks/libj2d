/*
 * Creative Commons - Attribution, Share Alike 4.0 
 * Nullpointer Works (2019)
 * Use is subject to license terms.
 */
package com.nullpointerworks.j2d.engine.shader;

/**
 * 
 * @since 1.0.0
 * @author Michiel Drost - Nullpointer Works
 */
abstract class ShaderMath 
{
	/**
	 * 
	 * @since 1.0.0
	 */
	protected float min(float a,float b,float c,float d)
	{
		float cab = (a<b)?a:b;
		float ccd = (c<d)?c:d;
		return (cab<ccd)?cab:ccd;
	}
	
	/**
	 * 
	 * @since 1.0.0
	 */
	protected float max(float a,float b,float c,float d)
	{
		float cab = (a>b)?a:b;
		float ccd = (c>d)?c:d;
		return (cab>ccd)?cab:ccd;
	}
	
	/**
	 * 
	 * @since 1.0.0
	 */
	protected void transform(float[][] m, float[] v)
	{
		float vx,vy;
		float[] mp = m[0];
		vx = mp[0]*v[0] + mp[1]*v[1] + mp[2];
		mp = m[1];
		vy = mp[0]*v[0] + mp[1]*v[1] + mp[2];
		v[0] = vx; v[1] = vy;
	}
	
	/**
	 * Integer interpolation of ARGB 32-bit colors
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

package com.nullpointerworks.j2d;

import com.nullpointerworks.core.Monitor;
import com.nullpointerworks.core.buffer.IntBuffer;
import com.nullpointerworks.core.input.KeyboardInput;
import com.nullpointerworks.core.input.MouseInput;
import com.nullpointerworks.core.window.Window;
import com.nullpointerworks.core.window.WindowMode;
import com.nullpointerworks.j2d.engine.Engine;

public class J2D 
{
	private static J2D instance = null;
	
	public static J2D getInstance() 
	{
		if (instance==null) instance = new J2D();
		return instance;
	}
	
	/**
	 *  
	 */
	public static void setResolution(int width, int height) 
	{
		instance.resolution(width, height);
	}
	
	public static void setTitle(String title) 
	{
		instance.title(title);
	}

	public static void setDisplay(int d) 
	{
		instance.display(d);
	}

	public static void setVisible(boolean show) 
	{
		instance.visible(show);
	}

	public static void initializeAll() 
	{
		instance.initAll();
	}

	public static void initializeWindow() 
	{
		instance.initWindowOnly();
	}

	public static void initializeEngine() 
	{
		instance.initEngineOnly();
	}

	public static int[] getResolution()
	{
		return new int[] {instance.width(), instance.height()};
	}
	
	public static IntBuffer newScreen() 
	{
		return new IntBuffer(instance.width(), instance.height());
	}

	public static MouseInput getMouse()
	{
		return instance.mouse();
	}
	
	public static KeyboardInput getKeyboard()
	{
		return instance.keyboard();
	}
	
	public static void addRequest(Request r) 
	{
		instance.request(r);
	}
	
	public static IntBuffer getFrame() 
	{
		return instance.frame();
	}
	
	public static void doGenerate()
	{
		instance.generate();
	}
	
	public static void doRepaint(IntBuffer screen) 
	{
		instance.repaint(screen);
	}
	
	public static void doRepaint()
	{
		instance.generate();
		instance.repaint();
	}

	// =================================
	
	/**
	 * apply a chroma key to the image. this will make all
	 * pixels which match the key in the image translucent
	 */
	public static void setChroma(Request r) 
	{
		IntBuffer i = r.image;
		int l 		= i.getLength()-1;
		int[] px 	= i.content();
		int key 	= r.chroma;
		key = key & 0x00FFFFFF;
		for (;l>=0;l--)
		{
			int c = px[l] & 0x00FFFFFF;
			if (c == key)
			{
				px[l] = c;
			}
		}
	}
	
	// =================================

	private boolean _initializedE 	= false;
	private boolean _initializedW 	= false;
	private Engine _engine 			= null;
	private Window _window 			= null;
	private IntBuffer _screen 		= null;
	private int _width 		= 1;
	private int _height 	= 1;
	private int _display 	= 0;
	private String _title 	= "";
	
	public J2D() { }
	
	public void resolution(int width, int height) 
	{
		_width = width;
		_height = height;
	}
	
	public int width()
	{
		return _width;
	}
	
	public int height()
	{
		return _height;
	}
	
	public void title(String title) 
	{
		_title = title;
	}
	
	public boolean isAllInitialized()
	{
		return _initializedE && _initializedW;
	}
	
	public boolean isWindowInitialized()
	{
		return _initializedW;
	}
	
	public boolean isEngineInitialized()
	{
		return _initializedE;
	}
	
	private void display(int d) 
	{
		_display = d;
	}
	
	public void initAll()
	{
		if (!isAllInitialized())
		{
			_engine = new Engine(_width, _height);
			_screen = _engine.getFrame();
			_initializedE = true;
			_window = new Window(_width, _height, _title, Monitor.getDisplay(_display) );
			_window.addInputDevice(new MouseInput());
			_window.addInputDevice(new KeyboardInput());
			_initializedW = true;
			_window.setWindowMode(WindowMode.WINDOWED);
		}
	}
	
	public void initEngineOnly()
	{
		if (!isEngineInitialized())
		{
			_engine = new Engine(_width, _height);
			_screen = _engine.getFrame();
			_initializedE = true;
		}
	}
	
	public void initWindowOnly()
	{
		if (!isWindowInitialized())
		{
			_window = new Window(_width, _height, _title, Monitor.getDisplay(_display) );
			_window.addInputDevice(new MouseInput());
			_window.addInputDevice(new KeyboardInput());
			_window.setWindowMode(WindowMode.WINDOWED);
			_window.setVisible(true);
			_screen = new IntBuffer(_width, _height);
			_initializedW = true;
		}
	}
	
	public void visible(boolean show) 
	{
		if (!isWindowInitialized()) return;
		_window.setVisible(show);
	}
	
	public MouseInput mouse()
	{
		if (!isWindowInitialized()) return null;
		return _window.getMouse();
	}
	
	public KeyboardInput keyboard()
	{
		if (!isWindowInitialized()) return null;
		return _window.getKeyboard();
	}
	
	public void request(Request r) 
	{
		if (!isEngineInitialized()) return;
		_engine.addRequest(r);
	}
	
	public IntBuffer frame() 
	{
		if (!isEngineInitialized()) 
		{
			if (!isWindowInitialized()) return null;
			return _screen;
		}
		return _engine.getFrame();
	}
	
	public void generate()
	{
		if (!isEngineInitialized()) return;
		_engine.generate();
		_screen = _engine.getFrame();
	}
	
	public void repaint()
	{
		if (!isWindowInitialized()) return;
		_window.swap(_screen.content());
	}
	
	public void repaint(IntBuffer screen)
	{
		if (!isWindowInitialized()) return;
		_window.swap(screen.content());
	}
}

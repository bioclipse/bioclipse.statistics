/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/

package net.bioclipse.model;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;


import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * Utility class for writing images to file
 * @author Eskil Andersen
 *
 */
public class ImageWriter {

	/**
	 * Save image to svg format
	 * @param path the path including filename where the image is to be stored
	 * @param chart TODO
	 */
	public static void saveImageSVG( String path, JFreeChart chart )
	{
		//First check that we have a valid chart
		if( chart != null )
		{
			//Create DOM objects
			DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
			Document document = domImpl.createDocument(null, "svg", null);
	
			//Create an instance of the SVG generator
			SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
	
			//Setting the precision apparently avoids a NullPointerException in Batik 1.5
			svgGenerator.getGeneratorContext().setPrecision(6);
	
			//Render chart into SVG Graphics2D impl.
			chart.draw(svgGenerator, new Rectangle2D.Double(0,0,400,300),null);
			
			//Write to file
			boolean useCSS = true;
			try {
				Writer out = new OutputStreamWriter(
						new FileOutputStream(new File(path)), "UTF-8");
				svgGenerator.stream(out,useCSS);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SVGGraphics2DIOException e) {
				e.printStackTrace();
			}
		}
	
	}

	public static void saveImageJPG(String path, JFreeChart chart) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(path);
		ChartUtilities.writeChartAsJPEG(fos, chart, 640, 480);
		fos.close();
	}

	public static void saveImagePNG(String path, JFreeChart chart) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(path);
		ChartUtilities.writeChartAsPNG(fos, chart, 640, 480);
		fos.close();
	}

}

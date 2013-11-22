/* ***************************************************************************
 * Copyright (c) 2013 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.chart;

/**
 * Just a simple class to store a point with x- and y-values that are doubles.
 * 
 * @author Klas Jšnsson (klas.joensson@gmail.com)
 *
 */
public class ChartPoint {

    private double x;
    private double y;
    
    public ChartPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
}

/*****************************************************************************
 * Copyright (c) 2008 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *****************************************************************************/
package net.bioclipse.model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.bioclipse.model.PlotPointData;
import org.eclipse.jface.viewers.IStructuredSelection;
public class ChartSelection implements IStructuredSelection 
{
        private List<PlotPointData> points;
        private ChartDescriptor descriptor;
        public ChartDescriptor getDescriptor() {
                return descriptor;
        }
        public boolean addAll(ChartSelection arg0) {
                return points.addAll(arg0.toList());
        }
        public boolean addAll(Collection<? extends PlotPointData> arg0) {
                return points.addAll(arg0);
        }
        public boolean addAll(int arg0, Collection<? extends PlotPointData> arg1) {
                return points.addAll(arg0, arg1);
        }
        public void setDescriptor(ChartDescriptor descriptor) {
                this.descriptor = descriptor;
        }
        public ChartSelection()
        {
                points = new ArrayList<PlotPointData>();
        }
        public void clear() {
                points.clear();
        }
        /**
         * @param ppd ChartPoint to add to selection
         */
        public boolean addPoint( PlotPointData ppd )
        {
                if( !points.contains(ppd)){
                        points.add(ppd);
                        return true;
                }
                return false;
        }
        public boolean contains(Object arg0) {
                return points.contains(arg0);
        }
        /**
         * @param ppd ChartPoint to remove from selection
         */
        public boolean removePoint( PlotPointData ppd)
        {
                if( points.contains(ppd)){
                        points.remove(ppd);
                        return true;
                }
                return false;
        }
        public boolean isEmpty() {
                return points.isEmpty();
        }
        public Object getFirstElement() {
                if( points.size() == 0)
                        return null;
                return points.get(0);
        }
        public Iterator<PlotPointData> iterator() {
                return points.iterator();
        }
        public int size() {
                return points.size();
        }
        public Object[] toArray() {
                return points.toArray();
        }
        public List<PlotPointData> toList() {
                return points;
        }
}

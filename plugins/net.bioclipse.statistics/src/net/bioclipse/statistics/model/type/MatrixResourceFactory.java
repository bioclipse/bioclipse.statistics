/*******************************************************************************
 * Copyright (c) 2005 Bioclipse Project
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ola Spjuth - core API and implementation
 *******************************************************************************/
package net.bioclipse.statistics.model.type;

import net.bioclipse.statistics.model.BioResourceFactory;
import net.bioclipse.statistics.model.BioResourceType;
import net.bioclipse.statistics.model.IBioResource;
import net.bioclipse.statistics.model.MatrixResource;
 
/**
 * @author egonw
 */
public class MatrixResourceFactory extends BioResourceFactory
{
   public MatrixResource newItem(
      BioResourceType type,
      Object obj) {

      return new MatrixResource(type, obj);
   }

   public IBioResource loadItem(
      BioResourceType type,
      String info) {

      return MatrixResource.loadItem(type, info);
   }
   public IBioResource newResource(BioResourceType type, Object resourceObject, String name) {
	      return MatrixResource.newResource(type, resourceObject, name);
	}

}

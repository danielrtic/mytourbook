/*******************************************************************************
 * Copyright (C) 2005, 2013  Wolfgang Schramm and Contributors
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
 *******************************************************************************/
package net.tourbook.photo.internal;

import net.tourbook.photo.PhotoImages;
import net.tourbook.photo.PhotoGallery;

import org.eclipse.jface.action.Action;

public class ActionPhotoFilterTour extends Action {

   private PhotoGallery _photoGallery;

   public ActionPhotoFilterTour(final PhotoGallery photoGallery) {

      super(Messages.Photo_Gallery_Action_PhotoFilter_Tour, AS_CHECK_BOX);

      setToolTipText(Messages.Photo_Gallery_Action_PhotoFilter_Tour_Tooltip);

      setImageDescriptor(Activator.getImageDescriptor(PhotoImages.Image__PhotoFilterTour));
      setDisabledImageDescriptor(Activator.getImageDescriptor(PhotoImages.Image__PhotoFilterTourDisabled));

      _photoGallery = photoGallery;
   }

   @Override
   public void run() {
      _photoGallery.actionImageFilterTour(this);
   }
}

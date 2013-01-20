/*
 * Shared Folder Chat is a program for chatting via a shared folder in a local area network (LAN).
 * Copyright (C) 2013  Niklas Wenzel <nikwen.developer@gmail.com>
 * 
 * This file is part of Shared Folder Chat.
 *  
 * Shared Folder Chat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Shared Folder Chat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with Shared Folder Chat.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.wenzel.niklas.sharedfolderchat;

import java.io.File;
import java.util.Comparator;

public class TimeComparator implements Comparator<File> {

	@Override
	public int compare(File f1, File f2) {
		return -Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
	}

}

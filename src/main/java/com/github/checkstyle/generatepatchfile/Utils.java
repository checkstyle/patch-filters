////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.checkstyle.generatepatchfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Contains utility methods.
 */
public final class Utils {

    /** Prevents instantiation. */
    private Utils() {
    }

    /**
     * Copy all src dir to dest dir.
     * @param src src dir.
     * @param dest dest dir.
     * @throws IOException IO exception.
     */
    public static void copyDir(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            final String[] files = src.list();
            for (String file : files) {
                final File srcFile = new File(src, file);
                final File destFile = new File(dest, file);
                copyDir(srcFile, destFile);
            }
        }
        else {
            final InputStream inputStream = new FileInputStream(src);
            final OutputStream outputStream = new FileOutputStream(dest);
            final int size = 1024;
            final byte[] buffer = new byte[size];

            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
        }
    }
}

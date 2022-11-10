///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2022 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.jgit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jgit.patch.Patch;

public abstract class AbstractJgitPatchParserEvaluationTest {

    protected abstract String getPatchPath(String patchName);

    protected static Patch loadPatch(String patchPath) throws IOException {
        final Patch patch = new Patch();
        try (InputStream is = new FileInputStream(patchPath)) {
            patch.parse(is);
        }
        return patch;
    }
}

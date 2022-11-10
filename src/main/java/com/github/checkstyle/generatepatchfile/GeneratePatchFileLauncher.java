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

package com.github.checkstyle.generatepatchfile;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * GeneratePatchFileWithGitCommandLauncher.
 */
public final class GeneratePatchFileLauncher {

    private GeneratePatchFileLauncher() {

    }

    /**
     * Main function.
     *
     * @param args parameters
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        final String repoPath = args[0];
        final String checkstyleRepoPath = args[1];
        final String testerPath = args[2];
        final String checkstyleBranch = args[3];
        final File baseConfigFile = new File(args[4]);
        final File patchConfigFile = new File(args[5]);
        final GeneratePatchFile generatePatchFile =
                new GeneratePatchFile(repoPath, testerPath, checkstyleRepoPath,
                        checkstyleBranch, baseConfigFile, patchConfigFile);
        final String commitParam = args[6];
        if (commitParam.matches("(0|[1-9]\\d*)")) {
            generatePatchFile.generatePatch(Integer.parseInt(args[6]));
        }
        else {
            final String[] commitIds = commitParam.split(",");
            final Set<String> commitSet = new HashSet<>(Arrays.asList(commitIds));
            generatePatchFile.generatePatch(commitSet);
        }
    }
}

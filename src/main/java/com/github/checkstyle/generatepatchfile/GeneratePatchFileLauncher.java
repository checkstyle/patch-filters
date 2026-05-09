///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
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
 * GeneratePatchFileLauncher.
 */
public final class GeneratePatchFileLauncher {

    /**
     * CHECKSTYLE_BRANCH_ARG_INDEX.
     */
    private static final int CHECKSTYLE_BRANCH_ARG_INDEX = 3;

    /**
     * BASE_CONFIG_FILE_ARG_INDEX.
     */
    private static final int BASE_CONFIG_FILE_ARG_INDEX = 4;

    /**
     * PATCH_CONFIG_FILE_ARG_INDEX.
     */
    private static final int PATCH_CONFIG_FILE_ARG_INDEX = 5;

    /**
     * COMMIT_PARAM_ARG_INDEX.
     */
    private static final int COMMIT_PARAM_ARG_INDEX = 6;

    /**
     * Required number of command line arguments.
     */
    private static final int REQUIRED_ARGS = 7;

    /**
     * Exit code for invalid arguments.
     */
    private static final int EXIT_CODE_INVALID_ARGS = 1;

    /**
     * Private constructor to prevent instantiation.
     */
    private GeneratePatchFileLauncher() {

    }

    /**
     * Main function.
     *
     * @param args parameters
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < REQUIRED_ARGS) {
            System.err.println("Usage: GeneratePatchFileLauncher"
                    + " <repoPath>"
                    + " <checkstyleRepoPath>"
                    + " <testerPath>"
                    + " <checkstyleBranch>"
                    + " <baseConfigFile>"
                    + " <patchConfigFile>"
                    + " <commitIdOrCount>");
            System.err.println("  commitIdOrCount: a non-negative integer "
                    + "(number of recent commits) or a comma-separated list "
                    + "of commit SHAs");
            System.exit(EXIT_CODE_INVALID_ARGS);
        }
        final String repoPath = args[0];
        final String checkstyleRepoPath = args[1];
        final String testerPath = args[2];
        final String checkstyleBranch = args[CHECKSTYLE_BRANCH_ARG_INDEX];
        final File baseConfigFile = new File(args[BASE_CONFIG_FILE_ARG_INDEX]);
        final File patchConfigFile = new File(args[PATCH_CONFIG_FILE_ARG_INDEX]);
        final GeneratePatchFile generatePatchFile =
                new GeneratePatchFile(repoPath, testerPath, checkstyleRepoPath,
                        checkstyleBranch, baseConfigFile, patchConfigFile);
        final String commitParam = args[COMMIT_PARAM_ARG_INDEX];
        if (commitParam.matches("(0|[1-9]\\d*)")) {
            generatePatchFile.generatePatch(
                    Integer.parseInt(args[COMMIT_PARAM_ARG_INDEX]));
        }
        else {
            final String[] commitIds = commitParam.split(",");
            final Set<String> commitSet =
                    new HashSet<>(Arrays.asList(commitIds));
            generatePatchFile.generatePatch(commitSet);
        }
    }
}

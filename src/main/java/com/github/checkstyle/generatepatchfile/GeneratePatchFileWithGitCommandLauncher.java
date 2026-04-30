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

/**
 * GeneratePatchFileWithGitCommandLauncher.
 * */
public final class GeneratePatchFileWithGitCommandLauncher {

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
     * COMMIT_COUNT_ARG_INDEX.
     */
    private static final int COMMIT_COUNT_ARG_INDEX = 6;

    /**
     * GIT_COMMAND_ARG_INDEX.
     */
    private static final int GIT_COMMAND_ARG_INDEX = 7;

    private GeneratePatchFileWithGitCommandLauncher() {

    }

    /**
     * Main function.
     *
     * @param args parameters
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        final int requiredArgs = 8;
        if (args.length < requiredArgs) {
            System.err.println(
                    "Usage: GeneratePatchFileWithGitCommandLauncher"
                    + " <repoPath>"
                    + " <checkstyleRepoPath>"
                    + " <testerPath>"
                    + " <checkstyleBranch>"
                    + " <baseConfigFile>"
                    + " <patchConfigFile>"
                    + " <commitCount>"
                    + " <gitCommand>");
            System.err.println("  commitCount: a non-negative integer");
            System.err.println(
                    "  gitCommand:  git command string to generate the patch");
            System.exit(1);
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
        generatePatchFile.generatePatchWithGitCommand(
                Integer.parseInt(args[COMMIT_COUNT_ARG_INDEX]), args[GIT_COMMAND_ARG_INDEX]);
    }
}

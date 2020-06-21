package com.github.checkstyle.generatepatchfile;

import java.io.File;

public class GeneratePatchFileLauncher {
    public static void main(String[] args) throws Exception {
        String repoPath = args[0];
        String checkstyleRepoPath = args[1];
        String testerPath = args[2];
        String checkstyleBranch = args[3];
        File baseConfigFile = new File(args[4]);
        File patchConfigFile = new File(args[5]);
        GeneratePatchFile generatePatchFile =
                new GeneratePatchFile(repoPath, testerPath, checkstyleRepoPath,
                        checkstyleBranch, baseConfigFile, patchConfigFile);
        generatePatchFile.generatePatch(Integer.parseInt(args[6]));
    }
}

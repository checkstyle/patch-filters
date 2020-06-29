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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

/**
 * To generate patch file through jgit.
 */
public class GeneratePatchFile {
    /**
     * HEAD.
     */
    private static final String HEAD = "-HEAD~";

    /**
     * PATCH_FILE_FORMAT.
     */
    private static final String PATCH_FILE_FORMAT = "%s-patch-%s-%s.txt";

    /**
     * GIT.
     */
    private static final String GIT = "git";

    /**
     * MOVE_PATCH_FILE_ERROR_MESSAGE.
     */
    private static final String MOVE_PATCH_FILE_ERROR_MESSAGE = "Moving Patch File failed!";

    /**
     * PATCH_TXT.
     */
    private static final String PATCH_TXT = "patch.txt";

    /**
     * Git instance.
     */
    private Git git;

    /**
     * RepoPath of repository that will be checked by checkstyle.
     */
    private String repoPath;

    /**
     * Repository instance.
     */
    private Repository repository;

    /**
     * Path of checkstyle-tester.
     */
    private String testerPath;

    /**
     * Path of checkstyle.
     */
    private String checkstyleRepoPath;

    /**
     * Patch-branch of checkstyle.
     */
    private String checkstyleBranch;

    /**
     * BaseConfigFile.
     */
    private File baseConfigFile;

    /**
     * PatchConfigFile.
     */
    private File patchConfigFile;

    /**
     * Directory name which be store diff reports.
     */
    private String diffReportDirName;

    /**
     * Init GeneratePatchFile.
     *
     * @param repoPath           RepoPath of repository that will be checked by checkstyle.
     * @param testerPath         Path of checkstyle-tester.
     * @param checkstyleRepoPath Path of checkstyle.
     * @param checkstyleBranch   Patch-branch of checkstyle.
     * @param baseConfigFile     BaseConfigFile.
     * @param patchConfigFile    PatchConfigFile.
     */
    public GeneratePatchFile(String repoPath, String testerPath,
                             String checkstyleRepoPath, String checkstyleBranch,
                             File baseConfigFile, File patchConfigFile) {
        this.testerPath = testerPath;
        this.checkstyleRepoPath = checkstyleRepoPath;
        this.checkstyleBranch = checkstyleBranch;
        this.baseConfigFile = baseConfigFile;
        this.patchConfigFile = patchConfigFile;
        this.repoPath = repoPath;
        final File gitDir = new File(repoPath, ".git");
        try {
            this.repository = new FileRepositoryBuilder().setGitDir(gitDir)
                    .readEnvironment().findGitDir().build();
            git = new Git(repository);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        diffReportDirName = new File(".").getAbsoluteFile().getParent() + "/DiffReport";
    }

    /**
     * To generate patch file through jgit.
     *
     * @param runPatchNum num of patch file between two commits
     * @throws Exception exception
     * @throws IllegalArgumentException runPatchNum should be greater than 1.
     */
    public void generatePatch(int runPatchNum) throws Exception {
        if (runPatchNum <= 1) {
            throw new IllegalArgumentException("runPatchNum should be greater than 1");
        }
        final List<RevCommit> commitList = getAllCommits();
        for (int i = 0; i < runPatchNum - 1; i++) {
            generateTwoCommitDiffPatch(commitList.get(i + 1), commitList.get(i));
        }
        generateSummaryIndexHtml();
    }

    /**
     * To generate patch file through git command.
     *
     * @param runPatchNum num of patch file between two commits
     * @param patchFormat choose git command to create patch format
     * @throws Exception exception
     */
    public void generatePatchWithGitCommand(int runPatchNum, String patchFormat) throws Exception {
        for (int i = 1; i < runPatchNum; i++) {
            generateDiffPatchWithGitCommand(i, patchFormat);
            checkoutWithGitCommand();
        }
        generateSummaryIndexHtml();
    }

    private List<RevCommit> getAllCommits() throws Exception {
        final Iterable<RevCommit> commits = git.log().add(repository.resolve("HEAD")).call();
        int count = 0;
        final List<RevCommit> commitList = new ArrayList<>();
        for (RevCommit commit : commits) {
            count++;
            commitList.add(commit);
        }
        System.out.println("Commit num: " + count);
        return commitList;
    }

    private void generateTwoCommitDiffPatch(RevCommit commitOld, RevCommit commitNew)
            throws Exception {
        final int commitNameLength = 7;
        final String subDirName = getSimpleRepoName() + "-"
                + commitNew.getName().substring(0, commitNameLength);
        final File destDirFile = createDestDirName(subDirName);
        final boolean succ = destDirFile.mkdirs();
        if (succ) {
            final File patchFile = new File(diffReportDirName, PATCH_TXT);
            final PrintStream ps = new PrintStream(new FileOutputStream(patchFile));
            final DiffFormatter diffFormatter = new DiffFormatter(ps);
            diffFormatter.setRepository(git.getRepository());
            final ObjectReader reader = git.getRepository().newObjectReader();
            final CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, commitOld.getTree());
            final CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, commitNew.getTree());
            final List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);
            diffFormatter.format(entries);
            diffFormatter.close();

            checkout(commitNew.getName());
            final File reportDir = generate();
            Utils.copyDir(reportDir, destDirFile);

            final String patchFileName = String.format(PATCH_FILE_FORMAT, getSimpleRepoName(),
                    commitOld.getName().substring(0, commitNameLength),
                    commitNew.getName().substring(0, commitNameLength));
            if (!(patchFile.renameTo(new File(destDirFile.getAbsolutePath(), patchFileName)))) {
                throw new IOException(MOVE_PATCH_FILE_ERROR_MESSAGE);
            }
        }
    }

    private void generateDiffPatchWithGitCommand(int headNum, String patchFormat) throws Exception {
        if ("show".equals(patchFormat)) {
            runShellCommand("git show > show.patch");
        }
        else if ("diff".equals(patchFormat)) {
            runShellCommand("git diff HEAD~1 HEAD > show.patch");
        }
        else if ("format".equals(patchFormat)) {
            runShellCommand("git format-patch -1");
        }
        else {
            throw new IllegalArgumentException("patchFormat should be 'show', 'diff' or 'format'");
        }
        final File repoDir = new File(repoPath);
        final File[] fileList = repoDir.listFiles((dir, name) -> name.endsWith(".patch"));
        final String subDirName = getSimpleRepoName() + HEAD + headNum;
        final File destDirFile = createDestDirName(subDirName);
        final boolean succ = destDirFile.mkdirs();
        if (succ) {
            final File patchFile = new File(diffReportDirName, PATCH_TXT);
            if (fileList != null && fileList[0] != null) {
                if (!(fileList[0].renameTo(patchFile))) {
                    throw new IOException(MOVE_PATCH_FILE_ERROR_MESSAGE);
                }
            }
            final File reportDir = generate();
            Utils.copyDir(reportDir, destDirFile);
            final DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            final String patchFileName = String.format(PATCH_FILE_FORMAT, getSimpleRepoName(),
                    HEAD + headNum, format.format(new Date()));
            System.out.println(destDirFile.getAbsolutePath());
            if (!(patchFile.renameTo(new File(destDirFile.getAbsolutePath(), patchFileName)))) {
                throw new IOException(MOVE_PATCH_FILE_ERROR_MESSAGE);
            }
        }
    }

    private void runShellCommand(String command) throws Exception {
        final List<String> cmds = new ArrayList<>();
        cmds.add("sh");
        cmds.add("-c");
        cmds.add(command);
        final Process process = new ProcessBuilder()
                .directory(new File(repoPath))
                .command(cmds)
                .inheritIO()
                .start();
        final int code = process.waitFor();
        if (code != 0) {
            throw new IllegalStateException("an error occurred when running " + command);
        }
    }

    private File createDestDirName(String subDirName) throws Exception {
        final File destDirFile = new File(diffReportDirName, subDirName);
        if (destDirFile.exists()) {
            throw new IOException("commit dir exists, please delete");
        }
        return destDirFile;
    }

    private void checkoutWithGitCommand() throws Exception {
        final Process process = new ProcessBuilder()
                .directory(new File(repoPath))
                .command(GIT, "checkout", "HEAD~1")
                .inheritIO()
                .start();
        final int code = process.waitFor();
        if (code != 0) {
            throw new IllegalStateException("an error occurred when running git checkout HEAD~1");
        }
    }

    private void checkout(String commitName) throws Exception {
        git.checkout().setName(commitName).call();
    }

    private File generate()
            throws InterruptedException, IOException {
        final Process process = new ProcessBuilder()
                .directory(new File(testerPath))
                .command(
                        "groovy", "diff.groovy",
                        "-r", checkstyleRepoPath,
                        "-b", "master",
                        "-p", checkstyleBranch,
                        "-bc", baseConfigFile.getAbsolutePath(),
                        "-pc", patchConfigFile.getAbsolutePath(),
                        "-l", "projects-to-test-on.properties"
                )
                .inheritIO()
                .start();
        final int code = process.waitFor();
        if (code != 0) {
            throw new IllegalStateException("an error occurred when running diff.groovy");
        }
        final File reportDir = new File(testerPath, "reports");
        if (!reportDir.exists() || !reportDir.isDirectory()) {
            throw new IOException("report does not exist or it is not a directory");
        }
        return reportDir;
    }

    private String getSimpleRepoName() {
        final String[] repoPaths = repoPath.split("/");
        return repoPaths[repoPaths.length - 1];
    }

    private void generateSummaryIndexHtml() {
        final File indexFile = new File(diffReportDirName, "index.html");
        try (FileWriter out = new FileWriter(indexFile)) {
            final String brLine = "<br />\n";
            out.write("<html><head>\n");
            out.write("<link rel=\"icon\" href=\"https://checkstyle.org/images/favicon.png\" "
                    + "type=\"image/x-icon\" />\n");
            out.write("<title>Checkstyle Tester Report Diff Summary</title>\n");
            out.write("</head><body>\n<h3><span style=\"color: #ff0000;\">\n");
            out.write("<strong>WARNING: Excludes are ignored by diff.groovy.</strong>\n");
            out.write("</span></h3>\n");

            out.write("<h2>\n");
            out.write("Base branch: master");
            out.write(brLine);
            out.write("Patch branch: " + checkstyleBranch);
            out.write(brLine + brLine);
            final File diffReportDir = new File(diffReportDirName);
            final String[] commitDirNameList =
                    diffReportDir.list((dir, name) -> !name.endsWith(".html"));
            final String simpleRepoName = getSimpleRepoName();
            if (commitDirNameList != null) {
                for (String commitDirName : commitDirNameList) {
                    final String repoCommitInfo = String.format(
                            "<a href='%s/diff/%s/index.html'>%s-diff</a>\n",
                            commitDirName, simpleRepoName, commitDirName);
                    out.write(repoCommitInfo);
                    out.write(brLine);
                    final File commitDir = new File(diffReportDirName, commitDirName);
                    final String[] patchFileList =
                            commitDir.list((dir, name) -> name.endsWith(".txt"));
                    final String patchFileInfo = String.format("<a href='%s/%s'>%s-patch</a>\n",
                            commitDirName, patchFileList[0], commitDirName);
                    out.write(patchFileInfo);
                    out.write(brLine);
                }
            }
            out.write("</h2>\n</body></html>");

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

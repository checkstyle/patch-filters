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

package com.github.checkstyle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.FileInputStream;

import org.junit.Ignore;
import org.junit.Test;

import com.github.difflib.unifieddiff.UnifiedDiff;
import com.github.difflib.unifieddiff.UnifiedDiffFile;
import com.github.difflib.unifieddiff.UnifiedDiffReader;

public class UnifiedDiffReaderTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/checkstyle/patchfilter/";
    }

    @Test
    public void testMultiChangesOnOneFileOne() throws Exception {
        final UnifiedDiff diff = getUnifiedDiff("MultiChangesOnOneFilePatch.txt");
        assertEquals(1, diff.getFiles().size());

        final UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertNull(file1.getDiffCommand());
        assertEquals("Origin.java", file1.getFromFile());
        assertEquals("Update.java", file1.getToFile());
        assertEquals(2, file1.getPatch().getDeltas().size());

        assertNull(diff.getTail());
    }

    @Test
    @Ignore("until https://github.com/java-diff-utils/java-diff-utils/issues/83")
    public void testMultiChangedFilesOnOnePatchOne() throws Exception {
        final UnifiedDiff diff = getUnifiedDiff("problem_diff_issue51.diff");
        assertEquals(2, diff.getFiles().size());

        final UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertEquals("diff -U0 old/f1 new/f1", file1.getDiffCommand());
        assertEquals("old/f1", file1.getFromFile());
        assertEquals("new/f1", file1.getToFile());
        assertEquals(1, file1.getPatch().getDeltas().size());

        final UnifiedDiffFile file2 = diff.getFiles().get(1);
        assertEquals("diff -U0 old/f2 new/f2", file2.getDiffCommand());
        assertEquals("old/f2", file2.getFromFile());
        assertEquals("new/f2", file2.getToFile());
        assertEquals(1, file2.getPatch().getDeltas().size());

        assertNull(diff.getTail());
    }

    @Test
    public void testMultiChangedFilesOnOnePatchTwo() throws Exception {
        final UnifiedDiff diff = getUnifiedDiff("MultiChangedFilesOnOnePatch.txt");
        assertEquals(2, diff.getFiles().size());

        final UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertEquals("diff -u Origin/Test1.java Update/Test1.java",
                file1.getDiffCommand());
        assertEquals("Origin/Test1.java", file1.getFromFile());
        assertEquals("Update/Test1.java", file1.getToFile());
        assertEquals(1, file1.getPatch().getDeltas().size());

        final UnifiedDiffFile file2 = diff.getFiles().get(1);
        assertEquals("diff -u Origin/Test2.java Update/Test2.java",
                file2.getDiffCommand());
        assertEquals("Origin/Test2.java", file2.getFromFile());
        assertEquals("Update/Test2.java", file2.getToFile());
        assertEquals(1, file2.getPatch().getDeltas().size());

        assertNull(diff.getTail());
    }

    @Test
    public void testGitDiffPatch() throws Exception {
        final UnifiedDiff diff = getUnifiedDiff("PatchFileFromDiffTools/GitDiffPatch.txt");
        assertEquals(1, diff.getFiles().size());

        final UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertEquals("diff --git a/pom.xml b/pom.xml", file1.getDiffCommand());
        assertEquals("pom.xml", file1.getFromFile());
        assertEquals("pom.xml", file1.getToFile());
        assertEquals(1, file1.getPatch().getDeltas().size());

        assertNull(diff.getTail());
    }

    @Test
    public void testGitFormatPatchLinuxOne() throws Exception {
        final UnifiedDiff diff =
                getUnifiedDiff("PatchFileFromDiffTools/GitFormatPatchLinuxOne.txt");
        assertEquals(1, diff.getFiles().size());

        final UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertEquals("diff --git a/pom.xml b/pom.xml", file1.getDiffCommand());
        assertEquals("pom.xml", file1.getFromFile());
        assertEquals("pom.xml", file1.getToFile());
        assertEquals(1, file1.getPatch().getDeltas().size());

        assertEquals("2.7.4\n\n", diff.getTail());
    }

    @Test
    @Ignore("error: com.github.difflib.unifieddiff.UnifiedDiffParserException: "
            + "expected file start line not found "
            + "until https://github.com/java-diff-utils/java-diff-utils/issues/84")
    public void testGitFormatPatchLinuxTwo() throws Exception {
        final UnifiedDiff diff =
                getUnifiedDiff("PatchFileFromDiffTools/GitFormatPatchLinuxTwo.txt");
        assertEquals(2, diff.getFiles().size());

        final UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertEquals("diff --git a/config/ant-phase-verify.xml b/config/ant-phase-verify.xml",
                file1.getDiffCommand());
        assertEquals("config/ant-phase-verify.xml", file1.getFromFile());
        assertEquals("config/ant-phase-verify.xml", file1.getToFile());
        assertEquals(1, file1.getPatch().getDeltas().size());

        final UnifiedDiffFile file2 = diff.getFiles().get(1);
        assertEquals("diff --git a/src/test/resources-noncompilable/com/"
                + "puppycrawl/tools/checkstyle/grammar/"
                + "java14/InputJava14InstanceofWithPatternMatching.java "
                + "b/src/test/resources-noncompilable/com/puppycrawl/tools/checkstyle/grammar/"
                + "java14/InputJava14InstanceofWithPatternMatching.java", file2.getDiffCommand());
        assertEquals("/dev/null", file2.getFromFile());
        assertEquals("src/test/resources-noncompilable/com/puppycrawl/tools/checkstyle/"
                + "grammar/java14/"
                + "InputJava14InstanceofWithPatternMatching.java", file2.getToFile());
        assertEquals(1, file2.getPatch().getDeltas().size());

        assertNull(diff.getTail());
    }

    @Test
    public void testGitFormatPatchLinuxThree() throws Exception {
        final UnifiedDiff diff =
                getUnifiedDiff("PatchFileFromDiffTools/GitFormatPatchLinuxThree.txt");
        assertEquals(1, diff.getFiles().size());

        final UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertEquals("diff --git a/src/main/java/com/puppycrawl/tools/checkstyle/checks/javadoc/"
                + "JavadocMethodCheck.java"
                + " b/src/main/java/com/puppycrawl/tools/checkstyle/checks/javadoc/"
                + "JavadocMethodCheck.java", file1.getDiffCommand());
        assertEquals("src/main/java/com/puppycrawl/tools/checkstyle/checks/javadoc/"
                + "JavadocMethodCheck.java", file1.getFromFile());
        assertEquals("src/main/java/com/puppycrawl/tools/checkstyle/checks/javadoc/"
                + "JavadocMethodCheck.java", file1.getToFile());
        assertEquals(1, file1.getPatch().getDeltas().size());

        assertNull(diff.getTail());
    }

    @Test
    public void testGitFormatPatchWindows() throws Exception {
        final UnifiedDiff diff = getUnifiedDiff("PatchFileFromDiffTools/GitFormatPatchWindows.txt");
        assertEquals(1, diff.getFiles().size());

        final UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertEquals("diff --git a/src/main/java/com/puppycrawl/tools/checkstyle/Checker.java "
                        + "b/src/main/java/com/puppycrawl/tools/checkstyle/Checker.java",
                file1.getDiffCommand());
        assertEquals("src/main/java/com/puppycrawl/tools/checkstyle/Checker.java",
                file1.getFromFile());
        assertEquals("src/main/java/com/puppycrawl/tools/checkstyle/Checker.java",
                file1.getToFile());
        assertEquals(1, file1.getPatch().getDeltas().size());

        assertEquals("2.25.1.windows.1\n\n", diff.getTail());
    }

    @Test
    @Ignore("filename parsed error, Expected :<tests/test-check-pyflakes.t>, "
            + "Actual :<tests/test-check-pyflakes.t\tTue Jun 09 17:13:26 2020 -0400>\n"
            + "until https://github.com/java-diff-utils/java-diff-utils/issues/85")
    public void testHgDiffPatch() throws Exception {
        final UnifiedDiff diff = getUnifiedDiff("PatchFileFromDiffTools/HGDiffPatch.txt");
        assertEquals(1, diff.getFiles().size());

        final UnifiedDiffFile file1 = diff.getFiles().get(0);
        assertEquals("diff -r 83e41b73d115 -r a4438263b228 tests/test-check-pyflakes.t",
                file1.getDiffCommand());
        assertEquals("tests/test-check-pyflakes.t", file1.getFromFile());
        assertEquals("tests/test-check-pyflakes.t", file1.getToFile());
        assertEquals(1, file1.getPatch().getDeltas().size());

        assertNull(diff.getTail());
    }

    private UnifiedDiff getUnifiedDiff(String fileName) throws Exception {
        final String fullFileName = getPath(fileName);
        final FileInputStream ins = new FileInputStream(fullFileName);
        return UnifiedDiffReader.parseUnifiedDiff(ins);
    }
}

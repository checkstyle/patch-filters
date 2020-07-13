package com.puppycrawl.tools.checkstyle;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.patch.Patch;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JgitPatchParserGitDiffTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/filters/jgit-patch-parser/";
    }

    private FileInputStream getInput(String fileName) throws Exception {
        final String fullFileName = getPath(fileName);
        final FileInputStream ins = new FileInputStream(fullFileName);
        return ins;
    }

    @Test
    public void testMultiHunksOnSingleFile_DiffCommandDefaultSize() throws Exception {
        String realHunksOne = "@@ -2,6 +2,14 @@ public class Test1 {\n" +
                "     public void test1() {\n" +
                " \n" +
                "     }\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }\n" +
                " }\n" +
                " \n" +
                " ";
        String[] realHunksArrayOne = realHunksOne.split("\n");
        String realHunksTwo = "@@ -15,5 +23,7 @@ public class Test1 {\n" +
                " class Test {\n" +
                "     public void test1() {\n" +
                "         System.out.println();\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();\n" +
                "     }\n" +
                " }";
        String[] realHunksArrayTwo = realHunksTwo.split("\n");
        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksArrayOne));
        realHunksListOne.add(Arrays.asList(realHunksArrayTwo));
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);

        List<List<List<Integer>>> linePositionLists = new ArrayList<>();
        List<List<Integer>> linePositionListOne = new ArrayList<>();
        linePositionListOne.add(Arrays.asList(4,11));
        linePositionListOne.add(Arrays.asList(26,27));
        linePositionLists.add(linePositionListOne);

        String fileName = "GitDiffCommandDefaultSize/MultiChangesOnOneFilePatch.txt";
        testJgitPatch(fileName, realHunksLists, linePositionLists);
    }

    @Test
    public void testMultiHunksOnSingleFile_DiffCommandZeroSize() throws Exception {
        String realHunksOne = "@@ -4,0 +5,8 @@ public class Test1 {\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }";
        String[] realHunksArrayOne = realHunksOne.split("\n");
        String realHunksTwo = "@@ -17,0 +26,2 @@ class Test {\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();";
        String[] realHunksArrayTwo = realHunksTwo.split("\n");
        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksArrayOne));
        realHunksListOne.add(Arrays.asList(realHunksArrayTwo));
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);

        List<List<List<Integer>>> linePositionLists = new ArrayList<>();
        List<List<Integer>> linePositionListOne = new ArrayList<>();
        linePositionListOne.add(Arrays.asList(4,11));
        linePositionListOne.add(Arrays.asList(26,27));
        linePositionLists.add(linePositionListOne);

        String fileName = "GitDiffCommandZeroSize/MultiChangesOnOneFilePatchZeroSize.txt";
        testJgitPatch(fileName, realHunksLists, linePositionLists);
    }

    private void testJgitPatch(String fileName, List<List<List<String>>> realHunksLists,
                               List<List<List<Integer>>> linePositionLists) throws Exception {
        Patch patch = new Patch();
        patch.parse(getInput(fileName));
        for (int k = 0; k < patch.getFiles().size(); k++) {
            FileHeader fh = patch.getFiles().get(k);

            ArrayList<Edit> edits = fh.toEditList();
            List<List<Integer>> linePositionList = linePositionLists.get(k);
            for (int m = 0; m < edits.size(); m++) {
                assertTrue(edits.get(m).getBeginB() == linePositionList.get(m).get(0) ||
                        edits.get(m).getBeginB() == linePositionList.get(m).get(0) - 1);
            }

            List<List<String>> realHunksList = realHunksLists.get(k);
            for (int i= 0; i <fh.getHunks().size(); i++) {
                HunkHeader hh = fh.getHunks().get(i);
                byte[] b = new byte[hh.getEndOffset() - hh.getStartOffset()];
                System.arraycopy(hh.getBuffer(), hh.getStartOffset(), b, 0, b.length);
                RawText hrt = new RawText(b);

                List<String> hunkLines = new ArrayList<>(hrt.size());
                for (int j = 0; j < hrt.size(); j++) {
                    hunkLines.add(hrt.getString(j));
                }
                List<String> realHunks = realHunksList.get(i);
                for (int j = 0; j< hunkLines.size(); j++) {
                    assertEquals(realHunks.get(j), hunkLines.get(j));
                }
            }
        }
    }
}

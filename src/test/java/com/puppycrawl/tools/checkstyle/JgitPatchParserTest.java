package com.puppycrawl.tools.checkstyle;

import com.github.difflib.unifieddiff.UnifiedDiff;
import com.github.difflib.unifieddiff.UnifiedDiffFile;
import com.github.difflib.unifieddiff.UnifiedDiffReader;
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
import static org.junit.Assert.assertNull;

public class JgitPatchParserTest extends AbstractModuleTestSupport {

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
        String realHunksOne = "@@ -1,9 +1,17 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiChangeInOneFile.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiChangeInOneFile.b;\n" +
                " \n" +
                " public class Test1 {\n" +
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
        String realHunksTwo = "@@ -17,5 +25,7 @@\n" +
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
        String fileName = "DiffCommandDefaultSize/MultiHunksOnSingleFilePatch.txt";
        testJgitPatch(fileName, realHunksLists);
    }

    @Test
    public void testMultiHunksOnMultiFiles_DiffCommandDefaultSize() throws Exception {
        String realHunksOneOnOneFile = "@@ -1,9 +1,17 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                " \n" +
                " public class Test1 {\n" +
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
        String[] realHunksOneArrayOnOneFile = realHunksOneOnOneFile.split("\n");

        String realHunksTwoOnOneFile = "@@ -17,5 +25,7 @@\n" +
                " class Test {\n" +
                "     public void test1() {\n" +
                "         System.out.println();\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();\n" +
                "     }\n" +
                " }";
        String[] realHunksTwoArrayOnOneFile = realHunksTwoOnOneFile.split("\n");

        String realHunksOneOnTwoFile = "@@ -1,9 +1,17 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                " \n" +
                " public class Test2 {\n" +
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
        String[] realHunksOneArrayOnTwoFile = realHunksOneOnTwoFile.split("\n");

        String realHunksTwoOnTwoFile = "@@ -17,5 +25,7 @@\n" +
                " class Test {\n" +
                "     public void test1() {\n" +
                "         System.out.println();\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();\n" +
                "     }\n" +
                " }";
        String[] realHunksTwoArrayOnTwoFile = realHunksTwoOnTwoFile.split("\n");
        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksOneArrayOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksTwoArrayOnOneFile));
        List<List<String>> realHunksListTwo = new ArrayList<>();
        realHunksListTwo.add(Arrays.asList(realHunksOneArrayOnTwoFile));
        realHunksListTwo.add(Arrays.asList(realHunksTwoArrayOnTwoFile));
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);
        realHunksLists.add(realHunksListTwo);
        String fileName = "DiffCommandDefaultSize/MultiHunksONMultiFiles.txt";
        testJgitPatch(fileName, realHunksLists);

    }

    @Test
    public void testMultiHunksOnSingleFile_DiffCommandZeroSize() throws Exception {
        String realHunksOne = "@@ -1 +1 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiChangeInOneFile.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiChangeInOneFile.b;";
        String[] realHunksArrayOne = realHunksOne.split("\n");
        String realHunksTwo = "@@ -6,0 +7,8 @@\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }";
        String[] realHunksArrayTwo = realHunksTwo.split("\n");
        String realHunksThree = "@@ -19,0 +28,2 @@\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();";
        String[] realHunksArrayThree = realHunksThree.split("\n");
        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksArrayOne));
        realHunksListOne.add(Arrays.asList(realHunksArrayTwo));
        realHunksListOne.add(Arrays.asList(realHunksArrayThree));
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);
        String fileName = "DiffCommandZeroSize/MultiHunksOnSingleFilePatchZeroSize.txt";
        testJgitPatch(fileName, realHunksLists);
    }

    @Test
    public void testMultiHunksOnMultiFiles_DiffCommandZeroSize() throws Exception {
        String realHunksOneOnOneFile = "@@ -1 +1 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                "@@ -6,0 +7,8 @@\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }";
        String[] realHunksOneArrayOnOneFile = realHunksOneOnOneFile.split("\n");

        String realHunksTwoOnOneFile = "@@ -6,0 +7,8 @@\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }";
        String[] realHunksTwoArrayOnOneFile = realHunksTwoOnOneFile.split("\n");

        String realHunksThreeOnOneFile = "@@ -19,0 +28,2 @@\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();";
        String[] realHunksThreeArrayOnOneFile = realHunksThreeOnOneFile.split("\n");

        String realHunksOneOnTwoFile = "@@ -1 +1 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;";
        String[] realHunksOneArrayOnTwoFile = realHunksOneOnTwoFile.split("\n");

        String realHunksTwoOnTwoFile = "@@ -6,0 +7,8 @@\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }";
        String[] realHunksTwoArrayOnTwoFile = realHunksTwoOnTwoFile.split("\n");
        String realHunksThreeOnTwoFile = "@@ -19,0 +28,2 @@\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();";
        String[] realHunksThreeArrayOnTwoFile = realHunksThreeOnTwoFile.split("\n");
        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksOneArrayOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksTwoArrayOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksThreeArrayOnOneFile));
        List<List<String>> realHunksListTwo = new ArrayList<>();
        realHunksListTwo.add(Arrays.asList(realHunksOneArrayOnTwoFile));
        realHunksListTwo.add(Arrays.asList(realHunksTwoArrayOnTwoFile));
        realHunksListTwo.add(Arrays.asList(realHunksThreeArrayOnTwoFile));
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);
        realHunksLists.add(realHunksListTwo);
        String fileName = "DiffCommandZeroSize/MultiHunksONMultiFilesZeroSize.txt";
        testJgitPatch(fileName, realHunksLists);

    }

    @Test
    public void testHaveRenamedWithNoChangedFile_DiffCommandDefaultSize() throws Exception {
        String realHunksOneOnOneFile = "@@ -1,9 +1,17 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                " \n" +
                " public class Test1 {\n" +
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
        String[] realHunksArrayOne = realHunksOneOnOneFile.split("\n");
        String realHunksTwoOnOneFile = "@@ -17,5 +25,7 @@\n" +
                " class Test {\n" +
                "     public void test1() {\n" +
                "         System.out.println();\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();\n" +
                "     }\n" +
                " }";
        String[] realHunksArrayTwo = realHunksTwoOnOneFile.split("\n");

        String realHunksOneOnTwoFile = "@@ -1,21 +0,0 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "-\n" +
                "-public class Test2 {\n" +
                "-    public void test1() {\n" +
                "-\n" +
                "-    }\n" +
                "-}\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-class Test {\n" +
                "-    public void test1() {\n" +
                "-        System.out.println();\n" +
                "-    }\n" +
                "-}";
        String[] realHunksOneArrayOnTwoFile = realHunksOneOnTwoFile.split("\n");

        String realHunksOneOnThreeFile = "@@ -0,0 +1,31 @@\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                "+\n" +
                "+public class Test2 {\n" +
                "+    public void test1() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }\n" +
                "+}\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+class Test {\n" +
                "+    public void test1() {\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();\n" +
                "+    }\n" +
                "+}";
        String[] realHunksOneArrayOnThreeFile = realHunksOneOnThreeFile.split("\n");

        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksArrayOne));
        realHunksListOne.add(Arrays.asList(realHunksArrayTwo));
        List<List<String>> realHunksListTwo = new ArrayList<>();
        realHunksListTwo.add(Arrays.asList(realHunksOneArrayOnTwoFile));
        List<List<String>> realHunksListThree = new ArrayList<>();
        realHunksListThree.add(Arrays.asList(realHunksOneArrayOnThreeFile));
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);
        realHunksLists.add(realHunksListTwo);
        realHunksLists.add(realHunksListThree);
        String fileName = "DiffCommandDefaultSize/HaveRenamedWithNoChangedFilePatch.txt";
        testJgitPatch(fileName, realHunksLists);
    }

    @Test
    public void testHaveRenamedWithNoChangedFile_DiffCommandZeroSize() throws Exception {
        String realHunksOneOnOneFile = "@@ -1 +1 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                "@@ -6,0 +7,8 @@\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }";
        String[] realHunksArrayOneOnOneFile = realHunksOneOnOneFile.split("\n");
        String realHunksTwoOnOneFile = "@@ -6,0 +7,8 @@\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }";
        String[] realHunksArrayTwoOnOneFile = realHunksTwoOnOneFile.split("\n");

        String realHunksThreeOnOneFile = "@@ -19,0 +28,2 @@\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();";
        String[] realHunksArrayThreeOnOneFile = realHunksThreeOnOneFile.split("\n");

        String realHunksOneOnTwoFile = "@@ -1,21 +0,0 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "-\n" +
                "-public class Test2 {\n" +
                "-    public void test1() {\n" +
                "-\n" +
                "-    }\n" +
                "-}\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-class Test {\n" +
                "-    public void test1() {\n" +
                "-        System.out.println();\n" +
                "-    }\n" +
                "-}";
        String[] realHunksOneArrayOnTwoFile = realHunksOneOnTwoFile.split("\n");

        String realHunksOneOnThreeFile = "@@ -0,0 +1,31 @@\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                "+\n" +
                "+public class Test2 {\n" +
                "+    public void test1() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }\n" +
                "+}\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+\n" +
                "+class Test {\n" +
                "+    public void test1() {\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();\n" +
                "+    }\n" +
                "+}";
        String[] realHunksOneArrayOnThreeFile = realHunksOneOnThreeFile.split("\n");

        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksArrayOneOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksArrayTwoOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksArrayThreeOnOneFile));
        List<List<String>> realHunksListTwo = new ArrayList<>();
        realHunksListTwo.add(Arrays.asList(realHunksOneArrayOnTwoFile));
        List<List<String>> realHunksListThree = new ArrayList<>();
        realHunksListThree.add(Arrays.asList(realHunksOneArrayOnThreeFile));
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);
        realHunksLists.add(realHunksListTwo);
        realHunksLists.add(realHunksListThree);
        String fileName = "DiffCommandZeroSize/HaveRenamedWithNoChangedFilePatchZeroSize.txt";
        testJgitPatch(fileName, realHunksLists);
    }

    @Test
    public void testHaveRemovedWithNoChangedFile_DiffCommandDefaultSize() throws Exception {
        String realHunksOneOnOneFile = "@@ -1,9 +1,17 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                " \n" +
                " public class Test1 {\n" +
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
        String[] realHunksArrayOneOnOneFile = realHunksOneOnOneFile.split("\n");
        String realHunksTwoOnOneFile = "@@ -17,5 +25,7 @@\n" +
                " class Test {\n" +
                "     public void test1() {\n" +
                "         System.out.println();\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();\n" +
                "     }\n" +
                " }";
        String[] realHunksArrayTwoOnOneFile = realHunksTwoOnOneFile.split("\n");

        String realHunksOneOnTwoFile = "@@ -1,21 +0,0 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "-\n" +
                "-public class Test2 {\n" +
                "-    public void test1() {\n" +
                "-\n" +
                "-    }\n" +
                "-}\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-class Test {\n" +
                "-    public void test1() {\n" +
                "-        System.out.println();\n" +
                "-    }\n" +
                "-}";
        String[] realHunksOneArrayOnTwoFile = realHunksOneOnTwoFile.split("\n");

        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksArrayOneOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksArrayTwoOnOneFile));
        List<List<String>> realHunksListTwo = new ArrayList<>();
        realHunksListTwo.add(Arrays.asList(realHunksOneArrayOnTwoFile));
        List<List<String>> realHunksListThree = new ArrayList<>();
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);
        realHunksLists.add(realHunksListTwo);
        realHunksLists.add(realHunksListThree);
        String fileName = "DiffCommandDefaultSize/HaveRemovedFilePatch.txt";
        testJgitPatch(fileName, realHunksLists);
    }

    @Test
    public void testHaveRemovedWithNoChangedFile_DiffCommandZeroSize() throws Exception {
        String realHunksOneOnOneFile = "@@ -1 +1 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;";
        String[] realHunksArrayOneOnOneFile = realHunksOneOnOneFile.split("\n");
        String realHunksTwoOnOneFile = "@@ -6,0 +7,8 @@\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }";
        String[] realHunksArrayTwoOnOneFile = realHunksTwoOnOneFile.split("\n");

        String realHunksThreeOnOneFile = "@@ -19,0 +28,2 @@\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();";
        String[] realHunksArrayThreeOnOneFile = realHunksThreeOnOneFile.split("\n");

        String realHunksOneOnTwoFile = "@@ -1,21 +0,0 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "-\n" +
                "-public class Test2 {\n" +
                "-    public void test1() {\n" +
                "-\n" +
                "-    }\n" +
                "-}\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-\n" +
                "-class Test {\n" +
                "-    public void test1() {\n" +
                "-        System.out.println();\n" +
                "-    }\n" +
                "-}";
        String[] realHunksOneArrayOnTwoFile = realHunksOneOnTwoFile.split("\n");

        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksArrayOneOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksArrayTwoOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksArrayThreeOnOneFile));
        List<List<String>> realHunksListTwo = new ArrayList<>();
        realHunksListTwo.add(Arrays.asList(realHunksOneArrayOnTwoFile));
        List<List<String>> realHunksListThree = new ArrayList<>();
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);
        realHunksLists.add(realHunksListTwo);
        realHunksLists.add(realHunksListThree);
        String fileName = "DiffCommandZeroSize/HaveRemovedFilePatchZeroSize.txt";
        testJgitPatch(fileName, realHunksLists);
    }

    @Test
    public void testHaveMovedCodeFile_DiffCommandDefaultSize() throws Exception {
        String realHunksOneOnOneFile = "@@ -1,8 +1,8 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                " \n" +
                "-public class Test1 {\n" +
                "+class Test {\n" +
                "     public void test1() {\n" +
                "-\n" +
                "+        System.out.println();\n" +
                "     }\n" +
                " }\n" +
                " ";
        String[] realHunksArrayOneOnOneFile = realHunksOneOnOneFile.split("\n");
        String realHunksTwoOnOneFile = "@@ -14,8 +14,8 @@\n" +
                " \n" +
                " \n" +
                " \n" +
                "-class Test {\n" +
                "+public class Test1 {\n" +
                "     public void test1() {\n" +
                "-        System.out.println();\n" +
                "+\n" +
                "     }\n" +
                " }";
        String[] realHunksArrayTwoOnOneFile = realHunksTwoOnOneFile.split("\n");

        String realHunksOneOnTwoFile = "@@ -1,9 +1,17 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                " \n" +
                " public class Test2 {\n" +
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
        String[] realHunksOneArrayOnTwoFile = realHunksOneOnTwoFile.split("\n");

        String realHunksTwoOnTwoFile = "@@ -17,5 +25,7 @@\n" +
                " class Test {\n" +
                "     public void test1() {\n" +
                "         System.out.println();\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();\n" +
                "     }\n" +
                " }";
        String[] realHunksTwoArrayOnTwoFile = realHunksTwoOnTwoFile.split("\n");

        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksArrayOneOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksArrayTwoOnOneFile));
        List<List<String>> realHunksListTwo = new ArrayList<>();
        realHunksListTwo.add(Arrays.asList(realHunksOneArrayOnTwoFile));
        realHunksListTwo.add(Arrays.asList(realHunksTwoArrayOnTwoFile));
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);
        realHunksLists.add(realHunksListTwo);
        String fileName = "DiffCommandDefaultSize/HaveMovedCodeFilePatch.txt";
        testJgitPatch(fileName, realHunksLists);
    }

    @Test
    public void testHaveMovedCodeFile_DiffCommandZeroSize() throws Exception {
        String realHunksOneOnOneFile = "@@ -1 +1 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;";
        String[] realHunksArrayOneOnOneFile = realHunksOneOnOneFile.split("\n");
        String realHunksTwoOnOneFile = "@@ -3 +3 @@\n" +
                "-public class Test1 {\n" +
                "+class Test {";
        String[] realHunksArrayTwoOnOneFile = realHunksTwoOnOneFile.split("\n");

        String realHunksThreeOnOneFile = "@@ -5 +5 @@\n" +
                "-\n" +
                "+        System.out.println();";
        String[] realHunksArrayThreeOnOneFile = realHunksThreeOnOneFile.split("\n");

        String realHunksFourOnOneFile = "@@ -17 +17 @@\n" +
                "-class Test {\n" +
                "+public class Test1 {";
        String[] realHunksArrayFourOnOneFile = realHunksFourOnOneFile.split("\n");

        String realHunksFiveOnOneFile = "@@ -19 +19 @@\n" +
                "-        System.out.println();\n" +
                "+";
        String[] realHunksArrayFiveOnOneFile = realHunksFiveOnOneFile.split("\n");

        String realHunksOneOnTwoFile = "@@ -1 +1 @@\n" +
                "-package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.a;\n" +
                "+package checkstyle_demo.PatchSuppression.MultiHunksOnMultiFiles.b;\n" +
                "@@ -6,0 +7,8 @@\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }";
        String[] realHunksOneArrayOnTwoFile = realHunksOneOnTwoFile.split("\n");

        String realHunksTwoOnTwoFile = "@@ -6,0 +7,8 @@\n" +
                "+\n" +
                "+    public void test2() {\n" +
                "+\n" +
                "+    }\n" +
                "+\n" +
                "+    public void test3() {\n" +
                "+\n" +
                "+    }";
        String[] realHunksTwoArrayOnTwoFile = realHunksTwoOnTwoFile.split("\n");

        String realHunksThreeOnTwoFile = "@@ -19,0 +28,2 @@\n" +
                "+        System.out.println();\n" +
                "+        System.out.println();";
        String[] realHunksThreeArrayOnTwoFile = realHunksThreeOnTwoFile.split("\n");


        List<List<String>> realHunksListOne = new ArrayList<>();
        realHunksListOne.add(Arrays.asList(realHunksArrayOneOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksArrayTwoOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksArrayThreeOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksArrayFourOnOneFile));
        realHunksListOne.add(Arrays.asList(realHunksArrayFiveOnOneFile));
        List<List<String>> realHunksListTwo = new ArrayList<>();
        realHunksListTwo.add(Arrays.asList(realHunksOneArrayOnTwoFile));
        realHunksListTwo.add(Arrays.asList(realHunksTwoArrayOnTwoFile));
        realHunksListTwo.add(Arrays.asList(realHunksThreeArrayOnTwoFile));
        List<List<List<String>>> realHunksLists = new ArrayList<>();
        realHunksLists.add(realHunksListOne);
        realHunksLists.add(realHunksListTwo);
        String fileName = "DiffCommandZeroSize/HaveMovedCodeFilePatchZeroSize.txt";
        testJgitPatch(fileName, realHunksLists);
    }


    private void testJgitPatch(String fileName, List<List<List<String>>> realHunksLists) throws Exception {
        Patch patch = new Patch();
        patch.parse(getInput(fileName));
        for (int k = 0; k < patch.getFiles().size(); k++) {
            FileHeader fh = patch.getFiles().get(k);
            ArrayList<Edit> edits = fh.toEditList();
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

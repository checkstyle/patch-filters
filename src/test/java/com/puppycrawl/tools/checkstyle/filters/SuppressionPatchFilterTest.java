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

package com.puppycrawl.tools.checkstyle.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.PackageObjectFactory;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.api.RootModule;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import com.puppycrawl.tools.checkstyle.internal.utils.BriefUtLogger;

public class SuppressionPatchFilterTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/filters/";
    }

    @Test
    @Ignore("until https://github.com/checkstyle/patch-filters/issues/89")
    public void testAccept() throws Exception {
        final String fileName = getPath("MethodCount/MethodCountPatch.txt");
        final SuppressionPatchFilter filter = createSuppressionPatchFilter(fileName);
        final LocalizedMessage message = new LocalizedMessage(7, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, "MethodCountUpdate.java", message);

        assertTrue(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    @Test
    @Ignore("until https://github.com/checkstyle/patch-filters/issues/89")
    public void testMultiChangesOnOneFileOne() throws Exception {
        final String fileName = getPath("MultiChangesOnOneFilePatch.txt");
        final SuppressionPatchFilter filter = createSuppressionPatchFilter(fileName);
        final LocalizedMessage message = new LocalizedMessage(4, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, "Update.java", message);
        assertTrue(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    @Test
    @Ignore("until https://github.com/checkstyle/patch-filters/issues/89")
    public void testMultiChangedFilesOnOnePatch() throws Exception {
        final String fileName = getPath("MultiChangedFilesOnOnePatch.txt");
        final SuppressionPatchFilter filter = createSuppressionPatchFilter(fileName);
        final LocalizedMessage message1 = new LocalizedMessage(7, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev1 = new AuditEvent(this, "Update/Test2.java", message1);
        assertTrue(filter.accept(ev1),
                "Audit event should be rejected when there are no matching patch filters");
        final LocalizedMessage message2 = new LocalizedMessage(77, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev2 = new AuditEvent(this, "Update/Test1.java", message2);
        assertFalse(filter.accept(ev2),
                "Audit event should be rejected when there are no matching patch filters");
        final LocalizedMessage message3 = new LocalizedMessage(7, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev3 = new AuditEvent(this, "Update/Test1.java", message1);
        assertTrue(filter.accept(ev1),
                "Audit event should be rejected when there are no matching patch filters");
    }

    @Test
    @Ignore("until https://github.com/checkstyle/patch-filters/issues/89")
    public void testBoundaryOne() throws Exception {
        final String fileName = getPath("BoundaryTestPatchOne.txt");
        final SuppressionPatchFilter filter = createSuppressionPatchFilter(fileName);
        final LocalizedMessage message = new LocalizedMessage(3, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, "Update1.java", message);
        assertTrue(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    @Test
    public void testAddOptionTwo() throws Exception {
        final String patchFileName = getPath("eclipse-cs-patch-1c057d1-9d473b4.txt");
        final SuppressionPatchFilter changedfilter =
                createSuppressionPatchFilter(patchFileName, "patchedline");
        final SuppressionPatchFilter addedfilter =
                createSuppressionPatchFilter(patchFileName, "newline");
        final List<Integer> addedLineList = Arrays.asList(4, 5, 6, 7, 8);
        final List<Integer> changedLineList = Arrays.asList(27, 39, 42, 57, 64, 74, 85, 93,
                94, 98, 99, 100, 114, 115, 116, 117, 149, 150, 154, 155, 158, 159, 164, 165,
                172, 173, 178, 179, 184, 190, 191, 194, 199, 208, 209, 210, 212, 213, 215,
                237, 238, 240, 241, 243, 244, 251, 253, 255, 256, 257, 259, 272, 274, 277,
                284, 331, 332, 337, 339, 346);
        final String fileName = "net.sf.eclipsecs.checkstyle/test/net/sf"
                + "/eclipsecs/checkstyle/ChecksTest.java";
        testAddedLine(addedfilter, changedfilter, fileName, addedLineList, changedLineList);
    }

    private void testAddedLine(SuppressionPatchFilter addedfilter,
                               SuppressionPatchFilter changedfilter, String fileName,
                               List<Integer> addedLineList, List<Integer> changedLineList) {
        for (int lineNo: addedLineList) {
            shouldAcceptLine(addedfilter, lineNo, fileName);
        }
        for (int lineNo: changedLineList) {
            shouldRejectLine(addedfilter, lineNo, fileName);
        }
        for (int lineNo: addedLineList) {
            shouldAcceptLine(changedfilter, lineNo, fileName);
        }
        for (int lineNo: changedLineList) {
            shouldAcceptLine(changedfilter, lineNo, fileName);
        }
    }

    private void shouldAcceptLine(SuppressionPatchFilter filter, int lineNo, String fileName) {
        final LocalizedMessage message = new LocalizedMessage(lineNo, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, fileName, message);
        assertTrue(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    private void shouldRejectLine(SuppressionPatchFilter filter, int lineNo, String fileName) {
        final LocalizedMessage message = new LocalizedMessage(lineNo, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, fileName, message);
        assertFalse(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    private static SuppressionPatchFilter
        createSuppressionPatchFilter(String fileName, String add) throws Exception {
        final SuppressionPatchFilter suppressionPatchFilter = new SuppressionPatchFilter();
        suppressionPatchFilter.setFile(fileName);
        suppressionPatchFilter.setStrategy(add);
        suppressionPatchFilter.finishLocalSetup();
        return suppressionPatchFilter;
    }

    private static SuppressionPatchFilter
        createSuppressionPatchFilter(String fileName) throws Exception {
        return createSuppressionPatchFilter(fileName, "changed");
    }

    @Test
    @Ignore("until https://github.com/checkstyle/patch-filters/issues/88 "
            + "when testByConfig can process a directory contains more than one file")
    public void testFileLength() throws Exception {
        final String inputFile = "strategy/FileLength/Test";

        final String defaultContextConfigPathOne =
                "strategy/FileLength/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/FileLength/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "1: File length is 14 lines (max allowed is 5).",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/FileLength/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/FileLength/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "1: File length is 14 lines (max allowed is 5).",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    public void testLineLength() throws Exception {
        final String inputFile = "strategy/LineLength/Test.java";

        final String defaultContextConfigPathOne =
                "strategy/LineLength/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/LineLength/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "10: Line is longer than 80 characters (found 163).",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/LineLength/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/LineLength/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "5: Line is longer than 80 characters (found 155).",
            "10: Line is longer than 80 characters (found 163).",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    public void testFileTabCharacter() throws Exception {
        final String inputFile = "strategy/FileTabCharacter/Test.java";

        final String defaultContextConfigPathOne =
                "strategy/FileTabCharacter/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/FileTabCharacter/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "21:25: Line contains a tab character.",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/FileTabCharacter/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/FileTabCharacter/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "5:25: Line contains a tab character.",
            "21:25: Line contains a tab character.",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    public void testFileTabCharacterMoveCodeInSameFileWithoutChanges() throws Exception {
        final String inputFile =
                "strategy/FileTabCharacter/MoveCodeInSameFileWithoutChanges/Test.java";

        final String defaultContextConfigPathOne =
                "strategy/FileTabCharacter/MoveCodeInSameFileWithoutChanges/"
                        + "newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/FileTabCharacter/MoveCodeInSameFileWithoutChanges/"
                        + "newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "19:25: Line contains a tab character.",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/FileTabCharacter/MoveCodeInSameFileWithoutChanges"
                        + "/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/FileTabCharacter/MoveCodeInSameFileWithoutChanges/"
                        + "patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "19:25: Line contains a tab character.",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    public void testFileTabCharacterMoveCodeInSameFileWithMinorChanges() throws Exception {
        final String inputFile =
                "strategy/FileTabCharacter/MoveCodeInSameFileWithMinorChanges/Test.java";

        final String defaultContextConfigPathOne =
                "strategy/FileTabCharacter/"
                        + "MoveCodeInSameFileWithMinorChanges/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/FileTabCharacter/"
                        + "MoveCodeInSameFileWithMinorChanges/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "19:25: Line contains a tab character.",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/FileTabCharacter/MoveCodeInSameFileWithMinorChanges"
                        + "/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/FileTabCharacter/MoveCodeInSameFileWithMinorChanges"
                        + "/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "19:25: Line contains a tab character.",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    public void testFileTabCharacterSingleChangedLine() throws Exception {
        final String inputFile = "strategy/FileTabCharacter/SingleChangedLine/Test.java";

        final String defaultContextConfigPathOne =
                "strategy/FileTabCharacter/SingleChangedLine/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/FileTabCharacter/SingleChangedLine/newline/zeroContextConfig.xml";
        final String[] expectedOne = {};
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/FileTabCharacter/SingleChangedLine/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/FileTabCharacter/SingleChangedLine/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "19:25: Line contains a tab character.",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    public void testFileTabCharacterSingleNewLine() throws Exception {
        final String inputFile = "strategy/FileTabCharacter/SingleNewLine/Test.java";

        final String defaultContextConfigPathOne =
                "strategy/FileTabCharacter/SingleNewLine/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/FileTabCharacter/SingleNewLine/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "20:25: Line contains a tab character.",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/FileTabCharacter/SingleNewLine/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/FileTabCharacter/SingleNewLine/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "20:25: Line contains a tab character.",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    @Ignore("until https://github.com/checkstyle/patch-filters/issues/88 "
            + "when testByConfig can process a directory contains more than one file")
    public void testRegexpOnFilename() throws Exception {
        final String inputFile = "strategy/RegexpOnFilename/Test";

        final String defaultContextConfigPathOne =
                "strategy/RegexpOnFilename/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/RegexpOnFilename/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "1: File match folder pattern '' and file pattern '\\.java$'.",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/RegexpOnFilename/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/RegexpOnFilename/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "1: File match folder pattern '' and file pattern '\\.java$'.",
            "1: File match folder pattern '' and file pattern '\\.java$'.",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    @Ignore("until https://github.com/checkstyle/patch-filters/issues/88 "
            + "when testByConfig can process a directory contains more than one file")
    public void testNewlineAtEndOfFile() throws Exception {
        final String inputFile = "strategy/NewlineAtEndOfFile/Test";

        final String defaultContextConfigPathOne =
                "strategy/NewlineAtEndOfFile/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/NewlineAtEndOfFile/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "1: File does not end with a newline.",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/NewlineAtEndOfFile/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/NewlineAtEndOfFile/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "1: File does not end with a newline.",
            "1: File does not end with a newline.",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    public void testOrderedProperties() throws Exception {
        final String inputFile = "strategy/OrderedProperties/test.properties";

        final String defaultContextConfigPathOne =
                "strategy/OrderedProperties/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/OrderedProperties/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "6: Property key 'key.pag' is not in the right order "
                    + "with previous property 'key.png'.",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/OrderedProperties/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/OrderedProperties/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "6: Property key 'key.pag' is not in the right order "
                    + "with previous property 'key.png'.",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    @Ignore("UniquePropertiesCheck has a problem is that violation's "
            + "line information is not on newline,"
            + " but on original duplicated property, this maybe solve on context strategy?")
    public void testUniqueProperties() throws Exception {
        final String inputFile = "strategy/UniqueProperties/test.properties";

        final String defaultContextConfigPathOne =
                "strategy/UniqueProperties/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/UniqueProperties/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "4: Duplicated property 'key.sub' (2 occurrence(s)).",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/UniqueProperties/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/UniqueProperties/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "4: Duplicated property 'key.sub' (2 occurrence(s)).",
            "18: Duplicated property 'key.sub' (2 occurrence(s)).",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    @Ignore("RegexpMultilineCheck has a problem is that violation's "
            + "line information is not on newline,"
            + " but on original duplicated property, this maybe solve on context strategy?")
    public void testRegexpMultiline() throws Exception {
        final String inputFile = "strategy/RegexpMultiline/Test.java";

        final String defaultContextConfigPathOne =
                "strategy/RegexpMultiline/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/RegexpMultiline/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "28: Line matches the illegal pattern 'System\\.out\\.\\n                print\\('.",
        };
        testByConfig(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfig(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/RegexpMultiline/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/RegexpMultiline/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "18: Line matches the illegal pattern 'System\\.out\\.\\n                print\\('.",
            "28: Line matches the illegal pattern 'System\\.out\\.\\n                print\\('.",
        };
        testByConfig(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfig(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    @Test
    public void testRegexpSingleline() throws Exception {

        final String inputFile = "strategy/RegexpSingleline/Input.java";

        final String configPathOne = "strategy/RegexpSingleline/newline/config.xml";
        final String[] expectedOne = {
            "7: Line matches the illegal pattern 'System.out.print'.",
            "12: Line matches the illegal pattern 'System.out.print'.",
        };
        testByConfig(configPathOne, inputFile, expectedOne);

        final String configPathTwo = "strategy/RegexpSingleline/patchedline/config.xml";
        final String[] expectedTwo = {
            "3: Line matches the illegal pattern 'System.out.print'.",
            "7: Line matches the illegal pattern 'System.out.print'.",
            "12: Line matches the illegal pattern 'System.out.print'.",
        };
        testByConfig(configPathTwo, inputFile, expectedTwo);
    }

    private void testByConfig(String configPath, String inputFile, String[] expected)
            throws Exception {
        // we can add here any variable to provide path to patch name by PropertiesExpander
        final Configuration config = ConfigurationLoader.loadConfiguration(
                getPath(configPath),
                new PropertiesExpander(System.getProperties()));
        final ClassLoader moduleClassLoader = SuppressionPatchFilter.class.getClassLoader();
        final ModuleFactory factory = new PackageObjectFactory(
            SuppressionPatchFilter.class.getPackage().getName(), moduleClassLoader);

        final RootModule rootModule = (RootModule) factory.createModule(config.getName());
        rootModule.setModuleClassLoader(moduleClassLoader);
        rootModule.configure(config);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rootModule.addListener(new BriefUtLogger(stream));

        // run RootModule
        final String path = getPath(inputFile);
        final List<File> files = Collections.singletonList(
                new File(path));
        final int errorCounter = rootModule.process(files);

        // process each of the lines
        try (ByteArrayInputStream inputStream =
                new ByteArrayInputStream(stream.toByteArray());
             LineNumberReader lnr = new LineNumberReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            final List<String> actuals = lnr.lines().limit(expected.length)
                    .sorted().collect(Collectors.toList());
            Arrays.sort(expected);

            for (int i = 0; i < expected.length; i++) {
                final String expectedResult = path + ":" + expected[i];
                assertEquals("error message " + i, expectedResult, actuals.get(i));
            }

            assertEquals("unexpected output: " + lnr.readLine(),
                    expected.length, errorCounter);
        }
    }
}

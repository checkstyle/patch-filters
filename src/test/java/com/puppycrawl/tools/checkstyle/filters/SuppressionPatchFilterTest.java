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
    @Ignore
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
    @Ignore
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
    @Ignore
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
    @Ignore
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

    @Ignore
    @Test
    public void testUniqueProperties() throws Exception {
        final String configPathOne = "strategy/newline/UniqueProperties/config.xml";
        final String inputFileOne = "strategy/newline/UniqueProperties/test.properties";
        final String[] expectedOne = {
            "4: Duplicated property 'key.sub' (2 occurrence(s)). [UniqueProperties]",
        };
        testByConfig(configPathOne, inputFileOne, expectedOne);
    }

    @Test
    public void testInputRegexpSingleline() throws Exception {

        final String configPathOne = "strategy/newline/InputRegexpSingleline/config.xml";
        final String inputFileOne = "strategy/newline/InputRegexpSingleline/Input.java";
        final String[] expectedOne = {
            "7: Line matches the illegal pattern 'System.out.print'.",
            "12: Line matches the illegal pattern 'System.out.print'.",
        };
        testByConfig(configPathOne, inputFileOne, expectedOne);

        final String configPathTwo = "strategy/patchedline/InputRegexpSingleline/config.xml";
        final String inputFileTwo = "strategy/patchedline/InputRegexpSingleline/Input.java";
        final String[] expectedTwo = {
            "3: Line matches the illegal pattern 'System.out.print'.",
            "7: Line matches the illegal pattern 'System.out.print'.",
            "12: Line matches the illegal pattern 'System.out.print'.",
        };
        testByConfig(configPathTwo, inputFileTwo, expectedTwo);
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

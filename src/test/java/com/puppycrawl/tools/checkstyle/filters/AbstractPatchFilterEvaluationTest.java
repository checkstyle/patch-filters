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

package com.puppycrawl.tools.checkstyle.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.PackageObjectFactory;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.RootModule;
import com.puppycrawl.tools.checkstyle.bdd.InlineConfigParser;
import com.puppycrawl.tools.checkstyle.bdd.TestInputViolation;
import com.puppycrawl.tools.checkstyle.internal.utils.BriefUtLogger;

/**
 * Base class for patch filter evaluation tests.
 *
 * <p>Each test bundle declares its expected violations in one of two ways:</p>
 * <ul>
 *     <li>Inline {@code // violation 'message'} comments in the input files, parsed by the main
 *     library's {@link InlineConfigParser#getViolationsFromInputFile(String)} (preferred). The
 *     column is not asserted, matching the inline-violation convention shared with checkstyle:
 *     these filters operate on lines, not columns.</li>
 *     <li>A legacy {@code expected.txt} file listing {@code file:line:column: message}
 *     entries.</li>
 * </ul>
 *
 * <p>The presence of {@code expected.txt} selects the mode, so bundles can be migrated to inline
 * comments one at a time by adding the comments and deleting {@code expected.txt}.</p>
 */
abstract class AbstractPatchFilterEvaluationTest extends AbstractModuleTestSupport {

    private static final String CONTEXT_CONFIG_PATTERN = "(default|zero)ContextConfig.xml";

    private static final FilenameFilter INPUT_FILE_FILTER =
            (dir, name) -> name.endsWith(".java") || name.endsWith(".properties");

    protected abstract String getPatchFileLocation();

    protected void testByConfig(String configPath)
            throws Exception {
        final String inputFile = configPath.replaceFirst(CONTEXT_CONFIG_PATTERN, "");
        // we can add here any variable to provide path to patch name by PropertiesExpander
        System.setProperty("tp", getPatchFileLocation() + inputFile);
        final Configuration config = ConfigurationLoader.loadConfiguration(
                getPath(configPath), new PropertiesExpander(System.getProperties()));
        final RootModule rootModule = createRootModule(config);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rootModule.addListener(new BriefUtLogger(stream));

        final String path = getPath(inputFile);
        final int errorCounter = processFiles(rootModule, path);
        assertResults(path, errorCounter, stream);
    }

    private static RootModule createRootModule(Configuration config) throws Exception {
        final ClassLoader moduleClassLoader = SuppressionPatchFilter.class.getClassLoader();
        final ModuleFactory factory = new PackageObjectFactory(
                SuppressionPatchFilter.class.getPackage().getName(), moduleClassLoader);
        final RootModule rootModule = (RootModule) factory.createModule(config.getName());
        rootModule.setModuleClassLoader(moduleClassLoader);
        rootModule.configure(config);
        return rootModule;
    }

    private static int processFiles(RootModule rootModule, String path) throws Exception {
        return rootModule.process(listInputFiles(path));
    }

    private static List<File> listInputFiles(String path) throws IOException {
        final File[] files = new File(path).listFiles(INPUT_FILE_FILTER);
        if (files == null) {
            throw new IOException("there is no java file in this directory.");
        }
        final List<File> theFiles = new ArrayList<>(Arrays.asList(files));
        Collections.sort(theFiles);
        return theFiles;
    }

    private void assertResults(String path, int errorCounter, ByteArrayOutputStream stream)
            throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(stream.toByteArray());
             LineNumberReader lnr = new LineNumberReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            final List<String> actuals = lnr.lines().toList();
            final File expectedFile = new File(path, "expected.txt");
            if (expectedFile.exists()) {
                assertLegacyResults(expectedFile, path, errorCounter, actuals);
            }
            else {
                assertInlineResults(path, errorCounter, actuals);
            }
        }
    }

    private static void assertLegacyResults(File expectedFile, String path, int errorCounter,
                                            List<String> actuals) throws IOException {
        final List<String> expected = Files.readAllLines(expectedFile.toPath());
        for (int index = 0; index < expected.size(); index++) {
            final String expectedResult = path + File.separator + expected.get(index);
            assertEquals(expectedResult, actuals.get(index),
                    "error message " + index + ". Expected file: " + expectedFile);
        }
        assertEquals(expected.size(), errorCounter, "unexpected output");
    }

    /**
     * Verifies the actual output against inline {@code // violation 'message'} comments in the
     * bundle's input files. Violations are parsed with the main library's
     * {@link InlineConfigParser#getViolationsFromInputFile(String)} and checked per file, then a
     * total count guards against any reported violation that is not attributed to a file.
     *
     * @param path the directory containing the bundle's input files
     * @param errorCounter number of violations reported by checkstyle
     * @param actuals actual output lines
     * @throws Exception if an input file cannot be parsed
     */
    private static void assertInlineResults(String path, int errorCounter, List<String> actuals)
            throws Exception {
        int expectedTotal = 0;
        for (File file : listInputFiles(path)) {
            final List<TestInputViolation> violations =
                    InlineConfigParser.getViolationsFromInputFile(file.getPath());
            final String prefix = file.getPath() + ":";
            final List<String> actualViolations = actuals.stream()
                    .filter(line -> line.startsWith(prefix))
                    .map(line -> line.substring(prefix.length()))
                    .toList();
            verifyViolations(file.getPath(), violations, actualViolations);
            expectedTotal += violations.size();
        }
        assertEquals(expectedTotal, errorCounter,
                "number of violations does not match inline comments");
    }

    /**
     * Replicates {@code AbstractModuleTestSupport.verifyViolations}, which is not visible here:
     * the reported violation lines must equal the commented lines, then each violation must match
     * its {@link TestInputViolation#toRegex()}. The column is not asserted, matching the
     * inline-violation convention shared with checkstyle: these filters operate on lines.
     *
     * @param file file path, for assertion messages
     * @param testInputViolations expected violations parsed from inline comments
     * @param actualViolations actual violations for the file, as {@code line:column: message}
     */
    private static void verifyViolations(String file,
                                         List<TestInputViolation> testInputViolations,
                                         List<String> actualViolations) {
        final List<Integer> actualViolationLines = actualViolations.stream()
                .map(violation -> violation.substring(0, violation.indexOf(':')))
                .map(Integer::valueOf)
                .toList();
        final List<Integer> expectedViolationLines = testInputViolations.stream()
                .map(TestInputViolation::getLineNo)
                .toList();
        assertEquals(expectedViolationLines, actualViolationLines,
                "Violation lines for " + file + " differ.");
        for (int index = 0; index < actualViolations.size(); index++) {
            assertTrue(actualViolations.get(index).matches(
                    testInputViolations.get(index).toRegex()),
                    "Actual and expected violations differ for " + file);
        }
    }
}

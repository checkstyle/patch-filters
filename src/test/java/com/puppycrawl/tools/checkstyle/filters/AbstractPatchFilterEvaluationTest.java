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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import java.util.Arrays;
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
 *     entries. It is still required for the bundles whose violations are reported in
 *     {@code .properties} inputs, because {@code //} comments cannot be used there, for example
 *     {@code OrderedProperties}, {@code Translation/caseOne}, {@code UniqueProperties/caseTwo}
 *     and {@code neversuppressedchecks/UniqueProperties}.</li>
 * </ul>
 *
 * <p>The presence of {@code expected.txt} selects the mode, so bundles can be migrated to inline
 * comments one at a time by adding the comments and deleting {@code expected.txt}. A bundle with
 * neither {@code expected.txt} nor any inline comment only asserts that nothing is reported, so
 * annotations have to be kept when such a bundle is modified.</p>
 *
 * <p>Rules for inline comments, all enforced by the main library's parser:</p>
 * <ul>
 *     <li>A message is matched as a regular expression, see
 *     {@link TestInputViolation#toRegex()}. It escapes only {@code &#123;}, {@code (} and
 *     {@code )}, so a message with any other metacharacter, for example {@code ^}, {@code $} or
 *     {@code \}, has to be truncated or escaped, as in
 *     {@code // violation 'Name 'x' must match pattern'}.</li>
 *     <li>Several violations of one line are declared as {@code // 2 violations above:}
 *     followed by the messages, in the order they are reported.</li>
 *     <li>{@code // filtered violation 'message'} marks a violation that the filter is expected
 *     to suppress, and is verified to be absent from the output. Such a comment has to stay
 *     indented, as a {@code // filtered violation} comment at the very start of a line is
 *     parsed as a regular violation.</li>
 * </ul>
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
        final List<File> inputFiles = listInputFiles(path);
        final int errorCounter = rootModule.process(inputFiles);
        assertResults(path, inputFiles, errorCounter, stream);
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

    private static List<File> listInputFiles(String path) throws IOException {
        final File[] files = new File(path).listFiles(INPUT_FILE_FILTER);
        if (files == null) {
            throw new IOException("there is no java file in this directory.");
        }
        return Arrays.stream(files).sorted().toList();
    }

    private void assertResults(String path, List<File> inputFiles, int errorCounter,
                               ByteArrayOutputStream stream) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(stream.toByteArray());
             LineNumberReader lnr = new LineNumberReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            final List<String> actuals = lnr.lines().toList();
            final long violationsInBundle = actuals.stream()
                    .filter(line -> line.startsWith(path + File.separator))
                    .count();
            assertEquals(violationsInBundle, errorCounter, "unexpected output: " + actuals);
            final File expectedFile = new File(path, "expected.txt");
            if (expectedFile.exists()) {
                assertLegacyResults(expectedFile, path, errorCounter, actuals);
            }
            else {
                assertInlineResults(inputFiles, errorCounter, actuals);
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
     * bundle's input files, and against the {@code // filtered violation 'message'} comments,
     * which must not be reported. Violations are parsed with the main library's
     * {@link InlineConfigParser#getViolationsFromInputFile(String)} and checked per file, then a
     * total count guards against any reported violation that is not attributed to a file.
     *
     * @param inputFiles input files of the bundle, in the order they are processed
     * @param errorCounter number of violations reported by checkstyle
     * @param actuals actual output lines
     * @throws Exception if an input file cannot be parsed
     */
    private static void assertInlineResults(List<File> inputFiles, int errorCounter,
                                            List<String> actuals) throws Exception {
        int expectedTotal = 0;
        for (File file : inputFiles) {
            final String filePath = file.getPath();
            final List<String> actualViolations = getActualViolations(actuals, filePath);
            final List<TestInputViolation> violations =
                    InlineConfigParser.getViolationsFromInputFile(filePath);
            verifyViolations(filePath, violations, actualViolations);
            verifyFilteredViolations(filePath,
                    InlineConfigParser.getFilteredViolationsFromInputFile(filePath),
                    actualViolations);
            expectedTotal += violations.size();
        }
        assertEquals(expectedTotal, errorCounter,
                "number of violations does not match the number of inline violation comments");
    }

    /** Extracts the reported lines of one file, without the file name prefix. */
    private static List<String> getActualViolations(List<String> actuals, String filePath) {
        final String prefix = filePath + ':';
        return actuals.stream()
                .filter(line -> line.startsWith(prefix))
                .map(line -> line.substring(prefix.length()))
                .toList();
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
            final String actual = actualViolations.get(index);
            final String expectedRegex = testInputViolations.get(index).toRegex();
            assertTrue(actual.matches(expectedRegex),
                    "Actual violation '" + actual + "' of " + file
                            + " does not match inline comment '" + expectedRegex + "'");
        }
    }

    /**
     * Verifies that no violation marked with a {@code // filtered violation} comment is reported,
     * as the patch filter is expected to suppress it. The regex of a filtered violation contains
     * its line number, so a violation of another line never matches.
     *
     * @param file file path, for assertion messages
     * @param filteredViolations violations parsed from filtered violation comments
     * @param actualViolations actual violations for the file, as {@code line:column: message}
     */
    private static void verifyFilteredViolations(String file,
                                                 List<TestInputViolation> filteredViolations,
                                                 List<String> actualViolations) {
        for (TestInputViolation filtered : filteredViolations) {
            final String expectedRegex = filtered.toRegex();
            for (String actual : actualViolations) {
                assertFalse(actual.matches(expectedRegex),
                        "Violation '" + actual + "' of " + file
                                + " should have been filtered out by the patch filter");
            }
        }
    }
}

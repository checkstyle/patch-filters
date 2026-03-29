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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import com.puppycrawl.tools.checkstyle.internal.utils.BriefUtLogger;

abstract class AbstractPatchFilterEvaluationTest extends AbstractModuleTestSupport {

    private static final String CONTEXT_CONFIG_PATTERN = "(default|zero)ContextConfig.xml";

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
        assertResults(configPath, path, errorCounter, stream);
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
        final File file = new File(path);
        final File[] files = file.listFiles((dir, name) -> {
            return name.endsWith(".java") || name.endsWith(".properties");
        });
        if (files == null) {
            throw new IOException("there is no java file in this directory.");
        }

        final List<File> theFiles = Arrays.asList(files);
        Collections.sort(theFiles);
        return rootModule.process(theFiles);
    }

    private void assertResults(String configPath, String path, int errorCounter,
                               ByteArrayOutputStream stream) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(stream.toByteArray());
             LineNumberReader lnr = new LineNumberReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            final String expectedFile = configPath.replaceFirst(CONTEXT_CONFIG_PATTERN,
                    "expected.txt");
            final Path expectedFilePath = Paths.get(getPath(expectedFile));
            final List<String> expected = Files.readAllLines(expectedFilePath);
            final List<String> actuals = lnr.lines().toList();

            for (int index = 0; index < expected.size(); index++) {
                final String expectedResult = path + File.separator + expected.get(index);
                assertEquals(expectedResult, actuals.get(index),
                        "error message " + index + ". Expected file: " + expectedFilePath);
            }
            assertEquals(expected.size(), errorCounter, "unexpected output: " + lnr.readLine());
        }
    }
}

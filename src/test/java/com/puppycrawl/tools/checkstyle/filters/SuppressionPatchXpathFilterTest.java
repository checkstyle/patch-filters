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

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.PackageObjectFactory;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.RootModule;
import com.puppycrawl.tools.checkstyle.internal.utils.BriefUtLogger;

public class SuppressionPatchXpathFilterTest extends AbstractModuleTestSupport {
    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/filters/";
    }

    @Test
    public void testIllegalToken() throws Exception {
        final String inputFile = "strategy/TreeWalker/IllegalToken/Test.java";

        final String defaultContextConfigPathOne =
                "strategy/TreeWalker/IllegalToken/newline/defaultContextConfig.xml";
        final String zeroContextConfigPathOne =
                "strategy/TreeWalker/IllegalToken/newline/zeroContextConfig.xml";
        final String[] expectedOne = {
            "15:14: Using 'outer:' is not allowed.",
        };
        testByConfigTreeWalker(defaultContextConfigPathOne, inputFile, expectedOne);
        testByConfigTreeWalker(zeroContextConfigPathOne, inputFile, expectedOne);

        final String defaultContextConfigPathTwo =
                "strategy/TreeWalker/IllegalToken/patchedline/defaultContextConfig.xml";
        final String zeroContextConfigPathTwo =
                "strategy/TreeWalker/IllegalToken/patchedline/zeroContextConfig.xml";
        final String[] expectedTwo = {
            "15:14: Using 'outer:' is not allowed.",
        };
        testByConfigTreeWalker(defaultContextConfigPathTwo, inputFile, expectedTwo);
        testByConfigTreeWalker(zeroContextConfigPathTwo, inputFile, expectedTwo);
    }

    private void testByConfigTreeWalker(String configPath, String inputFile, String[] expected)
            throws Exception {
        // we can add here any variable to provide path to patch name by PropertiesExpander
        System.setProperty("tp", "src/test/resources/com/puppycrawl/tools/checkstyle/filters");
        final Configuration config = ConfigurationLoader.loadConfiguration(
                getPath(configPath),
                new PropertiesExpander(System.getProperties()));
        final ClassLoader moduleClassLoader = SuppressionPatchXpathFilter.class.getClassLoader();
        final ModuleFactory factory = new PackageObjectFactory(
                SuppressionPatchXpathFilter.class.getPackage().getName(), moduleClassLoader);

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

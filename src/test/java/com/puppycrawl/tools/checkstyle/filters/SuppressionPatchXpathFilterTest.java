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

import org.junit.Ignore;
import org.junit.Test;

public class SuppressionPatchXpathFilterTest extends AbstractPatchFilterEvaluationTest {
    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/filters/suppressionpatchxpathfilter/";
    }

    @Test
    public void testIllegalToken() throws Exception {
        testByConfig(
                "IllegalToken/newline/defaultContextConfig.xml");
        testByConfig(
                "IllegalToken/newline/zeroContextConfig.xml");

        testByConfig(
                "IllegalToken/patchedline/defaultContextConfig.xml");
        testByConfig(
                "IllegalToken/patchedline/zeroContextConfig.xml");
    }

    @Test
    @Ignore("MethodLength should have a violation when method length"
            + " is more than threshold, but not. It should be solved by"
            + "context strategy.")
    public void testMethodLength() throws Exception {
        testByConfig(
                "MethodLength/newline/defaultContextConfig.xml");
        testByConfig(
                "MethodLength/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testMethodName() throws Exception {
        testByConfig(
                "MethodName/newline/defaultContextConfig.xml");
        testByConfig(
                "MethodName/patchedline/defaultContextConfig.xml");
    }

    @Test
    @Ignore("MethodCount should have a violation when method count"
            + " is more than threshold, but not. It should be solved by"
            + "context strategy.")
    public void testMethodCount() throws Exception {
        testByConfig(
                "MethodCount/newline/defaultContextConfig.xml");
        testByConfig(
                "MethodCount/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testMagicNumber() throws Exception {
        testByConfig(
                "MagicNumber/newline/defaultContextConfig.xml");
        testByConfig(
                "MagicNumber/patchedline/defaultContextConfig.xml");
    }

    @Test
    @Ignore("ReturnCount should have a violation when return count"
            + " is more than threshold, but not. It should be solved by"
            + "context strategy.")
    public void testReturnCount() throws Exception {
        testByConfig(
                "ReturnCount/newline/defaultContextConfig.xml");
        testByConfig(
                "ReturnCount/patchedline/defaultContextConfig.xml");
    }

    @Test
    @Ignore("JavadocMethod should have a violation, but not. It should be solved by"
            + "context strategy.")
    public void testJavadocMethod() throws Exception {
        testByConfig(
                "JavadocMethod/newline/defaultContextConfig.xml");
        testByConfig(
                "JavadocMethod/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testAvoidNestedBlocks() throws Exception {
        testByConfig(
                "AvoidNestedBlocks/newline/defaultContextConfig.xml");
        testByConfig(
                "AvoidNestedBlocks/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testMemberName() throws Exception {
        testByConfig(
                "MemberName/newline/defaultContextConfig.xml");
        testByConfig(
                "MemberName/patchedline/defaultContextConfig.xml");
    }
}

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

public class SuppressionPatchFilterTest extends AbstractPatchFilterEvaluationTest {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/filters/suppressionpatchfilter/";
    }

    @Test
    public void testFileLength() throws Exception {
        testByConfig("FileLength/newline/defaultContextConfig.xml");
        testByConfig("FileLength/newline/zeroContextConfig.xml");

        testByConfig("FileLength/patchedline/defaultContextConfig.xml");
        testByConfig("FileLength/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testLineLength() throws Exception {
        testByConfig("LineLength/newline/defaultContextConfig.xml");
        testByConfig("LineLength/newline/zeroContextConfig.xml");

        testByConfig("LineLength/patchedline/defaultContextConfig.xml");
        testByConfig("LineLength/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testFileTabCharacter() throws Exception {
        testByConfig("FileTabCharacter/newline/defaultContextConfig.xml");
        testByConfig("FileTabCharacter/newline/zeroContextConfig.xml");

        testByConfig("FileTabCharacter/patchedline/defaultContextConfig.xml");
        testByConfig("FileTabCharacter/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testFileTabCharacterMoveCodeInSameFileWithoutChanges() throws Exception {
        testByConfig("FileTabCharacter/MoveCodeInSameFileWithoutChanges/"
                + "newline/defaultContextConfig.xml");
        testByConfig("FileTabCharacter/MoveCodeInSameFileWithoutChanges/"
                + "newline/zeroContextConfig.xml");

        testByConfig("FileTabCharacter/MoveCodeInSameFileWithoutChanges"
                + "/patchedline/defaultContextConfig.xml");
        testByConfig("FileTabCharacter/MoveCodeInSameFileWithoutChanges/"
                + "patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testFileTabCharacterMoveCodeInSameFileWithMinorChanges() throws Exception {
        testByConfig("FileTabCharacter/"
                + "MoveCodeInSameFileWithMinorChanges/newline/defaultContextConfig.xml");
        testByConfig("FileTabCharacter/"
                + "MoveCodeInSameFileWithMinorChanges/newline/zeroContextConfig.xml");

        testByConfig("FileTabCharacter/MoveCodeInSameFileWithMinorChanges"
                + "/patchedline/defaultContextConfig.xml");
        testByConfig("FileTabCharacter/MoveCodeInSameFileWithMinorChanges"
                + "/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testFileTabCharacterSingleChangedLine() throws Exception {
        testByConfig(
                "FileTabCharacter/SingleChangedLine/newline/defaultContextConfig.xml");
        testByConfig(
                "FileTabCharacter/SingleChangedLine/newline/zeroContextConfig.xml");

        testByConfig(
                "FileTabCharacter/SingleChangedLine/"
                        + "patchedline/defaultContextConfig.xml");
        testByConfig(
                "FileTabCharacter/SingleChangedLine/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testFileTabCharacterSingleNewLine() throws Exception {
        testByConfig("FileTabCharacter/SingleNewLine/newline/defaultContextConfig.xml");
        testByConfig("FileTabCharacter/SingleNewLine/newline/zeroContextConfig.xml");

        testByConfig("FileTabCharacter/SingleNewLine/"
                + "patchedline/defaultContextConfig.xml");
        testByConfig("FileTabCharacter/SingleNewLine/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testRegexpOnFilename() throws Exception {
        testByConfig("RegexpOnFilename/newline/defaultContextConfig.xml");
        testByConfig("RegexpOnFilename/newline/zeroContextConfig.xml");

        testByConfig("RegexpOnFilename/patchedline/defaultContextConfig.xml");
        testByConfig("RegexpOnFilename/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testNewlineAtEndOfFile() throws Exception {
        testByConfig("NewlineAtEndOfFile/newline/defaultContextConfig.xml");
        testByConfig("NewlineAtEndOfFile/newline/zeroContextConfig.xml");

        testByConfig("NewlineAtEndOfFile/patchedline/defaultContextConfig.xml");
        testByConfig("NewlineAtEndOfFile/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testOrderedProperties() throws Exception {
        testByConfig("OrderedProperties/newline/defaultContextConfig.xml");
        testByConfig("OrderedProperties/newline/zeroContextConfig.xml");

        testByConfig("OrderedProperties/patchedline/defaultContextConfig.xml");
        testByConfig("OrderedProperties/patchedline/zeroContextConfig.xml");
    }

    @Test
    @Ignore("UniquePropertiesCheck has a problem is that violation's "
            + "line information is not on newline/patchedline,"
            + " but on original duplicated property, this maybe solve on context strategy")
    public void testUniqueProperties() throws Exception {
        testByConfig("UniqueProperties/newline/defaultContextConfig.xml");
        testByConfig("UniqueProperties/newline/zeroContextConfig.xml");

        testByConfig("UniqueProperties/patchedline/defaultContextConfig.xml");
        testByConfig("UniqueProperties/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testRegexpMultiline() throws Exception {
        testByConfig("RegexpMultiline/newline/defaultContextConfig.xml");
        testByConfig("RegexpMultiline/newline/zeroContextConfig.xml");

        testByConfig("RegexpMultiline/patchedline/defaultContextConfig.xml");
        testByConfig("RegexpMultiline/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testRegexpSingleline() throws Exception {
        testByConfig("RegexpSingleline/newline/defaultContextConfig.xml");
        testByConfig("RegexpSingleline/patchedline/defaultContextConfig.xml");
    }
}

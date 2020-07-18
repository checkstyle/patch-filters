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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

public class SuppressionPatchFilterTest extends AbstractPatchFilterEvaluationTest {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/filters/";
    }

    @Test
    public void testStrategyOptionOnEclipse() throws Exception {
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
        for (int lineNo : addedLineList) {
            shouldAcceptLine(addedfilter, lineNo, fileName);
        }
        for (int lineNo : changedLineList) {
            shouldRejectLine(addedfilter, lineNo, fileName);
        }
        for (int lineNo : addedLineList) {
            shouldAcceptLine(changedfilter, lineNo, fileName);
        }
        for (int lineNo : changedLineList) {
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
        createSuppressionPatchFilter(String fileName, String strategy) throws Exception {
        final SuppressionPatchFilter suppressionPatchFilter = new SuppressionPatchFilter();
        suppressionPatchFilter.setFile(fileName);
        suppressionPatchFilter.setStrategy(strategy);
        suppressionPatchFilter.finishLocalSetup();
        return suppressionPatchFilter;
    }

    private static SuppressionPatchFilter
        createSuppressionPatchFilter(String fileName) throws Exception {
        return createSuppressionPatchFilter(fileName, "changed");
    }

    @Test
    public void testFileLength() throws Exception {
        testByConfig("strategy/FileLength/newline/defaultContextConfig.xml");
        testByConfig("strategy/FileLength/newline/zeroContextConfig.xml");

        testByConfig("strategy/FileLength/patchedline/defaultContextConfig.xml");
        testByConfig("strategy/FileLength/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testLineLength() throws Exception {
        testByConfig("strategy/LineLength/newline/defaultContextConfig.xml");
        testByConfig("strategy/LineLength/newline/zeroContextConfig.xml");

        testByConfig("strategy/LineLength/patchedline/defaultContextConfig.xml");
        testByConfig("strategy/LineLength/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testFileTabCharacter() throws Exception {
        testByConfig("strategy/FileTabCharacter/newline/defaultContextConfig.xml");
        testByConfig("strategy/FileTabCharacter/newline/zeroContextConfig.xml");

        testByConfig("strategy/FileTabCharacter/patchedline/defaultContextConfig.xml");
        testByConfig("strategy/FileTabCharacter/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testFileTabCharacterMoveCodeInSameFileWithoutChanges() throws Exception {
        testByConfig("strategy/FileTabCharacter/MoveCodeInSameFileWithoutChanges/"
                + "newline/defaultContextConfig.xml");
        testByConfig("strategy/FileTabCharacter/MoveCodeInSameFileWithoutChanges/"
                + "newline/zeroContextConfig.xml");

        testByConfig("strategy/FileTabCharacter/MoveCodeInSameFileWithoutChanges"
                + "/patchedline/defaultContextConfig.xml");
        testByConfig("strategy/FileTabCharacter/MoveCodeInSameFileWithoutChanges/"
                + "patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testFileTabCharacterMoveCodeInSameFileWithMinorChanges() throws Exception {
        testByConfig("strategy/FileTabCharacter/"
                + "MoveCodeInSameFileWithMinorChanges/newline/defaultContextConfig.xml");
        testByConfig("strategy/FileTabCharacter/"
                + "MoveCodeInSameFileWithMinorChanges/newline/zeroContextConfig.xml");

        testByConfig("strategy/FileTabCharacter/MoveCodeInSameFileWithMinorChanges"
                + "/patchedline/defaultContextConfig.xml");
        testByConfig("strategy/FileTabCharacter/MoveCodeInSameFileWithMinorChanges"
                + "/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testFileTabCharacterSingleChangedLine() throws Exception {
        testByConfig(
                "strategy/FileTabCharacter/SingleChangedLine/newline/defaultContextConfig.xml");
        testByConfig(
                "strategy/FileTabCharacter/SingleChangedLine/newline/zeroContextConfig.xml");

        testByConfig(
                "strategy/FileTabCharacter/SingleChangedLine/"
                        + "patchedline/defaultContextConfig.xml");
        testByConfig(
                "strategy/FileTabCharacter/SingleChangedLine/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testFileTabCharacterSingleNewLine() throws Exception {
        testByConfig("strategy/FileTabCharacter/SingleNewLine/newline/defaultContextConfig.xml");
        testByConfig("strategy/FileTabCharacter/SingleNewLine/newline/zeroContextConfig.xml");

        testByConfig("strategy/FileTabCharacter/SingleNewLine/"
                + "patchedline/defaultContextConfig.xml");
        testByConfig("strategy/FileTabCharacter/SingleNewLine/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testRegexpOnFilename() throws Exception {
        testByConfig("strategy/RegexpOnFilename/newline/defaultContextConfig.xml");
        testByConfig("strategy/RegexpOnFilename/newline/zeroContextConfig.xml");

        testByConfig("strategy/RegexpOnFilename/patchedline/defaultContextConfig.xml");
        testByConfig("strategy/RegexpOnFilename/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testNewlineAtEndOfFile() throws Exception {
        testByConfig("strategy/NewlineAtEndOfFile/newline/defaultContextConfig.xml");
        testByConfig("strategy/NewlineAtEndOfFile/newline/zeroContextConfig.xml");

        testByConfig("strategy/NewlineAtEndOfFile/patchedline/defaultContextConfig.xml");
        testByConfig("strategy/NewlineAtEndOfFile/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testOrderedProperties() throws Exception {
        testByConfig("strategy/OrderedProperties/newline/defaultContextConfig.xml");
        testByConfig("strategy/OrderedProperties/newline/zeroContextConfig.xml");

        testByConfig("strategy/OrderedProperties/patchedline/defaultContextConfig.xml");
        testByConfig("strategy/OrderedProperties/patchedline/zeroContextConfig.xml");
    }

    @Test
    @Ignore("UniquePropertiesCheck has a problem is that violation's "
            + "line information is not on newline/patchedline,"
            + " but on original duplicated property, this maybe solve on context strategy")
    public void testUniqueProperties() throws Exception {
        testByConfig("strategy/UniqueProperties/newline/defaultContextConfig.xml");
        testByConfig("strategy/UniqueProperties/newline/zeroContextConfig.xml");

        testByConfig("strategy/UniqueProperties/patchedline/defaultContextConfig.xml");
        testByConfig("strategy/UniqueProperties/patchedline/zeroContextConfig.xml");
    }

    @Test
    @Ignore("RegexpMultilineCheck has a problem is that violation's "
            + "line information is not on newline/patchedline,"
            + "this maybe solve on context strategy")
    public void testRegexpMultiline() throws Exception {
        testByConfig("strategy/RegexpMultiline/newline/defaultContextConfig.xml");
        testByConfig("strategy/RegexpMultiline/newline/zeroContextConfig.xml");

        testByConfig("strategy/RegexpMultiline/patchedline/defaultContextConfig.xml");
        testByConfig("strategy/RegexpMultiline/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testRegexpSingleline() throws Exception {
        testByConfig("strategy/RegexpSingleline/newline/defaultContextConfig.xml");
        testByConfig("strategy/RegexpSingleline/patchedline/defaultContextConfig.xml");
    }
}

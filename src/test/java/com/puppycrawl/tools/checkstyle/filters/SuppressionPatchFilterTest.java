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

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;

public class SuppressionPatchFilterTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/filters/";
    }

    @Test
    public void testAccept() throws Exception {
        final String fileName = getPath("MethodCount/MethodCountPatch.txt");
        final SuppressionPatchFilter filter = createSuppressionPatchFilter(fileName);
        final LocalizedMessage message = new LocalizedMessage(7, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, "MethodCountUpdate.java", message);

        assertFalse(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    @Test
    public void testMultiChangesOnOneFileOne() throws Exception {
        final String fileName = getPath("MultiChangesOnOneFilePatch.txt");
        final SuppressionPatchFilter filter = createSuppressionPatchFilter(fileName);
        final LocalizedMessage message = new LocalizedMessage(3, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, "Update.java", message);
        assertTrue(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    @Test
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
    public void testBoundaryOne() throws Exception {
        final String fileName = getPath("BoundaryTestPatchOne.txt");
        final SuppressionPatchFilter filter = createSuppressionPatchFilter(fileName);
        final LocalizedMessage message = new LocalizedMessage(5, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, "Update1.java", message);
        assertFalse(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    @Test
    public void testAddOptionOne() throws Exception {
        final String fileName = getPath("MultiChangesOnOneFilePatch.txt");
        final SuppressionPatchFilter filter = createSuppressionPatchFilter(fileName, "added");
        final LocalizedMessage message = new LocalizedMessage(3, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, "Update.java", message);
        assertFalse(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    @Test
    public void testAddOptionTwo() throws Exception {
        final String patchFileName = getPath("eclipse-cs-patch-1c057d1-9d473b4.txt");
        final SuppressionPatchFilter addedfilter =
                createSuppressionPatchFilter(patchFileName, "added");
        final SuppressionPatchFilter changedfilter =
                createSuppressionPatchFilter(patchFileName, "changed");
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

    @Test
    public void testAddOptionThree() throws Exception {
        final String patchFileName = getPath("eclipse-cs-patch-1c057d1-9d473b4.txt");
        final SuppressionPatchFilter addedfilter =
                createSuppressionPatchFilter(patchFileName, "added");
        final SuppressionPatchFilter changedfilter =
                createSuppressionPatchFilter(patchFileName, "changed");
        final List<Integer> addedLineList = Arrays.asList(5, 6, 7);
        final List<Integer> changedLineList = Arrays.asList(29, 34, 35, 77);
        final String fileName = "net.sf.eclipsecs.ui/test/net/sf"
                + "/eclipsecs/ui/quickfixes/AbstractQuickfixTestCase.java";
        testAddedLine(addedfilter, changedfilter, fileName, addedLineList, changedLineList);
    }

    @Test
    public void testAddOptionFour() throws Exception {
        final String patchFileName = getPath("eclipse-cs-patch-1c057d1-9d473b4.txt");
        final SuppressionPatchFilter addedfilter =
                createSuppressionPatchFilter(patchFileName, "added");
        final SuppressionPatchFilter changedfilter =
                createSuppressionPatchFilter(patchFileName, "changed");
        final List<Integer> addedLineList = Arrays.asList(3, 4);
        final List<Integer> changedLineList = Arrays.asList(9, 10, 11, 12, 14, 15, 16,
                17, 19, 20, 21, 22, 24, 25, 26, 27, 29, 30, 31, 32, 34, 35, 36, 37, 39,
                40, 41, 42, 44, 45, 46, 47, 49, 50, 51, 52);
        final String fileName = "net.sf.eclipsecs.ui/test/net/sf"
                        + "/eclipsecs/ui/quickfixes/coding/RequireThisTest.java";
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
}

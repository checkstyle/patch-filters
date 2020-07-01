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

        assertTrue(filter.accept(ev),
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
        assertTrue(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    @Test
    public void testAddOptionOne() throws Exception {
        final String fileName = getPath("MultiChangesOnOneFilePatch.txt");
        final SuppressionPatchFilter filter = createSuppressionPatchFilter(fileName, true);
        final LocalizedMessage message = new LocalizedMessage(3, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, "Update.java", message);
        assertFalse(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");

    }

    @Test
    public void testAddOptionTwo() throws Exception {
        final String fileName = getPath("eclipse-cs-patch-1c057d1-9d473b4.txt");
        final SuppressionPatchFilter filter = createSuppressionPatchFilter(fileName, true);
        final SuppressionPatchFilter filter2 = createSuppressionPatchFilter(fileName, false);
        final LocalizedMessage message = new LocalizedMessage(27, 1, null, "msg", null,
                SeverityLevel.ERROR, null, getClass(), null);
        final AuditEvent ev = new AuditEvent(this, "net.sf.eclipsecs.checkstyle/test/net/sf/eclipsecs/checkstyle/ChecksTest.java", message);
        assertFalse(filter.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
        assertTrue(filter2.accept(ev),
                "Audit event should be rejected when there are no matching patch filters");
    }

    private static SuppressionPatchFilter
        createSuppressionPatchFilter(String fileName, boolean add) throws Exception {
        final SuppressionPatchFilter suppressionPatchFilter = new SuppressionPatchFilter();
        suppressionPatchFilter.setFile(fileName);
        suppressionPatchFilter.setAdd(add);
        suppressionPatchFilter.finishLocalSetup();
        return suppressionPatchFilter;
    }

    private static SuppressionPatchFilter
        createSuppressionPatchFilter(String fileName) throws Exception {
        return createSuppressionPatchFilter(fileName, false);
    }
}

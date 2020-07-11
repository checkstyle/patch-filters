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

import java.util.List;

import com.puppycrawl.tools.checkstyle.TreeWalkerAuditEvent;
import com.puppycrawl.tools.checkstyle.TreeWalkerFilter;

/**
 * This filter element is immutable and processes.
 */
public class PatchXpathFilterElement implements TreeWalkerFilter {

    /** The String of file names. */
    private final String fileName;

    /** The list of line range. */
    private final List<List<Integer>> lineRangeList;

    /**
     * Constructs a {@code SuppressPatchFilterElement} for a
     * file name pattern.
     *
     * @param fileName names of filtered files.
     * @param lineRangeList   list of line range for line number filtering.
     */
    public PatchXpathFilterElement(String fileName, List<List<Integer>> lineRangeList) {
        this.fileName = fileName;
        this.lineRangeList = lineRangeList;
    }

    @Override
    public boolean accept(TreeWalkerAuditEvent event) {
        return isFileNameMatching(event) && isXpathQueryMatching(event);
    }

    /**
     * Is matching by file name.
     *
     * @param event event
     * @return true if it is matching
     */
    private boolean isFileNameMatching(TreeWalkerAuditEvent event) {
        return event.getFileName() != null
                && ((event.getFileName()).equals(fileName)
                || event.getFileName().contains(fileName));
    }

    /**
     * Is matching by xpath query.
     *
     * @param event event
     * @return true if it is matching or not set.
     */
    private boolean isXpathQueryMatching(TreeWalkerAuditEvent event) {
        return true;
    }
}

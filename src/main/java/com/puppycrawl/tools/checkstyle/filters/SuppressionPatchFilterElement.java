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
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.Filter;
import com.puppycrawl.tools.checkstyle.utils.ModuleReflectionUtil;

/**
 * This filter element is immutable and processes.
 */
public class SuppressionPatchFilterElement implements Filter {

    /**
     * The String of file names.
     */
    private final String fileName;

    /**
     * The list of line range.
     */
    private final List<List<Integer>> lineRangeList;

    /**
     * Set has user defined Checks to never suppress if files are touched.
     */
    private final Set<String> neverSuppressedChecks;

    /**
     * Constructs a {@code SuppressPatchFilterElement} for a
     * file name pattern.
     *
     * @param fileName      names of filtered files
     * @param lineRangeList list of line range for line number filtering
     * @param neverSuppressedChecks set has user defined Checks to never suppress
     *                              if files are touched
     */
    public SuppressionPatchFilterElement(String fileName, List<List<Integer>> lineRangeList,
                                         Set<String> neverSuppressedChecks) {
        this.fileName = fileName;
        this.lineRangeList = lineRangeList;
        this.neverSuppressedChecks = neverSuppressedChecks;
    }

    @Override
    public boolean accept(AuditEvent event) {
        return isFileNameMatching(event)
                && (isTreeWalkerChecksMatching(event)
                || isNeverSuppressCheck(event)
                || isContextStrategyCheck(event)
                || isLineMatching(event));
    }

    /**
     * Is matching by file name.
     *
     * @param event event
     * @return true if it is matching
     */
    private boolean isFileNameMatching(AuditEvent event) {
        return event.getFileName() != null
                && ((event.getFileName()).equals(fileName)
                || event.getFileName().contains(fileName));
    }

    /**
     * Is matching by treeWalker checks.
     *
     * @param event event
     * @return true if it is matching
     */
    private boolean isTreeWalkerChecksMatching(AuditEvent event) {
        boolean result = false;
        final String sourceName = event.getLocalizedMessage().getSourceName();
        try {
            final Class clazz = Class.forName(sourceName);
            if (ModuleReflectionUtil.isCheckstyleTreeWalkerCheck(clazz)) {
                result = true;
            }
            return result;
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Class " + sourceName + " not found", ex);
        }
    }

    /**
     * Whether line match.
     *
     * @param event event to process.
     * @return true if line and column are matching or not set.
     */
    private boolean isLineMatching(AuditEvent event) {
        boolean result = false;
        if (event.getLocalizedMessage() != null) {
            for (List<Integer> aLineRangeList : lineRangeList) {
                final int startLine = aLineRangeList.get(0) + 1;
                final int endLine = aLineRangeList.get(1) + 1;
                final int currentLine = event.getLine();
                result = currentLine >= startLine && currentLine < endLine;
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Is matching by context strategy check.
     *
     * @param event event
     * @return true if it is matching
     */
    private boolean isContextStrategyCheck(AuditEvent event) {
        boolean result = false;
        final String checkShortName = getCheckShortName(event);
        if ("RegexpOnFilenameCheck".equals(checkShortName)
                || "RegexpHeaderCheck".equals(checkShortName)
                || "FileLengthCheck".equals(checkShortName)
                || "NewlineAtEndOfFileCheck".equals(checkShortName)
                || "HeaderCheck".equals(checkShortName)) {
            result = true;
        }
        return result;
    }

    /**
     * Is matching by never suppress check.
     *
     * @param event event
     * @return true if it is matching
     */
    private boolean isNeverSuppressCheck(AuditEvent event) {
        boolean result = false;
        if (neverSuppressedChecks != null) {
            if (containsShortName(neverSuppressedChecks, event)
                    || neverSuppressedChecks.contains(event.getModuleId())) {
                result = true;
            }
        }
        return result;
    }

    private boolean containsShortName(Set<String> checkNameSet,
                                      AuditEvent event) {
        final String checkShortName = getCheckShortName(event);
        final String shortName = checkShortName.replaceAll("Check", "");
        return checkNameSet.contains(checkShortName)
                || checkNameSet.contains(shortName);

    }

    private String getCheckShortName(AuditEvent event) {
        final String[] checkNames = event.getLocalizedMessage().getSourceName().split("\\.");
        return checkNames[checkNames.length - 1];
    }
}

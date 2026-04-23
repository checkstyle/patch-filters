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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.Filter;
import com.puppycrawl.tools.checkstyle.utils.ModuleReflectionUtil;

/**
 * This filter element is immutable and processes.
 */
public final class SuppressionPatchFilterElement implements Filter {

    /** Check name. */
    private static final String UNIQUE_PROPERTIES_CHECK = "UniqueProperties";

    /** Check name. */
    private static final String REGEXP_MULTILINE_CHECK = "RegexpMultiline";

    /** Suffix of check class names. */
    private static final String CHECK_SUFFIX = "Check";

    /** Escaped new line sequence in violation messages. */
    private static final String ESCAPED_NEW_LINE = "\\n";

    /** Message pattern to extract duplicated key from UniqueProperties violation. */
    private static final Pattern UNIQUE_PROPERTIES_KEY_PATTERN =
            Pattern.compile("Duplicated property '([^']+)'");

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

    /** Strategy used to build changed line ranges. */
    private final Strategy strategy;

    /**
     * Constructs a {@code SuppressPatchFilterElement} for a
     * file name pattern.
     *
     * @param fileName      names of filtered files
     * @param lineRangeList list of line range for line number filtering
     * @param neverSuppressedChecks set has user defined Checks to never suppress
     *                              if files are touched
     * @param strategy strategy used to build changed line ranges
     */
    public SuppressionPatchFilterElement(String fileName, List<List<Integer>> lineRangeList,
                                         Set<String> neverSuppressedChecks, Strategy strategy) {
        this.fileName = fileName;
        this.lineRangeList = lineRangeList;
        this.neverSuppressedChecks = neverSuppressedChecks;
        this.strategy = strategy;
    }

    @Override
    public boolean accept(AuditEvent event) {
        return isFileNameMatching(event)
                && (isTreeWalkerChecksMatching(event)
                || isMatchingByNeverSuppressedCheck(event)
                || isLineMatching(event));
    }

    /**
     * Is matching by file name.
     *
     * @param event event
     * @return true if it is matching
     */
    private boolean isFileNameMatching(AuditEvent event) {
        String eventFileName = event.getFileName();
        boolean result = eventFileName != null;

        if (result) {
            // git always displays paths with '/', even on windows
            if (File.separatorChar != '/') {
                eventFileName = eventFileName.replace(File.separatorChar, '/');
            }

            result = eventFileName.endsWith(fileName);
        }

        return result;
    }

    /**
     * Is matching by treeWalker checks.
     *
     * @param event event
     * @return true if it is matching
     * @throws IllegalStateException if source class can not be found.
     */
    private static boolean isTreeWalkerChecksMatching(AuditEvent event) {
        boolean result = false;
        final String sourceName = event.getViolation().getSourceName();
        try {
            final Class clazz = Class.forName(sourceName);
            if (ModuleReflectionUtil.isCheckstyleTreeWalkerCheck(clazz)) {
                result = true;
            }
            return result;
        }
        catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Class " + sourceName + " not found", exception);
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
        if (event.getViolation() != null) {
            result = lineMatching(event.getLine());
        }
        return result;
    }

    /**
     * Check if line intersects any changed line range.
     *
     * @param currentLine line number (1-based)
     * @return true if line belongs to a changed range
     */
    private boolean lineMatching(int currentLine) {
        boolean result = false;
        for (List<Integer> aLineRangeList : lineRangeList) {
            final int startLine = aLineRangeList.get(0) + 1;
            final int endLine = aLineRangeList.get(1) + 1;
            if (startLine == endLine) {
                result = currentLine == startLine;
            }
            else {
                result = currentLine >= startLine && currentLine < endLine;
            }
            if (result) {
                break;
            }
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

    /**
     * Apply stricter causality matching for checks configured under neverSuppressedChecks.
     *
     * @param event event to process
     * @return true when violation can be attributed to changed lines in touched file
     */
    private boolean isMatchingByNeverSuppressedCheck(AuditEvent event) {
        boolean result = false;
        if (isNeverSuppressCheck(event)) {
            result = isLineMatching(event);
            if (!result) {
                final String checkShortName = getCheckShortName(event)
                        .replaceAll(CHECK_SUFFIX, "");
                if (UNIQUE_PROPERTIES_CHECK.equals(checkShortName)
                        && strategy != Strategy.NEWLINE) {
                    result = isUniquePropertiesViolationCausedByPatch(event);
                }
                else if (REGEXP_MULTILINE_CHECK.equals(checkShortName)
                        && strategy != Strategy.NEWLINE) {
                    result = isRegexpMultilineViolationCausedByPatch(event);
                }
                else {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * UniqueProperties reports first duplicate occurrence line; we attribute it to patch
     * when any duplicate key occurrence is touched.
     *
     * @param event event to process
     * @return true if changed lines contain any occurrence of duplicated key
     */
    private boolean isUniquePropertiesViolationCausedByPatch(AuditEvent event) {
        boolean result = false;
        final String message = event.getMessage();
        if (message != null) {
            final Matcher matcher = UNIQUE_PROPERTIES_KEY_PATTERN.matcher(message);
            if (matcher.find()) {
                final String key = matcher.group(1);
                try {
                    final List<String> lines = Files.readAllLines(
                            Paths.get(event.getFileName()), StandardCharsets.UTF_8);
                    for (int index = 0; index < lines.size(); index++) {
                        final String line = lines.get(index).trim();
                        if (line.startsWith(key + "=") || line.startsWith(key + " =")) {
                            if (lineMatching(index + 1)) {
                                result = true;
                                break;
                            }
                        }
                    }
                }
                catch (IOException ioException) {
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * RegexpMultiline can report the first line of a multi-line match.
     * We expand matching window by the number of line breaks in the pattern.
     *
     * @param event event to process
     * @return true if any line in match window intersects patch lines
     */
    private boolean isRegexpMultilineViolationCausedByPatch(AuditEvent event) {
        final String message = event.getMessage();
        int linesInMatch = 1;
        if (message != null) {
            int index = message.indexOf(ESCAPED_NEW_LINE);
            while (index != -1) {
                linesInMatch++;
                index = message.indexOf(ESCAPED_NEW_LINE, index + ESCAPED_NEW_LINE.length());
            }
        }

        boolean result = false;
        final int startLine = event.getLine();
        for (int lineOffset = 0; lineOffset < linesInMatch; lineOffset++) {
            if (lineMatching(startLine + lineOffset)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static boolean containsShortName(Set<String> checkNameSet,
                                      AuditEvent event) {
        final String checkShortName = getCheckShortName(event);
        final String shortName = checkShortName.replaceAll(CHECK_SUFFIX, "");
        return checkNameSet.contains(checkShortName)
                || checkNameSet.contains(shortName);

    }

    private static String getCheckShortName(AuditEvent event) {
        final String[] checkNames = event.getViolation().getSourceName().split("\\.");
        return checkNames[checkNames.length - 1];
    }
}

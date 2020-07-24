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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.TreeWalkerAuditEvent;
import com.puppycrawl.tools.checkstyle.TreeWalkerFilter;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

/**
 * This filter element is immutable and processes.
 */
public class PatchXpathFilterElement implements TreeWalkerFilter {
    /**
     * List of checks that support context strategy.
     */
    private static final List<String> SUPPORT_CONTEXT_STRATEGY_CHECKS =
            Arrays.asList("ReturnCountCheck",
                    "MethodCountCheck",
                    "AnonInnerLengthCheck",
                    "ExecutableStatementCountCheck",
                    "MethodLengthCheck",
                    "EmptyBlockCheck");

    /**
     * List of checks that always matching context strategy.
     */
    private static final List<String> ALWAYS_MATCHING_CONTEXT_STRATEGY_CHECKS =
            Arrays.asList("OuterTypeNumberCheck");

    /** The String of file names. */
    private final String fileName;

    /** The list of line range. */
    private final List<List<Integer>> lineRangeList;

    /**
     * Strategy that used.
     */
    private final String strategy;

    /**
     * Constructs a {@code SuppressPatchFilterElement} for a
     * file name pattern.
     *
     * @param fileName names of filtered files.
     * @param lineRangeList   list of line range for line number filtering.
     * @param strategy      strategy that used.
     */
    public PatchXpathFilterElement(String fileName,
                                   List<List<Integer>> lineRangeList,
                                   String strategy) {
        this.fileName = fileName;
        this.lineRangeList = lineRangeList;
        this.strategy = strategy;
    }

    @Override
    public boolean accept(TreeWalkerAuditEvent event) {
        final boolean result;

        if (strategy.equals(LoadPatchFileUtils.CONTEXT)) {
            result = isFileNameMatching(event)
                    && (isMatchingByContextStrategy(event)
                    || isLineMatching(event));
        }
        else {
            result = isFileNameMatching(event)
                    && isLineMatching(event);
        }

        return result;
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
     * Whether line match.
     *
     * @param event event to process.
     * @return true if line and column are matching or not set.
     */
    private boolean isLineMatching(TreeWalkerAuditEvent event) {
        boolean result = false;
        if (event.getLocalizedMessage() != null) {
            result = lineMatching(event.getLine());
        }
        return result;
    }

    private boolean lineMatching(int currentLine) {
        boolean result = false;
        for (List<Integer> singleLineRangeList : lineRangeList) {
            final int startLine = singleLineRangeList.get(0) + 1;
            final int endLine = singleLineRangeList.get(1) + 1;
            result = currentLine >= startLine && currentLine < endLine;
            if (result) {
                break;
            }
        }
        return result;
    }

    /**
     * Is matching by context strategy.
     *
     * @param event event
     * @return true if it is matching or not set.
     */
    private boolean isMatchingByContextStrategy(TreeWalkerAuditEvent event) {
        boolean result = false;
        final String checkShortName = getCheckShortName(event);
        if (ALWAYS_MATCHING_CONTEXT_STRATEGY_CHECKS.contains(checkShortName)) {
            result = true;
        }
        else if (SUPPORT_CONTEXT_STRATEGY_CHECKS.contains(checkShortName)) {
            final DetailAST eventAst = getEventAst(event);
            final Set<Integer> childAstLineNoList = getChildAstLineNo(eventAst);
            for (Integer currentLine : childAstLineNoList) {
                result = lineMatching(currentLine);
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    private String getCheckShortName(TreeWalkerAuditEvent event) {
        final String[] checkNames = event.getLocalizedMessage().getSourceName().split("\\.");
        return checkNames[checkNames.length - 1];
    }

    private DetailAST getEventAst(TreeWalkerAuditEvent event) {
        DetailAST curNode = event.getRootAst();
        DetailAST eventAst = null;
        while (curNode != null) {
            if (isMatchingAst(curNode, event)) {
                eventAst = curNode;
                break;
            }
            DetailAST toVisit = curNode.getFirstChild();
            while (curNode != null && toVisit == null) {
                toVisit = curNode.getNextSibling();
                curNode = curNode.getParent();
            }
            curNode = toVisit;
        }
        return eventAst;
    }

    private Set<Integer> getChildAstLineNo(DetailAST ast) {
        final Set<Integer> childAstLineNoSet = new HashSet<>();
        DetailAST curNode = ast;
        while (curNode != null) {
            DetailAST toVisit = curNode.getFirstChild();
            if (toVisit != null) {
                childAstLineNoSet.add(toVisit.getLineNo());
            }
            while (curNode != null && toVisit == null) {
                toVisit = curNode.getNextSibling();
                curNode = curNode.getParent();
            }
            curNode = toVisit;
            if (curNode != null && curNode.equals(ast.getNextSibling())) {
                break;
            }
        }
        return childAstLineNoSet;
    }

    private boolean isMatchingAst(DetailAST root, TreeWalkerAuditEvent event) {
        return root.getType() == event.getTokenType()
                && root.getLineNo() == event.getLine()
                && root.getColumnNo() == event.getColumnCharIndex();
    }
}

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.TreeWalkerAuditEvent;
import com.puppycrawl.tools.checkstyle.TreeWalkerFilter;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * This filter element is immutable and processes.
 */
public class JavaPatchFilterElement implements TreeWalkerFilter {
    /**
     * The key of minimum line in child ast lines.
     */
    private static final String MIN = "min";

    /**
     * The key of maximum line in child ast lines.
     */
    private static final String MAX = "max";

    /**
     * Mapping between a check and its ancestor token types.
     */
    private static final Map<String, List<Integer>> CHECK_TO_ANCESTOR_NODES_MAP = new HashMap<>();

    static {
        CHECK_TO_ANCESTOR_NODES_MAP.put("ArrayTrailingComma",
                Arrays.asList(TokenTypes.ARRAY_INIT));
        CHECK_TO_ANCESTOR_NODES_MAP.put("AvoidNestedBlocks",
                Arrays.asList(TokenTypes.SLIST));
        CHECK_TO_ANCESTOR_NODES_MAP.put("DefaultComesLast",
                Arrays.asList(TokenTypes.LITERAL_SWITCH));
        CHECK_TO_ANCESTOR_NODES_MAP.put("DeclarationOrder",
                Arrays.asList(TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF,
                        TokenTypes.ENUM_DEF));
        CHECK_TO_ANCESTOR_NODES_MAP.put("FinalLocalVariable",
                Arrays.asList(TokenTypes.METHOD_DEF,
                TokenTypes.VARIABLE_DEF, TokenTypes.CTOR_DEF));
        CHECK_TO_ANCESTOR_NODES_MAP.put("RightCurly",
                Arrays.asList(TokenTypes.LITERAL_TRY, TokenTypes.LITERAL_IF));
    }

    /**
     * Set of checks that support context strategy but need modify violation nodes
     * to their parent abstract nodes to get their child nodes.
     */
    private final Set<String> checkNamesForContextStrategyByTokenOrParentSet = new HashSet<>();

    /**
     * Set of checks that support context strategy but need modify violation nodes
     * to their ancestor abstract nodes to get their child nodes.
     */
    private final Set<String> checkNamesForContextStrategyByTokenOrAncestorSet = new HashSet<>();

    /**
     * Set has user defined Checks that support context strategy.
     */
    private final Set<String> supportContextStrategyChecks = new HashSet<>();

    /** The String of file names. */
    private final String fileName;

    /** The list of line range. */
    private final List<List<Integer>> lineRangeList;

    /**
     * Set has user defined Checks to never suppress if files are touched.
     */
    private final Set<String> neverSuppressedChecks;

    /**
     * Strategy that used.
     */
    private final Strategy strategy;

    /**
     * Constructs a {@code SuppressPatchFilterElement} for a
     * file name pattern.
     *
     * @param fileName                                         names of filtered files
     * @param lineRangeList                                    list of line range for line number
     *                                                         filtering
     * @param strategy                                         strategy that used
     * @param checkNamesForContextStrategyByTokenOrParentSet   user defined Checks that need modify
     *                                                         violation nodes to their parent
     *                                                         abstract nodes to get their child
     *                                                         nodes
     * @param checkNamesForContextStrategyByTokenOrAncestorSet user defined Checks that need modify
     *                                                         violation nodes to their ancestor
     *                                                         abstract nodes to get their child
     *                                                         nodes
     * @param supportContextStrategyChecks                     user defined Checks that support
     *                                                         context strategy
     * @param neverSuppressedChecks                            set has user defined Checks to never
     *                                                         suppress if files are touched
     */
    public JavaPatchFilterElement(String fileName,
                                   List<List<Integer>> lineRangeList,
                                   Strategy strategy,
                                   Set<String> checkNamesForContextStrategyByTokenOrParentSet,
                                   Set<String> checkNamesForContextStrategyByTokenOrAncestorSet,
                                   Set<String> supportContextStrategyChecks,
                                   Set<String> neverSuppressedChecks) {
        this.fileName = fileName;
        this.lineRangeList = lineRangeList;
        this.strategy = strategy;
        if (checkNamesForContextStrategyByTokenOrParentSet != null) {
            this.checkNamesForContextStrategyByTokenOrParentSet.addAll(
                    checkNamesForContextStrategyByTokenOrParentSet);
        }
        if (checkNamesForContextStrategyByTokenOrAncestorSet != null) {
            this.checkNamesForContextStrategyByTokenOrAncestorSet.addAll(
                    checkNamesForContextStrategyByTokenOrAncestorSet);
        }
        if (supportContextStrategyChecks != null) {
            this.supportContextStrategyChecks.addAll(supportContextStrategyChecks);
        }
        this.neverSuppressedChecks = neverSuppressedChecks;
    }

    @Override
    public boolean accept(TreeWalkerAuditEvent event) {
        final boolean result;

        if (Strategy.CONTEXT == strategy) {
            result = isFileNameMatching(event)
                    && (isNeverSuppressCheck(event)
                    || isMatchingByContextStrategy(event)
                    || isLineMatching(event));
        }
        else {
            result = isFileNameMatching(event)
                    && (isNeverSuppressCheck(event)
                    || isLineMatching(event));
        }

        return result;
    }

    /**
     * Is matching by file name.
     *
     * @param event {@code TreeWalkerAuditEvent} object
     * @return true if it is matching
     */
    private boolean isFileNameMatching(TreeWalkerAuditEvent event) {
        return event.getFileName() != null
                && ((event.getFileName()).equals(fileName)
                || event.getFileName().contains(fileName));
    }

    /**
     * Is matching by never suppress check.
     *
     * @param event {@code TreeWalkerAuditEvent} object
     * @return true if it is matching
     */
    private boolean isNeverSuppressCheck(TreeWalkerAuditEvent event) {
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
     * Is matching by line number.
     *
     * @param event {@code TreeWalkerAuditEvent} object
     * @return true if line are matching.
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
     * Check whether at least one line from lineRangeList is between
     * event ast node's child nodes' min and max line number.
     *
     * @param childAstStartLine event ast node's child nodes' min line number
     * @param childAstEndLine   event ast node's child nodes' max line number
     * @return true if one line is between childAstStartLine and childAstEndLine line number.
     */
    private boolean lineMatching(int childAstStartLine, int childAstEndLine) {
        boolean result = false;
        for (List<Integer> singleLineRangeList : lineRangeList) {
            final int startLine = singleLineRangeList.get(0) + 1;
            int endLine = singleLineRangeList.get(1) + 1;
            if (startLine == endLine) {
                endLine++;
            }

            result = (childAstStartLine <= startLine && startLine <= childAstEndLine)
                    || (childAstStartLine <= endLine - 1 && endLine - 1 <= childAstEndLine);

            if (result) {
                break;
            }
        }
        return result;
    }

    /**
     * Is matching by context strategy.
     *
     * @param event {@code TreeWalkerAuditEvent} object
     * @return true if it is matching or not set.
     */
    private boolean isMatchingByContextStrategy(TreeWalkerAuditEvent event) {
        boolean result = false;
        if (containsShortName(supportContextStrategyChecks, event)
                || containsShortName(checkNamesForContextStrategyByTokenOrParentSet, event)
                || containsShortName(checkNamesForContextStrategyByTokenOrAncestorSet, event)) {
            final DetailAST eventAst = getAncestorAst(event);

            if (eventAst != null) {
                final Map<String, Integer> childAstLineNoMap = getChildAstLineNo(eventAst);
                final int childAstStartLine = childAstLineNoMap.get(MIN);
                final int childAstEndLine = childAstLineNoMap.get(MAX);
                result = lineMatching(childAstStartLine, childAstEndLine);
            }
        }
        return result;
    }

    private DetailAST getAncestorAst(TreeWalkerAuditEvent event) {
        DetailAST eventAst = getEventAst(event);
        if (containsShortName(checkNamesForContextStrategyByTokenOrAncestorSet, event)) {
            if (eventAst != null) {
                eventAst = eventAst.getParent();
                final List<Integer> checkAncestorNodesList =
                        CHECK_TO_ANCESTOR_NODES_MAP.get(getCheckShortName(event));
                while (eventAst != null && checkAncestorNodesList != null
                        && !checkAncestorNodesList.contains(eventAst.getType())) {
                    eventAst = eventAst.getParent();
                }
            }
        }
        else if (containsShortName(checkNamesForContextStrategyByTokenOrParentSet, event)) {
            if (eventAst != null) {
                eventAst = eventAst.getParent();
            }
        }
        return eventAst;
    }

    /**
     * Check whether checkNameSet contains event's check.
     *
     * @param checkNameSet Set of check names
     * @param event {@code TreeWalkerAuditEvent} object
     * @return true if set contains event's check.
     */
    private boolean containsShortName(Set<String> checkNameSet,
                                      TreeWalkerAuditEvent event) {
        final String checkName = getCheckName(event);
        final String checkShortName = getCheckShortName(event);
        return checkNameSet.contains(checkName)
                || checkNameSet.contains(checkShortName);

    }

    private String getCheckName(TreeWalkerAuditEvent event) {
        final String[] checkNames = event.getLocalizedMessage().getSourceName().split("\\.");
        return checkNames[checkNames.length - 1];
    }

    private String getCheckShortName(TreeWalkerAuditEvent event) {
        return getCheckName(event).replaceAll("Check", "");
    }

    /**
     * Return event's corresponding ast node using iterative algorithm.
     *
     * @param event {@code TreeWalkerAuditEvent} object
     * @return DetailAST event's corresponding ast node
     */
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

    /**
     * Find min and max line numbers from AST node and its children.
     *
     * @param ast DetailAST event's corresponding ast node
     * @return Map contains ast node's all child nodes' max and min line number
     */
    private static Map<String, Integer> getChildAstLineNo(DetailAST ast) {
        final Map<String, Integer> childAstLineNoMap = new HashMap<>();
        DetailAST curNode = ast;
        childAstLineNoMap.put(MIN, curNode.getLineNo());
        childAstLineNoMap.put(MAX, curNode.getLineNo());
        while (curNode != null && curNode != ast.getNextSibling()) {
            DetailAST toVisit = curNode.getFirstChild();
            setChildAstLineNo(childAstLineNoMap, curNode);
            setChildAstLineNo(childAstLineNoMap, toVisit);
            while (curNode != null && toVisit == null && curNode != ast.getParent()) {
                toVisit = curNode.getNextSibling();
                curNode = curNode.getParent();
            }
            if (curNode == ast.getParent()) {
                break;
            }
            curNode = toVisit;
        }
        return childAstLineNoMap;
    }

    /**
     * Update childAstLineNoMap if line number of ast is smaller
     * or larger than current min and max value.
     *
     * @param childAstLineNoMap Map contains ast node's all child nodes' max and min line number
     * @param ast DetailAST event's ast node's child node
     */
    private static void setChildAstLineNo(Map<String, Integer> childAstLineNoMap, DetailAST ast) {
        if (ast != null) {
            final int lineNo = ast.getLineNo();
            if (lineNo < childAstLineNoMap.get(MIN)) {
                childAstLineNoMap.put(MIN, lineNo);
            }
            else if (lineNo > childAstLineNoMap.get(MAX)) {
                childAstLineNoMap.put(MAX, lineNo);
            }
        }
    }

    /**
     * Check whether AST node matches event's node.
     *
     * @param ast DetailAST
     * @param event {@code TreeWalkerAuditEvent} object
     * @return true if it is matching.
     */
    private static boolean isMatchingAst(DetailAST ast, TreeWalkerAuditEvent event) {
        return ast.getType() == event.getTokenType()
                && ast.getLineNo() == event.getLine()
                && ast.getColumnNo() == event.getColumnCharIndex();
    }
}

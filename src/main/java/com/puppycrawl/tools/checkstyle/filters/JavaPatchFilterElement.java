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
import java.util.Arrays;
import java.util.Collections;
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
 * Immutable filter element that decides whether to suppress a single violation
 * for one file in the patch. Used internally by {@code SuppressionJavaPatchFilter}.
 */
public final class JavaPatchFilterElement implements TreeWalkerFilter {
    /**
     * The key of minimum line in child ast lines.
     */
    private static final String MIN = "min";

    /**
     * The key of maximum line in child ast lines.
     */
    private static final String MAX = "max";

    /**
     * Default ancestor token types for known checks, used when a check is
     * registered with {@link ContextScope#ANCESTOR} but the user has not
     * supplied an explicit token type via {@code contextStrategy}.
     * For example, {@code FallThrough} maps to {@code LITERAL_SWITCH}.
     */
    private static final Map<String, List<Integer>>
            CHECK_TO_ANCESTOR_NODES_MAP = new HashMap<>();

    /** Checks whose violations should be attributed at enclosing type scope. */
    private static final Set<String> CLASS_SCOPE_NEVER_SUPPRESSED_CHECKS = new HashSet<>(
            Arrays.asList("CovariantEquals"));

    static {
        CHECK_TO_ANCESTOR_NODES_MAP.put("ArrayTrailingComma",
                Arrays.asList(TokenTypes.ARRAY_INIT));
        CHECK_TO_ANCESTOR_NODES_MAP.put("AvoidNestedBlocks",
                Arrays.asList(TokenTypes.SLIST));
        CHECK_TO_ANCESTOR_NODES_MAP.put("CommentsIndentation",
                Arrays.asList(TokenTypes.METHOD_DEF, TokenTypes.SLIST,
                        TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF,
                        TokenTypes.RECORD_DEF, TokenTypes.ENUM_DEF));
        CHECK_TO_ANCESTOR_NODES_MAP.put("DefaultComesLast",
                Arrays.asList(TokenTypes.LITERAL_SWITCH));
        CHECK_TO_ANCESTOR_NODES_MAP.put("DeclarationOrder",
                Arrays.asList(TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF,
                        TokenTypes.ENUM_DEF));
        CHECK_TO_ANCESTOR_NODES_MAP.put("EqualsHashCode",
                Arrays.asList(TokenTypes.CLASS_DEF));
        CHECK_TO_ANCESTOR_NODES_MAP.put("FinalLocalVariable",
                Arrays.asList(TokenTypes.METHOD_DEF,
                        TokenTypes.VARIABLE_DEF, TokenTypes.CTOR_DEF));
        CHECK_TO_ANCESTOR_NODES_MAP.put("FallThrough",
                Arrays.asList(TokenTypes.LITERAL_SWITCH));
        CHECK_TO_ANCESTOR_NODES_MAP.put("InnerTypeLast",
                Arrays.asList(TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF,
                        TokenTypes.RECORD_DEF, TokenTypes.ENUM_DEF));
        CHECK_TO_ANCESTOR_NODES_MAP.put("RightCurly",
                Arrays.asList(TokenTypes.LITERAL_TRY, TokenTypes.LITERAL_IF));
        CHECK_TO_ANCESTOR_NODES_MAP.put("VariableDeclarationUsageDistance",
                Arrays.asList(TokenTypes.SLIST));
    }

    /**
     * Maps each check name (short or full class name) to its context scope.
     * Built from the {@code contextStrategy} property, or from the old deprecated
     * setters for backward compatibility.
     */
    private final Map<String, ContextScope> contextStrategyMap;

    /**
     * Explicit ancestor token types supplied by the user via {@code contextStrategy},
     * for example {@code FallThrough:LITERAL_SWITCH}. Takes priority over
     * {@link #CHECK_TO_ANCESTOR_NODES_MAP} when present.
     */
    private final Map<String, List<Integer>> userDefinedAncestorTokensMap;

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
     * Creates a filter element for one file in the patch.
     *
     * @param fileNameValue                     file name suffix from the patch header
     * @param lineRangeListValue                changed line ranges for this file
     * @param strategyValue                     suppression strategy to apply
     * @param contextStrategyMapValue           check name to scope map; may be {@code null}
     * @param userDefinedAncestorTokensMapValue explicit ancestor token types from the
     *                                          {@code contextStrategy} property; may be
     *                                          {@code null}
     * @param neverSuppressedChecksValue        checks that should never be suppressed
     */
    public JavaPatchFilterElement(String fileNameValue,
                                   List<List<Integer>> lineRangeListValue,
                                   Strategy strategyValue,
                                   Map<String, ContextScope> contextStrategyMapValue,
                                   Map<String, List<Integer>> userDefinedAncestorTokensMapValue,
                                   Set<String> neverSuppressedChecksValue) {
        this.fileName = fileNameValue;
        this.lineRangeList = lineRangeListValue;
        this.strategy = strategyValue;
        this.neverSuppressedChecks = neverSuppressedChecksValue;
        final Map<String, ContextScope> contextMap;
        if (contextStrategyMapValue == null) {
            contextMap = Collections.emptyMap();
        }
        else {
            contextMap = Collections.unmodifiableMap(new HashMap<>(contextStrategyMapValue));
        }
        this.contextStrategyMap = contextMap;
        final Map<String, List<Integer>> ancestorMap;
        if (userDefinedAncestorTokensMapValue == null) {
            ancestorMap = Collections.emptyMap();
        }
        else {
            ancestorMap = Collections.unmodifiableMap(
                    new HashMap<>(userDefinedAncestorTokensMapValue));
        }
        this.userDefinedAncestorTokensMap = ancestorMap;
    }

    @Override
    public boolean accept(TreeWalkerAuditEvent event) {
        final boolean result;

        if (Strategy.CONTEXT == strategy) {
            result = isFileNameMatching(event)
                    && (isMatchingByNeverSuppressedCheck(event)
                    || isMatchingByContextStrategy(event)
                    || isLineMatching(event));
        }
        else {
            result = isFileNameMatching(event)
                    && (isMatchingByNeverSuppressedCheck(event)
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
        String eventFileName = event.fileName();
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
     * Handles checks in {@code neverSuppressedChecks} with tighter scope logic.
     *
     * <p>Most checks just need a line match. A few are special:
     * {@code CovariantEquals} fires on a method but the real cause is the enclosing
     * type missing an {@code equals(Object)} override, so we check whether any changed
     * line falls inside that type instead.
     *
     * <p>For anything not in {@link #CLASS_SCOPE_NEVER_SUPPRESSED_CHECKS} the
     * fallback is to show the violation whenever the file is touched.
     *
     * @param event audit event
     * @return true if the violation should be shown
     */
    private boolean isMatchingByNeverSuppressedCheck(TreeWalkerAuditEvent event) {
        boolean result = false;
        if (isNeverSuppressCheck(event)) {
            result = isLineMatching(event);
            if (!result) {
                final String checkShortName = getCheckShortName(event);
                if (CLASS_SCOPE_NEVER_SUPPRESSED_CHECKS.contains(checkShortName)
                        && strategy != Strategy.NEWLINE) {
                    result = isChangedLineInsideEnclosingType(event);
                }
                else {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Returns true if any changed line falls inside the enclosing class/interface/enum/record
     * that contains the violation.
     *
     * @param event audit event
     * @return true if a changed line is inside the enclosing type
     */
    private boolean isChangedLineInsideEnclosingType(TreeWalkerAuditEvent event) {
        final DetailAST eventAst = getEventAst(event);
        DetailAST scopeAst = eventAst;
        while (scopeAst != null && scopeAst.getType() != TokenTypes.CLASS_DEF
                && scopeAst.getType() != TokenTypes.INTERFACE_DEF
                && scopeAst.getType() != TokenTypes.ENUM_DEF
                && scopeAst.getType() != TokenTypes.RECORD_DEF) {
            scopeAst = scopeAst.getParent();
        }
        return isChangedLineInAstScope(scopeAst);
    }

    /**
     * Returns true if any changed line falls within the line range covered by {@code ast}
     * and all its descendants.
     *
     * @param ast the enclosing node
     * @return true if a changed line is inside this node's range
     */
    private boolean isChangedLineInAstScope(DetailAST ast) {
        boolean result = false;
        if (ast != null) {
            final Map<String, Integer> childAstLineNoMap = getChildAstLineNo(ast);
            final int childAstStartLine = childAstLineNoMap.get(MIN);
            final int childAstEndLine = childAstLineNoMap.get(MAX);
            result = lineMatching(childAstStartLine, childAstEndLine);
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
        if (event.violation() != null) {
            result = lineMatching(event.getLine());
        }
        return result;
    }

    /**
     * Checks if the current line matches any line range in the patch.
     *
     * @param currentLine the line number to check
     * @return true if the line matches a range in the patch
     */
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
     * @param childAstStartLine event ast node's child nodes' min line
     *                          number
     * @param childAstEndLine   event ast node's child nodes' max line
     *                          number
     * @return true if one line is between childAstStartLine and
     *         childAstEndLine line number.
     */
    private boolean lineMatching(int childAstStartLine, int childAstEndLine) {
        boolean result = false;
        for (List<Integer> singleLineRangeList : lineRangeList) {
            final int startLine = singleLineRangeList.get(0) + 1;
            int endLine = singleLineRangeList.get(1) + 1;
            if (startLine == endLine) {
                endLine++;
            }

            result = childAstStartLine <= startLine
                    && startLine <= childAstEndLine
                    || childAstStartLine <= endLine - 1
                    && endLine - 1 <= childAstEndLine;

            if (result) {
                break;
            }
        }
        return result;
    }

    /**
     * Returns true if the violation's resolved AST scope overlaps with a changed line.
     * Looks the check up in {@link #contextStrategyMap} by short or full name,
     * resolves the node via {@link #getAncestorAst}, then checks line overlap.
     *
     * @param event the audit event
     * @return true if the context scope overlaps a changed line
     */
    private boolean isMatchingByContextStrategy(TreeWalkerAuditEvent event) {
        boolean result = false;
        final ContextScope scope = resolveContextScope(event);
        if (scope != null) {
            final DetailAST eventAst = getAncestorAst(event, scope);

            if (eventAst != null) {
                final Map<String, Integer> childAstLineNoMap =
                    getChildAstLineNo(eventAst);
                final int childAstStartLine = childAstLineNoMap.get(MIN);
                final int childAstEndLine = childAstLineNoMap.get(MAX);
                result = lineMatching(childAstStartLine, childAstEndLine);
            }
        }
        return result;
    }

    /**
     * Returns the scope for the check that raised {@code event},
     * or {@code null} if the check is not in {@link #contextStrategyMap}.
     * Tries the full class name first, then the short name.
     *
     * @param event the audit event
     * @return the assigned {@link ContextScope}, or null
     */
    private ContextScope resolveContextScope(TreeWalkerAuditEvent event) {
        final String checkName = getCheckName(event);
        final String checkShortName = getCheckShortName(event);
        ContextScope scope = contextStrategyMap.get(checkName);
        if (scope == null) {
            scope = contextStrategyMap.get(checkShortName);
        }
        return scope;
    }

    /**
     * Returns the AST node to use as the context window for line matching.
     *
     * <ul>
     * <li>{@link ContextScope#SELF} — the violation node itself</li>
     * <li>{@link ContextScope#PARENT} — the immediate parent</li>
     * <li>{@link ContextScope#ANCESTOR} — walks up until hitting a node whose token type
     *     is in {@link #userDefinedAncestorTokensMap} (user-supplied) or
     *     {@link #CHECK_TO_ANCESTOR_NODES_MAP} (built-in fallback)</li>
     * </ul>
     *
     * @param event the audit event
     * @param scope the scope to apply (never null)
     * @return the resolved node, or null if not found
     */
    private DetailAST getAncestorAst(TreeWalkerAuditEvent event, ContextScope scope) {
        DetailAST eventAst = getEventAst(event);
        switch (scope) {
            case SELF -> {
                // Use the violation's own AST node as the context window.
            }
            case PARENT -> {
                if (eventAst != null) {
                    eventAst = eventAst.getParent();
                }
            }
            case ANCESTOR -> {
                if (eventAst != null) {
                    eventAst = eventAst.getParent();
                    List<Integer> ancestorTokens =
                            resolveAncestorTokens(getCheckName(event));
                    if (ancestorTokens == null) {
                        ancestorTokens = resolveAncestorTokens(getCheckShortName(event));
                    }
                    if (ancestorTokens == null) {
                        ancestorTokens =
                                CHECK_TO_ANCESTOR_NODES_MAP.get(getCheckShortName(event));
                    }
                    while (eventAst != null && ancestorTokens != null
                            && !ancestorTokens.contains(eventAst.getType())) {
                        eventAst = eventAst.getParent();
                    }
                }
            }
            default -> {
                // No other values exist in ContextScope; this case is unreachable.
            }
        }
        return eventAst;
    }

    /**
     * Returns the user-supplied ancestor token list for the given check name,
     * or {@code null} if none was set.
     *
     * @param checkName short or full check name
     * @return token-type list, or null
     */
    private List<Integer> resolveAncestorTokens(String checkName) {
        return userDefinedAncestorTokensMap.get(checkName);
    }

    /**
     * Check whether checkNameSet contains event's check.
     *
     * @param checkNameSet Set of check names
     * @param event {@code TreeWalkerAuditEvent} object
     * @return true if set contains event's check.
     */
    private static boolean containsShortName(Set<String> checkNameSet,
                                      TreeWalkerAuditEvent event) {
        final String checkName = getCheckName(event);
        final String checkShortName = getCheckShortName(event);
        return checkNameSet.contains(checkName)
                || checkNameSet.contains(checkShortName);

    }

    /**
     * Returns the simple class name of the check that raised the event.
     *
     * @param event the audit event
     * @return simple class name (e.g. {@code FallThroughCheck})
     */
    private static String getCheckName(TreeWalkerAuditEvent event) {
        final String[] checkNames = event.violation()
                .getSourceName().split("\\.");
        return checkNames[checkNames.length - 1];
    }

    /**
     * Returns the check name with the {@code Check} suffix stripped.
     * For example, {@code FallThroughCheck} becomes {@code FallThrough}.
     *
     * @param event the audit event
     * @return short check name
     */
    private static String getCheckShortName(TreeWalkerAuditEvent event) {
        return getCheckName(event).replaceAll("Check", "");
    }

    /**
     * Walks the AST to find the node that matches the event's token type,
     * line, and column. Returns null if no match is found.
     *
     * @param event the audit event
     * @return the matching AST node, or null
     */
    private static DetailAST getEventAst(TreeWalkerAuditEvent event) {
        DetailAST curNode = event.rootAst();
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
     * Scans {@code ast} and all its descendants to find the min and max line numbers.
     * The result map has keys {@link #MIN} and {@link #MAX}.
     *
     * @param ast the root node to scan
     * @return map with min and max line numbers across the subtree
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
            while (curNode != null && toVisit == null
                    && curNode != ast.getParent()) {
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
     * Updates the min/max map if {@code ast}'s line number is outside the current range.
     * Does nothing if {@code ast} is null.
     *
     * @param childAstLineNoMap running min/max map
     * @param ast               node to check; may be null
     */
    private static void setChildAstLineNo(
            Map<String, Integer> childAstLineNoMap, DetailAST ast) {
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
     * Returns true if {@code ast} matches the token type, line, and column of the event.
     *
     * @param ast   node to test
     * @param event the audit event
     * @return true if the node is the one the event points at
     */
    private static boolean isMatchingAst(DetailAST ast,
                                         TreeWalkerAuditEvent event) {
        return ast.getType() == event.getTokenType()
                && ast.getLineNo() == event.getLine()
                && ast.getColumnNo() == event.getColumnCharIndex();
    }
}

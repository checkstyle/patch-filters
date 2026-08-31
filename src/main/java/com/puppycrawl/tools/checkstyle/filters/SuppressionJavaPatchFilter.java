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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.Patch;

import com.puppycrawl.tools.checkstyle.TreeWalkerAuditEvent;
import com.puppycrawl.tools.checkstyle.TreeWalkerFilter;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.ExternalResourceHolder;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.FilterUtil;

/**
 * Filter {@code SuppressionJavaPatchFilter} rejects audit events for Check
 * violations according to a patch file.
 *
 * @since 8.34
 */
public final class SuppressionJavaPatchFilter extends AutomaticBean implements
        TreeWalkerFilter, ExternalResourceHolder {

    /** Logger for deprecation warnings. */
    private static final Logger LOG =
            Logger.getLogger(SuppressionJavaPatchFilter.class.getName());

    /** Delimiter for splitting comma-separated property values. */
    private static final String COMMA = ",";

    /** Hardcoded list of checks that always use SELF scope. Currently empty. */
    private static final List<String> SUPPORT_CONTEXT_STRATEGY_CHECKS = Arrays.asList();

    /**
     * Specify the location of the patch file.
     */
    private String file;

    /**
     * Control what to do when the file is not existing. If {@code optional}
     * is set to {@code false} the file must exist, or else it ends with
     * error. On the other hand if optional is {@code true} and file is not
     * found, the filter accept all audit events.
     */
    private boolean optional;

    /**
     * Control if only consider added lines in file.
     */
    private Strategy strategy = Strategy.NEWLINE;

    /**
     * Maps each check name to its context scope.
     * Built from the {@code contextStrategy} property or from the deprecated setters.
     */
    private Map<String, ContextScope> contextStrategyMap = new HashMap<>();

    /**
     * Ancestor token types explicitly set via {@code contextStrategy} (e.g.
     * {@code FallThrough:LITERAL_SWITCH}). These take priority over the
     * built-in lookup table in {@code JavaPatchFilterElement}.
     */
    private Map<String, List<Integer>> userDefinedAncestorTokensMap = new HashMap<>();

    /**
     * Set has user defined Checks to never suppress if files are referenced
     * in patch. This property is useful for Checks that current context
     * strategy can not cover all violations.
     */
    private Set<String> neverSuppressedChecks;

    /**
     * Set of individual suppresses.
     */
    private Set<TreeWalkerFilter> filters = new HashSet<>();

    /**
     * Setter to specify the location of the patch file.
     *
     * @param fileName name of the patch file.
     * @since 8.34
     */
    public void setFile(String fileName) {
        file = fileName;
    }

    /**
     * Setter to control if only consider added lines in file.
     *
     * @param strategyName tells if only consider added lines is add, should be
     *                 added or changed.
     * @since 8.34
     */
    public void setStrategy(String strategyName) {
        this.strategy = Strategy.valueOf(strategyName.toUpperCase());
    }

    /**
     * Configures the unified context strategy for checks running under the
     * {@code context} strategy.
     *
     * <p>Accepts a comma-separated list of {@code CheckName:SCOPE} or
     * {@code CheckName:TOKEN_TYPE} pairs. {@code SCOPE} is one of:
     * <ul>
     *   <li>{@code SELF} — match against the violation's own AST node</li>
     *   <li>{@code PARENT} — match against the immediate parent node</li>
     *   <li>{@code ANCESTOR} — walk up using the built-in token map
     *       (for checks already listed there)</li>
     *   <li>Any valid {@code TokenTypes} constant name (e.g. {@code LITERAL_SWITCH},
     *       {@code CLASS_DEF}) — walk up to the first ancestor of that token type;
     *       works for any check, including custom ones</li>
     * </ul>
     *
     * <p>Whitespace around names is trimmed. Examples:
     * <pre>
     * &lt;property name="contextStrategy"
     *           value="MethodLength:SELF, RightCurly:PARENT, FallThrough:LITERAL_SWITCH"/&gt;
     * </pre>
     *
     * @param contextStrategyValue comma-separated entries
     * @throws IllegalArgumentException if an entry is malformed or the scope/token name
     *         is not recognised
     * @since 10.x
     */
    public void setContextStrategy(String contextStrategyValue) {
        final String[] entries = contextStrategyValue.split(COMMA);
        for (String entry : entries) {
            final String trimmed = entry.trim();
            final int colonIndex = trimmed.lastIndexOf(':');
            if (colonIndex < 1 || colonIndex == trimmed.length() - 1) {
                throw new IllegalArgumentException(
                        "contextStrategy entry must be 'CheckName:SCOPE' or "
                                + "'CheckName:TOKEN_TYPE', got: '" + trimmed + "'");
            }
            final String checkName = trimmed.substring(0, colonIndex).trim();
            final String scopeName = trimmed.substring(colonIndex + 1).trim().toUpperCase();

            ContextScope scope = null;
            try {
                scope = ContextScope.valueOf(scopeName);
            }
            catch (IllegalArgumentException ignored) {
                scope = null;
            }

            if (scope != null) {
                contextStrategyMap.put(checkName, scope);
            }
            else {
                try {
                    final int tokenType =
                            TokenTypes.class.getField(scopeName).getInt(null);
                    contextStrategyMap.put(checkName, ContextScope.ANCESTOR);
                    userDefinedAncestorTokensMap.put(
                            checkName, Collections.singletonList(tokenType));
                }
                catch (NoSuchFieldException | IllegalAccessException exc) {
                    throw new IllegalArgumentException(
                            "Unknown scope or token type '" + scopeName
                                    + "' in contextStrategy entry '" + trimmed
                                    + "'. Use SELF, PARENT, ANCESTOR, or a valid "
                                    + "TokenTypes constant name (e.g. LITERAL_SWITCH,"
                                    + " CLASS_DEF, SLIST).", exc);
                }
            }
        }
    }

    /**
     * Setter to set has user defined list of Checks need modify violation
     * nodes to their parent abstract nodes to get their child nodes.
     *
     * @param checkNamesForContextStrategyByTokenOrParentSetValue
     *                                 string which is user defined Checks
     *                                 that need modify violation nodes
     *                                 to their parent abstract nodes
     *                                 to get their child nodes,
     *                                 split by comma
     * @since 8.34
     * @deprecated Since 10.x. Use {@code contextStrategy} with {@code CheckName:PARENT} instead.
     *             Example: {@code &lt;property name="contextStrategy"
     *             value="RightCurly:PARENT"/&gt;}.
     */
    @Deprecated(since = "10.x", forRemoval = true)
    public void setCheckNamesForContextStrategyByTokenOrParentSet(
            String checkNamesForContextStrategyByTokenOrParentSetValue) {
        LOG.warning("Deprecated property 'checkNamesForContextStrategyByTokenOrParentSet'. "
                + "Use contextStrategy with :PARENT scope instead. "
                + "Example: <property name=\"contextStrategy\" value=\"CheckName:PARENT\"/>");
        for (String check : checkNamesForContextStrategyByTokenOrParentSetValue.split(COMMA)) {
            contextStrategyMap.put(check.trim(), ContextScope.PARENT);
        }
    }

    /**
     * Setter to set has user defined list of Checks need modify violation
     * nodes to their ancestor abstract nodes to get their child nodes.
     *
     * <p>Note: The ancestor token type used for each check was previously hardcoded
     * inside {@code JavaPatchFilterElement}. With the new {@code contextStrategy}
     * property you can now specify the exact token type explicitly, e.g.
     * {@code FallThrough:LITERAL_SWITCH}, which eliminates the need for that
     * hardcoded map.
     *
     * @param checkNamesForContextStrategyByTokenOrAncestorSetValue
     *                                 string which is user defined Checks
     *                                 that need modify violation nodes
     *                                 to their ancestor abstract nodes
     *                                 to get their child nodes,
     *                                 split by comma
     * @since 8.34
     * @deprecated Since 10.x. Use {@code contextStrategy} with an explicit token type instead.
     *             Example: {@code &lt;property name="contextStrategy"
     *             value="FallThrough:LITERAL_SWITCH"/&gt;}.
     */
    @Deprecated(since = "10.x", forRemoval = true)
    public void setCheckNamesForContextStrategyByTokenOrAncestorSet(
            String checkNamesForContextStrategyByTokenOrAncestorSetValue) {
        LOG.warning("Deprecated property 'checkNamesForContextStrategyByTokenOrAncestorSet'. "
                + "Use contextStrategy with an explicit token type instead. "
                + "Example: <property name=\"contextStrategy\" "
                + "value=\"FallThrough:LITERAL_SWITCH\"/>");
        for (String check
                : checkNamesForContextStrategyByTokenOrAncestorSetValue.split(COMMA)) {
            contextStrategyMap.put(check.trim(), ContextScope.ANCESTOR);
        }
    }

    /**
     * Setter to set has user defined Checks that support context strategy.
     *
     * @param supportContextStrategyChecksValue string has user defined checks that
     *                                     support context strategy
     * @since 8.34
     * @deprecated Since 10.x. Use {@code contextStrategy} with {@code CheckName:SELF} instead.
     *             Example: {@code &lt;property name="contextStrategy"
     *             value="MethodLength:SELF"/&gt;}.
     */
    @Deprecated(since = "10.x", forRemoval = true)
    public void setSupportContextStrategyChecks(
            String supportContextStrategyChecksValue) {
        LOG.warning("Deprecated property 'supportContextStrategyChecks'. "
                + "Use contextStrategy with :SELF scope instead. "
                + "Example: <property name=\"contextStrategy\" value=\"CheckName:SELF\"/>");
        final String[] checksArray = supportContextStrategyChecksValue.split(COMMA);
        for (String check : checksArray) {
            contextStrategyMap.put(check.trim(), ContextScope.SELF);
        }
        contextStrategyMap.putAll(
                buildSelfMapFromList(SUPPORT_CONTEXT_STRATEGY_CHECKS));
    }

    /**
     * Setter to set has user defined list of Checks to NEVER suppress if
     * files are touched.
     *
     * @param neverSuppressedChecksValue string has user defined Checks to never
     *                              suppress if files are touched, split by
     *                              comma
     * @since 8.34
     */
    public void setNeverSuppressedChecks(String neverSuppressedChecksValue) {
        final String[] checksArray =
                neverSuppressedChecksValue.split(COMMA);
        this.neverSuppressedChecks =
                new HashSet<>(Arrays.asList(checksArray));
    }

    /**
     * Setter to control what to do when the file is not existing.
     * If {@code optional} is set to {@code false} the file must exist, or
     * else it ends with error. On the other hand if optional is {@code true}
     * and file is not found, the filter accept all audit events.
     *
     * @param optionalValue tells if config file existence is optional.
     * @since 8.34
     */
    public void setOptional(boolean optionalValue) {
        this.optional = optionalValue;
    }

    @Override
    public boolean accept(TreeWalkerAuditEvent treeWalkerAuditEvent) {
        boolean result = false;
        for (TreeWalkerFilter filter : filters) {
            if (filter.accept(treeWalkerAuditEvent)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void finishLocalSetup() throws CheckstyleException {
        if (file != null) {
            if (optional) {
                if (FilterUtil.isFileExists(file)) {
                    loadPatchFile();
                }
                else {
                    filters = new HashSet<>();
                }
            }
            else {
                loadPatchFile();
            }
        }
    }

    /**
     * Loads and parses the patch file to create filter elements.
     *
     * @throws CheckstyleException if patch file cannot be loaded or parsed
     */
    private void loadPatchFile() throws CheckstyleException {
        try (InputStream is = new CrFilterInputStream(new FileInputStream(file))) {
            final Patch patch = new Patch();
            patch.parse(is);
            final List<? extends FileHeader> fileHeaders = patch.getFiles();
            for (FileHeader fileHeader : fileHeaders) {
                final LoadPatchFileUtils loadPatchFileUtils =
                        new LoadPatchFileUtils(fileHeader, strategy);
                final String fileName = loadPatchFileUtils.getFileName();
                final List<List<Integer>> lineRangeList =
                        loadPatchFileUtils.getLineRangeList();
                final JavaPatchFilterElement element =
                        new JavaPatchFilterElement(fileName, lineRangeList,
                                strategy,
                                Collections.unmodifiableMap(contextStrategyMap),
                                Collections.unmodifiableMap(userDefinedAncestorTokensMap),
                                neverSuppressedChecks);
                filters.add(element);
            }
        }
        // -@cs[IllegalCatch] There is no other way to deliver filename that
        // was under processing when a jgit exception occurs.
        catch (Exception exception) {
            throw new CheckstyleException(
                    "an error occurred when loading patch file " + file,
                    exception);
        }
    }

    @Override
    public Set<String> getExternalResourceLocations() {
        return Collections.singleton(file);
    }

    /**
     * Builds a SELF-scope entry map from a list of check names.
     * Used internally to convert the default {@link #SUPPORT_CONTEXT_STRATEGY_CHECKS} list.
     *
     * @param checks list of check names
     * @return map of check name to {@link ContextScope#SELF}
     */
    private static Map<String, ContextScope> buildSelfMapFromList(List<String> checks) {
        final Map<String, ContextScope> map = new HashMap<>();
        for (String check : checks) {
            map.put(check.trim(), ContextScope.SELF);
        }
        return map;
    }
}

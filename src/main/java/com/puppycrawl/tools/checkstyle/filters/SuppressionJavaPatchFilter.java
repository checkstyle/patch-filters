///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2022 the original author or authors.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.Patch;

import com.puppycrawl.tools.checkstyle.TreeWalkerAuditEvent;
import com.puppycrawl.tools.checkstyle.TreeWalkerFilter;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.ExternalResourceHolder;
import com.puppycrawl.tools.checkstyle.utils.FilterUtil;

/**
 * <p>
 * Filter {@code SuppressionJavaPatchFilter} rejects audit events for Check violations
 * according to a patch file.
 * </p>
 *
 * @since 8.34
 */
public class SuppressionJavaPatchFilter extends AutomaticBean implements
        TreeWalkerFilter, ExternalResourceHolder {
    /**
     * To split never Suppressed Checks and Ids's string.
     */
    private static final String COMMA = ",";

    /**
     * List of checks that support context strategy.
     */
    private static final List<String> SUPPORT_CONTEXT_STRATEGY_CHECKS = Arrays.asList();

    /**
     * Specify the location of the patch file.
     */
    private String file;

    /**
     * Control what to do when the file is not existing. If {@code optional} is
     * set to {@code false} the file must exist, or else it ends with error.
     * On the other hand if optional is {@code true} and file is not found,
     * the filter accept all audit events.
     */
    private boolean optional;

    /**
     * Control if only consider added lines in file.
     */
    private Strategy strategy = Strategy.NEWLINE;

    /**
     * Set has user defined Checks that need modify violation nodes
     * to their parent abstract nodes to get their child nodes.
     */
    private Set<String> checkNamesForContextStrategyByTokenOrParentSet;

    /**
     * Set has user defined Checks that need modify violation nodes
     * to their ancestor abstract nodes to get their child nodes.
     */
    private Set<String> checkNamesForContextStrategyByTokenOrAncestorSet;

    /**
     * Set has user defined Checks that support context strategy.
     */
    private Set<String> supportContextStrategyChecks;

    /**
     * Set has user defined Checks to never suppress if files are referenced in patch.
     * This property is useful for Checks that current context strategy can not cover all
     * violations.
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
     */
    public void setFile(String fileName) {
        file = fileName;
    }

    /**
     * Setter to control if only consider added lines in file.
     *
     * @param strategy tells if only consider added lines is add, should be added or changed.
     */
    public void setStrategy(String strategy) {
        this.strategy = Strategy.valueOf(strategy.toUpperCase());
    }

    /**
     * Setter to set has user defined list of Checks need modify violation nodes
     * to their parent abstract nodes to get their child nodes.
     *
     * @param checkNamesForContextStrategyByTokenOrParentSet string which is  user defined Checks
     *                                                         that need modify violation nodes
     *                                                         to their parent abstract nodes
     *                                                         to get their child nodes,
     *                                                         split by comma
     */
    public void setCheckNamesForContextStrategyByTokenOrParentSet(
            String checkNamesForContextStrategyByTokenOrParentSet) {
        final String[] checksArray = checkNamesForContextStrategyByTokenOrParentSet.split(COMMA);
        this.checkNamesForContextStrategyByTokenOrParentSet =
                new HashSet<>(Arrays.asList(checksArray));
    }

    /**
     * Setter to set has user defined list of Checks need modify violation nodes
     * to their ancestor abstract nodes to get their child nodes.
     *
     * @param checkNamesForContextStrategyByTokenOrAncestorSet string which is  user defined Checks
     *                                                         that need modify violation nodes
     *                                                         to their ancestor abstract nodes
     *                                                         to get their child nodes,
     *                                                         split by comma
     */
    public void setCheckNamesForContextStrategyByTokenOrAncestorSet(
            String checkNamesForContextStrategyByTokenOrAncestorSet) {
        final String[] checksArray = checkNamesForContextStrategyByTokenOrAncestorSet.split(COMMA);
        this.checkNamesForContextStrategyByTokenOrAncestorSet =
                new HashSet<>(Arrays.asList(checksArray));
    }

    /**
     * Setter to set has user defined Checks that support context strategy.
     *
     * @param supportContextStrategyChecks string has user defined checks that support
     *                                     context strategy
     */
    public void setSupportContextStrategyChecks(String supportContextStrategyChecks) {
        final String[] checksArray = supportContextStrategyChecks.split(COMMA);
        this.supportContextStrategyChecks = new HashSet<>(Arrays.asList(checksArray));
        this.supportContextStrategyChecks.addAll(SUPPORT_CONTEXT_STRATEGY_CHECKS);
    }

    /**
     * Setter to set has user defined list of Checks to NEVER suppress if files are touched.
     *
     * @param neverSuppressedChecks string has user defined Checks to never suppress
     *                              if files are touched, split by comma
     */
    public void setNeverSuppressedChecks(String neverSuppressedChecks) {
        final String[] checksArray = neverSuppressedChecks.split(COMMA);
        this.neverSuppressedChecks = new HashSet<>(Arrays.asList(checksArray));
    }

    /**
     * Setter to control what to do when the file is not existing.
     * If {@code optional} is set to {@code false} the file must exist, or else
     * it ends with error. On the other hand if optional is {@code true}
     * and file is not found, the filter accept all audit events.
     *
     * @param optional tells if config file existence is optional.
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
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

    private void loadPatchFile() throws CheckstyleException {
        try (InputStream is = new CrFilterInputStream(new FileInputStream(file))) {
            final Patch patch = new Patch();
            patch.parse(is);
            final List<? extends FileHeader> fileHeaders = patch.getFiles();
            for (FileHeader fileHeader : fileHeaders) {
                final LoadPatchFileUtils loadPatchFileUtils =
                        new LoadPatchFileUtils(fileHeader, strategy);
                final String fileName = loadPatchFileUtils.getFileName();
                final List<List<Integer>> lineRangeList = loadPatchFileUtils.getLineRangeList();
                final JavaPatchFilterElement element =
                        new JavaPatchFilterElement(fileName, lineRangeList,
                                strategy,
                                checkNamesForContextStrategyByTokenOrParentSet,
                                checkNamesForContextStrategyByTokenOrAncestorSet,
                                supportContextStrategyChecks,
                                neverSuppressedChecks);
                filters.add(element);
            }
        }
        // -@cs[IllegalCatch] There is no other way to deliver filename that was under
        // processing when a jgit exception occurs.
        catch (Exception exception) {
            throw new CheckstyleException("an error occurred when loading patch file "
                    + file, exception);
        }
    }

    @Override
    public Set<String> getExternalResourceLocations() {
        return Collections.singleton(file);
    }
}

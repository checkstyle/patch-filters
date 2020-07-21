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

import java.io.FileInputStream;
import java.io.IOException;
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

/**
 * <p>
 * Filter {@code SuppressionPatchXpathFilter} rejects audit events for Check violations
 * according to a patch file.
 * </p>
 *
 * @since 8.34
 */
public class SuppressionPatchXpathFilter extends AutomaticBean implements
        TreeWalkerFilter, ExternalResourceHolder {
    /**
     * To split never Suppressed Checks and Ids's string.
     */
    private static final String COMMA = ",";

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
    private String strategy;

    /**
     * Set has user defined Checks that need modify violation nodes
     * to their parent abstract nodes to get their child nodes.
     */
    private Set<String> checkNamesForContextStrategyByTokenOrParentSet;

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
        this.strategy = strategy;
    }

    /**
     * Setter to set has user defined list of Checks need modify violation nodes
     * to their parent abstract nodes to get their child nodes.
     *
     * @param checkNamesForContextStrategyByTokenOrParentSet string has user defined Checks to never
     *                                                   suppress if files are touched, split
     *                                                   by comma
     */
    public void setCheckNamesForContextStrategyByTokenOrParentSet(
            String checkNamesForContextStrategyByTokenOrParentSet) {
        final String[] checksArray = checkNamesForContextStrategyByTokenOrParentSet.split(COMMA);
        this.checkNamesForContextStrategyByTokenOrParentSet =
                new HashSet<>(Arrays.asList(checksArray));
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
            loadPatchFile();
        }
    }

    private void loadPatchFile() throws CheckstyleException {
        try (InputStream is = new FileInputStream(file)) {
            final Patch patch = new Patch();
            patch.parse(is);
            final List<? extends FileHeader> fileHeaders = patch.getFiles();
            for (FileHeader fileHeader : fileHeaders) {
                final LoadPatchFileUtils loadPatchFileUtils =
                        new LoadPatchFileUtils(fileHeader, strategy);
                final String fileName = loadPatchFileUtils.getFileName();
                final List<List<Integer>> lineRangeList = loadPatchFileUtils.getLineRangeList();
                final PatchXpathFilterElement element =
                        new PatchXpathFilterElement(fileName, lineRangeList,
                                strategy, checkNamesForContextStrategyByTokenOrParentSet);
                filters.add(element);
            }
        }
        catch (IOException ex) {
            throw new CheckstyleException("an error occurred when load patch file", ex);
        }
    }

    @Override
    public Set<String> getExternalResourceLocations() {
        return Collections.singleton(file);
    }
}

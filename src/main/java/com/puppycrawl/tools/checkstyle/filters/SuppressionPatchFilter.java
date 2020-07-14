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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.patch.Patch;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.ExternalResourceHolder;
import com.puppycrawl.tools.checkstyle.api.Filter;

/**
 * <p>
 * Filter {@code SuppressionFilter} rejects audit events for Check violations according to a
 * patch file.
 * </p>
 *
 * @since 8.34
 */
public class SuppressionPatchFilter extends AutomaticBean
        implements Filter, ExternalResourceHolder {

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
     * Set of individual suppresses.
     */
    private PatchFilterSet filters = new PatchFilterSet();

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
    public boolean accept(AuditEvent event) {
        return filters.accept(event);
    }

    /**
     * To finish the part of this component's setup.
     *
     * @throws CheckstyleException if there is a configuration error.
     */
    protected void finishLocalSetup() throws CheckstyleException {
        if (file != null) {
            loadPatchFile();
        }
    }

    private void loadPatchFile() throws CheckstyleException {
        System.out.print(new File(".").getAbsolutePath());
        try (InputStream is = new FileInputStream(file)) {
            final Patch patch = new Patch();
            patch.parse(is);
            final List<? extends FileHeader> fileHeaders = patch.getFiles();
            for (FileHeader fileHeader : fileHeaders) {
                final String fileName = getFileName(fileHeader);
                final List<List<Integer>> lineRangeList = getLineRange(fileHeader);
                final SuppressionPatchFilterElement element =
                        new SuppressionPatchFilterElement(fileName, lineRangeList);
                filters.addFilter(element);
            }
            System.out.println();
        }
        catch (IOException ex) {
            throw new CheckstyleException("an error occurred when load patch file", ex);
        }
    }

    private String getFileName(FileHeader fileHeader) {
        return fileHeader.getNewPath();
    }

    private List<List<Integer>> getLineRange(FileHeader fileHeader) {
        final List<List<Integer>> lineRangeList = new ArrayList<>();
        if (!"RENAME".equals(fileHeader.getChangeType().name())) {
            for (HunkHeader hunkHeader : fileHeader.getHunks()) {
                final EditList edits = hunkHeader.toEditList();
                for (int i = 0; i < edits.size(); i++) {
                    if ("newline".equals(strategy)) {
                        if (Edit.Type.INSERT.equals(edits.get(i).getType())) {
                            final List<Integer> lineRange = new ArrayList<>();
                            lineRange.add(edits.get(i).getBeginB());
                            lineRange.add(edits.get(i).getEndB());
                            lineRangeList.add(lineRange);
                        }
                    }
                    else {
                        if (Edit.Type.INSERT.equals(edits.get(i).getType())
                            || Edit.Type.REPLACE.equals(edits.get(i).getType())) {
                            final List<Integer> lineRange = new ArrayList<>();
                            lineRange.add(edits.get(i).getBeginB());
                            lineRange.add(edits.get(i).getEndB());
                            lineRangeList.add(lineRange);
                        }
                    }
                }
            }
        }
        return lineRangeList;
    }

    @Override
    public Set<String> getExternalResourceLocations() {
        return Collections.singleton(file);
    }

}

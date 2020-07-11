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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;

/**
 * A utils class to help load patch file.
 */
public class LoadPatchFileUtils {
    /**
     * FileHeader to load.
     */
    private final FileHeader fileHeader;

    /**
     * Strategy that should use.
     */
    private final String strategy;

    /**
     * Init LoadPatchFileUtils.
     * @param fileHeader FileHeader
     * @param strategy String
     */
    public LoadPatchFileUtils(FileHeader fileHeader, String strategy) {
        this.fileHeader = fileHeader;
        this.strategy = strategy;
    }

    /**
     * Get file name from FileHeader.
     * @return String fileName
     */
    public String getFileName() {
        return fileHeader.getNewPath();
    }

    /**
     * Get LineRange list from FileHeader.
     * @return List
     */
    public List<List<Integer>> getLineRange() {
        final List<List<Integer>> lineRangeList = new ArrayList<>();
        if (!"RENAME".equals(fileHeader.getChangeType().name())) {
            for (HunkHeader hunkHeader : fileHeader.getHunks()) {
                final EditList edits = hunkHeader.toEditList();
                for (Edit edit : edits) {
                    if ("newline".equals(strategy)) {
                        if (Edit.Type.INSERT.equals(edit.getType())) {
                            final List<Integer> lineRange = new ArrayList<>();
                            lineRange.add(edit.getBeginB());
                            lineRange.add(edit.getEndB());
                            lineRangeList.add(lineRange);
                        }
                    }
                    else {
                        if (Edit.Type.INSERT.equals(edit.getType())
                                || Edit.Type.REPLACE.equals(edit.getType())) {
                            final List<Integer> lineRange = new ArrayList<>();
                            lineRange.add(edit.getBeginB());
                            lineRange.add(edit.getEndB());
                            lineRangeList.add(lineRange);
                        }
                    }
                }
            }
        }
        return lineRangeList;
    }
}

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

package com.puppycrawl.tools.checkstyle.jgit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.Patch;
import org.junit.jupiter.api.Test;

public class GitDiffWithContextSizeZeroTest extends AbstractJgitPatchParserEvaluationTest {

    private static final String CONTEXT_SIZE_0_DIR =
        "src/test/resources/com/puppycrawl/tools/checkstyle/jgit/gitdiff/context-size-0/";

    @Test
    public void testMultiHunksOnSingleFile() throws Exception {
        final String patchName = "MultiHunksOnOneFile.patch";
        final Patch patch = loadPatch(getPatchPath(patchName));
        assertNotNull(patch);

        final List<? extends FileHeader> fileHeaders = patch.getFiles();
        assertEquals(1, fileHeaders.size());

        // file header 0
        assertEquals(FileHeader.PatchType.UNIFIED, fileHeaders.get(0).getPatchType());
        assertEquals(2, fileHeaders.get(0).getHunks().size());

        final EditList edits = fileHeaders.get(0).toEditList();
        assertEquals(2, edits.size());
        assertEquals(new Edit(3, 3, 4, 12), edits.get(0));
        assertEquals(Edit.Type.INSERT, edits.get(0).getType());
        assertEquals(new Edit(16, 16, 25, 27), edits.get(1));
        assertEquals(Edit.Type.INSERT, edits.get(1).getType());
    }

    @Test
    public void testMultipleFilesWithMultipleHunks() throws IOException {
        final String patchName = "MultipleFilesWithMultipleHunks.patch";
        final Patch patch = loadPatch(getPatchPath(patchName));
        assertNotNull(patch);

        final List<? extends FileHeader> fileHeaders = patch.getFiles();
        assertEquals(2, fileHeaders.size());

        // file header 0
        assertEquals(FileHeader.PatchType.UNIFIED, fileHeaders.get(0).getPatchType());
        assertEquals(2, fileHeaders.get(0).getHunks().size());

        final EditList edits0 = fileHeaders.get(0).toEditList();
        assertEquals(2, edits0.size());
        assertEquals(new Edit(3, 3, 4, 12), edits0.get(0));
        assertEquals(Edit.Type.INSERT, edits0.get(0).getType());
        assertEquals(new Edit(10, 10, 19, 21), edits0.get(1));
        assertEquals(Edit.Type.INSERT, edits0.get(1).getType());

        // file header 1
        assertEquals(FileHeader.PatchType.UNIFIED, fileHeaders.get(1).getPatchType());
        assertEquals(2, fileHeaders.get(1).getHunks().size());

        final EditList edits1 = fileHeaders.get(1).toEditList();
        assertEquals(2, edits1.size());
        assertEquals(new Edit(3, 3, 4, 12), edits1.get(0));
        assertEquals(Edit.Type.INSERT, edits1.get(0).getType());
        assertEquals(new Edit(16, 16, 25, 27), edits1.get(1));
        assertEquals(Edit.Type.INSERT, edits1.get(1).getType());
    }

    @Test
    public void testHaveRenamedWithNoChangedFile() throws Exception {
        final String patchName = "HaveRenamedWithNoChangedFile.patch";
        final Patch patch = loadPatch(getPatchPath(patchName));
        assertNotNull(patch);

        final List<? extends FileHeader> fileHeaders = patch.getFiles();
        assertEquals(1, fileHeaders.size());

        // file header 0
        assertEquals(FileHeader.PatchType.UNIFIED, fileHeaders.get(0).getPatchType());
        assertEquals(0, fileHeaders.get(0).getHunks().size());
        assertEquals("RENAME", fileHeaders.get(0).getChangeType().name());

    }

    @Test
    public void testHaveRemovedFile() throws Exception {
        final String patchName = "HaveRemovedFilePatch.patch";
        final Patch patch = loadPatch(getPatchPath(patchName));
        assertNotNull(patch);

        final List<? extends FileHeader> fileHeaders = patch.getFiles();
        assertEquals(1, fileHeaders.size());

        // file header 0
        assertEquals(FileHeader.PatchType.UNIFIED, fileHeaders.get(0).getPatchType());
        assertEquals(1, fileHeaders.get(0).getHunks().size());

        final EditList edits0 = fileHeaders.get(0).toEditList();
        assertEquals(1, edits0.size());
        assertEquals(new Edit(0, 23, -1, -1), edits0.get(0));
        assertEquals(Edit.Type.DELETE, edits0.get(0).getType());
    }

    @Test
    public void testMovedCodeInOneFileZeroSize() throws Exception {
        final String patchName = "MovedCodeInOneFile.patch";
        final Patch patch = loadPatch(getPatchPath(patchName));
        assertNotNull(patch);

        final List<? extends FileHeader> fileHeaders = patch.getFiles();
        assertEquals(1, fileHeaders.size());

        // file header 0
        assertEquals(FileHeader.PatchType.UNIFIED, fileHeaders.get(0).getPatchType());
        assertEquals(2, fileHeaders.get(0).getHunks().size());

        final EditList edits0 = fileHeaders.get(0).toEditList();
        assertEquals(2, edits0.size());
        assertEquals(new Edit(5, 12, 4, 4), edits0.get(0));
        assertEquals(Edit.Type.DELETE, edits0.get(0).getType());
        assertEquals(new Edit(27, 27, 21, 29), edits0.get(1));
        assertEquals(Edit.Type.INSERT, edits0.get(1).getType());
    }

    @Test
    public void testOnlyDeletionInOneFile() throws Exception {
        final String patchName = "OnlyDeletionInOneFile.patch";
        final Patch patch = loadPatch(getPatchPath(patchName));
        assertNotNull(patch);

        final List<? extends FileHeader> fileHeaders = patch.getFiles();
        assertEquals(1, fileHeaders.size());

        // file header 0
        assertEquals(FileHeader.PatchType.UNIFIED, fileHeaders.get(0).getPatchType());
        assertEquals(1, fileHeaders.get(0).getHunks().size());

        final EditList edits0 = fileHeaders.get(0).toEditList();
        assertEquals(1, edits0.size());
        assertEquals(new Edit(21, 29, 20, 20), edits0.get(0));
        assertEquals(Edit.Type.DELETE, edits0.get(0).getType());
    }

    @Test
    public void testOnlyAdditionInOneFile() throws Exception {
        final String patchName = "OnlyAdditionInOneFile.patch";
        final Patch patch = loadPatch(getPatchPath(patchName));
        assertNotNull(patch);

        final List<? extends FileHeader> fileHeaders = patch.getFiles();
        assertEquals(1, fileHeaders.size());

        // file header 0
        assertEquals(FileHeader.PatchType.UNIFIED, fileHeaders.get(0).getPatchType());
        assertEquals(1, fileHeaders.get(0).getHunks().size());

        final EditList edits0 = fileHeaders.get(0).toEditList();
        assertEquals(1, edits0.size());
        assertEquals(new Edit(19, 19, 20, 24), edits0.get(0));
        assertEquals(Edit.Type.INSERT, edits0.get(0).getType());
    }

    @Test
    public void testOnlyReplacementInOneFile() throws Exception {
        final String patchName = "OnlyReplaceMentInOneFile.patch";
        final Patch patch = loadPatch(getPatchPath(patchName));
        assertNotNull(patch);

        final List<? extends FileHeader> fileHeaders = patch.getFiles();
        assertEquals(1, fileHeaders.size());

        // file header 0
        assertEquals(FileHeader.PatchType.UNIFIED, fileHeaders.get(0).getPatchType());
        assertEquals(1, fileHeaders.get(0).getHunks().size());

        final EditList edits0 = fileHeaders.get(0).toEditList();
        assertEquals(1, edits0.size());
        assertEquals(new Edit(20, 22, 20, 22), edits0.get(0));
        assertEquals(Edit.Type.REPLACE, edits0.get(0).getType());
    }

    @Test
    public void testInsertDeleteReplaceEditTypesWithinHunk() throws IOException {
        final String patchName = "InsertDeleteReplaceEditTypes.patch";
        final Patch patch = loadPatch(getPatchPath(patchName));
        assertNotNull(patch);

        final List<? extends FileHeader> fileHeaders = patch.getFiles();
        assertEquals(1, fileHeaders.size());

        // file header 0
        assertEquals(FileHeader.PatchType.UNIFIED, fileHeaders.get(0).getPatchType());
        assertEquals(3, fileHeaders.get(0).getHunks().size());

        final EditList edits = fileHeaders.get(0).toEditList();
        assertEquals(3, edits.size());
        assertEquals(new Edit(16, 16, 17, 18), edits.get(0));
        assertEquals(Edit.Type.INSERT, edits.get(0).getType());
        assertEquals(new Edit(20, 21, 21, 22), edits.get(1));
        assertEquals(Edit.Type.REPLACE, edits.get(1).getType());
        assertEquals(new Edit(23, 24, 23, 23), edits.get(2));
        assertEquals(Edit.Type.DELETE, edits.get(2).getType());
    }

    @Override
    protected String getPatchPath(String patchName) {
        return CONTEXT_SIZE_0_DIR + patchName;
    }
}

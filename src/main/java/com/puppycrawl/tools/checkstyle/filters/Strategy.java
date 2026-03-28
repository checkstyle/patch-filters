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

/**
 * This is a strategy enum.
 */
public enum Strategy {
    /**
     * Suppresses all violations except those on newly added lines
     * (INSERT edits only) in the patch file.
     * Corresponds to XML configuration value {@code newline}.
     * Use this for strictest enforcement — only brand new code is checked.
     */
    NEWLINE,

    /**
     * Suppresses all violations except those on added or modified lines
     * (INSERT and REPLACE edits) in the patch file.
     * Corresponds to XML configuration value {@code patchedline}.
     * Use this when both new and changed code should be checked.
     */
    PATCHEDLINE,

    /**
     * Suppresses all violations except those on added, modified,
     * or deleted lines (INSERT, REPLACE, and DELETE edits).
     * For checks in {@code supportContextStrategyChecks}, also considers
     * violations whose child AST nodes fall within changed lines.
     * Corresponds to XML configuration value {@code context}.
     */
    CONTEXT
}

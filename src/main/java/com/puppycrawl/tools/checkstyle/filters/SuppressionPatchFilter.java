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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.Patch;
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
    private boolean add;

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

    /**
     * Setter to control if only consider added lines in file.
     *
     * @param add tells if only consider added lines is add.
     */
    public void setAdd(boolean add) {
        this.add = add;
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
            loadPatchFile(file);
        }
    }

    private void loadPatchFile(String patchFileName) throws CheckstyleException {
        try {
            final List<String> originPatch = Files.readAllLines(new File(patchFileName).toPath());
            final List<List<String>> patchList = getPatchList(originPatch);
            for (List<String> singlePatch : patchList) {
                final String fileName = getFileName(singlePatch);
                final Patch<String> patch = UnifiedDiffUtils.parseUnifiedDiff(singlePatch);
                final List<List<Integer>> lineRangeList = getLineRange(patch);
                final SuppressionPatchFilterElement element =
                        new SuppressionPatchFilterElement(fileName, lineRangeList);
                filters.addFilter(element);
            }
        }
        catch (IOException ex) {
            throw new CheckstyleException("an error occurred when load patch file", ex);
        }
    }

    /**
     * To separate different files's diff contents when there are multiple files in patch file.
     * @param originPatch List of String
     * @return patchedList List of List of String
     */
    private List<List<String>> getPatchList(List<String> originPatch) {
        final List<List<String>> patchedList = new ArrayList<>();
        boolean flag = true;
        List<String> singlePatched = new ArrayList<>();
        for (String str : originPatch) {
            if (str.startsWith("diff ")) {
                if (flag) {
                    flag = false;
                }
                else {
                    patchedList.add(singlePatched);
                    singlePatched = new ArrayList<>();
                }
            }
            singlePatched.add(str);
        }
        patchedList.add(singlePatched);
        return patchedList;
    }

    private String getFileName(List<String> singlePatch) {
        String fileName = null;
        for (String str : singlePatch) {
            if (str.startsWith("+++ ")) {
                fileName = str.split("\\s")[1];
                fileName = fileName.replaceAll("^b/", "");
                break;
            }
        }
        return fileName;
    }

    private List<Integer> getRemovePositionList(Patch<String> patch, int index) {
        final Chunk originChunk = patch.getDeltas().get(index).getSource();
        return originChunk.getChangePosition();
    }

    private List<Integer> getAddPositionList(Patch<String> patch, int index) {
        final Chunk targetChunk = patch.getDeltas().get(index).getTarget();
        return targetChunk.getChangePosition();
    }

    private int dealAddPosition(List<Integer> addPositionList, List<Integer> removePositionList,
                                 int addIndex, int totalAddNum, int totalRemoveNum) {
        if (addIndex == 0) {
            return 0;
        }
        int position = addPositionList.get(addIndex);
        int tempAddNum = 0;
        int tempRemoveNum = 0;
        int matchingRemovePositon = position - totalAddNum + totalRemoveNum;
        if (!removePositionList.contains(matchingRemovePositon)) {
            tempAddNum++;
            for (int k = addIndex+1; k < addPositionList.size(); k++) {
                int tempPosition = addPositionList.get(k-1);
                int nextAddPosition = addPositionList.get(k);
                if (nextAddPosition - tempPosition == 1) {
                    tempAddNum++;
                } else {
                    break;
                }
            }
        } else {
            for (int k = removePositionList.indexOf(matchingRemovePositon) + 1;
                 k < removePositionList.size(); k++) {
                int tempPosition = removePositionList.get(k-1);
                int nextRemovePosition = removePositionList.get(k);
                if (nextRemovePosition - tempPosition == 1) {
                    tempRemoveNum++;
                } else {
                    break;
                }
            }
            for (int k = addIndex+1; k < addPositionList.size(); k++) {
                int tempPosition = addPositionList.get(k-1);
                int nextAddPosition = addPositionList.get(k);
                if (nextAddPosition - tempPosition == 1) {
                    tempAddNum++;
                } else {
                    break;
                }
            }
        }
        return tempAddNum - tempRemoveNum;
    }

    private class AddLineRange {
        final List<List<Integer>> lineRangeList = new ArrayList<>();
        int totalAddNum = 0;
        int totalRemoveNum = 0;
        final List<Integer> removePositionList;
        final List<Integer> addPositionList;
        final int removePositionListSize;
        final int addPositionListSize;
        final List<Integer> newPositionList = new ArrayList<>();

        public AddLineRange(List<Integer> removePositionList,
                            List<Integer> addPositionList) {
            this.removePositionList = removePositionList;
            this.addPositionList = addPositionList;
            this. removePositionListSize = removePositionList.size();
            this.addPositionListSize = addPositionList.size();
        }

        private boolean initChangeFlag(int addIndex, int removeIndex) {
            boolean changeFlag;
            if (removePositionListSize == 0) {
                changeFlag = true;
            }
            else {
                changeFlag = addPositionList.get(addIndex) < removePositionList.get(removeIndex);
            }
            return changeFlag;
        }

        private boolean reverseFlag(boolean flag) {
            return !flag;
        }

        private List<Integer> dealAddContinuousPosition(int addIndex) {
            int tempNum=0;
            int index=0;
            for (int k = addIndex+1; k < addPositionList.size(); k++) {
                int tempPosition = addPositionList.get(k-1);
                int nextAddPosition = addPositionList.get(k);
                if (nextAddPosition - tempPosition == 1) {
                    newPositionList.add(nextAddPosition);
                    tempNum++;
                    index++;
                } else {
                    break;
                }
            }
            List<Integer> result = new ArrayList<>();
            result.add(tempNum);
            result.add(index);
            return result;
        }

        public List<List<Integer>> getAddLineRange() {
            if (add && addPositionListSize != 0) {
                final List<Integer> newPositionList = new ArrayList<>();
                int addIndex = 0;
                int removeIndex = 0;
                boolean changeFlag = initChangeFlag(addIndex, removeIndex);
                while (addIndex < addPositionListSize) {
                    if (changeFlag || removeIndex >= removePositionListSize) {
                        changeFlag = reverseFlag(changeFlag);
                        int position = addPositionList.get(addIndex);
                        int tempAddNum = 0;
                        int tempRemoveNum = 0;
                        int matchingRemovePositon = position - totalAddNum + totalRemoveNum;
                        if (!removePositionList.contains(matchingRemovePositon)) {
                            newPositionList.add(position);
                            tempAddNum++;
                            List<Integer> addResult = dealContinuousPosition(addIndex);
                            tempAddNum += addResult.get(0);
                            addIndex += addResult.get(1);
                        }
                        else {
                            boolean flag = removePositionList.indexOf(matchingRemovePositon) >= removeIndex;
                            if (flag) {
                                removeIndex++;
                            }
                            for (int k = removePositionList.indexOf(matchingRemovePositon) + 1;
                                 k < removePositionList.size(); k++) {
                                int tempPosition = removePositionList.get(k-1);
                                int nextRemovePosition = removePositionList.get(k);
                                if (nextRemovePosition - tempPosition == 1) {
                                    tempRemoveNum++;
                                    if (flag) {
                                        removeIndex++;
                                    }
                                } else {
                                    break;
                                }
                            }
                            for (int k = addIndex+1; k < addPositionList.size(); k++) {
                                int tempPosition = addPositionList.get(k-1);
                                int nextAddPosition = addPositionList.get(k);
                                if (nextAddPosition - tempPosition == 1) {
                                    totalAddNum++;
                                    addIndex++;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<List<Integer>> getLineRange(Patch<String> patch) {
        final List<List<Integer>> lineRangeList = new ArrayList<>();
        int totalAddNum = 0;
        int totalRemoveNum = 0;
        for (int i = 0; i < patch.getDeltas().size(); i++) {
            final List<Integer> removePositionList = getRemovePositionList(patch, i);
            final List<Integer> addPositionList = getAddPositionList(patch, i);
            if (add && addPositionList.size() != 0) {
                final List<Integer> newPositionList = new ArrayList<>();
                int addIndex = 0;
                int removeIndex = 0;
                boolean changeFlag;
                if (removePositionList.size() == 0) {
                    changeFlag = true;
                }
                else {
                    changeFlag = addPositionList.get(addIndex) < removePositionList.get(removeIndex);
                }
                final int addPositionListSize = addPositionList.size();
                final int removePositionListSize = removePositionList.size();
                while (addIndex < addPositionListSize) {
                    if (changeFlag || removeIndex >= removePositionListSize) {
                        changeFlag = !changeFlag;
                        int position = addPositionList.get(addIndex);
                        int tempAddNum = 0;
                        int tempRemoveNum = 0;
                        int matchingRemovePositon = position - totalAddNum + totalRemoveNum;
                        if (!removePositionList.contains(matchingRemovePositon)) {
                            newPositionList.add(position);
                            tempAddNum++;
                            for (int k = addIndex+1; k < addPositionList.size(); k++) {
                                int tempPosition = addPositionList.get(k-1);
                                int nextAddPosition = addPositionList.get(k);
                                if (nextAddPosition - tempPosition == 1) {
                                    newPositionList.add(nextAddPosition);
                                    tempAddNum++;
                                    addIndex++;
                                } else {
                                    break;
                                }
                            }
                        } else {
                            boolean flag = removePositionList.indexOf(matchingRemovePositon) >= removeIndex;
                            if (flag) {
                                removeIndex++;
                            }
                            for (int k = removePositionList.indexOf(matchingRemovePositon) + 1;
                                 k < removePositionList.size(); k++) {
                                int tempPosition = removePositionList.get(k-1);
                                int nextRemovePosition = removePositionList.get(k);
                                if (nextRemovePosition - tempPosition == 1) {
                                    tempRemoveNum++;
                                    if (flag) {
                                        removeIndex++;
                                    }
                                } else {
                                    break;
                                }
                            }
                            for (int k = addIndex+1; k < addPositionList.size(); k++) {
                                int tempPosition = addPositionList.get(k-1);
                                int nextAddPosition = addPositionList.get(k);
                                if (nextAddPosition - tempPosition == 1) {
                                    totalAddNum++;
                                    addIndex++;
                                } else {
                                    break;
                                }
                            }
                        }
                        final int diffNum = tempRemoveNum - tempAddNum;
                        if (diffNum > 0) {
                            totalRemoveNum += diffNum;
                        }
                        totalAddNum += tempAddNum;

                        addIndex++;
                    }
                    else {
                        int position = removePositionList.get(removeIndex);
                        int tempRemoveNum = 0;
                        int matchingAddPositon = position + totalAddNum - totalRemoveNum;
                        if (!addPositionList.contains(matchingAddPositon)) {
                            tempRemoveNum++;
                            for (int k = addIndex+1; k < removePositionList.size(); k++) {
                                int tempPosition = removePositionList.get(k-1);
                                int nextRemovePosition = removePositionList.get(k);
                                if (nextRemovePosition - tempPosition == 1) {
                                    tempRemoveNum++;
                                } else {
                                    break;
                                }
                            }
                        } else {
                            for (int k = removeIndex + 1;
                                 k < removePositionList.size(); k++) {
                                int tempPosition = removePositionList.get(k-1);
                                int nextRemovePosition = removePositionList.get(k);
                                if (nextRemovePosition - tempPosition == 1) {
                                    removeIndex++;
                                } else {
                                    break;
                                }
                            }
                        }
                        totalRemoveNum += tempRemoveNum;
                        removeIndex++;
                        if (removeIndex >= removePositionList.size()) {
                            changeFlag = !changeFlag;
                        } else if (removePositionList.get(removeIndex) >= addPositionList.get(addIndex)) {
                            changeFlag = !changeFlag;
                        }
                        else if (removePositionList.get(removeIndex) < addPositionList.get(addIndex)) {
                            int testposition = removePositionList.get(removeIndex);
                            int testmatchingAddPositon = testposition + totalAddNum - totalRemoveNum
                                    + dealAddPosition(addPositionList, removePositionList,
                                    addIndex, totalAddNum, totalRemoveNum);
                            if (addPositionList.contains(testmatchingAddPositon)) {
                                changeFlag = !changeFlag;
                            }
                        }
                    }
                }
                lineRangeList.add(newPositionList);
            }
            else {
                lineRangeList.add(addPositionList);
            }

        }
        return lineRangeList;
    }

    @Override
    public Set<String> getExternalResourceLocations() {
        return Collections.singleton(file);
    }

}

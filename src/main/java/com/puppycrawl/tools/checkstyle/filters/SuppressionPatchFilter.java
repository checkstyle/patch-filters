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
import java.util.Arrays;
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
    private String strategy = "all";

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
     * @param strategy tells if only consider added lines is add, should be added or changed.
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
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

    private List<List<Integer>> getLineRange(Patch<String> patch) {
        final List<List<Integer>> lineRangeList = new ArrayList<>();
        final TotalNumber totalNumber = new TotalNumber();
        for (int i = 0; i < patch.getDeltas().size(); i++) {
            final List<Integer> removePositionList = getRemovePositionList(patch, i);
            final List<Integer> addPositionList = getAddPositionList(patch, i);
            final int addPositionListSize = addPositionList.size();
            if ("added".equals(strategy) && addPositionListSize != 0) {
                final AddLineRange addLineRange =
                        new AddLineRange(removePositionList, addPositionList, totalNumber);
                addLineRange.getAddedLineRange();
                lineRangeList.add(addLineRange.newPositionList);
            }
            else {
                lineRangeList.add(addPositionList);
            }
        }
        return lineRangeList;
    }

    private List<Integer> getRemovePositionList(Patch<String> patch, int index) {
        final Chunk originChunk = patch.getDeltas().get(index).getSource();
        return originChunk.getChangePosition();
    }

    private List<Integer> getAddPositionList(Patch<String> patch, int index) {
        final Chunk targetChunk = patch.getDeltas().get(index).getTarget();
        return targetChunk.getChangePosition();
    }

    @Override
    public Set<String> getExternalResourceLocations() {
        return Collections.singleton(file);
    }

    /**
     * To get real added lines' positions.
     */
    private class AddLineRange {

        /**
         * It contains current Delta's the real added lines' positions.
         */
        private final List<Integer> newPositionList = new ArrayList<>();

        /**
         * It contains source and target files' offset numbers.
         */
        private final TotalNumber totalNumber;

        /**
         * It represents current Delta's removePositionList.
         */
        private final List<Integer> removePositionList;

        /**
         * It represents current Delta's addPositionList.
         */
        private final List<Integer> addPositionList;

        /**
         * It represents size of removePositionList.
         */
        private final int removePositionListSize;

        /**
         * It represents size of addPositionList.
         */
        private final int addPositionListSize;

        /**
         * It represents current index in removePositionList.
         */
        private int removeIndex;

        /**
         * It represents current index in addPositionList.
         */
        private int addIndex;

        /**
         * It decides which position list to process,
         * true is addPositionList,
         * false is removePositionList.
         */
        private boolean changeFlag;

        AddLineRange(List<Integer> removePositionList,
                            List<Integer> addPositionList, TotalNumber totalNumber) {
            this.totalNumber = totalNumber;
            this.removePositionList = removePositionList;
            this.addPositionList = addPositionList;
            this.removePositionListSize = removePositionList.size();
            this.addPositionListSize = addPositionList.size();
        }

        private void initChangeFlag() {
            if (removePositionListSize == 0) {
                changeFlag = true;
            }
            else {
                changeFlag = addPositionList.get(addIndex)
                        < removePositionList.get(removeIndex);
            }
        }

        public void getAddedLineRange() {
            initChangeFlag();
            int totalAddNum = totalNumber.getTotalAddNum();
            int totalRemoveNum = totalNumber.getTotalRemoveNum();
            while (addIndex < addPositionListSize) {
                if (changeFlag || removeIndex >= removePositionListSize) {
                    final List<Integer> updateTotalNumList =
                            processAddPositionList(totalAddNum, totalRemoveNum);
                    totalAddNum = updateTotalNumList.get(0);
                    totalRemoveNum = updateTotalNumList.get(1);
                }
                else {
                    final List<Integer> updateTotalNumList =
                            processRemovePositionList(totalAddNum, totalRemoveNum);
                    totalAddNum = updateTotalNumList.get(0);
                    totalRemoveNum = updateTotalNumList.get(1);
                }
            }
            totalNumber.setTotalAddNum(totalAddNum);
            totalNumber.setTotalRemoveNum(totalRemoveNum);
        }

        private List<Integer> processAddPositionList(int totalAddNum, int totalRemoveNum) {
            int tempTotalAddNum = totalAddNum;
            int tempTotalRemoveNum = totalRemoveNum;
            final int position = addPositionList.get(addIndex);
            final int matchingRemovePositon = position - tempTotalAddNum
                    + tempTotalRemoveNum;
            // Judge if there are matching line in the remove position list
            // if not, then it is added line
            if (!removePositionList.contains(matchingRemovePositon)) {
                newPositionList.add(position);
                tempTotalAddNum++;
                final List<Integer> increaseAddList =
                        dealContinuousPosition(addIndex, addPositionList,
                                true, newPositionList);
                tempTotalAddNum += increaseAddList.get(0);
                addIndex += increaseAddList.get(1);
            }
            else {
                final int matchingRemovePositionIndex =
                        removePositionList.indexOf(matchingRemovePositon);
                final boolean flag =
                        matchingRemovePositionIndex
                                >= removeIndex;
                if (flag) {
                    removeIndex++;
                }
                final List<Integer> increaseRemoveList =
                        dealContinuousPosition(matchingRemovePositionIndex,
                                removePositionList, flag);
                tempTotalRemoveNum += increaseRemoveList.get(0);
                removeIndex += increaseRemoveList.get(1);
                final List<Integer> increaseAddList =
                        dealContinuousPosition(addIndex, addPositionList, true);
                tempTotalAddNum += increaseAddList.get(0);
                addIndex += increaseAddList.get(1);
            }
            addIndex++;
            if (addIndex < addPositionListSize
                    && removeIndex < removePositionListSize
                    && addPositionList.get(addIndex)
                    < removePositionList.get(removeIndex)) {
                // test if current addIndex is a real added fragment
                final int testposition = addPositionList.get(addIndex);
                final int testmatchingRemovePositon = testposition
                        - tempTotalAddNum + tempTotalRemoveNum;
                if (removePositionList.contains(testmatchingRemovePositon)) {
                    changeFlag = !changeFlag;
                }
            }
            else {
                changeFlag = !changeFlag;
            }
            return Arrays.asList(tempTotalAddNum, tempTotalRemoveNum);
        }

        private List<Integer> processRemovePositionList(int totalAddNum, int totalRemoveNum) {
            final int position = removePositionList.get(removeIndex);
            int tempTotalRemoveNum = totalRemoveNum;
            final int matchingAddPositon = position + totalAddNum - tempTotalRemoveNum;
            // Judge if there are matching line in the add position list
            // if not, then it is just removed line
            if (!addPositionList.contains(matchingAddPositon)) {
                tempTotalRemoveNum++;
                final int increaseRemoveNum = dealContinuousPosition(removeIndex,
                        removePositionList).get(0);
                tempTotalRemoveNum += increaseRemoveNum;
                removeIndex++;
                // Judge if change flag to deal with add position list
                // this judgment is for a certain situation that two or more
                // removed fragment are continuous, this situation can be found
                // in testAddOptionThree
                if (removeIndex >= removePositionListSize) {
                    changeFlag = !changeFlag;
                }
                else if (removePositionList.get(removeIndex)
                        >= addPositionList.get(addIndex)) {
                    changeFlag = !changeFlag;
                }
                else if (removePositionList.get(removeIndex)
                        < addPositionList.get(addIndex)) {
                    // test if current removeIndex is a removed fragment
                    final int testposition = removePositionList.get(removeIndex);
                    final int testmatchingAddPositon = testposition
                            + totalAddNum - tempTotalRemoveNum;
                    if (addPositionList.contains(testmatchingAddPositon)) {
                        changeFlag = !changeFlag;
                    }
                }
            }
            else {
                final int increaseRemoveIndex = dealContinuousPosition(removeIndex,
                        removePositionList).get(0);
                removeIndex += increaseRemoveIndex;
                removeIndex++;
                changeFlag = !changeFlag;
            }
            return Arrays.asList(totalAddNum, tempTotalRemoveNum);
        }

        private List<Integer> dealContinuousPosition(int startIndex,
                                                     List<Integer> positionList) {
            return dealContinuousPosition(startIndex, positionList, false);
        }

        private List<Integer> dealContinuousPosition(int startIndex,
                                                     List<Integer> positionList,
                                                     boolean updateIndex) {
            return dealContinuousPosition(startIndex, positionList, updateIndex, null);
        }

        private List<Integer> dealContinuousPosition(int startIndex,
                                                     List<Integer> positionList,
                                                     boolean updateIndex,
                                                     List<Integer> newAddPositionList) {
            int tempNum = 0;
            int tempIndex = 0;
            for (int k = startIndex + 1; k < positionList.size(); k++) {
                final int tempPosition = positionList.get(k - 1);
                final int nextAddPosition = positionList.get(k);
                if (nextAddPosition - tempPosition == 1) {
                    tempNum++;
                    if (updateIndex) {
                        tempIndex++;
                    }
                    if (newAddPositionList != null) {
                        newAddPositionList.add(nextAddPosition);
                    }
                }
                else {
                    break;
                }
            }
            return Arrays.asList(tempNum, tempIndex);
        }
    }

    /**
     * TotalNumber Bean.
     */
    private class TotalNumber {
        /**
         * Represents the total offset num in target file.
         */
        private int totalAddNum;

        /**
         * Represents the total offset num in source file.
         */
        private int totalRemoveNum;

        public int getTotalAddNum() {
            return totalAddNum;
        }

        public void setTotalAddNum(int totalAddNum) {
            this.totalAddNum = totalAddNum;
        }

        public int getTotalRemoveNum() {
            return totalRemoveNum;
        }

        public void setTotalRemoveNum(int totalRemoveNum) {
            this.totalRemoveNum = totalRemoveNum;
        }
    }
}

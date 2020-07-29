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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

public class SuppressionPatchXpathFilterTest extends AbstractPatchFilterEvaluationTest {

    @Override
    protected String getPatchFileLocation() {
        return "src/test/resources/com/puppycrawl/tools"
                + "/checkstyle/filters/suppressionpatchxpathfilter/";
    }

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/filters/suppressionpatchxpathfilter/";
    }

    @Test
    public void testShortName() throws Exception {
        testByConfig("ShortName/supportContextStrategyChecks/defaultContextConfig.xml");
        testByConfig("ShortName/checkNamesForContextStrategyByTokenOrParentSet/"
                + "defaultContextConfig.xml");
        testByConfig("ShortName/neverSuppressedChecks/defaultContextConfig.xml");
    }

    @Test
    public void testNonExistentPatchFileWithFalseOptional() throws Exception {
        try {
            testByConfig("Optional/false/defaultContextConfig.xml");
        }
        catch (CheckstyleException ex) {
            assertEquals("cannot initialize module TreeWalker - "
                            + "cannot initialize module "
                            + "com.puppycrawl.tools.checkstyle.filters.SuppressionPatchXpathFilter "
                            + "- an error occurred when load patch file", ex.getMessage(),
                    "Invalid error message");
        }
    }

    @Test
    public void testNonExistentPatchFileWithTrueOptional() throws Exception {
        testByConfig("Optional/true/defaultContextConfig.xml");
    }

    @Test
    public void testNeverSuppressedChecks() throws Exception {
        testByConfig("neversuppressedchecks/CovariantEquals/"
                + "checkID/context/defaultContextConfig.xml");
        testByConfig("neversuppressedchecks/CovariantEquals/"
                + "checkID/newline/defaultContextConfig.xml");
        testByConfig("neversuppressedchecks/CovariantEquals"
                        + "/checkID/patchedline/defaultContextConfig.xml");
        testByConfig("neversuppressedchecks/CovariantEquals/"
                + "checkShortName/context/defaultContextConfig.xml");
        testByConfig("neversuppressedchecks/CovariantEquals/"
                + "checkShortName/newline/defaultContextConfig.xml");
        testByConfig("neversuppressedchecks/CovariantEquals/"
                + "checkShortName/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testWithSuppressionPatchFilter() throws Exception {
        testByConfig("WithSuppressionPatchFilter/contextSituation/defaultContextConfig.xml");
    }

    @Test
    public void testVariableDeclarationUsageDistance() throws Exception {
        testByConfig("VariableDeclarationUsageDistance/newline/defaultContextConfig.xml");
        testByConfig("VariableDeclarationUsageDistance/patchedline/defaultContextConfig.xml");
        testByConfig("VariableDeclarationUsageDistance/context/defaultContextConfig.xml");
    }

    @Test
    public void testIllegalToken() throws Exception {
        testByConfig(
                "IllegalToken/newline/defaultContextConfig.xml");
        testByConfig(
                "IllegalToken/newline/zeroContextConfig.xml");

        testByConfig(
                "IllegalToken/patchedline/defaultContextConfig.xml");
        testByConfig(
                "IllegalToken/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testMethodLength() throws Exception {
        testByConfig("MethodLength/case1/newline/defaultContextConfig.xml");
        testByConfig("MethodLength/case1/patchedline/defaultContextConfig.xml");
        testByConfig("MethodLength/case1/context/defaultContextConfig.xml");

        testByConfig("MethodLength/case2/newline/defaultContextConfig.xml");
        testByConfig("MethodLength/case2/patchedline/defaultContextConfig.xml");
        testByConfig("MethodLength/case2/context/defaultContextConfig.xml");
    }

    @Test
    public void testMethodName() throws Exception {
        testByConfig(
                "MethodName/newline/defaultContextConfig.xml");
        testByConfig(
                "MethodName/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testMethodCount() throws Exception {
        testByConfig(
                "sizes/MethodCount/newline/defaultContextConfig.xml");
        testByConfig(
                "sizes/MethodCount/patchedline/defaultContextConfig.xml");
        testByConfig(
                "sizes/MethodCount/context/defaultContextConfig.xml");
    }

    @Test
    public void testOneTopLevelClass() throws Exception {
        testByConfig("OneTopLevelClass/newline/defaultContextConfig.xml");
        testByConfig("OneTopLevelClass/patchedline/defaultContextConfig.xml");
        testByConfig("OneTopLevelClass/context/defaultContextConfig.xml");
    }

    @Test
    public void testFinalClass() throws Exception {
        testByConfig("FinalClass/newline/defaultContextConfig.xml");
        testByConfig("FinalClass/patchedline/defaultContextConfig.xml");
        testByConfig("FinalClass/context/defaultContextConfig.xml");
    }

    @Test
    public void testJavadocParagraph() throws Exception {
        testByConfig("JavadocParagraph/newline/defaultContextConfig.xml");
        testByConfig("JavadocParagraph/patchedline/defaultContextConfig.xml");
        testByConfig("JavadocParagraph/context/defaultContextConfig.xml");
    }

    @Test
    public void testMissingCtor() throws Exception {
        testByConfig("MissingCtor/newline/defaultContextConfig.xml");
        testByConfig("MissingCtor/patchedline/defaultContextConfig.xml");
        testByConfig("MissingCtor/context/defaultContextConfig.xml");
    }

    @Test
    public void testMissingJavadocType() throws Exception {
        testByConfig("MissingJavadocType/newline/defaultContextConfig.xml");
        testByConfig("MissingJavadocType/patchedline/defaultContextConfig.xml");
        testByConfig("MissingJavadocType/context/defaultContextConfig.xml");
    }

    @Test
    public void testInnerTypeLast() throws Exception {
        testByConfig("InnerTypeLast/newline/defaultContextConfig.xml");
        testByConfig("InnerTypeLast/patchedline/defaultContextConfig.xml");
        testByConfig("InnerTypeLast/context/defaultContextConfig.xml");
    }

    @Test
    public void testAtclauseOrder() throws Exception {
        testByConfig("AtclauseOrder/newline/defaultContextConfig.xml");
        testByConfig("AtclauseOrder/patchedline/defaultContextConfig.xml");
        testByConfig("AtclauseOrder/context/defaultContextConfig.xml");
    }

    @Test
    public void testMagicNumber() throws Exception {
        testByConfig(
                "MagicNumber/newline/defaultContextConfig.xml");
        testByConfig(
                "MagicNumber/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testInterfaceIsType() throws Exception {
        testByConfig("InterfaceIsType/newline/defaultContextConfig.xml");
        testByConfig("InterfaceIsType/patchedline/defaultContextConfig.xml");
        testByConfig("InterfaceIsType/context/defaultContextConfig.xml");
    }

    @Test
    public void testHideUtilityClassConstructor() throws Exception {
        testByConfig("HideUtilityClassConstructor/newline/defaultContextConfig.xml");
        testByConfig("HideUtilityClassConstructor/patchedline/defaultContextConfig.xml");
        testByConfig("HideUtilityClassConstructor/context/defaultContextConfig.xml");
    }

    @Test
    public void testReturnCount() throws Exception {
        testByConfig(
                "ReturnCount/newline/defaultContextConfig.xml");
        testByConfig(
                "ReturnCount/patchedline/defaultContextConfig.xml");
        testByConfig(
                "ReturnCount/context/defaultContextConfig.xml");
    }

    @Test
    public void testEmptyLineSeparator() throws Exception {
        testByConfig("EmptyLineSeparator/newline/defaultContextConfig.xml");
        testByConfig("EmptyLineSeparator/patchedline/defaultContextConfig.xml");
        testByConfig("EmptyLineSeparator/context/defaultContextConfig.xml");
    }

    @Test
    @Ignore("JavadocMethod should have a violation, but not. It should be solved by"
            + "context strategy.")
    public void testJavadocMethod() throws Exception {
        testByConfig(
                "JavadocMethod/newline/defaultContextConfig.xml");
        testByConfig(
                "JavadocMethod/patchedline/defaultContextConfig.xml");
    }

    @Test
    @Ignore("until https://github.com/checkstyle/patch-filters/issues/152")
    public void testDefaultComesLast() throws Exception {
        testByConfig("DefaultComesLast/newline/defaultContextConfig.xml");
        testByConfig("DefaultComesLast/patchedline/defaultContextConfig.xml");
        testByConfig("DefaultComesLast/context/defaultContextConfig.xml");
    }

    @Test
    public void testRegexp() throws Exception {
        testByConfig("Regexp/newline/defaultContextConfig.xml");
        testByConfig("Regexp/patchedline/defaultContextConfig.xml");
        testByConfig("Regexp/context/defaultContextConfig.xml");
    }

    @Test
    public void testAvoidNestedBlocks() throws Exception {
        testByConfig(
                "AvoidNestedBlocks/newline/defaultContextConfig.xml");
        testByConfig(
                "AvoidNestedBlocks/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testThrowsCount() throws Exception {
        testByConfig("ThrowsCount/newline/defaultContextConfig.xml");
        testByConfig("ThrowsCount/patchedline/defaultContextConfig.xml");
        testByConfig("ThrowsCount/context/defaultContextConfig.xml");
    }

    @Test
    public void testMemberName() throws Exception {
        testByConfig(
                "MemberName/newline/defaultContextConfig.xml");
        testByConfig(
                "MemberName/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testParameterNumber() throws Exception {
        testByConfig("ParameterNumber/newline/defaultContextConfig.xml");
        testByConfig("ParameterNumber/patchedline/defaultContextConfig.xml");
        testByConfig("ParameterNumber/context/defaultContextConfig.xml");
    }

    @Test
    public void testRedundantImport() throws Exception {
        testByConfig("RedundantImport/newline/defaultContextConfig.xml");
        testByConfig("RedundantImport/patchedline/defaultContextConfig.xml");
        testByConfig("RedundantImport/context/defaultContextConfig.xml");
    }

    @Test
    public void testCovariantEquals() throws Exception {
        testByConfig("CovariantEquals/newline/defaultContextConfig.xml");
        testByConfig("CovariantEquals/patchedline/defaultContextConfig.xml");
        testByConfig("CovariantEquals/context/defaultContextConfig.xml");
    }

    @Test
    public void testAnonInnerLength() throws Exception {
        testByConfig(
                "sizes/AnonInnerLength/newline/defaultContextConfig.xml");
        testByConfig(
                "sizes/AnonInnerLength/patchedline/defaultContextConfig.xml");
        testByConfig(
                "sizes/AnonInnerLength/context/defaultContextConfig.xml");
    }

    @Test
    public void testExecutableStatementCount() throws Exception {
        testByConfig(
                "sizes/ExecutableStatementCount/newline/defaultContextConfig.xml");
        testByConfig(
                "sizes/ExecutableStatementCount/patchedline/defaultContextConfig.xml");
        testByConfig(
                "sizes/ExecutableStatementCount/context/defaultContextConfig.xml");
    }

    @Test
    public void testNestedIfDepth() throws Exception {
        testByConfig("NestedIfDepth/newline/defaultContextConfig.xml");
        testByConfig("NestedIfDepth/patchedline/defaultContextConfig.xml");
        testByConfig("NestedIfDepth/context/defaultContextConfig.xml");
    }

    @Test
    public void testNoCodeInFile() throws Exception {
        testByConfig("NoCodeInFile/newline/defaultContextConfig.xml");
        testByConfig("NoCodeInFile/patchedline/defaultContextConfig.xml");
        testByConfig("NoCodeInFile/context/defaultContextConfig.xml");
    }

    @Test
    public void testDesignForExtension() throws Exception {
        testByConfig("DesignForExtension/newline/defaultContextConfig.xml");
        testByConfig("DesignForExtension/patchedline/defaultContextConfig.xml");
        testByConfig("DesignForExtension/context/defaultContextConfig.xml");
    }

    @Test
    public void testCyclomaticComplexity() throws Exception {
        testByConfig("CyclomaticComplexity/newline/defaultContextConfig.xml");
        testByConfig("CyclomaticComplexity/patchedline/defaultContextConfig.xml");
        testByConfig("CyclomaticComplexity/context/defaultContextConfig.xml");
    }

    @Test
    public void testClassFanOutComplexity() throws Exception {
        testByConfig("ClassFanOutComplexity/newline/defaultContextConfig.xml");
        testByConfig("ClassFanOutComplexity/patchedline/defaultContextConfig.xml");
        testByConfig("ClassFanOutComplexity/context/defaultContextConfig.xml");
    }

    @Test
    public void testOuterTypeNumber() throws Exception {
        testByConfig("sizes/OuterTypeNumber/newline/defaultContextConfig.xml");
        testByConfig("sizes/OuterTypeNumber/patchedline/defaultContextConfig.xml");
        testByConfig("sizes/OuterTypeNumber/context/defaultContextConfig.xml");
    }

    @Test
    public void testClassDataAbstractionCoupling() throws Exception {
        testByConfig("ClassDataAbstractionCoupling/newline/defaultContextConfig.xml");
        testByConfig("ClassDataAbstractionCoupling/patchedline/defaultContextConfig.xml");
        testByConfig("ClassDataAbstractionCoupling/context/defaultContextConfig.xml");
    }

    @Test
    public void testOuterTypeFilename() throws Exception {
        testByConfig("OuterTypeFilename/newline/defaultContextConfig.xml");
        testByConfig("OuterTypeFilename/patchedline/defaultContextConfig.xml");
        testByConfig("OuterTypeFilename/context/defaultContextConfig.xml");
    }

    @Test
    public void testSuperFinalize() throws Exception {
        testByConfig("SuperFinalize/newline/defaultContextConfig.xml");
        testByConfig("SuperFinalize/patchedline/defaultContextConfig.xml");
        testByConfig("SuperFinalize/context/defaultContextConfig.xml");
    }

    @Test
    public void testBooleanExpressionComplexity() throws Exception {
        testByConfig("BooleanExpressionComplexity/newline/defaultContextConfig.xml");
        testByConfig("BooleanExpressionComplexity/patchedline/defaultContextConfig.xml");
        testByConfig("BooleanExpressionComplexity/context/defaultContextConfig.xml");
    }

    @Test
    public void testEmptyCatchBlock() throws Exception {
        testByConfig("EmptyCatchBlock/case1/newline/defaultContextConfig.xml");
        testByConfig("EmptyCatchBlock/case1/patchedline/defaultContextConfig.xml");
        testByConfig("EmptyCatchBlock/case1/context/defaultContextConfig.xml");

        testByConfig("EmptyCatchBlock/case2/newline/defaultContextConfig.xml");
        testByConfig("EmptyCatchBlock/case2/patchedline/defaultContextConfig.xml");
        testByConfig("EmptyCatchBlock/case2/context/defaultContextConfig.xml");
    }

    @Test
    public void testCustomImportOrder() throws Exception {
        testByConfig("CustomImportOrder/newline/defaultContextConfig.xml");
        testByConfig("CustomImportOrder/patchedline/defaultContextConfig.xml");
        testByConfig("CustomImportOrder/context/defaultContextConfig.xml");
    }

    @Test
    public void testOverloadMethodsDeclarationOrder() throws Exception {
        testByConfig("OverloadMethodsDeclarationOrder/newline/defaultContextConfig.xml");
        testByConfig("OverloadMethodsDeclarationOrder/patchedline/defaultContextConfig.xml");
        testByConfig("OverloadMethodsDeclarationOrder/context/defaultContextConfig.xml");
    }

    @Test
    public void testEmptyStatement() throws Exception {
        testByConfig("EmptyStatement/newline/defaultContextConfig.xml");
        testByConfig("EmptyStatement/patchedline/defaultContextConfig.xml");
        testByConfig("EmptyStatement/context/defaultContextConfig.xml");
    }

    @Test
    public void testJavaNcss() throws Exception {
        testByConfig("JavaNCSS/newline/defaultContextConfig.xml");
        testByConfig("JavaNCSS/patchedline/defaultContextConfig.xml");
        testByConfig("JavaNCSS/context/defaultContextConfig.xml");
    }

    @Test
    public void testMissingSwitchDefault() throws Exception {
        testByConfig("MissingSwitchDefault/newline/defaultContextConfig.xml");
        testByConfig("MissingSwitchDefault/patchedline/defaultContextConfig.xml");
        testByConfig("MissingSwitchDefault/context/defaultContextConfig.xml");
    }

    @Test
    public void testNpathComplexity() throws Exception {
        testByConfig("NPathComplexity/newline/defaultContextConfig.xml");
        testByConfig("NPathComplexity/patchedline/defaultContextConfig.xml");
        testByConfig("NPathComplexity/context/defaultContextConfig.xml");
    }

    @Test
    public void testFallThrough() throws Exception {
        testByConfig("FallThrough/case1/newline/defaultContextConfig.xml");
        testByConfig("FallThrough/case1/patchedline/defaultContextConfig.xml");
        testByConfig("FallThrough/case1/context/defaultContextConfig.xml");

        testByConfig("FallThrough/case2/newline/defaultContextConfig.xml");
        testByConfig("FallThrough/case2/patchedline/defaultContextConfig.xml");
        testByConfig("FallThrough/case2/context/defaultContextConfig.xml");
    }

    @Test
    public void testNestedForDepth() throws Exception {
        testByConfig("NestedForDepth/newline/defaultContextConfig.xml");
        testByConfig("NestedForDepth/patchedline/defaultContextConfig.xml");
        testByConfig("NestedForDepth/context/defaultContextConfig.xml");
    }

    @Test
    public void testUnusedImports() throws Exception {
        testByConfig("UnusedImports/newline/defaultContextConfig.xml");
        testByConfig("UnusedImports/patchedline/defaultContextConfig.xml");
        testByConfig("UnusedImports/context/defaultContextConfig.xml");
    }

    @Test
    public void testEmptyBlock() throws Exception {
        testByConfig("EmptyBlock/newline/defaultContextConfig.xml");
        testByConfig("EmptyBlock/patchedline/defaultContextConfig.xml");
        testByConfig("EmptyBlock/context/defaultContextConfig.xml");
    }

    @Test
    public void testDeclarationOrder() throws Exception {
        testByConfig("DeclarationOrder/newline/defaultContextConfig.xml");
        testByConfig("DeclarationOrder/patchedline/defaultContextConfig.xml");
        testByConfig("DeclarationOrder/context/defaultContextConfig.xml");
    }

    @Test
    public void testImportOrder() throws Exception {
        testByConfig("ImportOrder/newline/defaultContextConfig.xml");
        testByConfig("ImportOrder/patchedline/defaultContextConfig.xml");
        testByConfig("ImportOrder/context/defaultContextConfig.xml");
    }

    @Test
    public void testNestedTryDepth() throws Exception {
        testByConfig("NestedTryDepth/newline/defaultContextConfig.xml");
        testByConfig("NestedTryDepth/patchedline/defaultContextConfig.xml");
        testByConfig("NestedTryDepth/context/defaultContextConfig.xml");
    }

    @Test
    public void testSimplifyBooleanReturn() throws Exception {
        testByConfig("SimplifyBooleanReturn/newline/defaultContextConfig.xml");
        testByConfig("SimplifyBooleanReturn/patchedline/defaultContextConfig.xml");
        testByConfig("SimplifyBooleanReturn/context/defaultContextConfig.xml");
    }

    @Test
    public void testSuperClone() throws Exception {
        testByConfig("SuperClone/newline/defaultContextConfig.xml");
        testByConfig("SuperClone/patchedline/defaultContextConfig.xml");
        testByConfig("SuperClone/context/defaultContextConfig.xml");
    }

    @Test
    public void testPackageDeclaration() throws Exception {
        testByConfig("PackageDeclaration/newline/defaultContextConfig.xml");
        testByConfig("PackageDeclaration/patchedline/defaultContextConfig.xml");
        testByConfig("PackageDeclaration/context/defaultContextConfig.xml");
    }

    @Test
    public void testUnnecessarySemicolonInEnumeration() throws Exception {
        testByConfig("UnnecessarySemicolonInEnumeration/newline/defaultContextConfig.xml");
        testByConfig("UnnecessarySemicolonInEnumeration/patchedline/defaultContextConfig.xml");
        testByConfig("UnnecessarySemicolonInEnumeration/context/defaultContextConfig.xml");
    }
}

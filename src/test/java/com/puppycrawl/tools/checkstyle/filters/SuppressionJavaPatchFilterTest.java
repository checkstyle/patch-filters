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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

public class SuppressionJavaPatchFilterTest extends AbstractPatchFilterEvaluationTest {

    @Override
    protected String getPatchFileLocation() {
        return "src/test/resources/com/puppycrawl/tools"
                + "/checkstyle/filters/suppressionjavapatchfilter/";
    }

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/filters/suppressionjavapatchfilter/";
    }

    @Test
    public void testDefaultStrategy() throws Exception {
        testByConfig("DefaultStrategy/defaultContextConfig.xml");
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
                            + "com.puppycrawl.tools.checkstyle.filters.SuppressionJavaPatchFilter "
                            + "- an error occurred when loading patch file "
                            + getPatchFileLocation() + "Optional/false//defaultContext.patch",
                            ex.getMessage(),
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
    public void testMissingDeprecated() throws Exception {
        testByConfig("annotation/MissingDeprecated/newline/defaultContextConfig.xml");
        testByConfig("annotation/MissingDeprecated/patchedline/defaultContextConfig.xml");
        testByConfig("annotation/MissingDeprecated/context/defaultContextConfig.xml");
    }

    @Test
    public void testMissingOverride() throws Exception {
        testByConfig("annotation/MissingOverride/newline/defaultContextConfig.xml");
        testByConfig("annotation/MissingOverride/patchedline/defaultContextConfig.xml");
        testByConfig("annotation/MissingOverride/context/defaultContextConfig.xml");
        testByConfig("annotation/MissingOverride/context/case2/defaultContextConfig.xml");
    }

    @Test
    public void testPackageAnnotation() throws Exception {
        testByConfig("annotation/PackageAnnotation/newline/defaultContextConfig.xml");
        testByConfig("annotation/PackageAnnotation/patchedline/defaultContextConfig.xml");
        testByConfig("annotation/PackageAnnotation/context/defaultContextConfig.xml");
    }

    @Test
    public void testAvoidNestedBlocks() throws Exception {
        testByConfig("blocks/AvoidNestedBlocks/newline/defaultContextConfig.xml");
        testByConfig("blocks/AvoidNestedBlocks/patchedline/defaultContextConfig.xml");
        testByConfig("blocks/AvoidNestedBlocks/context/defaultContextConfig.xml");
    }

    @Test
    public void testEmptyCatchBlock() throws Exception {
        testByConfig("blocks/EmptyCatchBlock/case1/newline/defaultContextConfig.xml");
        testByConfig("blocks/EmptyCatchBlock/case1/patchedline/defaultContextConfig.xml");
        testByConfig("blocks/EmptyCatchBlock/case1/context/defaultContextConfig.xml");

        testByConfig("blocks/EmptyCatchBlock/case2/newline/defaultContextConfig.xml");
        testByConfig("blocks/EmptyCatchBlock/case2/patchedline/defaultContextConfig.xml");
        testByConfig("blocks/EmptyCatchBlock/case2/context/defaultContextConfig.xml");
    }

    @Test
    public void testEmptyBlock() throws Exception {
        testByConfig("blocks/EmptyBlock/newline/defaultContextConfig.xml");
        testByConfig("blocks/EmptyBlock/patchedline/defaultContextConfig.xml");
        testByConfig("blocks/EmptyBlock/context/defaultContextConfig.xml");
    }

    @Test
    public void testRightCurly() throws Exception {
        testByConfig("blocks/RightCurly/newline/defaultContextConfig.xml");
        testByConfig("blocks/RightCurly/patchedline/defaultContextConfig.xml");
        testByConfig("blocks/RightCurly/context/defaultContextConfig.xml");
    }

    @Test
    public void testArrayTrailingComma() throws Exception {
        testByConfig("coding/ArrayTrailingComma/newline/defaultContextConfig.xml");
        testByConfig("coding/ArrayTrailingComma/patchedline/defaultContextConfig.xml");
        testByConfig("coding/ArrayTrailingComma/context/defaultContextConfig.xml");
    }

    @Test
    public void testAvoidDoubleBraceInitialization() throws Exception {
        testByConfig("coding/AvoidDoubleBraceInitialization/newline/defaultContextConfig.xml");
        testByConfig("coding/AvoidDoubleBraceInitialization/patchedline/defaultContextConfig.xml");
        testByConfig("coding/AvoidDoubleBraceInitialization/context/defaultContextConfig.xml");
    }

    @Test
    public void testCovariantEquals() throws Exception {
        testByConfig("coding/CovariantEquals/newline/defaultContextConfig.xml");
        testByConfig("coding/CovariantEquals/patchedline/defaultContextConfig.xml");
        testByConfig("coding/CovariantEquals/context/defaultContextConfig.xml");
    }

    @Test
    public void testDeclarationOrder() throws Exception {
        testByConfig("coding/DeclarationOrder/newline/defaultContextConfig.xml");
        testByConfig("coding/DeclarationOrder/patchedline/defaultContextConfig.xml");
        testByConfig("coding/DeclarationOrder/context/defaultContextConfig.xml");
    }

    @Test
    public void testDefaultComesLast() throws Exception {
        testByConfig("coding/DefaultComesLast/newline/defaultContextConfig.xml");
        testByConfig("coding/DefaultComesLast/patchedline/defaultContextConfig.xml");
        testByConfig("coding/DefaultComesLast/context/defaultContextConfig.xml");
    }

    @Test
    public void testEmptyStatement() throws Exception {
        testByConfig("coding/EmptyStatement/newline/defaultContextConfig.xml");
        testByConfig("coding/EmptyStatement/patchedline/defaultContextConfig.xml");
        testByConfig("coding/EmptyStatement/context/defaultContextConfig.xml");
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        testByConfig("coding/EqualsHashCode/newline/defaultContextConfig.xml");
        testByConfig("coding/EqualsHashCode/patchedline/defaultContextConfig.xml");
        testByConfig("coding/EqualsHashCode/context/defaultContextConfig.xml");
    }

    @Test
    public void testFallThrough() throws Exception {
        testByConfig("coding/FallThrough/case1/newline/defaultContextConfig.xml");
        testByConfig("coding/FallThrough/case1/patchedline/defaultContextConfig.xml");
        testByConfig("coding/FallThrough/case1/context/defaultContextConfig.xml");

        testByConfig("coding/FallThrough/case2/newline/defaultContextConfig.xml");
        testByConfig("coding/FallThrough/case2/patchedline/defaultContextConfig.xml");
        testByConfig("coding/FallThrough/case2/context/defaultContextConfig.xml");
    }

    @Test
    public void testFinalLocalVariable() throws Exception {
        testByConfig("coding/FinalLocalVariable/case1/newline/defaultContextConfig.xml");
        testByConfig("coding/FinalLocalVariable/case1/patchedline/defaultContextConfig.xml");
        testByConfig("coding/FinalLocalVariable/case1/context/defaultContextConfig.xml");

        testByConfig("coding/FinalLocalVariable/case2/newline/defaultContextConfig.xml");
        testByConfig("coding/FinalLocalVariable/case2/patchedline/defaultContextConfig.xml");
        testByConfig("coding/FinalLocalVariable/case2/context/defaultContextConfig.xml");
    }

    @Test
    public void testHiddenField() throws Exception {
        testByConfig("coding/HiddenField/newline/defaultContextConfig.xml");
        testByConfig("coding/HiddenField/patchedline/defaultContextConfig.xml");
        testByConfig("coding/HiddenField/context/defaultContextConfig.xml");
        testByConfig("coding/HiddenField/context/case2/defaultContextConfig.xml");
    }

    @Test
    public void testIllegalToken() throws Exception {
        testByConfig("coding/IllegalToken/newline/defaultContextConfig.xml");
        testByConfig("coding/IllegalToken/newline/zeroContextConfig.xml");

        testByConfig("coding/IllegalToken/patchedline/defaultContextConfig.xml");
        testByConfig("coding/IllegalToken/patchedline/zeroContextConfig.xml");
    }

    @Test
    public void testMagicNumber() throws Exception {
        testByConfig("coding/MagicNumber/newline/defaultContextConfig.xml");
        testByConfig("coding/MagicNumber/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testMissingCtor() throws Exception {
        testByConfig("coding/MissingCtor/newline/defaultContextConfig.xml");
        testByConfig("coding/MissingCtor/patchedline/defaultContextConfig.xml");
        testByConfig("coding/MissingCtor/context/defaultContextConfig.xml");
    }

    @Test
    public void testMissingSwitchDefault() throws Exception {
        testByConfig("coding/MissingSwitchDefault/newline/defaultContextConfig.xml");
        testByConfig("coding/MissingSwitchDefault/patchedline/defaultContextConfig.xml");
        testByConfig("coding/MissingSwitchDefault/context/defaultContextConfig.xml");
    }

    @Test
    public void testNestedIfDepth() throws Exception {
        testByConfig("coding/NestedIfDepth/newline/defaultContextConfig.xml");
        testByConfig("coding/NestedIfDepth/patchedline/defaultContextConfig.xml");
        testByConfig("coding/NestedIfDepth/context/defaultContextConfig.xml");
    }

    @Test
    public void testNestedForDepth() throws Exception {
        testByConfig("coding/NestedForDepth/newline/defaultContextConfig.xml");
        testByConfig("coding/NestedForDepth/patchedline/defaultContextConfig.xml");
        testByConfig("coding/NestedForDepth/context/defaultContextConfig.xml");
    }

    @Test
    public void testNestedTryDepth() throws Exception {
        testByConfig("coding/NestedTryDepth/newline/defaultContextConfig.xml");
        testByConfig("coding/NestedTryDepth/patchedline/defaultContextConfig.xml");
        testByConfig("coding/NestedTryDepth/context/defaultContextConfig.xml");
    }

    @Test
    public void testOverloadMethodsDeclarationOrder() throws Exception {
        testByConfig("coding/OverloadMethodsDeclarationOrder/newline/defaultContextConfig.xml");
        testByConfig("coding/OverloadMethodsDeclarationOrder/patchedline/defaultContextConfig.xml");
        testByConfig("coding/OverloadMethodsDeclarationOrder/context/defaultContextConfig.xml");
    }

    @Test
    public void testPackageDeclaration() throws Exception {
        testByConfig("coding/PackageDeclaration/newline/defaultContextConfig.xml");
        testByConfig("coding/PackageDeclaration/patchedline/defaultContextConfig.xml");
        testByConfig("coding/PackageDeclaration/context/defaultContextConfig.xml");
    }

    @Test
    public void testReturnCount() throws Exception {
        testByConfig("coding/ReturnCount/newline/defaultContextConfig.xml");
        testByConfig("coding/ReturnCount/patchedline/defaultContextConfig.xml");
        testByConfig("coding/ReturnCount/context/defaultContextConfig.xml");
    }

    @Test
    public void testSimplifyBooleanReturn() throws Exception {
        testByConfig("coding/SimplifyBooleanReturn/newline/defaultContextConfig.xml");
        testByConfig("coding/SimplifyBooleanReturn/patchedline/defaultContextConfig.xml");
        testByConfig("coding/SimplifyBooleanReturn/context/defaultContextConfig.xml");
    }

    @Test
    public void testSuperClone() throws Exception {
        testByConfig("coding/SuperClone/newline/defaultContextConfig.xml");
        testByConfig("coding/SuperClone/patchedline/defaultContextConfig.xml");
        testByConfig("coding/SuperClone/context/defaultContextConfig.xml");
    }

    @Test
    public void testSuperFinalize() throws Exception {
        testByConfig("coding/SuperFinalize/newline/defaultContextConfig.xml");
        testByConfig("coding/SuperFinalize/patchedline/defaultContextConfig.xml");
        testByConfig("coding/SuperFinalize/context/defaultContextConfig.xml");
    }

    @Test
    public void testUnnecessarySemicolonInEnumeration() throws Exception {
        testByConfig("coding/UnnecessarySemicolonInEnumeration/newline/defaultContextConfig.xml");
        testByConfig("coding/UnnecessarySemicolonInEnumeration/"
                + "patchedline/defaultContextConfig.xml");
        testByConfig("coding/UnnecessarySemicolonInEnumeration/context/defaultContextConfig.xml");
    }

    @Test
    public void testVariableDeclarationUsageDistance() throws Exception {
        testByConfig("coding/VariableDeclarationUsageDistance/newline/defaultContextConfig.xml");
        testByConfig("coding/VariableDeclarationUsageDistance/"
                + "patchedline/defaultContextConfig.xml");
        testByConfig("coding/VariableDeclarationUsageDistance/"
                + "context/case1/defaultContextConfig.xml");
        testByConfig("coding/VariableDeclarationUsageDistance/"
                + "context/case2/defaultContextConfig.xml");
    }

    @Test
    public void testDesignForExtension() throws Exception {
        testByConfig("design/DesignForExtension/newline/defaultContextConfig.xml");
        testByConfig("design/DesignForExtension/patchedline/defaultContextConfig.xml");
        testByConfig("design/DesignForExtension/context/defaultContextConfig.xml");
    }

    @Test
    public void testFinalClass() throws Exception {
        testByConfig("design/FinalClass/newline/defaultContextConfig.xml");
        testByConfig("design/FinalClass/patchedline/defaultContextConfig.xml");
        testByConfig("design/FinalClass/context/defaultContextConfig.xml");
    }

    @Test
    public void testHideUtilityClassConstructor() throws Exception {
        testByConfig("design/HideUtilityClassConstructor/newline/defaultContextConfig.xml");
        testByConfig("design/HideUtilityClassConstructor/patchedline/defaultContextConfig.xml");
        testByConfig("design/HideUtilityClassConstructor/context/defaultContextConfig.xml");
    }

    @Test
    public void testInnerTypeLast() throws Exception {
        testByConfig("design/InnerTypeLast/newline/defaultContextConfig.xml");
        testByConfig("design/InnerTypeLast/patchedline/defaultContextConfig.xml");
        testByConfig("design/InnerTypeLast/context/defaultContextConfig.xml");
    }

    @Test
    public void testInterfaceIsType() throws Exception {
        testByConfig("design/InterfaceIsType/newline/defaultContextConfig.xml");
        testByConfig("design/InterfaceIsType/patchedline/defaultContextConfig.xml");
        testByConfig("design/InterfaceIsType/context/defaultContextConfig.xml");
    }

    @Test
    public void testOneTopLevelClass() throws Exception {
        testByConfig("design/OneTopLevelClass/newline/defaultContextConfig.xml");
        testByConfig("design/OneTopLevelClass/patchedline/defaultContextConfig.xml");
        testByConfig("design/OneTopLevelClass/context/defaultContextConfig.xml");
    }

    @Test
    public void testThrowsCount() throws Exception {
        testByConfig("design/ThrowsCount/newline/defaultContextConfig.xml");
        testByConfig("design/ThrowsCount/patchedline/defaultContextConfig.xml");
        testByConfig("design/ThrowsCount/context/defaultContextConfig.xml");
    }

    @Test
    public void testCustomImportOrder() throws Exception {
        testByConfig("import/CustomImportOrder/newline/defaultContextConfig.xml");
        testByConfig("import/CustomImportOrder/patchedline/defaultContextConfig.xml");
        testByConfig("import/CustomImportOrder/context/defaultContextConfig.xml");
    }

    @Test
    public void testImportOrder() throws Exception {
        testByConfig("import/ImportOrder/newline/defaultContextConfig.xml");
        testByConfig("import/ImportOrder/patchedline/defaultContextConfig.xml");
        testByConfig("import/ImportOrder/context/defaultContextConfig.xml");
    }

    @Test
    public void testRedundantImport() throws Exception {
        testByConfig("import/RedundantImport/newline/defaultContextConfig.xml");
        testByConfig("import/RedundantImport/patchedline/defaultContextConfig.xml");
        testByConfig("import/RedundantImport/context/defaultContextConfig.xml");
    }

    @Test
    public void testUnusedImports() throws Exception {
        testByConfig("import/UnusedImports/newline/defaultContextConfig.xml");
        testByConfig("import/UnusedImports/patchedline/defaultContextConfig.xml");
        testByConfig("import/UnusedImports/context/defaultContextConfig.xml");
    }

    @Test
    public void testCommentsIndentation() throws Exception {
        testByConfig("indentation/CommentsIndentation/newline/defaultContextConfig.xml");
        testByConfig("indentation/CommentsIndentation/patchedline/defaultContextConfig.xml");
        testByConfig("indentation/CommentsIndentation/context/defaultContextConfig.xml");
        testByConfig("indentation/CommentsIndentation/context/case2/defaultContextConfig.xml");
        testByConfig("indentation/CommentsIndentation/context/case3/defaultContextConfig.xml");
        testByConfig("indentation/CommentsIndentation/context/case4/defaultContextConfig.xml");
        testByConfig("indentation/CommentsIndentation/context/case5/defaultContextConfig.xml");
    }

    @Test
    public void testRegexp() throws Exception {
        testByConfig("Regexp/newline/defaultContextConfig.xml");
        testByConfig("Regexp/patchedline/defaultContextConfig.xml");
        testByConfig("Regexp/context/defaultContextConfig.xml");
    }

    @Test
    public void testAnonInnerLength() throws Exception {
        testByConfig("sizes/AnonInnerLength/newline/defaultContextConfig.xml");
        testByConfig("sizes/AnonInnerLength/patchedline/defaultContextConfig.xml");
        testByConfig("sizes/AnonInnerLength/context/defaultContextConfig.xml");
    }

    @Test
    public void testExecutableStatementCount() throws Exception {
        testByConfig("sizes/ExecutableStatementCount/newline/defaultContextConfig.xml");
        testByConfig("sizes/ExecutableStatementCount/patchedline/defaultContextConfig.xml");
        testByConfig("sizes/ExecutableStatementCount/context/defaultContextConfig.xml");
    }

    @Test
    public void testMethodCount() throws Exception {
        testByConfig("sizes/MethodCount/newline/defaultContextConfig.xml");
        testByConfig("sizes/MethodCount/patchedline/defaultContextConfig.xml");
        testByConfig("sizes/MethodCount/context/defaultContextConfig.xml");
    }

    @Test
    public void testMethodLength() throws Exception {
        testByConfig("sizes/MethodLength/case1/newline/defaultContextConfig.xml");
        testByConfig("sizes/MethodLength/case1/patchedline/defaultContextConfig.xml");
        testByConfig("sizes/MethodLength/case1/context/defaultContextConfig.xml");

        testByConfig("sizes/MethodLength/case2/newline/defaultContextConfig.xml");
        testByConfig("sizes/MethodLength/case2/patchedline/defaultContextConfig.xml");
        testByConfig("sizes/MethodLength/case2/context/defaultContextConfig.xml");
    }

    @Test
    public void testOuterTypeNumber() throws Exception {
        testByConfig("sizes/OuterTypeNumber/newline/defaultContextConfig.xml");
        testByConfig("sizes/OuterTypeNumber/patchedline/defaultContextConfig.xml");
        testByConfig("sizes/OuterTypeNumber/context/defaultContextConfig.xml");
    }

    @Test
    public void testParameterNumber() throws Exception {
        testByConfig("sizes/ParameterNumber/newline/defaultContextConfig.xml");
        testByConfig("sizes/ParameterNumber/patchedline/defaultContextConfig.xml");
        testByConfig("sizes/ParameterNumber/context/defaultContextConfig.xml");
    }

    @Test
    public void testEmptyLineSeparator() throws Exception {
        testByConfig("whitespace/EmptyLineSeparator/newline/defaultContextConfig.xml");
        testByConfig("whitespace/EmptyLineSeparator/patchedline/defaultContextConfig.xml");
        testByConfig("whitespace/EmptyLineSeparator/context/defaultContextConfig.xml");
    }

    @Test
    public void testDescendantToken() throws Exception {
        testByConfig("miscellaneous/DescendantToken/newline/defaultContextConfig.xml");
        testByConfig("miscellaneous/DescendantToken/patchedline/defaultContextConfig.xml");
        testByConfig("miscellaneous/DescendantToken/context/defaultContextConfig.xml");
    }

    @Test
    public void testNoCodeInFile() throws Exception {
        testByConfig("miscellaneous/NoCodeInFile/newline/defaultContextConfig.xml");
        testByConfig("miscellaneous/NoCodeInFile/patchedline/defaultContextConfig.xml");
        testByConfig("miscellaneous/NoCodeInFile/context/defaultContextConfig.xml");
    }

    @Test
    public void testOuterTypeFilename() throws Exception {
        testByConfig("miscellaneous/OuterTypeFilename/newline/defaultContextConfig.xml");
        testByConfig("miscellaneous/OuterTypeFilename/patchedline/defaultContextConfig.xml");
        testByConfig("miscellaneous/OuterTypeFilename/context/defaultContextConfig.xml");
    }

    @Test
    public void testAtclauseOrder() throws Exception {
        testByConfig("javadoc/AtclauseOrder/newline/defaultContextConfig.xml");
        testByConfig("javadoc/AtclauseOrder/patchedline/defaultContextConfig.xml");
        testByConfig("javadoc/AtclauseOrder/context/defaultContextConfig.xml");
    }

    @Test
    public void testInvalidJavadocPosition() throws Exception {
        testByConfig("javadoc/InvalidJavadocPosition/newline/defaultContextConfig.xml");
        testByConfig("javadoc/InvalidJavadocPosition/patchedline/defaultContextConfig.xml");
        testByConfig("javadoc/InvalidJavadocPosition/context/defaultContextConfig.xml");
    }

    @Test
    public void testJavadocMethod() throws Exception {
        testByConfig("javadoc/JavadocMethod/newline/defaultContextConfig.xml");
        testByConfig("javadoc/JavadocMethod/patchedline/defaultContextConfig.xml");
        testByConfig("javadoc/JavadocMethod/context/defaultContextConfig.xml");
    }

    @Test
    public void testJavadocParagraph() throws Exception {
        testByConfig("javadoc/JavadocParagraph/newline/defaultContextConfig.xml");
        testByConfig("javadoc/JavadocParagraph/patchedline/defaultContextConfig.xml");
        testByConfig("javadoc/JavadocParagraph/context/defaultContextConfig.xml");
    }

    @Test
    public void testJavadocType() throws Exception {
        testByConfig("javadoc/JavadocType/newline/defaultContextConfig.xml");
        testByConfig("javadoc/JavadocType/patchedline/defaultContextConfig.xml");
        testByConfig("javadoc/JavadocType/context/defaultContextConfig.xml");
    }

    @Test
    public void testJavadocVariable() throws Exception {
        testByConfig("javadoc/JavadocVariable/newline/defaultContextConfig.xml");
        testByConfig("javadoc/JavadocVariable/patchedline/defaultContextConfig.xml");
        testByConfig("javadoc/JavadocVariable/context/defaultContextConfig.xml");
    }

    @Test
    public void testMissingJavadocMethod() throws Exception {
        testByConfig("javadoc/MissingJavadocMethod/newline/defaultContextConfig.xml");
        testByConfig("javadoc/MissingJavadocMethod/patchedline/defaultContextConfig.xml");
        testByConfig("javadoc/MissingJavadocMethod/context/defaultContextConfig.xml");
    }

    @Test
    public void testMissingJavadocPackage() throws Exception {
        testByConfig("javadoc/MissingJavadocPackage/newline/defaultContextConfig.xml");
        testByConfig("javadoc/MissingJavadocPackage/patchedline/defaultContextConfig.xml");
        testByConfig("javadoc/MissingJavadocPackage/context/defaultContextConfig.xml");
    }

    @Test
    public void testMissingJavadocType() throws Exception {
        testByConfig("javadoc/MissingJavadocType/newline/defaultContextConfig.xml");
        testByConfig("javadoc/MissingJavadocType/patchedline/defaultContextConfig.xml");
        testByConfig("javadoc/MissingJavadocType/context/defaultContextConfig.xml");
    }

    @Test
    public void testSummaryJavadocCheck() throws Exception {
        testByConfig("javadoc/SummaryJavadocCheck/newline/defaultContextConfig.xml");
        testByConfig("javadoc/SummaryJavadocCheck/patchedline/defaultContextConfig.xml");
        testByConfig("javadoc/SummaryJavadocCheck/context/defaultContextConfig.xml");
    }

    @Test
    public void testMethodName() throws Exception {
        testByConfig("naming/MethodName/newline/defaultContextConfig.xml");
        testByConfig("naming/MethodName/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testMemberName() throws Exception {
        testByConfig("naming/MemberName/newline/defaultContextConfig.xml");
        testByConfig("naming/MemberName/patchedline/defaultContextConfig.xml");
    }

    @Test
    public void testBooleanExpressionComplexity() throws Exception {
        testByConfig("metrics/BooleanExpressionComplexity/newline/defaultContextConfig.xml");
        testByConfig("metrics/BooleanExpressionComplexity/patchedline/defaultContextConfig.xml");
        testByConfig("metrics/BooleanExpressionComplexity/context/defaultContextConfig.xml");
    }

    @Test
    public void testClassFanOutComplexity() throws Exception {
        testByConfig("metrics/ClassFanOutComplexity/newline/defaultContextConfig.xml");
        testByConfig("metrics/ClassFanOutComplexity/patchedline/defaultContextConfig.xml");
        testByConfig("metrics/ClassFanOutComplexity/context/defaultContextConfig.xml");
    }

    @Test
    public void testClassDataAbstractionCoupling() throws Exception {
        testByConfig("metrics/ClassDataAbstractionCoupling/newline/defaultContextConfig.xml");
        testByConfig("metrics/ClassDataAbstractionCoupling/patchedline/defaultContextConfig.xml");
        testByConfig("metrics/ClassDataAbstractionCoupling/context/defaultContextConfig.xml");
    }

    @Test
    public void testCyclomaticComplexity() throws Exception {
        testByConfig("metrics/CyclomaticComplexity/newline/defaultContextConfig.xml");
        testByConfig("metrics/CyclomaticComplexity/patchedline/defaultContextConfig.xml");
        testByConfig("metrics/CyclomaticComplexity/context/defaultContextConfig.xml");
    }

    @Test
    public void testJavaNcss() throws Exception {
        testByConfig("metrics/JavaNCSS/newline/defaultContextConfig.xml");
        testByConfig("metrics/JavaNCSS/patchedline/defaultContextConfig.xml");
        testByConfig("metrics/JavaNCSS/context/defaultContextConfig.xml");
    }

    @Test
    public void testNpathComplexity() throws Exception {
        testByConfig("metrics/NPathComplexity/newline/defaultContextConfig.xml");
        testByConfig("metrics/NPathComplexity/patchedline/defaultContextConfig.xml");
        testByConfig("metrics/NPathComplexity/context/defaultContextConfig.xml");
    }

}

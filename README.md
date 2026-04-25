# patch-filters
special project to host Patch Suppressions

[![][mavenbadge img]][mavenbadge]
or at [repo](https://repo1.maven.org/maven2/com/puppycrawl/tools/patch-filters/).


## PatchFilter Description

### SuppressionPatchFilter

Filter SuppressionPatchFilter(Checker level) only accepts audit events for Check violations whose line number
belong to added/changed lines in patch file and will suppress all Checks’ violation messages
which are not from added/changed lines. If there is no configured patch file
or the optional is set to true and patch file was not found the Filter suppresses all audit events.

#### Properties

 | name                  | description                                                                                                                                                                                                                               | type                                                                     | default value |
 |-----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|---------------|
 | file                  | Specify the location of the patch file.                                                                                                                                                                                                   | [String](https://checkstyle.sourceforge.io/property_types.html#String)   | null          |
 | optional              | Control what to do when the file is not existing. If `optional` is set to `false` the file must exist, or else it ends with error. On the other hand if optional is `true` and file is not found, the filter suppresses all audit events. | [boolean](https://checkstyle.sourceforge.io/property_types.html#boolean) | false         |
 | strategy              | Control suppression scope that you need. If `strategy` is set to `newline`, it only accepts audit events for Check violations whose line number belong to added lines in patch file. `patchedline` will accept added/changed lines.       | [String](https://checkstyle.sourceforge.io/property_types.html#String)   | newline       |
 | neverSuppressedChecks | String has user defined Checks to never suppress if files are touched, split by comma. This property is useful for Checks that place violation on whole file or on not all (first/last) occurrence of cause/violated code.                | [String](https://checkstyle.sourceforge.io/property_types.html#String)   | null          |

#### Examples

For example, the following configuration fragment directs the Checker to use a SuppressionPatchFilter with patch file config/file.patch:
```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionPatchFilter">
    <property name="file" value="config/file.patch" />
    <property name="strategy" value="newline" />
</module>
```

the following configuration fragment directs the Checker to use a SuppressionPatchFilter
with patch file config/file.patch, whose strategy is `patchedline`,and never suppress
checks `Translation` and `UniqueProperties`:
```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionPatchFilter">
    <property name="file" value="config/file.patch" />
    <property name="strategy" value="patchedline" />
    <property name="neverSuppressedChecks" value="Translation,UniqueProperties" />
</module>
```

### SuppressionJavaPatchFilter

Filter SuppressionJavaPatchFilter(TreeWalker level) has three different strategies that control suppression
scope. if property `strategy` is set to `newline`, then it only accepts TreeWalker audit events for TreeWalker
Check violations whose line number belong to added lines in patch file and will suppress all TreeWalker Checks’
violation messages which are not from added lines. if property `strategy` is set to `patchedline`, it will accept
all violations whose line number belong to added/changed lines in patch file. if property `strategy` is set to `context`,
for checks listed in `supportContextStrategyChecks`, it will not only accept violations whose line number belong to added/changed/deleted lines,
but also consider a wider context that new code introduces violations outside of added/changed lines, but its child nodes in added/changed lines,
for checks not listed in `supportContextStrategyChecks`, it will accept violations whose line number belong to added/changed/deleted lines in patch file.
If there is no configured patch file or the optional is set to true and patch file was not found the Filter suppresses all audit events.

Note that it's ok to use all four properties (`supportContextStrategyChecks`, `checkNameForContextStrategyByTokenOrParentSet`, `checkNameForContextStrategyByTokenOrAncestorSet`,
`neverSuppressedChecks`), and the context scope of the three is also growing, for TreeWalker checks only in one of the property, its context scope is the same as that property,
if in two or all properties, then The context scope is the maximum scope. 

Attention: `supportContextStrategyChecks` and `checkNameForContextStrategyByTokenOrParentSet`, `checkNameForContextStrategyByTokenOrAncestorSet` 
only have effect when the `strategy` property is set to `context`, `neverSuppressedChecks` is no such requirement.

#### Properties

 | name                                             | description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | type                                                                     | default value |
 |--------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|---------------|
 | file                                             | Specify the location of the patch file.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | [String](https://checkstyle.sourceforge.io/property_types.html#String)   | null          |
 | optional                                         | Control what to do when the file is not existing. If `optional` is set to `false` the file must exist, or else it ends with error. On the other hand if optional is `true` and file is not found, the filter suppresses all audit events.                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | [boolean](https://checkstyle.sourceforge.io/property_types.html#boolean) | false         |
 | strategy                                         | Control suppression scope that you need. If `startegy` is set to `newline`, it only accepts TreeWalker audit events for TreeWalker Check violations whose line number belong to added lines in patch file. `patchedline` will accept added/changed lines. if `strategy` is set to `context` , for checks listed in `supportContextStrategyChecks`, it will accept violations whose line number belong to added/changed/deleted lines and new code introduces violations outside of added/changed lines, but its child nodes in added/changed lines, for checks not listed in `supportContextStrategyChecks`, it will accept violations whose line number belong to added/changed/deleted lines in patch file. | [String](https://checkstyle.sourceforge.io/property_types.html#String)   | newline       |
 | supportContextStrategyChecks                     | String has user defined Checks that support context strategy                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  | [String](https://checkstyle.sourceforge.io/property_types.html#String)   | null          |
 | checkNamesForContextStrategyByTokenOrParentSet   | String has user defined TreeWalker Checks that need modify violation nodes to their parent node to expand the context scope, split by comma                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | [String](https://checkstyle.sourceforge.io/property_types.html#String)   | null          |
 | checkNamesForContextStrategyByTokenOrAncestorSet | String has user defined TreeWalker Checks that need modify violation nodes to their ancestor node to expand the context scope, split by comma                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | [String](https://checkstyle.sourceforge.io/property_types.html#String)   | null          |
 | neverSuppressedChecks                            | String has user defined TreeWalker Checks to never suppress if files are touched, split by comma. This property is useful for Checks that place violation on whole file or on not all (first/last) occurrence of cause/violated code.                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | [String](https://checkstyle.sourceforge.io/property_types.html#String)   | null          |

#### Notes

Currently, the following checks support `checkNamesForContextStrategyByTokenOrAncestorSet` property, 
other checks using this property will get the same effect like `checkNamesForContextStrategyByTokenOrParentSet`:

* ArrayTrailingComma
* AvoidNestedBlocks
* CommentsIndentation
* DefaultComesLast
* DeclarationOrder
* EqualsHashCode
* FinalLocalVariable
* FallThrough
* RightCurly

#### Notes

Currently, the following checks will suppress some violations that should not be suppressed when using `supportContextStrategyChecks`. 
And you can use `checkNamesForContextStrategyByTokenOrParentSet`, `checkNamesForContextStrategyByTokenOrAncestorSet` 
or `neverSuppressedChecks` to get the larger context scope to solve this problem:

* ParameterNumberCheck
* SuperClone
* SuperFinalize
* BooleanExpressionComplexity
* OverloadMethodsDeclarationOrder
* UnnecessarySemicolonInEnumeration

Also, the following checks will suppress some violations that should not be suppressed when using `supportContextStrategyChecks` or `checkNamesForContextStrategyByTokenOrParentSet`. 
So you need use `checkNamesForContextStrategyByTokenOrAncestorSet` or `neverSuppressedChecks` to solve this problem:

* ArrayTrailingComma
* AvoidNestedBlocks
* CommentsIndentation
* DefaultComesLast
* DeclarationOrder
* EqualsHashCode
* FinalLocalVariable
* FallThrough
* RightCurly
* VariableDeclarationUsageDistance

Then the following checks will suppress some violations that should not be suppressed unless you use 
`neverSuppressedChecks`:

* CovariantEquals
* EmptyLineSeparator
* CustomImportOrder
* ImportOrder
* RedundantImport
* UnusedImports
* PackageDeclaration
* MissingJavadocPackage
* MissingJavadocType
* OuterTypeNumberCheck
* OuterTypeFilename
* NoCodeInFileCheck
* Regexp
* AtclauseOrder
* JavadocMethod
* JavadocParagraph
* SummaryJavadoc

#### Examples

For example, the following configuration fragment directs the Checker to use a SuppressionJavaPatchFilter
with patch file config/file.patch and strategy is `newline`:
```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionJavaPatchFilter">
    <property name="file" value="config/file.patch" />
    <property name="strategy" value="newline" />
</module>
```

the following configuration fragment directs the Checker to use a SuppressionJavaPatchFilter
with patch file config/file.patch and strategy is `patchedline`:
```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionJavaPatchFilter">
    <property name="file" value="config/file.patch" />
    <property name="strategy" value="patchedline" />
</module>
```

the following configuration fragment directs the Checker to use a SuppressionJavaPatchFilter
with patch file config/file.patch, whose strategy is `context`,
support context strategy check `MethodLength` and never suppress checks `EmptyBlock` and `HiddenField`:
```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionJavaPatchFilter">
    <property name="file" value="config/file.patch" />
    <property name="strategy" value="context" />
    <property name="supportContextStrategyChecks" value="MethodLength," />
    <property name="neverSuppressedChecks" value="EmptyBlock,HiddenField" />
</module>
```

the following configuration fragment directs the Checker to use a SuppressionJavaPatchFilter
with patch file config/file.patch, whose strategy is `context`,
expand `SuperFinalize`'s context scope to parents' node 
and expand `EqualsHashCode`, `FinalLocalVariable`'s context scope to ancestors' node :
```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionJavaPatchFilter">
    <property name="file" value="config/file.patch" />
    <property name="strategy" value="context" />
    <property name="checkNamesForContextStrategyByTokenOrParentSet" value="SuperFinalize," />
    <property name="checkNamesForContextStrategyByTokenOrAncestorSet" value="EqualsHashCode,FinalLocalVariable" />
</module>
```

## PatchFilter Report Setup

### Generate `show.patch` for local and CI validation

You can point both patch filters to the same generated patch file:

```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionPatchFilter">
    <property name="file" value="show.patch"/>
    <property name="strategy" value="patchedline"/>
</module>

<module name="TreeWalker">
    <module name="com.puppycrawl.tools.checkstyle.filters.SuppressionJavaPatchFilter">
        <property name="file" value="show.patch"/>
        <property name="strategy" value="patchedline"/>
    </module>
</module>
```

Pick patch generation mode based on the workflow you want to validate:

- Last commit only (linear local history): `git diff HEAD~1 HEAD > show.patch`
- Last `N` commits (for example last 3): `git diff HEAD~3 HEAD > show.patch`
- Full Pull Request diff against base branch (Travis-like result):
  `git diff origin/master...HEAD > show.patch`

`origin/master...HEAD` uses the merge-base internally, so it shows the whole branch delta like
Travis merge validation, even if your CI does not create a temporary merge commit.

### Maven example: last commit only

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.0.0</version>
    <executions>
        <execution>
            <phase>verify</phase>
            <goals>
                <goal>exec</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <executable>git</executable>
        <arguments>
            <argument>diff</argument>
            <argument>HEAD~1</argument>
            <argument>HEAD</argument>
        </arguments>
        <outputFile>${maven.multiModuleProjectDirectory}/show.patch</outputFile>
    </configuration>
</plugin>
```

### Maven example: validate last `N` commits

Use the same plugin and replace `HEAD~1` with `HEAD~${patch.lastCommits}`.
For example, running Maven with `-Dpatch.lastCommits=3` checks the last 3 commits.

```xml
<arguments>
    <argument>diff</argument>
    <argument>HEAD~${patch.lastCommits}</argument>
    <argument>HEAD</argument>
</arguments>
```

### Maven example: Travis-like Pull Request diff

Use three-dot diff to compare your branch with base branch from merge-base:

```xml
<arguments>
    <argument>diff</argument>
    <argument>origin/master...HEAD</argument>
</arguments>
```

Before running Maven in CI, make sure the base branch is fetched (for example `origin/master`).

## Handling Unstaged and Uncommitted Changes

By default, patch generation using `git diff HEAD~1 HEAD` only includes
changes that have been committed. Unstaged changes (modified files not
yet added) and untracked files (new files never added to git) will NOT
appear in the patch file. This means:

- Violations on unstaged modified lines will be shown even though
  the developer is actively working on them
- Violations on new untracked files will be suppressed entirely
  because the filter does not know these files exist

###  1: Use git add --intent-to-add (Recommended)

This registers new files with git without fully staging them.
The files will appear in the diff but will NOT be included in
`git commit` unless explicitly staged afterward.
```bash
# Register untracked files so diff can see them
git add --intent-to-add .

# Now generate patch including all working tree changes vs last commit
git diff HEAD~0 > show.patch
```

This is safe because `--intent-to-add` does not stage file content.
Running `git commit` afterward will not accidentally commit these files.

### 2a: Append Unstaged Changes (Linux/Mac)

For environments where choice 1 is not suitable, generate the base
patch and then append unstaged changes separately:
```bash
# Step 1: Base patch from last commit
git diff HEAD~1 HEAD > show.patch

# Step 2: Append unstaged changes to tracked files  
git diff >> show.patch

# Step 3: Append new untracked files
git ls-files -o --exclude-standard -x show.patch \
    | xargs -I {} git diff /dev/null {} >> show.patch
```

### 2b: Append Unstaged Changes (Windows)

On Windows, `/dev/null` is not available. Use the following instead:
```bash
git diff HEAD~1 HEAD > show.patch

git diff >> show.patch

git ls-files -o --exclude-standard -x show.patch ^
    | for /f "tokens=*" %f in ('more') do git diff --no-index NUL %f >> show.patch
```

Or use PowerShell on Windows:
```powershell
# Step 1: Base patch
git diff HEAD~1 HEAD | Out-File -FilePath show.patch -Encoding utf8

# Step 2: Unstaged tracked changes
git diff | Add-Content -Path show.patch -Encoding utf8

# Step 3: Untracked new files
git ls-files -o --exclude-standard -x show.patch | ForEach-Object {
    git diff --no-index $null $_ | Add-Content -Path show.patch -Encoding utf8
}
```

### Requirements

- [Checkstyle repository](https://github.com/checkstyle) need to be cloned.
- [Contribution repository](https://github.com/checkstyle/contribution) need to be cloned.

### Clone

Just clone this repository to your local.

```bash
$ git clone git@github.com:checkstyle/patch-filters.git
```

### local install

Because patch-filter has not been merged by Checkstyle, so you should local install this repository——patch-filters

```bash
$ cd /path/to/patch-filters
$ mvn clean install
```

then checkout to `patch-filter-jar` in contribution repo

```bash
$ cd /path/to/checkstyle-contribution
$ git checkout patch-filter-jar
```
### set checkstyle-tester

Firstly, you need to do is to modify projects-to-test-on.properties and test project that you want.
For example, if I want to test `guava`, then I will only remove # before `guava` (anything that starts with #
is considered a comment and is ignored.) and remove the specific version to change
`guava|git|https://github.com/google/guava|v28.2||` to `guava|git|https://github.com/google/guava||`
, which aims to avoid auto checkout to the specific version in `diff.groovy`.

Secondly, you need to set baseConfig.xml and patchConfig.xml by yourself.

**Attention:**

baseConfig does not need any checks in it and set `severity` to `ignore`,
like `<property name="severity" value="ignore"/>`;

patchConfig need Checks and patch-filter, added checks can
refer to [here](https://github.com/checkstyle/checkstyle/blob/ec4d06712ab203d31d73c5c6d5c46067f3a6d5b3/config/checkstyle_checks.xml#L60-L190),
you can skip Translation Check and checks that reuqires extra config file (Header and RegExpHeader), patchfilter setting is like:
```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionPatchFilter">
        <property name="file" value="${checkstyle.patchfilter.patch}"/>
</module>
```

### Open this repo in IDEA or other IDE

You can find `GeneratePatchFileLauncher` in `/path/to/patch-filters/src/main/java/com/github/checkstyle/generatepatchfile/`,
then add seven follow program arguments in Run/Debug Configurations:

```bash
# RepoPath of repository that will be checked by checkstyle, here example is guava
path/to/guava/
# Path of checkstyle
path/to/checkstyle/
# Path of checkstyle-tester
patch/to/checkstyle-contribution/checkstyle-tester/
# Patch-branch name of checkstyle, which can be any branch except master, because checkstyle-tester's diff mode
# not allow the baseBranch and patchBranch have the same name, you can create a new branch based on master which
# does not need any changes, here is branch name for example patch-filter
patch-filter
# path to the base checkstyle configuration file. It will be applied to base branch
path/to/baseConfig.xml
# path to the patch checkstyle config file. It will be applied to patch-filter branch
path/to/patchConfig.xml
# commit parameter will be used to create patch-filter report
# if commit parameter is number then the Generator will work in sequence mode,
# for example if commit parameter is 4, then 3 reports that represent
# the first three commits in HEAD branch will be created.
4 # sequence mode
# Attention: num should be greater than 1, because if num is 1, no report will be created.

# if commit parameter is a comma separated list of commit hashes then the Generator will work in set mode,
# for example if commit parameter is 86bf3a482c68a3a466b278ae4c7bba4bd7be1d9c,aafac1c6d794750aeba9213e9b15a0b8f0e54f81
# then 2 report that represent the two commit will be created if they belong to the HEAD branch.
86bf3a482c68a3a466b278ae4c7bba4bd7be1d9c,aafac1c6d794750aeba9213e9b15a0b8f0e54f81 # set mode
```
then, add Environment variables:
```bash
checkstyle.patchfilter.patch=/path/to/patch-filters/DiffReport/patch.txt
```
after above, if everything is ok, run it and then reports will be created in `/path/to/patch-filters/DiffReport/`.
for example, when `repoPath` is guava and `runPatchNum` is 4, then result will look like:

- DiffReport
  - guava-a1b3c06
  - guava-bfc1cce
  - guava-fb6ef19
  - index.html

by default, `guava` in `guava-a1b3c06` means `repo`'s name, `a1b3c06` in `guava-a1b3c06` means
the patch-filter diff report which use patch file between the commit `a1b3c06` and the commit before `a1b3c06`,
so it represents the commit `a1b3c06`.

[mavenbadge]:https://search.maven.org/search?q=g:%22com.puppycrawl.tools%22%20AND%20a:%patch-filters%22
[mavenbadge img]:https://img.shields.io/maven-central/v/com.puppycrawl.tools/patch-filters.svg?label=Maven%20Central

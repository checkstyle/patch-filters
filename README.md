# patch-filters
special project to host GSOC project Patch Suppressions 

## PatchFilter Description

### SuppressionPatchXpathFilter

Filter SuppressionPatchXpathFilter(TreeWalker level) has three different strategies that control suppression
scope. if property `strategy` is set to `newline`, then it only accepts TreeWalker audit events for TreeWalker 
Check violations whose line number belong to added lines in patch file and will suppress all TreeWalker Checks’ 
violation messages which are not from added lines. if property `strategy` is set to `patchedline`, it will accept 
all violations whose line number belong to added/changed lines in patch file. if property `strategy` is set to `context`, 
for checks listed in `supportContextStrategyChecks`, it will not only accept violations whose line number belong to added/changed/deleted lines,
but also consider a wider context that new code introduces violations outside of added/changed lines, but its child nodes in added/changed lines,
for checks not listed in `supportContextStrategyChecks`, it will accept violations whose line number belong to added/changed/deleted lines in patch file.
If there is no configured patch file or the optional is set to true and patch file was not found the Filter suppresses all audit events.

Note that it's ok to use all three properties (`supportContextStrategyChecks`, `checkNameForContextStrategyByTokenOrParentSet`, `neverSuppressedChecks`),
and the context scope of the three is also growing, for TreeWalker checks only in one of the property, its context scope is the same as that property,
if in two or all properties, then the The context scope is the maximum scope. 

Attention: `supportContextStrategyChecks` and `checkNameForContextStrategyByTokenOrParentSet` only have effect when the `strategy` property is set to `context`,
`neverSuppressedChecks` is no such requirement.

#### Properties
 
 | name                                             | description                                                  | type                                                         | default value |
 | ------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------- |
 | file                                             | Specify the location of the patch file.                      | [String](https://checkstyle.sourceforge.io/property_types.html#String) | Null          |
 | optional                                         | Control what to do when the file is not existing. If `optional` is set to `false` the file must exist, or else it ends with error. On the other hand if optional is `true` and file is not found, the filter suppresses all audit events. | [boolean](https://checkstyle.sourceforge.io/property_types.html#boolean) | false         |
 | strategy                                         | Control suppression scope that you need. If `startegy` is set to `newline`, it only accepts TreeWalker audit events for TreeWalker Check violations whose line number belong to added lines in patch file. `patchedline` will accept added/changed lines. if `strategy` is set to `context` , for checks listed in `supportContextStrategyChecks`, it will accept violations whose line number belong to added/changed/deleted lines and new code introduces violations outside of added/changed lines, but its child nodes in added/changed lines, for checks not listed in `supportContextStrategyChecks`, it will accept violations whose line number belong to added/changed/deleted lines in patch file. | [String](https://checkstyle.sourceforge.io/property_types.html#String) | newline       |
 | supportContextStrategyChecks                     | String has user defined Checks that support context strategy | [String](https://checkstyle.sourceforge.io/property_types.html#String) | null          |
 | neverSuppressedChecks                            | String has user defined TreeWalker Checks to never suppress if files are touched, split by comma. This property is useful for Checks that place violation on whole file or on not all (first/last) occurrence of cause/violated code. | [String](https://checkstyle.sourceforge.io/property_types.html#String) | null          |
 | checkNamesForContextStrategyByTokenOrParentSet | String has user defined TreeWalker Checks that need modify violation nodes to their parent node to expand the context scope, split by comma | [String](https://checkstyle.sourceforge.io/property_types.html#String) | null          |

#### Examples

For example, the following configuration fragment directs the Checker to use a SuppressionPatchXpathFilter 
with patch file config/file.patch and strategy is `newline`:
```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionPatchXpathFilter">
    <property name="file" value="config/file.patch" />
    <property name="strategy" value="newline" />
</module>
```

the following configuration fragment directs the Checker to use a SuppressionPatchXpathFilter 
with patch file config/file.patch and strategy is `patchedline`:
```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionPatchXpathFilter">
    <property name="file" value="config/file.patch" />
    <property name="strategy" value="patchedline" />
</module>
```

the following configuration fragment direts the Checker to use a SuppressionPatchXpathFilter 
with patch file config/file.patch, whose strategy is `context`,
support context strategy check `MethodLength` and never suppress checks `EmptyBlock` and `HiddenField`:
```xml
<module name="com.puppycrawl.tools.checkstyle.filters.SuppressionPatchXpathFilter">
    <property name="file" value="config/file.patch" />
    <property name="strategy" value="context" />
    <property name="supportContextStrategyChecks" value="MethodLength," />
    <property name="neverSuppressedChecks" value="EmptyBlock,HiddenField" />
</module>
```

## PatchFilter Report Setup

### Requirements

- Checkstyle repository which including patch-filter need to be cloned.
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

Secondly, you need to set baseConfig.xml and patchConfig.xml by yourself, 
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
after above, if everything is ok, reports will be created in `/path/to/patch-filters/DiffReport/`.
for example, when `repoPath` is guava and `runPatchNum` is 4, then result will look like:

- DiffReport
  - guava-a1b3c06
  - guava-bfc1cce
  - guava-fb6ef19
  - index.html

by default, `guava` in `guava-a1b3c06` means `repo`'s name, `a1b3c06` in `guava-a1b3c06` means 
the patch-filter diff report which use patch file between the commit `a1b3c06` and the commit before `a1b3c06`,
so it represents the commit `a1b3c06`.

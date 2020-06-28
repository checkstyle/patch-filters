# patch-filters
special project to host GSOC project Patch Suppressions 

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
is considered a comment and is ignored.)

Secondly, you need to set baseConfig.xml and patchConfig.xml by yourself, 
**Attention:**
 
baseConfig does not need any checks in it and set `severity` to `ignore`, 
like `<property name="severity" value="ignore"/>`; 

patchConfig need Checks and patch-filter, added checks can
refer to [here](https://github.com/checkstyle/checkstyle/blob/ec4d06712ab203d31d73c5c6d5c46067f3a6d5b3/config/checkstyle_checks.xml#L60-L190), 
you can skip Translation Check and checks that reuqires extra config file (Header and RegExpHeader), patchfilter setting is like:
```xml
<module name="com.github.checkstyle.patchfilter.SuppressionPatchFilter">
    <property name="file" value="path/to/patch-filters/DiffReport/patch.txt"/>
</module>
```
value should be absolute path

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
after above, if everything is ok, reports will be created in `/path/to/patch-filters/DiffReport/`.
for example, when `repopath` is guava and `runPatchNum` is 4, then result will look like:

- DiffReport
  - guava-a1b3c06
  - guava-bfc1cce
  - guava-fb6ef19
  - index.html

by default, `guava` in `guava-a1b3c06` means `repo`'s name, `a1b3c06` in `guava-a1b3c06` means 
the patch-filter diff report which use patch file between the commit `a1b3c06` and the commit before `a1b3c06`,
so it represents the commit `a1b3c06`.

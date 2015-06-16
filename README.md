See project page ( http://ddt-ide.github.io/ ) for user information.

Developers Guide
================

[![Build Status](https://bruno-medeiros.ci.cloudbees.com/buildStatus/icon?job=DDT)](https://bruno-medeiros.ci.cloudbees.com/job/DDT/)

#### Setting up the development environment:
 * You need [Eclipse PDE](https://eclipse.org/pde/) to develop Eclipse plugins.
 * Clone the Git repository.
 * In Eclipse, use the "import existing projects" wizard, navigate to the Git repository, and add all the Eclipse projects that are present in the root directory of the Git repo. Java Compiler settings should automatically be configured, since each project has project-specific settings stored in source control.
 * Setup the target platform: Open the target platform file: `releng/target-platform/IDE.target` and set it as your target platform.

 
#### Running the tests in Eclipse:

 * The tests are divided into 3 suites for each of the 3 main plugins: DTool, IDE Core, IDE UI.
 * There are common Eclipse launch configurations for each of these test suites, they should already be available in your workspace, and properly configured to run out of the box. Some of the default VM arguments in the launch configuration (already configured) should be:
  * `-DDToolTestResources.workingDir=${workspace_loc}/_runtime-tests` (workspace for tests to use while running)
  * `-DDToolTestResources.baseDir=${workspace_loc:/org.dsource.ddt.dtool/testdata}` (where to get certain DTool test resources. This allows DTool tests to run outside of Eclipse runtime.)
  * Some of the suites (DTool at the moment) can be run in Lite Mode, skipping some of the heavyweight, long-running tests. There is also a launch configuration for this.
  * Some tests may have external runtime dependencies, such as requiring the DUB tool on the environment PATH.

#### Automated Building and Testing:
Using Maven (and Tycho), it is possible to automatically build DDT, create an update site, and run all the tests. Download [Maven](http://maven.apache.org/) (minimum version 3.0), and run the following commands on the root folder of the repository:
 * Run `mvn package` to build the DDT feature into a p2 repository (which is a local update site). It will be placed at `bin-maven/features.repository/repository`
 * Run `mvn integration-test` to build DDT as above and also run the test suites. You can do `mvn integration-test -P TestsLiteMode` to run the test suites in "Lite Mode" (skip certain long-running tests).
 * Also, running `mvn package -P build-ide-product` will build a pre-packaged Eclipse installation with DDT already installed. This is not released to the public, but can potentially be of some use internally.

#### Creating and deploying a new release:
A release is a web site with an Eclipse p2 update site. The website may contain no web pages at all, rather it can be just the p2 site. To create and deploy a new release:

 1. Ensure the version numbers of all plugins/features/etc. are properly updated, if they haven't been already.
 1. Run `mvn clean integration-test` to perform the Tycho build (see section above). Ensure all tests pass.
 1. Create and push a new release tag for the current release commit. 
 1. Go to the Github releases page and edit the newly present release. Add the corresponding ([ChangeLog.md](documentation/ChangeLog.md)) entries to the release notes. 
 1. Locally, run `ant -f releng/ CreateProjectSite`. This last step will prepare the project web site under `bin-maven/ProjectSite`.
 1. To actually publish the project site, run `ant -f releng/ PublishProjectSite -DreleaseTag=<tagName>`. What happens here is that the whole project site will be pushed into a Git repository, to then be served in some way (for example Github Pages). If `projectSiteGitURL` is not specified, the default value in releng-build.properties will be used.
   * For more info on the Release Engineering script, run `ant -f releng/`, this will print the help.
 1. A branch or tag named `latest` should also be created in Github, pointing to the latest release commit. The previous `latest` tag can be deleted/overwritten. The documentation pages use this tag/branch in their links.

 
## Project design info and notes

#### LangEclipseIDE
This project uses the LangEclipseIDE framework, which is designed to have its source embedded in the host IDE.
See [this section]( https://github.com/bruno-medeiros/LangEclipseIDE/blob/master/README-LangEclipseIDE.md#langeclipseide-source-embedding) for more info on how this should be managed.


#### Unit tests double-method wrapper:
 
What is this code idiom seen so often in Junit tests? :
```java
@Test
public void testXXX() throws Exception { testXXX$(); }
public void testXXX$() throws Exception {
```
This is donely solely as an aid when debugging code, so that the "Drop to frame" functionality can be used on the unit-test method. It seems the Eclipse debugger cannot drop-to-frame to a method that is invoked dynamically (such as the unit-test method). So we wrap the unit-test method on another one. So while we now cannot drop-to-frame in `testXXX`, we can do it in `testXXX$`, which basically allows us to restart the unit-test.

TODO: investigate if there is an alternate way to achieve the same. I haven't actually checked that.

-*- outline -*- (use EMACS to see this in outline-mode)

This is a customization of the EPICS archiveviewer with plugins for the EPICS archiver appliance.

The original README follows:

This readme is about building and running ArchiveViewer.

* Intro
The original sources for this application are CVS-controlled:
CVS root: ics-srv01.sns.ornl.gov:/sns/ADE/cvsroot
Module:   archive_viewer
Tag:      check out from HEAD

ArchiveViewer requires JRE which can be downloaded at http://java.sun.com
Example for getting a specific release:
cvs -d :ext:serge@ics-srv01.sns.ornl.gov:/sns/ADE/cvsroot \
     co archive_viewer

There are three ways to run the ArchiveViewer.
1. Via Java Web Start, best for common users
Go to http://ics-web1.sns.ornl.gov/archive/viewer and follow the intructions.
2. Locally (best when you want to see the code)
- get Apache ANT (http://ant.apache.org/)
- type "ant" in ArchiveViewer's top directory, which creates a new jar file 
(archiveviewer.jar)
- type java -jar archiveviewer.jar to run the ArchiveViewer; add the -h(elp)
command to see the options
3. From other EPICS applications
- for this, EPICS_EXTENSIONS and EPICS_HOST_ARCH must be set properly
- get Apache ANT (http://ant.apache.org/)
- type "ant" in ArchiveViewer's top directory (it will create a jar file, a shell script and copy
them to the standard epics tools directory)
- run "archiveviewer" (all lower case)
 
 * ArchiveViewer directory structure:
ArchiveViewer/
    	ext_jars/ -- contains unmodified jar files this application depends upon
    	README -- this file
        RELEASE -- contans some information on current release
	build.xml -- the ANT makefile
	src/   -- contains the source code
	web/ -- contains jsp pages

4. For more information, go to http://ics-web1.sns.ornl.gov/archive/viewer
Sergei Chevtsov (09-07-2005)

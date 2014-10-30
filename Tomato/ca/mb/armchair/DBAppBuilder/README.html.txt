<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<HTML>
  <HEAD>
    <TITLE>DBAppBuilder - README</TITLE>
  </HEAD>
  <BODY>
  

<h1>DBAppBuilder</h1>

DBAppBuilder is a newborn collection of Java Beans and utilities to assist
in the construction of JDBC-based applications.  <b>IT IS A WORK IN PROGRESS!</b>
It is not yet particularily useful, though the database-enabled widgets provide an excellent
starting point for building Java database applications.

<P>As of this writing, DBAppBuilder has been tested with:
<ul>
<li>Database server:<ul><li>PostgreSQL 7.1.3 (<a href="http://www.postgresql.org">www.postgresql.org</a>)
  <li>PostgreSQL 7.2</ul>
<li>IDE: <ul><li>Netbeans 3.3 (<a href="http://www.netbeans.org">www.netbeans.org</a>)
  <li>Netbeans 3.3.1</ul>    
<li>OS: RedHat Linux 7.1 (<a href="http://www.redhat.com">www.redhat.com</a>), with kernel 2.4.9-12.
<li>Java:<ul><li>Sun JDK 1.3.1_02 (<a href="http://java.sun.com">java.sun.com</a>)
  <li>Sun JDK 1.4</ul>
</ul>


<h2>Goals</h2>

<ul>

<li>Provide a rich collection of JDBC-enabled Beans that collectively meet or
 surpass the 
functionality of MS Access and/or MS Visual Basic for building database applications.

<li>Provide a full suite of visual tools (stand-alone and as a Netbeans module) for
manipulating JDBC-accessible databases.  Collectively, these tools should equal
 or exceed the
functionality of MS Access.

<li>Provide a full suite of visual tools (stand-alone and as a Netbeans module) for 
assembling and manipulating all Beans, including non-DBAppBuilder Beans.  This should
minimise the need for coding.  No code should be required to easily test, implement,
and link Beans via their properties, methods, and events.  Code should only be required
to implement business/application rules or special customisations.  The existing Netbeans
GUI Editor is very close to this model, and may be suitable for this purpose once
a rich collection of Beans are installed on the component palette.

<li>All Beans and classes used to implement the aforementioned tools should be available to 
developers to use in their own applications.

</ul>


<h2>Notes for the Overly Optimistic</h2>

<ul>
<p>None of the "Goals" are implemented yet.
</ul>


<H2>Installation</H2>

<OL>

<li>Obtain the sources:
<ul>
<p><li><i>Using CVS:</i>
  <p><ul>
  <li>The CVSROOT is <b>:pserver:anonymous@cvs.armchair.mb.ca:/home/cvs/cvsroot</b>
  <li>The password is blank.
  <li>The module name is <b>DBAppBuilder</b>
  </ul>
  <p>Example:
  <p><blockquote><code>
       cvs -d :pserver:anonymous@cvs.armchair.mb.ca:/home/cvs/cvsroot checkout DBAppBuilder
  </blockquote></code>

  <p>Anonymous access is read-only.  To request write access, email 
 <a href="mailto:dave@armchair.mb.ca">dave@armchair.mb.ca</a>.

<li><i>Using HTTP:</i>
  <p><ul>
  <li>Unzipped: <a href="http://www.armchair.mb.ca/cvs/DBAppBuilder">http://www.armchair.mb.ca/cvs/DBAppBuilder</a>
  <li>Gzipped tar: <a href="http://www.armchair.mb.ca/cvs/DBAppBuilder.tar.gz">http://www.armchair.mb.ca/cvs/DBAppBuilder.tar.gz</a>
  </ul>
  <p>The most up to date sources are available via CVS.  HTTP access lags behind CVS,
     because the HTTP repository is only updated once per day at 5:00AM CST.
</ul>

<p><li>Compile the lot.  Please note that files are <b>not</b> in a package (though the bundle contains packages),
so the DBAppBuilder module should be mounted in your IDE (if it
 supports mounted filesystems) as a root directory.  For example, if you're using
Netbeans and CVS, your JCVS filesystem should be mounted as /home/mydir/ArmchairCVS/DBAppBuilder, not
/home/mydir/ArmchairCVS with a DBAppBuilder subdirectory.

<p><li>Put everything in ca/mb/armchair/DBAppBuilder/Beans and ca/mb/armchair/DBAppBuilder/Widgets 
in the Component Palette of your IDE.

<p><li>Build Javadocs from the source.

<p><li>Read the Javadocs.

<p><li>Go nuts.

<p><li>Sample apps are found in the <code>Demos</code> subdirectory.  The sample apps expect to find a database
       called 'dbappbuilder'.  Use the appropriate SQL script in <code>Demos/DatabaseDefinitions</code> to create the
       'dbappbuilder' database.

</OL>


<H2>Changes Since Last Time</h2>

<ul>

<li>The default settings for a new DatabaseConnection instance are loaded from a file.  This
 file can be created using the "Set As Default" button on the DBDialog dialog.  Developers
can use this default to point an entire application at a selected database.

<li>The visual Beans are now found in the <code>ca.mb.armchair.DBAppBuilder.Beans</code> package.
  Database-enabled widgets are found in the <code>ca.mb.armchair.DBAppBuilder.Widgets</code> package. 
Interfaces are found
in the <code>ca.mb.armchair.DBAppBuilder.Interfaces</code> package.

<li>A wide variety of database-enabled widgets have been created.

</ul>


<H2>To Do</h2>

<ul>

<li>Implement a widget for foreign key selections, with at least one
derivation based on a DBComboBox.

<li>Provide a derivation of the above that implements a "New Record" operation on the
foreign key table.

<li>After insert to DBRow, we must return to the inserted record.  PostgreSQL provides means
for returning the OID of an inserted record, but what about others?

<li>Implement some form of connection pooling, to prevent bogging database back end with
billions of connections.

<li>Facility to load default DatabaseConnection settings from an XML file.  

<li>Use the aforementioned XML file for all runtime configuration information.

<li>Provide a Bean for setting configuration information at runtime/configtime/developtime.

<li>Editor Beans, providing similar functionality to MS Access forms, tabular editors, etc.

<li>Data model editor.

<li>Form and Report designers and associated Beans.

<li>Data model update deployment Beans.

</ul>



<p>&nbsp;</p>

<center><font size="1">
<p>Copyright &copy 2002, 
<a href="http://www.armchair.mb.ca/">Armchair Airlines Computer Services Inc.</a><br>
All Rights Reserved.
<p><b>END</b>
</font>
</center>

</BODY>
</HTML>

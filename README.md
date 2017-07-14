# startup-listener

This is a temporary version of a startup listener to make the plugin `startupRuleEngine` work with Pentaho 7+.

The jar generated here should to replace the file `cpk-pentaho5-session-listener-TRUNK-SNAPSHOT.jar` packed with the plugin.

# Build

```bash
mvn package
```

# Plugin install

* Stop your pentaho 7.x install;

* Clone the plugin repo: 

```bash
cd <PENTAHO-PATH>/pentaho-solutions/system
git clone -b release https://github.com/webdetails/startupRuleEngine.git
```

* Insert the following line in your `/system/applicationContext-spring-security.xml`:

```xml
  <!-- Begin entry added by startupRuleEngine -->
  <bean class="pt.webdetails.cpk.SessionListener"/>
  <!-- End entry added by startupRuleEngine -->
```

* Copy your lib to Pentaho classpath

```bash
cp ./target/startup-listener-DEV-SNAPSHOT.jar <PENTAHO-PATH>/tomcat/webapps/pentaho/WEB-INF/lib/
```

* Create a fake lib file into the classpath - this will prevent the plugin to copy its file

```bash
touch <PENTAHO-PATH>/tomcat/webapps/pentaho/WEB-INF/lib/cpk-pentaho5-session-listener-TRUNK-SNAPSHOT.jar
```
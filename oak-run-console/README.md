# Property Utils
[propertyUtils.groovy](propertyUtils.groovy)

1. Download the oak-run-x.jar file where x matches the version of Oak used on your system: https://repo1.maven.org/maven2/org/apache/jackrabbit/oak-run/
2. Stop all AEM/Oak instances
3. Upload the oak-run version to the AEM server if using TarMK or MongoDB server (if using MongoMK) 
4. Run this command to start the oak console 
* on TarMK:
```
   java -jar target/oak-run.jar console --quiet /path/to/segmentstore --read-write
```
* For MongoMK run this:
```
   java -jar oak-run.jar console mongodb://localhost/aem-author --read-write
```
5. Run commands to set a property or remove a property on a node
```
:load propertyUtils.groovy
setProperty(session, "/path", "propName", "propValue", false)
rmProperty(session, "/path", "propName")
```

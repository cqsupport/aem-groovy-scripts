# 1. Start oak-run console
1. Download the oak-run-x.jar file where x matches the version of Oak used on your system: https://repo1.maven.org/maven2/org/apache/jackrabbit/oak-run/
2. Stop all AEM/Oak instances.
3. Upload the oak-run version to the AEM server if using TarMK or MongoDB server (if using MongoMK).
4. Run this command to start the oak console.
* on TarMK:
```
   java -jar target/oak-run.jar console --quiet /path/to/segmentstore --read-write
```
* For MongoMK run this:
```
   java -jar oak-run.jar console mongodb://localhost/aem-author --read-write
```
5. The oak-run console shell should start and you will see a prompt.

# 2. Run the script
## Property Utils
[propertyUtils.groovy](propertyUtils.groovy)

Run commands (in the oak-run console shell) to set a property or remove a property on a node
```
:load propertyUtils.groovy
setProperty(session, "/path", "propName", "propValue", false)
rmProperty(session, "/path", "propName")
```

## Node Utils
[nodeUtils.groovy](nodeUtils.groovy)

Run commands (in the oak-run console shell) to remove or rename a node
```
:load nodeUtils.groovy
rmNode(session, "/path/to/nodeToDelete")
mvNode(session, "/path/to/node", "newNameForNode")
```

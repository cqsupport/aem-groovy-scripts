// Groovy script to fix assets which have cq:tags values that are 
// improperly surrounded by square brackets, such as:
//     ./jcr:content/metadata/cq:tags = (String) [we-retail:season/summer,we-retail:genger/women]
// The corrected cq:tags value is stripped of [] and converted to String array (String[]), such as:
//     ./jcr:content/metadata/cq:tags = (String[]) we-retail:season/summer,we-retail:genger/women
// Uses ICFNext's Groovy console https://github.com/icfnext/aem-groovy-console

def TAGS_PROPERTY = "cq:tags"
def DEBUG = false
def SAVE = true 

// Define the query
def start = getNode("/content/dam")
def query = createXPathQuery(start)

println "query = ${query.statement}"

// Execute query and get the results.
def result = query.execute()
def rows = result.rows

println "Found ${rows.size} result(s)"

// Each row is the asset path
rows.each 
{ row ->

    // Get the asset node
    println "row.path is '${row.path}'"
    def assetNode = row.node
    if (DEBUG) println "assetNode is '${assetNode.path}'" 
    
    // Get the jcr:content sub-node of the asset
    def contentNode = assetNode.getNode(javax.jcr.Node.JCR_CONTENT)
    if (DEBUG) println "contentNode is '${contentNode.path}'" 
    
    // Get the metadata sub-node of the jcr:content
    def metadataNode = contentNode.getNode("metadata")
    if (DEBUG) println "metadataNode is '${metadataNode.path}'" 
    
    // Get the cq:tags property value (should be a String with value surrounded by square brackets [])
    def oldTags = metadataNode.get(TAGS_PROPERTY)
    if (DEBUG) println "oldTags is '$oldTags'" 
    
    // Remove the first and last characters "[]" from the existing cq:tags value. 
    def tagsString = oldTags[1..-2]
    if (DEBUG) println "tagsString is '$tagsString'" 
    
    // Convert the string to a String array
    def tagsArray = tagsString.split(",")
    if (DEBUG) println "tagsArray is '$tagsArray'" 
    
    if (SAVE) 
    {
        // Get the cq:tags Property
        def tagsProperty = metadataNode.getProperty(TAGS_PROPERTY)
        
        // Remove the existing cq:tags property because it's wrong type (String)
        tagsProperty.remove()
        
        // Set the cq:tags property
        metadataNode.set(TAGS_PROPERTY,tagsArray)
    }
}

if (SAVE) 
{
    print "Saving..."
	// Prevent workflows from being triggered when the asset is modified.
	session.getWorkspace().getObservationManager().setUserData("changedByWorkflowProcess");
	
	// Save the session changes.
    save()
    println "Done."
}

def createXPathQuery(node) 
{
    def queryManager = session.workspace.queryManager
    def statement = "/jcr:root${node.path}//element(*,dam:Asset)[jcr:like(jcr:content/metadata/@cq:tags,'[%]')]"
    def query = queryManager.createQuery(statement, "xpath")

    query
}
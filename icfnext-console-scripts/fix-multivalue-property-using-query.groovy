// Groovy script to fix assets which have cq:tags values that have 
// improper type Stirng (should be String array, String[]), such as:
//     ./jcr:content/metadata/cq:tags = (String) we-retail:season/summer,we-retail:genger/women
// The corrected cq:tags is converted to String array (String[]), such as:
//     ./jcr:content/metadata/cq:tags = (String[]) we-retail:season/summer,we-retail:genger/women
// Uses ICFNext's Groovy console https://github.com/icfnext/aem-groovy-console

def TAGS_PROPERTY = "cq:tags"
def DEBUG = false
def SAVE = true 

// Define the query
def start = getNode("/content/dam/we-retail/en/people/womens")
def query = createXPathQuery(start)

println "query = ${query.statement}"

// Execute query and get the results.
def result = query.execute()
def rows = result.rows

println "Found ${rows.size} potential result(s)"
def count = 0;

// Each row is the asset path
rows.each 
{ row ->

    // Get the asset node
    if (DEBUG) println "row.path is '${row.path}'"

    def assetNode = row.node
    if (DEBUG) println "assetNode is '${assetNode.path}'" 
    
    // Get the jcr:content sub-node of the asset
    def contentNode = assetNode.getNode(javax.jcr.Node.JCR_CONTENT)
    if (DEBUG) println "contentNode is '${contentNode.path}'" 
    
    // Get the metadata sub-node of the jcr:content
    def metadataNode = contentNode.getNode("metadata")
    if (DEBUG) println "metadataNode is '${metadataNode.path}'" 
    
    // Get the cq:tags Property
    def tagsProperty = metadataNode.getProperty(TAGS_PROPERTY)
    if (DEBUG) println "tagsProperty is '${tagsProperty}'" 
    
    // If property type is not multiple then it needs to be changed.
    if (! tagsProperty.isMultiple())
    {
        if (DEBUG) println "tagsProperty is single value type, needs to be changed."
        
        // Get the cq:tags property value (should be a String with value surrounded by square brackets [])
        def tagsString = metadataNode.get(TAGS_PROPERTY)
        if (DEBUG) println "tagsString is '$tagsString'" 
    
        // Convert the string to a String array
        def tagsArray = tagsString.split(",")
        if (DEBUG) println "tagsArray is '$tagsArray'" 
    
        if (SAVE) 
        {
            // Remove the existing cq:tags property because it's wrong type (String)
            tagsProperty.remove()
        
            // Set the cq:tags property
            metadataNode.set(TAGS_PROPERTY,tagsArray)
            
        }
        println "'${row.path}/jcr:content/metadata/${TAGS_PROPERTY}' changed to String[]"
        count++
    }
}
println "Total of $count assets changed."

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
    def statement = "/jcr:root${node.path}//element(*,dam:Asset)[jcr:content/metadata/@cq:tags]"
    def query = queryManager.createQuery(statement, "xpath")

    query
}
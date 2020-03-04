// Groovy script to copy 6.3 redirect target property to 6.5 redirect target property.
// Traverses all pages under the start path (/content/we-retail).
// Uses ICFNext's Groovy console https://github.com/icfnext/aem-groovy-console

def REDIRECT_PROPERTY_63 = "redirectTarget"
def REDIRECT_PROPERTY_65 = "cq:redirectTarget"
def DEBUG = false
def SAVE = true

getPage("/content/we-retail").recurse { page ->
    def content = page.node

    if (content) 
    {
        def redirect63 = content.get(REDIRECT_PROPERTY_63)
        def redirect65 = content.get(REDIRECT_PROPERTY_65)
        if (DEBUG && (redirect63 || redirect65))
        {
            println "[$page.path] $REDIRECT_PROPERTY_63 is '$redirect63', $REDIRECT_PROPERTY_65 is '$redirect65'"
        }
        if (redirect63 && !redirect65)
        {
            println "[$page.path] setting $REDIRECT_PROPERTY_65 to '$redirect63'"
            if (SAVE) content.set(REDIRECT_PROPERTY_65, redirect63)
        }
    }
}

if (SAVE) 
{
    print "Saving..."
    // Prevent workflows from being triggered when the page is modified.
    session.getWorkspace().getObservationManager().setUserData("changedByWorkflowProcess");
    save()
    println "Done."
}
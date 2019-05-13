// Find missing assets or missing assset/page defintions in a Activation or Deactivateion workflow package 
// Requires ICF Next's AEM Groovy Console: https://github.com/icfnext/aem-groovy-console
def data = []

// Start with the filter node and iterate over each of it's children nodes.
getNode("/etc/workflow/packages/new-coats-to-be-activated-thurs-11pm/jcr:content/vlt:definition/filter").nodes.each 
{ 
  // "root" property contains the path of the item being activated/deactivated.
  def root = it.get("root")
  if (root)
  {
     // Determine if the item actually exists in the JCR.
     def nodeExists = session.nodeExists(root)
     if (! nodeExists)
       data.add( ["Missing asset", root] ) 
  }
  else
  {
    data.add( ["No root property", it.path])
  }
}

table 
{
    columns("Error", "Path")
    rows(data)
}
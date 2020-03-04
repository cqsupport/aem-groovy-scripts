// Print path and blob ID for a given root path.
// Adaptation of @stillalex’s script from here https://gist.github.com/stillalex/06303f8cc1d3780d3eab4c72575883ae
// See: https://gist.github.com/andrewmkhoury/080d9e53dfed5115b90df9f5b06521fe
// This version works with Oak 1.6 and later versions
// 
// Runs in on-line Groovy console http://localhost:4502/system/console/sc
// http://felix.apache.org/documentation/subprojects/apache-felix-script-console-plugin.html
// 1. Web Console Script Console Plugin (which enabled the console at /system/console/sc)
// 2. Groovy (which installs the Groovy interpreter)
//
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger
import org.apache.jackrabbit.oak.api.Type
import org.apache.jackrabbit.oak.spi.state.NodeState
import org.apache.jackrabbit.oak.spi.state.NodeStore

// Prints out the path and blob ID for all non-null blob IDs under the path defined by "traversalPath".
// Optionally, if you provide a list of blob IDs in "matchingBlobIds" it will only print out the path for matching blob IDs.
// Such as: String[] matchingBlobIds = ["a6cab9d7e3829d90ff2456872dbddad1e313df0c56e089f562f34a6a6ddbabe4#22054"]

def countNodes(NodeState n, deep = false, String path = "/", Integer flush = 100000, AtomicInteger count = new AtomicInteger(0), 
               AtomicInteger binaries = new AtomicInteger(0), root = true, 
               String traversalPath = "/content", 
               String[] matchingBlobIds = [])
{
  if (root) 
  {
    out.println "Counting nodes in tree ${path}"
  }

  cnt = count.incrementAndGet()
  if (cnt % flush == 0) out.println("  " + cnt)

  try 
  {
    try 
    {
      if (path.startsWith(traversalPath))
      {
        for (prop in n.getProperties()) 
        {
          if (prop.getType() == Type.BINARIES) 
          {
            for (b in prop.getValue(Type.BINARIES)) 
            {
              if (   (null != b.getBlobId()) 
                  && ((0 == matchingBlobIds.size()) || (matchingBlobIds.contains(b.getBlobId()))))
              {
                out.println "${path} ====> ${b.getBlobId()}"
              }
              binaries.incrementAndGet();
            }
          } 
          else if (prop.getType() == Type.BINARY) 
          {
            def b = prop.getValue(Type.BINARY);
            if (   (null != b.getBlobId())
                && ((0 == matchingBlobIds.size()) || (matchingBlobIds.contains(b.getBlobId()))))
            { 
              out.println "${path} ====> ${b.getBlobId()}"
            }
            binaries.incrementAndGet();
          }
        }
      }
    } 
    catch (e) 
    {
      out.println "warning unable to read node properties ${path} : " + e.getMessage()
      org.codehaus.groovy.runtime.StackTraceUtils.printSanitizedStackTrace(e, out)
    }
    try 
    {
      for (child in n.getChildNodeEntries()) 
      {
        try 
        {
          countNodes(child.getNodeState(), deep, path + child.getName() + "/", flush, count, binaries, false, traversalPath, matchingBlobIds)
        } 
        catch (e) 
        {
          out.println "warning unable to read child node ${path} : " + e.getMessage()
	      org.codehaus.groovy.runtime.StackTraceUtils.printSanitizedStackTrace(e, out)
        }
      }
    } 
    catch (e) 
    {
      out.println "warning unable to read child entries ${path} : " + e.getMessage()
      org.codehaus.groovy.runtime.StackTraceUtils.printSanitizedStackTrace(e, out)
    }
  } 
  catch(e) 
  {
    out.println "warning unable to read node ${path} : " + e.getMessage()
    org.codehaus.groovy.runtime.StackTraceUtils.printSanitizedStackTrace(e, out)
  }

  if (root) 
  {
    out.println "Total nodes in tree ${path}: ${cnt}"
    out.println "Total binaries in tree ${path}: ${binaries.get()}"
  }

  return cnt
}

def countNodes(session) 
{
    NodeStore nstore = session.getRootNode().sessionDelegate.root.store
    def rs = nstore.root
    def ns = rs
    out.println("Running node counter")
    countNodes(ns)
    out.println("Done")
}

def countNodes() 
{
	def repo = osgi.getService(org.apache.sling.jcr.api.SlingRepository)
	def session = repo.loginAdministrative(null)
	try 
    {
	    countNodes(session)
	} 
    finally 
    {
	    session.logout()
	}
}

countNodes()
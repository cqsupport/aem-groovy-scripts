import org.apache.jackrabbit.oak.spi.commit.CommitInfo
import org.apache.jackrabbit.oak.spi.commit.EmptyHook
import org.apache.jackrabbit.oak.spi.state.NodeStore
import org.apache.jackrabbit.oak.commons.PathUtils

def rmNode(def session, String path) {
    println "Removing node ${path}"

    NodeStore ns = session.store
    def nb = ns.root.builder()

    def aBuilder = nb
    for(p in PathUtils.elements(path)) {  aBuilder = aBuilder.getChildNode(p) }

    if(aBuilder.exists()) {
        rm = aBuilder.remove()
        ns.merge(nb, EmptyHook.INSTANCE, CommitInfo.EMPTY)
        return rm
    } else {
        println "Node ${path} doesn't exist"
        return false
    }
}

def renameNode(def session, String path, String newName) {
    println "Renaming node from ${path} to ${newName}"
    
    NodeStore ns = session.store
    def nb = ns.root.builder()

    def aBuilder = nb
    def parentNodeBuilder = nb
    for(p in PathUtils.elements(path)) {  parentNodeBuilder = aBuilder; aBuilder = aBuilder.getChildNode(p); }

    if(aBuilder.exists()) {
        rn = aBuilder.moveTo(parentNodeBuilder, newName);
        ns.merge(nb, EmptyHook.INSTANCE, CommitInfo.EMPTY)
        return rn
    } else {
        println "Node ${path} doesn't exist"
        return false
    }
}

def addChildNode(def session, String path, String newChildNodeName, String primaryType) {
    println "Adding node ${path}/${newChildNodeName}"
    
    NodeStore ns = session.store
    def nb = ns.root.builder()

    def aBuilder = nb
    def parentNodeBuilder = nb
    for(p in PathUtils.elements(path)) {  aBuilder = aBuilder.getChildNode(p); }

    if(aBuilder.exists()) {
        newNodeBuilder = aBuilder.setChildNode(newChildNodeName);
        newNodeBuilder.setProperty("jcr:primaryType", primaryType, org.apache.jackrabbit.oak.api.Type.NAME,);
        ns.merge(nb, EmptyHook.INSTANCE, CommitInfo.EMPTY)
        return newNodeBuilder
    } else {
        println "Parent node ${path} doesn't exist"
        return false
    }
}

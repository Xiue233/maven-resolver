import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;

import java.io.File;
import java.util.*;

public class PreorderNodeListVisitor implements DependencyVisitor {
    private final Map<DependencyNode, Object> visitedNodes;
    protected final List<DependencyNode> nodes;

    public PreorderNodeListVisitor() {
        nodes = new ArrayList<DependencyNode>(128);
        visitedNodes = new IdentityHashMap<DependencyNode, Object>(512);
    }

    @Override
    public boolean visitEnter(DependencyNode node) {
        if (!setVisited(node)) {
            return false;
        }
        if (node.getDependency() != null) {
            nodes.add(node);
        }
        return true;
    }

    @Override
    public boolean visitLeave(DependencyNode node) {
        return true;
    }

    public List<DependencyNode> getNodes()
    {
        return nodes;
    }

    /**
     * Gets the dependencies seen during the graph traversal.
     *
     * @param includeUnresolved Whether unresolved dependencies shall be included in the result or not.
     * @return The list of dependencies, never {@code null}.
     */
    public List<Dependency> getDependencies(boolean includeUnresolved )
    {
        List<Dependency> dependencies = new ArrayList<Dependency>( getNodes().size() );

        for ( DependencyNode node : getNodes() )
        {
            Dependency dependency = node.getDependency();
            if ( dependency != null )
            {
                if ( includeUnresolved || dependency.getArtifact().getFile() != null )
                {
                    dependencies.add( dependency );
                }
            }
        }

        return dependencies;
    }

    /**
     * Gets the artifacts associated with the list of dependency nodes generated during the graph traversal.
     *
     * @param includeUnresolved Whether unresolved artifacts shall be included in the result or not.
     * @return The list of artifacts, never {@code null}.
     */
    public List<Artifact> getArtifacts(boolean includeUnresolved )
    {
        List<Artifact> artifacts = new ArrayList<Artifact>( getNodes().size() );

        for ( DependencyNode node : getNodes() )
        {
            if ( node.getDependency() != null )
            {
                Artifact artifact = node.getDependency().getArtifact();
                if ( includeUnresolved || artifact.getFile() != null )
                {
                    artifacts.add( artifact );
                }
            }
        }

        return artifacts;
    }

    /**
     * Gets the files of resolved artifacts seen during the graph traversal.
     *
     * @return The list of artifact files, never {@code null}.
     */
    public List<File> getFiles()
    {
        List<File> files = new ArrayList<File>( getNodes().size() );

        for ( DependencyNode node : getNodes() )
        {
            if ( node.getDependency() != null )
            {
                File file = node.getDependency().getArtifact().getFile();
                if ( file != null )
                {
                    files.add( file );
                }
            }
        }

        return files;
    }

    /**
     * Gets a class path by concatenating the artifact files of the visited dependency nodes. Nodes with unresolved
     * artifacts are automatically skipped.
     *
     * @return The class path, using the platform-specific path separator, never {@code null}.
     */
    public String getClassPath()
    {
        StringBuilder buffer = new StringBuilder( 1024 );

        for (Iterator<DependencyNode> it = getNodes().iterator(); it.hasNext(); )
        {
            DependencyNode node = it.next();
            if ( node.getDependency() != null )
            {
                Artifact artifact = node.getDependency().getArtifact();
                if ( artifact.getFile() != null )
                {
                    buffer.append( artifact.getFile().getAbsolutePath() );
                    if ( it.hasNext() )
                    {
                        buffer.append( File.pathSeparatorChar );
                    }
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Marks the specified node as being visited and determines whether the node has been visited before.
     *
     * @param node The node being visited, must not be {@code null}.
     * @return {@code true} if the node has not been visited before, {@code false} if the node was already visited.
     */
    protected boolean setVisited( DependencyNode node )
    {
        return visitedNodes.put( node, Boolean.TRUE ) == null;
    }
}

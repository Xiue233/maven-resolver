import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * 依赖库下载器
 * 支持设置仓库以及jar下载的位置
 */
public class DependencyDownloader {
    //jar下载目录
    private String target;
    private RepositorySystem repoSystem = DependencyFactory.newRepositorySystem();
    private RepositorySystemSession session;
    private HashMap<String, RemoteRepository> mavens = new HashMap<>();
    private ArtifactRequest artifactRequest;
    private CollectRequest collectRequest;

    public DependencyDownloader(String target) {
        setTarget(target);
        File targetFile = new File(target);
        if (!targetFile.exists())
            targetFile.mkdirs();
        session = DependencyFactory.newSession(repoSystem, target);
        artifactRequest = new ArtifactRequest();
        collectRequest = new CollectRequest();
        mavens.put("google",
                new RemoteRepository.Builder("google", "default", RepositoryConfig.GOOGLE).build());
        mavens.put("central",
                new RemoteRepository.Builder("central", "default", RepositoryConfig.CENTRAL).build());
        mavens.put("jcenter",
                new RemoteRepository.Builder("jcenter", "default", RepositoryConfig.JCENTER).build());
        mavens.put("public",
                new RemoteRepository.Builder("public", "default", RepositoryConfig.PUBLIC).build());
        artifactRequest.setRepositories(new ArrayList<>(mavens.values()));
        collectRequest.setRepositories(new ArrayList<>(mavens.values()));
    }

    /**
     * 仅下载此jar，不处理依赖关系
     *
     * @param coords 所下载库的artifacts
     */
    public void download(String[] coords, DownloadListener listener) {
        for (String coord : coords) {
            try {
                download(generateArtifact(coord));
                listener.onSuccess();
            } catch (Exception e) {
                listener.onFailure(coord, e.getMessage());
                break;
            }
        }
    }

    public synchronized void download(Artifact artifact) throws ArtifactResolutionException {
        artifactRequest.setArtifact(artifact);
        repoSystem.resolveArtifact(session, artifactRequest);
    }

    public void downloadAll(Artifact artifact, DownloadListener listener) {
        try {
            downloadAll(artifact);
            listener.onSuccess();
        } catch (Exception e) {
            listener.onFailure(artifact.getArtifactId(), e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 下载此依赖，并且下载其依赖的库
     *
     * @param artifact 所下载库的artifacts
     */
    public synchronized void downloadAll(Artifact artifact) throws ArtifactResolutionException, DependencyCollectionException, DependencyResolutionException {
        Dependency dependency = new Dependency(artifact, null);
        collectRequest.setRoot(dependency);
        DependencyNode node = repoSystem.collectDependencies(session, collectRequest).getRoot();
        DependencyRequest dependencyRequest = new DependencyRequest();
        dependencyRequest.setRoot(node);
        repoSystem.resolveDependencies(session, dependencyRequest);
        PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
        node.accept(nlg);
    }

    public List<Artifact> collectArtifact(Artifact artifact) throws DependencyCollectionException, DependencyResolutionException {
        Dependency dependency = new Dependency(artifact, null);
        collectRequest.setRoot(dependency);
        DependencyNode node = repoSystem.collectDependencies(session, collectRequest).getRoot();
        DependencyRequest dependencyRequest = new DependencyRequest();
        dependencyRequest.setRoot(node);
        repoSystem.resolveDependencies(session, dependencyRequest);
        PreorderNodeListVisitor nlg = new PreorderNodeListVisitor();
        node.accept(nlg);
        return nlg.getArtifacts(true);
    }

    public void addRepository(String id, String url) {
        mavens.put(id, new RemoteRepository.Builder(id, "default", url).build());
    }

    public Artifact generateArtifact(String coords) {
        return new DefaultArtifact(coords);
    }

    public String getTarget() {
        return target;
    }

    public synchronized void setTarget(String target) {
        this.target = target;
        session = DependencyFactory.newSession(repoSystem, target);
    }
}

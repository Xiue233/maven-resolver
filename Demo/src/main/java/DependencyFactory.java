import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

/**
 * @author LZJ
 * @create 2018-07-19 11:28
 **/
public class DependencyFactory {

    /**
     * 生成 RepositorySystem
     *
     * @return RepositorySystem
     */
    public static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    /**
     * 生成 RepositorySystemSession
     *
     * @param system RepositorySystem
     * @return RepositorySystemSession
     */
    public static RepositorySystemSession newSession(RepositorySystem system, String target) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = newLocalRepository(target);
        session.setConfigProperty(ConflictResolver.CONFIG_PROP_VERBOSE, true);
        session.setConfigProperty(DependencyManagerUtils.CONFIG_PROP_VERBOSE, true);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        return session;
    }

    /**
     * 生成 RemoteRepository
     *
     * @param params
     * @return
     */
    public static RemoteRepository newRemoteRepository(Params params) {
        RemoteRepository central;
        String repository = params.getRepository();
        String username = params.getUsername();
        String password = params.getPassword();
        if (StringUtils.isNotBlank(repository)) {
            if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
                central = new RemoteRepository.Builder("central", "default", repository).build();
            } else {
                Authentication authentication = new AuthenticationBuilder().addUsername(username).addPassword(password).build();
                central = new RemoteRepository.Builder("central", "default", repository).setAuthentication(authentication).build();
            }
        } else {
            repository = RepositoryConfig.CENTRAL;
            central = new RemoteRepository.Builder("central", "default", repository).build();
        }
        return central;
    }

    /**
     * 生成 LocalRepository
     *
     * @param target
     * @return
     */
    public static LocalRepository newLocalRepository(String target) {
        if (!StringUtils.isNotBlank(target))
            throw new RuntimeException("target path cannot be blank");
        return new LocalRepository(target);
    }

    public static DefaultArtifact newDefaultArtifact(String artifactId, String groupId, String version) {
        String artifactStr = groupId + ":" + artifactId + ":" + version;
        return new DefaultArtifact(artifactStr);
    }
}
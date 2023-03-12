public class Params {

    private String groupId;

    private String artifactId;

    private String version;

    //远程maven仓库的URL地址 http://" + host + ":" + port + "/nexus/content/groups/public/
    private String repository;

    //下载的jar包存放的目标地址
    private String target;

    private String username;

    private String password;

    public Params() {
        super();
    }

    public Params(String groupId, String artifactId) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public Params(String groupId, String artifactId, String username,
                  String password) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.username = username;
        this.password = password;
    }

    public Params(String groupId, String artifactId, String version,
                  String repository, /*String target,*/ String username, String password) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.repository = repository;
        /*this.target = target;*/
        this.username = username;
        this.password = password;
    }

    public Params(String groupId, String artifactId, String version,
                  String username, String password) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.username = username;
        this.password = password;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
import org.eclipse.aether.artifact.DefaultArtifact;

public class Main {
    public static void main(String[] args) {
        DependencyDownloader downloader = new DependencyDownloader("D://jars");
        downloader.downloadAll(
                new DefaultArtifact("androidx.appcompat:appcompat:aar:1.6.0"),
                new DownloadListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("Download successfully");
                    }

                    @Override
                    public void onFailure(String depedency, String errorMsg) {
                        System.out.println("Error in " + depedency + " : " + errorMsg);
                    }
                }
        );
    }
}

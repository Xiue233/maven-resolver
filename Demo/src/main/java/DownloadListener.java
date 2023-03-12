public interface DownloadListener {
    /**
     * 依赖下载成功
     */
    void onSuccess();

    /**
     * 依赖下载失败
     *
     * @param depedency 依赖的coords [groupId:artifactId:version]
     * @param errorMsg  错误信息
     */
    void onFailure(String depedency, String errorMsg);
}

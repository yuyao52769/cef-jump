package cn.yuyao.cefjump.cache;

import com.intellij.openapi.project.Project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yuyao
 * @create 2025/3/31
 */
public class CefCacheService {

    public final static CefCacheService instance = new CefCacheService();

    private static Map<String, String> HTML_CACHE = new ConcurrentHashMap<>();

    private static Map<String, DescCacheService> DESC_CACHE = new ConcurrentHashMap<>();

    public String getCacheHtmlStr(String projectPath) {
        return HTML_CACHE.get(projectPath);
    }

    public String getCacheHtmlStr(Project project) {
        return getCacheHtmlStr(project.getBasePath());
    }

    public void clear(String projectPath) {
        HTML_CACHE.remove(projectPath);
    }

    public void clear(Project project) {
        clear(project.getBasePath());
    }

    public DescCacheService getDescCacheByProject(Project project ) {
        DescCacheService service = DESC_CACHE.get(project.getBasePath());
        if (service != null) return service;
        DESC_CACHE.put(project.getBasePath(), new DescCacheService());
        return DESC_CACHE.get(project.getBasePath());
    }

    public void clearDescCacheByProject(Project project) {
        DESC_CACHE.remove(project.getBasePath());
    }
}

package cn.yuyao.cefjump.docGen;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CefDocGenListener implements ProjectManagerListener {

    private static final String TARGET_ANNO = "cn.zcy.ka.cef.method.CefDocDesc";

    private static final CodeGenHandler codeGenHandler = new CodeGenHandler();

    // 键：项目路径（String），值：你的业务数据（String）
    public static final Map<String, String> projectDataMap = new ConcurrentHashMap<>();

    @Override
    public void projectOpened(@NotNull Project project) {
        String projectPath = project.getBasePath(); // 或 project.getProjectFilePath()
        codeGenHandler.generate(project, projectPath, TARGET_ANNO);
    }
}

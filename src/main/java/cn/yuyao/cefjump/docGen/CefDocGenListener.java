package cn.yuyao.cefjump.docGen;

import cn.yuyao.cefjump.constant.AnnoConstant;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CefDocGenListener implements ProjectManagerListener {


    private static final CodeGenHandler codeGenHandler = new CodeGenHandler();

    // 键：项目路径（String），值：你的业务数据（String）
    public static final Map<String, String> projectDataMap = new ConcurrentHashMap<>();

    @Override
    public void projectOpened(@NotNull Project project) {
        String projectPath = project.getBasePath(); // 或 project.getProjectFilePath()
//        DumbService dumbService = DumbService.getInstance(project);
//        dumbService.smartInvokeLater(() -> {
//            // 索引可用，执行生成逻辑
//            codeGenHandler.generate(project, projectPath, AnnoConstant.TARGET_DOC_DESC_ANNO);
//        });
        codeGenHandler.generate(project, projectPath, AnnoConstant.TARGET_DOC_DESC_ANNO);

    }
}

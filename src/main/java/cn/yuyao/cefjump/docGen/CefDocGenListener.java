package cn.yuyao.cefjump.docGen;

import cn.yuyao.cefjump.constant.AnnoConstant;
import cn.yuyao.cefjump.cache.CefCacheService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CefDocGenListener implements ProjectManagerListener {

//    Messages.showMessageDialog(project, "项目打开" + projectPath,
//            "成功", Messages.getInformationIcon());
    @Override
    public void projectOpened(@NotNull Project project) {
        String projectPath = project.getBasePath(); // 或 project.getProjectFilePath()
        CodeGenHandler.INSTANCE.generate(project, projectPath, AnnoConstant.TARGET_DOC_DESC_ANNO);

    }

    @Override
    public void projectClosed(@NotNull Project project) {
        CefCacheService.instance.clear(project);
    }
}

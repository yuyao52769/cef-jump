package cn.yuyao.cefjump.docGen;

import cn.yuyao.cefjump.cache.CefCacheService;
import cn.yuyao.cefjump.constant.AnnoConstant;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class CefRefreshAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // 获取当前项目
        Project project = anActionEvent.getProject();
        String basePath = project.getBasePath();
        CefCacheService.instance.clear(project);
        CodeGenHandler.INSTANCE.generate(project, basePath, AnnoConstant.TARGET_DOC_DESC_ANNO, () -> {
            Messages.showMessageDialog(project, "cef框架说明书刷新成功",
                    "成功", Messages.getInformationIcon());
        });

    }
}

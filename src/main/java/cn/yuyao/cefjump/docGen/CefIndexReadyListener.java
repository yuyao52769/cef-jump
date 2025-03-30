package cn.yuyao.cefjump.docGen;

import cn.yuyao.cefjump.constant.AnnoConstant;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

/**
 * @author yuyao
 * @create 2025/3/29
 */
public class CefIndexReadyListener implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        DumbService.getInstance(project).runWhenSmart(() -> {
            // 核心逻辑：延迟检查 JAR 索引状态
            checkJarIndexingRecursively(project, 0);
        });
    }

    private void checkJarIndexingRecursively(Project project, int retryCount) {
        // 验证关键注解类是否已可访问（示例注解：com.example.TargetAnno）
        boolean isAnnotationClassAccessible = JavaPsiFacade.getInstance(project)
                .findClass("cn.yuyao.BaseProjectDemo", GlobalSearchScope.allScope(project)) != null;

        if (isAnnotationClassAccessible) {
            // 索引真正就绪，执行业务逻辑
            triggerBusinessLogic(project);
        } else if (retryCount < 5) {
            // 延迟重试（间隔 1秒）
            DumbService.getInstance(project).smartInvokeLater(() ->
                    checkJarIndexingRecursively(project, retryCount + 1)
            );
        } else {
            Messages.showErrorDialog(project, "依赖 JAR 索引超时", "错误");
        }
    }

    private void triggerBusinessLogic(Project project) {
        // 调用你的生成逻辑
        String projectPath = project.getBasePath();
        new CodeGenHandler().generate(project, projectPath, AnnoConstant.TARGET_DOC_DESC_ANNO);
    }
}

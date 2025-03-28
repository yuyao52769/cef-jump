package cn.yuyao.cefjump.docGen;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CodeGenHandler {

    // 键：项目路径（String），值：你的业务数据（String）
    private static final Map<String, Set<String>> projectDataMap = new ConcurrentHashMap<>();

  public void generate(Project project, String projectPath, String targetAnno) {
      ApplicationManager.getApplication().executeOnPooledThread(() -> {
          asyncGenerate(project, projectPath,  targetAnno);
      });
  }

  protected void asyncGenerate(Project project, String projectPath, String targetAnno) {
      // 1. 获取项目根目录
      VirtualFile projectDir = project.getBaseDir();
      if (projectDir == null) {
          throw new RuntimeException("项目目录不存在！");
      }
      projectDataMap.putIfAbsent(projectPath, new HashSet<String>());
      Set<String> methodCache = projectDataMap.get(projectPath);
      JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
      PsiClass[] annotationClassList = javaPsiFacade.findClasses(targetAnno, GlobalSearchScope.allScope(project));
      // 查找匹配的 @targetAnno 注解方法
      for (PsiClass aClass : annotationClassList) {
          Collection<PsiMethod> annotatedMethods = AnnotatedElementsSearch.searchPsiMethods(
                  aClass,
                  GlobalSearchScope.allScope(project)
          ).findAll();

          annotatedMethods.stream()
                  .filter(o -> methodCache.add(getTotalName(o)))
                  .forEach(o -> {

                  });

      }

  }

  protected void handlerMethod(PsiMethod method, String targetAnno) {
      PsiAnnotation annotation = method.getAnnotation(targetAnno);
      if (annotation == null) return;
      PsiLiteralExpression moduleExp = (PsiLiteralExpression)annotation.findAttributeValue("module");
      PsiLiteralExpression funcExp = (PsiLiteralExpression)annotation.findAttributeValue("func");
      String module = moduleExp.getValue().toString();
      String func = funcExp.getValue().toString();

  }

  public String getTotalName(PsiMethod psiMethod) {
      return psiMethod.getContainingClass().getQualifiedName()
              .concat(psiMethod.getName());
  }
}

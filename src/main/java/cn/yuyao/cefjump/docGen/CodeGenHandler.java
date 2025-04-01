package cn.yuyao.cefjump.docGen;

import cn.yuyao.cefjump.CefDocModuleDesc;
import cn.yuyao.cefjump.constant.AnnoConstant;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CodeGenHandler {

    public final static CodeGenHandler INSTANCE = new CodeGenHandler();

  public void generate(Project project, String projectPath, String targetAnno) {
      generate(project, projectPath, targetAnno, null);
  }

    public void generate(Project project, String projectPath, String targetAnno, Runnable runnable) {
        DumbService.getInstance(project).smartInvokeLater(() -> {
            asyncGenerate(project, projectPath, targetAnno);
            if (runnable != null)  runnable.run();

        });
    }

  protected void asyncGenerate(Project project, String projectPath, String targetAnno) {
      // 1. 获取项目根目录
      VirtualFile projectDir = project.getBaseDir();
      if (projectDir == null) {
          throw new RuntimeException("项目目录不存在！");
      }

      List<CefDocModuleDesc> cefDocModuleCache =  new ArrayList<>();
      JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
      PsiClass[] annotationClassList = javaPsiFacade.findClasses(targetAnno, GlobalSearchScope.allScope(project));

      for (PsiClass aClass : annotationClassList) {
          Collection<PsiMethod> annotatedMethods = AnnotatedElementsSearch.searchPsiMethods(
                  aClass,
                  GlobalSearchScope.allScope(project)
          ).findAll();
          List<CefDocModuleDesc> collect = annotatedMethods.stream()
                  .map(o -> handlerMethod(o, targetAnno))
                  .filter(o -> o != null)
                  .collect(Collectors.toList());
        if (collect != null & collect.size() > 0) cefDocModuleCache.addAll(collect);
      }
      StringBuilder sb = new StringBuilder();
      for (CefDocModuleDesc moduleDesc : cefDocModuleCache) {
          sb.append(moduleDesc.toString());
      }

      HtmlGenHandler.INSTANCE.generate(cefDocModuleCache, project, projectPath);

  }

  protected CefDocModuleDesc handlerMethod(PsiMethod method, String targetAnno) {
      PsiAnnotation annotation = method.getAnnotation(targetAnno);
      if (annotation == null) return null;
      PsiLiteralExpression moduleExp = (PsiLiteralExpression)annotation.findAttributeValue("module");
      PsiLiteralExpression nameExp = (PsiLiteralExpression)annotation.findAttributeValue("name");
      PsiLiteralExpression funcExp = (PsiLiteralExpression)annotation.findAttributeValue("func");
      PsiLiteralExpression descExp = (PsiLiteralExpression)annotation.findAttributeValue("desc");
      String module = moduleExp.getValue().toString();
      String name = nameExp.getValue().toString();
      String func = funcExp.getValue().toString();
      String desc = descExp.getValue().toString();
      PsiElement navigationElement = method.getNavigationElement();
      return createModuleDesc(method, navigationElement, module, name, func, desc);
  }

  protected CefDocModuleDesc createModuleDesc(PsiMethod method, PsiElement navigationElement, String module, String name, String func, String desc) {
      List<CefDocModuleDesc.OpenFunc> funcList = new ArrayList<>();
      List<AnnoConstant.OpenTypeHandler> openAnnoList = AnnoConstant.OPEN_ANNO_LIST;
      if (navigationElement instanceof PsiMethod) {
          PsiMethod naMethod = (PsiMethod) navigationElement;
          for (AnnoConstant.OpenTypeHandler open : openAnnoList) {
              PsiAnnotation annotation = naMethod.getAnnotation(open.getAnnoName());
              if (annotation != null) {
                  CefDocModuleDesc.OpenFunc openFunc = new CefDocModuleDesc.OpenFunc();
                  openFunc.setType(open.getTypeEnum());
                  openFunc.setOpenId(((PsiLiteralExpression)annotation.findAttributeValue("value")).getValue().toString());
                  funcList.add(openFunc);
              }
          }
      }

      if (funcList.size() == 0) return null;


      CefDocModuleDesc moduleDesc = new CefDocModuleDesc();
      moduleDesc.setModule(module);
      moduleDesc.setName(name);
      moduleDesc.setFunc(func);
      moduleDesc.setDesc(desc);
      moduleDesc.setOpenFuncList(funcList);
      moduleDesc.setClassName(method.getContainingClass().getQualifiedName());
      moduleDesc.setMethodName(method.getName());
      return moduleDesc;
  }

  public String getTotalName(PsiMethod psiMethod) {
      return psiMethod.getContainingClass().getQualifiedName()
              .concat(psiMethod.getName());
  }
}

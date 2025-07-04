package cn.yuyao.cefjump.docGen;

import cn.yuyao.cefjump.CefDocModuleDesc;
import cn.yuyao.cefjump.cache.CefCacheService;
import cn.yuyao.cefjump.cache.DescCacheService;
import cn.yuyao.cefjump.constant.AnnoConstant;
import cn.yuyao.cefjump.dto.Param;
import cn.yuyao.cefjump.dto.ParamDesc;
import cn.yuyao.cefjump.util.ClassUtil;
import cn.yuyao.cefjump.util.ClassWrapper;
import cn.yuyao.cefjump.util.RegularUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.javadoc.PsiDocParamRef;
import com.intellij.psi.impl.source.javadoc.PsiDocTagImpl;
import com.intellij.psi.impl.source.javadoc.PsiDocTokenImpl;
import com.intellij.psi.impl.source.tree.java.PsiBinaryExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiPolyadicExpressionImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.intellij.psi.util.PsiUtil;

import java.util.*;
import java.util.stream.Collectors;

public class CodeGenHandler {

    public final static CodeGenHandler INSTANCE = new CodeGenHandler();

  public void generate(Project project, String projectPath, String targetAnno) {
      generate(project, projectPath, targetAnno, null);
  }

    public void generate(Project project, String projectPath, String targetAnno, Runnable runnable) {
        DumbService.getInstance(project).smartInvokeLater(() -> {
            asyncGenerate(project, projectPath, targetAnno, CefCacheService.instance.getDescCacheByProject(project));
            if (runnable != null)  runnable.run();

        });
    }

  protected void asyncGenerate(Project project, String projectPath, String targetAnno, DescCacheService cacheService) {
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
                  .map(o -> handlerMethod(o, targetAnno, cacheService))
                  .filter(o -> o != null)
                  .collect(Collectors.toList());
        if (collect != null & collect.size() > 0) cefDocModuleCache.addAll(collect);
      }

      HtmlGenHandler.INSTANCE.generate(cefDocModuleCache, project, projectPath);
      CefCacheService.instance.clearDescCacheByProject(project);

  }

  protected CefDocModuleDesc handlerMethod(PsiMethod method, String targetAnno, DescCacheService cacheService) {
      PsiAnnotation annotation = method.getAnnotation(targetAnno);
      if (annotation == null) return null;
      Map<String, String> methodDescMap = new HashMap<>();
      PsiDocComment docComment2 = method.getDocComment();
      if (docComment2 != null && docComment2.getTags() != null && docComment2.getTags().length > 0) {
          methodDescMap = classifyTag(docComment2.getTags());
      }


      PsiLiteralExpression moduleExp = (PsiLiteralExpression)annotation.findAttributeValue("module");
      PsiLiteralExpression nameExp = (PsiLiteralExpression)annotation.findAttributeValue("name");
      //PsiLiteralExpression funcExp = (PsiLiteralExpression)annotation.findAttributeValue("func");
      String module = moduleExp.getValue().toString();
      String name = nameExp.getValue().toString();
      //String func = funcExp.getValue().toString();
      String func = getTextFromAnno(annotation, "func");
      PsiElement navigationElement = method.getNavigationElement();
      return createModuleDesc(method, navigationElement, module, name, func, methodDescMap, cacheService);
  }

  protected String getTextFromAnno(PsiAnnotation annotation, String key) {
      PsiAnnotationMemberValue psiValue = annotation.findAttributeValue(key);
      if (psiValue instanceof PsiLiteralExpression) {
          return ((PsiLiteralExpression) psiValue).getValue().toString();
      }
      String text1 = psiValue.getText();
      String cleaned = text1
              .replaceAll("\\s*\\+\\s*", "")
              .replaceAll("\n", "")
              .replaceAll("\r", "")
              .replaceAll("\"", "");
      return cleaned;
  }

  protected CefDocModuleDesc createModuleDesc(PsiMethod method, PsiElement navigationElement, String module, String name,
                                              String func, Map<String, String> methodDescMap,
                                              DescCacheService cacheService) {
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
      moduleDesc.setDesc(null);
      moduleDesc.setOpenFuncList(funcList);
      moduleDesc.setClassName(method.getContainingClass().getQualifiedName());
      moduleDesc.setMethodName(method.getName());
      ParamDesc methodDesc = createMethodDesc(method, methodDescMap, cacheService);
      moduleDesc.setParamDesc(methodDesc);
      return moduleDesc;
  }

  protected ParamDesc createMethodDesc(PsiMethod method, Map<String, String> methodDescMap, DescCacheService cacheService) {
      ParamDesc result = new ParamDesc();
      PsiType returnType = method.getReturnType();
      PsiParameterList parameterList = method.getParameterList();
      ClassWrapper build = ClassUtil.build(returnType);
      Param returnParam = ClassUtil.buildReturnParam(build, methodDescMap.get("return"), cacheService);
      result.setReturnDesc(returnParam);
      if (parameterList == null || parameterList.getParameters() == null || parameterList.getParameters().length <= 0) {
          // 说明该扩展方法没有入参

      } else {
          List<Param> paramList = ClassUtil.buildJoinParam(parameterList, methodDescMap, cacheService);
          result.setParamDescList(paramList);
      }
      return result;
  }



  protected List<Param> buildAllParam(PsiMethod method, Map<String, List<PsiDocTag>> methodParamDescMap) {
      PsiParameterList parameterList = method.getParameterList();
      if (parameterList == null || parameterList.getParameters() == null || parameterList.getParameters().length == 1) {
          // 说明该扩展方法没有入参
          return null;
      }
      List<Param> paramList = new ArrayList<>();
      for (PsiParameter parameter : parameterList.getParameters()) {
          Param param = new Param();
          String paramName = parameter.getName();
          PsiType type = parameter.getType();

          PsiClass psiClass = PsiUtil.resolveClassInType(parameter.getType());
          paramList.add(param);
      }
      return paramList;
  }


  public String getTotalName(PsiMethod psiMethod) {
      return psiMethod.getContainingClass().getQualifiedName()
              .concat(psiMethod.getName());
  }

  public Map<String, String> classifyTag(PsiDocTag[] tags) {
      Map<String, String> result = new HashMap<>();
      Map<String, List<PsiDocTag>> map = Arrays.stream(tags).collect(Collectors.groupingBy(PsiDocTag::getName));
      List<PsiDocTag> aReturn = map.get("return");
      if (aReturn != null && aReturn.size() > 0) {
          PsiDocTag returnTag = aReturn.get(0);
          String desc = RegularUtil.getReturnDesc(returnTag.getText());
          result.put("return", desc);
      }
      List<PsiDocTag> aParam = map.get("param");
      if (aParam != null && aParam.size() > 0) {
          for (PsiDocTag paramTag : aParam) {
              StringBuilder sb = new StringBuilder();
              String paramKey = "";
              PsiElement[] children = paramTag.getChildren();

              for (PsiElement child : children) {
                  if (child instanceof PsiDocParamRef) {
                      paramKey = child.getText();
                  }
                  if (child instanceof PsiDocTokenImpl) {
                      String debugName = ((PsiDocTokenImpl) child).getTokenType().getDebugName();
                      if ("DOC_COMMENT_DATA".equalsIgnoreCase(debugName)) {
                          String text = child.getText();
                          String trim = text.trim();
                          if (trim.isEmpty() || trim.matches("^\\s*$")) {
                              continue;
                          } else {
                              sb.append(trim).append(",");
                          }
                      }

                  }
              }
              result.put(paramKey, sb.substring(0, sb.length()-1));
          }

      }
      return result;
  }
}

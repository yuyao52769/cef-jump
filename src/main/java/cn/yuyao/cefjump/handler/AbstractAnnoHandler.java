package cn.yuyao.cefjump.handler;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuyao
 * @create 2025/2/12
 */
public abstract class AbstractAnnoHandler implements LineMarkerProvider, AnnotationHandler {

    public AbstractAnnoHandler(String originalAnno, List<String> targetAnnoList) {
        this.originalAnno = originalAnno;
        this.targetAnnoList = targetAnnoList;
    }

    protected String originalAnno;

    protected List<String> targetAnnoList;

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof PsiMethod && support((PsiMethod) element)) {
            PsiMethod pm = (PsiMethod) element;
            PsiAnnotation annotation = pm.getAnnotation(this.originalAnno);
            PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
            if (value instanceof PsiLiteralExpression) {
                String extensionKey = (String) ((PsiLiteralExpression) value).getValue();
                return createLineMarkerInfo(annotation, pm, extensionKey);
            }
        }
        return null;
    }

    protected LineMarkerInfo<?> createLineMarkerInfo(PsiAnnotation annotation, PsiMethod psiMethod, String extensionKey) {
        List<PsiElement> matchingMethods = new ArrayList<>();
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(psiMethod.getProject());
        for (String targetAnno : this.targetAnnoList) {
            PsiClass[] annotationClassList = javaPsiFacade.findClasses(targetAnno, GlobalSearchScope.allScope(psiMethod.getProject()));
            if (annotationClassList != null && annotationClassList.length > 0) {
                for (int i = 0; i < annotationClassList.length; i++) {
                    List<PsiAnnotation> psiAnnotations = searchTargetAnno(annotationClassList[i], psiMethod, targetAnno, extensionKey);
                    if (psiAnnotations != null && psiAnnotations.size() > 0) {
                        matchingMethods.addAll(psiAnnotations);
                    }
                }
            }
        }
        if (matchingMethods != null && matchingMethods.size() > 0) {
           return NavigationGutterIconBuilder.create(createIcon(annotation, extensionKey))
                    .setAlignment(GutterIconRenderer.Alignment.CENTER)
                    .setTooltipText("Navigate to calling location")
                    .setTargets(matchingMethods)
                    .createLineMarkerInfo(annotation);
        }
        return null;
    }


    protected List<PsiAnnotation> searchTargetAnno(PsiClass annotationClass, PsiMethod psiMethod, String targetAnno, String extensionKey) {
        if (annotationClass != null) {
            // 查找匹配的 @Extension 注解方法
            Collection<PsiMethod> annotatedMethods = AnnotatedElementsSearch.searchPsiMethods(
                    annotationClass,
                    GlobalSearchScope.allScope(psiMethod.getProject())
            ).findAll();
            List<PsiAnnotation> annotationList =  annotatedMethods.stream()
                    .filter(method -> {
                        PsiAnnotation targetAnnotation = method.getAnnotation(targetAnno);
                        PsiAnnotationMemberValue value = targetAnnotation.findAttributeValue("value");
                        return value instanceof PsiLiteralExpression && extensionKey.equals(((PsiLiteralExpression) value).getValue());
                    })
                    .map(method -> {
                        return method.getAnnotation(targetAnno);
                    })
                    .collect(Collectors.toList());
            return annotationList;
        }
        return null;
    }

    abstract Icon createIcon(PsiAnnotation annotation, String extensionKey);
}

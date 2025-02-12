package cn.yuyao.cefjump.handler;

import cn.yuyao.cefjump.constant.AnnoConstant;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuyao
 * @create 2025/2/12
 */
public class ClsHandler implements LineMarkerProvider {
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression methodCall = (PsiMethodCallExpression) element;
            PsiReferenceExpression methodExpression = methodCall.getMethodExpression();
            if (methodExpression.getCanonicalText().contains(AnnoConstant.EXTENSION_SERVICES_METHOD)) {
                PsiExpressionList argumentList = methodCall.getArgumentList();
                PsiExpression[] expressions = argumentList.getExpressions();
                if (expressions.length > 0 && expressions[0] instanceof PsiLiteralExpression) {
                    PsiLiteralExpression firstArg = (PsiLiteralExpression) expressions[0];
                    String extensionKey = (String) firstArg.getValue();
                    if (extensionKey != null) {
                        return createLineMarkerInfo(element, extensionKey);
                    }
                }
            }
        }
        return null;
    }

    private LineMarkerInfo<?> createLineMarkerInfo(PsiElement element, String extensionKey) {
        Icon icon = IconLoader.getIcon("/images/extend16.svg", getClass());
        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(icon)
                .setAlignment(GutterIconRenderer.Alignment.CENTER)
                .setTooltipText("Navigate to extension method");
        if (extensionKey != null) {
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(element.getProject());
            PsiClass annotationClass = javaPsiFacade.findClass(AnnoConstant.EXTENSION_ANNOTATION, GlobalSearchScope.allScope(element.getProject()));
            if (annotationClass != null) {
                // 查找匹配的 @Extension 注解方法
                Collection<PsiMethod> annotatedMethods = AnnotatedElementsSearch.searchPsiMethods(
                        annotationClass,
                        GlobalSearchScope.allScope(element.getProject())
                ).findAll();
                List<PsiAnnotation> annotationList= annotatedMethods.stream()
                        .filter(method -> {
                            PsiAnnotation annotation = method.getAnnotation(AnnoConstant.EXTENSION_ANNOTATION);
                            PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
                            return value instanceof PsiLiteralExpression && extensionKey.equals(((PsiLiteralExpression) value).getValue());
                        })
                        .map(method -> method.getAnnotation(AnnoConstant.EXTENSION_ANNOTATION))
                        .collect(Collectors.toList());
                if (annotationList == null || annotationList.size() <= 0) {
                    return null;
                }
                builder.setTargets(annotationList);
            }
        } else {
            // 如果是 @Extension 注解的方法，暂时不需要设置跳转目标
            return null;
        }
        return builder.createLineMarkerInfo(element);

    }
}

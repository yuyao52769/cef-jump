package cn.yuyao.cefjump.handler;

import com.intellij.psi.PsiMethod;

/**
 * @author yuyao
 * @create 2025/2/12
 */
public interface AnnotationHandler {

    Boolean support(PsiMethod method);

}

package cn.yuyao.cefjump.handler;

import cn.yuyao.cefjump.constant.AnnoConstant;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;

import javax.swing.*;
import java.util.Arrays;

/**
 * @author yuyao
 * @create 2025/2/12
 */
public class OpenAfterHandler extends AbstractAnnoHandler{

    public OpenAfterHandler() {
        super(AnnoConstant.OPEN_AFTER_ANNOTATION, Arrays.asList(AnnoConstant.EXTENSION_ANNOTATION));
    }
    @Override
    public Boolean support(PsiMethod method) {
        PsiAnnotation annotation = method.getAnnotation(this.originalAnno);
        return annotation != null;
    }

    @Override
    Icon createIcon(PsiAnnotation annotation, String extensionKey) {
        return IconLoader.getIcon("/images/extend16.svg", getClass());
    }
}

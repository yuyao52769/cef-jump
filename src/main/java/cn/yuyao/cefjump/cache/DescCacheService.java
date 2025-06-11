package cn.yuyao.cefjump.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.yuyao.cefjump.dto.FieldDesc;
import cn.yuyao.cefjump.util.ClassUtil;
import com.intellij.psi.PsiClass;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DescCacheService {

    // 这个缓存的都是 dto对应的内部字段注释的映射
    private Map<String, List<FieldDesc>> CACHE = new ConcurrentHashMap<>();



    public List<FieldDesc> takeCache(PsiClass psiClass) {
        if (psiClass == null) return Collections.emptyList();
        if (ClassUtil.isJavaLangWrapper(psiClass)) return Collections.emptyList();
        return CACHE.get(psiClass.getQualifiedName());

    }


    public void offerCache(PsiClass psiClass, List<FieldDesc> descList) {
        if (psiClass != null) {
            this.offerCache(psiClass.getQualifiedName(), descList);
        }
    }

    public void offerCache(String clsName, List<FieldDesc> descList) {
        if (StrUtil.isNotEmpty(clsName) && CollectionUtil.isNotEmpty(descList)) {
            CACHE.put(clsName, descList);
        }
    }
}

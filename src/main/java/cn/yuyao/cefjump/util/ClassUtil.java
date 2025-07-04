package cn.yuyao.cefjump.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.yuyao.cefjump.cache.DescCacheService;
import cn.yuyao.cefjump.dto.FieldDesc;
import cn.yuyao.cefjump.dto.Param;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.javadoc.PsiDocTokenImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClassUtil {

    public static ClassWrapper build(PsiType returnType) {
        ClassWrapper result = new ClassWrapper();
        if (returnType.equalsToText("void")) {
            result.setVoid(true);
            return result;
        }
        result.setReallyTypeName(returnType.getCanonicalText());
        PsiClass psiClass = PsiUtil.resolveClassInType(returnType);
        if (psiClass == null) {
            result.setBaseType(true);
        } else {
            String qualifiedName = psiClass.getQualifiedName();
            switch (qualifiedName) {
                case "java.util.List":
                case "java.util.ArrayList":
                case "java.util.Set":
                case "java.util.HashSet":
                    // List 类型
                    PsiClassReferenceType classReferenceType = (PsiClassReferenceType) returnType;
                    PsiType[] parameters = classReferenceType.getParameters();
                    if (parameters.length > 0) {
                        PsiType genericType = parameters[0]; // List 的第一个泛型参数，即 Integer
                        PsiClass genericClass = PsiUtil.resolveClassInType(genericType);
                        result.setReallyType(genericClass);
                    }
                    break;
                case "java.util.Map":
                case "java.util.HashMap":
                    // Map 类型
                    break;
                default:
                    // 普通类
                    result.setReallyType(psiClass);
                    break;
            }
        }
        if (result.getReallyType() != null) {
            // 判断是否为基础类型
            boolean javaLangWrapper = isJavaLangWrapper(result.getReallyType());
            result.setBaseType(javaLangWrapper);
        }
        return result;
    }


    public static List<Param> buildJoinParam(PsiParameterList parameterList, Map<String, String> methodDescMap, DescCacheService cacheService) {
        List<Param> result = new ArrayList<>();
        for (PsiParameter parameter : parameterList.getParameters()) {
            result.add(doBuildJoin(parameter, methodDescMap, cacheService));
        }
        return result;
    }

    public static Param doBuildJoin(PsiParameter parameter, Map<String, String> methodDescMap, DescCacheService cacheService) {
        // 最原始入参
        Param paramEntity = new Param();
        String paramName = parameter.getName();
        String paramDesc = methodDescMap.get(paramName);

        paramEntity.setName(paramName);
        paramEntity.setDesc(paramDesc);
        PsiType paramType = parameter.getType();
        paramEntity.setType(paramType.getCanonicalText());

        PsiClass paramClass = PsiUtil.resolveClassInType(paramType);
        if (!isJavaLangWrapper(paramClass)) {
            List<FieldDesc> fieldList = new ArrayList<>();
            doBuildFieldDesc(fieldList, paramClass, 1, cacheService);
            paramEntity.setFieldDescList(fieldList);
//            List<FieldDesc> cacheDescList = cacheService.takeCache(paramClass);
//            if (CollectionUtil.isNotEmpty(cacheDescList)) {
//                paramEntity.setFieldDescList(cacheDescList);
//            } else {
//                List<FieldDesc> fieldList = new ArrayList<>();
//                doBuildFieldDesc(fieldList, paramClass, 1, cacheService);
//                paramEntity.setFieldDescList(fieldList);
//                cacheService.offerCache(paramClass, fieldList);
//            }
        }
        return paramEntity;
    }

    public static Param buildReturnParam(ClassWrapper classWrapper, String desc, DescCacheService cacheService) {
        Param param = new Param();
        param.setName("返回参数");
        param.setDesc(desc);
        if (classWrapper.getVoid()) {
            return null;
        }
        param.setType(classWrapper.getReallyTypeName());
        if (classWrapper.getBaseType()) {
            return param;
        }
        PsiClass type = classWrapper.getReallyType();
//        List<FieldDesc> cacheList = cacheService.takeCache(type);
//        if (CollectionUtil.isNotEmpty(cacheList)) {
//            param.setFieldDescList(cacheList);
//        } else {
//            List<FieldDesc> fieldList = new ArrayList<>();
//            doBuildFieldDesc(fieldList, type, 1, cacheService);
//            param.setFieldDescList(fieldList);
//            cacheService.offerCache(type, fieldList);
//        }
        List<FieldDesc> fieldList = new ArrayList<>();
        doBuildFieldDesc(fieldList, type, 1, cacheService);
        param.setFieldDescList(fieldList);
        return param;
    }

    // level 避免整体的解析层数过高、或者出现循环
    public static void doBuildFieldDesc(List<FieldDesc> fieldList, PsiClass type, int level, DescCacheService cacheService) {
        PsiField[] fields = type.getFields();
        for (PsiField field : fields) {
            PsiDocComment fieldDocComment = field.getDocComment();
            String name = field.getName();
            if ("serialVersionUID".equalsIgnoreCase(name)) continue;
            StringBuilder sb = new StringBuilder();
            PsiType fieldType = field.getType();
            if (fieldDocComment != null) {
                System.out.println(field.getText());
                PsiElement[] descriptionElements = fieldDocComment.getDescriptionElements();
                for (PsiElement element : descriptionElements) {
                    if (element instanceof PsiDocTokenImpl) {
                        String text = element.getText().trim();
                        sb.append(text).append(",");
                    }

                }
            }
            FieldDesc fieldDesc = new FieldDesc();
            // 控制层数
            if (level <= 3) {
                ClassTypeDTO typeChecker = checkFieldType(fieldType);
                if (!typeChecker.baseType && !typeChecker.map) {
                    PsiClass reallyType = typeChecker.reallyType;
                    //List<FieldDesc> cacheDescList = cacheService.takeCache(reallyType);
//                    if (CollectionUtil.isNotEmpty(cacheDescList)) {
//                        fieldDesc.setFieldDescList(cacheDescList);
//                    } else {
//                        List<FieldDesc> childList = new ArrayList<>();
//                        doBuildFieldDesc(childList, reallyType, ++level, cacheService);
//                        fieldDesc.setFieldDescList(childList);
//                        cacheService.offerCache(reallyType, childList);
//                    }
                    List<FieldDesc> childList = new ArrayList<>();
                    doBuildFieldDesc(childList, reallyType, ++level, cacheService);
                    fieldDesc.setFieldDescList(childList);
                }
            }

            fieldDesc.setName(name);
            fieldDesc.setType(fieldType.getCanonicalText());
            fieldDesc.setDesc(sb.length() <= 0 ? "" : sb.substring(0, sb.length()-1));
            fieldList.add(fieldDesc);
        }
    }


    public static boolean isJavaLangWrapper(PsiClass psiClass) {
        if (psiClass == null) return false;
        String name = psiClass.getQualifiedName();
        return name != null && Arrays.asList(
                "java.lang.Integer",
                "java.lang.Long",
                "java.lang.Short",
                "java.lang.Byte",
                "java.lang.Character",
                "java.lang.Float",
                "java.lang.Double",
                "java.lang.Boolean",
                "java.lang.String"
        ).contains(name);
    }

    static class ClassTypeDTO {
        public boolean baseType = false;
        public boolean map = false;
        public PsiClass reallyType;
    }

    public static ClassTypeDTO checkFieldType(PsiType fieldType) {
        ClassTypeDTO result = new ClassTypeDTO();
        PsiClass psiClass = PsiUtil.resolveClassInType(fieldType);
        if (psiClass == null) {
            result.baseType = true;
            return result;
        }
        String qualifiedName = psiClass.getQualifiedName();
        switch (qualifiedName) {
            case "java.util.List":
            case "java.util.ArrayList":
            case "java.util.Set":
            case "java.util.HashSet":
                // List 类型
                PsiClassReferenceType classReferenceType = (PsiClassReferenceType) fieldType;
                PsiType[] parameters = classReferenceType.getParameters();
                if (parameters.length > 0) {
                    PsiType genericType = parameters[0]; // List 的第一个泛型参数，即 Integer
                    PsiClass genericClass = PsiUtil.resolveClassInType(genericType);
                    result.reallyType = genericClass;
                }
                break;
            case "java.util.Map":
            case "java.util.HashMap":
                // Map 类型
                result.map = true;
                break;
            default:
                // 普通类
                result.reallyType = psiClass;
                break;
        }
        if (result.reallyType != null) {
            // 判断是否为基础类型
            boolean javaLangWrapper = isJavaLangWrapper(result.reallyType);
            result.baseType = javaLangWrapper;
        }
        return result;
    }

}

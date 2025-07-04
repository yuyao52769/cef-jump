package cn.yuyao.cefjump.docGen;

import cn.hutool.core.collection.CollectionUtil;
import cn.yuyao.cefjump.CefDocModuleDesc;
import cn.yuyao.cefjump.dto.FieldDesc;
import cn.yuyao.cefjump.dto.Param;
import cn.yuyao.cefjump.dto.ParamDesc;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yuyao
 * @create 2025/3/30
 */
public class HtmlGenHandler {

    public static final HtmlGenHandler INSTANCE = new HtmlGenHandler();

    private static int paramIndex = 0;

    public void generate(List<CefDocModuleDesc> cefDocModuleCache, Project project, String projectPath) {
        try {
            if (cefDocModuleCache == null || cefDocModuleCache.size() == 0) return;
            List<CefModule> cefParams = buildLeafParam(cefDocModuleCache);
            if (cefParams == null || cefParams.size() == 0) return;
            wrapperParamId(cefParams);
            TemplateEngine templateEngine = new TemplateEngine();
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setPrefix("templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setCharacterEncoding("UTF-8");
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateEngine.setTemplateResolver(templateResolver);
            Context context = new Context();
            context.setVariable("mainModules", cefParams);
            String html = templateEngine.process("index", context);
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(new File(projectPath + File.separator + "output.html")),
                    StandardCharsets.UTF_8)) {
                writer.write(html);
                System.out.println("HTML 文件生成成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            String msg = e.getMessage();
            Messages.showMessageDialog(project, "生成html报错了，报错信息为" + msg + "||||" + stackTrace,
                    "成功", Messages.getErrorIcon());
        }

    }

    protected List<CefModule> buildLeafParam(List<CefDocModuleDesc> cefDocModuleCache) {
        List<CefModule> mainModuleList = new ArrayList<>();
        WrapperInt modelIndex = new WrapperInt();
        modelIndex.index = 10;
        Map<String, List<CefDocModuleDesc>> collectMap = cefDocModuleCache.stream().collect(Collectors.groupingBy(CefDocModuleDesc::getModule));
        for (Map.Entry<String, List<CefDocModuleDesc>> entry : collectMap.entrySet()) {
            String moduleName = entry.getKey();
            String id = makeId(moduleName);
            CefModule fatherModule = new CefModule();
            fatherModule.setId(id);
            fatherModule.setName(moduleName);
            fatherModule.setSubModules(buildSubModule(entry.getValue(), modelIndex));
            mainModuleList.add(fatherModule);
        }
        return mainModuleList;
    }


    protected static String makeId(String name) {
        return Objects.hash(name) + "key";
    }

    protected List<CefModule> buildSubModule(List<CefDocModuleDesc> moduleDescList, WrapperInt modelIndex) {
        List<CefModule> resultList = new ArrayList<>();
        for (int i = 0; i < moduleDescList.size(); i++) {
            resultList.add(doBuildSubModule(moduleDescList.get(i), modelIndex));
        }
        return resultList;
    }

    protected CefModule doBuildSubModule(CefDocModuleDesc moduleDesc, WrapperInt index) {
        CefModule result = new CefModule();
        result.setId("subCollapse" + index.index);
        index.index++;
        result.setName(moduleDesc.getName());
        result.setFuncDesc(moduleDesc.getFunc());
        result.setExtDesc(moduleDesc.getDesc());
        result.setFuncIntro(moduleDesc.getClassName().concat(".").concat(moduleDesc.getMethodName()));
        List<CefDocModuleDesc.OpenFunc> openFuncList = moduleDesc.getOpenFuncList();
        result.setTags(openFuncList.stream().map(o -> o.getType().getName()).collect(Collectors.toList()));
        result.setTagDescriptions(openFuncList.stream().map(o -> o.getOpenId()).collect(Collectors.toList()));
        result.setParamDesc(moduleDesc.getParamDesc());
        return result;
    }

    protected void wrapperParamId(List<CefModule> cefModules) {
        if (CollectionUtil.isEmpty(cefModules)) return;
        String paramIdPre = "paramId_";
        WrapperInt index = new WrapperInt();
        index.index = 0;
        for (CefModule module : cefModules) {
            List<CefModule> subModules = module.getSubModules();
            if (CollectionUtil.isEmpty(subModules)) continue;
            if (CollectionUtil.isNotEmpty(subModules)) {
                for (CefModule subModule : subModules) {
                    ParamDesc paramDesc = subModule.getParamDesc();
                    if (paramDesc != null) {
                        Param returnDesc = paramDesc.getReturnDesc();
                        List<Param> paramList = paramDesc.getParamDescList();
                        if (returnDesc != null) {
                            doAddParamId(index, paramIdPre, Collections.singletonList(returnDesc));
                        }
                        if (CollectionUtil.isNotEmpty(paramList)) {
                            doAddParamId(index, paramIdPre, paramList);
                        }
                    }
                }
            }

        }
    }


    protected void doAddParamId(WrapperInt index, String preFix, List<Param> paramList) {
        if (CollectionUtil.isNotEmpty(paramList)) {
            for (Param param : paramList) {
                param.setId(preFix + index.index);
                index.index++;
                List<FieldDesc> fieldDescList = param.getFieldDescList();
                if (CollectionUtil.isNotEmpty(fieldDescList)) {
                    for (FieldDesc childDesc : fieldDescList) {
                        childDesc.setId(preFix + index.index);
                        index.index++;
                        doAppDescParamId(index, preFix, childDesc.getFieldDescList());
                    }
                }
            }
        }
    }

    protected void doAppDescParamId(WrapperInt index, String preFix, List<FieldDesc> fieldDescList) {
        if (CollectionUtil.isNotEmpty(fieldDescList)) {
            for (FieldDesc child : fieldDescList) {
                child.setId(preFix + index.index);
                index.index++;
                doAppDescParamId(index, preFix, child.getFieldDescList());
            }
        }
    }

    public static class WrapperInt {
        public int index;
    }
}

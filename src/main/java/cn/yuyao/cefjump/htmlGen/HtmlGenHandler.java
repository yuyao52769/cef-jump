package cn.yuyao.cefjump.htmlGen;

import cn.yuyao.cefjump.CefDocModuleDesc;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yuyao
 * @create 2025/3/30
 */
public class HtmlGenHandler {

    public void generate(List<CefDocModuleDesc> cefDocModuleCache, Project project, String projectPath) {
        List<CefModule> cefParams = buildLeafParam(cefDocModuleCache);
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

    }

    protected List<CefModule> buildLeafParam(List<CefDocModuleDesc> cefDocModuleCache) {
        List<CefModule> mainModuleList = new ArrayList<>();

        Map<String, List<CefDocModuleDesc>> collectMap = cefDocModuleCache.stream().collect(Collectors.groupingBy(CefDocModuleDesc::getModule));
        for (Map.Entry<String, List<CefDocModuleDesc>> entry : collectMap.entrySet()) {
            String moduleName = entry.getKey();
            String id = makeId(moduleName);
            CefModule fatherModule = new CefModule();
            fatherModule.setId(id);
            fatherModule.setName(moduleName);
            fatherModule.setSubModules(buildSubModule(entry.getValue()));
            mainModuleList.add(fatherModule);
        }
        return mainModuleList;
    }


    protected static String makeId(String name) {
        return Objects.hash(name) + "key";
    }

    protected List<CefModule> buildSubModule(List<CefDocModuleDesc> moduleDescList) {
        List<CefModule> resultList = new ArrayList<>();
        for (int i = 0; i < moduleDescList.size(); i++) {
            resultList.add(doBuildSubModule(moduleDescList.get(i), i));
        }
        return resultList;
    }

    protected CefModule doBuildSubModule(CefDocModuleDesc moduleDesc, int index) {
        CefModule result = new CefModule();
        result.setId("subCollapse" + index + "1");
        result.setName(moduleDesc.getName());
        result.setFuncDesc(moduleDesc.getFunc());
        result.setExtDesc(moduleDesc.getDesc());
        result.setFuncIntro(moduleDesc.getClassName().concat(".").concat(moduleDesc.getMethodName()));
        List<CefDocModuleDesc.OpenFunc> openFuncList = moduleDesc.getOpenFuncList();
        result.setTags(openFuncList.stream().map(o -> o.getType().getName()).collect(Collectors.toList()));
        result.setTagDescriptions(openFuncList.stream().map(o -> o.getOpenId()).collect(Collectors.toList()));
        return result;
    }
}

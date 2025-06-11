package cn.yuyao.cefjump.docGen;

import cn.yuyao.cefjump.dto.ParamDesc;

import java.util.List;

/**
 * @author yuyao
 * @create 2025/3/30
 */
public class CefModule {

    private String id;

    private String name;

    private List<String> tags;

    private List<String> tagDescriptions;

    private String funcIntro;

    private String funcDesc;

    private String extDesc;

    /**
     * 出入参
     */
    private ParamDesc paramDesc;

    private List<CefModule> subModules;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTagDescriptions() {
        return tagDescriptions;
    }

    public void setTagDescriptions(List<String> tagDescriptions) {
        this.tagDescriptions = tagDescriptions;
    }

    public String getFuncIntro() {
        return funcIntro;
    }

    public void setFuncIntro(String funcIntro) {
        this.funcIntro = funcIntro;
    }

    public String getFuncDesc() {
        return funcDesc;
    }

    public void setFuncDesc(String funcDesc) {
        this.funcDesc = funcDesc;
    }

    public String getExtDesc() {
        return extDesc;
    }

    public void setExtDesc(String extDesc) {
        this.extDesc = extDesc;
    }

    public List<CefModule> getSubModules() {
        return subModules;
    }

    public void setSubModules(List<CefModule> subModules) {
        this.subModules = subModules;
    }

    public ParamDesc getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(ParamDesc paramDesc) {
        this.paramDesc = paramDesc;
    }

    @Override
    public String toString() {
        return "CefModule{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", tags=" + tags +
                ", tagDescriptions=" + tagDescriptions +
                ", funcIntro='" + funcIntro + '\'' +
                ", funcDesc='" + funcDesc + '\'' +
                ", extDesc='" + extDesc + '\'' +
                ", subModules=" + subModules +
                '}';
    }
}

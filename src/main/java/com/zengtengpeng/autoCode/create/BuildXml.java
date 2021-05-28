package com.zengtengpeng.autoCode.create;

import com.zengtengpeng.autoCode.enums.XmlElementType;
import com.zengtengpeng.autoCode.bean.BuildXmlBean;
import com.zengtengpeng.autoCode.config.AutoCodeConfig;
import com.zengtengpeng.autoCode.config.GlobalConfig;
import com.zengtengpeng.autoCode.utils.BuildUtils;
import com.zengtengpeng.autoCode.utils.MyStringUtils;
import com.zengtengpeng.jdbc.bean.Bean;
import com.zengtengpeng.jdbc.bean.BeanColumn;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 创建xml
 */
@FunctionalInterface
public interface BuildXml {


    /**
     * 创建insert
     *
     * @return
     */
    default BuildXml insert(AutoCodeConfig init, StringBuffer content) {
        Bean bean = init.getBean();
        GlobalConfig globalConfig = init.getGlobalConfig();
        BeanColumn beanColumn = bean.getPrimaryKey().get(0);
        BuildXmlBean buildXmlBean = new BuildXmlBean();
        buildXmlBean.setXmlElementType(XmlElementType.insert);
        Map<String, String> attr = new LinkedHashMap<>();
        attr.put("id", "saveObject");
        attr.put("parameterType", globalConfig.getParentPack() + "." + globalConfig.getPackageBean() + "." + globalConfig.getParentType() + "." + bean.getTableName());
        attr.put("keyProperty", beanColumn.getBeanName());
        attr.put("useGeneratedKeys", "true");
        buildXmlBean.setAttributes(attr);

        StringBuffer sql = new StringBuffer();
        MyStringUtils.append(sql, "insert into %s", 2, bean.getDataName());
        MyStringUtils.append(sql, " <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">", 2);
        bean.getAllColumns().forEach(t -> {

            MyStringUtils.append(sql, "<if test=\"%s!=null \">", 3,
                    t.getBeanName());
            MyStringUtils.append(sql, "%s, ", 4,
                    t.getJdbcName());
            MyStringUtils.append(sql, "</if>", 3);


        });
        MyStringUtils.append(sql, ">");
        sql.deleteCharAt(sql.length() - 2);
        MyStringUtils.append(sql, "</trim>", 2);
        MyStringUtils.append(sql, "values", 2);
        MyStringUtils.append(sql, "<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">", 2);
        bean.getAllColumns().forEach(t -> {

            MyStringUtils.append(sql, "<if test=\"%s!=null\">", 3,
                    t.getBeanName());
            MyStringUtils.append(sql, "#{%s,jdbcType=%s},", 4,
                     t.getBeanName(), t.getJdbcType_());
            MyStringUtils.append(sql, "</if>", 3);

        });
        MyStringUtils.append(sql, ">");
        sql.deleteCharAt(sql.length() - 2);
        MyStringUtils.append(sql, "</trim>", 2);
        buildXmlBean.setSql(sql.toString());
        content.append(BuildUtils.buildXml(buildXmlBean));
        return this;
    }

    /**
     * 创建delete
     *
     * @return
     */
    default BuildXml deleteByPrimaryKey(AutoCodeConfig init, StringBuffer content) {
        Bean bean = init.getBean();
        BuildXmlBean buildXmlBean = new BuildXmlBean();
        buildXmlBean.setXmlElementType(XmlElementType.delete);
        Map<String, String> attr = new LinkedHashMap<>();
        attr.put("id", "deleteObject");
        buildXmlBean.setAttributes(attr);
        StringBuffer sql = new StringBuffer();

        MyStringUtils.append(sql, "delete from  %s \n", 2, bean.getDataName());
        BeanColumn beanColumn = bean.getPrimaryKey().get(0);
        MyStringUtils.append(sql, "where %s = #{%s} \n ", 2, beanColumn.getJdbcName(), beanColumn.getBeanName());

        buildXmlBean.setSql(sql.toString());
        content.append(BuildUtils.buildXml(buildXmlBean));
        return this;
    }

    /**
     * 修改
     *
     * @return
     */
    default BuildXml update(AutoCodeConfig init, StringBuffer content) {
        Bean bean = init.getBean();
        GlobalConfig globalConfig = init.getGlobalConfig();
        BuildXmlBean buildXmlBean = new BuildXmlBean();
        buildXmlBean.setXmlElementType(XmlElementType.update);
        Map<String, String> attr = new LinkedHashMap<>();
        attr.put("id", "updateObject");
        attr.put("parameterType", globalConfig.getParentPack() + "." + globalConfig.getPackageBean() + "." + globalConfig.getParentType() + "." + bean.getTableName());

        buildXmlBean.setAttributes(attr);
        StringBuffer sql = new StringBuffer();

        MyStringUtils.append(sql, "update %s", 2, bean.getDataName());
        MyStringUtils.append(sql, "<set>", 2);
        bean.getAllColumns().forEach(t -> {
            if (!t.getKey()) {
                if ("java.lang.String".equalsIgnoreCase(t.getBeanType())) {
                    MyStringUtils.append(sql, "<if test=\"%s!=null and %s!=''\">", 3,
                            t.getBeanName(), t.getBeanName());
                    MyStringUtils.append(sql, "%s = #{%s,jdbcType=%s},", 4,
                            t.getJdbcName(), t.getBeanName(), t.getJdbcType_());
                    MyStringUtils.append(sql, " </if>", 3);
                } else {
                    MyStringUtils.append(sql, "<if test=\"%s!=null\">", 3,
                            t.getBeanName());
                    MyStringUtils.append(sql, "%s = #{%s,jdbcType=%s},", 4,
                            t.getJdbcName(), t.getBeanName(), t.getJdbcType_());
                    MyStringUtils.append(sql, "</if>", 3);
                }
            }
        });
        BeanColumn beanColumn = bean.getPrimaryKey().get(0);
        MyStringUtils.append(sql, "</set>", 2);
        MyStringUtils.append(sql, "where %s=#{%s}", 2, beanColumn.getJdbcName(), beanColumn.getBeanName());


        buildXmlBean.setSql(sql.toString());
        content.append(BuildUtils.buildXml(buildXmlBean));
        return this;
    }

    /**
     * 根据主键查询
     *
     * @return
     */
    default BuildXml selectByPrimaryKey(AutoCodeConfig init, StringBuffer content) {
        Bean bean = init.getBean();
        BuildXmlBean buildXmlBean = new BuildXmlBean();
        buildXmlBean.setXmlElementType(XmlElementType.select);
        Map<String, String> attr = new LinkedHashMap<>();
        attr.put("id", "getObjectById");
        attr.put("resultMap", "BaseResultMap");
        buildXmlBean.setAttributes(attr);
        StringBuffer sql = new StringBuffer();

        MyStringUtils.append(sql, "select", 2);
        bean.getAllColumns().forEach(t -> MyStringUtils.append(sql, "%s,", 2, t.getJdbcName()));
        sql.deleteCharAt(sql.length() - 2);

        MyStringUtils.append(sql, "from %s", 2, bean.getDataName());
        BeanColumn beanColumn = bean.getPrimaryKey().get(0);
        MyStringUtils.append(sql, "where %s = #{%s,jdbcType=%s}", 2, beanColumn.getJdbcName(), beanColumn.getBeanName(), beanColumn.getJdbcType_());

        buildXmlBean.setSql(sql.toString());
        content.append(BuildUtils.buildXml(buildXmlBean));
        return this;
    }

    /**
     * 查询所有
     *
     * @return
     */
    default BuildXml selectAll(AutoCodeConfig init, StringBuffer content) {
        select("selectAll", init, content);
        return this;
    }

    /**
     * 根据条件查询
     *
     * @return
     */
    default BuildXml selectByCondition(AutoCodeConfig init, StringBuffer content) {
        select("selectByCondition", init, content);
        return this;
    }

    default void select(String id, AutoCodeConfig init, StringBuffer content) {
        Bean bean = init.getBean();

        BuildXmlBean buildXmlBean = new BuildXmlBean();
        buildXmlBean.setXmlElementType(XmlElementType.select);
        Map<String, String> attr = new LinkedHashMap<>();
        attr.put("id", id);
        attr.put("resultMap", "BaseResultMap");
        buildXmlBean.setAttributes(attr);
        StringBuffer sql = new StringBuffer();

        MyStringUtils.append(sql, "select", 2);
        bean.getAllColumns().forEach(t -> MyStringUtils.append(sql, "%s,", 2, t.getJdbcName()));
        sql.deleteCharAt(sql.length() - 2);

        MyStringUtils.append(sql, "from %s", 2, bean.getDataName(), bean.getDataName());
        BeanColumn beanColumn = bean.getPrimaryKey().get(0);

        MyStringUtils.append(sql, "<where>", 2);
        BuildUtils.xmlWhere(bean, sql);
        MyStringUtils.append(sql, "</where>", 2);
        MyStringUtils.append(sql, "<choose>\n" +
                "          <when test=\"orderByString!=null and orderByString!=''\">\n" +
                "              ${orderByString}\n" +
                "          </when>\n" +
                "          <otherwise>\n" +
                "              order by %s desc\n" +
                "          </otherwise>\n" +
                "      </choose>", 2, beanColumn.getJdbcName());

        buildXmlBean.setSql(sql.toString());
        content.append(BuildUtils.buildXml(buildXmlBean));
    }


    /**
     * 创建之前的准备工作
     *
     * @return
     */
    default BuildXml before(AutoCodeConfig init, StringBuffer content) {
        GlobalConfig globalConfig = init.getGlobalConfig();
        Bean bean = init.getBean();
        MyStringUtils.append(content, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"%s.%s.%s.%s%s\">\n", globalConfig.getParentPack(), globalConfig.getPackageDao(), globalConfig.getParentType(), bean.getTableName(), globalConfig.getPackageDaoUp());

        MyStringUtils.append(content, "<resultMap id=\"BaseResultMap\" type=\"%s.%s.%s.%s\">\n", 1, globalConfig.getParentPack(), globalConfig.getPackageBean(), globalConfig.getParentType(),
                bean.getTableName());

        for (BeanColumn allColumn : bean.getAllColumns()) {
            if (allColumn.getKey()) {
                MyStringUtils.append(content, "<id column=\"%s\" jdbcType=\"%s\" property=\"%s\" />\n", 2, allColumn.getJdbcName(), allColumn.getJdbcType_(), allColumn.getBeanName());
            } else {
                MyStringUtils.append(content, "<result column=\"%s\" jdbcType=\"%s\" property=\"%s\" />\n", 2, allColumn.getJdbcName(), allColumn.getJdbcType_(), allColumn.getBeanName());
            }
        }
        MyStringUtils.append(content, "</resultMap>\n", 1);
        return this;
    }

    /**
     * 创建之后的
     *
     * @return
     */
    default BuildXml end(AutoCodeConfig init, StringBuffer content) {
        content.append("</mapper>");
        return this;
    }


    /**
     * 自定义sql
     */
    List<BuildXmlBean> custom(AutoCodeConfig autoCodeConfig);

    default String build(AutoCodeConfig autoCodeConfig) {
        StringBuffer content = new StringBuffer();
        BuildXml buildXml = before(autoCodeConfig, content).insert(autoCodeConfig, content).deleteByPrimaryKey(autoCodeConfig, content).
                update(autoCodeConfig, content).selectByPrimaryKey(autoCodeConfig, content).selectAll(autoCodeConfig, content).selectByCondition(autoCodeConfig, content);
        List<BuildXmlBean> custom = buildXml.custom(autoCodeConfig);
        if (custom != null) {
            custom.forEach(t -> content.append(BuildUtils.buildXml(t)).append("\n"));
        }
        buildXml.end(autoCodeConfig, content);
        return content.toString();
    }


}


<#-- @ftlvariable name="entities" type="java.util.List<com.bidanet.hprose.starter.doc.DocItem>" -->
<#-- @ftlvariable name="services" type="java.util.List<com.bidanet.hprose.starter.doc.DocItem>" -->
---
title: ${title!}
grammar_table: true
grammar_tableExtra: true
grammar_center: true
grammar_align: true
---
[toc]
# 1. 数据实体定义
<#list entities as item>
## 1.${item_index+1} ${item.title}(${item.name})
<@table item.table></@table>
</#list>

# 2. 接口定义
<#list services as item>
## 2.${item_index+1} ${item.title}(${item.name})
<#list item.subDoc as sub>
### 2.${item_index+1}.${sub_index+1} ${sub.title}(${sub.name})
<@table sub.table></@table>
</#list>
</#list>


<#macro table table>
<#list table as tb>
<#list tb as tbItem><#if tbItem_index==0>|</#if>${tbItem}|</#list><#if tb_index==0>
<#list tb as tbItem><#if tbItem_index==0>|</#if>--------|</#list></#if>
</#list>
</#macro>
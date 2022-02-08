package org.snax.supersnax.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.snax.supersnax.handler.JsonTypeHandler;

/**
 * @author maoth
 * @date 2021/11/16 15:27
 * @description
 */
@Data
public class JavaRule {
    private Long ruleId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则状态（0未入库 1入库）
     */
    private String ruleStatus;

    /**
     * 规则备注
     */
    private String ruleRemarks;

    /**
     * y_info 与 x_info
     */
    @TableField(value = "data_info", typeHandler = JsonTypeHandler.class)
    private JSONObject dataInfo;

    /**
     * labels attributes
     */
    @TableField(value = "labels", typeHandler = JsonTypeHandler.class)
    private JSONObject labels;

    /**
     * kafka消息字段rule_id对应数据库中id字段
     */
    @JSONField(name = "rule_id")
    private Long id;

    private Long patternId;

    private int edgeNum;

    private int ySupportSingle;

    private int attributeNum;

    private int xSupportSingle;

    private double confidence;

    private double lift;

    private int xSupportMultiple;

    private int ySupportMultiple;

    private int yWeight;

    private String pivotsId;

    private String oneInstance;

}
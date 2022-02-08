package org.snax.supersnax.entity;

import lombok.Data;

/**
 * @author maoth
 * @date 2021/11/16 15:23
 * @description
 */
@Data
public class GoRule {
    private int id;

    private int patternId;

    private int ruleId;

    private int edgeNum;

    private int attributeNum;

    private int xSupportSingle;

    private int ySupportSingle;

    private int ySupportMultiple;

    private int xSupportMultiple;

    private float confidence;

    private float lift;

    private String xInfo;

    private String yInfo;

    private String instanceInfo;
}

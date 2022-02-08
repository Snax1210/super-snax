package org.snax.supersnax.entity;

import lombok.Data;

/**
 * @author maoth
 * @date 2022/1/24 3:06
 * @description
 */
@Data
public class Stock {
    private double amount;

    private double opening;

    private double closing;

    private double amplitude;

    private double applies;

    private double max;

    private double min;

    private String firstDate;

    private double result;
}

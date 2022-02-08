package org.snax.supersnax.util;

import lombok.AllArgsConstructor;

/**
 * @author maoth
 * @date 2022/1/21 22:17
 * @description
 */
@AllArgsConstructor
public class FactorValue {
    public double lastClosePrice;
    public double closePrice ;
    public double maxPrice ;
    public double minPrice ;
    public double volume ;

    public double change(){
        return (closePrice-lastClosePrice)/lastClosePrice;
    }

    public double amount(){
        return volume*closePrice;
    }

    public double amplitude(){
        return (maxPrice-minPrice)/lastClosePrice;
    }
}

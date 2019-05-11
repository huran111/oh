package com.tykj.core.orm;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author huran
 */
@Data
@Accessors(chain = true)
public class Condition {
    private String property;
    private String operate;
    private Object value;

}

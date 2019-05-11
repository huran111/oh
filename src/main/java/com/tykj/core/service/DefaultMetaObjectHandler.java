package com.tykj.core.service;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.util.StringUtils;

import java.util.Date;

public class DefaultMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        System.out.println("=============================:insertFill");
        if(metaObject.hasSetter("creator") && StringUtils.isEmpty(metaObject.getValue("creator")))
            setFieldValByName("creator","admin",metaObject);
        if(metaObject.hasSetter("createDate") && StringUtils.isEmpty(metaObject.getValue("createDate")))
            metaObject.setValue("createDate",new Date());
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}

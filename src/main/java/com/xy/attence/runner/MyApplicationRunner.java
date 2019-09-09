package com.xy.attence.runner;

import com.xy.attence.task.RefreshData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Author: hexueyuan
 * @Name MyApplicationRunner
 * @Date: 2019/9/9 9:38
 * @Description:
 * @Version 1.0
 */
@Component
public class MyApplicationRunner implements ApplicationRunner {
    @Autowired
    private RefreshData refreshData;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        refreshData.scheduled();
    }
}

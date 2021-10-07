package com.zj.demo.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 监听器
 */
public class MyTaskListener implements TaskListener {
    /**
     * 指定负责人
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        // 判断当前的任务 是 创建申请 且 是创建事件
        if (delegateTask.getName().equals("创建申请")){
            if (delegateTask.getEventName().equals("create")){
                delegateTask.setAssignee("张三");
            }
        }

        if (delegateTask.getName().equals("审批申请")){
            if (delegateTask.getEventName().equals("create")){
                delegateTask.setAssignee("李总经理");
            }
        }
    }
}

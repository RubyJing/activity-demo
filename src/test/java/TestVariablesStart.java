import com.zj.demo.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class TestVariablesStart {

    @Test
    public void testDeployment() {
        ProcessEngine defaultProcessEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .name("出差申请流程-variables")
                .addClasspathResource("bpmn/evection-global.bpmn")
//                .addClasspathResource("bpmn/evection.png")
                .deploy();
        System.out.println("流程部署Id:" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
//        repositoryService.deleteDeployment("2501");
    }

    @Test
    public void testStart(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        // 获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //启动时设置流程变量map
        Map<String,Object> variables = new HashMap<>();
        String key = "evection-variable";
        Evection evection = new Evection();
        evection.setDay(4d);
        variables.put("evection",evection);

        variables.put("assignee0","张三");
        variables.put("assignee1","李经理");
        variables.put("assignee2","王总经理");
        variables.put("assignee3","赵财务");
        // 设定负责人的值：用来替换uel表达式
        runtimeService.startProcessInstanceByKey(key,variables);
        
    }

    @Test
    public void complete(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processDefinitionKey("evection-variable").singleResult();
        taskService.complete(task.getId());

    }

    @Test
    public void cascadeDelete(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.deleteDeployment("17501",true);
    }
}

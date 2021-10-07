import com.zj.demo.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ActivitiGatewayExclusive {

    @Test
    public void testDeployment() {
        ProcessEngine defaultProcessEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        RepositoryService repositoryService = defaultProcessEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .name("出差申请流程-排他网关")
                .addClasspathResource("bpmn/activitiGatewayExclusive.bpmn")
                .deploy();
        System.out.println("流程部署Id:" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
    }

    @Test
    public void testStartProcess(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();
        // 获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //启动时设置流程变量map
        Map<String,Object> variables = new HashMap<>();
        String key = "myProcess_gatewayExclusive";
        Evection evection = new Evection();
        evection.setDay(4d);
        variables.put("evection",evection);

        // 设定负责人的值：用来替换uel表达式
        runtimeService.startProcessInstanceByKey(key,variables);
    }

    @Test
    public void complete(){
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.default.xml").buildProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("myProcess_gatewayExclusive")
                .singleResult();
        taskService.complete(task.getId());

    }
}
